import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import JSONBuilder.JSONBuilder;
/**
 * Servlet implementation class for Servlet: Login
 * 
 */
public class Message extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

		/*
		 * (non-Java-doc)
		 * 
		 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
		 *      HttpServletResponse response)
		 */
		 
		 
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String operation = request.getParameter("op");				
		PrintWriter writer = response.getWriter();
	
		JSONBuilder jb = new JSONBuilder();
	
		if (operation == null) {
			JSONBuilder jb = new JSONBuilder();
			writer.println(getReturnCode(jb,0));
			return;
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			writer.println("<h1>Driver not found: " + e + e.getMessage()
							+ "</h1>");
		}

		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
					+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
					"g1227132_u", "W0zFGMaqup");
			
			handleOperation(operation, conn, request, writer);
			conn.close();
		} catch (Exception e) {
			writer.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
			}
			
	
	
		protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = response.getWriter();
		String operation = request.getParameter("op");

		if (operation == null) {
			JSONBuilder jb = new JSONBuilder();
			writer.println(getReturnCode(jb,0));
			return;
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			writer.println("<h1>Driver not found: " + e + e.getMessage()
											+ "</h1>");
		}

		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
					+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
					"g1227132_u", "W0zFGMaqup");
			
			handleOperation(request.getParameter("op"), conn, request, writer);
			conn.close();

		} catch (Exception e) {
			writer.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}


	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}
	
	
		
