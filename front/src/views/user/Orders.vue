<template>
  <div class="orders-container">
    <!-- 筛选与搜索 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="订单状态">
        <el-select
          v-model="searchForm.status"
          :placeholder="searchForm.status === '' ? '全部' : '请选择状态'"
          clearable
          style="width: 180px"
        >
          <el-option label="全部" :value="''" />
          <el-option label="待接单" :value="0" />
          <el-option label="准备中" :value="1" />
          <el-option label="配送中" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 260px"
        />
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="searchForm.keyword"
          placeholder="订单号/店铺名"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadOrders">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 订单列表 -->
    <el-table
      :data="orders"
      border
      v-loading="loading"
      style="margin-top: 16px"
    >
      <el-table-column prop="order_id" label="订单号" width="120" />
      <el-table-column prop="store_name" label="店铺" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="下单时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="showOrderDetail(row)"
            >详情</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="orderDetailDialogVisible"
      title="订单详情"
      width="700px"
    >
      <div v-if="orderDetail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{
            orderDetail.order_id
          }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{
            orderDetail.created_at
          }}</el-descriptions-item>
          <el-descriptions-item label="商家名称">{{
            orderDetail.store_name
          }}</el-descriptions-item>
          <el-descriptions-item label="配送费"
            >¥{{ orderDetail.delivery_price?.toFixed(2) }}</el-descriptions-item
          >
          <el-descriptions-item label="订单总价"
            >¥{{ orderDetail.total_price?.toFixed(2) }}</el-descriptions-item
          >
        </el-descriptions>

        <!-- 商品列表及评价 -->
        <div class="products-section" style="margin-top: 16px">
          <h3>商品列表</h3>
          <el-table :data="orderItems" border>
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="price" label="单价">
              <template #default="{ row }">
                ¥{{ row.price?.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" />
            <el-table-column label="小计">
              <template #default="{ row }">
                ¥{{ (row.price * row.quantity)?.toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 配送信息 -->
        <div
          class="delivery-section"
          v-if="orderDetail.rider_name"
          style="margin-top: 16px"
        >
          <h3>配送信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="骑手姓名">{{
              orderDetail.rider_name
            }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{
              orderDetail.rider_phone
            }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 评价功能 -->
        <!-- 店铺评价 -->
        <div class="review-section" v-if="orderDetail.status === 3">
          <h3>店铺评价</h3>
          <el-form
            :model="shopReviewForm"
            :rules="reviewRules"
            ref="shopReviewFormRef"
          >
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="shopReviewForm.rating" />
            </el-form-item>
            <el-form-item label="评价内容" prop="comment">
              <el-input
                v-model="shopReviewForm.comment"
                type="textarea"
                :rows="2"
                placeholder="请输入对店铺的评价"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitReview('shop')"
                >提交店铺评价</el-button
              >
            </el-form-item>
          </el-form>
        </div>

        <!-- 商品列表及商品评价 -->
        <el-table :data="orderItems" border>
          <el-table-column prop="name" label="商品名称" />
          <el-table-column prop="price" label="单价">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" />
          <el-table-column label="小计">
            <template #default="{ row }"
              >¥{{ (row.price * row.quantity)?.toFixed(2) }}</template
            >
          </el-table-column>
          <el-table-column label="商品评价" width="220">
            <template #default="{ row }">
              <el-form
                :model="row.reviewForm"
                :rules="reviewRules"
                ref="row.reviewFormRef"
                style="display: flex; flex-direction: column"
              >
                <el-rate
                  v-model="row.reviewForm.rating"
                  style="font-size: 14px"
                />
                <el-input
                  v-model="row.reviewForm.comment"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入评价"
                  style="margin: 4px 0"
                />
                <el-button
                  size="small"
                  type="primary"
                  @click="submitReview('product', row)"
                  >提交</el-button
                >
              </el-form>
              <div
                v-if="row.myReview"
                style="margin-top: 4px; font-size: 13px; color: #888"
              >
                已评价：<el-rate
                  :model-value="row.myReview.rating"
                  disabled
                  style="font-size: 14px"
                />
                {{ row.myReview.comment }}
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 已评价信息 -->
        <div
          class="review-info"
          v-if="orderDetail.reviewed"
          style="margin-top: 16px"
        >
          <h3>评价信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="评分">
              <el-rate :model-value="orderDetail.review_rating" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="评价内容">
              {{ orderDetail.review_content }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { userApi } from "@/api/user";
import { useRouter } from "vue-router";

const shopReviewFormRef = ref();
const shopReviewForm = reactive({
  rating: 5,
  comment: "",
});
const orderItems = ref<any[]>([]);
const router = useRouter();
const orders = ref<any[]>([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchForm = ref({
  status: "",
  dateRange: [],
  keyword: "",
});
const orderDetailDialogVisible = ref(false);
const orderDetail = ref<any>(null);
const reviewFormRef = ref();
const reviewForm = ref({
  rating: 5,
  content: "",
});
const reviewRules = {
  rating: [{ required: true, message: "请选择评分", trigger: "change" }],
  comment: [
    { required: true, message: "请输入评价内容", trigger: "blur" },
    {
      min: 5,
      max: 200,
      message: "评价内容长度在 5 到 200 个字符",
      trigger: "blur",
    },
  ],
};
// 订单状态映射
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "warning", // 待接单
    1: "info", // 准备中
    2: "primary", // 配送中
    3: "success", // 已完成
    4: "danger", // 已取消
  };
  return statusMap[status] || "info";
};
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待接单",
    1: "准备中",
    2: "配送中",
    3: "已完成",
    4: "已取消",
  };
  return statusMap[status] || "未知";
};
const loadOrders = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: currentPage.value,
      page_size: pageSize.value,
    };
    if (searchForm.value.status !== "") params.status = searchForm.value.status;
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      console.log("time", searchForm);
      params.start_time = `${searchForm.value.dateRange[0]}T00:00:00`;
      params.end_time = `${searchForm.value.dateRange[1]}T23:59:59`;
    }
    // 关键字搜索（订单号/店铺名），需前端筛选
    console.log("搜索条件:", params);
    const res = await userApi.getOrderHistory(params);
    console.log("获取订单结果:", res);
    if (res.success) {
      let list = res.data.orders || [];
      // 前端关键字筛选
      if (searchForm.value.keyword) {
        const kw = searchForm.value.keyword.trim();
        list = list.filter(
          (o: any) =>
            String(o.order_id).includes(kw) ||
            (o.store_name && o.store_name.includes(kw))
        );
      }
      orders.value = list;
      total.value = list.length; // 若后端有 total 字段优先用
    }
  } catch (e) {
    console.log("获取订单失败:", e);
  } finally {
    loading.value = false;
  }
};
// 展示订单详情
const showOrderDetail = async (row: any) => {
  try {
    // 获取订单基本信息
    // 获取订单基本信息
    const res = await userApi.getOrderDetail({ order_id: row.order_id });
    if (!res.success || !res.data) {
      ElMessage.error(res.message || "获取订单详情失败");
      return;
    }
    orderDetail.value = { ...row, ...res.data };
    // 获取订单商品明细
    const itemRes = await userApi.gerOrderItems({ order_id: row.order_id });
    let items = [];
    if (itemRes.success && Array.isArray(itemRes.data)) {
      items = itemRes.data;
    } else {
      items = [];
    }

    // 对每个商品查找本用户最近一次评价
    for (const item of items) {
      // 保证有 product_id 字段
      item.product_id = item.product_id || item.id;
      // 初始化评价表单
      item.reviewForm = reactive({ rating: 5, comment: "" });
      item.reviewFormRef = ref();
      item.myReview = null;
      try {
        const prodRes = await userApi.getProductDetail({
          id: item.product_id || item.id,
        });
        if (
          prodRes.success &&
          prodRes.data &&
          Array.isArray(prodRes.data.reviews)
        ) {
          // 假设后端返回的 review 有 user_id 字段，筛选本用户
          const userId = orderDetail.value.user_id;
          const myReviews = prodRes.data.reviews.filter(
            (r: any) => r.user_id === userId
          );
          if (myReviews.length > 0) {
            // 取最近一条
            item.myReview = myReviews.sort(
              (
                a: { created_at: string | number | Date },
                b: { created_at: string | number | Date }
              ) =>
                new Date(b.created_at).getTime() -
                new Date(a.created_at).getTime()
            )[0];
          }
        }
      } catch (e) {
        console.log("获取商品评价失败:", e);
      }
    }
    orderItems.value = items;
    console.log(
      "itemRes.data.items:",
      itemRes.data.items,
      Array.isArray(itemRes.data.items)
    );
    orderDetailDialogVisible.value = true;
  } catch (e) {
    console.error("获取订单详情失败:", e);
    ElMessage.error("获取订单详情失败");
  }
};

