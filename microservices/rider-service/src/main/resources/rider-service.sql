-- 骑手服务数据库初始化脚本
-- 创建骑手服务独立数据库和相关表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_rider DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_rider;

-- 三、骑手表 rider
CREATE TABLE IF NOT EXISTS rider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    order_status INT DEFAULT 1,
    dispatch_mode INT DEFAULT 1,
    phone VARCHAR(20),
    balance BIGINT,
    avatar VARCHAR(511),
    INDEX (order_status),
    INDEX (dispatch_mode),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=30000001;

-- 插入骑手测试数据
INSERT INTO rider (username, password, order_status, dispatch_mode, phone, balance, avatar) VALUES
('rider01', 'rdpass0001', 1, 1, '15000001111', 1200, 'rider1.jpg'),
('rider02', 'rdpass0002', 1, 2, '15000002222',  800, 'rider2.jpg'),
('rider03', 'rdpass0003', 0, 1, '15000003333',  500, 'rider3.jpg'),
('rider04', 'rdpass0004', 1, 1, '15000004444', 1500, 'rider4.jpg'),
('rider05', 'rdpass0005', 0, 2, '15000005555',  300, 'rider5.jpg'),
('rider06', 'rdpass0006', 1, 1, '15000006666', 2000, 'rider6.jpg'),
('rider07', 'rdpass0007', 1, 2, '15000007777',  950, 'rider7.jpg'),
('rider08', 'rdpass0008', 0, 1, '15000008888',  400, 'rider8.jpg'),
('rider09', 'rdpass0009', 1, 1, '15000009999', 1100, 'rider9.jpg'),
('rider10', 'rdpass0010', 1, 2, '15000001010',  750, 'rider10.jpg'),
('rider11', 'rdpass0011', 0, 1, '15000001111',  650, 'rider11.jpg'),
('rider12', 'rdpass0012', 1, 2, '15000001212',  900, 'rider12.jpg'),
('rider13', 'rdpass0013', 1, 1, '15000001313', 1300, 'rider13.jpg'),
('rider14', 'rdpass0014', 0, 2, '15000001414',  550, 'rider14.jpg'),
('rider15', 'rdpass0015', 1, 1, '15000001515', 1750, 'rider15.jpg');