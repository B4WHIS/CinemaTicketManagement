package service;

import dao.OrderDAO;
import dao.ProductOrderDAO;
import dao.PaymentMethodDAO;
import model.Orders;
import model.Product_Orders;
import model.PaymentMethod;
import java.sql.SQLException;
import java.util.List;
@SuppressWarnings("unused")

public class OrderManager {
    private OrderDAO orderDAO;
    private ProductOrderDAO productOrderDAO;
    private PaymentMethodDAO paymentMethodDAO;

    public OrderManager() {
        this.orderDAO = new OrderDAO();
        this.productOrderDAO = new ProductOrderDAO();
        this.paymentMethodDAO = new PaymentMethodDAO();
    }

    // Create
    public void createOrder(Orders order, List<Product_Orders> productOrders) throws SQLException {
        // Kiểm tra dữ liệu đầu vào
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

        // Tính tổng giá 
        double totalPrice = 0.0;
        for (Product_Orders po : productOrders) {
            if (po.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (po.getPrice() < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            totalPrice += po.getPrice() * po.getQuantity();
        }
        order.setTotalPrice(totalPrice);

        // Save
        orderDAO.saveOrder(order);

        // Save chi tiết đơn hàng
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

        // Lấy chi tiết đơn hàng
       
        List<Product_Orders> productOrders = productOrderDAO.getProductOrdersByOrder(orderID);

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
        }
        return orders;
    }

    // Lấy tất cả phương thức thanh toán 
    public List<PaymentMethod> getAllPaymentMethods() throws SQLException {
        return paymentMethodDAO.getAllPaymentMethods();
    }
}