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
where banra >= all(select banra from temp);
--sử dụng 1 bảng temp để đưa ra các sản phẩm và số lượng được bán ra của chúng.
--có thể tạo chỉ mục trên order_date (đã tạo) để tăng tốc độ.
create index idx_orders_date on orders using btree (order_date);
--Ở 2 cách trên, cách số 1 sẽ nhanh hơn do đặt phép toán bằng và 1 truy vấn tìm max con bên trong, do đó hệ thống chỉ cần tính 1 lần ra giá trị max,
--sau đó nó sẽ chỉ làm việc trên bảng chính theo quy tắc đường ống. Còn ở cách số 2 nó sẽ liên quan đến bộ nhớ, tức là cứ duyệt 1 bản ghi lại so sánh 
--với mọi bản ghi của truy vấn con, sau đó nó mới ghi vào bộ nhớ xem giá trị mới có vượt giá trị max hay không.

--4.	Thống kê số nhân viên mỗi ca làm trong tháng 6/2025.
select schedule_id, start_day, end_day, start_time, end_time, count(distinct employee_id) as soluong 
from schedule 
join working using (schedule_id)
where work_date between '2025/06/01' and '2025/06/30'
group by schedule_id;
--Có thể và nên sử dụng index B-Tree ở trên cột work_date của bảng working. Do cột work_date chứa các giá trị ngày tháng, trung bình 1 ngày có thể nhập đến 20 bản ghi nên số lượng bản ghi đủ lớn, dữ liệu trên cột lại không cần thay đổi nhiều nên cần thiết phải tạo 1 chỉ mục theo ngày:
create index idx_working_date on working using btree (work_date);
--Index B-Tree sẽ thực hiện vai trò của nó để gom khoảng giá trị thỏa mãn để có thể lọc ra các giá trị trong tháng đó. (Số bản ghi trong 1 tháng lại đủ nhỏ so với số bản ghi tổng thể, rất thuận lợi cho việc gọi chỉ mục này).

--5. Tạo 1 khung nhìn cho biết các loại sản phẩm hiện có trong siêu thị và số lượng của mỗi loại. Sắp xếp kết quả theo thứ tự giảm dần về số lượng. 
create or replace view categories_number as 
	select category_id, category_name, count(product_id) as soluong 
	from categories 
	join products using (category_id)
	group by category_id, category_name 
	order by soluong desc;

--6. Cho biết nhân viên chăm chỉ nhất siêu thị vào tháng 5/2025 để quản lí tặng thưởng. Nhân viên chăm chỉ là nhân viên đi làm đủ từ 25 buổi/ tháng
--nhưng không được Vắng (nghỉ không phép) buổi nào. 
with temp as(
	select employee_id, count(*) as dilam 
	from working 
	where status = 'D' and (work_date between '2025/05/01' and '2025/05/31')
	group by employee_id
	having count(*) >= 25),
temp_absent as(
	select employee_id, count(*) as vang 
	from working 
	where status = 'V' and (work_date between '2025/05/01' and '2025/05/31')
	group by employee_id)
select employee_id, concat(lastname, ' ', firstname) as fullname, dilam 
from employee 
join temp using (employee_id)
left join temp_absent using (employee_id)
where dilam = (select max(dilam) from temp) and vang is null;

--câu này có thể cải thiện hiệu năng câu lệnh truy vấn bằng cách đặt index trên 2 điều kiện ở phép where trong bảng tạm. Vấn đề cần thiết ở đây là 
--nên đặt index theo 2 cột (status, work_date) hay (work_date, status) hay 2 index trên 2 cột đơn lẻ status với workdate.
--create index idx_working_status_date on working using btree (status, work_date);
--create index idx_date_status on working using btree (work_date, status);
create index idx_working_date on working using btree (work_date);
--create index idx_working_status on working using btree (status);

