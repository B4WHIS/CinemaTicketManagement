package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.Movies;
import model.Rooms;
import model.Showtimes;
import service.MovieManager;
import service.RoomManager;
import service.ShowTimeManager;

public class DetailFilm_GUI extends JPanel {
    private MovieManager movieManager;
    private RoomManager roomManager;
    private ShowTimeManager showtimeManager;
    
    private JComboBox<String> movieComboBox;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> dateComboBox;
    private JComboBox<String> timeComboBox;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private int selectedMovieID;
    private int ticketQuantity = 0;
    private static final int TICKET_PRICE = 50000; // Giá vé giả định (VND)
    private MainFrame mainFrame;

    public DetailFilm_GUI(Connection connection, int movieID, MainFrame mainFrame) throws SQLException {
        this.selectedMovieID = movieID;
        this.movieManager = new MovieManager(connection);
        this.roomManager = new RoomManager();
        this.showtimeManager = new ShowTimeManager();
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        add(createNavigationBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
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

    private JPanel createMainContent() throws SQLException {
        movieComboBox = new JComboBox<>();

        JPanel leftPanel = createMovieInfoPanel();
        JPanel rightPanel = createBookingPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
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

        minusButton.addActionListener(e -> {
            if (ticketQuantity > 0) {
                ticketQuantity--;
                quantityLabel.setText(String.valueOf(ticketQuantity));
                priceLabel.setText((ticketQuantity * TICKET_PRICE) + " VND");
            }
        });

        plusButton.addActionListener(e -> {
            ticketQuantity++;
            quantityLabel.setText(String.valueOf(ticketQuantity));
            priceLabel.setText((ticketQuantity * TICKET_PRICE) + " VND");
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
            try {
                String selectedRoom = (String) roomComboBox.getSelectedItem();
                int roomID = Integer.parseInt(selectedRoom.split(" - ")[0]);
                Rooms room = roomManager.getRoomByID(roomID);
                List<Showtimes> showtimes = showtimeManager.getShowtimesByMovie(selectedMovieID);
                String selectedDateTime = (String) dateComboBox.getSelectedItem();
                String selectedTime = (String) timeComboBox.getSelectedItem();
                Showtimes selectedShowtime = showtimes.stream()
                        .filter(st -> st.getdateTime().toString().equals(selectedDateTime) && 
                                      st.getStartTime().toString().equals(selectedTime))
                        .findFirst()
                        .orElse(null);
                if (selectedShowtime == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy lịch chiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int showtimeID = selectedShowtime.getShowTimeID();
                mainFrame.setShowtimeID(showtimeID);
                mainFrame.setTicketID(ticketQuantity);
                Seat_GUI seatGUI = new Seat_GUI(room, showtimeID, mainFrame);
                mainFrame.showScreen("SeatGUI", seatGUI);
                System.out.println("Showing SeatGUI with showtimeID: " + showtimeID); // B: Added for debugging
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển sang màn hình chọn ghế: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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

    private JPanel createMovieInfoPanel() throws SQLException {
        JPanel infoPanel = new JPanel(new BorderLayout());

        if (selectedMovieID == -1 && movieComboBox.getItemCount() == 0) {
            infoPanel.add(new JLabel("Chưa có phim nào được chọn"), BorderLayout.CENTER);
            return infoPanel;
        }

        int movieId = selectedMovieID != -1 ? selectedMovieID : Integer.parseInt(movieComboBox.getSelectedItem().toString().split(" - ")[0]);
        Movies movie = movieManager.getMovieByID(movieId);

        if (movie == null) {
            infoPanel.add(new JLabel("Phim không tồn tại"), BorderLayout.CENTER);
            return infoPanel;
        }

        JLabel posterLabel = new JLabel();
        if (movie.getImage() != null) {
            ImageIcon icon = new ImageIcon(movie.getImage());
            Image scaledImage = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            posterLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            posterLabel.setText("No Image");
        }
        infoPanel.add(posterLabel, BorderLayout.WEST);

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

    private void loadShowtimes() throws SQLException {
        dateComboBox.removeAllItems();
        timeComboBox.removeAllItems();
        if (selectedMovieID == -1) {
            return;
        }
        List<Showtimes> showtimes = showtimeManager.getShowtimesByMovie(selectedMovieID);
        for (Showtimes st : showtimes) {
            dateComboBox.addItem(st.getdateTime().toString());
            timeComboBox.addItem(st.getStartTime().toString());
        }
    }
}