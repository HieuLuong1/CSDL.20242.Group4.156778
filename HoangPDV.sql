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



