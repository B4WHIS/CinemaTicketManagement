package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Movies;
import service.MovieManager;

public class AddMoviePanel extends JPanel {
    private JTextField txtTitle, txtGenre, txtDuration, txtReleaseDate, txtDirector, txtImage;
    private JButton btnAdd, btnChooseImage, btnCancel;
    private MovieManager movieManager;
    private mainGUI mainGui;
    private File selectedImageFile;

    public AddMoviePanel(Connection connection, mainGUI mainGui) {
        this.mainGui = mainGui;
        this.movieManager = new MovieManager(connection);
        setLayout(new GridLayout(8, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtTitle = new JTextField(20);
        txtGenre = new JTextField(20);
        txtDuration = new JTextField(20);
        txtReleaseDate = new JTextField(20);
        txtDirector = new JTextField(20);
        txtImage = new JTextField(20);
        txtImage.setEditable(false);
        btnChooseImage = new JButton("Chọn Hình Ảnh");
        btnAdd = new JButton("Thêm Phim");
        btnCancel = new JButton("Hủy");

        add(new JLabel("Tên phim:"));
        add(txtTitle);
        add(new JLabel("Thể loại:"));
        add(txtGenre);
        add(new JLabel("Thời lượng (phút):"));
        add(txtDuration);
        add(new JLabel("Ngày phát hành (yyyy-MM-dd):"));
        add(txtReleaseDate);
        add(new JLabel("Đạo diễn:"));
        add(txtDirector);
        add(new JLabel("Hình ảnh:"));
        add(txtImage);
        add(new JLabel(""));
        add(btnChooseImage);
        add(btnCancel);
        add(btnAdd);

        btnChooseImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(AddMoviePanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                    txtImage.setText(selectedImageFile.getAbsolutePath());
                }
            }
        });

        btnAdd.addActionListener(e -> addMovie());

        btnCancel.addActionListener(e -> {
            clearFields();
            mainGui.showMoviesPanel(); // Chuyển về màn hình danh sách phim
        });
    }

    private void addMovie() {
        try {
            String title = txtTitle.getText().trim();
            String genre = txtGenre.getText().trim();
            String durationStr = txtDuration.getText().trim();
            String releaseDateStr = txtReleaseDate.getText().trim();
            String director = txtDirector.getText().trim();

            if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || releaseDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (trừ đạo diễn và hình ảnh)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int duration = Integer.parseInt(durationStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date releaseDate = sdf.parse(releaseDateStr);

            Movies movie = new Movies();
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setDuration(duration);
            movie.setReleaseDate(new java.sql.Date(releaseDate.getTime()));
            movie.setDirector(director.isEmpty() ? null : director);
            movie.setImage(selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null);

            movieManager.addMovie(movie);

            JOptionPane.showMessageDialog(this, "Thêm phim thành công!");

            clearFields();
            mainGui.loadMovies(""); // Làm mới danh sách phim
            mainGui.showMoviesPanel(); // Chuyển về màn hình danh sách phim
        } catch (NumberFormatException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            System.err.println("SQL Error: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtTitle.setText("");
        txtGenre.setText("");
        txtDuration.setText("");
        txtReleaseDate.setText("");
        txtDirector.setText("");
        txtImage.setText("");
        selectedImageFile = null;
    }
}