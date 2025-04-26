package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.SQLException;
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
    private AddMoviePanel addMoviePanel;
    private JPanel centerContentPanel;
    private CardLayout centerCardLayout;
    private JScrollPane scrollPane;
    private MainFrame mainFrame; // B: Thêm tham chiếu MainFrame

    public mainGUI(Connection connection, CardLayout cardLayout, JPanel mainPanel, Users user, MainFrame mainFrame) {
        this.connection = connection;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.user = user;
        this.mainFrame = mainFrame; // B: Lưu MainFrame

        try {
            this.movieManager = new MovieManager(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể khởi tạo MovieManager: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setLayout(new BorderLayout());

        // ==== Menu ngang ====
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(Color.LIGHT_GRAY);
        String[] menuItems = {"Dashboard", "Add Movie", "Available Movies", "Edit Screening", "Customers"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            if (item.equals("Add Movie")) {
                btn.addActionListener(e -> {
                    System.out.println("Opening AddMoviePanel");
                    centerCardLayout.show(centerContentPanel, "AddMovie");
                });
            } else if (item.equals("Available Movies")) {
                btn.addActionListener(e -> showMoviesPanel());
            }
            menuPanel.add(btn);
        }
        add(menuPanel, BorderLayout.NORTH);

        // ==== Panel chính ====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.BLUE);
        centerPanel.setPreferredSize(new Dimension(1200, 800));
        add(centerPanel, BorderLayout.CENTER);

        // ==== Tìm kiếm ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.YELLOW);
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.addActionListener(e -> loadMovies(searchField.getText().trim()));
        searchPanel.add(new JLabel("Tìm phim:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // ==== Khu vực nội dung chính với CardLayout ====
        centerCardLayout = new CardLayout();
        centerContentPanel = new JPanel(centerCardLayout);
        centerContentPanel.setBackground(Color.GREEN);
        centerContentPanel.setPreferredSize(new Dimension(1000, 600));

        // ==== Danh sách phim ====
        moviesPanel = new JPanel(new GridBagLayout());
        moviesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        moviesPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(moviesPanel, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1000, 600));
        centerContentPanel.add(scrollPane, "Movies");

        // ==== Thêm AddMoviePanel vào centerContentPanel ====
        addMoviePanel = new AddMoviePanel(connection, this);
        centerContentPanel.add(addMoviePanel, "AddMovie");

        // ==== Thêm DetailFilm_GUI vào centerContentPanel ====
        try {
            centerContentPanel.add(new DetailFilm_GUI(connection, -1, mainFrame), "MovieDetail"); // B: Truyền MainFrame
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể khởi tạo trang chi tiết phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        centerPanel.add(centerContentPanel, BorderLayout.CENTER);

        // ==== Sidebar ====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 600));
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

        // Tải toàn bộ phim ban đầu
        showMoviesPanel();

        // Đảm bảo giao diện được cập nhật
        revalidate();
        repaint();
    }

    public void showMoviesPanel() {
        System.out.println("Showing Movies panel");
        System.out.println("centerContentPanel component count: " + centerContentPanel.getComponentCount());
        centerCardLayout.show(centerContentPanel, "Movies");
        loadMovies("");
    }

    public void loadMovies(String keyword) {
        moviesPanel.removeAll();
        try {
            List<Movies> movies = movieManager.getAllMovies();
            System.out.println("Number of movies loaded: " + movies.size());
            if (movies.isEmpty()) {
                System.out.println("No movies found in the database.");
                JOptionPane.showMessageDialog(this, "Không có phim nào trong cơ sở dữ liệu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            boolean foundMovies = false;
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new java.awt.Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            int columnCount = 0;
            int rowCount = 0;

            for (Movies movie : movies) {
                if (movie.getMovieID() == 0) {
                    System.out.println("Skipping movie with MovieID 0");
                    continue;
                }

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

                String imagePath = movie.getImage();
                System.out.println("Original image path from database for MovieID " + movie.getMovieID() + ": " + imagePath);

                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        String resourcePath = "/img/movies/" + imagePath.substring(imagePath.lastIndexOf("/") + 1);
                        System.out.println("Resource path for getResource: " + resourcePath);

                        java.net.URL resourceURL = getClass().getResource(resourcePath);
                        if (resourceURL == null) {
                            System.err.println("Resource not found in classpath: " + resourcePath);
                            img.setText(movie.getTitle());
                            img.setOpaque(true);
                            img.setBackground(Color.LIGHT_GRAY);
                            img.setFont(new Font("Arial", Font.PLAIN, 14));
                        } else {
                            ImageIcon icon = new ImageIcon(resourceURL);
                            if (icon.getIconWidth() == -1) {
                                System.err.println("Failed to load image as resource: " + resourcePath);
                                img.setText(movie.getTitle());
                                img.setOpaque(true);
                                img.setBackground(Color.LIGHT_GRAY);
                                img.setFont(new Font("Arial", Font.PLAIN, 14));
                            } else {
                                Image scaledImage = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                                img.setIcon(new ImageIcon(scaledImage));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image for MovieID " + movie.getMovieID() + ": " + e.getMessage());
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
                        DetailFilm_GUI detailPanel = new DetailFilm_GUI(connection, movie.getMovieID(), mainFrame); // B: Truyền MainFrame
                        centerContentPanel.add(detailPanel, "MovieDetail");
                        centerCardLayout.show(centerContentPanel, "MovieDetail");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Không thể hiển thị chi tiết phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });

                movieCard.add(img, BorderLayout.CENTER);
                movieCard.add(btnBuy, BorderLayout.SOUTH);

                gbc.gridx = columnCount;
                gbc.gridy = rowCount;
                moviesPanel.add(movieCard, gbc);

                columnCount++;
                if (columnCount >= 4) {
                    columnCount = 0;
                    rowCount++;
                }

                System.out.println("Added movie card for: " + movie.getTitle() + " (MovieID: " + movie.getMovieID() + ")");
            }

            if (!foundMovies && !keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phim nào phù hợp với từ khóa: " + keyword, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadMovies("");
                searchField.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phim: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
        scrollPane.getVerticalScrollBar().setValue(0);
        centerContentPanel.revalidate();
        centerContentPanel.repaint();
        revalidate();
        repaint();
    }
}