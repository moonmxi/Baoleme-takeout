<template>
  <div class="orders-container">
    <div class="action-bar">
      <el-select
        v-model="selectedStoreId"
        placeholder="请选择店铺"
        style="width:200px"
        @change="handleStoreChange"
      >
        <el-option
          v-for="store in storeList"
          :key="store.store_id || store.id"
          :label="store.name"
          :value="store.store_id || store.id"
        />
      </el-select>
      <el-select v-model="statusFilter" placeholder="订单状态" clearable @change="handleStatusChange">
        <el-option label="待接单" :value="0" />
        <el-option label="制作中" :value="1" />
        <el-option label="配送中" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-input
        v-model="searchQuery"
        placeholder="搜索订单号"
        class="search-input"
        clearable
        @clear="handleSearch"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table
      v-loading="loading"
      :data="filteredOrders"
      border
      style="width: 100%"
    >
      <el-table-column prop="order_id" label="订单号" width="120" />
      <el-table-column prop="user_name" label="用户ID" width="120" />
      <el-table-column prop="total_price" label="总价" width="120">
        <template #default="{ row }">
          ¥{{ row.total_price?.toFixed(2) }}
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="180" fixed="right">
  <template #default="{ row }">
    <el-button
      v-if="row.status === 0"
      type="danger"
      link
      @click="handleCancel(row)"
    >
      拒绝订单
    </el-button>
    <el-button
      v-if="row.status === 1"
      type="primary"
      link
      @click="handleDeliver(row)"
    >
      出餐
    </el-button>
  </template>
</el-table-column>
    </el-table>

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

    <!-- 取消订单对话框 -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="拒绝订单"
      width="400px"
    >
      <el-form
        ref="cancelFormRef"
        :model="cancelForm"
        :rules="cancelRules"
        label-width="100px"
      >
        <el-form-item label="拒绝原因" prop="reason">
          <el-input
            v-model="cancelForm.reason"
            type="textarea"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCancel">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { merchantApi } from '@/api/merchant'
import { formatDate } from '@/utils/format'

// 店铺选择
const storeList = ref<any[]>([])
const selectedStoreId = ref<number | null>(null)

// 订单相关
const loading = ref(false)
const orders = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')
const statusFilter = ref('')

// 取消订单相关
const cancelDialogVisible = ref(false)
const cancelFormRef = ref<FormInstance>()
const cancelForm = ref({
  orderId: 0,
  reason: ''
})
const cancelRules: FormRules = {
  reason: [
    { required: true, message: '请输入拒绝原因', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}

// 加载店铺列表
const loadStoreList = async () => {
  try {
    const res = await merchantApi.getStoreList({ page: 1, page_size: 100 })
    if (res.success) {
      storeList.value = (res.data.stores || []).map((item: { storeId: any; id: any }) => ({
        ...item,
        storeId: item.storeId ?? item.id
      }))
      if (storeList.value.length > 0 && !selectedStoreId.value) {
        selectedStoreId.value = storeList.value[0].storeId
      }
    }
  } catch (e) {
    ElMessage.error('获取店铺列表失败')
  }
}

// 获取订单列表（只查当前店铺）
const getOrders = async () => {
  if (selectedStoreId.value == null) {
    orders.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const params: any = {
      store_id: selectedStoreId.value,
      page: currentPage.value,
      page_size: pageSize.value
    }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const response = await merchantApi.getOrderList(params)
    // 兼容多种 total 字段
    if (response.success) {
      orders.value = response.data.list || []
      total.value =
        orders.value.length
    } else {
      orders.value = []
      total.value = 0
      ElMessage.error(response.message || '获取订单列表失败')
    }
  } catch (error) {
    orders.value = []
    total.value = 0
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索和筛选
const filteredOrders = computed(() => {
  if (!searchQuery.value) return orders.value
  return orders.value.filter((order: any) =>
    String(order.order_id).includes(searchQuery.value)
  )
})

const handleSearch = () => {
  currentPage.value = 1
  getOrders()
}

const handleStatusChange = () => {
  currentPage.value = 1
  getOrders()
}

const handleStoreChange = () => {
  currentPage.value = 1
  getOrders()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  getOrders()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getOrders()
}

// 只允许对待接单（0）订单做拒绝
const handleCancel = (row: any) => {
  cancelForm.value = {
    orderId: row.order_id,
    reason: ''
  }
  cancelDialogVisible.value = true
}

const confirmCancel = async () => {
  if (selectedStoreId.value == null) {
    ElMessage.error('请选择店铺')
    return
  }
  if (!cancelFormRef.value) return
  await cancelFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await merchantApi.updateOrderStatus({
          id: cancelForm.value.orderId,
          new_status: 4, // 4为拒绝订单
          store_id: selectedStoreId.value as number,
          cancel_reason: cancelForm.value.reason
        })
        if (response.success) {
          ElMessage.success('订单已拒绝')
          cancelDialogVisible.value = false
          getOrders()
        } else {
          ElMessage.error(response.message || '拒绝订单失败')
        }
      } catch (error) {
        ElMessage.error('拒绝订单失败')
      }
    }
  })
}
const handleDeliver = async (row: any) => {
  if (!selectedStoreId.value) {
    ElMessage.error('请选择店铺')
    return
  }
  try {
    const response = await merchantApi.updateOrderStatus({
      id: row.order_id,
      new_status: 2, // 2为配送中
      store_id: selectedStoreId.value
    })
    if (response.success) {
      ElMessage.success('已出餐，订单进入配送中')
      getOrders()
    } else {
      ElMessage.error(response.message || '出餐操作失败')
    }
  } catch (error) {
    ElMessage.error('出餐操作失败')
    console.error('出餐操作失败:', error)
  }
}
// 状态标签
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'warning', // 待接单
    1: 'primary', // 制作中
    2: 'info',    // 配送中
    3: 'success', // 已完成
    4: 'danger'   // 已取消/拒绝
  }
  return statusMap[status] || 'info'
}
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待接单',
    1: '制作中',
    2: '配送中',
    3: '已完成',
    4: '已取消/拒绝'
  }
  return statusMap[status] || '未知状态'
}

onMounted(() => {
  loadStoreList()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.orders-container {
  padding: $spacing-base;

  .action-bar {
    display: flex;
    gap: $spacing-base;
    margin-bottom: $spacing-base;

    .search-input {
      width: 300px;
    }
  }

  .pagination {
    margin-top: $spacing-base;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 