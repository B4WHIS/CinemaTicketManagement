package service;

import dao.OrderDAO;
import dao.ProductOrderDAO;
import dao.PaymentMethodDAO;
import model.Orders;
import model.Product_Orders;
import model.PaymentMethod;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderManager {
    private OrderDAO orderDAO;
    private ProductOrderDAO productOrderDAO;
    private PaymentMethodDAO paymentMethodDAO;

    public OrderManager(Connection connection) throws SQLException {
        this.orderDAO = new OrderDAO(connection);
        this.productOrderDAO = new ProductOrderDAO(connection);
        this.paymentMethodDAO = new PaymentMethodDAO(connection);
    }

    // Create
    public void createOrder(Orders order, List<Product_Orders> productOrders) throws SQLException {
        if (order.getUserID() == null || order.getUserID().getUserID() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (order.getPaymentMethod() == null || order.getPaymentMethod().getPaymentMethodID() <= 0) {
            throw new IllegalArgumentException("Invalid payment method");
        }
        if (productOrders == null || productOrders.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one product");
        }
        if (order.getOrderDate() == null) {
            throw new IllegalArgumentException("Order date cannot be null");
        }

        double totalPrice = 0.0;
        for (Product_Orders po : productOrders) {
            if (po.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (po.getTotalPrice() < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            totalPrice += po.getTotalPrice() * po.getQuantity();
        }
        order.setTotalAmount(totalPrice);

        orderDAO.saveOrder(order);

        for (Product_Orders po : productOrders) {
            po.setOrderID(order);
            productOrderDAO.saveProductOrder(po);
        }
    }

    // Read theo ID
    public Orders readOrder(int orderID) throws SQLException {
        if (orderID <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }

        Orders order = orderDAO.getOrderById(orderID);
        if (order == null) {
            throw new SQLException("Order with ID " + orderID + " not found");
        }

        List<Product_Orders> productOrders = productOrderDAO.getProductOrdersByOrder(orderID);
        order.setProductOrders(productOrders); // Gán productOrders vào order

        return order;
    }

    // Read: theo userID
    public List<Orders> readOrdersByUser(int userID) throws SQLException {
        if (userID <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        List<Orders> orders = orderDAO.getOrdersByUser(userID);
        for (Orders order : orders) {
            List<Product_Orders> productOrders = productOrderDAO.getProductOrdersByOrder(order.getOrderID());
            order.setProductOrders(productOrders); // Gán productOrders vào order
        }
        return orders;
    }

    // Lấy tất cả phương thức thanh toán
    public List<PaymentMethod> getAllPaymentMethods() throws SQLException {
        return paymentMethodDAO.getAllPaymentMethods();
    }

    // Lấy tất cả đơn hàng
    public List<Orders> getAllOrders() throws SQLException {
        return orderDAO.getAllOrders();
    }
}