--nhận xét do cột status ít dữ liệu khác nhau ('D', 'M', 'V', 'P') nên thứ tự bị lặp lại nhiều, có nhiều khả năng đặt index ở đây sẽ không hiệu quả 
--do hệ thống sẽ lựa chọn cách duyệt toàn bộ thay vì sắp xếp nhiều bản ghi giống hệt nhau trên cột status rồi mới lọc ra. => index trên status ko hiệu quae.
--Index trên work_date theo em đáp ứng được nhất trong trường hợp này, nó sẽ giúp lọc ra khoảng ngày tháng năm thỏa mãn rồi sau đó lấy ra các bản ghi có status phù hợp.
--Đối với index đôi tùy vào 2 cách viết truy vấn thì thứ tự trong where có sự thay đổi => dẫn đến nếu viết theo cách 1 thì index (status, work_date) thực hiện đc,
--còn index (work_date, status) lại ko được, cách 2 thì ngược lại. => có thể nhanh trong 1 số trường hợp, nhưng lại thiếu tính linh hoạt, bắt buộc theo thứ tự.
--ngoài ra việc đặt 1 index 2 cột ở đây cũng khá tốn kém chi phí, trừ trường hợp cần truy vấn việc này nhiều lần, ... => gây tốn kém bảo trì, trong khi việc lặp theo ngày đã cực hiệu quả rồi (do status bị lặp lại dữ liệu nhiều).


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
--Tuy nhiên trên thực tế người ta chỉ lưu đầy đủ chứ tìm kiếm thì hiếm khi. Thường khi tìm tên công ty người ta chỉ tìm Sao Mai, hoặc cùng lắm là 
--Công ty Sao Mai, do đó dòng where trên thực tế sẽ có dạng là (tùy vào tên của công ty nữa).
where supplier_name like 'Sao Mai%';
where supplier_name like '%Sao Mai';
where supplier_name like '%Sao Mai%';
--Như vậy trong 3 cách trên thì có đến 2 cách thường xuyên được dùng nhưng lại cấm index btree (%name), do đó ở đây việc đặt index sẽ không tối ưu
--về việc sử dụng lắm.


--9. Viết 1 hàm có đầu vào là mã nhân viên, ngày đi làm và trả về số đơn mà nhân viên đó đã thực hiện trong ngày hôm đó. 
create or replace function total_order (in v_employeeid int, v_date date) returns integer as 
$$
declare v_count integer;
begin 
	select into v_count count(order_id)
	from orders 
	where employee_id = v_employeeid and order_date = v_date;
	return v_count;
end;
$$ language plpgsql;
select total_order(1, '2025/06/09');

--10. Cho biết khách hàng nào mua nhiều đơn nhất từ trước đến giờ.
with temp as(
	select customer_id, count(order_id) as mua 
	from orders
	group by customer_id)
select customer_id, fullname, mua 
from customer 
join temp using (customer_id) 
where mua = (select max(mua) from temp);

--11.	Đặt trigger để quản lí việc khi 1 lô hàng mới được nhập thì số lượng tồn kho của 1 sản phẩm sẽ được tăng lên tương ứng, kiểm tra cả trường hợp khi lô được bán ra hoặc tiêu hủy. 
--INSERT:
create or replace function tf_after_insert_batch() returns trigger as 
$$ 
begin 
	update products 
	set quantity_in_stock = quantity_in_stock + new.total_quantity 
	where product_id = new.product_id;
	return new;
end;
$$ language plpgsql;

create trigger tg_after_insert_batch 
after insert on batch 
for each row 
execute procedure tf_after_insert_batch();
--DELETE:
create or replace function tf_after_delete_batch() returns trigger as 
$$ 
begin 
	update products 
	set quantity_in_stock = quantity_in_stock - old.quantity_in_stock 
	where product_id = old.product_id;
	return old;
end;
$$ language plpgsql;

create trigger tg_after_delete_batch 
after delete on batch 
for each row 
execute procedure tf_after_delete_batch();
--UPDATE:
create or replace function tf_after_update_batch() returns trigger as 
$$
begin 
	update products 
	set quantity_in_stock = quantity_in_stock - old.quantity_in_stock + new.quantity_in_stock 
	where product_id = new.product_id;
	return new;
end;
$$ language plpgsql;

