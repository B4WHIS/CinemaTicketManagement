package model;

import java.time.LocalDateTime;

public class Tickets {
	private int ticketID;
	private Showtimes showTimeID;
	private LocalDateTime saleDate;
	private Orders orderID;
	private Seats seatID;
	private double price;
	
	public Tickets() {
		
	}



	public Tickets(int ticketID, Showtimes showTimeID, LocalDateTime saleDate, Orders orderID, Seats seatID,
			double price) {
		this.ticketID = ticketID;
		this.showTimeID = showTimeID;
		this.saleDate = saleDate;
		this.orderID = orderID;
		this.seatID = seatID;
		this.price = price;
	}



	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public Showtimes getShowTimeID() {
		return showTimeID;
	}

	public void setShowTimeID(Showtimes showTimeID) {
		this.showTimeID = showTimeID;
	}

	public LocalDateTime getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(LocalDateTime saleDate) {
		this.saleDate = saleDate;
	}

	public Orders getOrderID() {
		return orderID;
	}

	public void setOrderID(Orders orderID) {
		this.orderID = orderID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}



	public Seats getSeatID() {
		return seatID;
	}



	public void setSeatID(Seats seatID) {
		this.seatID = seatID;
	}
	
	
	
}
