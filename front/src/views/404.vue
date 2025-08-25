<template>
  <app-layout>
    <div class="notfound-container">
      <h1>404</h1>
      <p>页面未找到</p>
      <p>将在 {{ countdown }} 秒后自动返回首页</p>
      <el-button type="primary" @click="goHome">立即返回首页</el-button>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const countdown = ref(5)
const router = useRouter()
const route = useRoute()
let timer: ReturnType<typeof setInterval> | null = null

const goHome = () => {
  router.push('/')
}

onMounted(() => {
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer!)
      goHome()
    }
  }, 1000)
})

// 监听路由变化，用户跳转时清除定时器
watch(
  () => route.fullPath,
  () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }
)

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped lang="scss">
.notfound-container {
  text-align: center;
  padding: 100px 0;
  h1 {
    font-size: 80px;
    color: #409EFF;
    margin-bottom: 24px;
  }
  p {
    font-size: 20px;
    color: #666;
    margin-bottom: 32px;
  }
}
</style>