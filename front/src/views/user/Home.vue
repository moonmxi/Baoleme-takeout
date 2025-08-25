<template>
  <app-layout>
    <div class="home-container">
      <!-- 右上角头像按钮 -->
      <div class="profile-avatar-btn" @click="openProfileDialog">
        <el-avatar
          :size="80"
          :src="
            userInfo.avatar
              ? `http://localhost:8080/images/${userInfo.avatar}`
              : defaultAvatar
          "
        />
      </div>
      
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>首页</h2>
          </div>
        </template>

        <div class="welcome-section">
          <h3>欢迎回来，{{ userStore.userInfo?.username }}</h3>
          <el-button
            type="primary"
            @click="handleLogout"
            class="edit-profile-btn"
            >退出登录
          </el-button>
        </div>
      </el-card>

      <!-- 搜索区域 -->
      <el-card class="search-card" shadow="hover">
        <div class="search-wrapper">
          <div class="search-input-wrapper">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索商家或商品"
              class="search-input"
              @keyup.enter="handleSearch"
              clearable
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
              <template #append>
                <el-button
                  type="primary"
                  @click="handleSearch"
                  :loading="searching"
                >
                  搜索
                </el-button>
              </template>
            </el-input>
          </div>
          <div
            class="search-advanced"
            style="margin: 12px 0; display: flex; gap: 12px"
          >
            <el-input-number
              v-model="searchForm.distance"
              :min="0"
              :max="20"
              placeholder="距离(km)"
              style="width: 160px"
            />
            <el-input-number
              v-model="searchForm.wishPrice"
              :min="0"
              placeholder="期望价格"
              style="width: 160px"
            />
            <el-input-number
              v-model="searchForm.startRating"
              :min="0"
              :max="5"
              placeholder="最低评分"
              style="width: 140px"
            />
            <el-input-number
              v-model="searchForm.endRating"
              :min="0"
              :max="5"
              placeholder="最高评分"
              style="width: 140px"
            />
            <!-- 新增配送时间选择，仅做样子 -->
            <el-select
              v-model="deliveryTime"
              placeholder="配送时间"
              style="width: 160px"
            >
              <el-option label="30分钟内" value="now" />
              <el-option label="30分钟后" value="30min" />
              <el-option label="1小时后" value="1h" />
              <el-option label="自定义" value="custom" />
            </el-select>
          </div>
          <div class="search-tips">
            <span class="tip-item">热门搜索：</span>
            <el-tag
              v-for="tag in hotSearches"
              :key="tag"
              class="hot-tag"
              @click="handleHotSearch(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 搜索结果展示区域 -->
        <div v-if="searchResults.length > 0" class="search-results">
          <div class="results-header">
            <h3>搜索结果</h3>
            <span class="results-count">共找到 {{ searchTotal }} 个商家</span>
          </div>
          <div
            v-for="store in searchResults"
            :key="store.id || store.id"
            class="search-store-item"
            style="
              margin-bottom: 32px;
              border-bottom: 1px solid #eee;
              padding-bottom: 24px;
              cursor: pointer;
            "
            @click="goToStore(store)"
          >
            <!-- 商家信息 -->
            <div
              class="store-info"
              style="display: flex; align-items: center; margin-bottom: 16px"
            >
              <el-avatar
                :src="getStoreImage(store)"
                :size="60"
                style="margin-right: 20px"
              />
              <div class="store-meta">
                <div
                  class="store-title"
                  style="font-size: 20px; font-weight: bold"
                >
                  <span class="store-name">{{ store.name || store.name }}</span>
                  <el-rate
                    v-if="store.rating !== undefined && store.rating !== null"
                    :model-value="store.rating"
                    disabled
                    show-score
                    style="margin-left: 12px"
                  />
                </div>
                <div class="store-desc" style="color: #888; margin-top: 6px">
                  <span v-if="store.location">地址：{{ store.location }}</span>
                  <span v-if="store.description" style="margin-left: 16px">{{
                    store.description
                  }}</span>
                </div>
              </div>
            </div>
            <!-- 商品列表 -->
            <div
              class="product-list"
              v-if="store.products && store.products.length"
            >
              <el-row :gutter="16">
                <el-col
                  v-for="product in store.products.filter(
                    (p) => p.status === 1
                  )"
                  :key="product.id"
                  :span="8"
                  style="margin-bottom: 20px"
                >
                  <el-card class="product-card" shadow="hover">
                    <img
                      :src="getProductImage(product)"
                      class="product-img"
                      :alt="product.name"
                      style="
                        width: 100%;
                        height: 120px;
                        object-fit: cover;
                        border-radius: 8px;
                      "
                    />
                    <div class="product-info" style="margin-top: 10px">
                      <div class="product-name" style="font-weight: bold">
                        {{ product.name }}
                      </div>
                      <div
                        class="product-desc"
                        style="color: #888; font-size: 14px; margin-bottom: 4px"
                      >
                        {{ product.description }}
                      </div>
                      <div
                        class="product-price"
                        style="
                          color: #409eff;
                          font-size: 16px;
                          font-weight: bold;
                        "
                      >
                        ￥{{ product.price }}
                      </div>
                      <div class="product-rating" style="margin-top: 4px">
                        <el-rate
                          v-if="
                            product.rating !== undefined &&
                            product.rating !== null
                          "
                          :model-value="product.rating"
                          disabled
                          show-score
                          style="font-size: 14px"
                        />
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              <div
                v-if="store.products.filter((p) => p.status === 1).length === 0"
                style="color: #aaa; margin-top: 8px"
              >
                暂无上架商品
              </div>
            </div>
            <div v-else style="color: #aaa; margin-top: 8px">暂无商品</div>
          </div>
          <el-pagination
            v-model:current-page="searchPage"
            v-model:page-size="searchPageSize"
            :total="searchTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSearchSizeChange"
            @current-change="handleSearchPageChange"
            style="margin-top: 16px; text-align: right"
          />
        </div>
        <div v-else-if="searchForm.keyword && !searching" class="no-results">
          <el-empty description="暂无搜索结果" />
        </div>

        <el-row :gutter="20" class="stats-section">
          <el-col :span="8">
            <el-card shadow="hover" class="stats-card" @click="goToMap">
              <template #header>
                <div class="stats-header">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>订单状态</span>
                </div>
              </template>
              <div class="stats-content">
                <div class="stats-value">{{ cartCount }}</div>
                <div class="stats-label">件商品</div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="stats-card" @click="goToHistory">
              <template #header>
                <div class="stats-header">
                  <el-icon><Document /></el-icon>
                  <span>订单</span>
                </div>
              </template>
              <div class="stats-content">
                <div class="stats-value">{{ orderCount }}</div>
                <div class="stats-label">个订单</div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="stats-card" @click="goToFavorite">
              <template #header>
                <div class="stats-header">
                  <el-icon><Star /></el-icon>
                  <span>收藏</span>
                </div>
              </template>
              <div class="stats-content">
                <div class="stats-value">{{ favoriteCount }}</div>
                <div class="stats-label">件商品</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
  <el-card shadow="hover" class="stats-card" @click="goToHistoryView">
    <template #header>
      <div class="stats-header">
        <el-icon><Document /></el-icon>
        <span>历史记录</span>
      </div>
    </template>
    <div class="stats-content">
      <div class="stats-value">{{ historyCount }}</div>
      <div class="stats-label">家店铺</div>
    </div>
  </el-card>
