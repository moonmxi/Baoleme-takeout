<template>
  <div class="landing-container">
    <!-- 顶部导航栏 -->
    <header class="landing-header">
      <div class="header-content">
        <div class="logo" @click="handleHomeClick" style="cursor:pointer;">
          <!-- 可以放置你的 Logo -->
          <img src="/logo.png" alt="Logo" class="logo-img">
          <span>饱了么外卖</span>
        </div>
        <nav class="nav-links">
          <router-link to="/" class="nav-item" @click.prevent="handleHomeClick">首页</router-link>
          <router-link to="/info/join" class="nav-item">入驻加盟</router-link>
          <router-link to="/info/tech" class="nav-item">技术合作中心</router-link>
          <router-link to="/info/social" class="nav-item">社会责任</router-link>
          <router-link to="/info/nutrition" class="nav-item">营养查询</router-link>
        </nav>
      </div>
    </header>

    <!-- 核心内容区域 (背景图/视频) -->
    <section class="hero-section" :style="{ backgroundImage: `url(${bgImg})`, backgroundSize: 'cover', backgroundPosition: 'center', backgroundRepeat: 'no-repeat' }">
      <div class="hero-content">
        <div class="hero-title-box">
          <h1>饱了么外卖</h1>
          <p>送啥都快</p>
        </div>
        
        <!-- 身份选择按钮 -->
        <div class="identity-buttons">
          <div class="identity-button primary" @click="goLoginToMerchant('merchant')">
             <el-icon><Shop /></el-icon>
            商家入驻
            <span>城市代理、服务市场</span>
          </div>
          <div class="identity-button accent" @click="goLoginToRider('rider')">
            <el-icon><Bicycle /></el-icon>
            骑手入职
            <span>全城配送、轻松赚钱</span>
          </div>
           <div class="identity-button warning" @click="goLoginToUser('user')">
            <el-icon><User /></el-icon>
            用户点餐
            <span>快捷下单、一键点餐</span>
          </div>
        </div>

      </div>
      <!-- 可以放置背景图或视频 -->
      <!-- <img src="/path/to/your/background.jpg" alt="background" class="hero-background"> -->
    </section>

    <!-- 网站介绍区域 -->
    <section class="introduction-section" ref="introSection">
      <div class="section-content">
        <h2 :class="{ 'fade-in': isIntroVisible }">关于饱了么</h2>
        <p :class="{ 'fade-in': isIntroVisible }">饱了么外卖平台致力于为用户提供便捷、快速、安全的外卖服务。我们拥有专业的配送团队、严格的商家审核机制，以及完善的用户服务体系，让您足不出户就能享受美食。</p>
        <div class="intro-image" :class="{ 'slide-in': isIntroVisible }">
          <img src="@/assets/images/intro.png" alt="平台介绍">
        </div>
      </div>
    </section>

    <!-- 服务介绍区域 -->
    <section class="services-section" ref="servicesSection">
      <div class="section-content">
        <h2 :class="{ 'fade-in': isServicesVisible }">我们的服务</h2>
        <p :class="{ 'fade-in': isServicesVisible }">从商家入驻到用户点餐，从骑手配送到售后服务，我们提供全方位的解决方案</p>
        <div class="services-grid" :class="{ 'slide-in': isServicesVisible }">
          <div class="service-item">
            <img src="@/assets/images/delivery.png" alt="配送服务">
            <h3>快速配送</h3>
            <p>30分钟内送达，让美食更快到达</p>
          </div>
          <div class="service-item">
            <img src="@/assets/images/merchant.png" alt="商家服务">
            <h3>商家入驻</h3>
            <p>简单快捷的入驻流程，助力商家成长</p>
          </div>
          <div class="service-item">
            <img src="@/assets/images/user.png" alt="用户服务">
            <h3>用户服务</h3>
            <p>优质的用户体验，让点餐更轻松</p>
          </div>
        </div>
      </div>
    </section>

     <!-- 底部信息 -->
    <footer class="landing-footer">
       <p>© 2024 饱了么. All rights reserved.</p>
         <div class="admin-entry" @click="goLoginToAdmin">
  <el-icon><UserFilled /></el-icon>
  <span>管理员入口</span>
</div>
    </footer>

  </div>


</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Bicycle, Shop } from '@element-plus/icons-vue'
import { UserFilled } from '@element-plus/icons-vue'
import bgImg from '@/assets/images/background.jpg'
// 如果需要滚动动画，可能需要引入相关库
// import AOS from 'aos';
// import 'aos/dist/aos.css';

