<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <router-link to="/">饱了么</router-link>
      </div>
      <div class="nav-menu">
        <router-link to="/user/home" class="nav-item">首页</router-link>
        <router-link to="/user/orders" class="nav-item">订单</router-link>
        <router-link to="/user/favorite" class="nav-item">收藏</router-link>
        <router-link to="/user/history-view" class="nav-item">浏览历史</router-link>
      </div>
      <div class="user-info">
        <el-dropdown>
          <span class="user-dropdown">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar ? `http://localhost:8080/images/${userStore.userInfo.avatar}` : defaultAvatar" />
            <span class="nickname">{{ userStore.userInfo?.username }}</span>
            <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/user/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体内容区域 -->
    <el-main class="main">
      <slot />
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { ref } from 'vue'

const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = '/logo.png'

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
    userStore.logout()
    router.push('/user/login')
  }).catch(() => {
    // 用户取消
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.user-layout {
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
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);

    .logo {
      font-size: 22px;
      font-weight: bold;
      a {
        color: white;
        text-decoration: none;
      }
    }

    .nav-menu {
      display: flex;
      gap: 32px;
      .nav-item {
        color: white;
        font-size: 16px;
        text-decoration: none;
        &:hover {
          color: $warning-color;
        }
      }
    }

    .user-info {
      .user-dropdown {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        color: white;
        .nickname {
          font-weight: 500;
        }
        &:hover {
          opacity: 0.8;
        }
      }
    }
  }

  .main {
    flex: 1;
    background: $background-color;
    padding: $spacing-large 0;
    min-height: 80vh;
  }
}
</style>