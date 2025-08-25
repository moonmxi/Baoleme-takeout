<template>
  <app-layout>
    <div class="shop-detail-container">
      <!-- 商家信息 -->
      <el-card class="shop-info-card">
        <div class="shop-header">
          <img
            :src="getStoreImage(shop)"
            class="shop-avatar"
            :alt="shop.name"
            @error="onShopImgError"
            style="
              width: 400px;
              height: 300px;
              object-fit: cover;
              border-radius: 8px;
            "
          />
          <div class="shop-basic">
            <h2 class="shop-name">{{ shop.name }}</h2>
            <div class="shop-rating">
              <el-rate
                v-model="shop.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
              <span class="shop-type">{{ shop.type }}</span>
            </div>
            <div class="shop-location">
              <el-icon><Location /></el-icon>
              {{ shop.location }}
            </div>
            <div class="shop-desc">
              {{ shop.description }}
            </div>
            <div class="shop-created">
              <el-icon><Clock /></el-icon>
              创建时间：{{
                shop.created_at ? shop.created_at.slice(0, 10) : "-"
              }}
              <el-button
                :type="isFavorite ? 'danger' : 'primary'"
                size="small"
                style="margin-left: 16px"
                @click="toggleFavorite"
              >
                <el-icon v-if="isFavorite"><Star /></el-icon>
                <el-icon v-else><Star /></el-icon>
                {{ isFavorite ? "已收藏" : "收藏店铺" }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
      <!-- 优惠券展示区 -->
<el-card class="coupon-section" style="margin-bottom: 16px">
  <template #header>
    <div style="display: flex; align-items: center">
      <el-icon><Ticket /></el-icon>
      <span style="margin-left: 8px">店铺优惠券</span>
    </div>
  </template>
  <div v-if="mergedCoupons.length === 0" style="color: #888">
    暂无可领取优惠券
  </div>
  <el-row v-else :gutter="12">
    <el-col
      v-for="coupon in mergedCoupons"
      :key="couponKey(coupon)"
      :span="8"
    >
      <el-card class="coupon-card" shadow="hover">
        <div>
          <div v-if="coupon.type == 1">
            <b>{{ (coupon.discount * 10).toFixed(1) }}折券</b>
          </div>
          <div v-else-if="coupon.type == 2">
            <b>满{{ coupon.full_amount }}减{{ coupon.reduce_amount }}</b>
          </div>
          <div v-else>
            <b>未知类型优惠券</b>
          </div>
          <div style="font-size: 13px; color: #888">
            有效期至：{{
              coupon.expiration_date
                ? coupon.expiration_date.slice(0, 10)
                : "长期有效"
            }}
          </div>
          <div style="margin: 8px 0">共 {{ coupon.count }} 张可领</div>
          <el-button
            type="primary"
            size="small"
            :disabled="coupon.claimed"
            @click="handleClaimCoupon(coupon)"
          >
            {{ coupon.claimed ? "已领取" : "领取" }}
          </el-button>
        </div>
      </el-card>
    </el-col>
  </el-row>
</el-card>
      <!-- 热门商品区域 -->
      <div v-if="hotProducts.length" class="hot-products-section">
        <h3 class="hot-title">
          <el-icon><Star /></el-icon>
          热门商品
        </h3>
        <div class="hot-products-list">
          <el-card
            v-for="product in hotProducts"
            :key="product.id"
            class="hot-product-card"
            shadow="hover"
          >
            <img
              :src="getProductImage(product)"
              class="hot-product-img"
              :alt="product.name"
              @error="onProductImgError"
              style="
                width: 150px;
                height: 100px;
                object-fit: cover;
                border-radius: 8px;
              "
            />
            <div class="hot-product-info">
              <div class="hot-product-name">{{ product.name }}</div>
              <div class="hot-product-desc">{{ product.description }}</div>
              <div class="hot-product-price">¥{{ product.price }}</div>
              <div class="hot-product-rating">
                <el-rate
                  v-if="product.rating !== undefined && product.rating !== null"
                  :model-value="product.rating"
                  disabled
                  show-score
                  style="font-size: 14px"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
      <!-- 商品列表 -->
      <div class="content-wrapper">
        <div class="product-list">
          <el-card
  v-for="product in products"
  :key="product.id"
  class="product-card"
  :style="product.status !== 1 ? 'opacity: 0.5; pointer-events: auto;' : ''"
>
  <div class="product-info">
    <img
      :src="getProductImage(product)"
      class="product-image"
      :alt="product.name"
      @error="onProductImgError"
    />
    <div class="product-detail">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-desc">{{ product.description }}</p>
      <div class="product-price">
        <span class="price">¥{{ product.price }}</span>
        <el-button
          type="primary"
          size="small"
          :disabled="product.status !== 1"
          :plain="product.status !== 1"
          style="margin-right: 8px"
          @click="addToCart(product)"
        >
          {{ product.status !== 1 ? "商品已下架" : "加入购物车" }}
        </el-button>
      </div>
      <el-button
        type="info"
        size="small"
        @click="showProductDetail(product)"
        style="margin-left: 8px"
      >
        查看详情
      </el-button>
      <el-rate
        v-if="product.rating !== undefined && product.rating !== null"
        :model-value="product.rating"
        disabled
        show-score
        style="font-size: 14px"
      />
    </div>
  </div>
</el-card>
        </div>
      </div>
      <el-dialog
        v-model="productDetailDialogVisible"
        :title="selectedProduct?.name || '商品详情'"
        width="400px"
        :close-on-click-modal="true"
      >
        <div v-if="selectedProduct">
          <img
            :src="getProductImage(selectedProduct)"
            alt="商品图片"
            style="
              width: 100%;
              height: 180px;
              object-fit: cover;
              border-radius: 8px;
              margin-bottom: 12px;
            "
            @error="onProductImgError"
          />
          <div style="font-size: 16px; font-weight: bold; margin-bottom: 8px">
            {{ selectedProduct.name }}
          </div>
          <div style="color: #888; margin-bottom: 8px">
            {{ selectedProduct.description }}
          </div>
          <div style="margin-bottom: 8px">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold">
              ¥{{ selectedProduct.price }}
            </span>
          </div>
          <div>
            <el-rate
              v-if="
                selectedProduct.rating !== undefined &&
                selectedProduct.rating !== null
              "
              :model-value="selectedProduct.rating"
              disabled
              show-score
              style="font-size: 18px"
            />
          </div>
          <div style="margin-top: 16px">
            <h4>商品评价</h4>
            <el-empty
              v-if="
                !selectedProduct.reviews || selectedProduct.reviews.length === 0
              "
              description="暂无评价"
            />
            <el-timeline v-else>
              <el-timeline-item
                v-for="review in selectedProduct.reviews"
                :key="review.id"
                :timestamp="review.created_at?.slice(0, 16)"
              >
                <div style="display: flex; align-items: center; gap: 8px">
                  <el-rate
                    :model-value="review.rating"
                    disabled
                    style="font-size: 14px"
                  />
                  <span style="color: #888; font-size: 13px">{{
                    review.comment
                  }}</span>
                </div>
                <div v-if="review.image" style="margin-top: 4px">
                  <el-image
                    :src="getReviewImage(review)"
                    style="width: 60px; height: 60px; border-radius: 4px"
                  />
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-dialog>
      <!-- 购物车 -->
      <el-drawer
        v-model="cartVisible"
        title="购物车"
        direction="rtl"
        size="300px"
      >
        <div class="cart-content">
          <div v-if="cartItems.length === 0" class="empty-cart">
            购物车是空的
          </div>
          <template v-else>
            <div class="cart-items">
              <div v-for="item in cartItems" :key="item.id" class="cart-item">
                <div class="item-info">
                  <h4>{{ item.name }}</h4>
                  <p class="price">¥{{ item.price?.toFixed(2) }}</p>
                </div>
                <div class="item-actions">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    size="small"
                    @change="updateCart"
                  />
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeFromCart(item)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
            <div class="cart-footer">
              <div class="total">总计: ¥{{ totalPrice?.toFixed(2) }}</div>
              <el-button
                type="primary"
                :disabled="cartItems.length === 0"
                @click="showPayDialog = true"
              >
                提交订单
              </el-button>
            </div>
          </template>
        </div>
      </el-drawer>
      <!-- 支付弹窗 -->
      <el-dialog
        v-model="showPayDialog"
        title="订单支付"
        width="500px"
        :close-on-click-modal="false"
      >
        <div class="order-summary">
          <el-table :data="cartItems" border size="small">
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
          <div class="order-info">
            <div>店铺：{{ shop.name }}</div>
            <div>配送费：¥{{ deliveryPrice }}</div>
            <div>
              <el-select
  v-model="selectedCouponId"
  placeholder="请选择优惠券"
  clearable
  style="width: 220px; margin-bottom: 8px"
  @change="recalcOrderTotal"
  :value-key="'coupon_id'"
>
  <el-option
    v-for="coupon in availableMyCoupons"
    :key="coupon.coupon_id"
    :label="couponLabel(coupon)"
    :value="coupon.coupon_id"
  />
</el-select>
            </div>
            <div class="order-total">
              应付总额：<b>¥{{ orderTotal.toFixed(2) }}</b>
            </div>
          </div>
        </div>
        <template #footer>
          <el-button @click="showPayDialog = false">取消</el-button>
          <el-button type="primary" :loading="paying" @click="handlePay"
            >支付</el-button
          >
        </template>
      </el-dialog>
      <el-button
        class="cart-float-btn"
        type="primary"
        circle
        size="large"
        @click="cartVisible = true"
      >
        <el-icon><ShoppingCart /></el-icon>
      </el-button>
    </div>
  </app-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { userApi } from "@/api/user";
import { ShoppingCart } from "@element-plus/icons-vue";
import { Star } from "@element-plus/icons-vue";
import { Location } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";
const isFavorite = ref(false);
const getStoreImage = (shop: any) => {
  if (!shop.image) return "/logo.png";
  return shop.image.startsWith("http")
    ? shop.image
    : "http://localhost:8080/images/" + shop.image;
};
const hotProducts = ref<any[]>([]);

const defaultAvatar = "/logo.png";
const defaultProductImg = "/logo.png";
const route = useRoute();
const router = useRouter();
const shopId = computed(() => Number(route.params.id));

const coupons = ref<any[]>([]);
const mergedCoupons = ref<any[]>([]);
const myCoupons = ref<any[]>([]);
const selectedCouponId = ref<number | null>(null);

const shop = ref<any>({});
const products = ref<any[]>([]);
const cartVisible = ref(false);
const cartItems = ref<
  Array<{ id: number; name: string; price: number; quantity: number }>
>([]);
const getRandomDeliveryPrice = () => {
  return +(Math.random() * 5 + 5).toFixed(2); // 5~10，保留两位小数
};
const showPayDialog = ref(false);
const paying = ref(false);
const deliveryPrice = ref(getRandomDeliveryPrice());
const getProductImage = (product: any) => {
  if (!product.image) return defaultProductImg;
  return product.image.startsWith("http")
    ? product.image
    : "http://localhost:8080/images/" + product.image;
};
// 店铺图片加载失败
const onShopImgError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  target.src = defaultAvatar;
};

