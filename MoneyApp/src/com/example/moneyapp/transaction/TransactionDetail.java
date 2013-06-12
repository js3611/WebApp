package com.example.moneyapp.transaction;

public class TransactionDetail {

	private int icon;
	private int transactionID;
	private String owesuser;
	private String user;
	private String subject;
	private double price;
	private double partial_pay;
	private String date;
	private String deadline;

	public TransactionDetail(){}
	
	public TransactionDetail(int icon, String owesuser, String user, String subject,
			Double price, Double partial_pay, String date, String deadline) {
		super();
		this.icon = icon;
		this.owesuser = owesuser;
		this.user = user;
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

	@Override
	public String toString() {
		return "TransactionDetail [icon=" + icon + ", transactionID="
				+ transactionID + ", owesuser=" + owesuser + ", user=" + user
				+ ", subject=" + subject + ", price=" + price
				+ ", partial_pay=" + partial_pay + ", date=" + date
				+ ", deadline=" + deadline + "]";
	}

	
	
}