</el-col>
        </el-row>

        <!-- <div class="recent-orders">
          <h3>最近订单</h3>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="order_id" label="订单号" width="180" />
            <el-table-column prop="created_at" label="创建时间" width="180" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="viewOrder(row.id)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div> -->
        <!-- 推荐商家区域 -->
        <div class="recommended-section">
          <h3>为你推荐</h3>
          <el-row :gutter="20">
            <el-col
              v-for="store in recommendedStores"
              :key="store.id"
              :span="12"
              style="margin-bottom: 20px"
              @click="goToStore(store)"
            >
              <el-card class="store-card" shadow="hover">
                <div
                  class="store-header"
                  style="display: flex; align-items: center"
                >
                  <el-avatar
                    :src="getStoreImage(store)"
                    :size="48"
                    style="margin-right: 16px"
                  />
                  <div>
                    <div
                      class="store-name"
                      style="font-size: 18px; font-weight: 500"
                    >
                      {{ store.name }}
                    </div>
                    <div
                      class="store-desc"
                      style="color: #888; font-size: 14px"
                    >
                      {{ store.description }}
                    </div>
                    <div
                      class="store-location"
                      style="color: #aaa; font-size: 13px"
                    >
                      {{ store.location }}
                    </div>
                  </div>
                </div>
                <div
                  v-if="store.products && store.products.length"
                  class="store-products"
                  style="margin-top: 12px"
                >
                  <div class="products-row">
                    <div
                      v-for="product in store.products.slice(0, 3)"
                      :key="product.id"
                      class="product-card"
                    >
                      <img
                        :src="getProductImage(product)"
                        :alt="product.name"
                        class="product-img"
                      />
                      <div class="product-info">
                        <div class="product-name">{{ product.name }}</div>
                        <div class="product-price">￥{{ product.price }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  v-else
                  style="color: #aaa; font-size: 13px; margin-top: 8px"
                >
                  暂无推荐商品
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-card>
      <!-- 个人资料弹窗 -->
      <el-dialog
        v-model="showProfile"
        title="个人资料"
        width="400px"
        :close-on-click-modal="false"
      >
        <template #default>
          <div v-if="!editingProfile">
            <div class="profile-dialog-content">
              头像
              <el-avatar
                :size="80"
                :src="
                  userInfo.avatar
                    ? `http://localhost:8080/images/${userInfo.avatar}`
                    : defaultAvatar
                "
              />

              <div class="info-item">
                <span class="label">用户ID：</span
                ><span class="value">{{ userStore.userInfo?.id }}</span>
              </div>
              <div class="info-item">
                <span class="label">用户名：</span
                ><span class="value">{{ userStore.userInfo?.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">手机号：</span
                ><span class="value">{{
                  userStore.userInfo?.phone || "未设置"
                }}</span>
              </div>
              <div class="info-item">
                <span class="label">性别：</span
                ><span class="value">{{
                  userStore.userInfo?.gender || "未设置"
                }}</span>
              </div>
              <div class="info-item">
                <span class="label">地址：</span
                ><span class="value">{{
                  userStore.userInfo?.location || "未设置"
                }}</span>
              </div>
              <div class="info-item">
                <span class="label">个人简介：</span
                ><span class="value">{{
                  userStore.userInfo?.description || "未设置"
                }}</span>
              </div>
            </div>
            <div style="text-align: right; margin-top: 16px">
              <el-popconfirm
                title="确定要注销账户吗？此操作不可恢复！"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleDeleteAccount"
              >
                <template #reference>
                  <el-button type="danger" plain>注销账户</el-button>
                </template>
              </el-popconfirm>
            </div>
            <div style="text-align: right; margin-top: 24px">
              <el-button type="primary" @click="editingProfile = true"
                >编辑资料</el-button
              >
            </div>
          </div>
          <div v-else>
            <div class="profile-dialog-content">
              <el-form ref="formRef" :model="form" label-width="100px">
                <el-form-item label="头像">
                  <el-upload
                    class="avatar-uploader"
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload"
                    :on-success="handleAvatarSuccess"
                    :action="uploadAction"
                    name="file"
                    :headers="uploadHeaders"
                  >
                    <el-avatar
                      :size="80"
                      :src="
                        userInfo.avatar
                          ? `http://localhost:8080/images/${userInfo.avatar}`
                          : defaultAvatar
                      "
                    />
                    <div class="upload-tip">点击头像上传</div>
                  </el-upload>
                </el-form-item>
              </el-form>
            </div>
            <el-form :model="editProfileForm" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="editProfileForm.username" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="editProfileForm.phone" />
              </el-form-item>
              <el-form-item label="性别">
                <el-select
                  v-model="editProfileForm.gender"
                  placeholder="请选择性别"
                >
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                  <el-option label="保密" value="保密" />
                </el-select>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="editProfileForm.location" />
              </el-form-item>
              <el-form-item label="个人简介">
                <el-input
                  v-model="editProfileForm.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入个人简介"
                />
              </el-form-item>
            </el-form>
            <div style="text-align: right; margin-top: 24px">
              <el-button @click="editingProfile = false">取消</el-button>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from "vue";
import { useRouter } from "vue-router";
import {
  ShoppingCart,
  Document,
  Star,
  Search,
  ArrowRight,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import type {
  SearchResult,
  Order,
  OrderStatus,
  OrderHistoryResponseData,
  OrderItem,
} from "@/types";
import { userApi } from "@/api/user";
const deliveryTime = ref("now"); // 仅做样子，不参与搜索参数
const form = ref({
  username: "",
  phone: "",
  password: "",
  location: "",
  description: "",
  avatar: "",
});

const uploadAction = "/api/image/upload-user-avatar";
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
};

// 上传头像前校验
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;
  console.log("上传头像文件:", file);
  if (!isJPG && !isPNG) {
    ElMessage.error("上传头像图片只能是 JPG 或 PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
    return false;
  }
  return true;
};
// 上传头像成功回调
const handleAvatarSuccess = (response: any) => {
  console.log("头像上传响应:", response);
  if (response && response.success && response.data) {
    form.value.avatar = response.data; // 完整URL
    ElMessage.success("头像上传成功");
    console.log("头像上传成功:", response.data);
    userStore.getUserInfo(); // 更新骑手信息
    console.log("更新骑手信息:", userStore.userInfo);
  } else {
    ElMessage.error(response?.message || "头像上传失败");
  }
};
const userInfo = computed(
  () =>
    userStore.userInfo || {
      id: "",
      username: "",
      phone: "",
      location: "",
      description: "",
      avatar: "",
    }
);

const defaultAvatar = "/logo.png";
const searchForm = reactive({
  keyword: "",
  distance: undefined,
  wishPrice: undefined,
  startRating: undefined,
  endRating: undefined,
});
const router = useRouter();
const userStore = useUserStore();

const cartCount = ref(0);
const orderCount = ref(0);
const favoriteCount = ref(0);
const recentOrders = ref<OrderItem[]>([]);
const searchKeyword = ref("");
const searchResults = ref<SearchResult[]>([]);
const stores = ref([]);
const searching = ref(false);
const hotSearches = ref(["奶茶", "快餐", "水果", "零食"]);
const searchPage = ref(1);
const searchPageSize = ref(10);
const searchTotal = ref(0);
const showProfile = ref(false);
const editingProfile = ref(false);
const recommendedStores = ref<any[]>([]);

// 头像url，优先用用户头像，没有则用logo
const avatarUrl = ref(userStore.userInfo?.avatar || defaultAvatar);
const editProfileForm = reactive({
  username: userStore.userInfo?.username || "",
  phone: userStore.userInfo?.phone || "",
  avatar: userStore.userInfo?.avatar || defaultAvatar,
  gender: userStore.userInfo?.gender || "or",
  location: userStore.userInfo?.location || "",
  description: userStore.userInfo?.description || "",
});
// 拼接商家图片URL
const getStoreImage = (store: any) => {
  if (!store.image) return "/logo.png";
  return store.image.startsWith("/images/")
    ? "http://localhost:8080" + store.image
    : "http://localhost:8080/images/" + store.image;
};
// 拼接商品图片URL
const getProductImage = (product: any) => {
  if (!product.image) return "/logo.png";
  return product.image.startsWith("/images/")
    ? "http://localhost:8080" + product.image
    : "http://localhost:8080/images/" + product.image;
};

const loadStats = async () => {
  try {
    // 获取订单数
    const orderRes = await userApi.getOrderHistory({
      page: 1,
      page_size: 100000,
    });
    if (
      orderRes.success &&
      orderRes.data &&
      Array.isArray(orderRes.data.orders)
    ) {
      orderCount.value = orderRes.data.orders.length;
    } else {
      orderCount.value = 0;
    }
    // 获取收藏数
    const favRes = await userApi.getFavoriteStores({ page: 1, page_size: 100000 });
    if (favRes.success && Array.isArray(favRes.data)) {
      favoriteCount.value = favRes.data.length;
    } else {
      favoriteCount.value = 0;
    }
     const res = await userApi.getViewHistory({ page: 1, page_size: 100000 });
    if (res.success && res.data && Array.isArray(res.data.stores)) {
      historyCount.value = res.data.stores.length;
    } else {
      historyCount.value = 0;
    }
  } catch (e) {
    console.log("获取统计数据失败:", e);
  }
};
const handleLogout = async () => {
  try {
    await userApi.logout(); // 先请求后端
    ElMessage.success("退出登录成功");
  } catch (error: any) {
    ElMessage.error(error.message || "退出登录失败");
  } finally {
    userStore.clearToken(); // 最后再清除本地 token
    router.push("/");
  }
};
const historyCount = ref(0);
const goToHistoryView = () => {
  router.push("/user/history-view");
};

const loadRecommendedStores = async () => {
  try {
    // 1. 获取推荐商家列表
    console.log("加载推荐商家列表...");
    const res = await userApi.getStoreList({ page: 1, page_size: 6 });
    console.log("推荐商家列表响应:", res);
    if (res.success && res.data && Array.isArray(res.data)) {
      const stores = res.data.map((store: any) => ({
        ...store,
        id: store.id ?? store.store_id,
      }));
      console.log("推荐商家:", stores);
      // 2. 批量获取每个商家的推荐商品（最多3个上架商品）
      const storeWithProducts = await Promise.all(
        stores.map(async (store: any) => {
          let products: any[] = [];
          try {
            const proRes = await userApi.getProductList({ store_id: store.id });
            if (proRes.success && proRes.data && Array.isArray(proRes.data)) {
              products = proRes.data
                .filter((p: any) => p.status === 1)
                .slice(0, 3);
            }
          } catch (e) {
            console.log("获取商品失败:", e);
          }
          return { ...store, products };
        })
      );
      recommendedStores.value = storeWithProducts;
    } else {
      recommendedStores.value = [];
      console.error(res.message || "获取推荐商家失败");
    }
  } catch (error) {
    console.error("获取推荐商家失败:", error);
    recommendedStores.value = [];
  }
};

const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "warning", // 待接单
    1: "info", // 准备中
    2: "primary", // 配送中
    3: "success", // 完成
    4: "danger", // 取消
  };
  return statusMap[status] || "info";
};
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待接单",
    1: "准备中",
    2: "配送中",
    3: "已完成",
    4: "已取消",
  };
  return statusMap[status] || "未知";
};

