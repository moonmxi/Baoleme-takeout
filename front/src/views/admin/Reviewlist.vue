<template>
  <app-layout>
    <div class="admin-reviewlist-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>评价列表</h2>
            <div class="filter-bar">
              <el-input
                v-model="searchUserId"
                placeholder="用户ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadReviews"
              />
              <el-input
                v-model="searchStoreId"
                placeholder="店铺ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadReviews"
              />
              <el-input
                v-model="searchProductId"
                placeholder="商品ID"
                clearable
                style="width: 120px; margin-right: 12px"
                @keyup.enter="loadReviews"
              />
              <el-input
                v-model="searchStartRating"
                placeholder="最低评分"
                clearable
                style="width: 100px; margin-right: 12px"
                @keyup.enter="loadReviews"
              />
              <el-input
                v-model="searchEndRating"
                placeholder="最高评分"
                clearable
                style="width: 100px; margin-right: 12px"
                @keyup.enter="loadReviews"
              />
              <el-date-picker
                v-model="searchDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="margin-right: 12px"
                value-format="YYYY-MM-DDTHH:mm:ss"
                @change="loadReviews"
              />
              <el-button type="primary" @click="loadReviews">搜索</el-button>
            </div>
          </div>
        </template>
        <el-table :data="reviewList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="评价ID" width="80" />
          <el-table-column prop="user_id" label="用户ID" width="100" />
          <el-table-column prop="store_id" label="店铺ID" width="100" />
          <el-table-column prop="product_id" label="商品ID" width="100" />
          <el-table-column prop="rating" label="评分" width="80" />
          <el-table-column prop="comment" label="评价内容" />
          <el-table-column prop="created_at" label="评价时间" width="180" />
          <el-table-column prop="images" label="图片" width="120">
            <template #default="{ row }">
              <el-image
                v-for="(img, idx) in row.images"
                :key="idx"
                :src="img"
                style="width: 40px; height: 40px; margin-right: 4px"
                fit="cover"
                v-if="row.images && row.images.length"
              />
              <span v-if="!row.images || !row.images.length">-</span>
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

const reviewList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchUserId = ref('')
const searchStoreId = ref('')
const searchProductId = ref('')
const searchStartRating = ref('')
const searchEndRating = ref('')
const searchDate = ref<[string, string] | null>(null)

const loadReviews = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value
    }
    if (searchUserId.value) params.user_id = Number(searchUserId.value)
    if (searchStoreId.value) params.store_id = Number(searchStoreId.value)
    if (searchProductId.value) params.product_id = Number(searchProductId.value)
    if (searchStartRating.value) params.start_rating = Number(searchStartRating.value)
    if (searchEndRating.value) params.end_rating = Number(searchEndRating.value)
    if (searchDate.value && searchDate.value.length === 2) {
      params.start_time = searchDate.value[0]
      params.end_time = searchDate.value[1]
    }
    const res = await adminApi.getReviewList(params)
    reviewList.value = res.data?.reviews || []
    total.value = reviewList.value.length < pageSize.value
      ? (currentPage.value - 1) * pageSize.value + reviewList.value.length
      : currentPage.value * pageSize.value + 1
  } catch (e: any) {
    ElMessage.error(e.message || '获取评价列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadReviews()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadReviews()
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped lang="scss">
.admin-reviewlist-container {
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
      flex-wrap: wrap;
    }
  }
  .pagination-bar {
    margin-top: 24px;
    text-align: right;
  }
}
</style>