// 商品图片加载失败
const onProductImgError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  target.src = defaultProductImg;
};

const productDetailDialogVisible = ref(false);
const selectedProduct = ref<any>(null);

const showProductDetail = async (product: any) => {
  try {
    const res = await userApi.getProductDetail({ id: product.id });
    if (res.success && res.data) {
      selectedProduct.value = res.data;
      productDetailDialogVisible.value = true;
    } else {
      ElMessage.error(res.message || "获取商品详情失败");
    }
  } catch (e) {
    console.error("获取商品详情失败:", e);
  }
};
const getReviewImage = (review: any) => {
  if (!review.image) return "";
  return review.image.startsWith("http")
    ? review.image
    : "http://localhost:8080/images/" + review.image;
};
// 加载商家信息
const loadShopDetail = async () => {
  try {
    console.log("加载商家信息，店铺ID:", shopId.value);
    const res = await userApi.getStoreDetail({ id: shopId.value });
    console.log("商家信息:", res);
    if (res.success) {
      shop.value =
        res.data ||
        (res.data.data || []).find((s: any) => s.id === shopId.value);
    } else {
      console.error("获取商家信息失败:", res.message);
    }
  } catch (error) {
    console.error("获取商家信息失败:", error);
  }
};

// 加载商品列表
const loadProducts = async () => {
  try {
    const params = {
      store_id: shopId.value,
    };
    const res = await userApi.getProductList(params);
    if (res.success) {
      products.value = res.data;
      console.log("return value:", res);
      console.log("商品:", products);
    } else {
      products.value = [];
      console.error("获取商品列表失败:", res.message);
    }
  } catch (error) {
    console.error("获取商品列表失败:", error);
  }
};
const loadHotProducts = async () => {
  try {
    const params = { store_id: shopId.value };
    const res = await userApi.getProductList(params);
    let productList: any[] = [];
    if (res.success && Array.isArray(res.data)) {
      productList = res.data;
    } else if (res.success && res.data && Array.isArray(res.data.data)) {
      productList = res.data.data;
    }
    // 只取上架商品，按评分降序，取前三
    hotProducts.value = productList
      .filter((p: any) => p.status === 1)
      .sort((a: any, b: any) => (b.rating ?? 0) - (a.rating ?? 0))
      .slice(0, 3);
  } catch (e) {
    console.error("获取热门商品失败:", e);
    hotProducts.value = [];
  }
};
// 添加到购物车
const addToCart = (product: any) => {
  if (product.status !== 1) {
    ElMessage.warning("该商品已下架，无法购买");
    return;
  }
  const existing = cartItems.value.find((item) => item.id === product.id);
  if (existing) {
    existing.quantity++;
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
    });
  }
  cartVisible.value = true;
  ElMessage.success("已添加到购物车");
};