// 提交评价
// 提交评价（type: 'shop' | 'product'）
const submitReview = async (type: "shop" | "product", item?: any) => {
  if (type === "shop") {
    if (!shopReviewFormRef.value) return;
    await shopReviewFormRef.value.validate(async (valid: boolean) => {
      if (valid) {
        try {
          const res = await userApi.submitReview({
            store_id: orderDetail.value.store_id,
            rating: shopReviewForm.rating,
            comment: shopReviewForm.comment,
          });
          if (res.success) {
            ElMessage.success("店铺评价提交成功");
          } else {
            ElMessage.error(res.message || "店铺评价提交失败");
          }
        } catch (e) {
          console.log("提交店铺评价失败:", e);
        }
      }
    });
  } else if (type === "product" && item) {
    // 只有订单已完成才能评价
    if (orderDetail.value.status !== 3) {
      ElMessage.error("订单未完成，无法评价商品");
      return;
    }
    console.log("start product review", item);
    try {
      const res = await userApi.submitReview({
        store_id: orderDetail.value.store_id,
        product_id: item.product_id,
        rating: item.reviewForm.rating,
        comment: item.reviewForm.comment,
      });
      console.log("提交评论表单", res);
      if (res.success) {
        ElMessage.success("商品评价提交成功");
        // 刷新该商品评价
        try {
          const prodRes = await userApi.getProductDetail({
            id: item.product_id,
          });
          if (
            prodRes.success &&
            prodRes.data &&
            Array.isArray(prodRes.data.reviews)
          ) {
            const userId = orderDetail.value.user_id;
            const myReviews = prodRes.data.reviews.filter(
              (r: any) => r.user_id === userId
            );
            if (myReviews.length > 0) {
              // 取最近一条
              item.myReview = myReviews.sort(
                (
                  a: { created_at: string | number | Date },
                  b: { created_at: string | number | Date }
                ) =>
                  new Date(b.created_at).getTime() -
                  new Date(a.created_at).getTime()
              )[0];
            } else {
              item.myReview = null;
            }
          }
        } catch (e) {
          console.log("刷新商品评价失败:", e);
        }
      } else {
        ElMessage.error(res.message || "商品评价提交失败");
      }
    } catch (e) {
      console.log("提交商品评价失败:", e);
    }
  }
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    status: "",
    dateRange: [],
    keyword: "",
  };
  loadOrders();
};
// 分页事件
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  loadOrders();
};
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  loadOrders();
};

