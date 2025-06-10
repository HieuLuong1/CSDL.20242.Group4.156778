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

--nếu viết theo cách này, order_id = 1 sẽ được lọc ra đầu tiên để giảm kích cỡ của bảng sau đó sẽ thực hiện join.
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
--Lê Vũ Nguyên Hoàng (20235723):
-- 1. Cho biết 5 sản phẩm có đơn giá cao nhất trong siêu thị
SELECT product_id, product_name, unit, price_with_tax
FROM products
ORDER BY price_with_tax DESC
LIMIT 5;
-- Hệ thống phải quét toàn bộ bảng, sau đó sắp xếp toàn bộ theo price_with_tax giảm dần rồi mới lấy ra được 5 dòng đầu tiên
-- Do đó có thể tạo một chỉ mục cho price_with_tax theo thứ tự giảm để có thể truy cập trục tiếp vào các phần tử lớn nhất mà không cần sắp xếp toàn bộ bảng
CREATE INDEX idx_products_price_desc ON products USING btree (price_with_tax DESC);

-- 2. Hiển thị toàn bộ danh sách hóa đơn gồm mã hóa đơn, ngày đặt hàng, tên khách hàng, tổng tiền và hình thức thanh toán
SELECT o.order_id, o.order_date, c.fullname AS customer_name, o.total_amount, o.payment_method
FROM orders o
JOIN customer c ON o.customer_id = c.customer_id;

-- Cách viết khác dùng subquery
SELECT o.order_id, o.order_date, (SELECT fullname FROM customer c WHERE c.customer_id = o.customer_id) AS customer_name, o.total_amount, o.payment_method
FROM orders o;

-- Ở cách viết 2,  hệ thống quét toàn bộ bảng orders và đối với mỗi bản ghi, hệ thống quét tiếp toàn bộ bảng customer để tìm bản ghi có customer_id trùng khớp. 
-- Do đó ở cách viết 1, sử dụng JOIN để ghép 2 bảng với nhau sẽ có tốc độ nhanh hơn

-- 3. Thống kê số loại sản phẩm, số lượng hàng tồn kho của mỗi danh mục hàng
SELECT ca.category_name, COUNT(p.product_id) AS so_loai_san_pham, SUM(p.quantity_in_stock) AS tong_so_hang_trong_kho
FROM categories ca
LEFT JOIN products p ON ca.category_id = p.category_id
GROUP BY ca.category_name;

-- Hệ thống quét toàn bộ bảng products để tìm các bản ghi có category_id tương ứng với từng bản ghi của bảng categories, sau đó nhóm các bản ghi theo danh mục hàng, rồi thực hiện COUNT và tính SUM

-- 4. Thống kê số lần nhập hàng từ các nhà cung cấp, và đưa ra giá trị đơn nhập cao nhất và thấp nhất của từng nhà cung cấp
SELECT s.supplier_id, s.supplier_name, COUNT(ir.import_id) AS so_lan_nhap, MAX(ir.total_amount) AS tong_cao_nhat, MIN(ir.total_amount) AS tong_thap_nhat
FROM suppliers s
LEFT JOIN import_reports ir ON s.supplier_id = ir.supplier_id
GROUP BY s.supplier_id, s.supplier_name;

-- Hệ thống quét toàn bộ bảng import_reports để tìm các bản ghi có supplier_id tương ứng với mỗi bản ghi của bảng suppliers, nhóm các bản ghi theo nhà cung cấp rồi COUNT, tìm MAX, MIN

-- 5. Đưa ra danh sách khách hàng đã đến siêu thị mua hàng vào tháng 5 năm 2025

SELECT DISTINCT c.customer_id, c.fullname
FROM customer c
JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_date BETWEEN '2025-05-01' AND '2025-05-31'
ORDER BY c.fullname;

-- Hệ thống quét bảng orders và lọc các đơn hàng có order_date nằm trong tháng 5/2025. Sau đó JOIN với bảng customer theo customer_id, và loại bỏ trùng lặp bằng DISTINCT. Cuối cùng, kết quả được sắp xếp theo tên khách hàng.
-- Có thể tạo chỉ mục theo order_date và customer_id để tăng tốc độ lọc các đơn hàng trong tháng 5 năm 202 5 và tăng tốc độ JOIN 2 bảng theo customer_id

CREATE INDEX idx_orders_date_customer ON orders(order_date, customer_id);

-- 6. Liệt kê chi tiết các biên bản kiểm kê trong năm 2025
SELECT cr.report_id, cr.check_date, b.batch_id, b.product_id, b.quantity_in_stock AS so_luong_ghi_nhan, cd.real_quantity AS so_luong_thuc_te
FROM check_reports cr
JOIN check_details cd ON cr.report_id = cd.report_id
JOIN batch b ON cd.batch_id = b.batch_id
WHERE cr.check_date >= '2025-01-01'
ORDER BY cr.check_date, cr.report_id, b.batch_id;

-- Hệ thống quét toàn bộ bảng check_reports để lọc ra các bản ghi có ngày đúng điều kiện, sau đó JOIN với 2 bảng check_details, batch dựa trên id tương ứng và đưa ra thông tin

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

-- Sử dụng WITH để tạo ra 3 bảng phụ thu là tổng doanh thu từ orders trong tháng 5 năm 2025, chi_luong là tổng số tiền để trả lương cho nhân viên trong tháng 5 năm 2025, chi_nhap là tổng số tiền nhập hàng trong tháng 5 năm 2025
-- Rồi tổng hợp lại thành lợi nhuận của siêu thị
-- Có thể tạo các chỉ mục với thời gian trên các bảng để lọc thời gian nhanh hơn
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

