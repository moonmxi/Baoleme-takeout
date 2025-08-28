-- 测试数据初始化SQL脚本
-- 用于网关服务集成测试的基础数据

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    description VARCHAR(100),
    location VARCHAR(100),
    gender VARCHAR(2),
    phone VARCHAR(20),
    avatar VARCHAR(511),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建商家表
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    avatar VARCHAR(511),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建店铺表
CREATE TABLE IF NOT EXISTS store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(10),
    description VARCHAR(50),
    location VARCHAR(100),
    distance DECIMAL(5,2),
    rating DECIMAL(2,1) DEFAULT 0,
    status TINYINT DEFAULT 0,
    avg_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image VARCHAR(511)
);

-- 创建商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    stock INT NOT NULL,
    rating DECIMAL(2,1),
    status TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image VARCHAR(511)
);

-- 创建骑手表
CREATE TABLE IF NOT EXISTS rider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    order_status INT DEFAULT 1,
    dispatch_mode INT DEFAULT 1,
    phone VARCHAR(20),
    balance BIGINT,
    avatar VARCHAR(511),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建管理员表
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    password VARCHAR(100) NOT NULL
);

-- 创建订单表
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
    delivery_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL
);

-- 创建订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, product_id)
);

-- 创建评价表
CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT,
    product_id BIGINT,
    rating INT NOT NULL,
    comment VARCHAR(300),
    image VARCHAR(511),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建购物车表
CREATE TABLE IF NOT EXISTS cart (
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id)
);

-- 创建优惠券表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    type INT NOT NULL,
    discount DECIMAL(5,2) NOT NULL,
    expiration_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_used BOOLEAN DEFAULT FALSE,
    full_amount DECIMAL(10,2),
    reduce_amount DECIMAL(10,2)
);

-- 创建消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(300) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    sender_role VARCHAR(10) NOT NULL,
    receiver_role VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建收藏夹表
CREATE TABLE IF NOT EXISTS favorite (
    user_id BIGINT NOT NULL,
    product_id BIGINT,
    store_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uc_user_store (user_id, store_id)
);

-- 创建销量表
CREATE TABLE IF NOT EXISTS sales (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    sale_date DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2),
    payment_method VARCHAR(20),
    customer_id BIGINT
);

-- 插入测试数据

-- 用户测试数据
INSERT INTO user (username, password, description, location, gender, phone, avatar) VALUES
('test_user_1', 'password123', '测试用户1', '北京市朝阳区', '男', '13800138001', 'avatar1.jpg'),
('test_user_2', 'password123', '测试用户2', '上海市浦东新区', '女', '13800138002', 'avatar2.jpg'),
('test_user_3', 'password123', '测试用户3', '广州市天河区', '男', '13800138003', 'avatar3.jpg'),
('john_doe', 'password123', 'John Doe用户', '深圳市南山区', '男', '13800138004', 'avatar4.jpg'),
('jane_smith', 'password123', 'Jane Smith用户', '杭州市西湖区', '女', '13800138005', 'avatar5.jpg');

-- 商家测试数据
INSERT INTO merchant (username, password, phone, avatar) VALUES
('test_merchant_1', 'merchant123', '13900139001', 'merchant1.png'),
('test_merchant_2', 'merchant123', '13900139002', 'merchant2.png'),
('test_merchant_3', 'merchant123', '13900139003', 'merchant3.png'),
('kfc_merchant', 'merchant123', '13900139004', 'kfc.png'),
('mcdonald_merchant', 'merchant123', '13900139005', 'mcdonald.png');

-- 店铺测试数据
INSERT INTO store (merchant_id, name, type, description, location, rating, status, avg_price, image) VALUES
(1, '测试快餐店1', '快餐', '美味快餐', '北京市朝阳区建国路', 4.5, 1, 25.50, 'store1.jpg'),
(2, '测试甜品店2', '甜品', '精美甜品', '上海市浦东新区陆家嘴', 4.8, 1, 35.00, 'store2.jpg'),
(3, '测试饮品店3', '饮品', '新鲜饮品', '广州市天河区珠江新城', 4.2, 1, 18.00, 'store3.jpg'),
(4, 'KFC测试店', '快餐', '肯德基快餐', '深圳市南山区科技园', 4.6, 1, 30.00, 'kfc.jpg'),
(5, '麦当劳测试店', '快餐', '麦当劳快餐', '杭州市西湖区文三路', 4.4, 1, 28.00, 'mcdonald.jpg');

-- 商品测试数据
INSERT INTO product (store_id, name, description, price, category, stock, rating, status, image) VALUES
(1, '汉堡套餐', '经典汉堡套餐', 25.50, '快餐', 100, 4.5, 1, 'burger.jpg'),
(1, '炸鸡块', '香脆炸鸡块', 18.00, '快餐', 80, 4.3, 1, 'chicken.jpg'),
(2, '巧克力蛋糕', '浓郁巧克力蛋糕', 35.00, '甜品', 50, 4.8, 1, 'cake.jpg'),
(2, '提拉米苏', '意式提拉米苏', 42.00, '甜品', 30, 4.9, 1, 'tiramisu.jpg'),
(3, '珍珠奶茶', '经典珍珠奶茶', 18.00, '饮品', 200, 4.2, 1, 'milk_tea.jpg'),
(3, '鲜榨橙汁', '新鲜橙汁', 15.00, '饮品', 150, 4.0, 1, 'orange_juice.jpg'),
(4, '原味鸡', 'KFC原味鸡', 12.00, '快餐', 120, 4.6, 1, 'original_chicken.jpg'),
(5, '巨无霸', '麦当劳巨无霸', 22.00, '快餐', 90, 4.4, 1, 'big_mac.jpg');

