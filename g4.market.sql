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
    month varchar(7),
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
    employee_id int
);

create table batch (
    batch_id serial primary key,
    import_date date,
    expiration_date date,
    total_quantity int,
    quantity_in_stock int,
    product_id int,
    supplier_id int,
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

alter table batch
add constraint fk_batch_product
foreign key (product_id) references products(product_id)
on update cascade on delete set null;

alter table batch
add constraint fk_batch_supplier
foreign key (supplier_id) references suppliers(supplier_id)
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
(3, 'Daily Goods Inc', '789 Daily Rd', 'Le Van C', '0978357421');

insert into categories values
(1, 'Do uong'),
(2, 'Thuc pham kho'),
(3, 'Nuoc giai khat'),
(4, 'Luong thuc');

insert into products values
(1, 'Sua tuoi Fresh', 'hop', 25000, 100, 1),
(2, 'Mi goi Healthy', 'goi', 4000, 500, 2),
(3, 'Nuoc suoi mat', 'chai', 5000, 300, 3),
(4, 'Gao thom Daily', 'kg', 18000, 200, 4);


insert into employee values
(1, 'Nguyen', 'An', '1990-01-01', 'an@gmail.com', 'Nam', 'Hanoi', '0954556901', '001199000001'),
(2, 'Tran', 'Binh', '1988-05-10', 'binh@gmail.com', 'Nam', 'HCMC', '0976543210', '001188000002');

insert into import_reports values
(1, '2025-06-01', 'Kho A', 1500000, 1),
(2, '2025-06-02', 'Kho B', 2000000, 2);

insert into batch values
(1, '2025-06-01', '2025-08-30', 94, 94, 3, 1, 1),
(2, '2025-06-01', '2025-08-30', 63, 63, 1, 1, 1),
(3, '2025-06-02', '2025-08-31', 52, 52, 4, 3, 2),
(4, '2025-06-02', '2025-08-31', 65, 65, 2, 3, 2);


