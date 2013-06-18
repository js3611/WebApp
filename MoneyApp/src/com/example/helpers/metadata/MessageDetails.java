package com.example.helpers.metadata;

public class MessageDetails {

	private int icon;
	private int conversationID;
	private String last_message_date;
	private String last_message_time;
	private int user1;
	private int user2;
	private boolean group_chat;
	private int groupid;
	private String group_name;
	private String content;
	private int senderid;
	private String firstname;
	private String date;
	private String time;
	
	public MessageDetails(){}

	public MessageDetails(int icon, int conversationID, String last_message_date, String last_message_time, int user1, int user2,
			boolean group_chat, int groupid, String group_name, String content, int senderid, String firstname,
			String date, String time){
		this.icon = icon;
		this.conversationID = conversationID;
		this.last_message_date = last_message_date;
		this.last_message_time = last_message_time;
		this.user1 = user1;
		this.user2 = user2;
		this.group_chat = group_chat;
		this.groupid = groupid;
		this.group_name = group_name;
		this.content = content;
		this.senderid = senderid;
		this.firstname = firstname;
		this.date = date;
		this.time = time;
		
		
	}
	
	public int getConversationID() {
		return conversationID;
	}

	public void setConversationID(int conversationID) {
		this.conversationID = conversationID;
	}

	public String getLast_message_date() {
		return last_message_date;
	}

	public void setLast_message_date(String last_message_date) {
		this.last_message_date = last_message_date;
	}

	public String getLast_message_time() {
		return last_message_time;
	}

	public void setLast_message_time(String last_message_time) {
		this.last_message_time = last_message_time;
	}

	public int getUser1() {
		return user1;
	}

	public void setUser1(int user1) {
		this.user1 = user1;
	}

	public int getUser2() {
		return user2;
	}

	public void setUser2(int user2) {
		this.user2 = user2;
	}

	public boolean getGroup_chat() {
		return group_chat;
	}

	public void setGroup_chat(boolean group_chat) {
		this.group_chat = group_chat;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSenderid() {
		return senderid;
	}

	public void setSenderid(int senderid) {
		this.senderid = senderid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}
	
}
