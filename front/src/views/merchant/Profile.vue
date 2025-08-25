<template>
  <div class="merchant-profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人资料</h2>
          <el-button v-if="!editing" type="primary" @click="editing = true">编辑资料</el-button>
        </div>
      </template>
      <div v-if="!editing" class="profile-content">
        <el-avatar :size="80" :src="merchantInfo.avatar ? `http://localhost:8080/images/${merchantInfo.avatar}` : defaultAvatar" />
        <div class="info-item"><span class="label">商家ID：</span><span class="value">{{ profile.user_id }}</span></div>
        <div class="info-item"><span class="label">用户名：</span><span class="value">{{ profile.username }}</span></div>
        <div class="info-item"><span class="label">手机号：</span><span class="value">{{ profile.phone }}</span></div>
        <div class="info-item"><span class="label">注册时间：</span><span class="value">{{ profile.created_at ? formatDate(profile.created_at) : '' }}</span></div>
      </div>
      <div v-else>
        <el-form :model="editForm" label-width="80px">
          <el-form
        ref="formRef"
        :model="form"
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
    <el-avatar :size="80" :src="merchantInfo.avatar ? `http://localhost:8080/images/${merchantInfo.avatar}` : defaultAvatar" />
    <div class="upload-tip">点击头像上传</div>
  </el-upload>
</el-form-item>
</el-form>
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="editForm.password" />
          </el-form-item>
        </el-form>
        <div style="text-align:right;margin-top:24px;">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted ,computed} from 'vue'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/logo.png'
import { merchantApi } from '@/api/merchant'
import { useMerchantStore } from '@/store/merchant'
import { useRouter } from 'vue-router'


const router = useRouter()
const merchantStore = useMerchantStore()
const editing = ref(false)
const profile = reactive({
  user_id: '',
  username: '',
  phone: '',
  created_at: '',
  avatar: ''
})
const editForm = reactive({
  username: '',
  phone: '',
  password: '',
  avatar: ''
})
const form = ref({
  username: '',
  phone: '',
  password: '',
  avatar: ''
})
const merchantInfo = computed(() => merchantStore.merchantInfo || {
  user_id: '',
  username: '',
  phone: '',
  created_at: '',
  avatar: ''
})

const uploadAction = '/api/image/upload-merchant-avatar'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('merchant_token') || ''}`
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
    merchantStore.getMerchantInfo() // 更新骑手信息
    console.log('更新骑手信息:', merchantStore.merchantInfo)
  } else {
    ElMessage.error(response?.message || '头像上传失败')
  }
}

// 日期格式化
function formatDate(dateStr: string) {
  const d = new Date(dateStr)
  return d.toLocaleString()
}
// 获取商家信息
const fetchProfile = async () => {
  try {
    const res = await merchantApi.getMerchantInfo()
    if (res.success) {
      Object.assign(profile, res.data)
      Object.assign(editForm, res.data)
    } else {
      ElMessage.error(res.message || '获取资料失败')
      console.error('获取商家信息失败:', res.message)
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取资料失败')
  }
}

// 保存资料
const saveProfile = async () => {
  try {
    const payload: any = {}
    if (editForm.username !== profile.username) payload.username = editForm.username
    if (editForm.phone !== profile.phone) payload.phone = editForm.phone 
    if (editForm.password) payload.password = editForm.password // 仅在修改时发送密码
    if (editForm.avatar !== profile.avatar) payload.avatar = editForm.avatar

    const res = await merchantApi.updateMerchantInfo(payload)
    if (res.success) {
      // 保存新token（注意是 new_token）
      if (res.data?.new_token) {
        localStorage.setItem('merchant_token', res.data.new_token)
      }
      ElMessage.success('资料已保存')
      editing.value = false
      fetchProfile()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  }
}

onMounted(() => {

  if (!merchantStore.merchantInfo && localStorage.getItem('merchant_token')) {
    try {
      merchantStore.getMerchantInfo()
    } catch (error) {
      console.error('获取商家信息失败:', error)
      merchantStore.clearToken()
      router.push('/')
      return
    }
  }

  try {
      Promise.all([
      fetchProfile()
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
// 头像上传（本地预览）
const handleAvatarChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    editForm.avatar = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const cancelEdit = () => {
  editing.value = false
  Object.assign(editForm, profile)
}
</script>

<style scoped lang="scss">
.merchant-profile-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}
.profile-card {
  width: 420px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    h2 {
      margin: 0;
      color: #333;
      font-size: 22px;
      font-weight: 600;
    }
  }
  .profile-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    .info-item {
      margin-bottom: 14px;
      .label {
        color: #888;
        margin-right: 8px;
      }
      .value {
        color: #222;
        font-weight: 500;
      }
    }
  }
  .avatar-uploader {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 12px;
    .upload-tip {
      font-size: 12px;
      color: #888;
      margin-top: 4px;
    }
  }
}
</style>

<style scoped lang="scss">
.merchant-profile-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}
.profile-card {
  width: 420px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    h2 {
      margin: 0;
      color: #333;
      font-size: 22px;
      font-weight: 600;
    }
  }
  .profile-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    .info-item {
      margin-bottom: 14px;
      .label {
        color: #888;
        margin-right: 8px;
      }
      .value {
        color: #222;
        font-weight: 500;
      }
    }
  }
  .avatar-uploader {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 12px;
    .upload-tip {
      font-size: 12px;
      color: #888;
      margin-top: 4px;
    }
  }
}
</style> 