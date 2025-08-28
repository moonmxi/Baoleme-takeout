-- 网关服务数据库初始化脚本
-- 创建网关服务数据库，主要处理订单相关的跨服务协调和消息传递

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_gateway DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_gateway;

-- 八、订单表 order
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    store_id BIGINT NOT NULL COMMENT '店铺ID',
    rider_id BIGINT COMMENT '骑手ID',
    status INT DEFAULT 0 COMMENT '订单状态：0-待接单，1-已接单，2-配送中，3-已送达，4-已取消',
    user_location VARCHAR(100) COMMENT '用户地址',
    store_location VARCHAR(100) COMMENT '店铺地址',
    total_price DECIMAL(10,2) COMMENT '订单总价',
    actual_price DECIMAL(10,2) COMMENT '实际支付价格',
    remark VARCHAR(255) COMMENT '订单备注',
    delivery_price DECIMAL(10,2) COMMENT '配送费',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deadline DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '截止时间',
    ended_at DATETIME NULL COMMENT '完成时间',
    INDEX idx_status (status),
    INDEX idx_rider_id (rider_id),
    INDEX idx_store_id (store_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) AUTO_INCREMENT=80000001 COMMENT='订单表';

-- 触发器：订单创建时自动设置deadline为created_at + 45分钟
DROP TRIGGER IF EXISTS order_set_deadline;
DELIMITER $$
CREATE TRIGGER order_set_deadline
    BEFORE INSERT ON `order`
    FOR EACH ROW
BEGIN
    IF NEW.deadline IS NULL THEN
        SET NEW.deadline = DATE_ADD(NEW.created_at, INTERVAL 45 MINUTE);
    END IF;
END$$
DELIMITER ;

-- 九、订单明细表 order_item
CREATE TABLE IF NOT EXISTS order_item (
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (order_id, product_id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) COMMENT='订单明细表';

-- 十、评价表 review
CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    store_id BIGINT NOT NULL COMMENT '店铺ID',
    product_id BIGINT COMMENT '商品ID',
    order_id BIGINT COMMENT '订单ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    comment TEXT COMMENT '评价内容',
    images VARCHAR(1000) COMMENT '评价图片',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_store_id (store_id),
    INDEX idx_product_id (product_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
) AUTO_INCREMENT=100000001 COMMENT='评价表';

-- 十一、购物车表 cart
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
) AUTO_INCREMENT=110000001 COMMENT='购物车表';

-- 十二、优惠券表 coupon
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT COMMENT '店铺ID，NULL表示平台券',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    description VARCHAR(255) COMMENT '优惠券描述',
    discount_type TINYINT NOT NULL DEFAULT 1 COMMENT '优惠类型：1-满减，2-折扣',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '优惠金额或折扣比例',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    max_discount DECIMAL(10,2) COMMENT '最大优惠金额（折扣券）',
    total_count INT NOT NULL DEFAULT 0 COMMENT '发放总数',
    used_count INT NOT NULL DEFAULT 0 COMMENT '已使用数量',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_store_id (store_id),
    INDEX idx_status (status),
    INDEX idx_start_end_time (start_time, end_time)
) AUTO_INCREMENT=120000001 COMMENT='优惠券表';

-- 十三、用户优惠券表 user_coupon
CREATE TABLE IF NOT EXISTS user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    order_id BIGINT COMMENT '使用的订单ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
    received_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    used_at DATETIME COMMENT '使用时间',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    INDEX idx_user_id (user_id),
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_status (status),
    INDEX idx_expired_at (expired_at)
) AUTO_INCREMENT=130000001 COMMENT='用户优惠券表';

-- 十四、消息表 message
CREATE TABLE IF NOT EXISTS message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    sender_role VARCHAR(20) NOT NULL COMMENT '发送者角色',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    receiver_role VARCHAR(20) NOT NULL COMMENT '接收者角色',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) DEFAULT 'text' COMMENT '消息类型',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sender (sender_id, sender_role),
    INDEX idx_receiver (receiver_id, receiver_role),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) AUTO_INCREMENT=140000001 COMMENT='消息表';

-- 十五、数据同步日志表 sync_log
CREATE TABLE IF NOT EXISTS sync_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_type VARCHAR(50) NOT NULL COMMENT '数据类型',
    data_id BIGINT NOT NULL COMMENT '数据ID',
    operation VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE, UPDATE, DELETE',
    source_service VARCHAR(50) NOT NULL COMMENT '源服务',
    target_services VARCHAR(200) COMMENT '目标服务列表',
    sync_status TINYINT DEFAULT 0 COMMENT '同步状态：0-待同步，1-同步中，2-同步成功，3-同步失败',
    error_message TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_data_type_id (data_type, data_id),
    INDEX idx_sync_status (sync_status),
    INDEX idx_created_at (created_at)
) AUTO_INCREMENT=150000001 COMMENT='数据同步日志表';

