package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import model.Movies;
import model.Rooms;
import model.Showtimes;
import service.MovieManager;
import service.RoomManager;
import service.ShowTimeManager;

public class AddMoviePanel extends JPanel {
    private JTextField txtTitle, txtGenre, txtDuration, txtReleaseDate, txtDirector, txtImage, txtStartTime, txtShowDate, txtPrice;
    private JComboBox<String> cbRoom;
    private JButton btnAdd, btnChooseImage, btnCancel, btnEdit, btnDelete, btnSearch;
    private JTextField txtSearch;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private MovieManager moviesManager;
    private RoomManager roomManager;
    private ShowTimeManager showTimeManager;
    private AdminGUI mainGui;
    private File selectedImageFile;
    private List<Rooms> roomsList;

    public AddMoviePanel(Connection connection, AdminGUI mainGui2) {
        this.mainGui = mainGui;
        try {
            // Khởi tạo các manager
            this.moviesManager = new MovieManager(connection);
            this.roomManager = new RoomManager();
            this.showTimeManager = new ShowTimeManager();
            this.roomsList = roomManager.getAllRooms();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể khởi tạo các Manager: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Thiết lập layout chính
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Phần bên trái: Thông tin phim (Movies)
        JPanel movieInputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        movieInputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phim"));
        txtTitle = new JTextField(20);
        txtGenre = new JTextField(20);
        txtDuration = new JTextField(20);
        txtReleaseDate = new JTextField(20);
        txtDirector = new JTextField(20);
        txtImage = new JTextField(20);
        txtImage.setEditable(false);
        btnChooseImage = new JButton("Chọn Hình Ảnh");

        movieInputPanel.add(new JLabel("Tên phim:"));
        movieInputPanel.add(txtTitle);
        movieInputPanel.add(new JLabel("Thể loại:"));
        movieInputPanel.add(txtGenre);
        movieInputPanel.add(new JLabel("Thời lượng (phút):"));
        movieInputPanel.add(txtDuration);
        movieInputPanel.add(new JLabel("Ngày phát hành (yyyy-MM-dd):"));
        movieInputPanel.add(txtReleaseDate);
        movieInputPanel.add(new JLabel("Đạo diễn:"));
        movieInputPanel.add(txtDirector);
        movieInputPanel.add(new JLabel("Hình ảnh:"));
        movieInputPanel.add(txtImage);
        movieInputPanel.add(new JLabel(""));
        movieInputPanel.add(btnChooseImage);

        // Phần bên phải: Thông tin suất chiếu (Showtimes)
        JPanel showtimeInputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        showtimeInputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin suất chiếu"));
        txtStartTime = new JTextField(20);
        txtShowDate = new JTextField(20);
        txtPrice = new JTextField(20);
        cbRoom = new JComboBox<>();
        for (Rooms room : roomsList) {
            cbRoom.addItem(room.getRoomName());
        }
       
        showtimeInputPanel.add(new JLabel("Giờ chiếu (HH:mm):"));
        showtimeInputPanel.add(txtStartTime);
        showtimeInputPanel.add(new JLabel("Ngày chiếu (yyyy-MM-dd):"));
        showtimeInputPanel.add(txtShowDate);
        showtimeInputPanel.add(new JLabel("Giá vé:"));
        showtimeInputPanel.add(txtPrice);
        showtimeInputPanel.add(new JLabel("Phòng chiếu:"));
        showtimeInputPanel.add(cbRoom);
    

        // Thêm hai panel nhập liệu vào panel chính
        inputPanel.add(movieInputPanel);
        inputPanel.add(showtimeInputPanel);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnAdd = new JButton("Thêm Phim");
        btnCancel = new JButton("Hủy");
        btnSearch = new JButton("Tìm kiếm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnAdd);
        searchPanel.add(btnEdit);
        searchPanel.add(btnDelete);
        searchPanel.add(btnCancel);
        

        // Bảng hiển thị danh sách phim
        String[] columns = {
            "ID", "Tên phim", "Thể loại", "Thời lượng", "Đạo diễn", "Ngày phát hành", "Hình ảnh",
            "Ngày chiếu", "Giờ chiếu", "Giá vé", "Phòng chiếu"
        };
        tableModel = new DefaultTableModel(columns, 0);
        movieTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(movieTable);

        // Thêm các panel vào layout chính
        add(inputPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        // Load danh sách phim ban đầu
        loadMoviesToTable("");

        // Sự kiện chọn hình ảnh
        btnChooseImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(AddMoviePanel.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                txtImage.setText(selectedImageFile.getName());
            }
        });

        // Sự kiện thêm phim và suất chiếu
        btnAdd.addActionListener(e -> addMovieAndShowtime());

        // Sự kiện hủy
        btnCancel.addActionListener(e -> {
            clearFields();
            mainGui.showMoviesPanel();
        });

        // Sự kiện tìm kiếm
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            loadMoviesToTable(keyword);
        });