// 从购物车移除
const removeFromCart = (item: any) => {
  const idx = cartItems.value.findIndex((i) => i.id === item.id);
  if (idx > -1) cartItems.value.splice(idx, 1);
};

// 更新购物车（可扩展为同步到后端）
const updateCart = () => {
  // 这里只做本地更新
};

// 计算总价
const totalPrice = computed(() =>
  cartItems.value.reduce((total, item) => total + item.price * item.quantity, 0)
);

// 提交订单
// 下单时带上优惠券id
const handlePay = async () => {
  paying.value = true;
  try {
    const orderData: any = {
      items: cartItems.value.map((item) => ({
        product_id: item.id,
        quantity: item.quantity,
      })),
      store_id: shopId.value,
      delivery_price: deliveryPrice.value,
    };
    if (selectedCouponId.value) {
      orderData.coupon_id = selectedCouponId.value;
    }
    console.log("orderdata",orderData)
    const response = await userApi.submitOrder(orderData);
    if (response.success) {
      ElMessage.success("订单提交成功");
      cartItems.value = [];
      cartVisible.value = false;
      showPayDialog.value = false;
      // 跳转到订单列表并自动打开详情
      router.push({
        path: "/user/history",
        query: { order_id: response.data.order_id },
      });
    } else {
      ElMessage.error(response.message || "订单提交失败");
    }
  } catch (error) {
    console.error("提交订单失败:", error);
    ElMessage.error("提交订单失败");
  } finally {
    paying.value = false;
  }
};

