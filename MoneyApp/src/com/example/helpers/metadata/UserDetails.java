package com.example.helpers.metadata;

import java.io.Serializable;

import com.example.moneyapp.MainActivity;

import android.content.Intent;
import android.os.Bundle;

@SuppressWarnings("serial")
public class UserDetails implements Serializable {

	private int userid;
	private String surname;
	private String firstName;
	private int calendarid;
	private int wishlist;
	private String password; // WHY? what if someone wants to change password?
	private String phoneNo;
	private int profilePicture;
	private double amount;
	
	public UserDetails(int userid, String surname, String firstName,
			int calendarid, int wishlist, String password, String phoneNo,
			int profilePicture) {
		super();
		this.userid = userid;
		this.surname = surname;
		this.firstName = firstName;
		this.calendarid = calendarid;
		this.wishlist = wishlist;
		this.password = password;
		this.phoneNo = phoneNo;
		this.profilePicture = profilePicture;
	}
	public UserDetails() {
		// TODO Auto-generated constructor stub
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public int getCalendarid() {
		return calendarid;
	}
	public void setCalendarid(int calendarid) {
		this.calendarid = calendarid;
	}
	public int getWishlist() {
		return wishlist;
	}
	public void setWishlist(int wishlist) {
		this.wishlist = wishlist;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public int getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(int profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Override
	public String toString() {
		return "UserDetails [userid=" + userid + ", surname=" + surname
				+ ", firstName=" + firstName + ", calendarid=" + calendarid
				+ ", wishlist=" + wishlist + ", password=" + password
				+ ", phoneNo=" + phoneNo + ", profilePicture=" + profilePicture
				+ "]";
	}
	public static UserDetails getUser(Intent intent) {
		Bundle bundle = intent.getExtras();
		if(bundle!=null)
			return (UserDetails) bundle.getSerializable(MainActivity.USER_KEY);
		
		return null;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + userid;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetails other = (UserDetails) obj;
		if (userid != other.userid)
			return false;
		return true;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double  amount) {
		this.amount = amount;
	}
	
	
	
}

	