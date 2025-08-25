<template>
  <div class="product-manage-container">
    <div style="margin-bottom: 24px;">
      <el-select v-model="selectedStoreId" placeholder="选择店铺" style="width:200px" @change="loadProductList">
        <el-option v-for="store in storeList" :key="store.store_id || store.id" :label="store.name" :value="store.store_id || store.id" />
      </el-select>
      <el-button type="primary" icon="Plus" @click="showCreateDialog = true" style="margin-left: 16px;">新增商品</el-button>
    </div>
    <el-table :data="productList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="商品ID" width="80" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="volume"label = "销量"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button icon="Edit" circle size="small" @click="handleEditProduct(row)" />
          <el-button icon="Delete" circle size="small" type="danger" @click="handleDeleteProduct(row)" />
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
  @size-change="handleSizeChange"
  @current-change="handleCurrentChange"
/>
      />
    </div>

    <!-- 新增商品弹窗 -->
    <el-dialog v-model="showCreateDialog" title="新增商品" width="400px" :close-on-click-modal="false">
      <el-form :model="createForm" :rules="rules" ref="createFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="createForm.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="createForm.price" :min="0" :step="0.01" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="createForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="createForm.category" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-input v-model="createForm.image" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateProduct">创建</el-button>
      </template>
    </el-dialog>

    <!-- 编辑商品弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑商品" width="400px" :close-on-click-modal="false">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="editForm.price" :min="0" :step="0.01" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="editForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="editForm.category" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-input v-model="editForm.image" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editing" @click="handleUpdateProduct">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除商品确认弹窗 -->
    <el-dialog v-model="showDeleteDialog" title="删除商品" width="350px" :close-on-click-modal="false">
      <div style="padding: 24px 0;">确定要删除该商品吗？此操作不可恢复。</div>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" :loading="deleting" @click="confirmDeleteProduct">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { merchantApi } from '@/api/merchant'

const loading = ref(false)
const productList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 店铺选择
const storeList = ref<any[]>([])
const selectedStoreId = ref<number | null>(null)

// 新增商品弹窗
const showCreateDialog = ref(false)
const createFormRef = ref()
const creating = ref(false)
const createForm = reactive({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  category: '',
  image: ''
})
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

// 编辑商品弹窗
const showEditDialog = ref(false)
const editFormRef = ref()
const editing = ref(false)
const editForm = reactive({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  category: '',
  image: '',
  status: 1
})

// 删除商品弹窗
const showDeleteDialog = ref(false)
const deleting = ref(false)
const deleteProductId = ref<number | null>(null)

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
  } catch (e: any) {
    ElMessage.error(e.message || '获取店铺列表失败')
  }
}

// 加载商品列表
const loadProductList = async () => {
  if (!selectedStoreId.value) return
  loading.value = true
  try {
    console.log(pageSize.value, currentPage.value, selectedStoreId.value)
    const res = await merchantApi.getProductList({
      store_id: selectedStoreId.value,
      page: currentPage.value,
      page_size: pageSize.value
    })
    console.log('商品列表', res)
    if (res.success) {
      productList.value = res.data.products || []
      total.value = res.data.total_items || 0
    } else {
      ElMessage.error(res.message || '获取商品列表失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 新增商品
const handleCreateProduct = async () => {
  if (!createFormRef.value || !selectedStoreId.value) return
  await createFormRef.value.validate()
  creating.value = true
  try {
    const payload = {
      store_id: selectedStoreId.value,
      name: createForm.name,
      descripition: createForm.description, // 注意字段拼写
      price: createForm.price,
      stock: createForm.stock,
      category: createForm.category,
      image: createForm.image
    }
    console.log('Creating product with payload:', payload)
    const res = await merchantApi.createProduct(payload)
    console.log('Create product response:', res)
    if (res.success) {
      ElMessage.success('商品创建成功')
      showCreateDialog.value = false
      loadProductList()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    creating.value = false
  }
}

// 编辑商品
const handleEditProduct = (row: any) => {
  Object.assign(editForm, row)
  showEditDialog.value = true
}

// 保存编辑
const handleUpdateProduct = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate()
  editing.value = true
  try {
    if (!editForm.id) {
      ElMessage.error('商品ID无效，无法保存')
      editing.value = false
      return
    }
    const payload = {
      product_id: editForm.id,
      name: editForm.name,
      descripition: editForm.description, // 注意字段拼写
      price: editForm.price,
      stock: editForm.stock,
      category: editForm.category,
      image: editForm.image,
      status: editForm.status
    }
    const res = await merchantApi.updateProduct(payload)
    if (res.success) {
      ElMessage.success('商品信息已更新')
      showEditDialog.value = false
      loadProductList()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } finally {
    editing.value = false
  }
}

// 删除商品
const handleDeleteProduct = (row: any) => {
  deleteProductId.value = row.id
  showDeleteDialog.value = true
}
const confirmDeleteProduct = async () => {
  if (!deleteProductId.value) {
    ElMessage.error('商品ID无效，无法删除')
    return
  }
  deleting.value = true
  try {
    const res = await merchantApi.deleteProduct({ id: deleteProductId.value })
    if (res.success) {
      ElMessage.success('商品已删除')
      showDeleteDialog.value = false
      loadProductList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  } finally {
    deleting.value = false
  }
}
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  loadProductList()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadProductList()
}
onMounted(async () => {
  loadStoreList()
  loadProductList()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.products-container {
  padding: $spacing-base;

  .action-bar {
    display: flex;
    justify-content: space-between;
    margin-bottom: $spacing-base;

    .search-input {
      width: 300px;
    }
  }

  .product-image {
    width: 80px;
    height: 80px;
    border-radius: $border-radius-small;
  }

  .pagination {
    margin-top: $spacing-base;
    display: flex;
    justify-content: flex-end;
  }

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed $border-color;
      border-radius: $border-radius-base;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: $transition-base;

      &:hover {
        border-color: $primary-color;
      }
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: $text-secondary;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }
}
</style> 