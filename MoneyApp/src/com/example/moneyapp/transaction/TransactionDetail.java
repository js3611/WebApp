package com.example.moneyapp.transaction;

public class TransactionDetail {

	private int icon;
	private int transactionID;
	private String owesuser;
	private String user;
	private String subject;
	private int price;
	private int partial_pay;
	private String date;
	private String deadline;

	public TransactionDetail(){}
	
	public TransactionDetail(int icon, String from, String to, String subject,
			int price, String date, String deadline) {
		super();
		this.icon = icon;
		this.owesuser = from;
		this.user = to;
		this.subject = subject;
		this.price = price;
		this.date = date;
		this.deadline = deadline;
	}

	public String getOwesuser() {
		return owesuser;
	}

	public void setOwesuser(String from) {
		this.owesuser = from;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
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

	public int getPartial_pay() {
		return partial_pay;
	}

	public void setPartial_pay(int partial_pay) {
		this.partial_pay = partial_pay;
	}

}