-- 骑手测试数据
INSERT INTO rider (username, password, order_status, dispatch_mode, phone, balance, avatar) VALUES
('test_rider_1', 'rider123', 1, 1, '15000150001', 1000, 'rider1.jpg'),
('test_rider_2', 'rider123', 1, 2, '15000150002', 800, 'rider2.jpg'),
('test_rider_3', 'rider123', 0, 1, '15000150003', 1200, 'rider3.jpg'),
('speed_rider', 'rider123', 1, 1, '15000150004', 1500, 'speed_rider.jpg'),
('safe_rider', 'rider123', 1, 2, '15000150005', 900, 'safe_rider.jpg');

-- 管理员测试数据
INSERT INTO admin (password) VALUES
('admin123'),
('admin456'),
('admin789');

-- 订单测试数据
INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, delivery_price, remark) VALUES
(1, 1, 1, 2, '北京市朝阳区某小区', '北京市朝阳区建国路', 28.50, 25.50, 3.00, '请尽快送达'),
(2, 2, 2, 1, '上海市浦东新区某办公楼', '上海市浦东新区陆家嘴', 35.00, 35.00, 0.00, ''),
(3, 3, 3, 3, '广州市天河区某商场', '广州市天河区珠江新城', 21.00, 18.00, 3.00, '少冰'),
(4, 4, 1, 0, '深圳市南山区某住宅', '深圳市南山区科技园', 15.00, 12.00, 3.00, ''),
(5, 5, 2, 2, '杭州市西湖区某学校', '杭州市西湖区文三路', 25.00, 22.00, 3.00, '送到门卫室');

-- 订单明细测试数据
INSERT INTO order_item (order_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 1),
(2, 3, 1),
(3, 5, 1),
(4, 7, 1),
(5, 8, 1);

-- 评价测试数据
INSERT INTO review (user_id, store_id, product_id, rating, comment, image) VALUES
(1, 1, 1, 5, '汉堡很好吃，服务也不错！', 'review1.jpg'),
(2, 2, 3, 4, '蛋糕味道不错，就是有点甜。', ''),
(3, 3, 5, 4, '奶茶口感很好，珍珠很Q弹。', 'review3.jpg'),
(4, 4, 7, 5, 'KFC的鸡肉还是很香的！', ''),
(5, 5, 8, 4, '巨无霸分量足，味道也可以。', 'review5.jpg');

-- 购物车测试数据
INSERT INTO cart (user_id, product_id, quantity) VALUES
(1, 2, 2),
(1, 3, 1),
(2, 5, 3),
(3, 7, 1),
(4, 8, 2);

-- 优惠券测试数据
INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, is_used, full_amount, reduce_amount) VALUES
(1, 1, 1, 0.90, '2025-12-31 23:59:59', FALSE, 50.00, 5.00),
(2, 2, 2, 0.80, '2025-12-31 23:59:59', FALSE, 100.00, 20.00),
(3, 3, 1, 0.85, '2025-12-31 23:59:59', TRUE, 30.00, 3.00),
(4, 4, 2, 0.75, '2025-12-31 23:59:59', FALSE, 80.00, 15.00),
(5, 5, 1, 0.88, '2025-12-31 23:59:59', FALSE, 60.00, 7.00);

-- 消息测试数据
INSERT INTO message (content, sender_id, receiver_id, sender_role, receiver_role) VALUES
('您好，请问今天有什么推荐的吗？', 1, 1, 'user', 'merchant'),
('今天推荐我们的招牌汉堡套餐！', 1, 1, 'merchant', 'user'),
('订单已接收，正在准备中...', 1, 1, 'rider', 'user'),
('请问大概多久能送到？', 2, 2, 'user', 'rider'),
('预计15分钟内送达，请耐心等待。', 2, 2, 'rider', 'user');

-- 收藏夹测试数据
INSERT INTO favorite (user_id, product_id, store_id) VALUES
(1, 1, NULL),
(1, NULL, 2),
(2, 3, NULL),
(3, NULL, 3),
(4, 7, NULL);

-- 浏览历史测试数据
INSERT INTO browse_history (user_id, store_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1),
(3, 3),
(4, 4),
(5, 5);

-- 销量测试数据
INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, total_amount, payment_method, customer_id) VALUES
(1, 1, '2025-01-20', 2, 25.50, 51.00, '微信支付', 1),
(2, 1, '2025-01-20', 1, 18.00, 18.00, '支付宝', 1),
(3, 2, '2025-01-21', 1, 35.00, 35.00, '现金', 2),
(5, 3, '2025-01-21', 1, 18.00, 18.00, '微信支付', 3),
(7, 4, '2025-01-22', 1, 12.00, 12.00, '支付宝', 4),
(8, 5, '2025-01-22', 1, 22.00, 22.00, '信用卡', 5);

-- 提交事务
COMMIT;