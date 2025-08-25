<template>
  <div class="favorite-container">
    <div class="favorite-header">
      <h2>我的收藏店铺</h2>
      <el-form :inline="true" :model="searchForm" class="favorite-search-form" @submit.prevent="handleSearch">
        <el-form-item label="店铺名">
          <el-input v-model="searchForm.keyword" placeholder="输入店铺名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="searchForm.type" placeholder="类型" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="favorite-content">
      <el-empty v-if="favoriteList.length === 0" description="暂无收藏店铺">
        <el-button type="primary" @click="goToHome">去逛逛</el-button>
      </el-empty>
      <div v-else class="favorite-list">
        <el-card v-for="item in favoriteList" :key="item.store_id" class="favorite-item">
          <div class="item-content">
            <div class="item-image">
              <el-image :src="getStoreImage(item)" fit="cover" />
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ item.name }}</h3>
              <p class="item-desc">{{ item.description }}</p>
              <div class="item-meta">
                <span>评分：{{ item.rating || '-' }}</span>
                <span>类型：{{ item.type || '-' }}</span>
              </div>
              <div class="item-meta">
                <span>地址：{{ item.location }}</span>
              </div>
            </div>
            <div class="item-actions">
              <el-button type="primary" @click="goToStoreDetail(item)">查看详情</el-button>
              <el-button type="danger" @click="removeFavorite(item)">取消收藏</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const router = useRouter()
const favoriteList = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = reactive({
  keyword: '',
  type: ''
})

// 获取图片url
const getStoreImage = (store: any) => {
  if (!store.image) return '/logo.png'
  return store.image.startsWith('http') ? store.image : `http://localhost:8080/images/${store.image}`
}

// 获取收藏店铺列表
const loadFavorite = async () => {
  try {
    const params: any = {
      page: page.value,
      page_size: pageSize.value
    }
    if (searchForm.type) params.type = searchForm.type
    // 搜索时前端过滤
    console.log('搜索条件:', params)
    const res = await userApi.getFavoriteStores(params)
    console.log('获取收藏列表:', res)
    if (res.success && Array.isArray(res.data)) {
      let list = res.data
      if (searchForm.keyword) {
        const kw = searchForm.keyword.trim()
        list = list.filter((item: any) => item.name?.includes(kw))
      }
      favoriteList.value = list
      total.value = list.length
    } else {
      favoriteList.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('获取收藏列表失败:', e)
    favoriteList.value = []
    total.value = 0
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadFavorite()
}
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  page.value = 1
  loadFavorite()
}
const handlePageChange = (val: number) => {
  page.value = val
  loadFavorite()
}

// 取消收藏
const removeFavorite = async (item: any) => {
  try {
    const res = await userApi.unfavoriteStore(item.store_id)
    if (res.success) {
      ElMessage.success('已取消收藏')
      loadFavorite()
    } else {
      ElMessage.error(res.message || '取消收藏失败')
    }
  } catch (e) {
    console.error('取消收藏失败:', e)
    ElMessage.error('取消收藏失败')
  }
}

// 跳转到店铺详情
const goToStoreDetail = (item: any) => {
  router.push(`/user/merchant/${item.store_id || item.id}`)
}

// 跳转到首页
const goToHome = () => {
  router.push('/user/home')
}

onMounted(() => {
  loadFavorite()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.favorite-container {
  padding: $spacing-base;
  
  .favorite-header {
    margin-bottom: $spacing-large;
    
    h2 {
      margin: 0;
      color: $text-primary;
    }
  }
  
  .favorite-content {
    .favorite-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: $spacing-base;
      
      .favorite-item {
        transition: $transition-base;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: $box-shadow-base;
        }
        
        .item-content {
          display: flex;
          flex-direction: column;
          gap: $spacing-base;
          
          .item-image {
            width: 100%;
            height: 200px;
            overflow: hidden;
            border-radius: $border-radius-base;
            
            .el-image {
              width: 100%;
              height: 100%;
            }
          }
          
          .item-info {
            .item-name {
              margin: 0 0 $spacing-small;
              font-size: $font-size-large;
              color: $text-primary;
            }
            
            .item-desc {
              margin: 0 0 $spacing-small;
              color: $text-secondary;
              font-size: $font-size-base;
            }
            
            .item-price {
              color: $primary-color;
              font-size: $font-size-large;
              font-weight: bold;
            }
          }
          
          .item-actions {
            display: flex;
            gap: $spacing-small;
            
            .el-button {
              flex: 1;
            }
          }
        }
      }
    }
  }
}
</style> 