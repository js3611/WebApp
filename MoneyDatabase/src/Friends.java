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


//TODO REMEMBER THAT THE DATABASE WAS CHANGED ADDED "confirmed" COLUMN FOR FRIENDS

import JSONBuilder.JSONBuilder;

/**
 * Servlet implementation class for Servlet: Login
 * 
 */
public class Friends extends javax.servlet.http.HttpServlet implements
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
			out.println("<h1>Driver not found: " + e + e.getMessage()
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

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	  
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
			out.println("<h1>Driver not found: " + e + e.getMessage()
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
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}


	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}

/* TODO
doGET:
1. Get list of all friends
rc 1 = no friends or fail
rc 2 = friends list success
rc 3 = NO FRIENDS LOL TOO SAD FOR U

2. Get list of people who've added you
rc 4 = failed
rc 5 = no requests at the moment
rc 6 = got requests

3. Check if user exists(for adding)
rc 9 = number not found
rc 12 = number existed user returned

4. View Friend profile
rc 16 = found friend
rc 17 = something went wrong

doPost:
1. Add friend
rc 7 = friend already exists
rc 8 = friend added

2. Delete friend
rc 13 = existing debts between users
rc 14 = something went wrong with delete
rc 15 = delete done

3. Edit friend
rc 18 = didnt work
rc 19 = edited successfully

4. Confirm friend
rc 10 = confirm didnt work
rc 11 = successful confirm
*/

	private void handleOperation(String operation, Conncection conn,
	 	HttpServletRequest request, PrintWriter writer) throws Exception {
	 	
	 	if (operation.equals("getFriends") {
	 	//doGet to get Current friends
	 		Statement getFriends = conn.createStatement();
	 		Statement friendCount = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parsenInt(request.getParameter("userid"));
	 		
	 		ResultSet friends = getFriends.executeQuery("SELECT * FROM friends "+
	 				"WHERE confirmed = true AND "+
					"(userid = " + userid + "  OR " +
					"friendid = " + userid + ");");
			
			ResultSet count = friendCount.executeQuery("SELECT count(userid) "+
					"FROM friends WHERE confirmed = true AND "+
					"(userid = " + userid + "  OR " +
					"friendid = " + userid + ");");
			
			if (!friends.isBeforeFirst() && !count.isbeforeFirst()) { // Something went wrong
				writer.println(getReturnCode(jb,1));
				
			} else if (!friends.isBeforeFirst()) { // YOU HAVE NO FRIENDS or soemthing went wrong
				int count = friendCount.getInt("count");
				if (count == 0)
					 writer.println(getReturnCode(jb,3));
				else
					writer.println(getReturnCode(jb,1));
					
			} else {
				jb.beginObject().append("returnCode",2).beginArray();
				while(friends.next()) {
					//if the supplied userid is in userid column
					if (userid = friends.getInt("userid")) {
						int friendid = friends.getInt("friendid");
						
						String firstname = IDtoNameMap.getFirstname(friendid);
						String surname = IDtoNameMap.getSurname(friendid);
						
						jb.beginObject().append("friendid",friendid)
										.append("firstname", firstname)
										.append("surname", surname)
						  .endObject();
					}
					jb.endArray().endObject();
					writer.println(jb.build());
				}
			}
	 	}
	 	else if (operation.equals("viewFriendDetails")) {
	 		Statement searchStmt = conn.createStatment();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int friendid = request.getParameter("friendid");
	 		ResultSet friendDetails = searchStmt.executeQuery("SELECT userid, firstname, surname, "+
	 							"wishlist, phonenumber FROM appuser WHERE userid = " +
	 							friendid + ";");
	 		
	 		if (!friendDetails.isBeforeFirst()) {
	 			writer.println(getReturnCode(jb,17));
	 		} else {		
	 		jb.beginObject().append("returnCode", 16)
	 						.append("userid", friendDetails.getInt("userid"))
	 						.append("firstname", friendDetails.getString("firstname"))
	 						.append("surname", friendDetails.getString("surname"))
	 						.append("wishlist", friendDetails.getInt("wishlist"))
	 						.append("phonenumber", friendDetails.getString("phonenumber"))
	 		  .endObject();
	 		  
	 		jb.endObject();
	 		writer.println(jb.build());  
	 		}
	 		
	 	}
	 	else if (operation.equals("getRequests") {
	 	//doGet to get friend requests 
	 		Statement getRequests = conn.createStatement();
	 		Statement requestCount = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		
	 		//Since when adding someone the person adding is "userid", get all requests that have user as "friendid"
	 		//This shows that they are the ones being added, or, "Requested"
	 		
	 		ResultSet requests = getRequests.executeQuery("SELECT * FROM friends f "+
	 				"INNER JOIN appuser a on (f.userid = a.userid)
	 				"WHERE confirmed = false AND "+
					"friendid = " + userid + ";");
					
			ResultSet count = requestCount.executeQuery("SELECT count(userid) "+
					"FROM friends WHERE confirmed = true AND "+
					"friendid = " + userid + ";");
	 	
	 	
	 		if (!requests.isBeforeFirst() && !count.isbeforeFirst()) { // Something went wrong
				writer.println(getReturnCode(jb,5));
				
			} else if (!requests.isBeforeFirst()) { // No requests or soemthing went wrong
				int count = friendCount.getInt("count");
				if (count == 0)
					 writer.println(getReturnCode(jb,4));
				else
					writer.println(getReturnCode(jb,5));
					
			} else {	
			//print all current requests
				jb.beginObject().append("returnCode",6).beginArray();

				while(requests.next()) {	
								
				int friendid = requests.getInt("friendid");
				String firstname = requests.getString("firstname");
				String surname = requests.getString("surname");
				String phonenumber = requests.getString("phonenumber");
								
				jb.beginObject().append("friendid",friendid)
								.append("firstname", firstname)
								.append("surname", surname)
								.append("phonenumber", phonenumber)
				  .endObject();
				}
					jb.endArray().endObject();
					writer.println(jb.build());
				}
	 		
	 	} 
	 	else if (operation.equals("searchFriend") {
	 	// searching for a friend in the database
	 		 	Statement addStmt = conn.createStatement();
	 		 	JSONBuilder jb = new JSONBuilder();
	 		 	
	 		 	int phonenumber = Integer.parseInt(request.getParameter("phonenumber"));
	 	
	 			ResultSet rs = addStmt.executeQuery("SELECT userid, firstname, surname " +
	 							"FROM appuser WHERE phonenumber = " +
	 							phonenumber + ";");
	 			
	 			if (rs == 0)  //Phonenumber doesnt' exist
	 				writer.println(getReturnCode(jb,12));
	 			
	 			jb.beginObject().append("returnCode",9)
	 							.append("userid", rs.getInt("userid"))
	 							.append("firstname", rs.getString("firstname"))
	 							.append("surname", rs.getString("surname"))
	 			  .endObject();
	 			  
	 			writer.println(jb.build());
	 			
	 	}
	 	else if (operation.equals("addFriend") {
	 		Statement checkStmt = conn.createStatement();

	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userd"));
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 						
	 		int rs = addStmt.executeUpdate("INSERT INTO friends VALUES (" +
	 						userid + ", " +
	 						friendid +"; ");
	 						
	 						
	 		if (rs == 0) // When either friendship exists or query didnt work 
	 				     //(Can we change this so no suggestions for existing friends show up?)
	 			writer.println(getReturnCode(jb,7));
	 		else		 
	 			writer.println(getReturnCode(jb,8));
	 						
	 	}
	 	else if (operation.equals("deleteFriend") {
	 		//FOR NOW JUST DELETES THE FOREVER as long as no oustanding debts
	 		Statement searchStmt = conn.createStatement();
	 		Statement dltStmt = conn.createStatement();
	 		JSONbuilder jb = new JSONBuilder();
	 		
	 		int friendid = request.getParameter("friendid");
	 		
	 		//finds out if any money should exchange between these two people.
	 		ResultSet oustanding_debts = searchStmt.executeQuery("SELECT * FROM debts WHERE " +
	 							"userid = " + friendid + " OR " +
	 						    "friendid = " + friendid + ";");
	 						    
	 		if (!outstanding_debts.isBeforeFirst()) {// No debts
	 			int userid = request.getParameter("userid");
	 		
	 			int rs = dltStmt.executeUpdate("DELETE FROM FRIENDS WHERE (userid = " +
	 							userid + " AND friendid = " +
	 							friendid + ":");
	 							
	 			if (rs == 0) //delete didnt work
	 				writer.println(getReturnCode(jb,14));
	 			else
	 				writer.println(getReturnCode(jb,15));	
	 		}
	 		else // else debts exist so cannot delete
	 			writer.println(getReturnCode(jb,13));			
	 		
	 	}
	 	else if (operation.equals("confirmRequest") {
	 		Statement confirmStmt = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		
	 		int rs = confirmStmt.executeUpdate("UPDATE friends " +
	 					"SET confirm = true WHERE userid = " + 
	 					friendid + " AND friendid = " +
	 					userid + ";");
	 					
	 		if (rs == 0)
	 			writer.println(getReturnCode(jb,10));
	 		else 
	 			writer.println(getReturnCode(jb,11));
	 	}
	 
	 }
	 
}


/*
	 	
	 	SHOULDN't BE A NEED TO EDIT FRIEND?! maybe
	 	else if (operation.equals("editFriend") {
	 		Statement updateStmt = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		String firstname = request.getParameter("firstname");
	 		String surname = request.getParameter("surname");
	 		String phonenumber = request.getParameter("phonenumber");
	 		
	 		int rs = updateStmt.executeUpdate("UPDATE appuser SET " +
	 						"firstname = " + firstname + ", " +
	 						"surname = " + surname + ", " +
	 						"phonennumber = " + phonenumber + " WHERE " +
	 						"userid = " + friendid + ";");
	 		
	 		if (rs == 0)
	 			writer.println(getReturnCode(jb,18));
	 		else 
	 			writer.println(getReturnCode(jb,19));
	 		
	 		
	 	} */

