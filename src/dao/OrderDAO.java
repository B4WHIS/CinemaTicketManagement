package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Orders;
import model.PaymentMethod;
import model.Product_Orders;
import model.Products;
import model.Rooms;
import model.Seats;
import model.Users;

public class OrderDAO { private Connection connection;

public OrderDAO(Connection connection) {
    this.connection = connection;
}

public List<Orders> getAllOrders() throws SQLException {
    List<Orders> ordersList = new ArrayList<>();
    String sql = "SELECT o.OrderID, o.UserID, o.OrderDate, o.TotalAmount, o.PaymentMethodID, " +
                 "m.Title AS MovieTitle, CONVERT(varchar, s.StartTime, 108) AS ShowtimeStartTime, r.RoomName " +
                 "FROM Orders o " +
                 "LEFT JOIN Tickets t ON o.OrderID = t.OrderID " +
                 "LEFT JOIN Showtimes s ON t.ShowtimeID = s.ShowtimeID " +
                 "LEFT JOIN Movies m ON s.MovieID = m.MovieID " +
                 "LEFT JOIN Rooms r ON s.RoomID = r.RoomID";

    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            Orders order = new Orders();
            order.setOrderID(rs.getInt("OrderID"));
            order.setUserID(new Users(rs.getInt("UserID")));
            order.setOrderDate(rs.getDate("OrderDate"));
            order.setTotalAmount(rs.getDouble("TotalAmount"));
            order.setPaymentMethod(new PaymentMethod(rs.getInt("PaymentMethodID")));
            order.setMovieTitle(rs.getString("MovieTitle"));
            order.setShowtimeStartTime(rs.getString("ShowtimeStartTime"));
            order.setRoomName(rs.getString("RoomName"));

            order.setSeats(getSeatsForOrder(order.getOrderID()));
            order.setProductOrders(getProductOrdersForOrder(order.getOrderID()));

            ordersList.add(order);
        }
    }
    return ordersList;
}

private List<Seats> getSeatsForOrder(int orderID) throws SQLException {
    List<Seats> seats = new ArrayList<>();
    String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName " +
                 "FROM Seats s " +
                 "JOIN Tickets t ON s.SeatID = t.SeatID " +
                 "JOIN Rooms r ON s.RoomID = r.RoomID " +
                 "WHERE t.OrderID = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, orderID);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Seats seat = new Seats();
                seat.setSeatID(rs.getInt("SeatID"));
                seat.setSeatNumber(rs.getString("SeatNumber"));
                seat.setRoom(new Rooms(rs.getInt("RoomID"), rs.getString("RoomName"), 0, "..."));
                seats.add(seat);
            }
        }
    }
    return seats;
}

private List<Product_Orders> getProductOrdersForOrder(int orderID) throws SQLException {
    List<Product_Orders> productOrders = new ArrayList<>();
    String sql = "SELECT po.ProductOrderID, po.ProductID, po.Quantity, po.TotalPrice, p.ProductName " +
                 "FROM ProductOrders po " +
                 "JOIN Products p ON po.ProductID = p.ProductID " +
                 "WHERE po.OrderID = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, orderID);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product_Orders po = new Product_Orders();
                po.setProductOrderID(rs.getInt("ProductOrderID"));
                po.setQuantity(rs.getInt("Quantity"));
                po.setTotalPrice(rs.getDouble("TotalPrice"));
                Products product = new Products();
                product.setProductID(rs.getInt("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                po.setProduct(product);
                productOrders.add(po);
            }
        }
    }
    return productOrders;
}

public void saveOrder(Orders order) throws SQLException {
    // Lấy OrderID lớn nhất hiện tại
    int newOrderID = 1; // Giá trị mặc định nếu bảng rỗng
    String getMaxIdSql = "SELECT MAX(OrderID) FROM Orders";
    try (PreparedStatement stmt = connection.prepareStatement(getMaxIdSql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            newOrderID = rs.getInt(1) + 1;
        }
    }

    // Chèn đơn hàng với OrderID đã tạo
    String sql = "INSERT INTO Orders (OrderID, UserID, OrderDate, TotalAmount, PaymentMethodID) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, newOrderID);
        stmt.setInt(2, order.getUserID().getUserID());
        stmt.setDate(3, new java.sql.Date(order.getOrderDate().getTime()));
        stmt.setDouble(4, order.getTotalAmount());
        stmt.setInt(5, order.getPaymentMethod().getPaymentMethodID());
        stmt.executeUpdate();

        // Cập nhật OrderID cho đối tượng order
        order.setOrderID(newOrderID);
    }
}

public Orders getOrderById(int orderID) throws SQLException {
    String sql = "SELECT o.OrderID, o.UserID, o.OrderDate, o.TotalAmount, o.PaymentMethodID " +
                 "FROM Orders o WHERE o.OrderID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, orderID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Orders order = new Orders();
                order.setOrderID(rs.getInt("OrderID"));
                order.setUserID(new Users(rs.getInt("UserID")));
                order.setOrderDate(rs.getDate("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setPaymentMethod(new PaymentMethod(rs.getInt("PaymentMethodID")));
                return order;
            }
        }
    }
    return null;
}

public List<Orders> getOrdersByUser(int userID) throws SQLException {
    List<Orders> ordersList = new ArrayList<>();
    String sql = "SELECT o.OrderID, o.UserID, o.OrderDate, o.TotalAmount, o.PaymentMethodID " +
                 "FROM Orders o WHERE o.UserID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userID);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Orders order = new Orders();
                order.setOrderID(rs.getInt("OrderID"));
                order.setUserID(new Users(rs.getInt("UserID")));
                order.setOrderDate(rs.getDate("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setPaymentMethod(new PaymentMethod(rs.getInt("PaymentMethodID")));
                ordersList.add(order);
            }
        }
    }
    return ordersList;
}

}