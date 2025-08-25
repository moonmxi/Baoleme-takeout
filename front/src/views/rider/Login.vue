<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>骑手登录</h2>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @keyup.enter="handleLogin"
        class="login-form"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            maxlength="11"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="submit-btn"
            @click="handleLogin"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="form-footer">
          <router-link to="/rider/register">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRiderStore } from '@/store/rider'
import { riderApi } from '@/api/rider'

const router = useRouter()
const route = useRoute()
const riderStore = useRiderStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = ref({
  phone: '',
  password: ''
})

const rules: FormRules = {
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
    // 调用骑手登录接口
    console.log('登录信息:', form.value)
    const res = await riderApi.login({
      phone: form.value.phone,
      password: form.value.password
    })
    console.log('登录结果', res)
    if (res.success ) {
      riderStore.setToken(res.data.token)
      ElMessage.success('登录成功')
      console.log('登录成功，token:', res.data.token)
      // 支持重定向
      const redirect = route.query.redirect as string
      router.push(redirect || '/rider/home')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '登录失败')
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