-- 十六、消息传递日志表 message_log
CREATE TABLE IF NOT EXISTS message_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    channel VARCHAR(100) NOT NULL COMMENT '消息频道',
    message_type VARCHAR(50) NOT NULL COMMENT '消息类型',
    message_content TEXT NOT NULL COMMENT '消息内容',
    sender_service VARCHAR(50) NOT NULL COMMENT '发送服务',
    receiver_services VARCHAR(200) COMMENT '接收服务列表',
    send_status TINYINT DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送中，2-发送成功，3-发送失败',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_channel (channel),
    INDEX idx_message_type (message_type),
    INDEX idx_send_status (send_status),
    INDEX idx_created_at (created_at)
) AUTO_INCREMENT=160000001 COMMENT='消息传递日志表';

-- 插入测试数据

-- 插入订单测试数据
INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, delivery_price, remark) VALUES
(10000001, 20000001, 30000001, 3, '北京市朝阳区建国路1号', '北京市朝阳区建国路2号', 58.50, 53.50, 5.00, '不要辣'),
(10000002, 20000002, 30000002, 2, '上海市浦东新区陆家嘴1号', '上海市浦东新区陆家嘴2号', 45.00, 45.00, 6.00, '多加醋'),
(10000003, 20000003, NULL, 1, '广州市天河区珠江新城1号', '广州市天河区珠江新城2号', 32.00, 30.00, 4.00, ''),
(10000004, 20000004, NULL, 0, '深圳市南山区科技园1号', '深圳市南山区科技园2号', 67.80, 67.80, 8.00, '加冰'),
(10000005, 20000005, 30000003, 3, '杭州市西湖区文三路1号', '杭州市西湖区文三路2号', 89.90, 85.90, 7.00, '少糖');

-- 插入订单明细测试数据
INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) VALUES
(80000001, 60000001, 2, 25.00, 50.00),
(80000001, 60000002, 1, 8.50, 8.50),
(80000002, 60000003, 1, 39.00, 39.00),
(80000002, 60000004, 1, 6.00, 6.00),
(80000003, 60000005, 1, 28.00, 28.00),
(80000004, 60000006, 2, 29.90, 59.80),
(80000004, 60000007, 1, 8.00, 8.00),
(80000005, 60000008, 1, 45.90, 45.90),
(80000005, 60000009, 2, 22.00, 44.00);

-- 插入评价测试数据
INSERT INTO review (user_id, store_id, product_id, order_id, rating, comment, images) VALUES
(10000001, 20000001, 60000001, 80000001, 5, '非常好吃，下次还会再来！', 'review1.jpg'),
(10000002, 20000002, 60000003, 80000002, 4, '味道不错，就是有点咸', 'review2.jpg'),
(10000005, 20000005, 60000008, 80000005, 5, '超级棒！强烈推荐！', 'review3.jpg,review4.jpg'),
(10000001, 20000003, 60000005, 80000003, 3, '一般般，没有想象中好吃', ''),
(10000004, 20000004, 60000006, 80000004, 4, '还可以，性价比不错', 'review5.jpg');

-- 插入购物车测试数据
INSERT INTO cart (user_id, product_id, quantity) VALUES
(10000001, 60000010, 2),
(10000001, 60000011, 1),
(10000002, 60000012, 3),
(10000003, 60000013, 1),
(10000004, 60000014, 2),
(10000005, 60000015, 1);