        // Sự kiện sửa
        btnEdit.addActionListener(e -> editMovie());

        // Sự kiện xóa
        btnDelete.addActionListener(e -> deleteMovie());

        // Sự kiện chọn dòng trong bảng
        movieTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtTitle.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtGenre.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtDuration.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtReleaseDate.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtDirector.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
                txtImage.setText(tableModel.getValueAt(selectedRow, 6) != null ? tableModel.getValueAt(selectedRow, 6).toString() : "");
                txtShowDate.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "");
                txtStartTime.setText(tableModel.getValueAt(selectedRow, 8) != null ? tableModel.getValueAt(selectedRow, 8).toString() : "");
                txtPrice.setText(tableModel.getValueAt(selectedRow, 9) != null ? tableModel.getValueAt(selectedRow, 9).toString() : "");
                String roomName = tableModel.getValueAt(selectedRow, 10) != null ? tableModel.getValueAt(selectedRow, 10).toString() : "";
                cbRoom.setSelectedItem(roomName);
            } else {
                clearFields();
            }
        });
    }

    private void addMovieAndShowtime() {
        try {
            // Lấy dữ liệu phim
            String title = txtTitle.getText().trim();
            String genre = txtGenre.getText().trim();
            String durationStr = txtDuration.getText().trim();
            String releaseDateStr = txtReleaseDate.getText().trim();
            String director = txtDirector.getText().trim();
            String startTimeStr = txtStartTime.getText().trim();
            String showDateStr = txtShowDate.getText().trim();
            String priceStr = txtPrice.getText().trim();

            // Kiểm tra dữ liệu đầu vào
            if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || releaseDateStr.isEmpty() ||
                startTimeStr.isEmpty() || showDateStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (trừ đạo diễn và hình ảnh)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!releaseDateStr.matches("\\d{4}-\\d{2}-\\d{2}") || !showDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Ngày phải có định dạng yyyy-MM-dd!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!startTimeStr.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Giờ chiếu phải có định dạng HH:mm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int duration = Integer.parseInt(durationStr);
            BigDecimal price = new BigDecimal(priceStr);
            if (duration <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Thời lượng và giá vé phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date releaseDate = sdf.parse(releaseDateStr);
            java.util.Date showDate = sdf.parse(showDateStr);

            SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
            stf.setLenient(false);
            java.util.Date startTime = stf.parse(startTimeStr);

            // Xử lý hình ảnh
            String imagePath = null;
            int nextMovieId = moviesManager.getNextMovieId();
            if (selectedImageFile != null) {
                String newImageName = "mv" + String.format("%02d", nextMovieId) + ".jpg";
                Path destinationPath = Paths.get("bin/movies/" + newImageName);
                Files.createDirectories(destinationPath.getParent());
                Files.copy(selectedImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = "img/movies/" + newImageName;
            }

            // Tạo đối tượng Movies
            Movies movie = new Movies();
            movie.setMovieID(nextMovieId);
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setDuration (duration);
            movie.setReleaseDate(new java.sql.Date(releaseDate.getTime()));
            movie.setDirector(director.isEmpty() ? null : director);
            movie.setImage(imagePath);

            // Tạo đối tượng Showtimes
            int selectedRoomIndex = cbRoom.getSelectedIndex();
            Rooms room = roomsList.get(selectedRoomIndex);
            Showtimes showtime = new Showtimes();
            showtime.setShowTimeID(showTimeManager.getNextShowtimeId());
            showtime.setMovie(movie);
            showtime.setRoom(room);
            showtime.setStartTime(new java.sql.Time(startTime.getTime()));
            showtime.setdateTime(new java.sql.Date(showDate.getTime()));
            showtime.setPrice(price);

            // Lưu vào cơ sở dữ liệu
            moviesManager.addMovie(movie);
            showTimeManager.addShowtime(showtime);

            JOptionPane.showMessageDialog(this, "Thêm phim và suất chiếu thành công!");

            // Cập nhật giao diện
            clearFields();
            loadMoviesToTable("");
            mainGui.loadMovies(""); // Cập nhật movie card
            mainGui.showMoviesPanel();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Thời lượng hoặc giá vé không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày hoặc giờ không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sao chép hình ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phim để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int movieId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String title = txtTitle.getText().trim();
            String genre = txtGenre.getText().trim();
            String durationStr = txtDuration.getText().trim();
            String releaseDateStr = txtReleaseDate.getText().trim();
            String director = txtDirector.getText().trim();
            String startTimeStr = txtStartTime.getText().trim();
            String showDateStr = txtShowDate.getText().trim();
            String priceStr = txtPrice.getText().trim();

            // Kiểm tra dữ liệu đầu vào
            if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || releaseDateStr.isEmpty() ||
                startTimeStr.isEmpty() || showDateStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (trừ đạo diễn và hình ảnh)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!releaseDateStr.matches("\\d{4}-\\d{2}-\\d{2}") || !showDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Ngày phải có định dạng yyyy-MM-dd!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!startTimeStr.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Giờ chiếu phải có định dạng HH:mm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int duration = Integer.parseInt(durationStr);
            BigDecimal price = new BigDecimal(priceStr);
            if (duration <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Thời lượng và giá vé phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date releaseDate = sdf.parse(releaseDateStr);
            java.util.Date showDate = sdf.parse(showDateStr);

            SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
            stf.setLenient(false);
            java.util.Date startTime = stf.parse(startTimeStr);

            String imagePath = txtImage.getText().trim();
            if (selectedImageFile != null) {
                String newImageName = "mv" + String.format("%02d", movieId) + ".jpg";
                Path destinationPath = Paths.get("bin/movies/" + newImageName);
                Files.createDirectories(destinationPath.getParent());
                Files.copy(selectedImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = "img/movies/" + newImageName;
            }

            // Cập nhật phim
            Movies movie = new Movies();
            movie.setMovieID(movieId);
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setDuration(duration);
            movie.setReleaseDate(new java.sql.Date(releaseDate.getTime()));
            movie.setDirector(director.isEmpty() ? null : director);
            movie.setImage(imagePath.isEmpty() ? null : imagePath);

            // Cập nhật suất chiếu
            List<Showtimes> showtimes = showTimeManager.getShowtimesByMovie(movieId);
            if (!showtimes.isEmpty()) {
                Showtimes showtime = showtimes.get(0); // Chỉ cập nhật suất chiếu đầu tiên
                showtime.setStartTime(new java.sql.Time(startTime.getTime()));
                showtime.setdateTime(new java.sql.Date(showDate.getTime()));
                showtime.setPrice(price);
                int selectedRoomIndex = cbRoom.getSelectedIndex();
                showtime.setRoom(roomsList.get(selectedRoomIndex));
                showTimeManager.updateShowtime(showtime);
            }

            moviesManager.updateMovie(movie);

            JOptionPane.showMessageDialog(this, "Sửa phim và suất chiếu thành công!");
            loadMoviesToTable("");
            mainGui.loadMovies("");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Thời lượng hoặc giá vé không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày hoặc giờ không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sao chép hình ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phim để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phim này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int movieId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                // Xóa các suất chiếu liên quan trước
                List<Showtimes> showtimes = showTimeManager.getShowtimesByMovie(movieId);
                for (Showtimes showtime : showtimes) {
                    showTimeManager.deleteShowtime(showtime.getShowTimeID());
                }
                // Xóa phim
                moviesManager.deleteMovie(movieId);
                JOptionPane.showMessageDialog(this, "Xóa phim và suất chiếu thành công!");
                loadMoviesToTable("");
                mainGui.loadMovies("");
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadMoviesToTable(String keyword) {
        try {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            List<Movies> movies = moviesManager.searchMoviesByTitle(keyword);
            for (Movies movie : movies) {
                List<Showtimes> showtimes = showTimeManager.getShowtimesByMovie(movie.getMovieID());
                if (!showtimes.isEmpty()) {
                    Showtimes showtime = showtimes.get(0); // Hiển thị suất chiếu đầu tiên
                    tableModel.addRow(new Object[]{
                        movie.getMovieID(),
                        movie.getTitle(),
                        movie.getGenre(),
                        movie.getDuration(),
                        movie.getDirector(),
                        movie.getReleaseDate(),
                        movie.getImage(),
                        showtime.getdateTime(),
                        showtime.getStartTime(),
                        showtime.getPrice(),
                        showtime.getRoom().getRoomName()
                    });
                } else {
                    tableModel.addRow(new Object[]{
                        movie.getMovieID(),
                        movie.getTitle(),
                        movie.getGenre(),
                        movie.getDuration(),
                        movie.getDirector(),
                        movie.getReleaseDate(),
                        movie.getImage(),
                        "", "", "", ""
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtTitle.setText("");
        txtGenre.setText("");
        txtDuration.setText("");
        txtReleaseDate.setText("");
        txtDirector.setText("");
        txtImage.setText("");
        txtStartTime.setText("");
        txtShowDate.setText("");
        txtPrice.setText("");
        cbRoom.setSelectedIndex(0);
        selectedImageFile = null;
        movieTable.clearSelection();
    }

    // Đóng các kết nối khi panel bị hủy
    @Override
    protected void finalize() throws Throwable {
        try {
            showTimeManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.finalize();
    }
}