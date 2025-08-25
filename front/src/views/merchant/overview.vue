<template>
  <div class="overview-container">
    <div class="overview-header">
      <el-select
        v-model="selectedStoreId"
        placeholder="请选择店铺"
        class="store-select"
        @change="handleStoreChange"
      >
        <el-option
          v-for="store in storeList"
          :key="store.id"
          :label="store.name"
          :value="store.id"
        />
      </el-select>
      <span class="store-info" v-if="currentStore">
        <el-tag type="info" effect="plain">
          <el-icon><Shop /></el-icon>
          店铺ID: {{ currentStore.id }}
        </el-tag>
        <span class="store-name">
          <el-icon><Star /></el-icon>
          {{ currentStore.name }}
        </span>
      </span>
    </div>

    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <el-icon><DataAnalysis /></el-icon>
          <span>销售概况</span>
          <el-select v-model="timeRange" class="range-select" @change="getOverview">
            <el-option label="今日" :value="0" />
            <el-option label="本周" :value="1" />
            <el-option label="本月" :value="2" />
          </el-select>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-block">
          <div class="stat-label">
            <el-icon><Money /></el-icon>
            总销售额
          </div>
          <div class="stat-value">￥{{ overview.total_sales ?? 0 }}</div>
        </div>
        <div class="stat-block">
          <div class="stat-label">
            <el-icon><ShoppingCart /></el-icon>
            订单数
          </div>
          <div class="stat-value">{{ overview.order_count ?? 0 }}</div>
        </div>
        <div class="stat-block">
          <div class="stat-label">
            <el-icon><Star /></el-icon>
            店铺评分
          </div>
          <div class="stat-value">{{ currentStore?.rating ?? '-' }}</div>
        </div>
      </div>
      <div class="hot-products">
        <span class="hot-title">
          <el-icon><TrendCharts /></el-icon>
          热销商品
        </span>
        <el-table :data="overview.popular_products || []" size="small" class="hot-table">
          <el-table-column prop="id" label="商品ID" width="80" />
          <el-table-column prop="name" label="商品名" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="price" label="价格" width="100" />
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                <el-icon v-if="row.status === 1"><CircleCheck /></el-icon>
                <el-icon v-else><CircleClose /></el-icon>
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-card class="overview-card" style="margin-top:32px;">
      <template #header>
        <div class="card-header">
          <el-icon><Histogram /></el-icon>
          <span>销售趋势</span>
          <el-select v-model="trendType" class="range-select" @change="getTrend">
            <el-option label="按日" :value="0" />
            <el-option label="按周" :value="1" />
            <el-option label="按月" :value="2" />
          </el-select>
        </div>
      </template>
      <div class="trend-charts">
        <div class="trend-chart">
          <el-icon class="chart-icon"><TrendCharts /></el-icon>
          <VChart :option="lineOption" autoresize />
        </div>
        <div class="trend-chart">
          <el-icon class="chart-icon"><Histogram /></el-icon>
          <VChart :option="barOption" autoresize />
        </div>
        <div class="trend-chart">
          <el-icon class="chart-icon"><PieChart /></el-icon>
          <VChart :option="pieOption" autoresize />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { merchantApi } from '@/api/merchant'
