<template>
  <app-layout>
    <div class="admin-dashboard-container">
      <div style="text-align: right; margin-bottom: 16px;">
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
      <!-- 快速入口 -->
      <el-card class="quick-nav-card" shadow="hover" style="margin-bottom: 32px;">
        <div class="quick-nav">
          <el-button type="primary" @click="goToUserList" :icon="User">用户管理</el-button>
          <el-button type="primary" @click="goToShopList" :icon="Shop">商家管理</el-button>
          <el-button type="primary" @click="goToStoreList" :icon="OfficeBuilding">店铺管理</el-button>
          <el-button type="primary" @click="goToRiderList" :icon="Bicycle">骑手管理</el-button>
          <el-button type="primary" @click="goToOrderList" :icon="Document">订单管理</el-button>
          <el-button type="primary" @click="goToReviewList" :icon="Star">评价管理</el-button>
        </div>
      </el-card>

      <!-- 平台数据总览 -->
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h2>平台数据总览</h2>
          </div>
        </template>
        <el-row :gutter="24" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card" shadow="hover">
              <div class="stats-title">
                <el-icon><User /></el-icon>
                用户数
              </div>
              <div class="stats-value">{{ stats.userCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card" shadow="hover">
              <div class="stats-title">
                <el-icon><Shop /></el-icon>
                商家数
              </div>
              <div class="stats-value">{{ stats.merchantCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card" shadow="hover">
              <div class="stats-title">
                <el-icon><Bicycle /></el-icon>
                骑手数
              </div>
              <div class="stats-value">{{ stats.riderCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card" shadow="hover">
              <div class="stats-title">
                <el-icon><Document /></el-icon>
                订单数
              </div>
              <div class="stats-value">{{ stats.orderCount }}</div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>

      <!-- 最新用户 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新用户</h3>
            <el-button type="primary" link @click="goToUserList">全部用户</el-button>
          </div>
        </template>
        <el-table :data="userList" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="created_at" label="注册时间" />
        </el-table>
      </el-card>

      <!-- 最新商家 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新商家</h3>
            <el-button type="primary" link @click="goToShopList">全部商家</el-button>
          </div>
        </template>
        <el-table :data="shopList" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="created_at" label="注册时间" />
        </el-table>
      </el-card>

      <!-- 最新骑手 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新骑手</h3>
            <el-button type="primary" link @click="goToRiderList">全部骑手</el-button>
          </div>
        </template>
        <el-table :data="riderList" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="created_at" label="注册时间" />
        </el-table>
      </el-card>

      <!-- 最新订单 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新订单</h3>
            <el-button type="primary" link @click="goToOrderList">全部订单</el-button>
          </div>
        </template>
        <el-table :data="orderList" style="width: 100%">
          <el-table-column prop="order_id" label="订单号" width="120" />
          <el-table-column prop="user_id" label="用户ID" width="100" />
          <el-table-column prop="store_id" label="店铺ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="total_price" label="金额" width="100" />
          <el-table-column prop="created_at" label="下单时间" width="180" />
        </el-table>
      </el-card>

      <!-- 最新店铺 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新店铺</h3>
            <el-button type="primary" link @click="goToStoreList">全部店铺</el-button>
          </div>
        </template>
        <el-table :data="storeList" style="width: 100%">
          <el-table-column prop="id" label="店铺ID" width="80" />
          <el-table-column prop="name" label="店铺名称" />
          <el-table-column prop="description" label="简介" />
          <el-table-column prop="location" label="地址" />
          <el-table-column prop="created_at" label="创建时间" />
        </el-table>
      </el-card>

      <!-- 最新评论 -->
      <el-card class="list-card" shadow="hover" style="margin-top: 32px;">
        <template #header>
          <div class="card-header">
            <h3>最新评论</h3>
            <el-button type="primary" link @click="goToReviewList">全部评论</el-button>
          </div>
        </template>
        <el-table :data="reviewList" style="width: 100%">
          <el-table-column prop="id" label="评价ID" width="80" />
          <el-table-column prop="user_id" label="用户ID" width="100" />
          <el-table-column prop="store_id" label="店铺ID" width="100" />
          <el-table-column prop="product_id" label="商品ID" width="100" />
          <el-table-column prop="rating" label="评分" width="80" />
          <el-table-column prop="comment" label="评价内容" />
          <el-table-column prop="created_at" label="评价时间" width="180" />
        </el-table>
      </el-card>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Shop, Bicycle, Document, Star, OfficeBuilding } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const router = useRouter()

const stats = ref({
  userCount: 0,
  merchantCount: 0,
  riderCount: 0,
  orderCount: 0
})

const userList = ref<any[]>([])
const shopList = ref<any[]>([])
const riderList = ref<any[]>([])
const orderList = ref<any[]>([])
const storeList = ref<any[]>([])
const reviewList = ref<any[]>([])

const fetchStats = async () => {
  try {
    const [userRes, merchantRes, riderRes, orderRes] = await Promise.all([
      adminApi.getUserList({ page: 1, page_size: 10000 }),
      adminApi.getMerchantList({ page: 1, page_size: 10000 }),
      adminApi.getRiderList({ page: 1, page_size: 10000 }),
      adminApi.getOrderList({ page: 1, page_size: 10000 })
    ])
    stats.value.userCount = userRes.data?.users?.length || 0
    stats.value.merchantCount = merchantRes.data?.merchants?.length || 0
    stats.value.riderCount = riderRes.data?.riders?.length || 0
    stats.value.orderCount = orderRes.data?.orders?.length || 0
  } catch (e: any) {
    ElMessage.error(e.message || '统计数据获取失败')
  }
}

const fetchUserList = async () => {
  try {
    // 拉取较多用户，前端按注册时间倒序取前5个
    const res = await adminApi.getUserList({ page: 1, page_size: 2000 })
    let users = res.data?.users || []
    users = users.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    userList.value = users.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '用户列表获取失败')
  }
}

// 最新商家
const fetchShopList = async () => {
  try {
    const res = await adminApi.getMerchantList({ page: 1, page_size: 1000 })
    let merchants = res.data?.merchants || []
    merchants = merchants.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    shopList.value = merchants.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '商家列表获取失败')
  }
}

// 最新骑手
const fetchRiderList = async () => {
  try {
    const res = await adminApi.getRiderList({ page: 1, page_size: 1000 })
    let riders = res.data?.riders || []
    riders = riders.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    riderList.value = riders.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '骑手列表获取失败')
  }
}

// 最新订单
const fetchOrderList = async () => {
  try {
    const res = await adminApi.getOrderList({ page: 1, page_size: 1000 })
    let orders = res.data?.orders || []
    orders = orders.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    orderList.value = orders.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '订单列表获取失败')
  }
}

// 最新店铺
const fetchStoreList = async () => {
  try {
    const res = await adminApi.getStoreList({ page: 1, page_size: 1000 })
    let stores = res.data?.stores || []
    stores = stores.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    storeList.value = stores.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '店铺列表获取失败')
  }
}

