import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import JoinPage from '@/views/info/JoinPage.vue';
import TechPage from '@/views/info/TechPage.vue';
import SocialPage from '@/views/info/SocialPage.vue';
import NutritionPage from '@/views/info/NutritionPage.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/views/Home.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/info/join',
      component: JoinPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/info/tech',
      component: TechPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/info/social',
      component: SocialPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/info/nutrition',
      component: NutritionPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/user/login',
      component: () => import('@/views/user/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/user/register',
      component: () => import('@/views/user/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/user/favorite',
      component: () => import('@/views/user/Favorite.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/rider/login',
      component: () => import('@/views/rider/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/rider/register',
      component: () => import('@/views/rider/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/merchant/login',
      component: () => import('@/views/merchant/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/merchant/register',
      component: () => import('@/views/merchant/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/merchant/home',
      component: () => import('@/views/merchant/Home.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/merchant/:id',
      component: () => import('@/views/user/StoreDetail.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/user/history',
      component: () => import('@/views/user/Orders.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/order/:id',
      component: () => import('@/views/user/Orders.vue'),
      meta: { requiresAuth: true }
    },
    // {
    //   path: '/user/order/:id/review',
    //   component: () => import('@/views/user/Review.vue'),
    //   meta: { requiresAuth: true }
    // },
    {
      path: '/user/home',
      component: () => import('@/views/user/Home.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/history-view',
      component: () => import('@/views/user/History.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/rider',
      component: () => import('@/layouts/RiderLayout.vue'),
      meta: { requiresAuth: true, role: 'rider' },
      children: [
        {
          path: 'home',
          name: 'RiderHome',
          component: () => import('@/views/rider/Home.vue'),
          meta: { title: '骑手首页' }
        },
        {
          path: 'orders',
          name: 'RiderOrders',
          component: () => import('@/views/rider/Orders.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: 'profile',
          name: 'RiderProfile',
          component: () => import('@/views/rider/Profile.vue'),
          meta: { title: '个人信息' }
        }
        
      ]
    },
    {
      path: '/merchant/products',
      component: () => import('@/views/merchant/Products.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/orders',
      component: () => import('@/views/merchant/Orders.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/info',
      component: () => import('@/views/merchant/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/storeSetting',
      component: () => import('@/views/merchant/storeSetting.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/promotions',
      component: () => import('@/views/merchant/promotions.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/overview',
      component: () => import('@/views/merchant/overview.vue'),
      meta: { requiresAuth: true }
    },
    // {
    //   path: '/merchant/settings',
    //   component: () => import('@/views/merchant/Settings.vue'),
    //   meta: { requiresAuth: true }
    // },
    {
      path: '/admin/login',
      component: () => import('@/views/admin/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/admin/dashboard',
      component: () => import('@/views/admin/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/userlist',
      component: () => import('@/views/admin/Userlist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/merchantlist',
      component: () => import('@/views/admin/Merchantlist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/riderlist',
      component: () => import('@/views/admin/Riderlist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/storelist',
      component: () => import('@/views/admin/Storelist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/orderlist',
      component: () => import('@/views/admin/Orderlist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/reviewlist',
      component: () => import('@/views/admin/Reviewlist.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/404.vue')
    }
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, left: 0 };
    }
  },
})

// 路由守卫
/*
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查路由是否需要认证
  if (to.meta.requiresAuth && !userStore.isLoggedIn.value) {
    // 如果需要认证但用户未登录，重定向到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})
*/

const goLoginToShop = () => {
  router.push('/merchant/login')
}

export default router 