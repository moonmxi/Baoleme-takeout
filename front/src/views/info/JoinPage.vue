<template>
  <div class="info-join-container">
    <el-card class="join-card">
      <template #header>
        <div class="card-header">
          <h2>入驻加盟</h2>
        </div>
      </template>
      <section class="intro-section">
        <h3>欢迎加入饱了么外卖平台！</h3>
        <p>我们致力于为商家提供高效、便捷的线上经营平台，助力您的生意腾飞。无论您是餐饮店、超市还是便利店，只要有优质的商品和服务，都可以轻松入驻。</p>
      </section>
      <section class="advantage-section">
        <h3>加盟优势</h3>
        <ul>
          <li>平台流量大，订单多，轻松拓展新客源</li>
          <li>智能配送系统，保障高效送达</li>
          <li>多样化营销工具，提升店铺曝光</li>
          <li>专属客服支持，解决经营难题</li>
        </ul>
      </section>
      <section class="process-section">
        <h3>加盟流程</h3>
        <ol>
          <li>在线提交入驻申请</li>
          <li>平台审核资料</li>
          <li>签署合作协议</li>
          <li>开通店铺并上线运营</li>
        </ol>
      </section>
      <section class="contact-section">
        <h3>联系我们</h3>
        <p>招商热线：<b>400-888-8888</b></p>
        <p>邮箱：<b>join@baoleme.com</b></p>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 页面逻辑（如果需要）
</script>

<style scoped lang="scss">
.info-join-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #f5f7fa;
  padding: 40px 0;
}
.join-card {
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