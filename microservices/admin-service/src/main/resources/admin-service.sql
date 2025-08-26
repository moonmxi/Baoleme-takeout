-- 管理员服务数据库初始化脚本
-- 创建管理员服务独立数据库和相关表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_admin;

-- 四、管理员表 administrator
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    password VARCHAR(100) NOT NULL
) AUTO_INCREMENT=40000001;

-- 插入管理员测试数据
INSERT INTO admin (password) VALUES
('adminpass001'),
('adminpass002'),
('adminpass003'),
('adminpass004'),
('adminpass005'),
('adminpass006'),
('adminpass007'),
('adminpass008'),
('adminpass009'),
('adminpass010'),
('adminpass011'),
('adminpass012'),
('adminpass013'),
('adminpass014'),
('adminpass015');