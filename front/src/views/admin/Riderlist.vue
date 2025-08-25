<template>
  <app-layout>
    <div class="admin-riderlist-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>骑手列表</h2>
          </div>
        </template>
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.keyword" placeholder="用户名" />
          </el-form-item>
          <el-form-item label="ID区间">
            <el-input v-model="searchForm.start_id" placeholder="起始ID" style="width:100px" />
            <span style="margin:0 4px;">-</span>
            <el-input v-model="searchForm.end_id" placeholder="结束ID" style="width:100px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table :data="riderList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="order_status" label="接单状态" />
          <el-table-column prop="dispatch_mode" label="接单模式" />
          <el-table-column prop="balance" label="余额" />
          <el-table-column prop="created_at" label="注册时间" />
          <el-table-column label="操作" width="100" fixed="right">
  <template #default="{ row }">
    <el-popconfirm
      title="确定要删除该骑手吗？"
      confirm-button-text="确定"
      cancel-button-text="取消"
      @confirm="handleDeleteRider(row)"
    >
      <template #reference>
        <el-button type="danger" size="small" link>删除</el-button>
      </template>
    </el-popconfirm>
  </template>
</el-table-column>
          <el-table-column prop="avatar" label="头像" width="80">
            <template #default="{ row }">
              <el-avatar :src="row.avatar" v-if="row.avatar" />
              <span v-else>-</span>
            </template>
          </el-table-column>
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

const riderList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  keyword: '',
  start_id: undefined,
  end_id: undefined
})

const loadRiders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value,
      ...searchForm.value
    }
    const res = await adminApi.getRiderList(params)
    riderList.value = res.data?.riders || []
    total.value = riderList.value.length < pageSize.value
      ? (currentPage.value - 1) * pageSize.value + riderList.value.length
      : currentPage.value * pageSize.value + 1
  } finally {
    loading.value = false
  }
}
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadRiders()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadRiders()
}
const handleSearch = () => {
  currentPage.value = 1
  loadRiders()
}
const resetSearch = () => {
  searchForm.value = { keyword: '', start_id: undefined, end_id: undefined }
  handleSearch()
}
const handleDeleteRider = async (row: any) => {
  try {
    const res = await adminApi.delete({ rider_name: row.username })
    if (res.success) {
      ElMessage.success('删除成功')
      loadRiders()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadRiders()
})
</script>

<style scoped lang="scss">
.admin-riderlist-container {
  max-width: 1000px;
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
  }
  .pagination-bar {
    margin-top: 24px;
    text-align: right;
  }
}
</style>