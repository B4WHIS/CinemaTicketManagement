CREATE TABLE Roles (
    roleID INT PRIMARY KEY IDENTITY(1,1),
    roleName VARCHAR(50) NOT NULL
);

CREATE TABLE Movies (
    movieID INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    duration INT,
    releaseDate DATE
);

CREATE TABLE Rooms (
    roomID INT PRIMARY KEY IDENTITY(1,1),
    roomName VARCHAR(50) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE PaymentMethods (
    paymentMethodID INT PRIMARY KEY IDENTITY(1,1),
    methodName VARCHAR(50) NOT NULL
);

CREATE TABLE Products (
    productID INT PRIMARY KEY IDENTITY(1,1),
    productName VARCHAR(100) NOT NULL,
    productType VARCHAR(50),
    price DECIMAL(10,2) NOT NULL
);

-- Tạo các bảng có khóa ngoại
CREATE TABLE Users (
    userID INT PRIMARY KEY IDENTITY(1,1),
    fullName VARCHAR(100),
    username VARCHAR(50) NOT NULL,
    passwordHash VARCHAR(100) NOT NULL,
    hireDate DATE,
    roleID INT,
    FOREIGN KEY (roleID) REFERENCES Roles(roleID)
);

CREATE TABLE Seats (
    seatID INT PRIMARY KEY IDENTITY(1,1),
    roomID INT,
    seatNumber VARCHAR(10) NOT NULL,
    FOREIGN KEY (roomID) REFERENCES Rooms(roomID)
);

CREATE TABLE Showtimes (
    showtimeID INT PRIMARY KEY IDENTITY(1,1),
    movieID INT,
    roomID INT,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (movieID) REFERENCES Movies(movieID),
    FOREIGN KEY (roomID) REFERENCES Rooms(roomID)
);

CREATE TABLE Orders (
    orderID INT PRIMARY KEY IDENTITY(1,1),
    userID INT,
    orderDate DATETIME NOT NULL,
    totalAmount DECIMAL(10,2),
    paymentMethodID INT,
    FOREIGN KEY (userID) REFERENCES Users(userID),
    FOREIGN KEY (paymentMethodID) REFERENCES PaymentMethods(paymentMethodID)
);

CREATE TABLE Tickets (
    ticketID INT PRIMARY KEY IDENTITY(1,1),
    showtimeID INT,
    seatID INT,
    saleDate DATETIME,
    price DECIMAL(10,2),
    FOREIGN KEY (showtimeID) REFERENCES Showtimes(showtimeID),
    FOREIGN KEY (seatID) REFERENCES Seats(seatID)
);

CREATE TABLE ProductOrders (
    productOrderID INT PRIMARY KEY IDENTITY(1,1),
    orderID INT,
    productID INT,
    quantity INT NOT NULL,
    totalPrice DECIMAL(10,2),
    FOREIGN KEY (orderID) REFERENCES Orders(orderID),
    FOREIGN KEY (productID) REFERENCES Products(productID)
);
