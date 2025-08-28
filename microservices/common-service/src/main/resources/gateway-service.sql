-- 网关服务数据库初始化脚本
-- 创建网关服务数据库，主要处理订单相关的跨服务协调

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_gateway DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_gateway;

-- 八、订单表 order
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    rider_id BIGINT,
    status INT DEFAULT 0,
    user_location VARCHAR(100),
    store_location VARCHAR(100),
    total_price DECIMAL(10,2),
    actual_price DECIMAL(10,2),
    remark VARCHAR(255),
    delivery_price  DECIMAL(10,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deadline DATETIME DEFAULT CURRENT_TIMESTAMP,
    ended_at DATETIME NULL,
    INDEX (status),
    INDEX (rider_id),
    INDEX (store_id)
) AUTO_INCREMENT=80000001;

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
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, product_id)
) AUTO_INCREMENT=90000001;

-- 十、评价表 review
CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT,
    product_id BIGINT,
    rating INT NOT NULL,
    comment varchar(300),
    image  VARCHAR(511),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=100000001;

-- 十三、消息表 message
CREATE TABLE IF NOT EXISTS message(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(300) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    sender_role VARCHAR(10) NOT NULL,
    receiver_role VARCHAR(10) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=120000001;

-- 插入订单测试数据
INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, delivery_price, remark, ended_at) VALUES
(10000001, 50000001, 30000001, 2, '北京海淀区小区A栋',   '北京海淀区',  66.00,  63.00, 3.00, '尽快送达', NULL),
(10000002, 50000002, 30000002, 1, '上海浦东新区小区B栋',   '上海浦东新区', 45.00,  45.00, 0.00, NULL,          NULL),
(10000003, 50000003, 30000003, 3, '天津河西区小区C栋',     '天津河西区',   28.00,  28.00, 0.00, '少放沙拉汁', '2025-05-17 12:30:00'),
(10000004, 50000004, 30000004, 0, '广州天河区小区D栋',     '广州天河区',   30.00,  30.00, 0.00, NULL,          NULL),
(10000005, 50000005, 30000005, 2, '深圳罗湖区小区E栋',     '深圳罗湖区',   50.00,  47.00, 3.00, '加快配送',    NULL),
(10000006, 50000006, 30000001, 1, '南京鼓楼区小区F栋',     '南京鼓楼区',   35.00,  35.00, 0.00, NULL,          NULL),
(10000007, 50000007, 30000002, 3, '杭州西湖区小区G栋',     '杭州西湖区',   36.00,  36.00, 0.00, '去冰',       '2025-05-15 18:45:00'),
(10000008, 50000008, 30000003, 0, '成都武侯区小区H栋',     '成都武侯区',   58.00,  58.00, 0.00, NULL,          NULL),
(10000009, 50000009, 30000004, 2, '武汉洪山区小区I栋',     '武汉洪山区',   40.00,  37.00, 3.00, '少糖',       NULL),
(10000010, 50000010, 30000005, 1, '西安雁塔区小区J栋',     '西安雁塔区',   60.00,  60.00, 0.00, NULL,          NULL),
(10000011, 50000011, 30000001, 0, '长沙岳麓区小区K栋',     '长沙岳麓区',   32.00,  32.00, 0.00, NULL,          NULL),
(10000012, 50000012, 30000002, 3, '青岛市南区小区L栋',     '青岛市南区',   30.00,  30.00, 0.00, '多放珍珠',  '2025-05-12 14:20:00'),
(10000013, 50000013, 30000003, 2, '大连甘井子区小区M栋',  '大连甘井子区', 48.00,  45.00, 3.00, '送餐电话',  NULL),
(10000014, 50000014, 30000004, 1, '昆明五华区小区N栋',     '昆明五华区',   22.00,  22.00, 0.00, NULL,          NULL),
(10000015, 50000015, 30000005, 0, '沈阳和平区小区O栋',     '沈阳和平区',   50.00,  50.00, 0.00, NULL,          NULL);

-- 插入订单明细测试数据
INSERT INTO order_item (order_id, product_id, quantity) VALUES
-- 订单 80000001
(80000001, 60000001, 1),
(80000001, 60000002, 2),
-- 订单 80000002
(80000002, 60000002, 1),
(80000002, 60000003, 1),
-- 订单 80000003
(80000003, 60000003, 2),
(80000003, 60000004, 1),
-- 订单 80000004
(80000004, 60000004, 1),
(80000004, 60000005, 2),
-- 订单 80000005
(80000005, 60000005, 1),
(80000005, 60000006, 1),
-- 订单 80000006
(80000006, 60000006, 2),
(80000006, 60000007, 1),
-- 订单 80000007
(80000007, 60000007, 1),
(80000007, 60000008, 1),
-- 订单 80000008
(80000008, 60000008, 2),
(80000008, 60000009, 1),
-- 订单 80000009
(80000009, 60000009, 1),
(80000009, 60000010, 1),
-- 订单 80000010
(80000010, 60000010, 2),
(80000010, 60000011, 1),
-- 订单 80000011
(80000011, 60000011, 1),
(80000011, 60000012, 1),
-- 订单 80000012
(80000012, 60000012, 2),
(80000012, 60000013, 1),
-- 订单 80000013
(80000013, 60000013, 1),
(80000013, 60000014, 1),
-- 订单 80000014
(80000014, 60000014, 2),
(80000014, 60000015, 1),
-- 订单 80000015
(80000015, 60000015, 1),
(80000015, 60000001, 1);

-- 插入评价测试数据
INSERT INTO review (user_id, store_id, product_id, rating, comment, image) VALUES
(10000001, 50000001, 60000001, 5, '鸡腿汉堡味道很赞！',    'review1.jpg'),
(10000002, 50000002, 60000002, 4, '蛋糕很新鲜，甜度适中。', NULL),
(10000003, 50000003, 60000003, 5, '沙拉很健康，配送迅速。', 'review3.jpg'),
(10000004, 50000004, 60000004, 4, '拉面面条劲道，非常好吃。', NULL),
(10000005, 50000005, 60000005, 5, '羊肉串烤得恰到好处。',  'review5.jpg'),
(10000006, 50000006, 60000006, 5, '芝士汉堡浓郁，推荐！',   NULL),
(10000007, 50000007, 60000007, 4, '冰沙果味十足，很清爽。', NULL),
(10000008, 50000008, 60000008, 5, '小龙虾非常鲜美，回味无穷。','review8.jpg'),
(10000009, 50000009, 60000009, 4, '咖啡香醇，杯型漂亮。',   NULL),
(10000010, 50000010, 60000010, 5, '披萨热乎好吃，推荐辣味。', 'review10.jpg'),
(10000011, 50000011, 60000011, 4, '盖饭分量足，味道不错。', NULL),
(10000012, 50000012, 60000012, 5, '奶茶珍珠Q弹，味道超好。', 'review12.jpg'),
(10000013, 50000013, 60000013, 5, '寿司新鲜，鱼肉入口即化。',NULL),
(10000014, 50000014, 60000014, 5, '小面麻辣过瘾，非常好吃。', 'review14.jpg'),
(10000015, 50000015, 60000015, 4, '巧克力蛋糕浓郁但略甜。', NULL);