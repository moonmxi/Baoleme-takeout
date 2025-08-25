<template>
  <div class="store-setting-container">
    <div style="margin-bottom: 24px;">
      <el-button type="primary" icon="Plus" @click="showCreateDialog = true">创建新店铺</el-button>
    </div>
    <el-table :data="storeList" style="width: 100%" v-loading="loading">
      <el-table-column prop="store_id" label="店铺ID" width="80" />
      <el-table-column prop="name" label="店铺名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="description" label="简介" />
      <el-table-column prop="location" label="地址" />
      <el-table-column prop="rating" label="评分" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '营业中' : '休息/下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button icon="View" circle size="small" @click="handleViewStore(row)" />
          <el-button icon="Edit" circle size="small" @click="handleEditStore(row)" />
          <el-button icon="Delete" circle size="small" type="danger" @click="handleDeleteStore(row)" />
            <el-button icon="View" circle size="small" @click="handleShowReviewDialog(row)" />
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top: 16px; text-align: right;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadStoreList"
        @current-change="loadStoreList"
      />
      <el-dialog v-model="showReviewDialog" title="店铺评论" width="700px" :close-on-click-modal="false">
      <div style="margin-bottom: 12px;">
        <el-checkbox v-model="reviewFilter.hasImage" @change="fetchStoreReviews">仅看带图</el-checkbox>
      </div>
      <el-table :data="reviewList" style="width:100%;" v-loading="reviewLoading">
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="user_avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.user_avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="product_name" label="商品" width="120" />
        <el-table-column prop="rating" label="评分" width="80">
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容" />
        <el-table-column prop="image" label="图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="row.image" :preview-src-list="[row.image]" style="width:60px;height:60px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="时间" width="160" />
      </el-table>
      <div style="margin-top:16px;text-align:right;">
        <el-pagination
          v-model:current-page="reviewFilter.page"
          v-model:page-size="reviewFilter.page_size"
          :total="reviewTotal"
          :page-sizes="[5,10,20]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchStoreReviews"
          @current-change="fetchStoreReviews"
        />
      </div>
      <template #footer>
        <el-button @click="showReviewDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 所有店铺评论区块 -->
    <el-card style="margin-top: 40px;">
  <template #header>
    <div style="display:flex;align-items:center;gap:16px;">
      <span>所有店铺评论</span>
      <el-select v-model="allReviewFilter.store_id" placeholder="选择店铺" style="width:200px" clearable @change="fetchAllReviews">
        <el-option label="全部店铺" :value="null" />
        <el-option v-for="store in storeList" :key="store.store_id || store.id" :label="store.name" :value="store.store_id || store.id" />
      </el-select>
      <el-checkbox v-model="allReviewFilter.hasImage" @change="fetchAllReviews">仅看带图</el-checkbox>
    </div>
  </template>
  <el-table :data="allReviewList" style="width:100%;" v-loading="allReviewLoading">
    <el-table-column prop="username" label="用户" width="120" />
    <el-table-column prop="product_name" label="商品" width="120" />
    <el-table-column prop="rating" label="评分" width="200">
      <template #default="{ row }">
        <el-rate :model-value="row.rating" disabled show-score />
      </template>
    </el-table-column>
    <el-table-column prop="comment" label="评价内容" />
    <el-table-column prop="image" label="图片" width="100">
      <template #default="{ row }">
        <el-image v-if="row.image" :src="row.image" :preview-src-list="[row.image]" style="width:60px;height:60px;" />
        <span v-else>-</span>
      </template>
    </el-table-column>
    <el-table-column prop="created_at" label="时间" width="160" />
  </el-table>
  <div style="margin-top:16px;text-align:right;">
    <el-pagination
      v-model:current-page="allReviewFilter.page"
      v-model:page-size="allReviewFilter.page_size"
      :total="allReviewTotal"
      :page-sizes="[5,10,20]"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchAllReviews"
      @current-change="fetchAllReviews"
    />
  </div>
