package gui;

import java.awt.CardLayout;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.Product_Orders;
import model.Users;

// Lớp đại diện cho cửa sổ chính của ứng dụng
public class MainFrame extends JFrame {
    // CardLayout để chuyển đổi giữa các panel giao diện
    private CardLayout cardLayout;
    // Panel chính chứa tất cả các panel giao diện
    private JPanel mainPanel;
    // ID của người dùng hiện tại
    private int userID;
    // ID của suất chiếu được chọn
    private int showtimeID;
    // ID của vé được chọn
    private int ticketID;
    // Danh sách lưu trữ sản phẩm trong giỏ hàng
    private ArrayList<Product_Orders> cart;
    // Kết nối cơ sở dữ liệu
    private Connection connection;
    // Đối tượng người dùng hiện tại
    private Users user;

    // Constructor để khởi tạo cửa sổ chính với kết nối cơ sở dữ liệu và người dùng
    public MainFrame(Connection connection, Users user) {
        // Lưu trữ kết nối và người dùng được cung cấp
        this.connection = connection;
        this.user = user;
        // Thiết lập userID từ đối tượng người dùng
        this.userID = user.getUserID();
        // Khởi tạo showtimeID và ticketID về 0
        this.showtimeID = 0;
        this.ticketID = 0;
        // Khởi tạo giỏ hàng rỗng
        this.cart = new ArrayList<>();

        // Khởi tạo CardLayout và panel chính
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Thêm panel chính vào cửa sổ
        add(mainPanel);

        // Thiết lập thuộc tính cửa sổ
        setTitle("Quản lý Vé Rạp Chiếu Phim");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa cửa sổ trên màn hình
    }

    // Phương thức hiển thị giao diện chính cho nhân viên
    public void showMainGUI() {
        // Tạo panel StaffGUI mới
        StaffGUI mainGuiPanel = new StaffGUI(connection, cardLayout, mainPanel, user, this);
        // Thêm panel vào panel chính với tên "MainGUI"
        mainPanel.add(mainGuiPanel, "MainGUI");
        // Hiển thị panel "MainGUI"
        cardLayout.show(mainPanel, "MainGUI");
    }

    // Phương thức hiển thị giao diện chính cho quản trị viên (roleID = 1)
    public void showMainGUI2() {
        // Tạo panel AdminGUI mới
        AdminGUI mainGui2Panel = new AdminGUI(connection, cardLayout, mainPanel, user, this);
        // Thêm panel vào panel chính với tên "MainGUI2"
        mainPanel.add(mainGui2Panel, "MainGUI2");
        // Hiển thị panel "MainGUI2"
        cardLayout.show(mainPanel, "MainGUI2");
    }


    public void clearCart() {
        // Xóa toàn bộ sản phẩm trong giỏ hàng
        cart.clear();
    }

    public int getUserID() {
        // Trả về userID
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getShowtimeID() {
        return showtimeID;
    }

    public void setShowtimeID(int showtimeID) {
        this.showtimeID = showtimeID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    // Phương thức hiển thị một màn hình cụ thể theo tên và panel
    public void showScreen(String screenName, JPanel screenPanel) {
        mainPanel.add(screenPanel, screenName);
        cardLayout.show(mainPanel, screenName);
    }

    // Phương thức lấy kết nối cơ sở dữ liệu
    public Connection getConnection() {
        return connection;
    }

    public Users getUser() {
        return user;
    }
}