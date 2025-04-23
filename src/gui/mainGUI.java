package gui;

import javax.swing.*;
import model.Movies;
import service.MovieManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
//b
public class mainGUI extends JFrame {
    private JPanel moviesPanel;
    private JTextField searchField;
    private MovieManager movieManager;

    public mainGUI() {
        movieManager = new MovieManager();

        setTitle("Movie Ticket System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ==== Menu ngang ====
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] menuItems = {"Dashboard", "Add Movie", "Available Movies", "Edit Screening", "Customers"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            menuPanel.add(btn);
        }
        add(menuPanel, BorderLayout.NORTH);

        // ==== Panel chính ====
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // ==== Tìm kiếm ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.addActionListener(e -> loadMovies(searchField.getText().trim()));
        searchPanel.add(new JLabel("Tìm phim:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // ==== Danh sách phim ====
        moviesPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

        JLabel nameLabel = new JLabel("Tên nhân viên", SwingConstants.CENTER);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(welcomeLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(avatar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(nameLabel);

        mainPanel.add(sidebar, BorderLayout.EAST);

        // Tải toàn bộ phim ban đầu
        loadMovies("");
        setVisible(true);
    }

    private void loadMovies(String keyword) {
        moviesPanel.removeAll();
        List<Movies> movies = movieManager.getAllMovies();

        for (Movies movie : movies) {
            if (!keyword.isEmpty() && !movie.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                continue;
            }

            JPanel movieCard = new JPanel(new BorderLayout());
            movieCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel img = new JLabel(movie.getTitle(), SwingConstants.CENTER);
            img.setPreferredSize(new Dimension(120, 180));
            img.setOpaque(true);
            img.setBackground(Color.LIGHT_GRAY); // Placeholder cho hình ảnh

            JButton btnBuy = new JButton("Đặt vé");

            movieCard.add(img, BorderLayout.CENTER);
            movieCard.add(btnBuy, BorderLayout.SOUTH);
            moviesPanel.add(movieCard);
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainGUI::new);
    }
}

