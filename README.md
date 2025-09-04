# 饱了么单体系统 Kubernetes 本地复现说明

本说明适用于本项目（前后端分离，Spring Boot + Vue3 + MySQL + Redis）在本地 Docker Desktop 自带的 Kubernetes 环境下的完整复现，包括自动化流水线（Jenkins）和手动部署两种方式。

---

## 目录结构说明

项目主要目录如下：

Baoleme-takeout/ 

├── back/ # Spring Boot 后端 
├── front/ # Vue3 前端
├── k8s/ # Kubernetes 配置文件 
├── db_scripts/ # 数据库初始化脚本 
├── jenkinsfile # Jenkins流水线配置（如使用自动化） 
└── ...

---

## 1. 环境准备

- **Docker Desktop**（需启用 Kubernetes）
- **kubectl** 命令行工具
- **Node.js**（建议 16+，用于前端构建）
- **Maven**（用于后端构建）
- **Jenkins**（如需自动化流水线，可用容器部署）
- **Git**（用于代码管理）
- **Postman**（用于接口测试，已提供 baoleme.postman_collection.json）

---

## 2. Dockerfile 准备

- 在 `back/` 和 `front/` 目录下分别准备好 Dockerfile。
  - `back/Dockerfile` 用于 Spring Boot 后端镜像构建。
  - `front/dockerfile` 用于 Vue3 前端镜像构建。
- **注意**：Dockerfile 中暴露端口需与 k8s 配置一致（如后端 8080，前端 80）。

---

## 3. Kubernetes 配置文件准备

- 在 `k8s/` 文件夹内，分别存放：
  - `frontend.yaml`：前端 Deployment 和 Service
  - `backend.yaml`：后端 Deployment 和 Service
  - `database.yaml`：MySQL 部署与 Service
  - `redis.yaml`：Redis 部署与 Service

- **注意事项**：
  - Service 名称需与 nginx.conf、前后端代码中的服务名一致。
  - 数据库和 Redis 的密码、端口等环境变量需与后端 application.yml 保持一致。
  - 如需持久化数据库，建议为 MySQL 配置 PersistentVolume（当前示例为 emptyDir，仅适合测试）。

---

## 4. 数据库初始化

- 数据库初始化脚本位于 `db_scripts/init.sql`。
- 可通过 Jenkins 脚本或手动方式导入（见后文）。

---

## 5. 自动化部署（Jenkins 流水线）

### 5.1 Jenkins 项目配置

1. **新建任务**  
   Jenkins 主页 → New Item → 输入项目名 → 选择 Freestyle project → OK

2. **源码管理**  
   - 选择 Git，填入你的项目仓库地址
   - 配置凭据（如有需要）

3. **触发器**  
   - 配置 Webhook 或定时触发（可选）

4. **全局工具配置**  
   - Jenkins → 全局工具配置 → 安装 NodeJs，命名如 `nodejs16`
   - 项目配置中选择该 NodeJs

5. **构建步骤**  
   - Add build step → Execute shell，粘贴如下脚本（可根据实际调整）：