import { Shop, Star, Money, ShoppingCart, TrendCharts, Histogram, PieChart, DataAnalysis, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
// 店铺列表与选择
const storeList = ref<any[]>([])
const selectedStoreId = ref<number | null>(null)
const currentStore = computed(() => storeList.value.find(s => s.id === selectedStoreId.value) || null)

// 概况
const timeRange = ref(0) // 0=今日 1=本周 2=本月
const overview = reactive({
  total_sales: 0,
  order_count: 0,
  popular_products: []
})

// 销售趋势
const trendType = ref(0) // 0=日 1=周 2=月
const trendData = reactive({
  dates: [],
  values: []
})

// 加载所有店铺并选中最早创建的
const loadStoreList = async () => {
  try {
    const res = await merchantApi.getStoreList({ page: 1, page_size: 100 })
    if (res.success) {
      storeList.value = (res.data.stores || []).map((item: { id: any; store_id: any; storeId: any }) => ({
  ...item,
  id: item.id ?? item.store_id ?? item.storeId
}))
      storeList.value.sort((a, b) => new Date(a.created_at).getTime() - new Date(b.created_at).getTime())
      if (storeList.value.length > 0) {
        selectedStoreId.value = storeList.value[0].id
      }
    }
  } catch (e) {
    console.error('loadStoreList error:', e)
  }
}

// 概况接口
const getOverview = async () => {
  if (!selectedStoreId.value) return
  try {
    console.log("start",timeRange.value)
    console.log("selectedStoreId", selectedStoreId.value)
    const res = await merchantApi.getSalesOverview({
      store_id: selectedStoreId.value,
      time_range: timeRange.value
    })
    console.log("res", res)
    if (res.success) {

      overview.total_sales = res.data.total_sales ?? 0
      overview.order_count = res.data.order_count ?? 0
      overview.popular_products = res.data.popular_products || []
    }
  } catch (e) {
    console.error('getOverview error:', e)
  }
}

// 销售趋势接口
const getTrend = async () => {
  if (!selectedStoreId.value) return
  try {
    console.log("getTrend called with store_id:", selectedStoreId.value, "and type:", trendType.value)
    const res = await merchantApi.getSalesTrend({
      store_id: selectedStoreId.value,
      type: trendType.value
    })
    console.log("res", res)
    if (res.success) {
      trendData.dates = res.data.dates || []
      trendData.values = res.data.values || []
    }
  } catch (e) {
    console.error('getTrend error:', e)
  }
}

// 切换店铺时刷新数据
const handleStoreChange = () => {
  getOverview()
  getTrend()
}

// 折线图
const lineOption = computed(() => ({
  title: { text: '销售额趋势', left: 'center', top: 10, textStyle: { fontSize: 16 } },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: trendData.dates },
  yAxis: { type: 'value' },
  series: [
    {
      name: '销售额',
      data: trendData.values,
      type: 'line',
      smooth: true,
      areaStyle: {}
    }
  ]
}))

// 柱状图
const barOption = computed(() => ({
  title: { text: '销售额柱状图', left: 'center', top: 10, textStyle: { fontSize: 16 } },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: trendData.dates },
  yAxis: { type: 'value' },
  series: [
    {
      name: '销售额',
      data: trendData.values,
      type: 'bar',
      itemStyle: { color: '#409EFF' }
    }
  ]
}))

// 饼图（按热销商品分布）
const pieOption = computed(() => ({
  title: { text: '热销商品占比', left: 'center', top: 10, textStyle: { fontSize: 16 } },
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, left: 'center' },
  series: [
    {
      name: '热销商品',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: (overview.popular_products || []).map((p: any) => ({
        value: p.price ?? 0,
        name: p.name
      }))
    }
  ]
}))

onMounted(async () => {
  await loadStoreList()
  getOverview()
  getTrend()
})

</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.overview-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 32px 0;
}
.overview-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}
.store-select {
  width: 220px;
}
.store-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: 16px;
}
.store-name {
  font-weight: bold;
  font-size: 18px;
  color: $primary-color;
}
.overview-card {
  margin-bottom: 32px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.range-select {
  width: 120px;
}
.overview-stats {
  display: flex;
  gap: 48px;
  margin: 24px 0 16px 0;
}
.stat-block {
  background: $background-color;
  border-radius: 8px;
  padding: 24px 32px;
  min-width: 180px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
}
.stat-label {
  color: $text-secondary;
  font-size: 15px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: $primary-color;
}
.hot-products {
  margin-top: 24px;
}
.hot-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  display: inline-block;
}
.hot-table {
  margin-top: 8px;
}
.trend-chart {
  height: 320px;
}
</style>