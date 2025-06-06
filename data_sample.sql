INSERT INTO suppliers (supplier_id, supplier_name, address, representative_name, contact_phone) VALUES
(4,'Cong ty TNHH Minh Long', '123 Duong So 1, Quan 5, TP.HCM', 'Nguyen Van An', '0909123456'),
(5,'CTCP Thien Phu', '456 Tran Hung Dao, Quan 1, TP.HCM', 'Tran Thi Binh', '0911223344'),
(6,'Cong ty TNHH Duoc pham Hoa Binh', '789 Pham Van Dong, Thu Duc, TP.HCM', 'Le Hoang', '0933445566'),
(7,'CTCP San Xuat Bao Bi An Phat', '22 Nguyen Trai, Ha Noi', 'Pham Minh Tuan', '0977554433'),
(8,'Cong ty TNHH Kim Ngan', '100 Le Lai, Quan 1, TP.HCM', 'Do Thi Lan', '0909777888'),
(9,'Cong ty TNHH Bao Chau', '15A Cach Mang Thang Tam, Da Nang', 'Nguyen Thi Hoa', '0966112233'),
(10,'CTCP Cong nghe Xanh', '98 Vo Thi Sau, Vung Tau', 'Tran Quang Huy', '0944556677'),
(11,'Cong ty TNHH Minh Chau', '88 Nguyen Van Linh, Da Nang', 'Nguyen Van B', '0933221100'),
(12,'CTCP TM Quoc Te Hoang Gia', '27 Ly Thai To, Ha Noi', 'Pham Thi Dung', '0919988776'),
(13,'Cong ty TNHH Nguyen Hieu', '65 Le Duan, TP.HCM', 'Nguyen Hieu', '0909111222'),
(14,'CTCP Dau Tu Nam Viet', '40 Hoang Hoa Tham, Ha Noi', 'Le Thi Mai', '0977443322'),
(15,'Cong ty TNHH Sao Mai', '31 Tran Phu, Nha Trang', 'Hoang Van Tuan', '0933666999'),
(16,'CTCP Phat Trien Phuong Nam', '21 Bach Dang, Can Tho', 'Tran Van Nam', '0955778899'),
(17,'Cong ty TNHH Thien Ha', '17 Vo Van Tan, Hue', 'Doan Thi Thu', '0966332211'),
(18,'CTCP Dich Vu An Toan', '11A Nguyen Thi Minh Khai, TP.HCM', 'Hoang Ngoc Bao', '0988776655'),
(19,'Cong ty TNHH Long Phat', '59 Phan Dinh Phung, Da Lat', 'Vo Van Linh', '0922113344'),
(20,'CTCP Quang Cao Sao Viet', '38 Nguyen Hue, TP.HCM', 'Truong Thanh Son', '0911778899'),
(21,'Cong ty TNHH Tam An', '72 Le Loi, Da Nang', 'Ngo Thi Ha', '0933445522'),
(22,'Cong ty TNHH Xuat Nhap Khau Dong A', '44 Hai Ba Trung, Ha Noi', 'Pham Anh Dung', '0944667788'),
(23,'CTCP Cong nghiep Viet Phat', '12 Quang Trung, TP.HCM', 'Le Trung Kien', '0909001122');

INSERT INTO employee (employee_id, lastname, firstname, dob, email, gender, address, phone, identity_id) VALUES
(3, 'Le', 'Minh Khoa', '1992-03-15', 'khoa.le@gmail.com', 'Nam', 'Da Nang', '0966123456', '001192000003'),
(4, 'Pham', 'Thi Mai', '1995-07-20', 'mai.pham@gmail.com', 'Nu', 'Can Tho', '0911223344', '001195000004'),
(5, 'Do', 'Thanh Tam', '1991-11-30', 'tam.do@gmail.com', 'Nam', 'Hue', '0933445566', '001191000005'),
(6, 'Hoang', 'Bao Chau', '1993-06-25', 'chau.hoang@gmail.com', 'Nu', 'HCMC', '0909887766', '001193000006'),
(7, 'Nguyen', 'Duy Khanh', '1989-09-12', 'khanh.nguyen@gmail.com', 'Nam', 'Hanoi', '0988776655', '001189000007'),
(8, 'Tran', 'Ngoc Anh', '1996-02-08', 'anh.tran@gmail.com', 'Nu', 'Vung Tau', '0977665544', '001196000008'),
(9, 'Bui', 'Quoc Tuan', '1990-10-05', 'tuan.bui@gmail.com', 'Nam', 'Da Lat', '0955778899', '001190000009'),
(10, 'Vo', 'Kieu Oanh', '1994-04-18', 'oanh.vo@gmail.com', 'Nu', 'Nha Trang', '0966332211', '001194000010'),
(11, 'Dang', 'Van Hieu', '1987-12-01', 'hieu.dang@gmail.com', 'Nam', 'Bac Ninh', '0944667788', '001187000011'),
(12, 'Le', 'Thu Ha', '1998-08-09', 'ha.le@gmail.com', 'Nu', 'Quang Ninh', '0933557799', '001198000012');

