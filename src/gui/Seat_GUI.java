package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Seat_GUI extends JFrame implements ActionListener {
	
	private JButton[][] seats;
	private boolean[][] seatStatus;
	private JComponent seat;

	public Seat_GUI() {
		
		seats = new JButton[10][10]; 
		seatStatus = new boolean[10][10];

		setTitle("Seat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Pnl màn hình
		JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
		lblScreen.setFont(new Font("Times new roman", Font.BOLD, 16));
		lblScreen.setOpaque(true);
		lblScreen.setBackground(Color.LIGHT_GRAY);
		lblScreen.setPreferredSize(new Dimension(600, 50));
		add(lblScreen, BorderLayout.NORTH);

		// Pnl ghe
		JPanel pnlSeat = new JPanel();
		pnlSeat.setLayout(new GridLayout(11, 11, 5, 5)); 
		
		String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		
		for (int row = 0; row < 10; row++) {
			// Hàng chữ bên trái
			String rowLabelText = rowLabels[row];
			JLabel rowLabel = new JLabel(rowLabelText, SwingConstants.CENTER);
			pnlSeat.add(rowLabel);

			// Thêm ghế cho hàng
			for (int col = 0; col < 10; col++) {
				// Tạo nhãn ghế 
				String seatLabel = rowLabelText + String.valueOf(col + 1);
				JButton seat = new JButton(seatLabel);
				seat.setPreferredSize(new Dimension(40, 40));
				seat.setBackground(Color.RED); // Ghế trống màu đỏ
				seat.setOpaque(true);
				seat.setBorderPainted(false);
				seat.setFont(new Font("Times new roman", Font.PLAIN, 10)); // Điều chỉnh kích thước chữ

				// Lưu thông tin ghế
				seats[row][col] = seat;
				seatStatus[row][col] = false;
				
				// Gắn ActionListener cho ghế
				seat.addActionListener(this);
				
				pnlSeat.add(seat);
			}
		}
		// Thêm hàng số ở dưới cùng
		pnlSeat.add(new JLabel("")); // Ô trống ở góc trái để căn chỉnh
		for (int col = 0; col < 10; col++) {
			JLabel colLabel = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
			pnlSeat.add(colLabel);
		}

		add(pnlSeat, BorderLayout.CENTER);
		setSize(1500, 1000);
		setLocationRelativeTo(null); // Căn giữa màn hình
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Lấy ghế được nhấp từ sự kiện
		JButton clickedSeat = (JButton) e.getSource();
		
		// Tìm vị trí của ghế trong mảng seats
		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				if (seats[row][col] == clickedSeat) {
					// Cập nhật trạng thái và màu sắc ghế
					if (!seatStatus[row][col]) {
						clickedSeat.setBackground(Color.GREEN); // Chọn ghế: màu xanh
						seatStatus[row][col] = true;
					} else {
						clickedSeat.setBackground(Color.RED); // Bỏ chọn ghế: màu đỏ
						seatStatus[row][col] = false;
					}
					return; // Thoát sau khi tìm thấy ghế
				}
			}
		}
	}

	public static void main(String[] args) {
		Seat_GUI seat_GUI = new Seat_GUI();
		seat_GUI.setVisible(true);
	}
}