<template>
  <div class="orders-container">
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <el-button-group>
        <el-button  
          :type="activeTab === 'available' ? 'primary' : 'default'"
          @click="activeTab = 'available'"
        >
          可抢订单
        </el-button>
        <el-button 
          :type="activeTab === 'current' ? 'primary' : 'default'"
          @click="activeTab = 'current'"
        >
          当前订单
        </el-button>
        <el-button 
          :type="activeTab === 'history' ? 'primary' : 'default'"
          @click="activeTab = 'history'"
        >
          历史订单
        </el-button>
      </el-button-group>
    </div>

    <!-- 可抢订单列表 -->
    <div v-if="activeTab === 'available'" class="order-list">
      <el-table
        v-loading="loading"
        :data="availableOrders"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="delivery_price" label="配送费" width="120" />
        <el-table-column prop="store_location" label="店铺位置" width="120"/>
        <el-table-column prop="user_location" label="用户位置" width="120"/>
        <el-table-column prop="created_at" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link 
              @click="handleGrabOrder(row)"
            >
              抢单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
    <el-pagination
      v-model:current-page="currentPageCurrent"
      v-model:page-size="pageSizeCurrent"
      :total="totalCurrent"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      @size-change="handlePageSizeChange"
      @current-change="handleCurrentPageChange"
    />
  </div>
    </div>

    <!-- 当前订单列表 -->
    <div v-if="activeTab === 'current'" class="order-list">
  <el-table
    v-loading="loading"
    :data="currentOrdersPaged"
    border
    style="width: 100%"
  >
    <el-table-column prop="order_id" label="订单号" width="120" />
    <el-table-column prop="delivery_price" label="配送费" width="120">
  <template #default="{ row }">
    ¥{{ row.delivery_price?.toFixed(2) }}
  </template>
</el-table-column>
        <el-table-column prop="store_location" label="店铺位置" width="120"/>
        <el-table-column prop="user_location" label="用户位置" width="120"/>
    <el-table-column prop="status" label="状态" width="120">
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="created_at" label="创建时间" width="180">
      <template #default="{ row }">
        {{ formatDate(row.created_at) }}
      </template>
    </el-table-column>
    <el-table-column label="操作" width="200" fixed="right">
      <template #default="{ row }">
        <el-button-group>
          <el-button 
            v-if="row.status === 1"
            type="primary" 
            link 
            @click="handleUpdateStatus(row, 2)"
          >
            取餐
          </el-button>
          <el-button 
            v-if="row.status === 2"
            type="success" 
            link 
            @click="handleUpdateStatus(row, 3)"
          >
            送达
          </el-button>
          <el-button 
            v-if="row.status === 1"
            type="danger" 
            link 
            @click="handleCancel(row)"
          >
            取消
          </el-button>
        </el-button-group>
      </template>
    </el-table-column>
  </el-table>
  <!-- 分页 -->
  <div class="pagination">
    <el-pagination
      v-model:current-page="currentPageCurrent"
      v-model:page-size="pageSizeCurrent"
      :total="totalCurrent"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      @size-change="handlePageSizeChange"
      @current-change="handleCurrentPageChange"
    />
  </div>
</div>

    <!-- 历史订单列表 -->
<div v-if="activeTab === 'history'" class="order-list">
  <!-- 搜索表单 -->
  <el-form :inline="true" :model="searchForm" class="search-form">
    <el-form-item label="订单状态" style="min-width: 180px;">
      <el-select
  v-model="searchForm.status"
  :placeholder="searchForm.status === '' ? '全部' : '请选择状态'"
  clearable
  style="width: 180px;"
>
        <el-option label="全部" :value="''" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="0" />
      </el-select>
    </el-form-item>
    <el-form-item label="起始时间">
      <el-date-picker
        v-model="searchForm.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px;"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </el-form-item>
  </el-form>

  <el-table
    v-loading="loading"
    :data="historyOrders"
    border
    style="width: 100%"
  >
    <el-table-column prop="order_id" label="订单号" width="120" />
    <el-table-column prop="status" label="状态" width="120">
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
        <el-table-column prop="store_location" label="店铺位置" width="120"/>
        <el-table-column prop="user_location" label="用户位置" width="120"/>
    <el-table-column prop="delivery_price" label="配送费" width="120">
      <template #default="{ row }">
        ¥{{ row.delivery_price?.toFixed(2) }}
      </template>
    </el-table-column>
    <el-table-column prop="created_at" label="下单时间" width="180">
      <template #default="{ row }">
        {{ formatDate(row.created_at) }}
      </template>
    </el-table-column>
    <el-table-column prop="completed_at" label="完成时间" width="180">
      <template #default="{ row }">
        {{ row.ended_at ? formatDate(row.ended_at) : '-' }}
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页 -->
  <div class="pagination">
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
</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { riderApi } from '@/api/rider'
import { formatDate } from '@/utils/format'

// 数据列表
const loading = ref(false)
const activeTab = ref('available')
const availableOrders = ref<any[]>([])
const currentOrders = ref<any[]>([])
const historyOrders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const currentOrdersPaged = ref<any[]>([])
const currentPageCurrent = ref(1)
const pageSizeCurrent = ref(10)
const totalCurrent = ref(0)


