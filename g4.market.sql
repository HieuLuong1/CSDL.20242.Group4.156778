--create table 
create table employee(
	employeeid int not null,
	lastname varchar(30) not null,
	firstname varchar(30) not null,
	dob date,
	email varchar(50),
	gender char(1),
	address varchar(50),
	phone varchar(11),
	identityid varchar(15) not null,
	constraint pk_employee primary key(employeeid));
	
insert into employee values ('1', 'An', 'Nguyen Van', '2000/10/09', 'an.nv@gmail.com', 'F', '40 Ta Quang Buu HBT HN', '0123678932', '0312123456');
insert into employee values ('2', 'Hoa', 'Do Thi', '2001/08/31', 'hoa.dt@gmail.com', 'F', '40 Le Thanh Nghi HBT HN', '0425689924', '0214131978');
insert into employee values ('3', 'Phuong', 'Tran Hoang', '1998/09/12', 'abcmn@gmail.com', 'M', '37 Thanh Nhan HBT HN', '0256738945', '0112143678');
	
create table schedule(
	scheduleid varchar(10) not null, --lưu theo dạng "MM-YYYY" ví dụ "052025"
	months varchar(20),
	from_day int, --mặc định thứ 2 = 2, thứ 3 = 3...
	to_day int, 
	timestart time, 
	timeend time,
	constraint pk_schedule primary key (scheduleid));
	
insert into schedule values ('052025S1', '2025/5', '2', '6', '7:00:00', '15:00:00');
insert into schedule values ('052025S2', '2025/5', '4', '8', '7:00:00', '15:00:00');
insert into schedule values ('052025C1', '2025/5', '2', '6', '15:00:00', '23:00:00');
insert into schedule values ('052025C2', '2025/5', '4', '8', '15:00:00', '23:00:00');

create table working(
	employeeid int not null,
	scheduleid varchar(10) not null,
	constraint pk_working primary key (employeeid, scheduleid));
	
insert into working values ('1', '052025S1');
insert into working values ('2', '052025C1');
insert into working values ('3', '052025S2');

create table salary(
	salaryid int not null,
	months int, 
	basic_salary int not null, 
	workdays int,
	bonus int,
	reward_punish int,
	note varchar(100),
	leavepay int, 
	actualsalary int, 
	employeeid int not null,
	constraint pk_salary primary key (salaryid),
	constraint chk_workdays check (workdays between 0 and 31));

create table customers(
	customerid int not null,
	fullname varchar(30),
	phone varchar(12),
	email varchar(20),
	constraint pk_customers primary key (customerid));

insert into customers values ('1', 'Luong Quy Hieu', '0968057524', null);
insert into customers values ('2', 'Le Vu Nguyen Hoang', '0968057624', null);

create table orders(
	orderid int not null,
	orderdate date not null,
	totalamount int, 
	method varchar(10),
	customerid int not null,
	constraint pk_orders primary key (orderid));

create table batch(
	batch int not null,
	importdate date,
	expiration date, -- HSD 
	totalnumber int,
	productid int not null, 
	supplierid int not null, 
	constraint pk_batch primary key (batch));

create table including(
	orderid int not null,
	batch int not null,
	quantity int,
	constraint pk_incld primary key (orderid));

create table products(
	productid int not null,
	name varchar(20),
	unit varchar(10),
	price int,
	quant_in_stock int,
	category int not null,
	constraint pk_prods primary key (productid));
	
insert into products values ('1', 'Cosy Marie loai to', 'goi', '50000', '0', '1');
insert into products values ('2', 'Vinamilk co duong 1l', 'hop 1 lit', '34000', '0', '2');
insert into products values ('3', 'Colgate tra xanh', 'tuyp', '30000', '0', '3');

	
create table categories(
	category int not null,
	title varchar(15),
	constraint pk_category primary key (category));

insert into categories values ('1', 'Banh keo');
insert into categories values ('2', 'Sua va kem');
insert into categories values ('3', 'Do thiet yeu');
	
