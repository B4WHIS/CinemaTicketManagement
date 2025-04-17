package model;

import java.time.LocalDateTime;

public class Users {
	private int userID;
	private String fullName;
	private String userName;
	private String passwordHash;
	private String email;
	private LocalDateTime hireDate;
	private boolean available;
	private int roleID;
	
	
	public Users() {
		
	}


	public Users(int userID, String fullName, String userName, String passwordHash, String email,
			LocalDateTime hireDate, boolean available, int roleID) {
		this.userID = userID;
		this.fullName = fullName;
		this.userName = userName;
		this.passwordHash = passwordHash;
		this.email = email;
		this.hireDate = hireDate;
		this.available = available;
		this.roleID = roleID;
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPasswordHash() {
		return passwordHash;
	}


	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public LocalDateTime getHireDate() {
		return hireDate;
	}


	public void setHireDate(LocalDateTime hireDate) {
		this.hireDate = hireDate;
	}


	public boolean isAvailable() {
		return available;
	}


	public void setAvailable(boolean available) {
		this.available = available;
	}


	public int getRoleID() {
		return roleID;
	}


	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}
	
	
	
}