INSERT INTO customer (fullname, phone, email) VALUES
('Nguyen Minh Khoa', '0909123456', 'khoa.nguyen@example.com'),
('Tran Thi Bao Chau', '0911223344', 'chau.tran@example.com'),
('Le Hoang Long', '0933445566', 'long.le@example.com'),
('Pham Kim Ngan', '0977554433', 'ngan.pham@example.com'),
('Hoang Gia Bao', '0909777888', 'bao.hoang@example.com'),
('Do Thi Mai', '0966112233', 'mai.do@example.com'),
('Nguyen Thanh Tam', '0944556677', 'tam.nguyen@example.com'),
('Tran Van Kiet', '0933221100', 'kiet.tran@example.com'),
('Pham Thi My Duyen', '0919988776', 'duyen.pham@example.com'),
('Le Nhat Nam', '0909111222', 'nam.le@example.com'),
('Hoang Thi Ngoc Anh', '0977443322', 'anh.hoang@example.com'),
('Vo Minh Tri', '0933666999', 'tri.vo@example.com'),
('Dang Quoc Tuan', '0955778899', 'tuan.dang@example.com'),
('Bui Thi Kieu Oanh', '0966332211', 'oanh.bui@example.com'),
('Nguyen Duy Khanh', '0988776655', 'khanh.nguyen@example.com');

INSERT INTO categories (category_id, category_name) VALUES
(5, 'Rau cu'),
(6, 'Thit tuoi'),
(7, 'Hai san'),
(8, 'Do dong hop'),
(9, 'Banh keo'),
(10, 'San pham tu sua'),
(11, 'Gia vi'),
(12, 'Do dong lanh'),
(13, 'Do chay'),
(14, 'Do an nhanh'),
(15, 'Mi goi'),
(16, 'Do tuoi song'),
(17, 'Tra'),
(18, 'Ca phe'),
(19, 'Do lau'),
(20, 'Nuoc mam'),
(21, 'Do hanh phuc'),
(22, 'Do chuc nang'),
(23, 'San pham huu co'),
(24, 'Do cho be');

INSERT INTO products (product_id, product_name, unit, price_with_tax, quantity_in_stock, category_id) VALUES
(5, 'Ca rot tuoi', 'kg', 12000.00, 150, 5),           -- Rau cu
(6, 'Thit heo say', 'goi', 35000.00, 80, 6),          -- Thit tuoi
(7, 'Tom tuoi song', 'kg', 200000.00, 50, 7),         -- Hai san
(8, 'Ca hop VinaFish', 'hop', 27000.00, 120, 8),      -- Do dong hop
(9, 'Keo dua BenTre', 'goi', 18000.00, 300, 9),       -- Banh keo
(10, 'Sua chua Umi', 'lo', 9000.00, 250, 10),         -- San pham tu sua
(11, 'Muoi tieu chanh', 'goi', 7000.00, 400, 11),     -- Gia vi
(12, 'Ca vien dong lanh', 'goi', 30000.00, 130, 12),  -- Do dong lanh
(13, 'Tofu chay', 'hop', 22000.00, 70, 13),           -- Do chay
(14, 'Burger chay nhanh', 'cai', 40000.00, 60, 14),   -- Do an nhanh
(15, 'Mi goi AnLien', 'goi', 5000.00, 450, 15),       -- Mi goi
(16, 'Rau muong tuoi', 'kg', 10000.00, 90, 16),       -- Do tuoi song
(17, 'Tra xanh tuoi', 'chai', 15000.00, 200, 17),     -- Tra
(18, 'Ca phe rang xay', 'goi', 65000.00, 140, 18),    -- Ca phe
(19, 'Lau thai an lien', 'set', 75000.00, 40, 19);    -- Do lau

