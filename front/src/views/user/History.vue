<template>
  <div class="history-view-container">
    <h2>浏览历史</h2>
    <el-empty v-if="historyList.length === 0" description="暂无浏览记录" />
    <el-row v-else :gutter="20">
      <el-col
        v-for="store in historyList"
        :key="store.id || store.store_id"
        :span="8"
        style="margin-bottom: 20px"
      >
        <el-card class="store-card" shadow="hover" @click="goToStore(store)">
          <img
            :src="getStoreImage(store)"
            class="store-img"
            :alt="store.name"
            style="width: 100%; height: 120px; object-fit: cover; border-radius: 8px"
          />
          <div class="store-info" style="margin-top: 10px">
            <div class="store-name" style="font-weight: bold">{{ store.name }}</div>
            <div class="store-desc" style="color: #888">{{ store.description }}</div>
            <div class="store-meta" style="color: #aaa; font-size: 13px">
              地址：{{ store.location }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { userApi } from "@/api/user";

const router = useRouter();
const historyList = ref<any[]>([]);
const page = ref(1);
const pageSize = ref(12);
const total = ref(0);

const getStoreImage = (store: any) => {
  if (!store.image) return "/logo.png";
  return store.image.startsWith("http")
    ? store.image
    : `http://localhost:8080/images/${store.image}`;
};

const loadHistory = async () => {
  try {
    const res = await userApi.getViewHistory({ page: page.value, page_size: pageSize.value });
    if (res.success && res.data && Array.isArray(res.data.stores)) {
      historyList.value = res.data.stores;
      total.value = res.data.stores.length;
    } else {
      historyList.value = [];
      total.value = 0;
    }
  } catch (e) {
    console.error("获取浏览历史失败:", e);
    historyList.value = [];
    total.value = 0;
  }
};

const handlePageChange = (val: number) => {
  page.value = val;
  loadHistory();
};

const goToStore = (store: any) => {
  router.push(`/user/merchant/${store.id || store.store_id}`);
};

onMounted(() => {
  loadHistory();
});
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.history-view-container {
  padding: $spacing-large;
  min-height: 60vh;

  h2 {
    margin-bottom: $spacing-large;
    color: $primary-color;
    font-size: 24px;
    font-weight: 600;
    text-align: left;
  }

  .el-empty {
    margin: $spacing-xl 0;
  }

  .el-row {
    margin-bottom: $spacing-large;
  }

  .store-card {
    cursor: pointer;
    transition: $transition-base;
    border-radius: $border-radius-large;
    box-shadow: $box-shadow-light;
    overflow: hidden;
    padding: 0;

    &:hover {
      box-shadow: $box-shadow-base;
      transform: translateY(-4px) scale(1.02);
    }

    .store-img {
      width: 100%;
      height: 120px;
      object-fit: cover;
      border-radius: $border-radius-large $border-radius-large 0 0;
      background: #f5f7fa;
    }

    .store-info {
      padding: $spacing-base;

      .store-name {
        font-size: 18px;
        font-weight: bold;
        color: $text-primary;
        margin-bottom: $spacing-mini;
      }

      .store-desc {
        color: $text-secondary;
        font-size: 15px;
        margin-bottom: $spacing-mini;
      }

      .store-meta {
        color: $text-regular;
        font-size: 13px;
      }
    }
  }

  .el-pagination {
    margin-top: $spacing-large;
    text-align: center;
  }
}
</style>