</el-card>
    </div>

    <!-- 创建店铺弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建新店铺" width="400px" :close-on-click-modal="false">
      <el-form :model="createForm" :rules="rules" ref="createFormRef" label-width="80px">
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="createForm.type" placeholder="如快餐、饮品等" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" placeholder="店铺简介" />
        </el-form-item>
        <el-form-item label="地址" prop="location">
          <el-input v-model="createForm.location" placeholder="店铺地址" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-input v-model="createForm.image" placeholder="图片URL或文件名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateStore">创建</el-button>
      </template>
    </el-dialog>

    <!-- 查看店铺信息弹窗 -->
    <el-dialog v-model="showViewDialog" title="店铺信息" width="400px" :close-on-click-modal="false">
      <div v-if="viewStoreInfo">
        <el-avatar :src="viewStoreInfo.image" size="large" v-if="viewStoreInfo.image" style="margin-bottom: 16px;" />
        <div><b>名称：</b>{{ viewStoreInfo.name }}</div>
        <div><b>类型：</b>{{ viewStoreInfo.type || '-' }}</div>
        <div><b>描述：</b>{{ viewStoreInfo.description || '-' }}</div>
        <div><b>地址：</b>{{ viewStoreInfo.location || '-' }}</div>
        <div><b>评分：</b>{{ viewStoreInfo.rating ?? '-' }}</div>
        <div><b>状态：</b>{{ viewStoreInfo.status === 1 ? '营业中' : '休息/下架' }}</div>
        <div><b>创建时间：</b>{{ viewStoreInfo.created_at }}</div>
      </div>
      <template #footer>
        <el-button @click="showViewDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑店铺弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑店铺信息" width="400px" :close-on-click-modal="false">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="80px">
        <el-form-item label="店铺ID" prop="id">
          <el-input v-model="editForm.id" disabled />
        </el-form-item>
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="editForm.type" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" />
        </el-form-item>
        <el-form-item label="地址" prop="location">
          <el-input v-model="editForm.location" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-input v-model="editForm.image" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择">
            <el-option label="营业中" :value="1" />
            <el-option label="休息/下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editing" @click="handleUpdateStore">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除店铺确认弹窗 -->
    <el-dialog v-model="showDeleteDialog" title="删除店铺" width="350px" :close-on-click-modal="false">
      <div style="padding: 24px 0;">确定要删除该店铺吗？此操作不可恢复。</div>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDeleteStore">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { merchantApi } from '@/api/merchant'

