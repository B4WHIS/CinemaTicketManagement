package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
    private AddMoviePanel addMoviePanel; // Lưu instance của AddMoviePanel
    private MainFrame mainFrame; // B: Added to reference MainFrame

    public mainGUI(Connection connection, CardLayout cardLayout, JPanel mainPanel, Users user, MainFrame mainFrame) { // B: Added MainFrame parameter
        this.connection = connection;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.user = user;
        this.mainFrame = mainFrame; // B: Store MainFrame reference
        this.movieManager = new MovieManager(connection);

        setLayout(new BorderLayout());

        // Khởi tạo AddMoviePanel một lần
        addMoviePanel = new AddMoviePanel(connection, this);
        mainPanel.add(addMoviePanel, "AddMovie");

        // ==== Menu ngang ====
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] menuItems = {"Dashboard", "Add Movie", "Available Movies", "Edit Screening", "Customers"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            if (item.equals("Add Movie")) {
                btn.addActionListener(e -> {
                    System.out.println("Opening AddMoviePanel");
                    cardLayout.show(mainPanel, "AddMovie");
                });
            } else if (item.equals("Available Movies")) {
                btn.addActionListener(e -> showMoviesPanel());
            }
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
        moviesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        moviesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        JLabel nameLabel = new JLabel(user.getUserName(), SwingConstants.CENTER);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(welcomeLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(avatar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(nameLabel);

        centerPanel.add(sidebar, BorderLayout.EAST);

        // Thêm DetailFilm_GUI vào mainPanel
        try {
            mainPanel.add(new DetailFilm_GUI(connection, -1, mainFrame), "MovieDetail"); // B: Added mainFrame parameter
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể khởi tạo trang chi tiết phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        // Thêm moviesPanel vào mainPanel
        mainPanel.add(moviesPanel, "Movies");

        // Tải toàn bộ phim ban đầu
        showMoviesPanel();
    }

    public void showMoviesPanel() {
        cardLayout.show(mainPanel, "Movies");
        loadMovies("");
    }

    public void loadMovies(String keyword) {
        moviesPanel.removeAll();
        try {
            List<Movies> movies = movieManager.getAllMovies();
            boolean foundMovies = false;

            for (Movies movie : movies) {
                if (!keyword.isEmpty() && !movie.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }

                foundMovies = true;

                JPanel movieCard = new JPanel(new BorderLayout());
                movieCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                movieCard.setPreferredSize(new Dimension(200, 350));

                JLabel img = new JLabel();
                img.setPreferredSize(new Dimension(200, 300));
                img.setHorizontalAlignment(SwingConstants.CENTER);

                if (movie.getImage() != null && !movie.getImage().isEmpty()) {
                    System.out.println("Loading image for movie: " + movie.getTitle() + ", path: " + movie.getImage());
                    File imageFile = new File(movie.getImage());
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon(movie.getImage());
                        Image scaledImage = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                        img.setIcon(new ImageIcon(scaledImage));
                    } else {
                        System.err.println("Image file not found: " + movie.getImage());
                        img.setText(movie.getTitle());
                        img.setOpaque(true);
                        img.setBackground(Color.LIGHT_GRAY);
                        img.setFont(new Font("Arial", Font.PLAIN, 14));
                    }
                } else {
                    System.out.println("No image for movie: " + movie.getTitle());
                    img.setText(movie.getTitle());
                    img.setOpaque(true);
                    img.setBackground(Color.LIGHT_GRAY);
                    img.setFont(new Font("Arial", Font.PLAIN, 14));
                }

                JButton btnBuy = new JButton("Đặt vé");
                btnBuy.setBackground(new Color(0, 102, 204));
                btnBuy.setForeground(Color.WHITE);
                btnBuy.addActionListener(e -> {
                    try {
                        DetailFilm_GUI detailPanel = new DetailFilm_GUI(connection, movie.getMovieID(), mainFrame); // B: Added mainFrame parameter
                        mainPanel.add(detailPanel, "MovieDetail");
                        cardLayout.show(mainPanel, "MovieDetail");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Không thể hiển thị chi tiết phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });

                movieCard.add(img, BorderLayout.CENTER);
                movieCard.add(btnBuy, BorderLayout.SOUTH);
                moviesPanel.add(movieCard);
            }

            if (!foundMovies && !keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phim nào phù hợp với từ khóa: " + keyword, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadMovies("");
                searchField.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }
}