<template>
  <el-card class="promotion-card">
    <div class="promotion-header">
      <h2>发放优惠券</h2>
    </div>
    <el-form :inline="true" style="margin-bottom: 16px;">
      <el-form-item label="选择店铺">
        <el-select
          v-model="selectedStoreId"
          placeholder="请选择店铺"
          style="width: 220px"
          @change="onStoreChange"
        >
          <el-option
            v-for="store in storeList"
            :key="store.id"
            :label="store.name"
            :value="store.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <el-form :model="couponForm" :rules="rules" ref="couponFormRef" label-width="100px" class="promotion-form">
      <el-form-item label="优惠券类型" prop="type">
        <el-radio-group v-model="couponForm.type">
          <el-radio :label="1">折扣券</el-radio>
          <el-radio :label="2">满减券</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="couponForm.type === 1" label="折扣率" prop="discount">
        <el-input-number v-model="couponForm.discount" :min="0.1" :max="0.99" :step="0.01" :precision="2" />
        <span class="form-tip">范围0.1~0.99，0.8表示8折</span>
      </el-form-item>
      <template v-if="couponForm.type === 2">
        <el-form-item label="满减条件" prop="full_amount">
          <el-input-number v-model="couponForm.full_amount" :min="0.01" :step="0.01" :precision="2" />
          <span class="form-tip">订单满多少元可用</span>
        </el-form-item>
        <el-form-item label="减免金额" prop="reduce_amount">
          <el-input-number v-model="couponForm.reduce_amount" :min="0.01" :step="0.01" :precision="2" />
          <span class="form-tip">减免多少元</span>
        </el-form-item>
      </template>
      <el-form-item label="发放数量" prop="count">
        <el-input-number v-model="couponForm.count" :min="1" :max="100" />
        <span class="form-tip">每次最多发放100张</span>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleCreateCoupon" :loading="creating">发放</el-button>
      </el-form-item>
    </el-form>
    <el-alert v-if="successMsg" :title="successMsg" type="success" show-icon style="margin-top: 16px;" />
  </el-card>
</template>

<script setup lang="ts">
import { ref ,onMounted} from 'vue'
import { ElMessage } from 'element-plus'
import { merchantApi } from '@/api/merchant'

const storeList = ref<any[]>([])
const selectedStoreId = ref<number | null>(null)
const couponFormRef = ref()
const creating = ref(false)
const successMsg = ref('')

const couponForm = ref({
  type: 1,           // 1-折扣券 2-满减券
  discount: null as number | null,
  full_amount: null as number | null,
  reduce_amount: null as number | null,
    count: 1 // 新增数量
})

const rules = {
  type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
  discount: [
    { required: true, message: '请输入折扣率', trigger: 'blur', validator: (rule: any, value: any, callback: any) => {
      if (couponForm.value.type === 1 && (value === null || value < 0.1 || value > 0.99)) {
        callback(new Error('折扣率范围0.1~0.99'))
      } else {
        callback()
      }
    }}
  ],
  full_amount: [
    { required: true, message: '请输入满减条件', trigger: 'blur', validator: (rule: any, value: any, callback: any) => {
      if (couponForm.value.type === 2 && (value === null || value <= 0)) {
        callback(new Error('请输入有效的满减条件金额'))
      } else {
        callback()
      }
    }}
  ],
  reduce_amount: [
    { required: true, message: '请输入减免金额', trigger: 'blur', validator: (rule: any, value: any, callback: any) => {
      if (couponForm.value.type === 2 && (value === null || value <= 0)) {
        callback(new Error('请输入有效的减免金额'))
      } else {
        callback()
      }
    }}
  ],
  count: [
    { required: true, message: '请输入发放数量', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '数量范围1~100', trigger: 'blur' }
  ]
}

const handleCreateCoupon = async () => {

  await couponFormRef.value.validate()
  creating.value = true
  successMsg.value = ''
  try {
    const payload: any = {
  store_id: selectedStoreId.value,
  type: couponForm.value.type,
  discount: couponForm.value.type === 1 ? couponForm.value.discount : 1, // 满减券也传1
  full_amount: couponForm.value.type === 2 ? couponForm.value.full_amount : null,
  reduce_amount: couponForm.value.type === 2 ? couponForm.value.reduce_amount : null
}
    let successCount = 0
    console.log('发放优惠券参数:', payload)
    for (let i = 0; i < couponForm.value.count; i++) {
      const res = await merchantApi.createCoupon(payload)
      console.log(`第${i + 1}次发放结果:`, res)
      if (res.success) {
        successCount++
      } else {
        console.error('第' + (i + 1) + '次发放失败:', res.message)
      }
    }
    if (successCount > 0) {
      successMsg.value = `成功发放${successCount}张优惠券`
      ElMessage.success(successMsg.value)
      couponForm.value.discount = null
      couponForm.value.full_amount = null
      couponForm.value.reduce_amount = null
    } else {
      ElMessage.error('全部发放失败')
    }
  } catch (e) {
    console.error('发放优惠券失败:', e)
    ElMessage.error('发放优惠券失败')
  } finally {
    creating.value = false
  }
}
// 获取店铺列表
const loadStoreList = async () => {
  try {
    const res = await merchantApi.getStoreList?.({ page: 1, page_size: 100 })
    if (res && res.success) {
      // 兼容不同字段名
      storeList.value = (res.data.stores || res.data.list || []).map((item: any) => ({
        ...item,
        id: item.id ?? item.store_id,
        name: item.name
      }))
      if (storeList.value.length > 0) {
        selectedStoreId.value = storeList.value[0].id
      }
    }
  } catch (e) {
    console.error('获取店铺列表失败:', e)
  }
}

const onStoreChange = (val: number) => {
  selectedStoreId.value = val
  // 可根据需要加载店铺相关数据
}
onMounted(() => {
  loadStoreList()
})
</script>
<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.promotion-card {
  margin: $spacing-base 0;
  padding: $spacing-base;
  .promotion-header {
    margin-bottom: $spacing-base;
    h2 {
      font-size: $font-size-large;
      color: $primary-color;
    }
  }
  .promotion-form {
    max-width: 400px;
    .form-tip {
      color: $text-secondary;
      font-size: $font-size-small;
      margin-left: 8px;
    }
  }
}
</style>