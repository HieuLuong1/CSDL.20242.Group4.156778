create table employee (
    employee_id serial primary key,
    lastname varchar(50),
    firstname varchar(50),
    dob date,
    email varchar(100),
    gender varchar(10),
    address text,
    phone varchar(20),
    identity_id varchar(20)
);

create table customer (
    customer_id serial primary key,
    fullname varchar(100),
    phone varchar(20),
    email varchar(100)
);


create table categories (
    category_id serial primary key,
    category_name varchar(100)
);

create table products (
    product_id serial primary key,
    product_name varchar(100),
    unit varchar(20),
    price_with_tax numeric(12,2),
    quantity_in_stock int,
    category_id int
);

create table schedule (
    schedule_id serial primary key,
    month varchar(7),
    start_day varchar(10),
    end_day varchar(10),
    start_time time,
    end_time time
);

create table working (
    employee_id int,
    schedule_id int,
    work_date date,
    status varchar(20),
    primary key (employee_id, schedule_id, work_date)
);

create table salary (
    salary_id serial primary key,
    monthyear varchar(8),
    basic_salary numeric(12,2),
    workdays int,
    allowance numeric(12,2),
    note text,
    adjustment numeric(12,2),
    leave_pay numeric(12,2),
    actual_salary numeric(12,2),
    employee_id int
);

create table suppliers (
    supplier_id serial primary key,
    supplier_name varchar(100),
    address text,
    representative_name varchar(100),
    contact_phone varchar(20)
);

create table import_reports (
    import_id serial primary key,
    import_date date,
    delivery_address text,
    total_amount numeric(12,2),
    employee_id int,
	supplier_id int
);

create table batch (
    batch_id serial primary key,
    import_date date,
    expiration_date date,
    total_quantity int,
    quantity_in_stock int,
    product_id int,
    import_id int,
	value_batch int 
);

create table orders (
    order_id serial primary key,
    order_date date,
    total_amount numeric(12,2),
    payment_method varchar(50),
    customer_id int,
    employee_id int
);

create table order_details (
    order_id int,
    batch_id int,
    quantity int,
    primary key (order_id, batch_id)
);

create table check_reports (
    report_id serial primary key,
    check_date date
);

create table check_details (
    report_id int,
    batch_id int,
    real_quantity int,
    primary key (report_id, batch_id)
);

create table incident_reports (
    incident_id serial primary key,
    report_date date,
    report_time time,
    description text,
    batch_id int
);

alter table products
add constraint fk_products_category
foreign key (category_id) references categories(category_id)
on update cascade on delete set null;

alter table working
add constraint fk_working_employee
foreign key (employee_id) references employee(employee_id)
on update cascade on delete cascade;

alter table working
add constraint fk_working_schedule
foreign key (schedule_id) references schedule(schedule_id)
on update cascade on delete cascade;

alter table salary
add constraint fk_salary_employee
foreign key (employee_id) references employee(employee_id)
on update cascade on delete set null;

alter table import_reports
add constraint fk_import_employee
foreign key (employee_id) references employee(employee_id)
on update cascade on delete set null;

alter table import_reports
add constraint fk_import_supplier 
foreign key (supplier_id) references suppliers(supplier_id)
on update cascade on delete set null;

alter table batch
add constraint fk_batch_product
foreign key (product_id) references products(product_id)
on update cascade on delete set null;

alter table batch
add constraint fk_batch_import
foreign key (import_id) references import_reports(import_id)
on update cascade on delete set null;

alter table orders
add constraint fk_orders_customer
foreign key (customer_id) references customer(customer_id)
on update cascade on delete set null;

alter table orders
add constraint fk_orders_employee
foreign key (employee_id) references employee(employee_id)
on update cascade on delete set null;

alter table order_details
add constraint fk_orderdetails_order
foreign key (order_id) references orders(order_id)
on update cascade on delete cascade;


alter table order_details
add constraint fk_orderdetails_batch
foreign key (batch_id) references batch(batch_id)
on update cascade on delete set null;

alter table check_details
add constraint fk_checkdetails_report
foreign key (report_id) references check_reports(report_id)
on update cascade on delete cascade;

alter table check_details
add constraint fk_checkdetails_batch
foreign key (batch_id) references batch(batch_id)
on update cascade on delete set null;