// 查看订单详情
const viewOrder = (orderId: number) => {
  router.push(`/order/${orderId}`);
};

// 保存信息
const saveProfile = async () => {
  try {
    if (!userStore.userInfo) {
      ElMessage.error("用户信息不存在");
      return;
    }

    // 1. 构造更新数据，只传递修改的字段
    const updateData: {
      username?: string;
      phone?: string;
      avatar?: string;
      gender?: string;
      location?: string;
      description?: string;
    } = {};

    // 2. 检查数据是否有变化
    if (editProfileForm.username !== userStore.userInfo.username) {
      updateData.username = editProfileForm.username;
    }
    if (editProfileForm.phone !== userStore.userInfo.phone) {
      updateData.phone = editProfileForm.phone;
    }
    if (avatarUrl.value !== userStore.userInfo.avatar) {
      updateData.avatar = avatarUrl.value;
    }
    if (editProfileForm.gender !== userStore.userInfo.gender) {
      updateData.gender = editProfileForm.gender;
    }
    if (editProfileForm.location !== userStore.userInfo.location) {
      updateData.location = editProfileForm.location;
    }
    if (editProfileForm.description !== userStore.userInfo.description) {
      updateData.description = editProfileForm.description;
    }

    // 如果没有修改任何内容，直接返回
    if (Object.keys(updateData).length === 0) {
      ElMessage.info("未修改任何信息");
      editingProfile.value = false;
      return;
    }

    // 3. 调用更新接口
    const res = await userApi.updateUserInfo(updateData);

    if (res.success) {
      ElMessage.success("保存成功");

      // 4. 如果修改了用户名，会返回新token
      if (res.data?.token) {
        userStore.setToken(res.data.token);
        localStorage.setItem("token", res.data.token);
      }

      // 5. 重新获取用户信息
      await userStore.getUserInfo();

      // 6. 关闭编辑模式
      editingProfile.value = false;
      showProfile.value = false;
    } else {
      throw new Error(res.message || "保存失败");
    }
  } catch (error: any) {
    ElMessage.error(error.message || "保存失败");
  }
};