create trigger tg_after_update_batch
after update on batch 
for each row 
when (new.quantity_in_stock is distinct from old.quantity_in_stock)
execute procedure tf_after_update_batch();
--12.	Đặt trigger để đảm bảo mỗi khi 1 sản phẩm trong lô hàng được bán ra thì số lượng tồn kho của lô đó giảm tương ứng.
create or replace function tf_after_insert_order_details() returns trigger as 
$$
begin 
	update batch 
	set quantity_in_stock = quantity_in_stock - new.quantity 
	where batch_id = new.batch_id;
	return new;
end;
$$ language plpgsql;
create trigger tg_after_insert_order_details 
after insert on order_details 
for each row 
execute procedure tf_after_insert_order_details();
--13.	Cho biết từ đầu năm 2025 có những lô hàng nào bị sai khác số lượng? Sắp xếp mức độ nghiêm trọng theo (số lượng thực tế - số lượng hệ thống) giảm dần.
select batch_id, product_name, check_details.quantity_in_stock, real_quantity, (check_details.quantity_in_stock - real_quantity) as thatthoat 
from check_reports 
join check_details using (report_id)
join batch using (batch_id)
join products using (product_id)
where check_date >= '2025/01/01' and (check_details.quantity_in_stock - real_quantity) > 0 
order by thatthoat desc;

--Lê Vũ Nguyên Hoàng (20235723):
-- 1. Cho biết 5 sản phẩm có đơn giá cao nhất trong siêu thị
SELECT product_id, product_name, unit, price_with_tax
FROM products
ORDER BY price_with_tax DESC
LIMIT 5;
-- Vì truy vấn cần sắp xếp toàn bộ bảng theo price_with_tax giảm dần rồi lấy 5 dòng đầu tiên, việc tạo chỉ mục DESC trên cột này giúp truy cập trực tiếp các phần tử lớn nhất mà không cần sắp xếp toàn bảng.
CREATE INDEX idx_products_price_desc ON products USING btree (price_with_tax DESC);

-- 2. Hiển thị toàn bộ danh sách hóa đơn gồm mã hóa đơn, ngày đặt hàng, tên khách hàng, tổng tiền và hình thức thanh toán
SELECT o.order_id, o.order_date, c.fullname AS customer_name, o.total_amount, o.payment_method
FROM orders o
JOIN customer c ON o.customer_id = c.customer_id;

-- Cách viết khác dùng subquery
SELECT o.order_id, o.order_date, (SELECT fullname FROM customer c WHERE c.customer_id = o.customer_id) AS customer_name, o.total_amount, o.payment_method
FROM orders o;

-- Ở cách viết 2, hệ thống quét toàn bộ bảng orders và đối với mỗi bản ghi, hệ thống quét tiếp toàn bộ bảng customer để tìm bản ghi có customer_id trùng khớp. 
-- Do đó ở cách viết 1, sử dụng JOIN để ghép 2 bảng với nhau sẽ có tốc độ nhanh hơn

-- 3. Thống kê số loại sản phẩm, số lượng hàng tồn kho của mỗi danh mục hàng
SELECT ca.category_name, COUNT(p.product_id) AS so_loai_san_pham, SUM(p.quantity_in_stock) AS tong_so_hang_trong_kho
FROM categories ca
LEFT JOIN products p ON ca.category_id = p.category_id
GROUP BY ca.category_name;

-- 4. Thống kê số lần nhập hàng từ các nhà cung cấp, và đưa ra giá trị đơn nhập cao nhất và thấp nhất của từng nhà cung cấp
SELECT s.supplier_id, s.supplier_name, COUNT(ir.import_id) AS so_lan_nhap, MAX(ir.total_amount) AS tong_cao_nhat, MIN(ir.total_amount) AS tong_thap_nhat
FROM suppliers s
LEFT JOIN import_reports ir ON s.supplier_id = ir.supplier_id
GROUP BY s.supplier_id, s.supplier_name;

-- 5. Đưa ra danh sách khách hàng đã đến siêu thị mua hàng vào tháng 6 năm 2025

SELECT DISTINCT c.customer_id, c.fullname
FROM customer c
JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_date BETWEEN '2025-06-01' AND '2025-06-30';

