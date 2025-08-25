<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>商家注册</h2>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @keyup.enter="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
          <el-button type="text" @click="goLogin">已有账号？登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchantApi } from '@/api/merchant'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (rule: any, value: string, callback: any) => {
      if (value !== form.value.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    console .log('注册数据:', form.value)
    const res = await merchantApi.register({
      username: form.value.username,
      phone: form.value.phone,
      password: form.value.password
    })
    if (res.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/merchant/login')
    } else {
      ElMessage.error(res?.message || '注册失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败')
    console.error('注册错误:', error)
  } finally {
    loading.value = false
  }
}
const goLogin = () => {
  router.push('/merchant/login')
}

</script>

<style scoped lang="scss">
.shop-register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f7faff;

  .register-card {
    width: 400px;
    .el-card__header {
      text-align: center;
      h2 {
        margin: 0;
        color: #333;
      }
    }
    .form-footer {
      text-align: center;
      margin-top: 16px;
      a {
        color: #409eff;
      }
    }
  }
}
</style>