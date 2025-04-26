package gui;

import dao.Showtimes_DAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.Movies;
import model.Rooms;
import model.Showtimes;
import service.MovieManager;
import service.RoomManager;

public class DetailFilm_GUI extends JPanel implements ActionListener{
    private MovieManager movieManager;
    private RoomManager roomManager;
    private Showtimes_DAO showtimesDAO;
    
    private JComboBox<String> movieComboBox;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> dateComboBox;
    private JComboBox<String> timeComboBox;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private int selectedMovieID;
    private int ticketQuantity = 0;
    private BigDecimal ticketPrice = BigDecimal.ZERO;
    private Map<String, Showtimes> showtimesMap;
    private DecimalFormat priceFormatter;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private JSplitPane splitPane;

    public DetailFilm_GUI(Connection connection, int movieID) throws SQLException {
        this.selectedMovieID = movieID;
        this.movieManager = new MovieManager(connection);
        this.roomManager = new RoomManager();
        this.showtimesDAO = new Showtimes_DAO(connection);
        this.showtimesMap = new HashMap<>();
        this.priceFormatter = new DecimalFormat("#,###");
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        this.timeFormatter = new SimpleDateFormat("HH:mm:ss");

        setLayout(new BorderLayout());

        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() throws SQLException {
        movieComboBox = new JComboBox<>();

        JPanel leftPanel = createMovieInfoPanel();
        JPanel rightPanel = createBookingPanel();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.7);
        splitPane.setContinuousLayout(true);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createBookingPanel() throws SQLException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        roomComboBox = new JComboBox<>();
        dateComboBox = new JComboBox<>();
        timeComboBox = new JComboBox<>();
        JButton bookButton = new JButton("Đặt vé");

        JLabel roomLabel = new JLabel("Chọn phòng chiếu:");
        JLabel dateLabel = new JLabel("Ngày chiếu:");
        JLabel timeLabel = new JLabel("Giờ chiếu:");

        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityTextLabel = new JLabel("Số lượng vé:");
        JButton minusButton = new JButton("-");
        quantityLabel = new JLabel("0");
        JButton plusButton = new JButton("+");
        priceLabel = new JLabel("0 VND");

        ActionListener updatePriceListener = e -> updateTicketPrice();

        roomComboBox.addActionListener(updatePriceListener);
        dateComboBox.addActionListener(updatePriceListener);
        timeComboBox.addActionListener(updatePriceListener);

        minusButton.addActionListener(e -> {
            if (ticketQuantity > 0) {
                ticketQuantity--;
                quantityLabel.setText(String.valueOf(ticketQuantity));
                updateTotalPrice();
            }
        });

        plusButton.addActionListener(e -> {
            ticketQuantity++;
            quantityLabel.setText(String.valueOf(ticketQuantity));
            updateTotalPrice();
        });

        bookButton.addActionListener(e -> {
            if (ticketQuantity == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn số lượng vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (roomComboBox.getSelectedItem() == null || dateComboBox.getSelectedItem() == null || timeComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng, ngày và giờ chiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BigDecimal totalPrice = ticketPrice.multiply(BigDecimal.valueOf(ticketQuantity));
            JOptionPane.showMessageDialog(this, "Đặt vé thành công!\nSố lượng: " + ticketQuantity + "\nTổng giá: " + priceFormatter.format(totalPrice) + " VND", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        quantityPanel.add(quantityTextLabel);
        quantityPanel.add(minusButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusButton);
        quantityPanel.add(Box.createHorizontalStrut(20));
        quantityPanel.add(priceLabel);

        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(quantityPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(bookButton);

        loadMovies();
        loadRooms();
        loadShowtimes();

        movieComboBox.addActionListener(e -> {
            try {
                selectedMovieID = Integer.parseInt(movieComboBox.getSelectedItem().toString().split(" - ")[0]);
                createMovieInfoPanel();
                loadShowtimes();
                revalidate();
                repaint();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void updateTicketPrice() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        String selectedDate = (String) dateComboBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();

        if (selectedRoom == null || selectedDate == null || selectedTime == null) {
            ticketPrice = BigDecimal.ZERO;
            updateTotalPrice();
            return;
        }

        // Chuẩn hóa selectedTime
        selectedTime = selectedTime.trim();
        if (selectedTime.contains(".")) {
            selectedTime = selectedTime.substring(0, selectedTime.indexOf("."));
        }

        String key = selectedRoom + "|" + selectedDate + "|" + selectedTime;

        // Log chi tiết để kiểm tra
        System.out.println("Selected Room: " + selectedRoom);
        System.out.println("Selected Date: " + selectedDate);
        System.out.println("Selected Time: " + selectedTime);
        System.out.println("Generated Key: " + key);
        System.out.println("Showtimes Map Keys: " + showtimesMap.keySet());
        Showtimes showtime = showtimesMap.get(key);
        System.out.println("Showtime: " + showtime);
        System.out.println("Showtime Price: " + (showtime != null ? showtime.getPrice() : "null"));

        if (showtime != null) {
            ticketPrice = showtime.getPrice() != null ? showtime.getPrice() : BigDecimal.ZERO;
            updateTotalPrice();
        } else {
            ticketPrice = BigDecimal.ZERO;
            updateTotalPrice();
        }

        // Cập nhật panel thông tin phim để hiển thị giá vé mới
        try {
            if (splitPane != null) {
                splitPane.setLeftComponent(createMovieInfoPanel());
                revalidate();
                repaint();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotalPrice() {
        BigDecimal totalPrice = ticketPrice.multiply(BigDecimal.valueOf(ticketQuantity));
        priceLabel.setText(priceFormatter.format(totalPrice) + " VND");
    }

    private JPanel createMovieInfoPanel() throws SQLException {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

        if (selectedMovieID == -1 && movieComboBox.getItemCount() == 0) {
            infoPanel.add(new JLabel("Chưa có phim nào được chọn"));
            return infoPanel;
        }

        int movieId = selectedMovieID != -1 ? selectedMovieID : Integer.parseInt(movieComboBox.getSelectedItem().toString().split(" - ")[0]);
        Movies movie = movieManager.getMovieByID(movieId);

        if (movie == null) {
            infoPanel.add(new JLabel("Phim không tồn tại"));
            return infoPanel;
        }

        JPanel posterPanel = new JPanel();
        posterPanel.setLayout(new BoxLayout(posterPanel, BoxLayout.Y_AXIS));
        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final int posterWidth = 400;
        final int posterHeight = 500;

        String imagePath = movie.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                String resourcePath = "/movies/" + imagePath.substring(imagePath.lastIndexOf("/") + 1);
                java.net.URL resourceURL = getClass().getResource(resourcePath);
                if (resourceURL == null) {
                    System.err.println("Resource not found in classpath: " + resourcePath);
                    posterLabel.setText("No Image");
                    posterLabel.setOpaque(true);
                    posterLabel.setBackground(Color.LIGHT_GRAY);
                    posterLabel.setPreferredSize(new Dimension(posterWidth, posterHeight));
                } else {
                    ImageIcon icon = new ImageIcon(resourceURL);
                    if (icon.getIconWidth() == -1) {
                        System.err.println("Failed to load image as resource: " + resourcePath);
                        posterLabel.setText("No Image");
                        posterLabel.setOpaque(true);
                        posterLabel.setBackground(Color.LIGHT_GRAY);
                        posterLabel.setPreferredSize(new Dimension(posterWidth, posterHeight));
                    } else {
                        Image scaledImage = icon.getImage().getScaledInstance(posterWidth, posterHeight, Image.SCALE_SMOOTH);
                        posterLabel.setIcon(new ImageIcon(scaledImage));
                        posterLabel.setPreferredSize(new Dimension(posterWidth, posterHeight));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image for MovieID " + movieId + ": " + e.getMessage());
                posterLabel.setText("No Image");
                posterLabel.setOpaque(true);
                posterLabel.setBackground(Color.LIGHT_GRAY);
                posterLabel.setPreferredSize(new Dimension(posterWidth, posterHeight));
            }
        } else {
            posterLabel.setText("No Image");
            posterLabel.setOpaque(true);
            posterLabel.setBackground(Color.LIGHT_GRAY);
            posterLabel.setPreferredSize(new Dimension(posterWidth, posterHeight));
        }

        posterPanel.add(posterLabel);
        posterPanel.add(Box.createHorizontalStrut(10));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        String priceInfo = ticketPrice.compareTo(BigDecimal.ZERO) > 0 
            ? priceFormatter.format(ticketPrice) + " VND/vé" 
            : "Vui lòng chọn lịch chiếu để xem giá vé";

        String html = "<html><body style='padding: 10px; font-family: Arial;'>" +
                      "<h2 style='color: #0066cc;'>" + movie.getTitle() + "</h2>" +
                      "<p><b>Đạo diễn:</b> " + (movie.getDirector() != null ? movie.getDirector() : "Không có thông tin") + "</p>" +
                      "<p><b>Thể loại:</b> " + movie.getGenre() + "</p>" +
                      "<p><b>Thời lượng:</b> " + movie.getDuration() + " phút</p>" +
                      "<p><b>Ngày phát hành:</b> " + movie.getReleaseDate().toLocalDate() + "</p>" +
                      "<p><b>Giá vé:</b> " + priceInfo + "</p>" +
                      "</body></html>";

        editorPane.setText(html);

        infoPanel.add(posterPanel);
        infoPanel.add(new JScrollPane(editorPane));

        return infoPanel;
    }

    private void loadMovies() throws SQLException {
        List<Movies> movies = movieManager.getAllMovies();
        movieComboBox.removeAllItems();
        for (Movies m : movies) {
            String item = m.getMovieID() + " - " + m.getTitle();
            movieComboBox.addItem(item);
            if (m.getMovieID() == selectedMovieID) {
                movieComboBox.setSelectedItem(item);
            }
        }
    }

    private void loadRooms() {
        List<Rooms> rooms = roomManager.getAllRooms();
        roomComboBox.removeAllItems();
        for (Rooms r : rooms) {
            roomComboBox.addItem(r.getRoomID() + " - " + r.getRoomName());
        }
    }
//dsadw
    private void loadShowtimes() throws SQLException {
        dateComboBox.removeAllItems();
        timeComboBox.removeAllItems();
        showtimesMap.clear();

        if (selectedMovieID == -1) {
            return;
        }

        List<Showtimes> showtimes = showtimesDAO.getShowtimesByMovie(selectedMovieID);
        if (showtimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có lịch chiếu nào cho phim này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Showtimes st : showtimes) {
            String room = st.getRoom().getRoomID() + " - Phòng chiếu " + st.getRoom().getRoomID();
            String date = dateFormatter.format(st.getdateTime());
            String time = timeFormatter.format(st.getStartTime());

            // Chuẩn hóa time
            time = time.trim();
            if (time.contains(".")) {
                time = time.substring(0, time.indexOf("."));
            }

            // Log chi tiết
            System.out.println("Loading Showtime - Room: " + room + ", Date: " + date + ", Time: " + time);

            if (!isItemInComboBox(roomComboBox, room)) {
                roomComboBox.addItem(room);
            }
            if (!isItemInComboBox(dateComboBox, date)) {
                dateComboBox.addItem(date);
            }
            if (!isItemInComboBox(timeComboBox, time)) {
                timeComboBox.addItem(time);
            }

            String key = room + "|" + date + "|" + time;
            showtimesMap.put(key, st);
        }

        // Log toàn bộ showtimesMap
        System.out.println("Showtimes Map: " + showtimesMap);
        System.out.println("Showtimes Map Keys: " + showtimesMap.keySet());

        updateTicketPrice();
    }

    private boolean isItemInComboBox(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}