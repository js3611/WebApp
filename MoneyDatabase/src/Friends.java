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

import IdMap.IDtoNameMap;
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


	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}

/* TODO
doGET:
1. Get list of all friends
rc 31 = no friends or fail
rc 32 = friends list success
rc 33 = NO FRIENDS LOL TOO SAD FOR U

2. Get list of people who've added you
rc 34 = failed
rc 35 = no requests at the moment
rc 36 = got requests

3. Check if user exists(for adding)
rc 312 = number not found
rc 39 = number existed user returned

4. View Friend profile
rc 316 = found friend
rc 317 = something went wrong

doPost:
1. Add friend
rc 37 = friend already exists
rc 38 = friend added

2. Delete friend
rc 313 = existing debts between users
rc 314 = something went wrong with delete
rc 315 = delete done

3. Edit friend
rc 318 = didnt work
rc 319 = edited successfully

4. Confirm friend
rc 310 = confirm didnt work
rc 311 = successful confirm
*/

	private void handleOperation(String operation, Connection conn,
	 	HttpServletRequest request, PrintWriter writer) throws Exception {
	 	
	 	if (operation.equals("getFriends")) { //DONT USE THIS
	 	//doGet to get Current friends
	 		Statement getFriends = conn.createStatement();
	 		Statement friendCount = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		
	 		ResultSet friends = getFriends.executeQuery("SELECT * FROM friends "+
	 				"WHERE confirmed = true AND "+
					"(userid = " + userid + "  OR " +
					"friendid = " + userid + ");");
			
			ResultSet count = friendCount.executeQuery("SELECT count(userid) "+
					"FROM friends WHERE confirmed = true AND "+
					"(userid = " + userid + "  OR " +
					"friendid = " + userid + ");");
			
			if (!friends.isBeforeFirst() && !count.isBeforeFirst()) { // Something went wrong
				writer.println(getReturnCode(jb,31));
				
			} else if (!friends.isBeforeFirst()) { // YOU HAVE NO FRIENDS or something went wrong
				int total = count.getInt("count");
				if (total == 0)
					 writer.println(getReturnCode(jb,33));
				else
					writer.println(getReturnCode(jb,31));
					
			} else {
				jb.beginObject().append("returnCode",32).beginArray();
				while(friends.next()) {
					//if the supplied userid is in userid column
					if (userid == friends.getInt("userid")) {
						int friendid = friends.getInt("friendid");
						
						String firstname = null;
						String surname = null;
						
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
	 		Statement searchStmt = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		ResultSet friendDetails = searchStmt.executeQuery("SELECT userid, firstname, surname, "+
	 							"wishlist, phonenumber FROM appuser WHERE userid = " +
	 							friendid + ";");
	 		
	 		if (!friendDetails.isBeforeFirst()) {
	 			writer.println(getReturnCode(jb,317));
	 		} else {		
	 		jb.beginObject().append("returnCode", 316)
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
	 	else if (operation.equals("getRequests")) {
	 	//doGet to get friend requests 
	 		Statement getRequests = conn.createStatement();
	 		Statement requestCount = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int friendid = Integer.parseInt(request.getParameter("userid"));
	 		
	 		//Since when adding someone the person adding is "userid", get all requests that have user as "friendid"
	 		//This shows that they are the ones being added, or, "Requested"
	 		
	 		ResultSet requests = getRequests.executeQuery("SELECT * FROM friends f "+
	 				"INNER JOIN appuser a on (f.userid = a.userid)" +
	 				"WHERE confirmed = false AND "+
					"friendid = " + friendid + ";");
					
			ResultSet count = requestCount.executeQuery("SELECT count(userid) as count "+
					"FROM friends WHERE confirmed = false AND "+
					"friendid = " + friendid + ";");
	 	
	 	
	 		if (!requests.isBeforeFirst() && !count.isBeforeFirst()) { // Something went wrong
				writer.println(getReturnCode(jb,35));
				
			} else if (!requests.isBeforeFirst()) { // No requests or soemthing went wrong
				count.next();
				int total = count.getInt("count");
				if (total == 0)
					 writer.println(getReturnCode(jb,34));
				else
					writer.println(getReturnCode(jb,35));
					
			} else {	 
			//print all current requests
				jb.beginObject().append("returnCode",36).beginArray();

				while(requests.next()) {	
				//Your friend id is actually userid
				int userid = requests.getInt("userid");
				String firstname = requests.getString("firstname");
				String surname = requests.getString("surname");
				String phonenumber = requests.getString("phonenumber");
								
				jb.beginObject().append("userid",userid)
								.append("firstname", firstname)
								.append("surname", surname)
								.append("phonenumber", phonenumber)
				  .endObject();
				}
				jb.endArray().endObject();
				writer.println(jb.build());
			}
	 		
	 	} 
	 	else if (operation.equals("searchFriend")) {
	 	// searching for a friend in the database
	 		 	Statement addStmt = conn.createStatement();
	 		 	JSONBuilder jb = new JSONBuilder();
	 		 	
	 		 	String phonenumber = request.getParameter("phonenumber");
	 	
	 			ResultSet rs = addStmt.executeQuery("SELECT userid, firstname, surname " +
	 							"FROM appuser WHERE phonenumber = '" +
	 							phonenumber + "';");
	 			
	 			if (rs.next()) { //Phonenumber exists
	 				
	 				jb.beginObject().append("returnCode",39)
	 				  .beginObject()
						.append("userid", rs.getInt("userid"))
						.append("firstname", rs.getString("firstname"))
						.append("surname", rs.getString("surname"))
					  .endObject()
					  .endObject();
		  
	 				writer.println(jb.build());
	 			}
	 			else 
	 				writer.println(getReturnCode(jb,312));
 							 			
	 	}
	 	else if (operation.equals("addFriend")) {
	 		Statement addStmt = conn.createStatement();
	 		Statement checkStmt = conn.createStatement();
	 		
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		
	 		ResultSet pending = checkStmt.executeQuery("SELECT confirmed FROM friends WHERE (userid = " +
	 							 userid + " AND friendid = " +
	 							 friendid + ") OR "+
	 							 "(userid = " +
	 							 friendid + " AND friendid =" +
	 							 userid + ")");
	 		
	 		if (!pending.next()) {
	 			int rs = addStmt.executeUpdate("INSERT INTO friends VALUES (" +
	 						userid + ", " +
	 						friendid +"); ");
	 			
	 			if (rs == 0)
	 				writer.println(getReturnCode(jb,37));
	 			else
	 				writer.println(getReturnCode(jb,38));
	 		}

	 		// When either friendship exists or query didn't work 
	 		//TODO (Can we change this so no suggestions for existing friends AND pending friends show up?)
	 		else {
	 			boolean confirmed = pending.getBoolean("confirmed");
	 			if (confirmed)
	 				writer.println(getReturnCode(jb,321));
	 			else
	 				writer.println(getReturnCode(jb,320));
	 		}
	 						
	 	}
	 	else if (operation.equals("deleteFriend")) {
	 		//FOR NOW JUST DELETES THE FOREVER as long as no oustanding debts
	 		Statement searchStmt = conn.createStatement();
	 		Statement dltStmt = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		
	 		//finds out if any money should exchange between these two people.
	 		ResultSet outstanding_debts = 
	 			searchStmt.executeQuery(
	 					"SELECT * " +
	 					"FROM debt " +
	 					"WHERE " +"(userid = " + friendid + "AND owesuserid ="+ userid +" AND paid_off=false)" 
	 					+" OR " + "(userid = " + userid + "AND owesuserid ="+ friendid +" AND paid_off=false);");
	 						    
	 		if (!outstanding_debts.isBeforeFirst()) {// No debts
	 		
	 			int rs = dltStmt.executeUpdate(
	 					"DELETE FROM friends " +
	 					"WHERE (userid = " + friendid + " AND friendid = " + userid + ")" +
	 					"OR" +"(userid = " + userid + " AND friendid = " + friendid + ");");
	 							
	 			if (rs == 0) //delete didn't work
	 				writer.println(getReturnCode(jb,314));
	 			else //deleted
	 				writer.println(getReturnCode(jb,315));	
	 		}
	 		else // else debts exist so cannot delete
	 			writer.println(getReturnCode(jb,313));			
	 		
	 	} else if (operation.equals("confirmRequest")) {
	 		Statement confirmStmt = conn.createStatement();
	 		JSONBuilder jb = new JSONBuilder();
	 		
	 		int userid = Integer.parseInt(request.getParameter("userid"));
	 		int friendid = Integer.parseInt(request.getParameter("friendid"));
	 		
	 		int rs = confirmStmt.executeUpdate(
	 				"UPDATE friends " +
	 				"SET confirmed = true " +
	 				"WHERE userid = " + friendid + " " +
	 				"AND friendid = " +	userid + ";");
	 					
	 		if (rs == 0)
	 			writer.println(getReturnCode(jb,310));
	 		else 
	 			writer.println(getReturnCode(jb,311));
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

