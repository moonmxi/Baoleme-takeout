<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>商家登录</h2>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <el-button type="text" :loading="loading" @click="handleRegister">还没有账号？去注册</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchantApi } from '@/api/merchant'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const form = ref({
  username: '',
  phone: '',
  password: ''
})
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const res = await merchantApi.login({
      username: form.value.username,
      phone: form.value.phone,
      password: form.value.password
    })
    if (res.success) {
      ElMessage.success('登录成功')
      localStorage.setItem('merchant_token', res.data.token)
      const redirect = route.query.redirect as string
      router.push(redirect || '/merchant/home')
    } else {
      ElMessage.error(res?.message || '登录失败')
    }
  } catch (error: any) {
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}
const handleRegister = () => {
  router.push('/merchant/register')
}

</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $background-color;

  .login-card {
    width: 400px;

    .card-header {
      text-align: center;
      h2 {
        margin: 0;
        color: $text-primary;
      }
    }

    .login-form {
      padding: $spacing-base;
    }

    .form-footer {
      text-align: center;
      margin-top: $spacing-base;
      a {
        color: $primary-color;
        margin: 0 $spacing-small;
      }
    }
  }
}
</style>