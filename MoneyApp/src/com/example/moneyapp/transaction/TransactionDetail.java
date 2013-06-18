package com.example.moneyapp.transaction;

public class TransactionDetail {

	private int icon;
	private int transactionID;
	private String owesuser;
	private String user;
	private int owesuserid;
	private int userid;
	private String subject;
	private double price;
	private double partial_pay;
	private String date;
	private String deadline;
	private String description;

	public TransactionDetail(){}
	
	public TransactionDetail(int icon, int transactionID, String owesuser, String user, int owesuserid, int userid, String subject,
			Double price, Double partial_pay, String date, String deadline) {
		super();
		this.transactionID = transactionID;
		this.icon = icon;
		this.owesuser = owesuser;
		this.user = user;
		this.owesuserid = owesuserid;
		this.userid = userid;
		this.subject = subject;
		this.price = price;
		this.partial_pay = partial_pay;
		this.date = date;
		this.deadline = deadline;
	}

	public String getOwesuser() {
		return owesuser;
	}

	public void setOwesuser(String from) {
		this.owesuser = from;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String to) {
		this.user = to;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public double getPartial_pay() {
		return partial_pay;
	}

	public void setPartial_pay(double partial_pay) {
		this.partial_pay = partial_pay;
	}

	public double getRemainingToPay() {
		return price - partial_pay;
	}

	

	@Override
	public String toString() {
		return "TransactionDetail [icon=" + icon + ", transactionID="
				+ transactionID + ", owesuser=" + owesuser + ", user=" + user
				+ ", owesuserid=" + owesuserid + ", userid=" + userid
				+ ", subject=" + subject + ", price=" + price
				+ ", partial_pay=" + partial_pay + ", date=" + date
				+ ", deadline=" + deadline + "]";
	}

	public int getOwesuserid() {
		return owesuserid;
	}

	public void setOwesuserid(int owesuserid) {
		this.owesuserid = owesuserid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
}
