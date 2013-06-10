package com.example.helpers.metadata;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserDetails implements Serializable {

	private String phoneNo;
	private String surname;
	private String firstName;
	private String password; // WHY? what if someone wants to change password?
	private String someIDSTOFOLLOW;
	private int profilePicture;

	// ...

	public UserDetails(String phoneNo, String surname, String firstName) {
		this.phoneNo = phoneNo;
		this.surname = surname;
		this.firstName = firstName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSomeIDSTOFOLLOW() {
		return someIDSTOFOLLOW;
	}

	public void setSomeIDSTOFOLLOW(String someIDSTOFOLLOW) {
		this.someIDSTOFOLLOW = someIDSTOFOLLOW;
	}

	public int getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(int profilePicture) {
		this.profilePicture = profilePicture;
	}

}
