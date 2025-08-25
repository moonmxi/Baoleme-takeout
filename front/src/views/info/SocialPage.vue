<template>
  <div class="info-social-container">
    <el-card class="social-card">
      <template #header>
        <div class="card-header">
          <h2>社会责任</h2>
        </div>
      </template>
      <section class="intro-section">
        <h3>公益活动</h3>
        <ul>
          <li>定期举办"爱心送餐"活动，关爱社区老人和弱势群体</li>
          <li>与多家公益组织合作，助力社会公益项目</li>
        </ul>
      </section>
      <section class="eco-section">
        <h3>环保理念</h3>
        <p>我们积极推广环保包装，鼓励用户选择无塑料、可降解餐具，致力于减少外卖行业对环境的影响。</p>
      </section>
      <section class="culture-section">
        <h3>企业文化</h3>
        <ul>
          <li>以用户为中心，追求卓越服务</li>
          <li>诚信经营，合作共赢</li>
          <li>创新驱动，持续成长</li>
        </ul>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 页面逻辑（如果需要）
</script>

<style scoped lang="scss">
.info-social-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #f5f7fa;
  padding: 40px 0;
}
.social-card {
  width: 600px;
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