create table checkreport(
	reportid int not null,
	checkdate date,
	constraint pk_checkreport primary key (reportid));
	
create table checking(
	reportid int not null,
	productid int not null,
	quant_real int,
	constraint pk_checking primary key (reportid));
	
create table import(
	importid int not null,
	imdate date, 
	imaddress varchar(50),
	totalamount int,
	employeeid int not null,
	supplierid int not null, 
	constraint pk_import primary key (importid));
	
create table supplier(
	supplierid int not null,
	name varchar(15),
	address varchar(30),
	contactname varchar(15),
	contact varchar(10), --số điện thoại của nhà cung cấp
	constraint pk_supplier primary key (supplierid));
	
insert into supplier values ('1', 'Colgate', '123 Hoan Kiem', '0123456778', 'anh Cuong');
insert into supplier values ('2', 'Vinamilk', '30 Tran Dai Nghia', '0124356678', 'chi Van');
insert into supplier values ('3', 'Cosy', '40 Truc Bach', '0123456779', 'anh Ha');

create table incident(
	incidentid int not null, --báo cáo xử lí sản phẩm 
	date_solve date,
	hour_solve time, 
	content varchar(50),
	productid int not null,
	constraint pk_incident primary key (incidentid));


alter table working 
add constraint fk_working_employee foreign key (employeeid) references employee(employeeid) on update cascade on delete restrict;
alter table working 
add constraint fk_working_schedule foreign key (scheduleid) references schedule(scheduleid) on update cascade on delete restrict;
alter table salary 
add constraint fk_salary_employee foreign key (employeeid) references employee(employeeid) on update cascade on delete restrict;
alter table orders 
add constraint fk_orders_customer foreign key (customerid) references customers(customerid) on update cascade on delete restrict;
alter table batch 
add constraint fk_batch_product foreign key (productid) references products(productid) on update cascade on delete restrict;
alter table batch 
add constraint fk_batch_supplier foreign key (supplierid) references supplier(supplierid) on update cascade on delete restrict;
alter table including 
add constraint fk_including_order foreign key (orderid) references orders(orderid) on update cascade on delete restrict;
alter table including 
add constraint fk_including_batch foreign key (batch) references batch(batch) on update cascade on delete restrict;
alter table products 
add constraint fk_product_category foreign key (category) references categories (category) on update cascade on delete restrict;
alter table checking 
add constraint fk_checking_report foreign key (reportid) references checkreport(reportid) on update cascade on delete restrict;
alter table checking 
add constraint fk_checking_product foreign key (productid) references products(productid) on update cascade on delete restrict;

alter table import  
add constraint fk_import_supplier foreign key (supplierid) references supplier(supplierid) on update cascade on delete restrict;

alter table incident 
add constraint fk_incident_product foreign key (productid) references products(productid) on update cascade on delete restrict;
--view 
--khách hàng 
--xem thông tin KH: bảng customers 
--xem lịch sử mua hàng: 
create or replace view order_history AS	
	select orderid, orderdate, method, totalamount 
	from orders;
--bấm vào từng order thì xem được chi tiết hóa đơn của khách hàng đó 
create or replace view order_specific AS
	select orderid, orderdate, productid, quantity, price, (quantity * price) as total 
	from orders 
	join including using (orderid)
	join batch using (batch)
	join products using (productid);

--bổ sung 
alter table orders 
add column employeeid int; 

alter table orders 
add constraint fk_orders_employee foreign key (employeeid) references employee (employeeid); 

--nhân viên 
--xem kho: bảng products 
--xem khách hàng: bảng customers 
--xem lịch làm việc:
create or replace view schedule_specific as 
	select scheduleid,concat(firstname, ' ', lastname) as fullname , months, from_day, end_day, timestart, timeend
	from schedule
	join working using (scheduleid) 
	join employee using (employeeid);
	
--xem điểm danh, bảng lương: bảng salary 
--xem hóa đơn: bảng orders 

--quản lí 
