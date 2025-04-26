package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dao.SeatDAO;
import dao.SeatStatusDAO;
import model.Rooms;
import model.Seats;

public class Seat_GUI extends JPanel implements ActionListener {
    private JButton[][] seats;
    private boolean[][] seatStatus;
    private List<Seats> selectedSeats;
    private SeatDAO seatDAO;
    private SeatStatusDAO seatStatusDAO;
    private Rooms room;
    private int showtimeID;
    private int ticketQuantity;
    private BigDecimal ticketPrice;
    private JButton confirmButton;
    private Seats[][] seatObjects;
    private MainFrame mainFrame;
    private Consumer<List<String>> seatSelectionListener;

    public Seat_GUI(Rooms room, int showtimeID, int ticketQuantity, BigDecimal ticketPrice, MainFrame mainFrame) throws SQLException {
        this.room = room;
        this.showtimeID = showtimeID;
        this.ticketQuantity = ticketQuantity;
        this.ticketPrice = ticketPrice;
        this.mainFrame = mainFrame;
        this.seatDAO = new SeatDAO();
        this.seatStatusDAO = new SeatStatusDAO();
        this.selectedSeats = new ArrayList<>();
        this.seats = new JButton[10][10];
        this.seatStatus = new boolean[10][10];
        this.seatObjects = new Seats[10][10];

        setLayout(new BorderLayout());

        // B: Cải thiện thông báo lỗi và kiểm tra
        System.out.println("RoomID: " + (room != null ? room.getRoomID() : "null") + 
                          ", RoomName: " + (room != null ? room.getRoomName() : "null"));
        if (room == null || room.getRoomID() <= 0) {
            String errorMessage = "Phòng chiếu không hợp lệ (RoomID: " + (room != null ? room.getRoomID() : "null") + 
                                 ", RoomName: " + (room != null ? room.getRoomName() : "null") + 
                                 "). Vui lòng kiểm tra dữ liệu phòng trong cơ sở dữ liệu.";
            int option = JOptionPane.showConfirmDialog(
                    this,
                    errorMessage + "\nBạn có muốn quay lại màn hình trước đó không?",
                    "Lỗi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                mainFrame.showScreen("DetailFilmGUI", null);
            }
            return;
        }

        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Times new roman", Font.BOLD, 16));
        lblScreen.setOpaque(true);
        lblScreen.setBackground(Color.LIGHT_GRAY);
        lblScreen.setPreferredSize(new Dimension(600, 50));
        add(lblScreen, BorderLayout.NORTH);

        JPanel pnlSeat = new JPanel();
        pnlSeat.setLayout(new GridLayout(11, 11, 5, 5));

        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        List<Seats> seatList = seatDAO.getSeatsByRoom(room);
        System.out.println("Seat list size for RoomID " + room.getRoomID() + ": " + seatList.size());
        if (seatList.isEmpty()) {
            String errorMessage = "Không tìm thấy ghế cho phòng này!\n" +
                                 "Phòng: " + room.getRoomName() + " (ID: " + room.getRoomID() + ")\n" +
                                 "Vui lòng kiểm tra dữ liệu ghế trong cơ sở dữ liệu.";
            int option = JOptionPane.showConfirmDialog(
                    this,
                    errorMessage + "\nBạn có muốn quay lại màn hình trước đó không?",
                    "Lỗi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                mainFrame.showScreen("DetailFilmGUI", null);
            }
            return;
        }

        seatStatusDAO.initializeSeatsForShowtime(showtimeID, seatList);

        for (int row = 0; row < 10; row++) {
            String rowLabelText = rowLabels[row];
            JLabel rowLabel = new JLabel(rowLabelText, SwingConstants.CENTER);
            pnlSeat.add(rowLabel);

            for (int col = 0; col < 10; col++) {
                String seatLabel = rowLabelText + String.valueOf(col + 1);
                JButton seat = new JButton(seatLabel);
                seat.setPreferredSize(new Dimension(40, 40));
                seat.setFont(new Font("Times new roman", Font.PLAIN, 10));

                Seats currentSeat = seatList.stream()
                        .filter(s -> s.getSeatNumber().equalsIgnoreCase(seatLabel))
                        .findFirst()
                        .orElse(null);

                if (currentSeat != null) {
                    boolean isBooked = seatStatusDAO.isSeatBooked(currentSeat.getSeatID(), showtimeID);
                    System.out.println("Seat: " + seatLabel + ", SeatID: " + currentSeat.getSeatID() + ", Booked: " + isBooked);
                    if (isBooked) {
                        seat.setBackground(Color.RED);
                        seat.setEnabled(false);
                    } else {
                        seat.setBackground(Color.WHITE);
                        seat.setEnabled(true);
                    }
                } else {
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

        pnlSeat.add(new JLabel(""));
        for (int col = 0; col < 10; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
            pnlSeat.add(colLabel);
        }

        add(pnlSeat, BorderLayout.CENTER);

        JPanel pnlConfirm = new JPanel();
        confirmButton = new JButton("Xác nhận");
        confirmButton.addActionListener(this);
        pnlConfirm.add(confirmButton);
        add(pnlConfirm, BorderLayout.SOUTH);
    }

    public void setSeatSelectionListener(Consumer<List<String>> listener) {
        this.seatSelectionListener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == confirmButton) {
            handleConfirmAction();
            return;
        }

        JButton clickedSeat = (JButton) source;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (seats[row][col] == clickedSeat) {
                    Seats seat = seatObjects[row][col];
                    if (seat != null && clickedSeat.isEnabled()) {
                        if (!seatStatus[row][col]) {
                            if (selectedSeats.size() < ticketQuantity) {
                                clickedSeat.setBackground(Color.GREEN);
                                seatStatus[row][col] = true;
                                selectedSeats.add(seat);
                            } else {
                                JOptionPane.showMessageDialog(this, "Bạn đã chọn đủ số ghế!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            clickedSeat.setBackground(Color.WHITE);
                            seatStatus[row][col] = false;
                            selectedSeats.remove(seat);
                        }
                    }
                    return;
                }
            }
        }
    }

    private void handleConfirmAction() {
        if (selectedSeats.size() != ticketQuantity) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đúng " + ticketQuantity + " ghế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            for (Seats seat : selectedSeats) {
                seatStatusDAO.updateSeatStatus(showtimeID, seat.getSeatID(), "Booked");
            }
            List<String> seatLabels = new ArrayList<>();
            for (Seats seat : selectedSeats) {
                seatLabels.add(seat.getSeatNumber());
            }
            if (seatSelectionListener != null) {
                seatSelectionListener.accept(seatLabels);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu ghế: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn chọn đồ ăn không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            ProductSelectionScreen pss = new ProductSelectionScreen(mainFrame, showtimeID, ticketQuantity, ticketPrice, selectedSeats, room);
            mainFrame.showScreen("ProductSelection", pss);
        } else {
            ConfirmationScreen cs = new ConfirmationScreen(mainFrame, showtimeID, ticketQuantity, ticketPrice, selectedSeats, new ArrayList<>());
            mainFrame.showScreen("Confirmation", cs);
        }
    }
}