-- Truy vấn lọc các đơn hàng trong khoảng thời gian tháng 6/2025, đây là khoảng nhỏ so với toàn bộ dữ liệu. 
-- Do đó, tạo chỉ mục với order_date giúp hệ thống nhanh chóng xác định các bản ghi phù hợp mà không cần quét toàn bộ bảng.
-- Đồng thời, customer_id cũng thường xuyên được sử dụng nên có thể tạo chỉ mục kết hợp để tăng tốc độ lọc thời gian và ghép bảng

CREATE INDEX idx_orders_date_customer ON orders(order_date, customer_id);

-- 6. Liệt kê chi tiết các biên bản kiểm kê trong năm 2025
SELECT cr.report_id, cr.check_date, b.batch_id, b.product_id, b.quantity_in_stock AS so_luong_ghi_nhan, cd.real_quantity AS so_luong_thuc_te
FROM check_reports cr
JOIN check_details cd ON cr.report_id = cd.report_id
JOIN batch b ON cd.batch_id = b.batch_id
WHERE cr.check_date >= '2025-01-01'
ORDER BY cr.check_date, cr.report_id, b.batch_id;

-- Truy vấn lọc theo check_date trong một khoảng thời gian cụ thể, nên có thể tạo chỉ mục giúp truy vấn phạm vi hiệu quả.
CREATE INDEX idx_checkreports_date ON check_reports(check_date);

-- 7. Tính lợi nhuận (thu - chi) của siêu thị trong tháng 5/2025
WITH thu AS (
    SELECT SUM(total_amount) AS tong_thu
    FROM orders
    WHERE order_date BETWEEN '2025-05-01' AND '2025-05-31'
),
chi_luong AS (
    SELECT SUM(actual_salary) AS tong_chi_luong
    FROM salary
    WHERE monthyear = '5/2025'
),
chi_nhap AS (
    SELECT SUM(total_amount) AS tong_chi_nhap
    FROM import_reports
    WHERE import_date BETWEEN '2025-05-01' AND '2025-05-31'
)
SELECT 
    thu.tong_thu,
    chi_luong.tong_chi_luong,
    chi_nhap.tong_chi_nhap,
    thu.tong_thu - chi_luong.tong_chi_luong - chi_nhap.tong_chi_nhap AS loi_nhuan
FROM thu, chi_luong, chi_nhap;

-- Các truy vấn con đều lọc theo khoảng thời gian ngắn, do đó có thể tạo chỉ mục trên các cột thời gian trong các bảng để tăng hiệu suất lọc thời gian.
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_salary_monthyear ON salary(monthyear);
CREATE INDEX idx_import_date ON import_reports(import_date);

-- 8. Tạo bảng view thống kê tổng chi tiêu của mỗi khách hàng   
CREATE OR REPLACE VIEW customer_spending_view AS
SELECT c.customer_id, c.fullname, COUNT(o.order_id) AS so_don_hang, SUM(o.total_amount) AS tong_chi_tieu
FROM customer c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.fullname;

-- 9. Hàm thống kê số ngày đi làm đúng giờ, đi muộn và vắng của nhân viên theo ID trong tháng 5 năm 2025
CREATE OR REPLACE FUNCTION employee_attendance_summary(v_employee_id INT)
RETURNS TABLE (
    employee_id INT,
    so_ngay_dung_gio INT,
    so_ngay_di_muon INT,
    so_ngay_vang INT
) AS
$$
BEGIN
    RETURN QUERY
    SELECT 
        w.employee_id,
        COUNT(*) FILTER (WHERE w.status = 'D')::INT AS so_ngay_dung_gio,
        COUNT(*) FILTER (WHERE w.status = 'M')::INT AS so_ngay_di_muon,
        COUNT(*) FILTER (WHERE w.status = 'V')::INT AS so_ngay_vang
    FROM working w
    WHERE w.employee_id = v_employee_id
      AND w.work_date BETWEEN DATE '2025-05-01' AND DATE '2025-05-31'
    GROUP BY w.employee_id;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM employee_attendance_summary(1);