const loading = ref(false)
const storeList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 创建店铺弹窗
const showCreateDialog = ref(false)
const createFormRef = ref()
const creating = ref(false)
const createForm = reactive({
  name: '',
  type: '',
  description: '',
  location: '',
  image: ''
})
const rules = {
  name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

// ----------- 单店铺评论弹窗 -----------
const showReviewDialog = ref(false)
const reviewStoreId = ref<number | null>(null)
const reviewList = ref<any[]>([])
const reviewTotal = ref(0)
const reviewLoading = ref(false)
const reviewFilter = reactive({
  hasImage: false,
  page: 1,
  page_size: 10
})

// ----------- 所有店铺评论区块 -----------
const allReviewList = ref<any[]>([])
const allReviewTotal = ref(0)
const allReviewLoading = ref(false)
const allReviewFilter = reactive({
  store_id: null as number | null,
  hasImage: false,
  page: 1,
  page_size: 10
})

// 查看店铺弹窗
const showViewDialog = ref(false)
const viewStoreInfo = ref<any>(null)

// 编辑店铺弹窗
const showEditDialog = ref(false)
const editFormRef = ref()
const editing = ref(false)
const editForm = reactive({
  id: null,
  name: '',
  type: '',
  description: '',
  location: '',
  image: '',
  status: 1
})

// 删除店铺弹窗
const showDeleteDialog = ref(false)
const deleting = ref(false)
const deleteStoreId = ref<number | null>(null)

// 加载店铺列表（按创建时间升序）
const loadStoreList = async () => {
  loading.value = true
  try {
    const res = await merchantApi.getStoreList({
      page: currentPage.value,
      page_size: pageSize.value
    })
    if (res.success) {
      storeList.value = (res.data.stores || []).slice().sort(
        (a: { created_at: string }, b: { created_at: string }) =>
          new Date(a.created_at).getTime() - new Date(b.created_at).getTime()
      )
      total.value = res.data.stores?.length || 0
    } else {
      ElMessage.error(res.message || '获取店铺列表失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取店铺列表失败')
  } finally {
    loading.value = false
  }
}

// 创建店铺
const handleCreateStore = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate()
  creating.value = true
  try {
    const res = await merchantApi.createStore({ ...createForm })
    if (res.success) {
      ElMessage.success('店铺创建成功')
      showCreateDialog.value = false
      loadStoreList()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    creating.value = false
  }
}

// 查看店铺信息
const handleViewStore = async (row: any) => {
  try {
    console.log('查看店铺信息，ID:', row.store_id )
    const res = await merchantApi.getStore({ store_id: row.store_id })
    if (res.success) {
      viewStoreInfo.value = res.data
      showViewDialog.value = true
    } else {
      ElMessage.error(res.message || '获取店铺信息失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取店铺信息失败')
  }
}

// 编辑店铺
const handleEditStore = (row: any) => {
  Object.assign(editForm, row)
  editForm.id = row.id ?? row.store_id ?? row.storeId
  showEditDialog.value = true
}

// 保存编辑
const handleUpdateStore = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate()
  editing.value = true
  try {
    console.log('准备更新店铺信息:', editForm)
    if (editForm.id == null) {
      ElMessage.error('店铺ID无效，无法保存')
      console.error('编辑店铺时未提供有效的ID:', editForm)
      editing.value = false
      return
    }
    const payload = {
      id: Number(editForm.id), // 确保是 number
      name: editForm.name,
      type: editForm.type,
      description: editForm.description,
      location: editForm.location,
      image: editForm.image,
      status: editForm.status
    }
    const res = await merchantApi.updateStore(payload)
    if (res.success) {
      ElMessage.success('店铺信息已更新')
      showEditDialog.value = false
      loadStoreList()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } finally {
    editing.value = false
  }
}
let test = ref<any[]>
// 删除店铺
const handleDeleteStore = (row: any) => {
  deleteStoreId.value = row.store_id
  showDeleteDialog.value = true
  test = row
}
const confirmDeleteStore = async () => {
    if (deleteStoreId.value == null) {
    ElMessage.error('店铺ID无效，无法删除')
    console.error('删除店铺时未提供有效的ID:',test )
    return
  }
  try {
    console.log('准备删除店铺ID:', deleteStoreId.value)
    const res = await merchantApi.deleteStore({ store_id: deleteStoreId.value })
    console.log('删除店铺响应:', res)
    if (res.success) {
      ElMessage.success('店铺已删除')
      showDeleteDialog.value = false
      loadStoreList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  } finally {
    deleting.value = false
  }
}


// 打开某个店铺评论弹窗
const handleShowReviewDialog = (row: any) => {
  reviewStoreId.value = row.store_id || row.id
  reviewFilter.page = 1
  fetchStoreReviews()
  showReviewDialog.value = true
}

// 获取某店铺评论
const fetchStoreReviews = async () => {
  if (!reviewStoreId.value) return
  reviewLoading.value = true
  try {
    const params: any = {
      store_id: reviewStoreId.value,
      page: reviewFilter.page,
      page_size: reviewFilter.page_size
    }
    if (reviewFilter.hasImage) params.hasImage = true
    const res = await merchantApi.getStoreReviews(params)
    if (res.success) {
      reviewList.value = res.data.reviews || []
      reviewTotal.value = res.data.total_count || 0
    } else {
      ElMessage.error(res.message || '获取评论失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取评论失败')
  } finally {
    reviewLoading.value = false
  }
}

// 获取所有店铺评论（可筛选店铺/带图）store_id写法不统一，注意
const fetchAllReviews = async () => {
  // 如果未选具体店铺，不请求，直接提示
  if (!allReviewFilter.store_id) {
    allReviewList.value = [];
    allReviewTotal.value = 0;
    ElMessage.warning('请先选择具体店铺');
    return;
  }
  allReviewLoading.value = true;
  try {
    const params: any = {
      page: allReviewFilter.page,
      page_size: allReviewFilter.page_size,
      store_id: allReviewFilter.store_id
    }
    if (allReviewFilter.hasImage) params.hasImage = true
    const res = await merchantApi.getStoreReviews(params)
    if (res.success) {
      allReviewList.value = res.data.reviews || []
      allReviewTotal.value = res.data.total_count || 0
    } else {
      ElMessage.error(res.message || '获取评论失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取评论失败')
  } finally {
    allReviewLoading.value = false
  }
}

onMounted(() => {
    loadStoreList()
})
</script>

<style scoped>
.store-setting-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 0;
}
</style>