```sh
echo "Starting Kubernetes operations..."
export KUBECONFIG=${KUBE_CONFIG_PATH}
kubectl cluster-info
kubectl get pods --all-namespaces
kubectl get svc -n default
kubectl get deployment -n default

echo "=========== Building Vue App ==========="
cd front
npm install
npm run test
npm run build
ls -l dist

cd ..
echo "=========== Building Backend ==========="
cd back
mvn clean test
mvn package -DskipTests=false
cd ..

IMAGE_TAG=${BUILD_NUMBER}
FRONTEND_IMAGE_NAME="cmdsu/my-vue-app-test:${IMAGE_TAG}"
BACKEND_IMAGE_NAME="cmdsu/my-springboot-app:${IMAGE_TAG}"

docker build -t ${FRONTEND_IMAGE_NAME} ./front
docker build -t ${BACKEND_IMAGE_NAME} ./back

kubectl --kubeconfig "$KUBE_CONFIG_PATH" apply -f k8s/database.yaml
kubectl --kubeconfig "$KUBE_CONFIG_PATH" rollout status statefulset/database
sleep 20

DB_POD_NAME=$(kubectl --kubeconfig "$KUBE_CONFIG_PATH" get pods -l app=database -o jsonpath='{.items[0].metadata.name}')
cat db_scripts/init.sql | kubectl --kubeconfig "$KUBE_CONFIG_PATH" exec -i "$DB_POD_NAME" -- mysql -uroot -p"$DB_ROOT_PASSWORD" baoleme

sed -i "s|image: .*|image: ${FRONTEND_IMAGE_NAME}|g" k8s/frontend.yaml
kubectl --kubeconfig "$KUBE_CONFIG_PATH" apply -f k8s/frontend.yaml
sed -i "s|image: .*|image: ${BACKEND_IMAGE_NAME}|g" k8s/backend.yaml
kubectl --kubeconfig "$KUBE_CONFIG_PATH" apply -f k8s/backend.yaml

kubectl --kubeconfig "$KUBE_CONFIG_PATH" rollout status deployment/frontend-deployment
kubectl --kubeconfig "$KUBE_CONFIG_PATH" rollout status deployment/backend-deployment
kubectl --kubeconfig "$KUBE_CONFIG_PATH" get all -o wide

echo "🎉 前后端CI/CD流水线全部执行成功! 🎉"
```

6.保存并运行

- 保存配置，手动或自动触发构建即可。

------

### 5.2 自动化部署注意事项

- Jenkins 需有 Docker、kubectl 权限（建议用 Docker Desktop 的 kubeconfig）。
- 数据库密码等敏感信息建议用 Jenkins Credentials 注入。
- 镜像推送到本地仓库或远程仓库，K8S yaml 中 image 字段需与实际一致。

------

## 6. 手动部署流程

1. **构建镜像**
2. **部署到 Kubernetes**
3. **初始化数据库**
4. **运行单元测试**
5. **检查部署状态**

------

## 7. 前后端联调与测试

- 前端访问方式：`http://localhost:<NodePort>`，NodePort 可通过 `kubectl get svc` 查询。
- 后端接口可用 Postman 导入 `baoleme.postman_collection.json` 进行测试。
- 前端与后端通信通过 nginx.conf 反向代理，需确保 proxy_pass 指向 K8S Service 名称和端口。

------

## 8. 常见问题排查

- **502 Bad Gateway**：请检查前端 nginx.conf 的 proxy_pass 是否指向 K8S Service 名称和端口。
- **数据库初始化失败**：确认 SQL 文件路径和 Pod 名称正确，密码一致。
- **Pod CrashLoopBackOff**：用 `kubectl logs` 查看详细报错。
- **端口冲突或访问不到服务**：确认 Service 类型为 NodePort，端口未被占用。

------

## 9. 其它说明

- 所有镜像名、端口、Service 名称需与 k8s 配置、nginx.conf 保持一致。
- 推荐使用 Jenkins 自动化流水线，便于持续集成与部署。
- 如需自定义环境变量、端口等，请同步修改 Dockerfile、k8s yaml、nginx.conf 等相关配置。
- Redis 如需持久化或密码保护，请在 `redis.yaml` 中配置持久卷和密码。
- 若需扩展为微服务架构，可参考 `microservices-api-documentation.md` 和 `.trae/documents/微服务化重构方案.md`。

------

## 10. 复现成功标准

- `kubectl get all` 显示所有 Pod、Service 均为 Running/Ready。
- 前端 NodePort 可访问页面，后端 NodePort 可用 Postman 测试接口。
- 数据库已初始化，功能正常。
- 前后端、数据库、Redis 均以容器方式运行于本地 K8S 集群。
