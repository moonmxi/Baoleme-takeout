-- 商家服务数据库初始化脚本
-- 创建商家服务独立数据库和相关表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS baoleme_merchant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baoleme_merchant;

-- 二、商家表 merchant
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    avatar VARCHAR(511),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=20000001;

-- 五、店铺表 store
CREATE TABLE IF NOT EXISTS store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE ,
    type VARCHAR(10),
    description VARCHAR(50),
    location VARCHAR(100),
    distance  DECIMAL(5,2),
    rating DECIMAL(2,1) DEFAULT 0,
    status TINYINT DEFAULT 0,
    avg_price DECIMAL(10,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    image VARCHAR(511)
) AUTO_INCREMENT=50000001;

-- 六、商品表 product
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    stock int not null,
    rating DECIMAL(2,1),
    status TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    image VARCHAR(511)
) AUTO_INCREMENT=60000001;

-- 七、销量表 sales
CREATE TABLE IF NOT EXISTS sales (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    sale_date DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    payment_method VARCHAR(20),
    customer_id BIGINT,
    INDEX (product_id),
    INDEX (store_id),
    INDEX (sale_date)
) AUTO_INCREMENT=70000001;

-- 创建存储过程：计算并更新商店的加权均价
DELIMITER $$
DROP PROCEDURE IF EXISTS UpdateStoreWeightedPrice;
CREATE PROCEDURE UpdateStoreWeightedPrice(IN store_id BIGINT)
BEGIN
    DECLARE avg_price DECIMAL(10,2);

    -- 计算前3名热销商品的加权均价
    SET avg_price = COALESCE((
        SELECT SUM(p.price * sale.total_quantity) / SUM(sale.total_quantity)
        FROM (
            SELECT s2.product_id, SUM(s2.quantity) AS total_quantity
            FROM sales s2
            WHERE s2.store_id = store_id
              AND s2.sale_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY s2.product_id
            ORDER BY total_quantity DESC
            LIMIT 3
        ) sale
        JOIN product p ON p.id = sale.product_id
    ), 0);

    -- 更新store表的avgPrice字段
    UPDATE store
    SET avg_price = avg_price
    WHERE id = store_id;
END$$
DELIMITER ;

-- 创建触发器：当sales表更新后更新store表
DELIMITER $$
DROP TRIGGER IF EXISTS sales_update_store_price;
CREATE TRIGGER sales_update_store_price
    AFTER INSERT ON sales
    FOR EACH ROW
BEGIN
    -- 调用存储过程更新对应商店的均价
    CALL UpdateStoreWeightedPrice(NEW.store_id);
END$$
DELIMITER ;

-- 插入商家测试数据
INSERT INTO merchant (username, password, phone, avatar) VALUES
('merchant01', 'mkpass0001', '13900001111', 'merchant1.png'),
('merchant02', 'mkpass0002', '13900002222', 'merchant2.png'),
('merchant03', 'mkpass0003', '13900003333', 'merchant3.png'),
('merchant04', 'mkpass0004', '13900004444', 'merchant4.png'),
('merchant05', 'mkpass0005', '13900005555', 'merchant5.png'),
('merchant06', 'mkpass0006', '13900006666', 'merchant6.png'),
('merchant07', 'mkpass0007', '13900007777', 'merchant7.png'),
('merchant08', 'mkpass0008', '13900008888', 'merchant8.png'),
('merchant09', 'mkpass0009', '13900009999', 'merchant9.png'),
('merchant10', 'mkpass0010', '13900001010', 'merchant10.png'),
('merchant11', 'mkpass0011', '13900001112', 'merchant11.png'),
('merchant12', 'mkpass0012', '13900001212', 'merchant12.png'),
('merchant13', 'mkpass0013', '13900001313', 'merchant13.png'),
('merchant14', 'mkpass0014', '13900001414', 'merchant14.png'),
('merchant15', 'mkpass0015', '13900001515', 'merchant15.png');