// 每次打开支付弹窗时生成新的配送费
watch(showPayDialog, (val: any) => {
  if (val) {
    deliveryPrice.value = getRandomDeliveryPrice();
  }
});
const checkFavorite = async () => {
  try {
    const res = await userApi.getFavoriteStores({ page: 1, page_size: 10000 });
    if (res.success && Array.isArray(res.data)) {
      isFavorite.value = res.data.some(
        (item: any) =>
          item.store_id === shopId.value || item.id === shopId.value,
        console.log(
          "收藏状态:",
          isFavorite.value,
          "店铺ID:",
          shopId.value,
          "收藏列表:",
          res.data
        )
      );
    }
  } catch (e) {
    console.error("检查收藏状态失败:", e);
  }
};
// 收藏/取消收藏
const toggleFavorite = async () => {
  if (isFavorite.value) {
    // 取消收藏
    try {
      console.log("取消收藏，店铺ID:", shopId.value);
      const res = await userApi.unfavoriteStore(shopId.value);
      console.log("取消收藏结果:", res);
      if (res.success) {
        isFavorite.value = false;
        ElMessage.success("已取消收藏");
      } else {
        ElMessage.error(res.message || "取消收藏失败");
      }
    } catch (e) {
      console.error("取消收藏失败:", e);
    }
  } else {
    // 收藏
    try {
      console.log("收藏店铺，店铺ID:", shopId.value);
      const res = await userApi.favoriteStore({ store_id: shopId.value });
      console.log("收藏结果:", res);
      if (res.success) {
        isFavorite.value = true;
        ElMessage.success("收藏成功");
      } else {
        ElMessage.error(res.message || "收藏失败");
      }
    } catch (e) {
      console.error("收藏失败:", e);
      ElMessage.error("收藏失败");
    }
  }
};
const updateViewHistory = async () => {
  try {
    await userApi.updateViewHistory({
      store_id: shopId.value,
      view_time: new Date().toISOString(),
    });
  } catch (e) {
    console.error("更新浏览记录失败:", e);
  }
};