-- Khi nhập vào employee_id, hệ thống sẽ quét bảng working để tìm ra bản ghi có employee_id tương ứng và có ngày nằm trong tháng 5 năm 2025, sau đó thống kê trạng thái từng ngày theo 3 loại đúng giờ, muộn, vắng
-- và trả kết quả ra một bảng mới 


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

-- Khi nhập vào số ngày n_days, hệ thống quét bảng batch để tìm những lô hàng có hạn sử dụng từ ngày hiện tại đến sau n_days ngày nữa, sau đó JOIN với bảng products thông qua product_id để lấy được tên sản phẩm
-- Cuối cùng trả kết quả ra bảng với thông tin hàng đó và số ngày còn lại

--Phạm Đào Việt Hoàng (20235727):
--1. Xem tất cả các đơn hàng của khách hàng "Nguyen Minh Khoa", sắp xếp theo ngày mua.

	SELECT o.order_id, o.order_date, o.total_amount, o.payment_method
	FROM orders o
	JOIN customer c ON o.customer_id = c.customer_id
	WHERE c.fullname = 'Nguyen Minh Khoa'
	ORDER BY o.order_date DESC;

  -- chỉ mục nên tạo
	CREATE INDEX idx_customer_fullname ON customer(fullname);
	CREATE INDEX idx_orders_customer_id ON orders(customer_id);

--2. Hiển thị tên sản phẩm, số lượng và giá trong hóa đơn có order_id = 1.

	SELECT p.product_name, od.quantity, p.price_with_tax
	FROM order_details od
	JOIN batch b ON od.batch_id = b.batch_id
	JOIN products p ON b.product_id = p.product_id
	WHERE od.order_id = 1;

  -- chỉ mục nên tạo
	CREATE INDEX idx_batch_product_id ON batch(product_id);
--3. Lấy danh sách sản phẩm có số lượng tồn kho < 50.

	SELECT product_name, quantity_in_stock
	FROM products
	WHERE quantity_in_stock < 50;	
  
  -- chỉ mục nên tạo
	CREATE INDEX idx_products_quantity_in_stock ON products(quantity_in_stock);

--4. Lấy lương tháng 5/2025 của nhân viên "An Nguyen".

	SELECT s.monthyear, s.actual_salary
	FROM salary s
	JOIN employee e ON s.employee_id = e.employee_id
	WHERE e.firstname = 'An' AND e.lastname = 'Nguyen' AND s.monthyear = '5/2025';

  -- chỉ mục nên tạo
	CREATE INDEX idx_salary_employee_month ON salary(employee_id, monthyear);
	CREATE INDEX idx_employee_name ON employee(firstname, lastname);

--5. Lấy tổng tiền thu được từ đơn hàng theo từng ngày.

	SELECT order_date, SUM(total_amount) AS daily_revenue
	FROM orders
	GROUP BY order_date
	ORDER BY order_date DESC;

  -- chỉ mục nên tạo
	CREATE INDEX idx_orders_order_date ON orders(order_date);

--6. Liệt kê 5 sản phẩm được bán nhiều nhất.

	SELECT p.product_name, SUM(od.quantity) AS total_sold
	FROM order_details od
	JOIN batch b ON od.batch_id = b.batch_id
	JOIN products p ON b.product_id = p.product_id
	GROUP BY p.product_name
	ORDER BY total_sold DESC
	LIMIT 5;

  -- chỉ mục nên tạo
	CREATE INDEX idx_batch_product_id ON batch(product_id);

--7. Tìm các lô có hạn sử dụng trước ngày 2025-12-31.
	
	SELECT batch_id, product_id, expiration_date
	FROM batch
	WHERE expiration_date < '2025-12-31'
	ORDER BY expiration_date;

  -- chỉ mục cần tạo
	CREATE INDEX idx_batch_expiration ON batch(expiration_date);

--8. Đếm số ngày "Đi làm" (status = 'D') của mỗi nhân viên trong tháng 5/2025.

	SELECT e.firstname, e.lastname, COUNT(*) AS workdays
	FROM working w
	JOIN employee e ON w.employee_id = e.employee_id
	WHERE w.status = 'D'
  	AND EXTRACT(MONTH FROM w.work_date) = 5
  	AND EXTRACT(YEAR FROM w.work_date) = 2025
	GROUP BY e.employee_id, e.firstname, e.lastname
	ORDER BY workdays DESC;

  -- chỉ mục cần tạo
	CREATE INDEX idx_working_employee_date_status ON working(employee_id, work_date, status);

--9. Tính tổng tiền nhập hàng từ mỗi nhà cung cấp.
	
	SELECT s.supplier_name, SUM(ir.total_amount) AS total_spent
	FROM import_reports ir
	JOIN suppliers s ON ir.supplier_id = s.supplier_id
	GROUP BY s.supplier_name
	ORDER BY total_spent DESC;

  -- chỉ mục cần tạo
	CREATE INDEX idx_import_reports_supplier ON import_reports(supplier_id);

--10. Xác định ngày nhập gần nhất cho từng sản phẩm.

	SELECT p.product_name, MAX(b.import_date) AS last_import
	FROM batch b
	JOIN products p ON b.product_id = p.product_id
	GROUP BY p.product_name
	ORDER BY last_import DESC;

  -- chỉ mục cần tạo
	CREATE INDEX idx_batch_product_import_date ON batch(product_id, import_date);