-- 카테고리 테이블에 샘플 데이터 추가
INSERT INTO category (name) VALUES 
('전자제품'),
('의류'),
('식품'),
('도서'),
('가구');

-- 사용자 테이블에 샘플 데이터 추가
INSERT INTO "user" (email, password, name) VALUES 
('kim@example.com', 'password123', '김철수'),
('lee@example.com', 'password456', '이영희'),
('park@example.com', 'password789', '박민수'),
('jung@example.com', 'password101', '정지원'),
('hong@example.com', 'password112', '홍길동');

-- 제품 테이블에 샘플 데이터 추가 (카테고리 ID 참조)
INSERT INTO product (name, price, stock, category_id) VALUES 
('삼성 갤럭시 S23', 1200000, 50, 1),
('맥북 프로 M2', 2500000, 30, 1),
('나이키 운동화', 129000, 100, 2),
('유기농 제주 감귤', 25000, 200, 3),
('해리포터 시리즈', 88000, 80, 4);

-- 주문 테이블에 샘플 데이터 추가 (유저 ID 참조)
INSERT INTO orders (total_price, ordered_at, user_id) VALUES 
(1200000, NOW(), 1),
(2500000, NOW(), 2),
(258000, NOW(), 3),
(75000, NOW(), 4),
(88000, NOW(), 5);

-- 주문 아이템 테이블에 샘플 데이터 추가 (주문 ID, 제품 ID 참조)
INSERT INTO order_item (quantity, item_price, order_id, product_id) VALUES 
(1, 1200000, 1, 1),
(1, 2500000, 2, 2),
(2, 129000, 3, 3),
(3, 25000, 4, 4),
(1, 88000, 5, 5);