INSERT INTO schedule (month, start_day, end_day, start_time, end_time) VALUES
(6, 2, 7, '08:00:00', '15:00:00'),  -- Ca sang, Thu 2 den Thu 7
(6, 3, 8, '15:00:00', '22:00:00'),  -- Ca chieu, Thu 3 den Chu Nhat
(6, 2, 7, '08:00:00', '15:00:00'),
(6, 3, 8, '15:00:00', '22:00:00'),
(7, 2, 7, '08:00:00', '15:00:00'),
(7, 3, 8, '15:00:00', '22:00:00'),
(7, 2, 7, '08:00:00', '15:00:00'),
(7, 3, 8, '15:00:00', '22:00:00'),
(8, 2, 7, '08:00:00', '15:00:00'),
(8, 3, 8, '15:00:00', '22:00:00'),
(8, 2, 7, '08:00:00', '15:00:00'),
(8, 3, 8, '15:00:00', '22:00:00'),
(9, 2, 7, '08:00:00', '15:00:00'),
(9, 3, 8, '15:00:00', '22:00:00'),
(9, 2, 7, '08:00:00', '15:00:00'),
(9, 3, 8, '15:00:00', '22:00:00'),
(10, 2, 7, '08:00:00', '15:00:00'),
(10, 3, 8, '15:00:00', '22:00:00'),
(10, 2, 7, '08:00:00', '15:00:00'),
(10, 3, 8, '15:00:00', '22:00:00');

INSERT INTO working (employee_id, schedule_id, work_date, status) VALUES
-- Nhân viên 1
(1, 1, '2025-05-05', 'D'), -- Thứ 2
(1, 1, '2025-05-06', 'M'), -- Thứ 3
(1, 1, '2025-05-07', 'D'), -- Thứ 4
(1, 1, '2025-05-08', 'V'), -- Thứ 5
(1, 1, '2025-05-09', 'D'), -- Thứ 6
(1, 1, '2025-05-10', 'D'), -- Thứ 7
(1, 1, '2025-05-12', 'D'), -- Thứ 2
(1, 1, '2025-05-13', 'M'),
(1, 1, '2025-05-14', 'D'),
(1, 1, '2025-05-15', 'D'),
(1, 1, '2025-05-16', 'V'),
(1, 1, '2025-05-17', 'D'),

-- Nhân viên 2
(2, 2, '2025-05-06', 'D'), -- Thứ 3
(2, 2, '2025-05-07', 'D'), -- Thứ 4
(2, 2, '2025-05-08', 'M'), -- Thứ 5
(2, 2, '2025-05-09', 'D'), -- Thứ 6
(2, 2, '2025-05-10', 'D'), -- Thứ 7
(2, 2, '2025-05-11', 'V'), -- Chủ nhật
(2, 2, '2025-05-13', 'D'), -- Thứ 3
(2, 2, '2025-05-14', 'D'),
(2, 2, '2025-05-15', 'M'),
(2, 2, '2025-05-16', 'D'),
(2, 2, '2025-05-17', 'D'),
(2, 2, '2025-05-18', 'V'); -- Chủ nhật

INSERT INTO salary (month, basic_salary, workdays, allowance, note, adjustment, leave_pay, actual_salary, employee_id) VALUES
(5, 7000000, 9, 500000, 'Thang 5', 200000, 1000000, 6700000, 1),
(5, 7000000, 8, 400000, 'Thang 5', -100000, 1000000, 6300000, 2);

-- IMPORT_ID 3, total 1,800,000
INSERT INTO batch (batch_id, import_date, expiration_date, total_quantity, quantity_in_stock, product_id, supplier_id, import_id, value_batch) VALUES
(1, '2025-06-03', '2025-09-03', 200, 200, 1, 4, 3, 900000),
(2, '2025-06-03', '2025-08-03', 300, 300, 2, 5, 3, 900000);

-- IMPORT_ID 4, total 2,200,000
INSERT INTO batch VALUES
(3, '2025-06-04', '2025-09-04', 150, 150, 3, 6, 4, 1200000),
(4, '2025-06-04', '2025-10-04', 200, 200, 4, 7, 4, 1000000);

-- IMPORT_ID 5, total 2,500,000
INSERT INTO batch VALUES
(5, '2025-06-05', '2025-09-05', 250, 250, 1, 4, 5, 1000000),
(6, '2025-06-05', '2025-10-05', 300, 300, 2, 5, 5, 1500000);

-- IMPORT_ID 6, total 1,700,000
INSERT INTO batch VALUES
(7, '2025-06-06', '2025-08-06', 200, 200, 3, 6, 6, 800000),
(8, '2025-06-06', '2025-09-06', 150, 150, 4, 7, 6, 900000);

-- IMPORT_ID 7, total 2,100,000
INSERT INTO batch VALUES
(9, '2025-06-07', '2025-08-07', 250, 250, 2, 5, 7, 1100000),
(10,'2025-06-07', '2025-11-07', 200, 200, 1, 4, 7, 1000000);

