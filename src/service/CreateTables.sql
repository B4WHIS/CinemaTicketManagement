-- Tạo bảng Users
CREATE TABLE Users (
    userID INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    roleID INT,
    FOREIGN KEY (roleID) REFERENCES Roles(roleID)
);

-- Tạo bảng Seats
CREATE TABLE Seats (
    seatID INT PRIMARY KEY IDENTITY(1,1),
    roomID INT,
    FOREIGN KEY (roomID) REFERENCES Rooms(roomID)
);

-- Tạo bảng Tickets
CREATE TABLE Tickets (
    ticketID INT PRIMARY KEY IDENTITY(1,1),
    showtimeID INT,
    saleDate DATETIME,
    orderID INT,
    price DECIMAL(10,2),
    FOREIGN KEY (showtimeID) REFERENCES Showtimes(showtimeID),
    FOREIGN KEY (orderID) REFERENCES Orders(orderID)
);