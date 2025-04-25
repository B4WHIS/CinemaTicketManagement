package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dao.SeatDAO;
import dao.SeatStatusDAO;
import model.Rooms;
import model.Seats;

public class Seat_GUI extends JFrame implements ActionListener {
    private JButton[][] seats;
    private boolean[][] seatStatus;
    private List<Seats> selectedSeats;
    private SeatDAO seatDAO;
    private SeatStatusDAO seatStatusDAO;
    private Rooms room;
    private int showtimeID;
    private JButton confirmButton;
    private Seats[][] seatObjects;

    public Seat_GUI(Rooms room, int showtimeID) {
        this.room = room;
        this.showtimeID = showtimeID;
        this.seatDAO = new SeatDAO();
        this.seatStatusDAO = new SeatStatusDAO();
        this.selectedSeats = new ArrayList<>();
        this.seats = new JButton[10][10];
        this.seatStatus = new boolean[10][10];
        this.seatObjects = new Seats[10][10];

        setTitle("Chọn Ghế");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel màn hình
        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Times new roman", Font.BOLD, 16));
        lblScreen.setOpaque(true);
        lblScreen.setBackground(Color.LIGHT_GRAY);
        lblScreen.setPreferredSize(new Dimension(600, 50));
        add(lblScreen, BorderLayout.NORTH);

        // Panel ghế
        JPanel pnlSeat = new JPanel();
        pnlSeat.setLayout(new GridLayout(11, 11, 5, 5));

        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        // Lấy danh sách ghế từ sp_GetSeatsByRoom
        List<Seats> seatList;
        try {
            System.out.println("Lấy ghế cho roomID: " + (room != null ? room.getRoomID() : "null"));
            seatList = seatDAO.getSeatsByRoom(room);
            System.out.println("Số ghế lấy được từ DB: " + seatList.size());
            for (Seats seat : seatList) {
                System.out.println("Ghế: " + seat.getSeatNumber() + ", seatID: " + seat.getSeatID());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách ghế: " + e.getMessage());
            e.printStackTrace();
            seatList = new ArrayList<>(); // Tiếp tục với danh sách rỗng
        }

        // Nếu danh sách ghế rỗng, hiển thị thông báo nhưng vẫn khởi tạo giao diện
        if (seatList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ghế cho phòng này! Hiển thị giao diện mặc định (tất cả ghế không khả dụng).");
        }

        // Khởi tạo ghế
        for (int row = 0; row < 10; row++) {
            String rowLabelText = rowLabels[row];
            JLabel rowLabel = new JLabel(rowLabelText, SwingConstants.CENTER);
            pnlSeat.add(rowLabel);

            for (int col = 0; col < 10; col++) {
                String seatLabel = rowLabelText + String.valueOf(col + 1);
                JButton seat = new JButton(seatLabel);
                seat.setPreferredSize(new Dimension(40, 40));
                seat.setFont(new Font("Times new roman", Font.PLAIN, 10));

                // Tìm ghế trong danh sách
                Seats currentSeat = seatList.stream()
                        .filter(s -> s.getSeatNumber().equalsIgnoreCase(seatLabel))
                        .findFirst()
                        .orElse(null);

                if (currentSeat != null) {
                    try {
                        boolean isBooked = seatStatusDAO.isSeatBooked(currentSeat.getSeatID(), showtimeID);
                        System.out.println("Ghế " + seatLabel + " (seatID: " + currentSeat.getSeatID() + ") trạng thái: " + (isBooked ? "Đã đặt" : "Trống"));
                        if (isBooked) {
                            seat.setBackground(Color.RED);
                            seat.setEnabled(false);
                        } else {
                            seat.setBackground(Color.WHITE);
                            seat.setEnabled(true);
                        }
                    } catch (SQLException e) {
                        System.out.println("Lỗi khi kiểm tra trạng thái ghế " + seatLabel + ": " + e.getMessage());
                        e.printStackTrace();
                        seat.setBackground(Color.WHITE);
                        seat.setEnabled(true);
                    }
                } else {
                    // Nếu không tìm thấy ghế trong danh sách, hiển thị ghế đỏ (không khả dụng)
                    System.out.println("Không tìm thấy ghế: " + seatLabel + " trong dữ liệu, đặt trạng thái không khả dụng");
                    seat.setBackground(Color.RED);
                    seat.setEnabled(false);
                }

                seats[row][col] = seat;
                seatObjects[row][col] = currentSeat;
                seatStatus[row][col] = false;
                seat.addActionListener(this);
                pnlSeat.add(seat);
            }
        }

        // Thêm hàng số ở dưới cùng
        pnlSeat.add(new JLabel(""));
        for (int col = 0; col < 10; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
            pnlSeat.add(colLabel);
        }

        add(pnlSeat, BorderLayout.CENTER);

        // Panel nút Xác nhận
        JPanel pnlConfirm = new JPanel();
        confirmButton = new JButton("Xác nhận");
        confirmButton.addActionListener(this);
        pnlConfirm.add(confirmButton);
        add(pnlConfirm, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == confirmButton) {
            handleConfirmAction();
            return;
        }

        // Xử lý sự kiện nhấn ghế
        JButton clickedSeat = (JButton) source;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (seats[row][col] == clickedSeat) {
                    Seats seat = seatObjects[row][col];
                    if (seat != null && clickedSeat.isEnabled()) {
                        if (!seatStatus[row][col]) {
                            clickedSeat.setBackground(Color.GREEN);
                            seatStatus[row][col] = true;
                            selectedSeats.add(seat);
                            System.out.println("Chọn ghế: " + seat.getSeatNumber());
                        } else {
                            clickedSeat.setBackground(Color.WHITE);
                            seatStatus[row][col] = false;
                            selectedSeats.remove(seat);
                            System.out.println("Bỏ chọn ghế: " + seat.getSeatNumber());
                        }
                    } else {
                        System.out.println("Ghế " + clickedSeat.getText() + " không hợp lệ hoặc đã bị disable");
                    }
                    return;
                }
            }
        }
    }

    private void handleConfirmAction() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!");
            return;
        }

        try {
            for (Seats seat : selectedSeats) {
                boolean updated = seatStatusDAO.updateSeatStatus(showtimeID, seat.getSeatID(), "Booked");
                if (!updated) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái ghế: " + seat.getSeatNumber());
                    return;
                }
                System.out.println("Đã cập nhật trạng thái ghế: " + seat.getSeatNumber());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu trạng thái ghế: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn chọn đồ ăn không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
        	ProductSelectionScreen pss = new ProductSelectionScreen();
            pss.setVisible(true);
        } else {
        	ConfirmationScreen cs = new ConfirmationScreen();
            cs.setVisible(true);
        }

        dispose();
    }

    // Main để test độc lập
    public static void main(String[] args) {
        Rooms room = new Rooms();
        room.setRoomID(1);
        int showtimeID = 1;
        Seat_GUI seatGUI = new Seat_GUI(room, showtimeID);
        seatGUI.setVisible(true);
    }
}