-- 10. Hàm đưa ra các sản phẩm sắp hết hạn sau số ngày nhập vào
CREATE OR REPLACE FUNCTION expiring_batches(n_days INT)
RETURNS TABLE (
    batch_id INT,
    product_name VARCHAR(100),
    expiration_date DATE,
    quantity_in_stock INT,
    days_left INT
) AS
$$
BEGIN
    RETURN QUERY
    SELECT 
        b.batch_id,
        p.product_name,
        b.expiration_date,
        b.quantity_in_stock,
        (b.expiration_date - CURRENT_DATE)::INT AS days_left
    FROM batch b
    JOIN products p ON b.product_id = p.product_id
    WHERE b.expiration_date BETWEEN CURRENT_DATE AND CURRENT_DATE + n_days;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM expiring_batches(200);

-- Vì truy vấn lọc theo cột expiration_date trong một khoảng giới hạn (ngày hiện tại đến n ngày sau), nên có thể tạo chỉ mục để tăng hiệu suất lọc thời gian.
CREATE INDEX idx_batch_expiration_date ON batch(expiration_date);

-- 11. Thống kê số đơn hàng theo từng ngày
SELECT order_date, COUNT(order_id) AS so_don_hang, SUM(total_amount) AS tong_doanh_thu
FROM orders
GROUP BY order_date
ORDER BY order_date;

-- Vì truy vấn nhóm và sắp xếp theo order_date, hệ thống cần quét và nhóm theo cột này. Do đó có thể tạo chỉ mục với order_date để tăng hiệu suất nhóm và sắp xếp.
CREATE INDEX idx_orders_orderdate ON orders(order_date);

-- 12. Tạo view tổng nhập hàng theo tháng
CREATE OR REPLACE VIEW monthly_import_summary AS
SELECT 
    TO_CHAR(import_date, 'YYYY-MM') AS thang_nhap,
    COUNT(import_id) AS so_lan_nhap,
    SUM(total_amount) AS tong_gia_tri_nhap
FROM import_reports
GROUP BY TO_CHAR(import_date, 'YYYY-MM')
ORDER BY thang_nhap;

-- Truy vấn tổng hợp theo tháng dựa vào import_date, nên tạo chỉ mục trên cột này sẽ giúp hệ thống tăng tốc độ nhóm theo tháng.
CREATE INDEX idx_importreports_importdate ON import_reports(import_date);

-- 13. Tạo view xem các sản phẩm bán chạy nhất
CREATE OR REPLACE VIEW top_selling_products AS
SELECT 
    p.product_id,
    p.product_name,
    SUM(od.quantity) AS tong_so_luong_ban
FROM products p
JOIN batch b ON p.product_id = b.product_id
JOIN order_details od ON b.batch_id = od.batch_id
GROUP BY p.product_id, p.product_name
ORDER BY tong_so_luong_ban DESC;

-- 14. Liệt kê các lô hàng đã bị lập biên bản xử lý kèm tên sản phẩm
SELECT 
    ir.incident_id,
    ir.report_date,
    ir.report_time,
    ir.description,
    b.batch_id,
    b.expiration_date,
    p.product_name
FROM incident_reports ir
JOIN batch b ON ir.batch_id = b.batch_id
JOIN products p ON b.product_id = p.product_id;

--15. Thống kê tổng lương thực tế đã trả cho từng nhân viên trong năm 2025
SELECT 
    e.employee_id,
    e.lastname || ' ' || e.firstname AS hoten,
    SUM(s.actual_salary) AS tong_luong
FROM salary s
JOIN employee e ON s.employee_id = e.employee_id
WHERE s.monthyear LIKE '%2025%'
GROUP BY e.employee_id, hoten;


--Phạm Đào Việt Hoàng (20235727):
--1. Tổng số đơn hàng mỗi ngày trong tháng 6/2025

  SELECT 
    order_date, 
    COUNT(*) AS num_orders
  FROM orders
  WHERE order_date BETWEEN '2025-06-01' AND '2025-06-30'
  GROUP BY order_date
  ORDER BY order_date;

  -- Điều kiện lọc order_date BETWEEN … bao phủ 30 ngày (~30/365 ≈ 8%) khá nhỏ so với toàn bộ tổng số đơn hàng
  -- Index B‑Tree trên order_date sẽ nhanh chóng khoanh vùng các bản ghi trong khoảng, tránh quét toàn bộ.

  CREATE INDEX idx_orders_orderdate ON orders(order_date);

