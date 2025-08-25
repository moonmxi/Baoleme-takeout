<template>
  <app-layout>
    <div class="admin-storelist-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>店铺列表</h2>
          </div>
        </template>
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="店铺名">
            <el-input v-model="searchForm.keyword" placeholder="店铺名/简介" />
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
        <el-table :data="storeList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="店铺ID" width="80" />
          <el-table-column prop="name" label="店铺名称" />
          <el-table-column prop="description" label="简介" />
          <el-table-column prop="location" label="地址" />
          <el-table-column prop="rating" label="评分" />
          <el-table-column prop="status" label="状态" />
          <el-table-column prop="created_at" label="创建时间" />
          <el-table-column label="操作" width="120">
    <template #default="{ row }">
      <el-button size="small" @click="showProductDialog(row)">查看商品</el-button>
      <el-popconfirm
        title="确定要删除该店铺吗？"
        confirm-button-text="确定"
        cancel-button-text="取消"
        @confirm="handleDeleteStore(row)"
      >
        <template #reference>
          <el-button type="danger" size="small" link>删除</el-button>
        </template>
      </el-popconfirm>
    </template>
  </el-table-column>
          <el-table-column prop="image" label="图片" width="80">
            <template #default="{ row }">
              <el-avatar :src="row.image" v-if="row.image" />
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
        <!-- 商品弹窗 -->
<el-dialog v-model="productDialogVisible" title="店铺商品" width="700px">
  <el-table :data="productList" style="width: 100%">
    <el-table-column prop="id" label="商品ID" width="90"/>
    <el-table-column prop="name" label="商品名"/>
    <el-table-column prop="price" label="价格"/>
    <el-table-column prop="stock" label="库存"/>
    <el-table-column prop="status" label="状态">
      <template #default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? '上架' : '下架' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="100">
      <template #default="{ row }">
        <el-popconfirm
          title="确定要删除该商品吗？"
          confirm-button-text="确定"
          cancel-button-text="取消"
          @confirm="handleDeleteProduct(row)"
        >
          <template #reference>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>
</el-dialog>
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
import { merchantApi } from '@/api/merchant'
const storeList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  keyword: '',
  start_id: undefined,
  end_id: undefined
})
const productDialogVisible = ref(false)
const productList = ref<any[]>([])
const currentStore = ref<any>(null)
const loadStores = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value,
      ...searchForm.value
    }
    const res = await adminApi.getStoreList(params)
    storeList.value = res.data?.stores || []
    total.value = storeList.value.length < pageSize.value
      ? (currentPage.value - 1) * pageSize.value + storeList.value.length
      : currentPage.value * pageSize.value + 1
  } finally {
    loading.value = false
  }
}
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadStores()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadStores()
}
const handleSearch = () => {
  currentPage.value = 1
  loadStores()
}
const resetSearch = () => {
  searchForm.value = { keyword: '', start_id: undefined, end_id: undefined }
  handleSearch()
}
// 查看商品弹窗
const showProductDialog = async (store: any) => {
  currentStore.value = store
  productDialogVisible.value = true
  try {
    const res = await adminApi.getStoreProducts({
      store_id: store.id,
      page: 1,
      page_size: 100
    })
    console.log('获取商品列表:', res)
    if (res.success && res.data && res.data.products) {
      // 兼容两种返回结构
      if (Array.isArray(res.data.products)) {
        productList.value = res.data.products
      } else if (
        res.data.products &&
        typeof res.data.products === 'object' &&
        Array.isArray((res.data.products as any).list)
      ) {
        productList.value = (res.data.products as any).list
      } else {
        productList.value = []
      }
    } else {
      productList.value = []
      console.error('获取商品失败:', res.message)
    }
  } catch (e) {
    productList.value = []
    console.error('获取商品失败:', e)
  }
}

// 删除商品（管理员权限）
const handleDeleteProduct = async (row: any) => {
  try {
    const res = await adminApi.delete({
      store_name: currentStore.value.name,
      product_name: row.name
    })
    if (res.success) {
      ElMessage.success('商品删除成功')
      showProductDialog(currentStore.value) // 重新加载商品
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('删除失败')
  }
}
const handleDeleteStore = async (row: any) => {
  try {
    const res = await adminApi.delete({ store_name: row.name })
    if (res.success) {
      ElMessage.success('删除成功')
      loadStores()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadStores()
})
</script>

<style scoped lang="scss">
.admin-storelist-container {
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

<style scoped lang="scss">
.admin-storelist-container {
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