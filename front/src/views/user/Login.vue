<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>登录</h2>
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
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="submit-btn"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="form-footer">
          <router-link to="/user/register">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const rules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
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

    // 1. 调用 userApi.login 登录接口
    console.log('登录信息:', form)
    console.log("开始")
    const res = await userApi.login({
      phone: form.phone,
      password: form.password
    })
    console.log('登录结果', res)
    // 2. 登录成功后，将 token 和用户信息存到 pinia
    if (res.success) {
      userStore.token=res.data.token
      localStorage.setItem('token', res.data.token)
      // 更新用户信息
      userStore.updateUserInfo({
        user_id: res.data.user_id,
        username: res.data.username
      })
      ElMessage.success('登录成功')
      const redirect = route.query.redirect as string
      router.push(redirect || '/user/home')
    } else {
      throw new Error(res.message || '登录失败1')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败2')
  } finally {
    loading.value = false
  }
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
    
    .footer-links {
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