/*TODO
doGet:
1. get message list
rc 42 = nope, no messages
rc 43 = yup successful display of all messages

2. get full message history / details
rc 44 = something went wrong, no message
rc 45 = messages for the convo displayed	
	
doPost:
1. Send a Message
rc 46 = add went wrong, didn't work
rc 47 = add was correctly registered

2. Make new Single message
rc 48 =  adding new message wrong
rc 49 = adding messagecontent to that emssage went wrong
rc 410 = fine
	
3. Make new Group message	
rc 411 = could not make new group
rc 412 = could not add new message
rc 413 = could not add message content
rc 414 = done
*/	
	
	private void handleOperation(String operation, Conncection conn,
	 	HttpServletRequest request, PrintWriter writer) throws Exception {
		
	if (operation.equals("messageList")) {
		JSONBuilder jb = new JSONBuilder();
		Statement viewStmt = conn.createStatement();
		int userid = Integer.parseInt(request.getParameter("userid"));
		
		//Query gets all fields of "message" where the supplied userid is part of the 
		//conversation OR part of group if it is a group conversation
		ResultSet messageList = viewStmt.executeQuery("SELECT conversationid, _date, _time, " + 
					"user1, user2, a.groupid, group_chat, group_name FROM messagegroups a INNER JOIN " +
					"(select m.conversationid, m._date,m._time,m.user1, m.user2, " +
					"m.groupid, m.group_chat from message m inner join chatgroups cg " +
					"ON  (m.groupid = cg.groupid) where m.user1 =" +
					userid + " OR cg.userid = " +
					userid + " UNION SELECT * FROM message WHERE user1 = " +
					userid + " OR user2 = " + 
					userid + ") AS b ON (a.groupid = b.groupid;");
					
		if (!messageList.isBeforeFirst()) // no messages
			writer.println(getReturnCode(jb,42));
		else {
		//NOTE that the group_chat field will be false if convo is not between group and thus
		//groupid and group_name will not be useful (though group = 0.)
			jb.beginObject().append("returnCode",43).beginArray();
			(while messageList.next()){
				jb.beginObject().append("conversationid", messageList.getInt("conversationid"))
								.append("last_message_date", messageList.getString("_date"))
								.append("last_message_time", messageList.getString("_time"))
								.append("user1", messageList.getInt("user1"))
								.append("user2", messageList.getInt("user2"))
								.append("group_chat", messageList.getString("group_chat"))
								.append("groupid", messageList.getInt("groupid"))
								.append("group_name", messageList.getString("group_name"))
				  .endObject();
			}
			jb.endArray().endObject();
			writer.println(jb.build());
		}	
	}
	else if (operation.equals("messageDetails")) {
		JSONBuilder jb = new JSONBuilder();
		Statement viewStmt = conn.createStatement();
		Statement detailStmt = conn.createStatement();
		int conversationid = Integer.parseInt(request.getParameter("conversationid"));
		int other_person = Integer.parseInt(request.getParameter("userid")); 
		String name = request.getParameter("name");	//Note, depending on whether the message is a group_chat or not, the
													//name sent is either the person you are talking to or the group name. 
													// same applies for the other_person/userid
		
 		ResultSet messages = viewStmt.executeQuery("SELECT mc.content, mc._date,mc._time, " +
					"mc.userid, a.firstname FROM messagecontent mc " +
					"INNER JOIN appuser a ON(mc.userid = a.userid) WHERE conversationid = " +
					conversationid + "ORDER BY _date ASC, _time ASC;");
		
		if (!messages.isBeforeFirst()) // no messages
			writer.println(getReturnCode(jb,44));
		else {
		/* select mc.content, mc._date,mc._time, mc.userid, a.firstname, a.surname from messagecontent mc inner join appuser a on(mc.userid=a.userid) where conversationid = 2 ORDER BY _date ASC, _time ASC; */
			jb.beginObject().append("returnCode",45).append("other_person", other_person).beginArray();
			(while messages.next()){
				jb.beginObject().append("content", messageList.getInt("content"))
								.append("date", messageList.getString("_date"))
								.append("time", messageList.getString("_time"))
								.append("senderid", messageList.getInt("userid"))
								.append("firstname", messageList.getString("firstname"))
				  .endObject();
			}
			jb.endArray().endObject();
			writer.println(jb.build());
		}	
		
	}
	else if (operation.equals("sendMessage")) {
		JSONBuilder jb = new JSONBuilder();
		Statement addStmt = conn.createStatement();
		int conversationid = Integer.parseInt(request.getParameter("conversationid"));
		int userid = Integer.parseInt(request.getParameter("userid"));
		String date = request.getParameter("_date");
		String time = request.getParameter("_time");
		String content = request.getParameter("content"); 
		
		int rs = addStmt.executeUpdate("INSERT INTO messagecontent (content, _date, " +
										"_time, conversationid, userid) VALUES (" +
										content + ", " +
										date + ", " +
										time + ", " +
										conversationid + ", " +
										userid + ");");
										
		if (rs == 0) // add didn't work
			writer.println(getReturnCode(jb,46));
		else // add worked
			writer.println(getReturnCode(jb,47));
		
	}
	else if (operation.equals("newSingleMessage")) {
		JSONBuilder jb = new JSONBuilder();
		Statement newMessageStmt = conn.createStatement();
		Statement contentStmt = conn.createStatement();
		Statement checkStmt = conn.createStatement();
		
		int newConversationid = 0;
		int userid = Integer.parseInt(request.getParameter("userid"));		
		int friendid = Integer.parseInt(request.getParameter("friendid"));
		String date = request.getParameter("_date");
		String time = request.getParameter("_time");
		String content = request.getParameter("content");
				
		
		ResultSet rs = checkStmt.executeQuery("SELECT * FROM message WHERE "+
						"(user1 =" +
						userid + " AND user2 =" +
						friendid + ") OR (user1 = "+
						friendid + " AND user2 = " +
						userid + ") returning conversationid");
						
		if (rs.next()) { // means a message between these two people already exists
			ModifiedServeltRequestMsg msr = new ModifiedServletRequestMsg(request,conversationid);
			handleOperation("sendMessage",conn, msr, writer)
		}
		else{
						

		ResultSet message = newMessageStmt.executeQuery("INSERT INTO message( _date, "+
									"_time, user1, user2) VALUES (" + 
									date + ", " +
									time + ", " +
									userid + ", " +
									friendid +") returning conversationid");
									
		if (message.next()) {
			newConversationid = message.getInt("conversationid");
			
			int rs = contentStmt.executeUpdate("INSERT INTO messagecontent (content, "+
						"_date, _time, conversationid, userid) VALUES (" +
						content + ", " +
						date + ", " + 
						time + ", " +
						conversationid + ", " +
						userid + ")");
						
			if (rs == 0) // wrong
				writer.println(getReturnCode(jb,49));
			else
				writer.println(getReturnCode(jb,410));
			
		} 			
		else // nothing returned, went wrong wrong
			writer.println(getReturnCode(jb,48));
		}

	}
	else if (operation.equals("newGroupMessage")) {
		JSONBuilder jb = new JSONBuilder();
		int groupid = 0;
		Statement newMessageStmt = conn.createStatement();
		Statement contentStmt = conn.createStatement();
		Statement newGroupStmt = conn.createStatement();
		
		//Again sending the list in the form (userid,userid,userid...)
		String userList = request.getParameter("userList");
		String[] users = userList.split(",");
		int numUsers = user.size();
		
		//TODO MUST CHECK THIS GROUP DOESNT ALREADY EXIST THIS IS TROUBLESOME
		//MEANS HAVE TO INNER JOIN ON CHATGROUPS AND THEN CHECK ALL THE PEOPLE INVOLVED
		String group_name = request.getParameter("group_name");
		
		ResultSet messagegroup = newGroupStmt.executeQuery("INSERT INTO messagegroups(group_name) "+
									"VALUES ( " +
									group_name +") returning groupid;");
							
		if(messagegroup.next()) { //new group created and that id returned
			groupid = messagegroup.getInt("groupid");
			
			String date = request.getParameter("_date");
			String time = request.getParameter("_time");
			int userid = Integer.parseInt(request.getParameter("userid"));
		
			String content = request.getParameter("content");
				
			ResultSet message = newMessageStmt.executeQuery("INSERT INTO message( _date, "+
										"_time, user1, groupid, group_chat) VALUES (" + 
										date + ", " +
										time + ", " +
										userid + ", " +
										groupid +", true) returning conversationid");
			
			if(message.next()) {
				newConversationid = message.getInt("conversationid");
			
				int rs = contentStmt.executeUpdate("INSERT INTO messagecontent (content, "+
						"_date, _time, conversationid, userid) VALUES (" +
						content + ", " +
						date + ", " + 
						time + ", " +
						conversationid + ", " +
						userid + ")");
						
				if (rs == 0) // wrong
					writer.println(getReturnCode(jb,413));
				else
					writer.println(getReturnCode(jb,414));
			}
				else //Could not add this new message
					writer.println(getReturnCode(jb,412));
			
			}else  // new group not created
				writer.println(getReturnCode(jb,411));
	
	}

}