--2. Danh sách sản phẩm có tồn kho dưới 10 đơn vị

  SELECT 
  b.product_id,
  p.product_name,
  SUM(b.quantity_in_stock) AS total_in_stock
  FROM batch b
  JOIN products p USING(product_id)
  GROUP BY b.product_id, p.product_name
  HAVING SUM(b.quantity_in_stock) < 10;

  --Có trên batch(product_id):
product_id là FK, B‑Tree index giúp join nhanh với products và gom nhóm trên cột này. Khi GROUP BY, engine có thể sử dụng index ordering của product_id để nhóm liên tục mà không phải sort ngoài.

  CREATE INDEX idx_batch_product ON batch(product_id);

--3. Top 3 khách hàng chi tiêu cao nhất
SELECT c.customer_id, c.fullname, SUM(o.total_amount) AS total_spent
FROM customer c
JOIN orders o USING(customer_id)
GROUP BY c.customer_id, c.fullname
ORDER BY total_spent DESC
LIMIT 3;

có thể dùng index vì Join theo customer_id diễn ra nhiều, B‑Tree trên orders(customer_id) cho phép nhanh chóng tìm tất cả đơn của một khách mà không full scan orders. Đồng thời, khi thực hiện GROUP BY và ORDER BY, engine có thể tận dụng phần ordering trong index để tạo trước dãy giá trị customer_id, giảm chi phí sort kết quả cuối.

CREATE INDEX idx_orders_customer ON orders(customer_id);

--4.  Sản phẩm chưa bán (không xuất hiện trong order_details)

SELECT p.product_id, p.product_name
FROM products p
WHERE NOT EXISTS (
  SELECT 1
  FROM batch b
  JOIN order_details od USING(batch_id)
  WHERE b.product_id = p.product_id
);

có thể dùng index vì Ở mệnh đề con, b.product_id = p.product_id sẽ lặp qua từng p; nếu index trên batch(product_id), hệ thống chỉ theo index tìm liền các batch cùng sản phẩm rồi kiểm tra tiếp order_details. B‑Tree cho phép range scan nhanh nhóm batch của mỗi sản phẩm. Mặt khác, index trên order_details(batch_id) (FK) vốn đã có sẵn cũng hỗ trợ join tiếp.

CREATE INDEX idx_batch_product ON batch(product_id);

--5. Doanh thu theo nhóm danh mục trong tháng 5/2025

SELECT c.category_name, SUM(od.quantity*p.price_with_tax) AS revenue
FROM orders o
JOIN order_details od USING(order_id)
JOIN batch b USING(batch_id)
JOIN products p USING(product_id)
JOIN categories c USING(category_id)
WHERE o.order_date BETWEEN '2025-05-01' AND '2025-05-31'
GROUP BY c.category_name
ORDER BY revenue DESC;

có thể dùng index vì orders(order_date): B‑Tree index tối ưu truy vấn range tháng 5, khoanh vùng chỉ ~8% dữ liệu.
order_details(order_id): index giúp join nhanh orders → order_details.
Các join tiếp theo trên batch, products, categories đều tận dụng FK index. Nhờ đó, bộ máy planner chọn kế hoạch index-nested-loop thay vì full scan nhiều bảng.

CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_od_order ON order_details(order_id);

--6. liệt kê sản phẩm và tổng số lượng bán được ngoài tháng 5/2025:

SELECT 
  p.product_id,
  p.product_name,
  SUM(od.quantity) AS total_quantity_sold
FROM orders o
JOIN order_details od USING(order_id)
JOIN batch b USING(batch_id)
JOIN products p USING(product_id)
WHERE o.order_date < '2025-05-01' OR o.order_date > '2025-05-31'
GROUP BY p.product_id, p.product_name
ORDER BY total_quantity_sold DESC;

không thể dùng index vì số lượng sản phẩm được bán ngoài tháng 5 là rất lớn so với số lượng sản phẩm bán trong tháng 5. Do đó khi sử dụng index sẽ chậm hơn rất nhiều so với việc tìm kiếm tuần tự. Ngoài ra đây còn có phép OR, một phép toán không sử dụng được index

