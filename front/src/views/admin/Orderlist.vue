<template>
  <app-layout>
    <div class="admin-orderlist-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>订单列表</h2>
            <div class="filter-bar">
              <el-input
                v-model="searchUserId"
                placeholder="用户ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadOrders"
              />
              <el-input
                v-model="searchStoreId"
                placeholder="店铺ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadOrders"
              />
              <el-input
                v-model="searchRiderId"
                placeholder="骑手ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadOrders"
              />
              <el-select
                v-model="searchStatus"
                placeholder="订单状态"
                clearable
                style="width: 120px; margin-right: 12px"
              >
                <el-option label="全部" :value="undefined" />
                <el-option label="待接单" :value="0" />
                <el-option label="制作中" :value="1" />
                <el-option label="配送中" :value="2" />
                <el-option label="已完成" :value="3" />
                <el-option label="已取消" :value="4" />
              </el-select>
              <el-date-picker
                v-model="searchDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="margin-right: 12px"
                value-format="YYYY-MM-DDTHH:mm:ss"
                @change="loadOrders"
              />
              <el-button type="primary" @click="loadOrders">搜索</el-button>
            </div>
          </div>
        </template>
        <el-table :data="orderList" style="width: 100%" v-loading="loading">
          <el-table-column prop="order_id" label="订单号" width="120" />
          <el-table-column prop="user_id" label="用户ID" width="100" />
          <el-table-column prop="store_id" label="店铺ID" width="100" />
          <el-table-column prop="rider_id" label="骑手ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="total_price" label="金额" width="100" />
          <el-table-column prop="created_at" label="下单时间" width="180" />
          <el-table-column prop="deadline" label="截止时间" width="180" />
          <el-table-column prop="ended_at" label="完成时间" width="180" />
        </el-table>
        <div class="pagination-bar">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'

const orderList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchUserId = ref('')
const searchStoreId = ref('')
const searchRiderId = ref('')
const searchStatus = ref()
const searchDate = ref<[string, string] | null>(null)

const loadOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value
    }
    if (searchUserId.value) params.user_id = Number(searchUserId.value)
    if (searchStoreId.value) params.store_id = Number(searchStoreId.value)
    if (searchRiderId.value) params.rider_id = Number(searchRiderId.value)
    if (searchStatus.value !== undefined) params.status = searchStatus.value
    if (searchDate.value && searchDate.value.length === 2) {
      params.create_at = searchDate.value[0]
      params.end_at = searchDate.value[1]
    }
    const res = await adminApi.getOrderList(params)
    orderList.value = res.data?.orders || []
    total.value = orderList.value.length < pageSize.value
      ? (currentPage.value - 1) * pageSize.value + orderList.value.length
      : currentPage.value * pageSize.value + 1
  } catch (e: any) {
    ElMessage.error(e.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadOrders()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadOrders()
}

// 状态类型映射
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

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.admin-orderlist-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 0;

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    h2 {
      margin: 0;
      color: #333;
    }
    .filter-bar {
      display: flex;
      align-items: center;
    }
  }
  .pagination-bar {
    margin-top: 24px;
    text-align: right;
  }
}
</style>