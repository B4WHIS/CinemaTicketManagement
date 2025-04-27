package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.UserDAO; // Thêm import cho UserDAO
import dbs.connectDB;
import model.Users;

public class Login_GUI extends JFrame implements ActionListener {
    private JLabel lblTenDN;
    private JTextField txtTenDN;
    private JPasswordField txtPw;
    private JLabel lblPw;
    private JButton btnDN;
    private JButton btnThoat;
    private UserDAO userDAO; // Thay UserManager bằng UserDAO
    private Connection connection;

    public Login_GUI(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO(connection); // Khởi tạo UserDAO với connection

        setTitle("Login");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Phần logo
        JLabel lblIconLogin = new JLabel();
        ImageIcon iconLogin = new ImageIcon("img/login.png");
        Image image = iconLogin.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        lblIconLogin.setIcon(new ImageIcon(image));
        lblIconLogin.setBorder(new EmptyBorder(30, 230, 0, 230));
        add(lblIconLogin, BorderLayout.NORTH);

        // Phần nhập
        JPanel pnlCent = new JPanel();

        // Tên đăng nhập
        JPanel pnlUser = new JPanel();
        pnlUser.setLayout(new BoxLayout(pnlUser, BoxLayout.Y_AXIS));
        pnlUser.add(lblTenDN = new JLabel("Tên đăng nhập"));
        lblTenDN.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        pnlUser.add(txtTenDN = new JTextField());
        txtTenDN.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTenDN.setPreferredSize(new Dimension(350, 30));

        // Mật khẩu
        JPanel pnlPw = new JPanel();
        pnlPw.setLayout(new BoxLayout(pnlPw, BoxLayout.Y_AXIS));
        pnlPw.add(lblPw = new JLabel("Mật khẩu"));
        lblPw.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        pnlPw.add(txtPw = new JPasswordField());
        txtPw.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPw.setPreferredSize(new Dimension(350, 30));

        // Btn đăng nhập và thoát
        JPanel pnlSouth = new JPanel();
        pnlSouth.add(btnDN = new JButton("Đăng nhập"));
        btnDN.setBackground(Color.BLUE);
        btnDN.setForeground(Color.WHITE);
        btnDN.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnDN.addActionListener(this);

        pnlSouth.add(btnThoat = new JButton("Thoát"));
        btnThoat.setBackground(Color.RED);
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnThoat.addActionListener(this);

        pnlCent.add(pnlUser);
        pnlCent.add(Box.createVerticalStrut(20));
        pnlCent.add(pnlPw);

        add(pnlCent, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDN) {
            String username = txtTenDN.getText();
            String password = new String(txtPw.getPassword());

            try {
                Users user = userDAO.loginUser(username, password); // Gọi UserDAO thay vì UserManager
                // Thêm log để kiểm tra user
                System.out.println("User after login in Login_GUI: " + (user != null ? user.toString() : "null"));
                if (user != null) {
                    System.out.println("FullName after login in Login_GUI: " + user.getFullName());
                    // Kiểm tra roleID để chuyển hướng giao diện
                    MainFrame mainFrame = new MainFrame(connection, user);
                    if (user.getRoleID() == 1) {
                        mainFrame.showMainGUI2(); // roleID = 1 thì vào AdminGUI (admin)
                    } else if (user.getRoleID() == 2) {
                        mainFrame.showMainGUI(); // roleID = 2 thì vào StaffGUI (staff)
                    }
                    mainFrame.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        } else if (e.getSource() == btnThoat) {
            connectDB.closeConnection();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = connectDB.getConnection();
            Login_GUI loginFrame = new Login_GUI(connection);
            loginFrame.setVisible(true);
        } catch (RuntimeException e) {
            System.err.println("Không thể kết nối tới cơ sở dữ liệu: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Không thể kết nối tới cơ sở dữ liệu. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}