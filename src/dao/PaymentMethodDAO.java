package dao;

import model.PaymentMethod;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAO {
    
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:1433/databaseName=CinemaTickerManagement"; 
        String user = "sa";
        String password = "sapassword"; 
        return DriverManager.getConnection(url, user, password);
    }


    // Lấy tất cả phương thức thanh toán
    public List<PaymentMethod> getAllPaymentMethods() throws SQLException {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        String query = "SELECT * FROM PaymentMethods";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PaymentMethod pm = new PaymentMethod();
                pm.setPaymentMethodID(rs.getInt("paymentMethodID"));
                pm.setPaymentMethodName(rs.getString("paymentMethodName"));
                paymentMethods.add(pm);
            }
        }
        return paymentMethods;
    }

    // Thêm phương thức thanh toán mới
    public void addPaymentMethod(PaymentMethod paymentMethod) throws SQLException {
        String query = "INSERT INTO PaymentMethods (paymentMethodName) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, paymentMethod.getPaymentMethodName());
            stmt.executeUpdate();
        }
    }

    // Cập nhật phương thức thanh toán
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws SQLException {
        String query = "UPDATE PaymentMethods SET paymentMethodName = ? WHERE paymentMethodID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, paymentMethod.getPaymentMethodName());
            stmt.setInt(2, paymentMethod.getPaymentMethodID());
            stmt.executeUpdate();
        }
    }

    // Xóa phương thức thanh toán
    public void deletePaymentMethod(int paymentMethodID) throws SQLException {
        String query = "DELETE FROM PaymentMethods WHERE paymentMethodID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, paymentMethodID);
            stmt.executeUpdate();
        }
    }
}