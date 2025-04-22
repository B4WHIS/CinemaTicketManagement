package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import model.Movies;
import model.Rooms;
import model.Showtimes;
import model.Showtimes;
import service.MovieManager;
import service.RoomManager;
import service.ShowTimeManager;

public class DetailFilm_GUI extends JFrame {
    private MovieManager movieManager;
    private RoomManager roomManager;
    private ShowTimeManager showtimeManager;
    
    private JComboBox<String> movieComboBox;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> dateComboBox;
    private JComboBox<String> timeComboBox;
    private JTextArea movieDetails;

    public DetailFilm_GUI() throws SQLException {
        movieManager = new MovieManager();
        roomManager = new RoomManager();
        showtimeManager = new ShowTimeManager();

        setTitle("Movie Booking");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createNavigationBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createNavigationBar() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tabs = {"Bảng điều khiển", "Thêm phim", "Phim có sẵn", "Edit Screening", "Khách hàng"};
        for (String tab : tabs) {
            JButton btn = new JButton(tab);
            navPanel.add(btn);
        }
        return navPanel;
    }

    private JPanel createMainContent() {
        movieComboBox = new JComboBox<>();

        JPanel leftPanel = createMovieInfoPanel();
        JPanel rightPanel = createBookingPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(600); // Phần thông tin phim chiếm 600px, phần đặt vé còn lại
        splitPane.setResizeWeight(0.7); // Ưu tiên phần bên trái
        splitPane.setContinuousLayout(true);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);

        return contentPanel;
    }

   

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        roomComboBox = new JComboBox<>();
        dateComboBox = new JComboBox<>();
        timeComboBox = new JComboBox<>();
        JButton bookButton = new JButton("Đặt vé");

        JLabel roomLabel = new JLabel("Chọn phòng chiếu:");
        JLabel dateLabel = new JLabel("Ngày chiếu:");
        JLabel timeLabel = new JLabel("Giờ chiếu:");

        // Căn trái các thành phần
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Set kích thước cố định
        roomComboBox.setMaximumSize(new Dimension(200, 30));
        dateComboBox.setMaximumSize(new Dimension(200, 30));
        timeComboBox.setMaximumSize(new Dimension(200, 30));

        panel.add(roomLabel);
        panel.add(roomComboBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dateLabel);
        panel.add(dateComboBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(timeLabel);
        panel.add(timeComboBox);
        panel.add(Box.createVerticalStrut(20));

        // Số lượng vé
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityTextLabel = new JLabel("Số lượng vé:");
        JButton minusButton = new JButton("-");
        JLabel quantityLabel = new JLabel("0");
        JButton plusButton = new JButton("+");
        JLabel priceLabel = new JLabel("0 VND");

        quantityPanel.add(quantityTextLabel);
        quantityPanel.add(minusButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusButton);
        quantityPanel.add(Box.createHorizontalStrut(20));
        quantityPanel.add(priceLabel);

        // Căn trái panel này luôn
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(quantityPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(bookButton);

        loadMovies();
        loadRooms();
        loadShowtimes();

        movieComboBox.addActionListener(e -> updateMovieInfo());

        return panel;
    }

    private JPanel createMovieInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());

        // Lấy thông tin phim đang được chọn
        if (movieComboBox.getSelectedItem() == null) {
            infoPanel.add(new JLabel("Chưa có phim nào được chọn"), BorderLayout.CENTER);
            return infoPanel;
        }

        String selected = movieComboBox.getSelectedItem().toString();
        int movieId = Integer.parseInt(selected.split(" - ")[0]);
        Movies movie = movieManager.getMovieByID(movieId);

        // Poster từ byte[] image trong database
        JLabel posterLabel = new JLabel();
        if (movie.getImage() != null) {
            ImageIcon icon = new ImageIcon(movie.getImage());
            Image scaledImage = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            posterLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            posterLabel.setText("No Image");
        }
        infoPanel.add(posterLabel, BorderLayout.WEST);

        // Movie details - HTML hiển thị thông tin
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        String html = "<html><body style='padding: 10px; font-family: Arial;'>" +
                      "<h2 style='color: #0066cc;'>" + movie.getTitle() + "</h2>" +
                      "<p><b>Đạo diễn:</b> " + movie.getDirector() + "</p>" +
                      "<p><b>Thể loại:</b> " + movie.getGenre() + "</p>" +
                      "<p><b>Thời lượng:</b> " + movie.getDuration() + " phút</p>" +
                      "<p><b>Ngày phát hành:</b> " + movie.getReleaseDate().toLocalDate() + "</p>" +
                      "</body></html>";

        editorPane.setText(html);
        infoPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);

        return infoPanel;
    }


    private void loadMovies() {
        List<Movies> movies = movieManager.getAllMovies();
        movieComboBox.removeAllItems();
        for (Movies m : movies) {
            movieComboBox.addItem(m.getMovieID() + " - " + m.getTitle());
        }
        updateMovieInfo();
    }

    private void loadRooms() {
        List<Rooms> rooms = roomManager.getAllRooms();
        roomComboBox.removeAllItems();
        for (Rooms r : rooms) {
            roomComboBox.addItem(r.getRoomID() + " - " + r.getRoomName());
        }
    }

    private void loadShowtimes() {
        List<Showtimes> showtimes = showtimeManager.getAllShowtimes();
        dateComboBox.removeAllItems();
        timeComboBox.removeAllItems();
        for (Showtimes st : showtimes) {
            dateComboBox.addItem(st.getdateTime().toString());
            timeComboBox.addItem(st.getStartTime().toString());
        }
    }

    private void updateMovieInfo() {
        if (movieComboBox.getSelectedItem() != null) {
            String selected = movieComboBox.getSelectedItem().toString();
            int movieId = Integer.parseInt(selected.split(" - ")[0]);
            Movies movie = movieManager.getMovieByID(movieId);

            if (movie != null) {
                movieDetails.setText(
                    "Tên phim: " + movie.getTitle() + "\n" +
                    "Thể loại: " + movie.getGenre() + "\n" +
                    "Thời lượng: " + movie.getDuration() + " phút\n" +
                    "Đạo diễn: " + movie.getDirector() + "\n" 
                   
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				new DetailFilm_GUI();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
    }
}
