<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>个人信息</h3>
          <el-button type="primary" @click="handleEdit">编辑信息</el-button>
        </div>
      </template>
      <div class="info-content">
        <div class="info-item">
          <span class="label">头像：</span>
          <el-avatar :size="64" :src="riderInfo.avatar ? `http://localhost:8080/images/${riderInfo.avatar}` : defaultAvatar" /> 
        </div>
        <div class="info-item">
          <span class="label">用户ID：</span>
          <span class="value">{{ riderInfo.id }}</span>
        </div>
        <div class="info-item">
          <span class="label">用户名：</span>
          <span class="value">{{ riderInfo.username }}</span>
        </div>
        <div class="info-item">
          <span class="label">手机号：</span>
          <span class="value">{{ riderInfo.phone }}</span>
        </div>
        <div class="info-item">
          <span class="label">接单状态：</span>
          <el-tag :type="riderInfo.order_status === 1 ? 'success' : 'info'">
            {{ riderInfo.order_status === 1 ? '接单中' : '暂停接单' }}
          </el-tag>
        </div>
        <div class="info-item">
          <span class="label">接单方式：</span>
          <el-tag :type="riderInfo.dispatch_mode === 1 ? 'success' : 'warning'">
            {{ riderInfo.dispatch_mode === 1 ? '自动接单' : '手动接单' }}
          </el-tag>
        </div>
        <div class="info-item">
          <span class="label">账户余额：</span>
          <span class="value">¥{{ riderInfo.balance?.toFixed(2) }}</span>
        </div>
      </div>
      <el-button
        type="primary"
        @click="deleteAccount"
        style="margin-top: 20px;"
      >注销账户
      </el-button>
    </el-card>

    <!-- 编辑信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑信息"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
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
    <el-avatar :size="80" :src="riderInfo.avatar ? `http://localhost:8080/images/${riderInfo.avatar}` : defaultAvatar" />
    <div class="upload-tip">点击头像上传</div>
  </el-upload>
</el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="接单状态" prop="order_status">
          <el-switch
            v-model="form.order_status"
            :active-value="1"
            :inactive-value="0"
            active-text="接单中"
            inactive-text="暂停接单"
          />
        </el-form-item>
        <el-form-item label="接单方式" prop="dispatch_mode">
          <el-radio-group v-model="form.dispatch_mode">
            <el-radio :label="1">自动接单</el-radio>
            <el-radio :label="0">手动接单</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { riderApi } from '@/api/rider'
import { useRiderStore } from '@/store/rider'
import router from '@/router'

const riderStore = useRiderStore()
const defaultAvatar = '/logo.png'
// 直接用 pinia 的 riderInfo
const riderInfo = computed(() => riderStore.riderInfo || {
  id: '',
  username: '',
  phone: '',
  order_status: 0,
  dispatch_mode: 0,
  balance: 0,
  avatar: '',
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = ref({
  username: '',
  phone: '',
  password: '',
  order_status: 1,
  dispatch_mode: 1,
  avatar: ''
})
const uploadAction = '/api/image/upload-rider-avatar'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('rider_token') || ''}`
}
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}
// 上传头像前校验
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  console.log('上传头像文件:', file)
  if (!isJPG && !isPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}
// 上传头像成功回调
const handleAvatarSuccess = (response: any) => {
  console.log('头像上传响应:', response)
  if (response && response.success && response.data) {
    form.value.avatar = response.data // 完整URL
    ElMessage.success('头像上传成功')
    console.log('头像上传成功:', response.data)
    riderStore.getInfo() // 更新骑手信息
    console.log('更新骑手信息:', riderStore.riderInfo)
  } else {
    ElMessage.error(response?.message || '头像上传失败')
  }
}



// 获取骑手信息并同步表单
const getRiderInfo = async () => {
  try {
    const response = await riderApi.getInfo()
    if (response && response.success) {
      // 同步表单
      const info = riderStore.riderInfo
      if (info) {
        form.value = {
          username: info.username,
          phone: info.phone,
          password: '',
          order_status: info.order_status,
          dispatch_mode: info.dispatch_mode,
          avatar: info.avatar || ''
        }
      }
    } else {
      ElMessage.error(response?.message || '获取骑手信息失败')
    }
  } catch (error) {
    ElMessage.error('获取骑手信息失败')
  }
}

// 编辑信息
const deleteAccount = async () => {
  try {
    const response = await riderApi.delete()
    if (response.success) {
      ElMessage.success('账户已注销')
      riderStore.clearRiderInfo()
      riderStore.clearToken()
      // 跳转到登录页
      router.push('/')
    } else {
      ElMessage.error(response.message || '注销账户失败')
      console.error('注销账户失败:', response.message)
    }
  } catch (error) {
    ElMessage.error('注销账户失败')
  }
}


// 编辑信息
const handleEdit = () => {
  const info = riderStore.riderInfo
  if (info) {
    form.value = {
      username: info.username,
      phone: info.phone,
      password: '',
      order_status: info.order_status,
      dispatch_mode: info.dispatch_mode,
      avatar: info.avatar || ''
    }
  }
  dialogVisible.value = true
}

// 提交表单时带上 avatar 字段
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const updateData: any = {}
        if (form.value.username !== riderInfo.value.username) updateData.username = form.value.username
        if (form.value.phone !== riderInfo.value.phone) updateData.phone = form.value.phone
        if (form.value.password) updateData.password = form.value.password
        if (form.value.order_status !== riderInfo.value.order_status) updateData.order_status = form.value.order_status
        if (form.value.dispatch_mode !== riderInfo.value.dispatch_mode) updateData.dispatch_mode = form.value.dispatch_mode
        //if (form.value.avatar && form.value.avatar !== riderInfo.value.avatar) updateData.avatar = form.value.avatar

        if (Object.keys(updateData).length === 0) {
          ElMessage.info('未修改任何信息')
          dialogVisible.value = false
          return
        }
        console.log("骑手数据",updateData)
        const response = await riderApi.updateInfo(updateData)
        if (response.success) {
          dialogVisible.value = false
          if (response.data?.token) {
            riderStore.setToken(response.data.token)
          }
          await riderStore.getInfo()
          ElMessage.success('更新成功')
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败', error)
      }
    }
  })
}


onMounted(() => {
  // 1. 如果没有 riderInfo 但有 token，尝试获取骑手信息
  if (!riderStore.riderInfo && localStorage.getItem('token')) {
    try {
      riderStore.getInfo()
    } catch (error) {
      console.error('获取骑手信息失败:', error)
      riderStore.clearToken()
      return
    }
  }

  // 2. 加载统计数据、待配送订单、收入统计
  try {
      Promise.all([
      getRiderInfo(),
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.profile-container {
  padding: $spacing-base;

  .profile-card {
    max-width: 800px;
    margin: 0 auto;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        color: $text-primary;
      }
    }

    .info-content {
      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: $spacing-base;
        
        &:last-child {
          margin-bottom: 0;
        }

        .label {
          width: 100px;
          color: $text-secondary;
        }

        .value {
          color: $text-primary;
          font-weight: 500;
        }
      }
    }
  }
}
</style>