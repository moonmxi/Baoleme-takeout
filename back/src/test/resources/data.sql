-- 测试数据初始化SQL脚本
-- 用于单元测试和集成测试的数据准备
--
-- @author Baoleme Team
-- @version 1.0
-- @since 2025-01-25

-- 清理现有数据（按外键依赖关系的逆序删除）
DELETE FROM sales;
DELETE FROM reviews;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM coupons;
DELETE FROM products;
DELETE FROM stores;
DELETE FROM users;
DELETE FROM merchants;
DELETE FROM riders;
DELETE FROM admins;

-- 插入测试用户数据
INSERT INTO users (id, username, password, phone, email, avatar, created_at, updated_at, deleted) VALUES
(1, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13800138001', 'test1@example.com', 'avatar1.jpg', NOW(), NOW(), 0),
(2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13800138002', 'test2@example.com', 'avatar2.jpg', NOW(), NOW(), 0),
(3, 'testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13800138003', 'test3@example.com', 'avatar3.jpg', NOW(), NOW(), 0);

-- 插入测试商家数据
INSERT INTO merchants (id, username, password, phone, email, avatar, created_at, updated_at, deleted) VALUES
(1, 'merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13900139001', 'merchant1@example.com', 'merchant1.jpg', NOW(), NOW(), 0),
(2, 'merchant2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13900139002', 'merchant2@example.com', 'merchant2.jpg', NOW(), NOW(), 0);

-- 插入测试骑手数据
INSERT INTO riders (id, username, password, phone, email, avatar, order_status, dispatch_mode, created_at, updated_at, deleted) VALUES
(1, 'rider1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13700137001', 'rider1@example.com', 'rider1.jpg', 0, 1, NOW(), NOW(), 0),
(2, 'rider2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13700137002', 'rider2@example.com', 'rider2.jpg', 0, 0, NOW(), NOW(), 0);

-- 插入测试管理员数据
INSERT INTO admins (id, username, password, phone, email, avatar, created_at, updated_at, deleted) VALUES
(1, 'admin1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jS6NNBpgq7Sm', '13600136001', 'admin1@example.com', 'admin1.jpg', NOW(), NOW(), 0);

-- 插入测试店铺数据
INSERT INTO stores (id, name, description, location, image, type, status, merchant_id, created_at, updated_at, deleted) VALUES
(1, '测试餐厅1', '美味的中式餐厅', '北京市朝阳区', 'store1.jpg', '中餐', 1, 1, NOW(), NOW(), 0),
(2, '测试餐厅2', '正宗的川菜馆', '北京市海淀区', 'store2.jpg', '川菜', 1, 1, NOW(), NOW(), 0),
(3, '测试餐厅3', '精致的西餐厅', '北京市西城区', 'store3.jpg', '西餐', 0, 2, NOW(), NOW(), 0);

-- 插入测试商品数据
INSERT INTO products (id, name, description, price, image, category, status, store_id, created_at, updated_at, deleted) VALUES
(1, '宫保鸡丁', '经典川菜，香辣可口', 28.00, 'product1.jpg', '热菜', 1, 1, NOW(), NOW(), 0),
(2, '麻婆豆腐', '麻辣鲜香的经典菜品', 18.00, 'product2.jpg', '热菜', 1, 1, NOW(), NOW(), 0),
(3, '红烧肉', '肥而不腻的传统菜', 35.00, 'product3.jpg', '热菜', 1, 2, NOW(), NOW(), 0),
(4, '西红柿鸡蛋', '家常菜经典', 15.00, 'product4.jpg', '热菜', 0, 2, NOW(), NOW(), 0),
(5, '牛排套餐', '精选牛排配土豆泥', 88.00, 'product5.jpg', '主食', 1, 3, NOW(), NOW(), 0);

-- 插入测试购物车数据
INSERT INTO cart_items (id, user_id, product_id, quantity, created_at, updated_at, deleted) VALUES
(1, 1, 1, 2, NOW(), NOW(), 0),
(2, 1, 2, 1, NOW(), NOW(), 0),
(3, 2, 3, 1, NOW(), NOW(), 0);

-- 插入测试订单数据
INSERT INTO orders (id, user_id, store_id, rider_id, total_amount, status, delivery_address, delivery_phone, order_time, delivery_time, created_at, updated_at, deleted) VALUES
(1, 1, 1, 1, 46.00, 3, '北京市朝阳区测试地址1', '13800138001', NOW(), NULL, NOW(), NOW(), 0),
(2, 2, 2, 2, 35.00, 2, '北京市海淀区测试地址2', '13800138002', NOW(), NULL, NOW(), NOW(), 0),
(3, 3, 1, NULL, 28.00, 1, '北京市西城区测试地址3', '13800138003', NOW(), NULL, NOW(), NOW(), 0);

-- 插入测试订单项数据
INSERT INTO order_items (id, order_id, product_id, quantity, price, created_at, updated_at, deleted) VALUES
(1, 1, 1, 2, 28.00, NOW(), NOW(), 0),
(2, 1, 2, 1, 18.00, NOW(), NOW(), 0),
(3, 2, 3, 1, 35.00, NOW(), NOW(), 0),
(4, 3, 1, 1, 28.00, NOW(), NOW(), 0);

-- 插入测试优惠券数据
INSERT INTO coupons (id, name, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_time, end_time, usage_limit, used_count, status, created_at, updated_at, deleted) VALUES
(1, '新用户优惠券', '新用户专享10元优惠', 1, 10.00, 30.00, 10.00, NOW(), DATEADD('DAY', 30, NOW()), 1000, 0, 1, NOW(), NOW(), 0),
(2, '满减优惠券', '满50减15', 1, 15.00, 50.00, 15.00, NOW(), DATEADD('DAY', 15, NOW()), 500, 10, 1, NOW(), NOW(), 0),
(3, '折扣优惠券', '全场8.5折', 2, 0.85, 20.00, 20.00, NOW(), DATEADD('DAY', 7, NOW()), 200, 50, 0, NOW(), NOW(), 0);

-- 插入测试评价数据
INSERT INTO reviews (id, user_id, store_id, order_id, rating, comment, created_at, updated_at, deleted) VALUES
(1, 1, 1, 1, 5, '菜品很好吃，服务也很棒！', NOW(), NOW(), 0),
(2, 2, 2, 2, 4, '味道不错，就是稍微有点咸', NOW(), NOW(), 0),
(3, 3, 1, 3, 3, '一般般，没有特别惊艳', NOW(), NOW(), 0);

-- 插入测试销售数据
INSERT INTO sales (id, store_id, product_id, quantity, unit_price, total_amount, sale_date, created_at, updated_at, deleted) VALUES
(1, 1, 1, 2, 28.00, 56.00, CURRENT_DATE, NOW(), NOW(), 0),
(2, 1, 2, 1, 18.00, 18.00, CURRENT_DATE, NOW(), NOW(), 0),
(3, 2, 3, 1, 35.00, 35.00, CURRENT_DATE, NOW(), NOW(), 0),
(4, 1, 1, 3, 28.00, 84.00, DATEADD('DAY', -1, CURRENT_DATE), NOW(), NOW(), 0),
(5, 2, 3, 2, 35.00, 70.00, DATEADD('DAY', -1, CURRENT_DATE), NOW(), NOW(), 0);

-- 提交事务
COMMIT;