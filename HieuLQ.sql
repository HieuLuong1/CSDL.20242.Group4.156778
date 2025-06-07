--Lương Quý Hiếu 20235711 
--1. Cho biết các đơn hàng và tổng số tiền của đơn hàng nhận được trong ngày 5/6/2025:
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
--Ở 2 cách trên thì cách số 1 có thể chạy hơn hơn, chi phí nhỏ hơn do truy vấn trên 1 bảng (có truy vấn con) nhưng đặt trong phép toán in
--điều này thực sự giúp cho khi dữ liệu nhiều thì hệ thống có thể quét bằng customerid_pkey trước để lọc ra các giá customerid, sau đó mới lọc ra 
--theo date, còn câu 2 lại sử dụng 1 phép join có thể tốn nhiều chi phí hơn khi bảng có nhiều dữ liệu.

--3. Đưa ra sản phẩm được bán nhiều nhất trong tháng 4/2025.
with temp as(
	select product_id, product_name, sum(quantity) as banra 
	from orders
	join order_details using (order_id)
	join batch using (batch_id) 
	join products using (product_id)
	where order_date between '2025/03/01' and '2025/03/31'
	group by product_id, product_name)
select product_id, product_name, banra 
from temp 
where banra = (select max(banra) from temp);
--có thể tạo chỉ mục trên order_date (đã tạo) để tăng tốc độ.

--4. Chi tiết đơn hàng có order_id = 1 biết 1 đơn hàng cần bao gồm mã sản phẩm, tên sản phẩm, số lượng bán ra, giá cả mỗi SP. 
select product_id, product_name, quantity, price_with_tax  
from products 
join batch using (product_id)
join order_details using (batch_id) 
where order_id = '1';

--5. Cho biết các lô hàng và nội dung xử lí các lô hàng mà bị lập biên bản xử lí tính từ đầu năm 2025.
select batch_id, description 
from incident_reports 
where report_date >= '2025/01/01';
--Có thể đặt truy vấn trên cột report_date để cải thiện tốc độ truy vấn. 
create index idx_incident_date on incident_reports using btree (incident_date);
--Tuy nhiên sử dụng index ở đây đối với cá nhân em khá liều lĩnh. Bởi vì ở đây là truy vấn trên 1 bảng, nếu bảng dữ liệu nhỏ thì hệ thống 
--chỉ cần duyệt qua từ trên xuống dưới cứ giá trị nào thỏa mãn thì sẽ đưa ra màn hình, còn nếu sử dụng index thì nó sẽ mất công sắp xếp lại dữ liệu 
--rồi mới từ đó đưa ra các bản ghi thỏa mãn => cost ban đầu bị tăng do bước sắp xếp, nhưng cost lúc sau được giảm cũng chỉ 1 chút.
--Bảng incident_report vốn ít dữ liệu (vì ko thể lúc nào cũng phải xử lí lô hàng hỏng đc) nên việc đặt index ở đây sẽ cần phải bảo trì, thêm 
--chi phí tốn kém.

--6. Cho biết nhân viên chăm chỉ nhất siêu thị vào tháng 5/2025. Nhân viên chăm chỉ là nhân viên đi làm không nghỉ buổi nào, không muộn buổi nào.
with temp as(
	select employee_id, count(*) as dilam 
	from working 
	where (work_date between '2025/05/01' and '2025/05/31') and status = 'D'
	group by employee_id)
select employee_id, firstname, lastname
from employee
join temp using (employee_id)
where dilam = (select max(dilam) from temp);

--7. Thống kê số tiền lương đã chi trả cho nhân viên tháng 5/2025.
select sum(actual_salary)
from salary 
where monthyear = '5/2025';
--có thể tạo chỉ mục trên cột monthyear để cải thiện tốc độ truy vấn với phép toán =, hệ thống sẽ lọc ra mọi bản ghi thỏa mãn để tính tổng sum ở 
--câu lệnh select.
create index idx_salary_monthyear on salary using btree (monthyear);

--8. Cho biết Công ty TNHH Sao Mai đã cung cấp những sản phẩm gì cho siêu thị.
select distinct product_id, product_name 
from products 
join batch using (product_id) 
join import_reports using (import_id)
join suppliers using (supplier_id)
where supplier_name = 'Cong ty TNHH Sao Mai';
--do việc truy vấn các nhà cung cấp là thường xuyên nên cũng có thể đặt index trên cột name để cải thiện tốc độ truy vấn
create index idx_suppliers_name on suppliers using btree (supplier_name);

--9. Đưa ra thông tin các lô hàng được kiểm kê số lượng vào ngày 5/5/2025. Lưu ý khi đưa ra màn hình cần cho biết mã lô, số lượng lưu trên hệ Thống
--với số lượng thực tế mà quản lí ghi vào. 
select batch_id, quantity_in_stock, real_quantity 
from batch 
join check_details using (batch_id) 
join check_reports using (report_id) 
where check_date = '2025/05/05';

--10. Cho biết khách hàng nào mua nhiều đơn nhất.
with temp as(
	select customer_id, count(order_id) as mua 
	from orders
	group by customer_id)
select customer_id, fullname, mua 
from customer 
join temp using (customer_id) 
where mua = (select max(mua) from temp);