// 获取店铺所有可领取优惠券
const loadCoupons = async () => {
  try {
    console.log("加载店铺优惠券，店铺ID:", shopId.value);
    console.log("shopId.value 类型和值：", typeof shopId.value, shopId.value);
    const res = await userApi.getStoreCoupons( {store_id:shopId.value} );
    console.log("店铺优惠券:", res);
    if (res.success && Array.isArray(res.data)) {
      coupons.value = res.data.filter((c: any) => !c.is_used);
      // 合并同类型同参数的优惠券
      const map = new Map<string, any>();
      for (const c of coupons.value) {
        const key = couponKey(c);
        if (!map.has(key)) {
          map.set(key, { ...c, count: 1, claimed: false });
        } else {
          map.get(key).count++;
        }
      }
      mergedCoupons.value = Array.from(map.values());
    } else {
      coupons.value = [];
      mergedCoupons.value = [];
    }
  } catch (e) {
    console.error("获取优惠券失败:", e);
    coupons.value = [];
    mergedCoupons.value = [];
  }
};

// 获取当前用户已领取的本店铺优惠券
const loadMyCoupons = async () => {
  try {
    console.log("加载我的优惠券，店铺ID:", shopId.value);
    const res = await userApi.getMyCoupons({store_id: shopId.value});
    console.log("我的优惠券:", res);
    if (res.success && Array.isArray(res.data)) {
      myCoupons.value = res.data.filter((c: any) => !c.is_used);
      loadCoupons()
    } else {
      myCoupons.value = [];
    }
  } catch (e) {
    console.error("获取我的优惠券失败:", e);
    myCoupons.value = [];
  }
};

// 合并优惠券的唯一key
const couponKey = (c: any) => {
  if (c.type == 1) return `1-${c.discount}`;
  return `2-${c.full_amount}-${c.reduce_amount}`;
};

// 优惠券标签
const couponLabel = (c: any) => {
  if (c.type == 1) {
    return `${(c.discount * 10).toFixed(1)}折券`;
  } else if (c.type == 2) {
    return `满${c.full_amount}减${c.reduce_amount}`;
  }
  return "未知类型";
};

// 领取优惠券
const handleClaimCoupon = async (coupon: any) => {
  try {
    console.log("优惠券",coupon)
    const res = await userApi.claimCoupon({id:coupon.coupon_id});
    if (res.success) {
      ElMessage.success("领取成功");
      coupon.claimed = true;
      loadMyCoupons();
    } else {
      ElMessage.error(res.message || "领取失败");
    }
  } catch (e) {
    console.error("领取优惠券失败:", e);
    ElMessage.error("领取优惠券失败");
  }
};

// 仅展示未使用的本店铺优惠券
const availableMyCoupons = computed(() =>
  myCoupons.value.filter((c: any) => !c.is_used)
);

