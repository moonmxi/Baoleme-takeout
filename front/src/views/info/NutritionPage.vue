<template>
  <div class="info-nutrition-container">
    <el-card class="nutrition-card">
      <template #header>
        <div class="card-header">
          <h2>营养查询</h2>
        </div>
      </template>
      <section class="advice-section">
        <h3>健康饮食建议</h3>
        <ul>
          <li>均衡摄入主食、蔬菜、水果、蛋白质和脂肪</li>
          <li>多喝水，少饮含糖饮料</li>
          <li>适量运动，保持健康体重</li>
        </ul>
      </section>
      <section class="table-section">
        <h3>常见食材营养表</h3>
        <el-table :data="nutritionData" style="width: 100%; margin-bottom: 16px;">
          <el-table-column prop="name" label="食材" width="120" />
          <el-table-column prop="calories" label="热量(kcal/100g)" width="140" />
          <el-table-column prop="protein" label="蛋白质(g)" width="120" />
          <el-table-column prop="fat" label="脂肪(g)" width="120" />
          <el-table-column prop="carb" label="碳水(g)" width="120" />
        </el-table>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const nutritionData = ref([
  { name: '米饭', calories: 116, protein: 2.6, fat: 0.3, carb: 25.6 },
  { name: '鸡蛋', calories: 144, protein: 13.3, fat: 9.5, carb: 1.3 },
  { name: '牛肉', calories: 125, protein: 20.1, fat: 4.2, carb: 1.7 },
  { name: '西兰花', calories: 33, protein: 2.8, fat: 0.4, carb: 5.5 },
  { name: '苹果', calories: 52, protein: 0.2, fat: 0.2, carb: 13.8 },
  { name: '豆腐', calories: 81, protein: 8.1, fat: 4.2, carb: 1.6 },
])
</script>

<style scoped lang="scss">
.info-nutrition-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #f5f7fa;
  padding: 40px 0;
}
.nutrition-card {
  width: 700px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  .card-header {
    text-align: center;
    h2 {
      margin: 0;
      color: #409EFF;
      font-size: 26px;
      font-weight: 700;
    }
  }
  section {
    margin-bottom: 32px;
    h3 {
      color: #303133;
      font-size: 20px;
      margin-bottom: 12px;
      font-weight: 600;
    }
    p, ul {
      color: #606266;
      font-size: 16px;
      line-height: 1.8;
    }
    ul {
      padding-left: 24px;
    }
    li {
      margin-bottom: 8px;
    }
  }
}
</style> 