<template>
  <div class="info-tech-container">
    <el-card class="tech-card">
      <template #header>
        <div class="card-header">
          <h2>技术合作中心</h2>
        </div>
      </template>
      <section class="intro-section">
        <h3>平台技术优势</h3>
        <ul>
          <li>高并发分布式架构，保障平台稳定运行</li>
          <li>智能调度算法，提升配送效率</li>
          <li>大数据分析，助力商家精准营销</li>
          <li>多终端适配，支持Web、App、小程序</li>
        </ul>
      </section>
      <section class="api-section">
        <h3>API开放</h3>
        <p>我们为合作伙伴提供丰富的API接口，支持订单、商品、配送、用户等多场景对接，助力业务快速集成。</p>
      </section>
      <section class="cooperation-section">
        <h3>合作流程</h3>
        <ol>
          <li>提交合作意向</li>
          <li>技术对接与需求评估</li>
          <li>签署合作协议</li>
          <li>系统集成与上线</li>
        </ol>
      </section>
      <section class="contact-section">
        <h3>联系我们</h3>
        <p>技术合作邮箱：<b>tech@baoleme.com</b></p>
        <p>电话：<b>400-888-9999</b></p>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 页面逻辑（如果需要）
</script>

<style scoped lang="scss">
.info-tech-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #f5f7fa;
  padding: 40px 0;
}
.tech-card {
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
    p, ul, ol {
      color: #606266;
      font-size: 16px;
      line-height: 1.8;
    }
    ul, ol {
      padding-left: 24px;
    }
    li {
      margin-bottom: 8px;
    }
  }
  .contact-section p {
    margin-bottom: 6px;
  }
}
</style> 