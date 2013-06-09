package com.example.helpers.metadata;

public class UserDetails {

	private String phoneNo;
	private String surname;
	private String firstName;
	private String password; // WHY?
	private String someIDSTOFOLLOW; 
	//...
	
	
	public UserDetails(String phoneno,String surname,String firstname){
		this.phoneNo = phoneno;
		this.surname = surname;
		this.firstName = firstname;
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
	
	
}