// 跳转到购物车页面
const goToCart = () => {
  router.push("/user/cart");
};

// 跳转到地图页面
const goToMap = () => {
  router.push("/user/map");
};

// 跳转到订单页面
const goToHistory = () => {
  router.push("/user/history");
};

// 跳转到收藏页面
const goToFavorite = () => {
  router.push("/user/favorite");
};

// 处理热门搜索点击
const handleHotSearch = (tag: string) => {
  searchKeyword.value = tag;
  handleSearch();
};

// 搜索
const handleSearch = async () => {
  if (!searchForm.keyword.trim()) {
    searchResults.value = [];
    searchTotal.value = 0;
    return;
  }
  searching.value = true;
  try {
    const params: any = {
      keyword: searchForm.keyword,
      page: searchPage.value,
      page_size: searchPageSize.value,
    };
    if (searchForm.distance !== undefined && searchForm.distance !== null)
      params.distance = searchForm.distance;
    if (searchForm.wishPrice !== undefined && searchForm.wishPrice !== null)
      params.wishPrice = searchForm.wishPrice;
    if (searchForm.startRating !== undefined && searchForm.startRating !== null)
      params.startRating = searchForm.startRating;
    if (searchForm.endRating !== undefined && searchForm.endRating !== null)
      params.endRating = searchForm.endRating;
    console.log("搜索参数:", params);
    const response = await userApi.search(params);

    if (response.success) {
      // 1. 拿到商家基本信息
      const stores = response.data.results || response.data.reults || [];
      console.log("搜索结果:", stores);
      // 2. 批量查商品列表
      const storeDetailPromises = stores.map(async (store: any) => {
        let products: any[] = [];
        try {
          const proRes = await userApi.getProductList({
            store_id: store.store_id || store.id,
          });
          if (proRes.success && proRes.data && Array.isArray(proRes.data)) {
            products = proRes.data;
          }
        } catch (e) {
          console.log("获取商品详情失败:", e);
        }
        return {
          ...store,
          products,
        };
      });

      console.log("11", storeDetailPromises);
      searchResults.value = await Promise.all(storeDetailPromises);
      console.log("搜索结果详情:", searchResults);
      searchTotal.value = searchResults.value.length;
    } else {
      searchResults.value = [];
      searchTotal.value = 0;
      console.error(response.message || "搜索失败");
    }
  } catch (error) {
    console.error("搜索失败:", error);
    searchResults.value = [];
    searchTotal.value = 0;
  } finally {
    searching.value = false;
  }
};
const handleSearchPageChange = (val: number) => {
  searchPage.value = val;
  handleSearch();
};
const handleSearchSizeChange = (val: number) => {
  searchPageSize.value = val;
  searchPage.value = 1;
  handleSearch();
};

