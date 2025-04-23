package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login_GUI extends JFrame {

    private JLabel lblTenDN;
    private JTextField txtTenDN;
    private JTextField txtPw;
    private JLabel lblPw;
	private JButton btnDN;
	private JButton btnThoat;

    public Login_GUI() {
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

        //Tên đăng nhập
        JPanel pnlUser = new JPanel();
        pnlUser.setLayout(new BoxLayout(pnlUser, BoxLayout.Y_AXIS));
        pnlUser.add(lblTenDN = new JLabel("Tên đăng nhập"));
        lblTenDN.setFont(new Font("Times new roman", ABORT, 20));
        pnlUser.add(txtTenDN = new JTextField());
        txtTenDN.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTenDN.setPreferredSize(new Dimension(350, 30));
        

        //Mật khẩu
        JPanel pnlPw = new JPanel();
        pnlPw.setLayout(new BoxLayout(pnlPw, BoxLayout.Y_AXIS));
        pnlPw.add(lblPw = new JLabel("Mật khẩu"));
        lblPw.setFont(new Font("Times new roman", ABORT, 20));
        pnlPw.add(txtPw = new JPasswordField());
        txtPw.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPw.setPreferredSize(new Dimension(350, 30));
        add(pnlCent, BorderLayout.CENTER);
        
        //Btn đăng nhập và thoát
        JPanel pnlSouth = new JPanel();
        pnlSouth.add(btnDN = new JButton("Đăng nhập"));
        btnDN.setBackground(Color.BLUE);
        btnDN.setForeground(Color.WHITE);
        btnDN.setFont(new Font("Times new roman", Font.BOLD, 16));
        pnlSouth.add(btnThoat = new JButton("Thoát"));
        btnThoat.setBackground(Color.RED);
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFont(new Font("Times new roman", Font.BOLD, 16));
        
        pnlCent.add(pnlUser);
        pnlCent.add(Box.createVerticalStrut(20)); 
        pnlCent.add(pnlPw);
        
        add(pnlSouth, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        Login_GUI login_GUI = new Login_GUI();
        login_GUI.setVisible(true);
    }
}