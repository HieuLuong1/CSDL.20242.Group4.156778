--Lương Quý Hiếu 20235711 
--1. Cho biết các đơn hàng và số tiền của đơn hàng nhận được trong ngày 5/6/2025:
select order_id, total_amount 
from orders 
where order_date = '2025/06/05';
--câu lệnh có thể sử dụng index để tối ưu do phép toán = thỏa mãn điều kiện tìm kiếm của index btree. Nó sẽ sắp xếp dữ liệu theo 1 trình tự rồi lấy ra 
--các kết quả orderdate thỏa mãn.
create index idx_orders_date on orders using btree (order_date);

--2. Cho biết các khách hàng đã mua hàng ở cửa hàng vào ngày 5/6/2025. Thông tin in ra gồm mã khách hàng, họ tên đầy đủ của khách hàng đó.
--Lưu ý nếu khách hàng mua từ 2 lần trên 1 ngày cũng chỉ hiện thông tin 1 lần.
select distinct customer_id, fullname 
from customer 
where customer_id in (select customer_id from orders where order_date = '2025/06/05');
--câu lệnh có thể viết theo nhiều cách 
select distinct customer_id, fullname 
from customer 
join orders using (customer_id) 
where order_date = '2025/06/05';
--có thể sử dụng chỉ mục trên cột order_date do phép toán = giúp index sắp xếp dữ liệu, thực thi tìm ra nhanh các dòng có giá trị là 2025/06/05 
create index idx_orders_date on orders using btree (order_date);
--Ở 2 cách viết trên thì cách số 1 dùng truy vấn con, nó sẽ lọc ra các bản ghi customerid thỏa mãn, sau đó sẽ group theo các giá trị trùng nhau 
--rồi mới so sánh với các bản ghi customerid ở câu truy vấn chính. Cách số 2 thì nó lọc ra các bản ghi theo orderid thỏa mãn, rồi kết nối với 
--bảng customer để trả ra kết quả. 2 cách đều có thể dùng index tuy nhiên cách số 2 nhanh hơn cách 1 do máy ko cần lọc để loại truy trong truy vấn con.

--3. Đưa ra sản phẩm được bán nhiều nhất trong tháng 6/2025.
with temp as(
	select product_id, product_name, sum(quantity) as banra 
	from orders
	join order_details using (order_id)
	join batch using (batch_id) 
	join products using (product_id)
	where order_date between '2025/06/01' and '2025/06/30'
	group by product_id, product_name)
select product_id, product_name, banra 
from temp 
where banra = (select max(banra) from temp);
--sử dụng 1 bảng temp để đưa ra các sản phẩm và số lượng được bán ra của chúng.
--có thể tạo chỉ mục trên order_date (đã tạo) để tăng tốc độ.
create index idx_orders_date on orders using btree (order_date);

--4. Chi tiết đơn hàng có order_id = 1 biết 1 đơn hàng cần bao gồm mã sản phẩm, tên sản phẩm, số lượng bán ra, giá cả mỗi SP, thành tiên mỗi SP 
select product_id, product_name, quantity, price_with_tax, (quantity * price_with_tax) as thanhtien 
from order_details 
join batch using (batch_id)
join products using (product_id) 
where order_id = '1';

--nếu viết theo cách này, order_id = 1 sẽ được lọc ra đầu tiên để giảm kích cỡ của bảng sau đó sẽ thực hiện jon.
--Mặc dù đã có sẵn index khóa chính orderid với batch_id, nhưng hệ thống không lựa chọn sử dụng index này do phép where chỉ có 1 điều kiện và
--sử dụng index trên 2 cột cũng không nhanh bằng phép lọc thông thường (do máy tự chọn). 
--Tuy nhiên vẫn có thể sử dụng 1 index trên 1 cột 
create index idx_details_orderid on order_details using btree (order_id);
--thì hệ thống sẽ nhận ra chỉ có 1 điều kiện duy nhất và trùng khớp với điều kiện trên index này nên nó dùng => cải thiện truy vấn.

--5. Tạo 1 khung nhìn cho biết các loại sản phẩm hiện có trong siêu thị và số lượng của mỗi loại. Sắp xếp kết quả theo thứ tự giảm dần về số lượng. 
create or replace view categories_number as 
	select category_id, category_name, count(product_id) as soluong 
	from categories 
	join products using (category_id)
	group by category_id, category_name 
	order by soluong desc;

--6. Cho biết nhân viên chăm chỉ nhất siêu thị vào tháng 5/2025 để quản lí tặng thưởng. Nhân viên chăm chỉ là nhân viên đi làm 
--không nghỉ buổi nào, không muộn buổi nào, không phép buổi nào.
with temp as(
	select employee_id, count(*) as dilam 
	from working 
	where status = 'D' and (work_date between '2025/05/01' and '2025/05/31')
	group by employee_id)
