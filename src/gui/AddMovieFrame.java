package gui;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.Movies;
import service.MovieManager;

public class AddMovieFrame extends JFrame {
    private JTextField txtTitle, txtGenre, txtDuration, txtReleaseDate;
    private JButton btnAdd;
    private MovieManager movieManager;

    public AddMovieFrame() {
        movieManager = new MovieManager();
        setTitle("Thêm Phim Mới");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtTitle = new JTextField(20);
        txtGenre = new JTextField(20);
        txtDuration = new JTextField(20);
        txtReleaseDate = new JTextField(20);
        btnAdd = new JButton("Thêm Phim");

        panel.add(new JLabel("Tên phim:"));
        panel.add(txtTitle);
        panel.add(new JLabel("Thể loại:"));
        panel.add(txtGenre);
        panel.add(new JLabel("Thời lượng (phút):"));
        panel.add(txtDuration);
        panel.add(new JLabel("Ngày phát hành (yyyy-MM-dd):"));
        panel.add(txtReleaseDate);
        panel.add(new JLabel(""));
        panel.add(btnAdd);

        btnAdd.addActionListener(e -> addMovie());

        add(panel);
    }

    private void addMovie() {
        try {
            String title = txtTitle.getText();
            String genre = txtGenre.getText();
            int duration = Integer.parseInt(txtDuration.getText());
            String releaseDateStr = txtReleaseDate.getText();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date releaseDate = sdf.parse(releaseDateStr);

            Movies movie = new Movies();
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setDuration(duration);
            movie.setReleaseDate(new java.sql.Date(releaseDate.getTime()));

            movieManager.addMovie(movie);
            JOptionPane.showMessageDialog(this, "Thêm phim thành công!");
            dispose();
        } catch (NumberFormatException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddMovieFrame().setVisible(true));
    }
}