-- 插入店铺测试数据
INSERT INTO store (merchant_id, name, type, description, location, rating, status, image) VALUES
(20000001, '快餐小屋01', '快餐', '快捷美味', '北京海淀区', 4.7, 1, 'store1.jpg'),
(20000002, '甜品乐园02', '甜品', '甜品烘焙坊', '上海浦东新区', 4.5, 1, 'store2.jpg'),
(20000003, '清新沙拉03', '饮品', '健康沙拉和果汁', '天津河西区', 4.6, 1, 'store3.jpg'),
(20000004, '面食大师04', '面食', '手工面条', '广州天河区', 4.8, 1, 'store4.jpg'),
(20000005, '烧烤天地05', '烧烤', '夜市烧烤串', '深圳罗湖区', 4.3, 1, 'store5.jpg'),
(20000006, '汉堡王06', '快餐', '经典汉堡', '南京鼓楼区', 4.4, 1, 'store6.jpg'),
(20000007, '果汁清吧07', '饮品', '新鲜果汁', '杭州西湖区', 4.2, 1, 'store7.jpg'),
(20000008, '小龙虾08', '烧烤', '麻辣小龙虾', '成都武侯区', 4.9, 1, 'store8.jpg'),
(20000009, '咖啡时光09', '饮品', '精品咖啡', '武汉洪山区', 4.1, 1, 'store9.jpg'),
(20000010, '比萨之家10', '快餐', '意式披萨', '西安雁塔区', 4.6, 1, 'store10.jpg'),
(20000011, '盖饭达人11', '快餐', '快餐盖浇饭', '长沙岳麓区', 4.5, 1, 'store11.jpg'),
(20000012, '奶茶物语12', '饮品', '特色奶茶', '青岛市南区', 4.3, 1, 'store12.jpg'),
(20000013, '寿司吧13', '快餐', '新鲜寿司', '大连甘井子区', 4.7, 1, 'store13.jpg'),
(20000014, '重庆小面14', '面食', '重庆辣味小面', '昆明五华区', 4.8, 1, 'store14.jpg'),
(20000015, '花样蛋糕15', '甜品', '精致蛋糕店', '沈阳和平区', 4.9, 1, 'store15.jpg');

-- 插入商品测试数据
INSERT INTO product (store_id, name, description, price, category, stock, rating, status, image) VALUES
(50000001, '鸡腿汉堡', '经典鸡腿堡', 22.00, '快餐', 150, 4.7, 1, 'prod1.jpg'),
(50000002, '草莓蛋糕', '新鲜草莓蛋糕', 45.00, '甜品', 80, 4.9, 1, 'prod2.jpg'),
(50000003, '凯撒沙拉', '健康蔬菜沙拉', 28.00, '饮品', 60, 4.5, 1, 'prod3.jpg'),
(50000004, '牛肉拉面', '手工牛肉拉面', 30.00, '面食', 100, 4.8, 1, 'prod4.jpg'),
(50000005, '孜然羊肉串', '经典孜然味', 25.00, '烧烤', 200, 4.6, 1, 'prod5.jpg'),
(50000006, '双层芝士汉堡', '浓郁芝士风味', 35.00, '快餐', 120, 4.8, 1, 'prod6.jpg'),
(50000007, '芒果冰沙', '新鲜芒果制成', 18.00, '饮品', 90, 4.4, 1, 'prod7.jpg'),
(50000008, '蒜香小龙虾', '麻辣蒜香风味', 58.00, '烧烤', 50, 4.9, 1, 'prod8.jpg'),
(50000009, '卡布奇诺', '香醇拿铁', 20.00, '饮品', 70, 4.3, 1, 'prod9.jpg'),
(50000010, '夏威夷披萨', '菠萝火腿披萨', 60.00, '快餐', 40, 4.6, 1, 'prod10.jpg'),
(50000011, '照烧鸡腿盖饭', '香甜照烧酱', 32.00, '快餐', 110, 4.5, 1, 'prod11.jpg'),
(50000012, '珍珠奶茶', '经典黑糖珍珠', 15.00, '饮品', 150, 4.7, 1, 'prod12.jpg'),
(50000013, '三文鱼寿司', '新鲜三文鱼', 48.00, '快餐', 60, 4.8, 1, 'prod13.jpg'),
(50000014, '麻辣小面', '地道重庆风味', 22.00, '面食', 130, 4.9, 1, 'prod14.jpg'),
(50000015, '巧克力蛋糕', '浓郁巧克力味', 50.00, '甜品', 75, 4.9, 1, 'prod15.jpg');

-- 插入销量测试数据
INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, payment_method, customer_id) VALUES
(60000001, 50000001, '2025-05-18',  2, 22.00, '微信支付',   10000001),
(60000002, 50000002, '2025-05-17',  1, 45.00, '支付宝',     10000002),
(60000003, 50000003, '2025-05-16',  3, 28.00, '现金',       10000003),
(60000004, 50000004, '2025-05-15',  1, 30.00, '信用卡',     10000004),
(60000005, 50000005, '2025-05-14',  4, 25.00, '微信支付',   10000005),
(60000006, 50000006, '2025-05-13',  2, 35.00, '支付宝',     10000006),
(60000007, 50000007, '2025-05-12',  1, 18.00, '现金',       10000007),
(60000008, 50000008, '2025-05-11',  5, 58.00, '微信支付',   10000008),
(60000009, 50000009, '2025-05-10',  2, 20.00, '支付宝',     10000009),
(60000010, 50000010, '2025-05-09',  1, 60.00, '信用卡',     10000010),
(60000011, 50000011, '2025-05-08',  3, 32.00, '微信支付',   10000011),
(60000012, 50000012, '2025-05-07',  2, 15.00, '现金',       10000012),
(60000013, 50000013, '2025-05-06',  1, 48.00, '支付宝',     10000013),
(60000014, 50000014, '2025-05-05',  2, 22.00, '微信支付',   10000014),
(60000015, 50000015, '2025-05-04',  4, 50.00, '信用卡',     10000015);