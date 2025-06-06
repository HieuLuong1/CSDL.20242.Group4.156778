import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:postgresql://localhost:5432/market";
	private static final String USER = "admin_market";
	private static final String PASSWORD = "levunguyenhoang";

	public static Connection getConnection() throws SQLException {
		try {
			// Đăng ký driver PostgreSQL
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("PostgreSQL Driver not found", e);
		}
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}