// 最新评论
const fetchReviewList = async () => {
  try {
    const res = await adminApi.getReviewList({ page: 1, page_size: 1000 })
    let reviews = res.data?.reviews || []
    reviews = reviews.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    reviewList.value = reviews.slice(0, 5)
  } catch (e: any) {
    ElMessage.error(e.message || '评论列表获取失败')
  }
}

// 状态类型映射（如需订单状态展示）
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning', // 待接单
    1: 'info',    // 制作中
    2: 'primary', // 配送中
    3: 'success', // 已完成
    4: 'danger'   // 已取消
  }
  return map[status] || 'default'
}
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待接单',
    1: '制作中',
    2: '配送中',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

// 跳转入口
const goToUserList = () => {
  router.push('/admin/userlist')
}
const goToShopList = () => {
  router.push('/admin/merchantlist')
}
const goToStoreList = () => {
  router.push('/admin/storelist')
}
const goToRiderList = () => {
  router.push('/admin/riderlist')
}
const goToOrderList = () => {
  router.push('/admin/orderlist')
}
const goToReviewList = () => {
  router.push('/admin/reviewlist')
}

const handleLogout = async () => {
  try {
    await adminApi.logout()
  } catch (e) {
    // 即使接口报错也继续清理
    console.error('退出登录失败:', e)
    localStorage.removeItem('admin_token')
    ElMessage.success('已退出登录')
    router.push('/admin/login')
  }
}

onMounted(() => {
  fetchStats()
  fetchUserList()
  fetchShopList()
  fetchRiderList()
  fetchOrderList()
  fetchStoreList()
  fetchReviewList()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.admin-dashboard-container {
  padding: $spacing-large 0;
  max-width: 1200px;
  margin: 0 auto;
}

.quick-nav-card {
  .quick-nav {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    justify-content: flex-start;
    align-items: center;
    padding: 8px 0;
    .el-button {
      min-width: 120px;
      font-size: 16px;
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}

.dashboard-card {
  margin-bottom: $spacing-large;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  h2, h3 {
    margin: 0;
    color: $text-primary;
    font-weight: bold;
  }
}

.stats-row {
  margin-top: $spacing-base;
}

.stats-card {
  text-align: center;
  padding: $spacing-large 0;
  .stats-title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-small;
    font-size: $font-size-large;
    color: $text-secondary;
    margin-bottom: $spacing-small;
  }
  .stats-value {
    font-size: 32px;
    font-weight: bold;
    color: $primary-color;
  }
}

.list-card {
  margin-bottom: $spacing-large;
}
</style>