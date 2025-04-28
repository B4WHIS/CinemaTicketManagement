
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
import dao.Showtimes_DAO;
import model.Product_Orders;
import model.Rooms;
import model.Seats;
import model.Showtimes;
import model.Users;

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

        //thông tin phòng để kiểm tra
        // System.out.println("RoomID: " + (room != null ? room.getRoomID() : "null") + 
        //                   ", RoomName: " + (room != null ? room.getRoomName() : "null"));
        
        // Kiểm tra dữ liệu phòng; hiển thị lỗi và quay lại màn hình trước nếu không hợp lệ
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

        // Khung "SCREEN"
        JLabel lblScreen = new JLabel("SCREEN", SwingConstants.CENTER);
        lblScreen.setFont(new Font("Times new roman", Font.BOLD, 16));
        lblScreen.setOpaque(true);
        lblScreen.setBackground(Color.LIGHT_GRAY);
        lblScreen.setPreferredSize(new Dimension(600, 50));
        add(lblScreen, BorderLayout.NORTH);

        // Tạo panel cho lưới ghế
        JPanel pnlSeat = new JPanel();
        pnlSeat.setLayout(new GridLayout(11, 11, 5, 5));

        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        // Lấy danh sách ghế
        List<Seats> seatList = seatDAO.getSeatsByRoom(room);
        // System.out.println("Kích thước danh sách ghế cho RoomID " + room.getRoomID() + ": " + seatList.size());
        
        //Không tìm thấy ghế cho phòng
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

        // Khởi tạo trạng thái ghế cho suất chiếu
        seatStatusDAO.initializeSeatsForShowtime(showtimeID, seatList);

        // Tạo các nút ghế và nhãn hàng
        for (int row = 0; row < 10; row++) {
            String rowLabelText = rowLabels[row];
            JLabel rowLabel = new JLabel(rowLabelText, SwingConstants.CENTER);
            pnlSeat.add(rowLabel);

            for (int col = 0; col < 10; col++) {
                // Tạo nhãn ghế
                String seatLabel = rowLabelText + String.valueOf(col + 1);
                JButton seat = new JButton(seatLabel);
                seat.setPreferredSize(new Dimension(40, 40));
                seat.setFont(new Font("Times new roman", Font.PLAIN, 10));

                // Tìm đối tượng ghế tương ứng trong danh sách ghế
                Seats currentSeat = seatList.stream()
                        .filter(s -> s.getSeatNumber().equalsIgnoreCase(seatLabel))
                        .findFirst()
                        .orElse(null);

                // Cấu hình nút ghế dựa trên trạng thái
                if (currentSeat != null) {
                    boolean isBooked = seatStatusDAO.isSeatBooked(currentSeat.getSeatID(), showtimeID);
                    // System.out.println("Ghế: " + seatLabel + ", SeatID: " + currentSeat.getSeatID() + ", Đã đặt: " + isBooked);
                    if (isBooked) {
                        seat.setBackground(Color.RED); // Ghế đã đặt màu đỏ và vô hiệu hóa
                        seat.setEnabled(false);
                    } else {
                        seat.setBackground(Color.WHITE); // Ghế trống màu trắng và kích hoạt
                        seat.setEnabled(true);
                    }
                } else {
                    seat.setBackground(Color.RED); // Ghế không tồn tại màu đỏ và vô hiệu hóa
                    seat.setEnabled(false);
                }

                // Lưu nút ghế và đối tượng
                seats[row][col] = seat;
                seatObjects[row][col] = currentSeat;
                seatStatus[row][col] = false;
                seat.addActionListener(this);
                pnlSeat.add(seat);
            }
        }

        // Thêm nhãn (1 đến 10) ở phía dưới
        pnlSeat.add(new JLabel(""));
        for (int col = 0; col < 10; col++) {
            JLabel colLabel = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
            pnlSeat.add(colLabel);
        }

        // Thêm panel ghế vào giữa bố cục
        add(pnlSeat, BorderLayout.CENTER);

        // Tạo và cấu hình panel nút xác nhận ở phía dưới
        JPanel pnlConfirm = new JPanel();
        confirmButton = new JButton("Xác nhận");
        confirmButton.addActionListener(this);
        pnlConfirm.add(confirmButton);
        add(pnlConfirm, BorderLayout.SOUTH);
    }

    // Thiết lập trình lựa chọn ghế
    public void setSeatSelectionListener(Consumer<List<String>> listener) {
        this.seatSelectionListener = listener;
    }

    // Xử lý sự kiện khi nhấn nút (chọn ghế hoặc xác nhận)
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Xử lý nhấn nút xác nhận
        if (source == confirmButton) {
            handleConfirmAction();
            return;
        }

        // Xử lý nhấn nút ghế
        JButton clickedSeat = (JButton) source;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (seats[row][col] == clickedSeat) {
                    Seats seat = seatObjects[row][col];
                    if (seat != null && clickedSeat.isEnabled()) {
                        if (!seatStatus[row][col]) {
                            // Chọn ghế nếu chưa đủ số lượng vé
                            if (selectedSeats.size() < ticketQuantity) {
                                clickedSeat.setBackground(Color.GREEN); // Ghế được chọn màu xanh lá
                                seatStatus[row][col] = true;
                                selectedSeats.add(seat);
                            } else {
                                JOptionPane.showMessageDialog(this, "Bạn đã chọn đủ số ghế!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            // Bỏ chọn ghế
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

    //Nhấn nút xác nhận
    private void handleConfirmAction() {
        // Kiểm tra xem đã chọn đúng số lượng ghế chưa
        if (selectedSeats.size() != ticketQuantity) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đúng " + ticketQuantity + " ghế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cập nhật trạng thái ghế thành "Booked" trong dbs
            for (Seats seat : selectedSeats) {
                seatStatusDAO.updateSeatStatus(showtimeID, seat.getSeatID(), "Booked");
            }
            // Chuẩn bị danh sách nhãn ghế đã chọn
            List<String> seatLabels = new ArrayList<>();
            for (Seats seat : selectedSeats) {
                seatLabels.add(seat.getSeatNumber());
            }
            // Thông báo cho trình nghe về các ghế được chọn
            if (seatSelectionListener != null) {
                seatSelectionListener.accept(seatLabels);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu ghế: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hỏi người dùng có muốn chọn đồ ăn không
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn chọn đồ ăn không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            // Chuyển đến màn hình chọn đồ ăn
            ProductSelectionGUI pss = new ProductSelectionGUI(mainFrame, showtimeID, ticketQuantity, ticketPrice, selectedSeats, room);
            mainFrame.showScreen("ProductSelection", pss);
        } else {
            //Đến màn hình xác nhận mà không chọn đồ ăn
            Users user = mainFrame.getUser();
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy thông tin suất chiếu từ cơ sở dữ liệu
            Showtimes_DAO showtimeDAO = new Showtimes_DAO(mainFrame.getConnection());
            Showtimes showtime;
            try {
                showtime = showtimeDAO.getShowtimeByID(showtimeID);
                if (showtime == null) {
                    throw new IllegalStateException("Không tìm thấy suất chiếu với ID: " + showtimeID);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin suất chiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo giỏ hàng rỗng nếu không chọn đồ ăn
            List<Product_Orders> cart = new ArrayList<Product_Orders>();

            // Chuyển đến màn hình xác nhận
            ConfirmationGUI cs = new ConfirmationGUI(
                mainFrame, 
                user, 
                showtime, 
                cart, 
                selectedSeats, 
                ticketQuantity, 
                ticketPrice,
                room
            );
            mainFrame.showScreen("Confirmation", cs);
        }
    }
}