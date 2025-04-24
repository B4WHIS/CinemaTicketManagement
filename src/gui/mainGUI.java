package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.Movies;
import model.Users;
import service.MovieManager;

public class mainGUI extends JPanel {
    private JPanel moviesPanel;
    private JTextField searchField;
    private MovieManager movieManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection connection;
    private Users user;

    public mainGUI(Connection connection, CardLayout cardLayout, JPanel mainPanel, Users user) {
        this.connection = connection;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.user = user;
        this.movieManager = new MovieManager(connection);

        setLayout(new BorderLayout());

        // ==== Menu ngang ====
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] menuItems = {"Dashboard", "Add Movie", "Available Movies", "Edit Screening", "Customers"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            menuPanel.add(btn);
        }
        add(menuPanel, BorderLayout.NORTH);

        // ==== Panel chính ====
        JPanel centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        // ==== Tìm kiếm ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.addActionListener(e -> loadMovies(searchField.getText().trim()));
        searchPanel.add(new JLabel("Tìm phim:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // ==== Danh sách phim ====
        moviesPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ==== Sidebar ====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("👋 Welcome", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(100, 100));
        avatar.setMaximumSize(new Dimension(100, 100));
        avatar.setOpaque(true);
        avatar.setBackground(Color.GRAY);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(user.getUserName(), SwingConstants.CENTER); // Hiển thị tên người dùng
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(welcomeLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(avatar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(nameLabel);

        centerPanel.add(sidebar, BorderLayout.EAST);

        // Tải toàn bộ phim ban đầu
        loadMovies("");
    }

    private void loadMovies(String keyword) {
        moviesPanel.removeAll();
        try {
            List<Movies> movies = movieManager.getAllMovies();
            for (Movies movie : movies) {
                if (!keyword.isEmpty() && !movie.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }

                JPanel movieCard = new JPanel(new BorderLayout());
                movieCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JLabel img = new JLabel(movie.getTitle(), SwingConstants.CENTER);
                img.setPreferredSize(new Dimension(200, 300));
                img.setOpaque(true);
                img.setBackground(Color.LIGHT_GRAY);

                JButton btnBuy = new JButton("Đặt vé");
                btnBuy.addActionListener(e -> {
                    // Mở MovieDetailFrame với movieID
                    cardLayout.show(mainPanel, "MovieDetail");
                });

                movieCard.add(img, BorderLayout.CENTER);
                movieCard.add(btnBuy, BorderLayout.SOUTH);
                moviesPanel.add(movieCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }
}