select employee_id, concat(lastname, ' ', firstname) as fullname, dilam 
from employee 
join temp using (employee_id)
where dilam = (select max(dilam) from temp);

--câu này có thể cải thiện hiệu năng câu lệnh truy vấn bằng cách đặt index trên 2 điều kiện ở phép where trong bảng tạm. Vấn đề cần thiết ở đây là 
--nên đặt index theo 2 cột (status, work_date) hay (work_date, status) hay 2 index trên 2 cột đơn lẻ status với workdate.
--create index idx_working_status_date on working using btree (status, work_date);
--create index idx_date_status on working using btree (work_date, status);
create index idx_working_date on working using btree (work_date);
--create index idx_working_status on working using btree (status);

with temp as(
	select employee_id, count(*) as dilam 
	from working 
	where (work_date between '2025/05/01' and '2025/05/31') and status = 'D'
	group by employee_id)
select employee_id, concat(lastname, ' ', firstname) as fullname, dilam 
from employee 
join temp using (employee_id)
where dilam = (select max(dilam) from temp);

--nhận xét do cột status ít dữ liệu khác nhau ('D', 'M', 'V', 'P') nên thứ tự bị lặp lại nhiều, có nhiều khả năng đặt index ở đây sẽ không hiệu quả 
--do hệ thống sẽ lựa chọn cách duyệt toàn bộ thay vì sắp xếp nhiều bản ghi giống hệt nhau trên cột status rồi mới lọc ra. => index trên status ko hiệu quae.
--Index trên work_date theo em đáp ứng đc nhất trong trường hợp này, nó sẽ giúp lọc ra khoảng ngày tháng năm thỏa mãn rồi sau đó lấy ra các bản ghi có status phù hợp.
--Đối với index đôi tùy vào 2 cách viết truy vấn thì thứ tự trong where có sự thay đổi => dẫn đến nếu viết theo cách 1 thì index (status, work_date) thực hiện đc,
--còn index (work_date, status) lại ko đc, cách 2 thì ngược lại. => có thể nhanh trong 1 số trường hợp, nhưng lại thiếu tính linh hoạt, bắt buộc theo thứ tự.
--ngoài ra việc đặt 1 index 2 cột ở đây cũng khá tốn kém chi phí, trừ trường hợp cần truy vấn việc này nhiều lần, còn nếu muốn tìm người nghỉ nhiều 
--nhất, muộn nhiều nhất thì lại phải cần thêm các index khác ... => gây tốn kém bảo trì, trong khi việc lặp theo ngày đã cực hiệu quả rồi (do status bị lặp lại dữ liệu nhiều).


--7. Viết hàm trả về các đơn hàng mà khách hàng đã mua với đầu vào là số điện thoại của khách, kết quả trả về là mã hóa đơn, ngày mua hàng,
--hình thức thanh toán, tổng số tiền phải trả.
create or replace function order_bought(in v_phone varchar(20)) returns table (orderid int, orderdate date, method varchar(50), totalamount numeric(12,2)) as
$$
begin 
	return query select order_id, order_date, payment_method, total_amount 
				 from orders 
				 join customer using (customer_id)
				 where phone = v_phone;
end;
$$ language plpgsql;

select * from order_bought('0909123456');

--8. Cho biết Công ty TNHH Sao Mai đã cung cấp những sản phẩm gì cho siêu thị.
select distinct product_id, product_name 
from products 
join batch using (product_id) 
join import_reports using (import_id)
join suppliers using (supplier_id)
where supplier_name = 'Cong ty TNHH Sao Mai';

--có thể đặt index trên supplier_name để truy vấn hiệu quả hơn.
create index idx_suppliers_name on suppliers using btree (supplier_name);

--9. Đưa ra thông tin các lô hàng được kiểm kê số lượng vào ngày 5/5/2025. Lưu ý khi đưa ra màn hình cần cho biết mã lô, số lượng lưu trên hệ thống
--với số lượng thực tế mà quản lí ghi vào. 
select batch_id, quantity_in_stock, real_quantity 
from batch 
join check_details using (batch_id) 
join check_reports using (report_id) 
where check_date = '2025/05/05';

--có thể đặt index trên cột check_date.
create index idx_check_date on check_reports using btree (check_date);

--10. Cho biết khách hàng nào mua nhiều đơn nhất từ trước đến giờ.
with temp as(
	select customer_id, count(order_id) as mua 
	from orders
	group by customer_id)
select customer_id, fullname, mua 
from customer 
join temp using (customer_id) 
where mua = (select max(mua) from temp);