-- 插入优惠券测试数据
INSERT INTO coupon (store_id, name, description, discount_type, discount_value, min_amount, max_discount, total_count, used_count, start_time, end_time, status) VALUES
(20000001, '满50减10', '满50元减10元优惠券', 1, 10.00, 50.00, NULL, 1000, 150, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(20000002, '8折优惠', '全场8折优惠券', 2, 0.80, 30.00, 20.00, 500, 80, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(NULL, '新用户专享', '新用户注册送15元券', 1, 15.00, 30.00, NULL, 10000, 2500, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(20000003, '满100减25', '满100元减25元优惠券', 1, 25.00, 100.00, NULL, 200, 45, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(20000004, '9折优惠', '全场9折优惠券', 2, 0.90, 20.00, 15.00, 800, 120, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1);

-- 插入用户优惠券测试数据
INSERT INTO user_coupon (user_id, coupon_id, order_id, status, received_at, used_at, expired_at) VALUES
(10000001, 120000001, 80000001, 1, '2024-01-15 10:00:00', '2024-01-20 12:30:00', '2024-12-31 23:59:59'),
(10000002, 120000002, NULL, 0, '2024-01-16 11:00:00', NULL, '2024-12-31 23:59:59'),
(10000003, 120000003, NULL, 0, '2024-01-17 12:00:00', NULL, '2024-12-31 23:59:59'),
(10000004, 120000004, NULL, 0, '2024-01-18 13:00:00', NULL, '2024-12-31 23:59:59'),
(10000005, 120000005, 80000005, 1, '2024-01-19 14:00:00', '2024-01-25 18:45:00', '2024-12-31 23:59:59');

-- 插入消息测试数据
INSERT INTO message (sender_id, sender_role, receiver_id, receiver_role, content, message_type, is_read) VALUES
(10000001, 'user', 20000001, 'merchant', '您好，请问这个商品还有库存吗？', 'text', 1),
(20000001, 'merchant', 10000001, 'user', '您好，目前还有库存，欢迎下单！', 'text', 1),
(10000002, 'user', 30000001, 'rider', '骑手师傅，我在楼下等您', 'text', 1),
(30000001, 'rider', 10000002, 'user', '好的，马上到！', 'text', 0),
(40000001, 'admin', 20000002, 'merchant', '您的店铺评分较低，请注意改善服务质量', 'text', 0);

-- 插入数据同步日志测试数据
INSERT INTO sync_log (data_type, data_id, operation, source_service, target_services, sync_status, error_message, retry_count) VALUES
('user', 10000001, 'UPDATE', 'user-service', 'merchant-service,admin-service', 2, NULL, 0),
('order', 80000001, 'CREATE', 'gateway-service', 'user-service,merchant-service,rider-service', 2, NULL, 0),
('product', 60000001, 'UPDATE', 'merchant-service', 'user-service,admin-service', 1, NULL, 0),
('store', 20000001, 'UPDATE', 'merchant-service', 'user-service,admin-service', 3, 'Connection timeout', 2),
('rider', 30000001, 'CREATE', 'rider-service', 'admin-service', 2, NULL, 0);

-- 插入消息传递日志测试数据
INSERT INTO message_log (channel, message_type, message_content, sender_service, receiver_services, send_status, error_message) VALUES
('data-change:user', 'user-data-sync', '{"userId":10000001,"operation":"update"}', 'user-service', 'merchant-service,admin-service', 2, NULL),
('direct:merchant-service', 'order-notification', '{"orderId":80000001,"status":"created"}', 'gateway-service', 'merchant-service', 2, NULL),
('broadcast:all', 'system-maintenance', '{"message":"系统将于今晚进行维护"}', 'admin-service', 'user-service,merchant-service,rider-service,gateway-service', 2, NULL),
('data-change:product', 'product-data-sync', '{"productId":60000001,"operation":"update"}', 'merchant-service', 'user-service,admin-service', 1, NULL),
('direct:rider-service', 'order-assignment', '{"orderId":80000002,"riderId":30000002}', 'gateway-service', 'rider-service', 3, 'Service unavailable');

-- 创建视图：订单详情视图
CREATE OR REPLACE VIEW order_detail_view AS
SELECT 
    o.id as order_id,
    o.user_id,
    o.store_id,
    o.rider_id,
    o.status,
    o.user_location,
    o.store_location,
    o.total_price,
    o.actual_price,
    o.delivery_price,
    o.remark,
    o.created_at,
    o.deadline,
    o.ended_at,
    GROUP_CONCAT(CONCAT(oi.product_id, ':', oi.quantity, ':', oi.price) SEPARATOR ',') as order_items
FROM `order` o
LEFT JOIN order_item oi ON o.id = oi.order_id
GROUP BY o.id;

-- 创建存储过程：获取用户订单统计
DROP PROCEDURE IF EXISTS GetUserOrderStats;
DELIMITER $$
CREATE PROCEDURE GetUserOrderStats(IN userId BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_orders,
        COUNT(CASE WHEN status = 3 THEN 1 END) as completed_orders,
        COUNT(CASE WHEN status = 4 THEN 1 END) as cancelled_orders,
        COALESCE(SUM(CASE WHEN status = 3 THEN actual_price END), 0) as total_spent,
        COALESCE(AVG(CASE WHEN status = 3 THEN actual_price END), 0) as avg_order_value
    FROM `order`
    WHERE user_id = userId;
END$$
DELIMITER ;

-- 创建存储过程：清理过期数据
DROP PROCEDURE IF EXISTS CleanExpiredData;
DELIMITER $$
CREATE PROCEDURE CleanExpiredData()
BEGIN
    -- 清理过期的用户优惠券
    UPDATE user_coupon 
    SET status = 2 
    WHERE status = 0 AND expired_at < NOW();
    
    -- 清理30天前的消息传递日志
    DELETE FROM message_log 
    WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 清理90天前的数据同步日志
    DELETE FROM sync_log 
    WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY) AND sync_status = 2;
    
    SELECT ROW_COUNT() as cleaned_records;
END$$
DELIMITER ;

-- 创建定时事件：每天清理过期数据
-- SET GLOBAL event_scheduler = ON;
-- DROP EVENT IF EXISTS daily_cleanup;
-- CREATE EVENT daily_cleanup
-- ON SCHEDULE EVERY 1 DAY
-- STARTS CURRENT_TIMESTAMP
-- DO CALL CleanExpiredData();

COMMIT;

-- 数据库初始化完成
SELECT 'Gateway Service Database Initialized Successfully!' as message;