const router = useRouter()
const route = useRoute()
const introSection = ref<HTMLElement | null>(null)
const servicesSection = ref<HTMLElement | null>(null)
const isIntroVisible = ref(false)
const isServicesVisible = ref(false)

// 创建 Intersection Observer
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.target === introSection.value) {
      isIntroVisible.value = entry.isIntersecting
    } else if (entry.target === servicesSection.value) {
      isServicesVisible.value = entry.isIntersecting
    }
  })
}, {
  threshold: 0.1,
  rootMargin: '-50px 0px'
})

onMounted(() => {
  if (introSection.value) {
    observer.observe(introSection.value)
  }
  if (servicesSection.value) {
    observer.observe(servicesSection.value)
  }
})

onUnmounted(() => {
  observer.disconnect()
})

const goLoginToUser = (role: string) => {
  router.push({ path: '/user/login', query: { role } })
}
const goLoginToRider = (role: string) => {
  router.push({ path: '/rider/login', query: { role } })
}
const goLoginToMerchant = (role: string) => {
  // 根据选择的角色跳转到对应的登录页或主页
  // 目前跳转到带 role 参数的登录页
  router.push({ path: '/merchant/login', query: { role } })
  
  // 如果用户直接点"我要点外卖"，可能直接跳转到用户首页并保持登录状态
  // if (role === 'user') {
  //   router.push('/user/home'); // 假设用户首页是 /user/home
  // } else {
  //   router.push({ path: '/login', query: { role } }); // 其他角色跳转登录页
  // }
}

