<template>
  <div class="rider-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <span>骑手平台</span>
      </div>
      <div class="user-info">
        <el-dropdown>
          <span class="user-dropdown">
            {{ riderStore.riderInfo?.username }}
            <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/rider/profile')">
                个人信息
              </el-dropdown-item>
              <el-dropdown-item @click="handleLogout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 侧边菜单 -->
      <el-aside width="200px" class="aside">
        <el-menu
          :default-active="activeMenu"
          class="menu"
          router
        >
          <el-menu-item index="/rider/home">
            <el-icon><home-filled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/rider/orders">
            <el-icon><list /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/rider/profile">
            <el-icon><user /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed,onUnmounted, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useRiderStore } from '@/store/rider'
import { ElMessageBox } from 'element-plus'
import { ArrowDown, HomeFilled, List, User } from '@element-plus/icons-vue'
import { riderApi } from '@/api/rider'
import { ElNotification } from 'element-plus'
const router = useRouter()
const route = useRoute()
const riderStore = useRiderStore()

// 计算当前激活的菜单项
const activeMenu = computed(() => route.path)

let orderNotifyTimer: any = null

const autoGrabOrder = () => {
  orderNotifyTimer = setInterval(async () => {
    try {
        //riderApi.autoGrabOrder()
    } catch (e) {
      console.error('获取订单失败:', e)
    }
  }, 5000) // 每10秒轮询一次
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    riderApi.logout().then(() => {
      router.push('/rider/login')
      ElMessageBox.alert('已成功退出登录', '提示', {
        confirmButtonText: '确定'})
    }).catch(error => {
      ElMessageBox.alert(`退出登录失败: ${error.message}`, '错误', {
        confirmButtonText: '确定',
        type: 'error'
      })
      console.error('Logout error:', error)
    })
  }).then(() => {
    riderStore.clearRiderInfo()
    riderStore.clearToken()
  }).catch(() => {
    // 用户取消了退出登录
  })
}
onMounted(() => {
  // 启动订单通知轮询
  autoGrabOrder()
})
onUnmounted(() => {
  if (orderNotifyTimer) clearInterval(orderNotifyTimer)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.rider-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  .header {
    background-color: $primary-color;
    color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 $spacing-base;
    height: 60px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .logo {
      display: flex;
      align-items: center;
      gap: $spacing-small;

      span {
        font-size: $font-size-large;
        font-weight: bold;
      }
    }

    .user-info {
      .user-dropdown {
        display: flex;
        align-items: center;
        gap: $spacing-small;
        cursor: pointer;
        color: white;

        &:hover {
          opacity: 0.8;
        }
      }
    }
  }

  .main-container {
    flex: 1;
    display: flex;
    background-color: $background-color;

    .aside {
      background-color: white;
      border-right: 1px solid $border-color;

      .menu {
        height: 100%;
        border-right: none;
      }
    }

    .main {
      padding: $spacing-base;
      overflow-y: auto;
    }
  }
}
</style> 