alter table incident_reports
add constraint fk_incident_batch
foreign key (batch_id) references batch(batch_id)
on update cascade on delete set null;


insert into suppliers values
(1, 'Fresh Foods Co', '123 Market St', 'Nguyen Van A', '0992711250'),
(2, 'Healthy Supply Ltd', '456 Health Blvd', 'Tran Thi B', '0996822183'),
(3, 'Daily Goods Inc', '789 Daily Rd', 'Le Van C', '0978357421'),
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

insert into categories values
(1, 'Do uong'),
(2, 'Thuc pham kho'),
(3, 'Nuoc giai khat'),
(4, 'Luong thuc'),
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

insert into products values
(1, 'Sua tuoi Fresh', 'hop', 25000, 100, 1),
(2, 'Mi goi Healthy', 'goi', 4000, 500, 2),
(3, 'Nuoc suoi mat', 'chai', 5000, 300, 3),
(4, 'Gao thom Daily', 'kg', 18000, 200, 4),
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
(19, 'Lau thai an lien', 'set', 75000.00, 40, 19);


insert into employee values
(1, 'Nguyen', 'An', '1990-01-01', 'an@gmail.com', 'Nam', 'Hanoi', '0954556901', '001199000001'),
(2, 'Tran', 'Binh', '1988-05-10', 'binh@gmail.com', 'Nam', 'HCMC', '0976543210', '001188000002'),
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



insert into batch values
(1, '2025-06-01', '2025-08-30', 94, 94, 3, 1, 1),
(2, '2025-06-01', '2025-08-30', 63, 63, 1, 1, 1),
(3, '2025-06-02', '2025-08-31', 52, 52, 4, 3, 2),
(4, '2025-06-02', '2025-08-31', 65, 65, 2, 3, 2);

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

INSERT INTO schedule (month, start_day, end_day, start_time, end_time) VALUES
(6, 2, 7, '08:00:00', '15:00:00'),  -- Ca sang, Thu 2 den Thu 7
(6, 3, 8, '08:00:00', '15:00:00'),  -- Ca chieu, Thu 3 den Chu Nhat
(6, 2, 7, '15:00:00', '22:00:00'),
(6, 3, 8, '15:00:00', '22:00:00');

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

INSERT INTO salary (monthyear, basic_salary, workdays, allowance, note, adjustment, leave_pay, actual_salary, employee_id) VALUES
('5/2025', 200000, 10, 50000, 'Muon: 2 Vang: 2', -100000, 0, 2400000, 1),
('5/2025', 200000, 10, 50000, 'Muon: 2 Vang: 2 Thuong: Di lam 1/5', -100000, 300000, 270000, 2);

INSERT INTO orders (order_id, order_date, total_amount, payment_method, customer_id, employee_id) VALUES
(1, '2025-06-01',240000, 'Tien mat', 1, 1),
(2, '2025-06-01', 180000, 'Chuyen khoan', 2, 1),
(3, '2025-06-02', 318000, 'Tien mat', 3, 2);

insert into import_reports (import_date, delivery_address, total_amount, employee_id, supplier_id) values 
('2024/12/12', '123 Ta Quang Buu', 1000000, 1, 2),
('2025/05/04', '123 Ta Quang Buu', 2000000, 1, 1),
('2025/01/02', '124 Thanh Nhan', 1500000, 2, 2);

insert into batch (import_date, expiration_date, total_quantity, quantity_in_stock, product_id, import_id, value_batch) values 
('2024/12/12', '2026/01/01', 100, 89, 4, 1, 1000000),
('2025/05/04', '2025/10/11', 200, 194, 1, 2, 2000000),
('2025/01/02', '2025/12/31', 300, 282, 2, 3, 1500000);


INSERT INTO order_details (order_id, batch_id, quantity) VALUES
(1, 1, 5),   -- 5 x 18000 = 90000
(1, 2, 6),   -- 6 x 25000 = 150000  → Tổng: 240000

(2, 1, 6),   -- 6 x 18000 = 108000
(2, 3, 18),  -- 18 x 4000 = 72000   → Tổng: 180000

(3, 1, 7),   -- 7 x 18000 = 126000
(3, 3, 48); 






