package model;

import java.time.LocalDateTime;

public class Tickets {
	private int ticketID;
	private int showTimeID;
	private LocalDateTime saleDate;
	private int orderID;
	private double price;
	
	public Tickets() {
		
	}

	public Tickets(int ticketID, int showTimeID, LocalDateTime saleDate, int orderID, double price) {
		this.ticketID = ticketID;
		this.showTimeID = showTimeID;
		this.saleDate = saleDate;
		this.orderID = orderID;
		this.price = price;
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public int getShowTimeID() {
		return showTimeID;
	}

	public void setShowTimeID(int showTimeID) {
		this.showTimeID = showTimeID;
	}

	public LocalDateTime getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(LocalDateTime saleDate) {
		this.saleDate = saleDate;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	
	
}
