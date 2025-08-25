<template>
  <div class="app-layout">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <div class="logo">
            <router-link to="/">饱了么</router-link>
          </div>
          
          <div class="nav-menu">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/orders" class="nav-item">订单</router-link>
          </div>
          
          <div class="user-menu">
            <template v-if="userStore.isLoggedIn">
              <el-dropdown trigger="click" @command="handleCommand">
                <span class="user-info">
                  <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                    {{ userStore.userInfo?.username?.[0] }}
                  </el-avatar>
                  <span class="nickname">{{ userStore.userInfo?.username }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <router-link to="/login" class="login-btn">登录</router-link>
              <router-link to="/register" class="register-btn">注册</router-link>
            </template>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <slot></slot>
      </el-main>
      
      <el-footer height="60px">
        <div class="footer-content">
          <p>© 2024 饱了么. All rights reserved.</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import IconTooling from '@/components/icons/IconTooling.vue'
import IconCommunity from '@/components/icons/IconCommunity.vue'
import AppLayout from '@/components/layout/AppLayout.vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}
</script>

<style scoped lang="scss">
.app-layout {
  min-height: 100vh;
  
  .el-container {
    min-height: 100vh;
  }
  
  .el-header {
    background-color: $white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    
    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .logo {
        font-size: $font-size-large;
        font-weight: bold;
        
        a {
          color: $primary-color;
        }
      }
      
      .nav-menu {
        display: flex;
        gap: $spacing-base;
        
        .nav-item {
          color: $text-primary;
          font-size: $font-size-base;
          
          &:hover,
          &.router-link-active {
            color: $primary-color;
          }
        }
      }
      
      .user-menu {
        display: flex;
        align-items: center;
        gap: $spacing-base;
        
        .user-info {
          display: flex;
          align-items: center;
          gap: $spacing-small;
          cursor: pointer;
          
          .nickname {
            color: $text-primary;
          }
        }
        
        .login-btn,
        .register-btn {
          padding: $spacing-small $spacing-base;
          border-radius: $border-radius-base;
          
          &.login-btn {
            color: $primary-color;
            
            &:hover {
              background-color: rgba($primary-color, 0.1);
            }
          }
          
          &.register-btn {
            background-color: $primary-color;
            color: $white;
            
            &:hover {
              background-color: $primary-color-light;
            }
          }
        }
      }
    }
  }
  
  .el-main {
    padding-top: 80px;
    background-color: $background-color-base;
  }
  
  .el-footer {
    background-color: $white;
    border-top: 1px solid $border-color-base;
    
    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      
      p {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }
  }
}

.icon {
  color: $primary-color;
}
</style> 