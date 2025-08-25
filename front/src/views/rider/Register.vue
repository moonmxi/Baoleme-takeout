<template>
  <div class="rider-register-container">
    <el-card class="register-card">
      <template #header>
        <h2>骑手注册</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onRegister" :loading="loading">注册</el-button>
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
import { riderApi } from '@/api/rider'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', phone: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const onRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await riderApi.register(form.value)
    if (res.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/rider/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
const goLogin = () => router.push('/rider/login')
</script>

<style scoped lang="scss">
.rider-register-container {
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
  }
}
</style>