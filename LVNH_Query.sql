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
WHERE cr.check_date >= '2025-06-01'
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
JOIN orders o ON c.customer_id = o.customer_id
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
