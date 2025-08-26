-- 用户服务数据库初始化脚本
-- 创建用户服务独立数据库和相关表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_user;

-- 一、用户表 user
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    description VARCHAR(100),
    location VARCHAR(100),
    gender varchar(2),
    phone VARCHAR(20),
    avatar VARCHAR(511),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=10000001;

-- 十一、购物车表 cart
CREATE TABLE IF NOT EXISTS cart (
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id)
);

-- 十二、优惠券表 coupon
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    type int NOT NULL,
    discount DECIMAL(5,2) NOT NULL,
    expiration_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_used BOOLEAN DEFAULT FALSE,
    full_amount DECIMAL(10, 2),
    reduce_amount DECIMAL(10, 2)
) AUTO_INCREMENT=110000001;

-- 十四、收藏夹表 favorite
CREATE TABLE IF NOT EXISTS favorite (
    user_id BIGINT NOT NULL,
    product_id BIGINT,
    store_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 十五、浏览记录表
CREATE TABLE IF NOT EXISTS browse_history (
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uc_user_store (user_id, store_id)  -- 联合唯一约束
);

-- 插入用户测试数据
INSERT INTO user (username, password, description, location, gender, phone, avatar) VALUES
('user01', 'pwd000001', '美食爱好者', '北京', '女', '13800001111', 'avatar1.jpg'),
('user02', 'pwd000002', '程序员', '上海', '男', '13800002222', 'avatar2.jpg'),
('user03', 'pwd000003', '学生党', '天津', '女', '13800003333', 'avatar3.jpg'),
('user04', 'pwd000004', '上班族', '广州', '男', '13800004444', 'avatar4.jpg'),
('user05', 'pwd000005', '健身达人', '深圳', '女', '13800005555', 'avatar5.jpg'),
('user06', 'pwd000006', '摄影爱好者', '南京', '男', '13800006666', 'avatar6.jpg'),
('user07', 'pwd000007', '游戏玩家', '杭州', '女', '13800007777', 'avatar7.jpg'),
('user08', 'pwd000008', '旅游达人', '成都', '男', '13800008888', 'avatar8.jpg'),
('user09', 'pwd000009', '电影爱好者', '武汉', '女', '13800009999', 'avatar9.jpg'),
('user10', 'pwd000010', '阅读爱好者', '西安', '男', '13800001010', 'avatar10.jpg'),
('user11', 'pwd000011', '学生党', '长沙', '女', '13800001111', 'avatar11.jpg'),
('user12', 'pwd000012', '设计师', '青岛', '男', '13800001212', 'avatar12.jpg'),
('user13', 'pwd000013', '音乐发烧友', '大连', '女', '13800001313', 'avatar13.jpg'),
('user14', 'pwd000014', '科技极客', '昆明', '男', '13800001414', 'avatar14.jpg'),
('user15', 'pwd000015', '美食评论家', '沈阳', '女', '13800001515', 'avatar15.jpg');

-- 插入购物车测试数据
INSERT INTO cart (user_id, product_id, quantity) VALUES
(10000001, 60000001, 1),
(10000002, 60000002, 2),
(10000003, 60000003, 1),
(10000004, 60000004, 1),
(10000005, 60000005, 3),
(10000006, 60000006, 1),
(10000007, 60000007, 2),
(10000008, 60000008, 1),
(10000009, 60000009, 1),
(10000010, 60000010, 2),
(10000011, 60000011, 1),
(10000012, 60000012, 3),
(10000013, 60000013, 1),
(10000014, 60000014, 1),
(10000015, 60000015, 2);

-- 插入优惠券测试数据
INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, full_amount, reduce_amount) VALUES
(10000001, 50000001, 1, 0.90, '2025-12-31 23:59:59', 50.00, 5.00),
(10000002, 50000002, 2, 10.00, '2025-12-31 23:59:59', 100.00, 10.00),
(10000003, 50000003, 1, 0.85, '2025-12-31 23:59:59', 30.00, 3.00),
(10000004, 50000004, 2, 15.00, '2025-12-31 23:59:59', 80.00, 15.00),
(10000005, 50000005, 1, 0.80, '2025-12-31 23:59:59', 60.00, 6.00);