-- IMPORT_ID 8, total 2,300,000
INSERT INTO batch VALUES
(11,'2025-06-08', '2025-09-08', 300, 300, 3, 6, 8, 1200000),
(12,'2025-06-08', '2025-10-08', 300, 300, 4, 7, 8, 1100000);

INSERT INTO incident_reports (incident_id, report_date, report_time, description, batch_id) VALUES
(1, '2025-06-04', '10:30:00', 'Bao bi bi mop khi nhap hang', 2),
(2, '2025-06-06', '09:45:00', 'Hop sua bi vo khi van chuyen', 1);

INSERT INTO orders (order_id, order_date, total_amount, payment_method, customer_id, employee_id) VALUES
(1, '2025-06-01', 250000.00, 'Tien mat', 1, 1),
(2, '2025-06-01', 180000.00, 'Chuyen khoan', 2, 1),
(3, '2025-06-02', 320000.00, 'Tien mat', 3, 2),
(4, '2025-06-02', 150000.00, 'Vi dien tu', 4, 2),
(5, '2025-06-03', 275000.00, 'Chuyen khoan', 5, 1),
(6, '2025-06-03', 330000.00, 'Tien mat', 6, 2),
(7, '2025-06-04', 198000.00, 'Vi dien tu', 7, 1),
(8, '2025-06-04', 400000.00, 'Chuyen khoan', 8, 2),
(9, '2025-06-05', 145000.00, 'Tien mat', 9, 1),
(10, '2025-06-05', 212000.00, 'Vi dien tu', 10, 2),
(11, '2025-06-06', 370000.00, 'Chuyen khoan', 1, 1),
(12, '2025-06-06', 225000.00, 'Tien mat', 2, 2),
(13, '2025-06-06', 180000.00, 'Vi dien tu', 3, 1),
(14, '2025-06-07', 295000.00, 'Tien mat', 4, 2),
(15, '2025-06-07', 310000.00, 'Chuyen khoan', 5, 1),
(16, '2025-06-07', 260000.00, 'Vi dien tu', 6, 2),
(17, '2025-06-08', 150000.00, 'Tien mat', 7, 1),
(18, '2025-06-08', 340000.00, 'Chuyen khoan', 8, 2),
(19, '2025-06-08', 175000.00, 'Vi dien tu', 9, 1),
(20, '2025-06-08', 295000.00, 'Tien mat', 10, 2);

UPDATE orders SET total_amount = 240000 WHERE order_id = 1;
UPDATE orders SET total_amount = 180000 WHERE order_id = 2;
UPDATE orders SET total_amount = 318000 WHERE order_id = 3;
UPDATE orders SET total_amount = 150000 WHERE order_id = 4;
UPDATE orders SET total_amount = 260000 WHERE order_id = 5;
UPDATE orders SET total_amount = 307000 WHERE order_id = 6;
UPDATE orders SET total_amount = 183000 WHERE order_id = 7;
UPDATE orders SET total_amount = 390000 WHERE order_id = 8;
UPDATE orders SET total_amount = 128000 WHERE order_id = 9;
UPDATE orders SET total_amount = 197000 WHERE order_id = 10;

INSERT INTO order_details (order_id, batch_id, quantity) VALUES
(1, 4, 5),   -- 5 x 18000 = 90000
(1, 1, 6),   -- 6 x 25000 = 150000  → Tổng: 240000

(2, 4, 6),   -- 6 x 18000 = 108000
(2, 6, 18),  -- 18 x 4000 = 72000   → Tổng: 180000

(3, 8, 7),   -- 7 x 18000 = 126000
(3, 2, 48),  -- 48 x 4000 = 192000  → Tổng: 318000

(4, 3, 18),  -- 18 x 5000 = 90000
(4, 6, 15),  -- 15 x 4000 = 60000   → Tổng: 150000

(5, 7, 22),  -- 22 x 5000 = 110000
(5, 1, 6),   -- 6 x 25000 = 150000  → Tổng: 260000

(6, 1, 7),   -- 7 x 25000 = 175000
(6, 2, 33),  -- 33 x 4000 = 132000  → Tổng: 307000

(7, 7, 15),  -- 15 x 5000 = 75000
(7, 4, 6),   -- 6 x 18000 = 108000  → Tổng: 183000

(8, 6, 60),  -- 60 x 4000 = 240000
(8, 5, 6),   -- 6 x 25000 = 150000  → Tổng: 390000

(9, 6, 14),  -- 14 x 4000 = 56000
(9, 8, 4),   -- 4 x 18000 = 72000   → Tổng: 128000

(10, 7, 25), -- 25 x 5000 = 125000
(10, 4, 4);  -- 4 x 18000 = 72000   → Tổng: 197000