// 跳转到商家详情
const goToStore = (store: any) => {
  console.log("跳转到商家详情:", store);
  router.push(`/user/merchant/${store.id || store.store_id}`);
};

const openProfileDialog = () => {
  editingProfile.value = false;
  showProfile.value = true;
  // 打开时同步头像
  avatarUrl.value = userStore.userInfo?.avatar || defaultAvatar;
  editProfileForm.avatar = avatarUrl.value;
};

const goToProduct = (id: number) => {
  router.push(`/user/product/${id}`);
};

const handleDeleteAccount = async () => {
  try {
    // 假设 userApi.deleteAccount() 已实现
    const res = await userApi.delete();
    if (res.success) {
      ElMessage.success("账户已注销");
      userStore.clearToken();
      router.push("/");
    } else {
      throw new Error(res.message || "注销失败");
    }
  } catch (error: any) {
    console.error("注销账户失败:", error);
    ElMessage.error(error.message || "注销失败");
  }
};

onMounted(() => {
  if (!userStore.userInfo && localStorage.getItem("token")) {
    try {
      // 2. 重新获取用户信息
      userStore.getUserInfo();
    } catch (error) {
      console.error("获取用户信息失败:", error);
      userStore.clearToken();
      router.push("/login");
      return;
    }
  }

  // 3. 更新编辑表单的默认值
  if (userStore.userInfo) {
    editProfileForm.username = userStore.userInfo.username;
    editProfileForm.phone = userStore.userInfo.phone || "";
    editProfileForm.avatar = userStore.userInfo.avatar || defaultAvatar;
    avatarUrl.value = userStore.userInfo.avatar || defaultAvatar;
  }

  // 4. 加载其他数据
  try {
    Promise.all([loadStats(), loadRecommendedStores()]);
  } catch (error) {
    console.error("加载数据失败:", error);
  }
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;
:deep(.el-input-group__append) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0 !important;
}
.store-products {
  margin-top: 12px;
  .products-row {
    display: flex;
    gap: 16px;
    justify-content: flex-start;
    align-items: stretch;
    flex-wrap: wrap;
  }
  .product-card {
    width: 120px;
    min-width: 0;
    background: #f8fafd;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
    padding: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    transition: box-shadow 0.2s;
    cursor: pointer;
    &:hover {
      box-shadow: 0 4px 16px rgba(64, 158, 255, 0.12);
      transform: translateY(-2px) scale(1.03);
    }
    .product-img {
      width: 100px;
      height: 70px;
      object-fit: cover;
      border-radius: 8px;
      background: #fff;
      margin-bottom: 6px;
    }
    .product-info {
      width: 100%;
      text-align: center;
      .product-name {
        font-size: 14px;
        font-weight: 500;
        color: #333;
        margin-bottom: 2px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .product-price {
        color: #409eff;
        font-size: 13px;
        font-weight: bold;
      }
    }
  }
}
.home-container {
  padding: $spacing-base;
  background-color: #f5f7fa;
  min-height: 100vh;

  .card-header {
    h2 {
      margin: 0;
      color: $text-primary;
      font-size: 24px;
      font-weight: 600;
    }
  }

  .welcome-section {
    margin-bottom: $spacing-large;
    padding: $spacing-large;
    background: linear-gradient(135deg, #409eff 0%, #36d1dc 100%);
    border-radius: 16px;
    color: white;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);

    h3 {
      margin: 0;
      font-size: 28px;
      font-weight: 600;
    }
  }

  .search-card {
    margin-bottom: $spacing-large;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    }

    .search-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-large;

      .search-input-wrapper {
        position: relative;
        margin-bottom: $spacing-base;
        width: 60%;
        min-width: 300px;
        max-width: 600px;

        .search-input {
          width: 100%;

          :deep(.el-input__wrapper) {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            border-radius: 24px;
            padding: 0 20px;
            height: 48px;
            transition: all 0.3s ease;

            &.is-focus {
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
            }
          }

          :deep(.el-input__prefix) {
            .search-icon {
              font-size: 20px;
              color: $text-secondary;
            }
          }

          :deep(.el-input-group__append) {
            margin-left: 12px;
            border: none;
            box-shadow: none !important;

            .el-button {
              border: none !important;
              border-radius: 20px;
              padding: 0 24px;
              height: 100%;
              margin-left: 0;
              transition: all 0.3s ease;
              position: relative;
              overflow: hidden;
              background: linear-gradient(135deg, #409eff 0%, #36d1dc 100%);

              &:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
              }

              &:active {
                transform: translateY(0);
                box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
              }
            }
          }
        }
      }

      .search-tips {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: $spacing-small;
        padding: 0 $spacing-small;
        justify-content: center;
        width: 100%;

        .tip-item {
          color: $text-secondary;
          font-size: $font-size-small;
        }

        .hot-tag {
          cursor: pointer;
          transition: all 0.3s;
          background: #f0f7ff;
          border: 1px solid #e6f0ff;
          color: #409eff;

          &:hover {
            transform: translateY(-2px);
            background: #409eff;
            color: white;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
          }
        }
      }
    }
  }

  .profile-avatar-btn {
    position: fixed;
    top: 32px;
    right: 48px;
    z-index: 1001;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    border-radius: 50%;
    background: #fff;
    transition: box-shadow 0.2s;
    &:hover {
      box-shadow: 0 4px 16px rgba(64, 158, 255, 0.18);
    }
  }

  .profile-dialog-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    .info-item {
      margin-bottom: 12px;
      .label {
        color: $text-secondary;
        margin-right: 8px;
      }
      .value {
        color: $text-primary;
        font-weight: 500;
      }
    }
  }

  .stats-section {
    margin-bottom: $spacing-large;

    .stats-card {
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: 16px;
      overflow: hidden;
      background: white;
      border: none;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
      }

      .stats-header {
        display: flex;
        align-items: center;
        gap: $spacing-small;
        padding: $spacing-base;
        background: linear-gradient(135deg, #f6f9fc 0%, #f0f7ff 100%);

        .el-icon {
          font-size: 24px;
          color: #409eff;
        }

        span {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
        }
      }

      .stats-content {
        text-align: center;
        padding: $spacing-large $spacing-base;

        .stats-value {
          font-size: 32px;
          font-weight: bold;
          color: #409eff;
          margin-bottom: $spacing-small;
          background: linear-gradient(135deg, #409eff 0%, #36d1dc 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }

        .stats-label {
          color: $text-secondary;
          font-size: 14px;
        }
      }
    }
  }

  .recent-orders {
    background: white;
    border-radius: 16px;
    padding: $spacing-large;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

    h3 {
      margin: 0 0 $spacing-base;
      color: $text-primary;
      font-size: 20px;
      font-weight: 600;
    }

    :deep(.el-table) {
      border-radius: 8px;
      overflow: hidden;

      th {
        background-color: #f6f9fc;
        font-weight: 600;
      }

      td {
        padding: 16px 0;
      }

      .el-tag {
        border-radius: 4px;
        padding: 4px 8px;
      }
    }
  }
  .recommended-section {
    margin-top: 32px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 32px 24px 16px 24px;
    h3 {
      margin: 0 0 24px 0;
      color: $text-primary;
      font-size: 20px;
      font-weight: 600;
    }
    .store-card {
      display: flex;
      align-items: center;
      background: #fff;
      border-radius: 14px;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
      padding: 12px 16px;
      cursor: pointer;
      transition: box-shadow 0.2s, transform 0.2s;
      &:hover {
        box-shadow: 0 4px 16px rgba(64, 158, 255, 0.12);
        transform: translateY(-2px) scale(1.02);
      }
      .store-logo {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        object-fit: cover;
        margin-right: 16px;
        background: #f5f7fa;
      }
      .store-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        .store-name {
          font-size: 17px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 6px;
        }
        .store-desc {
          color: $text-secondary;
          font-size: 15px;
        }
      }
    }
  }
}

:deep(.el-input-group__append) .el-button {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 12px;
  .upload-tip {
    font-size: 12px;
    color: $text-secondary;
    margin-top: 4px;
  }
}

.recommended-products {
  margin-top: 24px;
  .product-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 14px;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
    padding: 12px 16px;
    cursor: pointer;
    transition: box-shadow 0.2s, transform 0.2s;
    &:hover {
      box-shadow: 0 4px 16px rgba(64, 158, 255, 0.12);
      transform: translateY(-2px) scale(1.02);
    }
    .product-img {
      width: 60px;
      height: 60px;
      border-radius: 10px;
      object-fit: cover;
      margin-right: 16px;
      background: #f5f7fa;
    }
    .product-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      .product-name {
        font-size: 17px;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 6px;
      }
      .product-desc {
        color: #888;
        font-size: 14px;
        margin-bottom: 4px;
      }
      .product-price {
        color: $primary-color;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }
}
</style>