--7. Danh sách nhân viên từng làm ca “15:00–22:00”

SELECT DISTINCT e.employee_id, e.lastname||' '||e.firstname AS fullname
FROM working w
JOIN schedule s USING(schedule_id)
JOIN employee e USING(employee_id)
WHERE s.start_time='15:00:00' AND s.end_time='22:00:00';

có thể dùng index vì Hai điều kiện s.start_time và s.end_time cùng lúc phù hợp cho composite index (start_time,end_time). B‑Tree composite giúp planner nhanh xác định ca đúng khung, chỉ scan những leaf page cần, rồi mới join tiếp đến working và employee. 

CREATE INDEX idx_schedule_time ON schedule(start_time,end_time);

--8. Số đơn hàng theo phương thức thanh toán

SELECT payment_method, COUNT(*) AS cnt
FROM orders
GROUP BY payment_method; 

không thể dùng index vì payment_method chỉ vài giá trị (ví dụ 'Tien mat', 'Chuyen khoan'), selectivity rất thấp, B‑Tree index phân tán kém, và optimizer ưu tiên full table scan nhanh hơn lookup index rồi đọc hàng loạt pages lặp lại nhiều.

--9. Danh sách lô hàng sắp hết hạn trong 7 ngày tới

SELECT batch_id, expiration_date, quantity_in_stock
FROM batch
WHERE expiration_date BETWEEN CURRENT_DATE AND CURRENT_DATE+INTERVAL'7 days';

có thể dùng index vì Khoảng 7 ngày rất nhỏ so với thời gian tồn tại lô hàng (có thể nhiều tháng/năm), selectivity cao. B‑Tree trên batch(expiration_date) giúp range scan chính xác, chỉ truy cập leaf pages chứa các ngày sắp hết, tránh full scan. 

CREATE INDEX idx_batch_expiry ON batch(expiration_date);

--10. Tổng số lô hàng nhập theo nhà cung cấp

SELECT s.supplier_id, s.supplier_name, COUNT(im.import_id) AS import_count
FROM suppliers s
LEFT JOIN import_reports im USING(supplier_id)
GROUP BY s.supplier_id, s.supplier_name
ORDER BY import_count DESC;

có thể dùng index vì Join dựa trên supplier_id – B‑Tree trên import_reports(supplier_id) giúp nhanh chóng tập hợp các phiếu nhập của mỗi supplier, sau đó GROUP BY & ORDER BY trên kết quả nhóm cũng tận dụng ordering trong index phần nào.

CREATE INDEX idx_import_supplier ON import_reports(supplier_id);

--11. Doanh thu trung bình mỗi đơn hàng theo tháng năm 2025

SELECT to_char(order_date,'MM/YYYY') AS monthyear, AVG(total_amount) AS avg_revenue
FROM orders
WHERE order_date BETWEEN '2025-01-01' AND '2025-12-31'
GROUP BY monthyear
ORDER BY monthyear;

có thể dùng index vì Dù dùng to_char trong SELECT, điều kiện WHERE order_date BETWEEN … vẫn tận dụng B‑Tree trên orders(order_date) để lọc trước dữ liệu cho một năm. Sau đó, grouping trên kết quả con đã được giới hạn. 

CREATE INDEX idx_orders_date_fullyear ON orders(order_date);

--12. Nhân viên không từng làm ca nào trong tháng 5/2025

SELECT e.employee_id, e.lastname||' '||e.firstname AS fullname
FROM employee e
WHERE NOT EXISTS (
  SELECT 1
  FROM working w
  WHERE w.employee_id=e.employee_id
    AND w.work_date BETWEEN '2025-05-01' AND '2025-05-31'
);

có thể dùng index vì Correlated subquery lọc theo w.employee_id + range w.work_date rất phù hợp với composite index (employee_id, work_date) trên working. B‑Tree composite cung cấp lookup nhanh những ngày của mỗi nhân viên, giúp NOT EXISTS loại ngay những e có lịch, hiệu quả hơn full scan.

CREATE INDEX idx_working_emp_date ON working(employee_id, work_date);
