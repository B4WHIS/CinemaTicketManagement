package dbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.IntStream;

public class connectDB {
	public static Connection con;
	private static connectDB instance = new connectDB();
	public static connectDB getInstance() {
		return instance;
	}
	public void connect() {
		String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTickerManagement";
		try {
			con = DriverManager.getConnection(url);
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static Connection getConnection() {
		return con;
	}
	
}