// 查看订单详情
const viewOrder = (orderId: number) => {
  router.push(`/user/order/${orderId}`);
};

// 评价订单
const reviewOrder = (orderId: number) => {
  router.push(`/user/order/${orderId}/review`);
};

// // 删除订单
// const deleteOrder = async (orderId: number) => {
//   try {
//     await ElMessageBox.confirm('确定要删除这个订单吗？', '提示', {
//       type: 'warning'
//     })
//     // 假设有 userApi.deleteOrder(orderId) 方法
//     await userApi.deleteOrder(orderId)
//     ElMessage.success('删除成功')
//     loadOrders()
//   } catch (error: any) {
//     if (error !== 'cancel') {
//       console.log('删除失败:', error)
//     }
//   }
// }

onMounted(() => {
  loadOrders();

  // 监听 query 里的 order_id
  const orderId = Number(router.currentRoute.value.query.order_id);
  if (orderId) {
    // 监听 orders 加载完成后自动弹窗
    const stop = watch(
      () => orders.value,
      (list) => {
        const target = list.find((o: any) => o.order_id === orderId);
        if (target) {
          showOrderDetail(target);
          stop(); // 只弹一次
        }
      },
      { immediate: true }
    );
  }
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.orders-container {
  padding: $spacing-base;

  .card-header {
    h2 {
      margin: 0;
      color: $text-primary;
    }
  }

  .order-list {
    margin-top: $spacing-base;

    .order-card {
      margin-bottom: $spacing-base;

      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: $spacing-base;

        .order-info {
          .order-id {
            margin-right: $spacing-base;
            color: $text-regular;
          }

          .order-time {
            color: $text-secondary;
            font-size: $font-size-small;
          }
        }
      }

      .order-items {
        .order-item {
          display: flex;
          align-items: center;
          padding: $spacing-base 0;
          border-bottom: 1px solid $border-light;

          &:last-child {
            border-bottom: none;
          }

          .product-image {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: $border-radius-base;
            margin-right: $spacing-base;
          }

          .product-info {
            flex: 1;

            .product-name {
              margin: 0 0 $spacing-small;
              font-size: $font-size-base;
              color: $text-primary;
            }

            .product-price {
              margin: 0;
              color: $danger-color;
              font-size: $font-size-base;
              font-weight: bold;
            }
          }

          .quantity {
            color: $text-secondary;
          }
        }
      }

      .order-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: $spacing-base;
        padding-top: $spacing-base;
        border-top: 1px solid $border-light;

        .order-amount {
          color: $text-regular;

          .price {
            color: $danger-color;
            font-size: $font-size-large;
            font-weight: bold;
          }
        }

        .order-actions {
          display: flex;
          gap: $spacing-small;
        }
      }
    }
  }

  .pagination {
    margin-top: $spacing-base;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
