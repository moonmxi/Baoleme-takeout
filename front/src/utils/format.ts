/**
 * 格式化日期
 * @param date 日期字符串或Date对象
 * @param format 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export const formatDate = (date: string | Date | null | undefined, format = 'YYYY-MM-DD HH:mm:ss'): string => {
  if (!date) return '-'
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return '-'

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化金额
 * @param amount 金额
 * @param decimals 小数位数，默认为2
 * @returns 格式化后的金额字符串
 */
export const formatAmount = (amount: number | null | undefined, decimals = 2): string => {
  if (amount === null || amount === undefined) return '-'
  return amount.toFixed(decimals)
}

/**
 * 格式化手机号
 * @param phone 手机号
 * @returns 格式化后的手机号字符串
 */
export const formatPhone = (phone: string | null | undefined): string => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 格式化订单状态
 * @param status 状态码
 * @returns 状态文本
 */
export const formatOrderStatus = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: '已取消',
    1: '待取餐',
    2: '取餐中',
    3: '已完成'
  }
  return statusMap[status] || '未知状态'
}

/**
 * 格式化订单状态类型
 * @param status 状态码
 * @returns 状态类型（用于el-tag的type属性）
 */
export const formatOrderStatusType = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: 'info',    // 已取消
    1: 'warning', // 待取餐
    2: 'primary', // 取餐中
    3: 'success'  // 已完成
  }
  return statusMap[status] || 'info'
} 