// 订单总价（含优惠券）
const orderTotal = ref(0);
// 订单总价计算
const recalcOrderTotal = () => {
  let total = totalPrice.value + deliveryPrice.value;
  if (selectedCouponId.value) {
    const coupon = myCoupons.value.find(
      (c: any) => c.coupon_id === selectedCouponId.value
    );
    if (coupon) {
      if (coupon.type == 1) {
        total = totalPrice.value * coupon.discount + deliveryPrice.value;
      } else if (coupon.type == 2 && totalPrice.value >= coupon.full_amount) {
        total = total - coupon.reduce_amount;
      }
    }
  }
  orderTotal.value = Math.max(total, 0);
};

onMounted(() => {
  loadShopDetail();
  loadProducts();
  loadHotProducts();
  checkFavorite();
  updateViewHistory();
  loadCoupons();
  loadMyCoupons();
});
watch(shopId, () => {
  loadHotProducts();
});
// 每次打开支付弹窗时、选择优惠券时都重新计算
watch([showPayDialog, selectedCouponId, totalPrice, deliveryPrice], () => {
  if (showPayDialog.value) recalcOrderTotal();
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;
.product-detail {
  .product-price {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
}
.product-card[style*="opacity: 0.5"],
.hot-product-card[style*="opacity: 0.5"] {
  filter: grayscale(0.3);
}
.shop-detail-container {
  padding: $spacing-base;

  .shop-info-card {
    margin-bottom: $spacing-base;

    .shop-header {
      .shop-basic {
        margin-bottom: $spacing-base;

        .shop-name {
          margin: 0 0 $spacing-small;
          font-size: $font-size-large;
          color: $text-primary;
        }

        .shop-rating {
          display: flex;
          align-items: center;
          margin-bottom: $spacing-small;

          .monthly-sales {
            margin-left: $spacing-small;
            color: $text-secondary;
            font-size: $font-size-small;
          }
        }

        .shop-tags {
          .tag {
            margin-right: $spacing-small;
          }
        }
      }

      .shop-notice {
        h3 {
          margin: 0 0 $spacing-small;
          font-size: $font-size-base;
          color: $text-primary;
        }

        p {
          margin: 0;
          color: $text-secondary;
          font-size: $font-size-small;
        }
      }
    }
  }

  .content-wrapper {
    display: flex;
    gap: $spacing-base;

    .category-menu {
      width: 200px;
      background-color: $white;
      border-radius: $border-radius-base;

      .category-list {
        border-right: none;
      }
    }

    .product-list {
      flex: 1;
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: $spacing-base;

      .product-card {
        .product-info {
          display: flex;
          gap: $spacing-base;

          .product-image {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: $border-radius-base;
          }

          .product-detail {
            flex: 1;
            display: flex;
            flex-direction: column;

            .product-name {
              margin: 0 0 $spacing-small;
              font-size: $font-size-base;
              color: $text-primary;
            }

            .product-desc {
              margin: 0 0 $spacing-small;
              color: $text-secondary;
              font-size: $font-size-small;
              overflow: hidden;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
            }

            .product-price {
              margin-top: auto;
              display: flex;
              align-items: center;
              justify-content: space-between;

              .price {
                color: $danger-color;
                font-size: $font-size-large;
                font-weight: bold;
              }
            }
          }
        }
      }
    }
  }

  .cart-content {
    height: 100%;
    display: flex;
    flex-direction: column;

    .empty-cart {
      text-align: center;
      color: $text-secondary;
      padding: $spacing-base;
    }

    .cart-items {
      flex: 1;
      overflow-y: auto;
      padding: $spacing-base;

      .cart-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-small 0;
        border-bottom: 1px solid $border-color;

        .item-info {
          h4 {
            margin: 0 0 $spacing-small;
          }

          .price {
            color: $primary-color;
          }
        }

        .item-actions {
          display: flex;
          gap: $spacing-small;
        }
      }
    }

    .cart-footer {
      padding: $spacing-base;
      border-top: 1px solid $border-color;

      .total {
        margin-bottom: $spacing-small;
        font-size: $font-size-large;
        font-weight: bold;
      }
    }
  }
}
</style>