// 搜索表单
const searchForm = ref({
  status: undefined,
  dateRange: []
})

// 获取可抢订单列表
const getAvailableOrders = async () => {
  loading.value = true
  try {
    console.log('获取可抢订单列表')
    const response = await riderApi.getAvailableOrders({
      page: currentPage.value,
      page_size: pageSize.value
    })
    console.log('可抢订单列表响应:', response)
    if (response.success) {
      availableOrders.value = response.data.orders
    } else {
      ElMessage.error(response.message || '获取可抢订单失败')
    }
  } catch (error) {
    console.error('获取可抢订单失败:', error)
  } finally {
    loading.value = false
  }
}

const getCurrentOrders = async () => {
  loading.value = true
  try {
    // 分别请求 status=1 和 status=2 的当前页
    const [res1, res2] = await Promise.all([
      riderApi.getOrderHistory({ status: 1, page: currentPageCurrent.value, page_size: pageSizeCurrent.value }),
      riderApi.getOrderHistory({ status: 2, page: currentPageCurrent.value, page_size: pageSizeCurrent.value })
    ])
    let orders: any[] = []
    if (res1.success && res1.data?.orders) {
      orders = orders.concat(res1.data.orders)
    }
    if (res2.success && res2.data?.orders) {
      orders = orders.concat(res2.data.orders)
    }
    // 按创建时间倒序
    orders.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    currentOrdersPaged.value = orders
    totalCurrent.value = orders.length // 用合并后的订单数作为总数
  } catch (error) {
    console.error('获取当前订单失败:', error)
    ElMessage.error('获取当前订单失败')
  } finally {
    loading.value = false
  }
}

// 分页事件
const handleCurrentPageChange = (val: number) => {
  currentPageCurrent.value = val
  getCurrentOrders()
}
const handlePageSizeChange = (val: number) => {
  pageSizeCurrent.value = val
  currentPageCurrent.value = 1
  getCurrentOrders()
}

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'current') {
    getCurrentOrders()
  }
})
// 获取历史订单列表
const getHistoryOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value,
      status: searchForm.value.status,
    }
    if (searchForm.value.dateRange?.[0]) {
      params.start_time = `${searchForm.value.dateRange[0]}T00:00:00`
    }
    if (searchForm.value.dateRange?.[1]) {
      params.end_time = `${searchForm.value.dateRange[1]}T23:59:59`
    }
    const response = await riderApi.getOrderHistory(params)
    if (response.success) {
      historyOrders.value = response.data.orders
      total.value = response.data.orders?.length < pageSize.value && currentPage.value === 1
        ? response.data.orders.length
        : response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取历史订单失败')
    }
  } catch (error) {
    console.error('获取历史订单失败:', error)
    ElMessage.error('获取历史订单失败')
  } finally {
    loading.value = false
  }
}

// 抢单
const handleGrabOrder = async (row: any) => {
  try {
    const response = await riderApi.grabOrder(row.id)
    if (response.success) {
      ElMessage.success('抢单成功')
      getAvailableOrders()
    } else {
      ElMessage.error(response.message || '抢单失败')
    }
  } catch (error) {
    console.error('抢单失败:', error)
    ElMessage.error('抢单失败')
  }
}

// 更新订单状态
const handleUpdateStatus = async (row: any, targetStatus: number) => {
  try {
    const response = await riderApi.updateOrderStatus(row.order_id, targetStatus)
    if (response.success) {
      ElMessage.success('状态更新成功')
      getCurrentOrders()
    } else {
      ElMessage.error(response.message || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 取消订单
const handleCancel = (row: any) => {
  ElMessageBox.confirm(
    '确定要取消该订单吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      console.log('取消订单:', row)
      const response = await riderApi.cancelOrder(row.order_id)
      console.log('取消订单响应:', response)
      if (response.success) {
        ElMessage.success('订单已取消')
        getCurrentOrders()
      } else {
        ElMessage.error(response.message || '取消订单失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  })
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getHistoryOrders()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getHistoryOrders()
}

// 获取状态类型
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'info',    // 已取消
    1: 'warning', // 待取餐
    2: 'primary', // 取餐中
    3: 'success'  // 已完成
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '已取消',
    1: '待取餐',
    2: '取餐中',
    3: '已完成'
  }
  return statusMap[status] || '未知状态'
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    status: undefined,
    dateRange: []
  }
  getHistoryOrders()
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  getHistoryOrders()
}

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'available') {
    getAvailableOrders()
  } else if (newTab === 'current') {
    getCurrentOrders()
  } else if (newTab === 'history') {
    getHistoryOrders()
  }
})

onMounted(() => {
  getAvailableOrders()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.orders-container {
  padding: $spacing-base;

  .action-bar {
    margin-bottom: $spacing-base;
  }

  .order-list {
    .pagination {
      margin-top: $spacing-base;
      display: flex;
      justify-content: flex-end;
    }
  }

  .search-form {
    margin-bottom: $spacing-base;
    padding: $spacing-base;
    background-color: white;
    border-radius: $border-radius-base;
  }
}
</style>