const handleHomeClick = () => {
  if (route.path !== '/') {
    router.push('/').then(() => {
      nextTick(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' })
      })
    })
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// const scrollToTop = () => {
//   window.scrollTo({ top: 0, behavior: 'smooth' })
// }

// 如果需要滚动动画库的初始化
// onMounted(() => {
//   AOS.init();
// });

// 如果需要手动触发动画更新（在动态内容加载后）
// onUpdated(() => {
//   AOS.refresh();
// });
// 在 <script setup lang="ts"> 中补充
const goLoginToAdmin = () => {
  router.push('/admin/login')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.home-bg {
  min-height: 100vh;
  min-width: 100vw;
  background: linear-gradient(135deg, #f7faff 0%, #e3f0ff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.banner {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 48px 0 24px 0;
  justify-content: center;
  .logo {
    height: 56px;
    margin-right: 20px;
  }
  .banner-title {
    font-size: 2.6rem;
    font-weight: bold;
    color: #409eff;
    letter-spacing: 2px;
  }
}

.slide-up-fade-enter-active {
  animation: slide-up-fade-in 0.8s cubic-bezier(0.23, 1, 0.32, 1);
}
@keyframes slide-up-fade-in {
  0% {
    opacity: 0;
    transform: translateY(60px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.landing-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  width: 100%;
  justify-content: center;
  // background-color: $background-color-base; // 如果需要统一背景色
}

.landing-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px; // 根据需要调整高度
  background-color: rgba(255, 255, 255, 0.8); // 半透明背景
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;

  .header-content {
    max-width: 1200px; // 限制内容最大宽度
    width: 100%;
    padding: 0 $spacing-base; // 内边距
    display: flex;
    align-items: center;
    justify-content: space-between;

    .logo {
      display: flex;
      align-items: center;
      font-size: $font-size-large;
      font-weight: bold;
      color: $primary-color;
      
      .logo-img {
        height: 30px; // 根据需要调整
        margin-right: $spacing-small;
      }
    }

    .nav-links {
      display: flex;
      gap: $spacing-base; // 导航链接间距

      .nav-item {
        color: $text-primary;
        font-size: $font-size-base;
        text-decoration: none;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }
}

.hero-section {
  position: relative;
  width: 100%;
  height: 150vh; // 调整为90vh
  display: flex;
  justify-content: center;
  align-items: center;
  color: $text-primary;
  text-align: center;
  overflow: hidden;
  background: linear-gradient(135deg, #f7faff 0%, #e3f0ff 100%);
  margin-top: 60px; // 保留顶部空隙给导航栏

  .hero-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    position: relative;
    z-index: 1;
  }
}

.hero-title-box {
  background: none;
  box-shadow: none;
  border-radius: 0;
  padding: 0;
  margin-bottom: 32px;
  text-align: center;
  display: inline-block;
  min-width: 0;
  max-width: 90vw;
}

.hero-title-box h1 {
  color: #fff;
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 12px;
  letter-spacing: 2px;
  text-shadow: 0 4px 16px rgba(0,0,0,0.28), 0 1px 0 #333;
}

.hero-title-box p {
  color: #fff;
  font-size: 24px;
  margin: 0;
  font-weight: 500;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.24), 0 1px 0 #333;
}

.identity-buttons {
    display: flex;
    gap: $spacing-large; // 按钮间距
    margin-top: $spacing-large; // 与上方内容的间距
    
    .identity-button {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 200px; // 按钮宽度
      height: 120px; // 按钮高度
      border-radius: $border-radius-large; // 大圆角
      cursor: pointer;
      color: $white;
      font-size: $font-size-large;
      font-weight: bold;
      transition: transform 0.2s, box-shadow 0.2s; // 添加动画效果
      padding: $spacing-base;
      
      .el-icon {
        font-size: 36px; // 图标大小
        margin-bottom: $spacing-small; // 图标与文字间距
      }
      
      span {
         font-size: $font-size-small; // 副标题文字大小
         font-weight: normal;
         margin-top: $spacing-small; // 副标题与主标题间距
      }

      &:hover {
        transform: translateY(-5px); // 鼠标悬停上移效果
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2); // 鼠标悬停阴影效果
      }

      &.primary {
        background-color: #409EFF; // Element Plus Primary 颜色
      }
      &.accent {
         background-color: #F56C6C; // Element Plus Danger 颜色 (示例，可调整)
      }
      &.warning {
         background-color: #E6A23C; // Element Plus Warning 颜色
      }
    }
}

.introduction-section,
.services-section {
  padding: 80px 0;
  background-color: #fff;
  position: relative;
  overflow: hidden;
  
  .section-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    
    h2 {
      font-size: 36px;
      color: #333;
      margin-bottom: 20px;
      text-align: center;
      opacity: 0;
      transform: translateY(30px);
      transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
      
      &.fade-in {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    p {
      font-size: 18px;
      color: #666;
      line-height: 1.6;
      margin-bottom: 40px;
      text-align: center;
      opacity: 0;
      transform: translateY(30px);
      transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.2s;
      
      &.fade-in {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }
}

.intro-image {
  text-align: center;
  opacity: 0;
  transform: translateX(-50px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.4s;
  
  &.slide-in {
    opacity: 1;
    transform: translateX(0);
  }
  
  img {
    max-width: 100%;
    max-height: 400px;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    object-fit: contain;
  }
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
  
  .service-item {
    opacity: 0;
    transform: translateX(-50px);
    transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:nth-child(1) {
      transition-delay: 0.2s;
    }
    
    &:nth-child(2) {
      transition-delay: 0.4s;
    }
    
    &:nth-child(3) {
      transition-delay: 0.6s;
    }
  }
  
  &.slide-in {
    .service-item {
      opacity: 1;
      transform: translateX(0);
    }
  }
}

.service-item {
  text-align: center;
  padding: 2rem;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  img {
    width: 120px;
    height: 120px;
    margin-bottom: 1.5rem;
    object-fit: contain;
  }
  
  h3 {
    color: #333;
    margin: 1rem 0;
    font-size: 1.2rem;
  }
  
  p {
    color: #666;
    font-size: 0.9rem;
    line-height: 1.5;
  }
}

.landing-footer {
  position: relative;
  padding: $spacing-base 0;
  text-align: center;
  color: $text-secondary;
  font-size: $font-size-small;
  border-top: 1px solid $border-color;

  .admin-entry {
    display: inline-flex;
    align-items: center;
    position: absolute;
    right: 32px;
    top: 50%;
    transform: translateY(-50%);
    background: #606266;
    color: #fff;
    border-radius: 16px;
    padding: 4px 10px;
    font-size: 13px;
    cursor: pointer;
    opacity: 0.6;
    transition: opacity 0.2s, background 0.2s;

    .el-icon {
      font-size: 16px;
      margin-right: 4px;
    }

    &:hover {
      opacity: 1;
      background: #409EFF;
    }
  }
}

.intro-icon {
  font-size: 120px;
  color: var(--el-color-primary);
  margin: 2rem 0;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.1);
  }
}

.service-icon {
  font-size: 48px;
  color: var(--el-color-primary);
  margin-bottom: 1rem;
  transition: transform 0.3s ease;
}

.service-item {
  text-align: center;
  padding: 2rem;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    
    .service-icon {
      transform: scale(1.1);
    }
  }
  
  h3 {
    color: #333;
    margin: 1rem 0;
    font-size: 1.2rem;
  }
  
  p {
    color: #666;
    font-size: 0.9rem;
    line-height: 1.5;
  }
}

@media (max-width: 768px) {
  .services-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .hero-section {
    height: 60vh;
  }
  
  .introduction-section,
  .services-section {
    padding: 40px 0;
  }
  
  .service-item img {
    width: 100px;
    height: 100px;
  }
}

</style>