<template>
  <app-layout>
    <div class="home-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>骑手首页</h2>
          </div>
        </template>
        
        <div class="welcome-section">
          <h3>欢迎回来，{{  riderStore.riderInfo?.username }}</h3>
        </div>
        
        <!-- <el-row :gutter="20" class="stats-section">
  <el-col :span="8">
    <el-card shadow="hover" class="stats-card">
      <template #header>
        <div class="stats-header">
          <el-icon><Document /></el-icon>
          <span>今日配送</span>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-value">{{ deliveryCount }}</div>
        <div class="stats-label">单</div>
      </div>
    </el-card>
  </el-col>

  <el-col :span="8">
    <el-card shadow="hover" class="stats-card">
      <template #header>
        <div class="stats-header">
          <el-icon><Document /></el-icon>
          <span>总订单</span>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-value">{{ totalOrderCount }}</div>
        <div class="stats-label">单</div>
      </div>
    </el-card>
  </el-col>
</el-row> -->
        

        <!-- 收入统计卡片 -->
        <!-- 收入统计卡片 -->
        <el-row :gutter="20" class="income-section" style="margin-top: 24px;">
  <el-col :span="8">
    <el-card shadow="hover" class="income-card">
      <template #header>
        <div class="stats-header">
          <el-icon><Document /></el-icon>
          <span>总收入</span>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-value">￥{{ incomeStats.total_earnings }}</div>
        <div class="stats-label">历史累计</div>
      </div>
    </el-card>
  </el-col>
  <el-col :span="8">
    <el-card shadow="hover" class="income-card">
      <template #header>
        <div class="stats-header">
          <el-icon><Document /></el-icon>
          <span>本月收入</span>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-value">￥{{ incomeStats.current_month }}</div>
        <div class="stats-label">本月</div>
      </div>
    </el-card>
  </el-col>
  <el-col :span="8">
    <el-card shadow="hover" class="income-card">
      <template #header>
        <div class="stats-header">
          <el-icon><Document /></el-icon>
          <span>已完成订单</span>
        </div>
      </template>
      <div class="stats-content">
        <div class="stats-value">{{ incomeStats.completed_orders }}</div>
        <div class="stats-label">单</div>
      </div>
    </el-card>
  </el-col>
</el-row>

        <el-button @click="goToProfile">个人资料</el-button>
      </el-card>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Star } from '@element-plus/icons-vue'
import { useRiderStore } from '@/store/rider'
import { ElMessage } from 'element-plus'
import { riderApi } from '@/api/rider'
import { adminApi } from '@/api/admin'
const router = useRouter()
const riderStore = useRiderStore()

const deliveryCount = ref(0)
const praiseCount = ref(0)
const totalOrderCount = ref(0)
const pendingOrders = ref<any[]>([])
const incomeStats = ref({
  total_earnings: 0,
  current_month: 0,
  completed_orders: 0
})

// 跳转订单详情
const viewOrder = () => {
  router.push(`/rider/orders`)
}

// 跳转个人资料
const goToProfile = () => {
  router.push('/rider/profile')
}

// 获取骑手信息
const getRiderInfo = async () => {
  try {
    const res = await riderApi.getInfo()
  } catch (e) {
    ElMessage.error('获取骑手信息失败')
  }
}

// 获取统计数据
const getStats = async () => {
  try {
    // 1. 获取总订单数
    const orderRes = await riderApi.getOrderHistory({
      page: 1,
      page_size: 10000 // 假设订单不会太多
    })
    if (orderRes.success) {
      const orders = orderRes.data.orders || []
      totalOrderCount.value = orders.length

      // 2. 今日配送单数（状态为3且created_at为今日）
      const today = new Date().toISOString().slice(0, 10)
      deliveryCount.value = orders.filter(
        (o: any) =>
          o.status === 3 &&
          o.created_at &&
          o.created_at.slice(0, 10) === today
      ).length
    }
  } catch (e) {
    console.log('获取统计数据失败:', e)
  }
}


// 获取收入统计
const getIncomeStats = async () => {
  try {
    const res = await riderApi.getIncomeStats()
    if (res.success) {
      incomeStats.value.total_earnings = res.data.total_earnings ?? 0
      incomeStats.value.current_month = res.data.current_month ?? 0
      incomeStats.value.completed_orders = res.data.completed_orders ?? 0
    }
  } catch (e) {
    console.log('获取收入统计失败:', e)
  }
}

//用户订单记录
/**
 * 订单状态
 * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
 */



onMounted(() => {

 // 1. 如果没有 riderInfo 但有 token，尝试获取骑手信息
  if (!riderStore.riderInfo && localStorage.getItem('rider_token')) {
    try {
      riderStore.getInfo()
    } catch (error) {
      console.error('获取骑手信息失败:', error)
      riderStore.clearToken()
      router.push('/rider/login')
      return
    }
  }

  // 2. 加载统计数据、待配送订单、收入统计
  try {
      Promise.all([
      getRiderInfo(),
      getStats(),
      getIncomeStats()
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>


<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.home-container {
  padding: $spacing-base;
  .card-header {
    h2 {
      margin: 0;
      color: $text-primary;
    }
  }
  .welcome-section {
    margin-bottom: $spacing-large;
    h3 {
      margin: 0 0 $spacing-small;
      color: $text-primary;
    }
    p {
      margin: 0;
      color: $text-secondary;
    }
  }
  .stats-section {
    margin-bottom: $spacing-large;
    .stats-card {
      .stats-header {
        display: flex;
        align-items: center;
        gap: $spacing-small;
        .el-icon {
          font-size: 20px;
          color: $primary-color;
        }
      }
      .stats-content {
        text-align: center;
        padding: $spacing-base 0;
        .stats-value {
          font-size: 24px;
          font-weight: bold;
          color: $text-primary;
          margin-bottom: $spacing-small;
        }
        .stats-label {
          color: $text-secondary;
        }
      }
    }
  }
  .recent-orders {
    h3 {
      margin: 0 0 $spacing-base;
      color: $text-primary;
    }
  }
  .stats-row {
    margin-bottom: $spacing-base;
  }
  .stats-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .stats-value {
      font-size: 24px;
      font-weight: bold;
      color: $primary-color;
      text-align: center;
      padding: $spacing-base 0;
    }
  }
}
</style>