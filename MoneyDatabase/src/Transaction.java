import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import HttpServletReqWrapper.ModifiedServletRequestTrans;
import JSONBuilder.JSONBuilder;
import IdMap.IDtoNameMap;

/**
 * Servlet implementation class for Servlet: Transaction
 * 
 */
public class Transaction extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		PrintWriter writer = response.getWriter();
		String operation = request.getParameter("op");
		String viewMode = request.getParameter("viewMode");
			
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			writer.println("<h1>Driver not found: " + e + e.getMessage()
							+ "</h1>");
		}

		try {
			Connection conn = DriverManager
					.getConnection(
							"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
									+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
							"g1227132_u", "W0zFGMaqup");
	
			handleGetOperation(operation, viewMode, conn, request, writer);

		} catch (Exception e) {
			writer.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}
	

	private void handleGetOperation(String operation, String viewMode, Connection conn,
			HttpServletRequest request, PrintWriter writer) throws Exception {
		
		if (operation == null) {
			JSONBuilder jb = new JSONBuilder();
			writer.println(getReturnCode(jb,0));
			return;
		}
		
		if (viewMode.equals("perItem")) {	
			
			if (operation.equals("viewLiveTransactions")) {
				Statement transStmt = conn.createStatement();
				JSONBuilder jb = new JSONBuilder();
				
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				// Shows all transactions that have yet to be completed
				ResultSet transactionSet = transStmt.executeQuery(
						"SELECT t.transid, t.name,a._date, a.userid, sum(a.amount) - sum(a.partial_pay) as amount " +
						"FROM " + "transactions t INNER JOIN " +
							"(SELECT t.transid, t.name, t._date, d.amount, d.partial_pay, d.userid, d.owesuserid " +
							"FROM " + "transactions t INNER JOIN debt d ON (t.transid = d.transid) AND d.paid_off=false  " +
							"WHERE(d.userid = " + userid + "OR d.owesuserid = " +userid + ") " +
							"AND t.total_paid_off = false ORDER BY t._date DESC) " + "as a ON (t.transid = a.transid) " +
						"GROUP BY t.transid, t.name, a.userid, a._date" + ";");
	
				if (!transactionSet.isBeforeFirst()) {
					writer.println(getReturnCode(jb,22));
				}
				else {
					jb.beginObject().append("returnCode",1).beginArray();
					while (transactionSet.next()) {
						int transId = transactionSet.getInt("transid");
						String transName = transactionSet.getString("name");
						Double transAmount = transactionSet.getDouble("amount");
						int transUserid = transactionSet.getInt("userid");
						String date = transactionSet.getString("_date");
						
						jb.beginObject().append("transid", transId)
										.append("name",transName)
										.append("amount",transAmount)
										.append("userid",transUserid)
										.append("date",date)
						  .endObject();
						}
						jb.endArray().endObject();			
						writer.println(jb.build());
					}
				
			} else if (operation.equals("viewDeadTransactions")) {
				Statement transStmt = conn.createStatement();
				JSONBuilder jb = new JSONBuilder();
				
				int userid = Integer.parseInt(request.getParameter("userid"));
	
				// Shows all transactions that have already been completed
				ResultSet transactionSet = transStmt
						.executeQuery("SELECT t.transid, t.name, t.complete_date, t.total_amount, d.owesuserid FROM "
								+"transactions t INNER JOIN debt d on (t.transid = d.transid) WHERE (d.userid = "
								+ userid
								+ " OR d.owesuserid = "
								+ userid
								+ ") AND t.total_paid_off = true ORDER BY t._date DESC");
	
				if (!transactionSet.isBeforeFirst()) {
					writer.println(getReturnCode(jb,4));
				} else {
					jb.beginObject().append("returnCode",1).beginArray();
					while (transactionSet.next()) {
						int transId = transactionSet.getInt("transid");
						String transName = transactionSet.getString("name");
						String transCompleteDate = transactionSet.getString("complete_date");
						double transAmount = transactionSet.getDouble("total_amount");
						
						jb.beginObject().append("transid", transId)
										.append("name",transName)
										.append("total_amount",transAmount)
										.append("complete_date",transCompleteDate)
						  .endObject();
						}
						jb.endArray().endObject();			
						writer.println(jb.build());
				}
	
			} else if (operation.equals("transactionDetails")) {
				Statement detailsStmt = conn.createStatement();
				Statement debtsStmt = conn.createStatement();
				JSONBuilder jbDetails = new JSONBuilder();
				JSONBuilder jbDebt = new JSONBuilder();
				
				int userid = Integer.parseInt(request.getParameter("userid"));
				int transid = Integer.parseInt(request.getParameter("transid"));
				// Gets the transaction details
				ResultSet detailsSet = detailsStmt
						.executeQuery("SELECT * FROM transactions WHERE transid = "
								+ transid + " ;");				
					
				// Gets the individual debts of each transaction (should have 1
				// result if user owes, multiple if user is owed)
				ResultSet debtSet = debtsStmt
						.executeQuery("SELECT d.transid, d.amount, d.userid, a.firstname, a.surname, amount, d.paid_off, d.partial_pay FROM" 
								+ "  debt d INNER JOIN appuser a ON (d.owesuserid = a.userid)"  
								+ " AND d.transid = " + transid +";" );
				
				if (!detailsSet.isBeforeFirst() || !debtSet.isBeforeFirst()) { // SELECTs returned
															// nothing , i.e.
															// something went
															// wrong
					writer.println(getReturnCode(jbDebt,22));
				} else {
						
					// Print the details of the transaction first then the people
					// involved in it after
					detailsSet.next();
					int details_transid = detailsSet.getInt("transid");
					String details_name = detailsSet.getString("name");
					double details_amount = detailsSet.getDouble("total_amount");
					String details_date = detailsSet.getString("_date");
					int details_urgency = detailsSet.getInt("urgency");
					String details_complete_date = detailsSet.getString("complete_date");
					String details_description = detailsSet.getString("description");
					String details_paid_off = detailsSet.getString("total_paid_off");
						
					// TODO CHANGE THIS IN THE IMPLEMENTATION, ADDED "CAN_DELETE"	
					// WILL ALLOW DETERMINATION OF WHETHER USER CAN DELETE THIS TRANSACTION
					//CHECK AND CHANGE
					debtSet.next();
					int transactionStarter = debtSet.getInt("userid");
					boolean deletable = (userid == transactionStarter);
						
					jbDetails.beginObject().append("returnCode",21).append("can_delete", deletable);
					jbDetails.beginObject().append("transid", details_transid)
										   .append("name",details_name)
										   .append("total_amount",details_amount)
										   .append("date",details_date)
										   .append("urgency",details_urgency)
										   .append("description",details_description)
										   .append("complete_date",details_complete_date)
										   .append("total_paid_off",details_paid_off)
										   .append("userid", transactionStarter);
										  // .append("can_delete", deletable);
					jbDetails.endObject();
					
					jbDetails.beginArray();
					//writer.println(jbDetails.build());
					
					do {
						int transId = debtSet.getInt("transid");
						double amount = debtSet.getDouble("amount");
						int friendid = debtSet.getInt("userid");
						String friendfname = debtSet.getString("firstname");	
						double partial =  debtSet.getDouble("partial_pay");
						boolean paid_off = debtSet.getBoolean("paid_off");
						
						jbDetails.beginObject().append("transid", transId)
											.append("userid",friendid)
											.append("firstname",friendfname)
											.append("amount",amount)
											.append("paid_off",paid_off)
											.append("partial_pay",partial)
							  .endObject();
						} while (debtSet.next()); 
					jbDetails.endArray().endObject();			
					writer.println(jbDetails.build());
				}	
			}
		} else if (viewMode.equals("perPerson")) {
			
			if(operation.equals("getFriendsList")) {
				Statement getFriendsStmt = conn.createStatement();
				JSONBuilder jb = new JSONBuilder();
				
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				ResultSet friendsList = getFriendsStmt.executeQuery("SELECT f.friendid, a.firstname, a.surname FROM friends f " 
											+ "INNER JOIN appuser a on f.friendid = a.userid WHERE f.userid = "
											+ userid + ";");
					
				if (friendsList.next()) {
						jb.beginObject().append("returnCode",1).beginArray();
					do {
						jb.beginObject().append("friendid", friendsList.getInt("friendid"))
										.append("firstname", friendsList.getString("firstname"))
										.append("surname", friendsList.getString("surname"))
						  .endObject();
					} while(friendsList.next());
					jb.endArray().endObject();
					writer.println(jb.build());
				} else {
						writer.println(getReturnCode(jb,4));
				} 
			} else if (operation.equals("viewFriendsOwe")) {
				Statement friendsGetStmt = conn.createStatement();
				int userid = Integer.parseInt(request.getParameter("userid"));

				
				JSONBuilder jb = new JSONBuilder();
				ResultSet debtSet = friendsGetStmt.executeQuery(
						"SELECT d.userid, d.owesuserid, d.amount, d.partial_pay " +
						"FROM transactions t INNER JOIN debt d on (t.transid=d.transid)"+
						"WHERE (d.owesuserid = " + userid +" OR d.userid = " + userid +")" +
						"AND paid_off=false;");

				if (!debtSet.isBeforeFirst()) {
					writer.println(getReturnCode(jb,20));
					return;
				}
				
				jb.beginObject().append("returnCode",1).beginArray();
				while (debtSet.next()) {
//					String user_firstname = idToNameMap.getFirstname(debtSet.getInt("userid"));
//					String owesuser_firstname = idToNameMap.getFirstname(debtSet.getInt("owesuserid"));
					Double debtAmount = debtSet.getDouble("amount");
					Double partial_pay = debtSet.getDouble("partial_pay");
					
					
//					writer.println(idToNameMap.getFirstname(debtSet.getInt("owesuserid")));
					
					jb.beginObject().append("userid", debtSet.getInt("userid"))
									.append("owesuserid", debtSet.getInt("owesuserid"))
//									.append("user_fname", user_firstname)
//									.append("owesuser_fname", owesuser_firstname)
									.append("amount",debtAmount)
									.append("partial_pay", partial_pay)
					  .endObject();
					}
					jb.endArray().endObject();			
					writer.println(jb.build());
				
			} else if (operation.equals("viewFriendsProfile")){
				Statement friendsProfStmt = conn.createStatement();
				JSONBuilder jb = new JSONBuilder();

				int friendid = Integer.parseInt(request.getParameter("friendid"));		
				ResultSet friendSet 
				  = friendsProfStmt.executeQuery(
						  "SELECT userid as friendid, firstname, surname " +
						  "FROM appuser " +
						  "WHERE userid = "+ friendid +";");

				
				// TODO MUST JOIN WITH THE AMOUNT SOMEHOW FROM A PRVIOUS QUERY
				// OR ANOTHER QUERY
				// currently only gets the user profile details not the amount
				if (!friendSet.isBeforeFirst()) {
					writer.println(getReturnCode(jb,4));
				} 
				
				
				else if (friendSet.next()) {
					jb.beginObject().append("returnCode",1);
					int friend_id = friendSet.getInt("friendid");
					String friend_firstname = friendSet.getString("firstname");
					String friend_surname = friendSet.getString("surname");
					// TODO String friend_amount = MUST CALCULATE SOMEHOW
					
					jb.beginObject().append("friend_id", friend_id)
									.append("friend_firstname", friend_firstname)
									.append("friend_surname",friend_surname)
					// TODO .append("amount",friend_amount)
					  .endObject();
					
					jb.endObject();			
					writer.println(jb.build());
				}	
					
					
			} else if (operation.equals("viewFriendsLog")){
				Statement friendsLogStmt = conn.createStatement();
				int friendid = Integer.parseInt(request.getParameter("friendid"));
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				JSONBuilder jb = new JSONBuilder();
				ResultSet friendsTrans 
				  = friendsLogStmt.executeQuery(
						"SELECT d.transid, t.name, d.userid,d.owesuserid, d.amount, d.partial_pay, t._date " +
						"FROM debt d INNER JOIN transactions t " +
						"ON (t.transid = d.transid) " +
						"WHERE (d.owesuserid = "+ friendid +" AND d.userid =" + userid +") " +
						"OR (d.userid = "+friendid + " AND d.owesuserid = " + userid + ") " +
					    "AND total_paid_off = false" + " UNION " +
					    "SELECT d.transid, t.name, d.userid,d.owesuserid, d.amount, d.partial_pay, t._date " +
						"FROM debt d INNER JOIN transactions t " +
						"ON (t.transid = d.transid) " +
						"WHERE (d.owesuserid = "+ userid +" AND d.userid =" + userid +") " +
						"OR (d.userid = "+ userid + " AND d.owesuserid = " + friendid + ") " +
					    "AND total_paid_off = false;"
						);
				
				if (!friendsTrans.isBeforeFirst()) {
					writer.println(getReturnCode(jb,4));
				} else {
					// Gets a list of the live transactions involving user and
					// friend selected
				
					jb.beginObject().append("returnCode",1).beginArray();
					while (friendsTrans.next()) {
						String transId = friendsTrans.getString("transid");
						String transName = friendsTrans.getString("name");
						String transUserid = friendsTrans.getString("userid");
						String transOwesuserid = friendsTrans.getString("owesuserid");
						String transAmount = friendsTrans.getString("amount");
						String transPartial = friendsTrans.getString("partial_pay");
						String transDate = friendsTrans.getString("_date");
						
						jb.beginObject().append("transid", transId)
										.append("name",transName)
										.append("userid", transUserid)
										.append("owesuserid", transOwesuserid)
										.append("amount",transAmount)
										.append("partial_pay", transPartial)
										.append("date",transDate)
						  .endObject();
					}
						jb.endArray().endObject();			
						writer.println(jb.build());
				}
			} else if (operation.equals("remindFriend")) {
				Statement msgStmt = conn.createStatement();
				Statement transStmt = conn.createStatement();
				Statement lookupStmt = conn.createStatement();
				JSONBuilder jb = new JSONBuilder();
				
				int userid = Integer.parseInt(request.getParameter("userid"));
				int friendid = Integer.parseInt(request.getParameter("friendid"));
//				String friend_firstname = idToNameMap.getFirstname(friendid);
				String user_firstname = request.getParameter("firstname");
				
				int transid = Integer.parseInt(request.getParameter("transid"));
				String date_today = request.getParameter("date");
				String time_now = request.getParameter("time");
				
				
				ResultSet trans = transStmt.executeQuery("SELECT t.name, d.amount, d.partial_pay " +
									 "FROM debt d INNER JOIN transactions t on " +
									 "(t.transid = d.transid) WHERE (d.userid = "+
									 userid + " AND d.owesuserid = " + 
									 friendid + "AND t.transid = " +
									 transid + ";");

				double total = trans.getDouble("amount") - trans.getDouble("partial_pay");
				
				
				String msg = "Yo " + /*friend_firstname + */", " + user_firstname + " needs his money from "+
							 trans.getString("name") + ". This comes to " + total + " pounds. Pay it back soon or "+
							 "I'll come and smash your legs bro.";
						
				
				ResultSet conversation = lookupStmt.executeQuery("SELECT conversationid FROM message " +
						 						"WHERE (user1 = " + userid + " " +
						 						"AND user2 = " + friendid + ") OR (user1 = " + userid + " " +
							 					"AND user2 = " + friendid + ");");
				
				if (!conversation.isBeforeFirst()) {}
					//TODO create conversation here		
				else {
					int conversationid = conversation.getInt("conversationid");
					
					int rs = msgStmt.executeUpdate("INSERT INTO messagecontent VALUES (" +
												msg +", " +
												date_today +", " +
												time_now + ", " +
												conversationid + ", " +
												userid + ";");
				}
			}
		}
												
			    		
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
			
		PrintWriter out = response.getWriter();
		String operation = request.getParameter("op");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			out.println("<h1>Driver not found: " + e + e.getMessage()+ "</h1>");
		}

		try {
			Connection conn = DriverManager
					.getConnection(
							"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
									+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
							"g1227132_u", "W0zFGMaqup");
			handlePostOperation(operation, conn, request, out);

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}

	}

	private void handlePostOperation(String operation, Connection conn,
	HttpServletRequest request, PrintWriter writer) throws Exception {


		if (operation == null) {
			JSONBuilder jb = new JSONBuilder();
			writer.println(getReturnCode(jb,0));
			return;
		} else	if (operation.equals("newTransaction")) {
			
			// Add to transactions table
			Statement transStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
			
			
			int user_id = Integer.parseInt(request.getParameter("userid"));
			String trans_name = request.getParameter("name");
			String trans_desc = request.getParameter("desc");
			String trans_date = request.getParameter("date");
			double trans_amount = Double.parseDouble(request.getParameter("total_amount"));
			int trans_urgency = Integer.parseInt(request.getParameter("urgency"));
			//Should be default 0 unless taken from updateTransaction;
			double trans_partial_pay = 0;
			if (request.getParameter("partial_pay") != null) {
				trans_partial_pay = Double.parseDouble(request.getParameter("partial_pay"));
			}
			// Client Side counter please to count how many people owe in this transaction
			int user_owes_count = Integer.parseInt(request.getParameter("user_owes_count"));	
			
			//Store all the userids into one string and split it to get all the things to add
			//format is like ("1:8,2:10,3:2") in the form (userid:amount-partial_pay)
			//another parameter if it is update in the form ("1-0,2-0,3-0") (userid-partial_pay)
			String owerIdAmount = request.getParameter("oweridIdAmount");			
			String[] owerList = owerIdAmount.split(",");
			String owerIdPartialPairs = request.getParameter("owerIdPartialPairs");
			String[] owerPartialPay = owerIdPartialPairs.split(",");

			//Need to return the new transid to use
			ResultSet new_trans = transStmt.executeQuery("INSERT INTO transactions(name, description, _date, total_amount, urgency) values ('"
							+ trans_name + "', '"
							+ trans_desc + "', '"
							+ trans_date + "', "
							+ trans_amount + ", "
							+ trans_urgency + ") RETURNING transid;");

			if (!new_trans.next()) { 
				writer.print(getReturnCode(jb,30));
			} else {

				// The transaction id of just added transaction above
				int trans_id = new_trans.getInt("transid");
				
				transStmt.close();

				Statement individualStmt = conn.createStatement();

				for (int i = 0; i < user_owes_count; i++) {
					String[] userPair = owerList[i].split(":");
					String[] owerPartialList = owerPartialPay[i].split("-");

					int rs = individualStmt.executeUpdate("INSERT INTO debt(transid, userid, owesuserid, amount, partial_pay) VALUES ( "
														+ trans_id + ", " 
														+ user_id + ", " 
														+ userPair[0] + ", "
														+ userPair[1] + ", "
														+ owerPartialList[1] + ");");
					if (rs != 0) //SUCCESSFUL ADD	
						jb.beginObject().append("returnCode",1).endObject(); 
					else {		// DATABASE INSERT WENT WRONG NOTHING INSERTED
						jb = new JSONBuilder();
						jb.beginObject().append("returnCode",2).endObject();
						break;
					}
				}
				writer.println(jb.build());
			}
		
			
		} else if (operation.equals("updateTransaction")) {
			//TODO CHECK, JO THINKS IS WRONG
			// WHEN THE USER WANTS TO EDIT A TRANSACTION, E.G, A MISTAKE			
		
			Statement getStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
			String preservedAmounts = "";
			
			int old_trans_id = Integer.parseInt(request.getParameter("transid"));
			String old_date = request.getParameter("_date");
			
			ResultSet records = getStmt.executeQuery("SELECT t.transid, d.owesuserid, d.partial_pay "
								+ "FROM transactions t INNER JOIN debt d on (t.transid = d.transid) WHERE " 
								+ "transid = "
								+ old_trans_id + ";");
			
			if (!records.isBeforeFirst())
				jb.beginObject().append("returnCode", 44).endObject();
			else {
				while (records.next()){
					int owesuserid = records.getInt("owesuserid");
					double partial_pay = records.getDouble("partial_pay");
					preservedAmounts += owesuserid + "-" + Double.toString(partial_pay);
					//(userid:amount-partial_pay)
				}
			}
			
			ModifiedServletRequestTrans msr = new ModifiedServletRequestTrans(request,-1,old_date,preservedAmounts);
			//I'm assuming that we post all the relevant fields necessary for the newTransaction in the request
			// to this operation too
			handlePostOperation("newTransaction", conn, msr, writer);
	
			
		} else if (operation.equals("partialRepay")){
			Statement updateStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();

			// NEED TO ALSO HAVE THE CURRENT DATE IN THIS REQUEST
			int transid = Integer.parseInt(request.getParameter("transid"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			int owesuserid = Integer.parseInt(request.getParameter("owesuserid"));
			//THIS DOUBLE NEEDS TO BE WORKED OUT CLIENT SIDE
			double new_partial_pay = Double.parseDouble(request.getParameter("new_partial_pay"));			

	
			ResultSet rs = updateStmt.executeQuery("UPDATE debt SET partial_pay = "
											+ new_partial_pay 
											+ " WHERE transid = " 
											+ transid 
											+ " AND userid = "
											+ userid
											+ " AND owesuserid = "
											+ owesuserid 
											+ " Returning partial_pay, amount; ");
									
			if (rs.next()) {  	//Update was successful
				writer.println(getReturnCode(jb,23));
				
				double partial_pay = rs.getDouble("partial_pay");
				double amount = rs.getDouble("amount");
				double epsilon = 0.0001;
				double total = amount-partial_pay;
				//If this payment completes the debt we can delegate to the debtRepaid operation
				if (Math.abs(total) <= epsilon)
					handlePostOperation("debtRepaid", conn, request, writer); 
			}
			else			//Update went wrong
				writer.println(getReturnCode(jb,24));

			
		} else if (operation.equals("personRepay")){
			// This is for when in per person view, the whole thing is paid
			Statement updateStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
			
			//NEED THE DATE TO BE PASSED IN HERE TOO FOR THE modifiedservletrequest
			int owesuserid = Integer.parseInt(request.getParameter("owesuserid"));
			int userid = Integer.parseInt(request.getParameter("userid"));

			ResultSet allTransactions 
				= updateStmt.executeQuery(
						"SELECT transid " +
						"FROM debt " +
						"WHERE userid = "+ userid + " " +
						"AND owesuserid = "+ owesuserid + " UNION " +
						"SELECT transid " +
						"FROM debt " +
						"WHERE userid = "+ owesuserid + " " +
						"AND owesuserid = "+ userid + ";");  
			
			if (allTransactions.next()){ // The select statement returned correctly
				do {
					int current_transid = allTransactions.getInt("transid");
					ModifiedServletRequestTrans modifiedReq = 
							new ModifiedServletRequestTrans(request,current_transid,null,null);	
					handlePostOperation("debtRepaid", conn, modifiedReq, writer);
					
				} while (allTransactions.next());
			} else { // Something went wrong, there should be a debt alive
				writer.println(getReturnCode(jb,69));			
			}
		} else if (operation.equals("debtRepaid")) { //
			// WHEN A PERSON PAYS THEIR PART OF A TRANSACTION
			Statement updateStmt = conn.createStatement();
			Statement checkStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
		
			int transid = Integer.parseInt(request.getParameter("transid"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			int owesuserid = Integer.parseInt(request.getParameter("owesuserid"));
			String date = request.getParameter("date");
		
			int rs = updateStmt.executeUpdate(
					"UPDATE debt " +
					"SET paid_off = true,complete_date = '"+ date+ "' " +
					" WHERE (transid = " + transid + 
					" AND userid = " + userid +
					" AND owesuserid = " + owesuserid + ") OR (transid = " + transid + 
					" AND userid = " + owesuserid +
					" AND owesuserid = " + userid + ");"); 
		
			if (rs != 0) // update correctly, debt marked as paid
				writer.print(getReturnCode(jb,5));
			else  // update went wrong, nothing was changed
				writer.print(getReturnCode(jb,6));
		
			//This is if the last debt has been repaid of a transaction and we can ALSO complete the transaction
			ResultSet results = checkStmt
					.executeQuery("SELECT * FROM debt WHERE transid = "
							+ transid + " AND paid_off = false;");
		
			if (!results.isBeforeFirst()) { // NO RESULTS SO ALL DEBTS HAVE BEEN PAID
									// transaction completion
				rs = updateStmt.executeUpdate("UPDATE transactions SET total_paid_off = true, complete_date = '" + date + "' WHERE transid = "
								+ transid + ";");
				
				if (rs == 0)	// Update didn't go through
					writer.print(getReturnCode(jb,7));
		
				// else do nothing
			}
		
			
		} else if (operation.equals("deleteTransaction")) {
			// WHEN THE USER WANTS TO DELETE A TRANSACTION
			Statement dltStmt = conn.createStatement();
			Statement dltDebtsStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
			
			int transid = Integer.parseInt(request.getParameter("transid"));
		
			int result = dltStmt.executeUpdate("DELETE FROM transactions WHERE transid = " + transid + ";");
			int deletedDebts = dltStmt.executeUpdate("DELETE FROM debt WHERE transid = " + transid + ";");
			
			if (result != 0 && deletedDebts != 0)  // Delete worked
				writer.print(getReturnCode(jb,8));
			else		 // The DELETE statement didnt execute correctly
				writer.print(getReturnCode(jb,9));
		
			dltStmt.close();
		
		} else {
			JSONBuilder jb = new JSONBuilder();
			writer.print(getReturnCode(jb,10));
			// COULD NOT RECOGNISE OPERATION
		
		}
		
	}
	
	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}
}








			/*
			if (operation.equals("viewFriendsPay")) {
				Statement friendsGetStmt = conn.createStatement();
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				JSONBuilder jb = new JSONBuilder();
				ResultSet debtSet = friendsGetStmt.executeQuery("SELECT b.firstname as payfname, m.amount FROM"+
				" ((appuser a INNER JOIN debt d on (a.userid = d.userid)) as m"+
				" INNER JOIN appuser b on (b.userid = m.owesuserid))" + 
				" INNER JOIN transactions t on (m.transid = t.transid) WHERE m.owesuserid = " + userid +" ;");
				
				jb.beginObject().append("returnCode",1).beginArray();
				while (debtSet.next()) {
					String payuser_firstname = debtSet.getString("payfname");
					String debtAmount = debtSet.getString("amount");
					
					jb.beginObject().append("payuser_firstname", payuser_firstname)
									.append("amount",debtAmount)
					  .endObject();
					}
					jb.endArray().endObject();			
					writer.println(jb.build());
				
			} else if (operation.equals("viewFriendsGet")){
				Statement friendsGetStmt = conn.createStatement();
				
				String userid = Integer.parseInt(request.getParameter("userid");
				writer.println(userid);
				JSONBuilder jb = new JSONBuilder();
				ResultSet debtSet = friendsGetStmt.executeQuery("SELECT m.firstname as owesfname, m.amount FROM"+
				" ((appuser a INNER JOIN debt d on (a.userid = d.userid)) as m"+
				" INNER JOIN appuser b on (b.userid = m.owesuserid))" + 
				" INNER JOIN transactions t on (m.transid = t.transid) WHERE m.userid = " + userid +" ;");
				
				jb.beginObject().append("returnCode",1).beginArray();
				while (debtSet.next()) {
					String owesuser_firstname = debtSet.getString("owesfname");
					String debtAmount = debtSet.getString("amount");
					
					jb.beginObject().append("owesuser_firstname", owesuser_firstname)
									.append("amount",debtAmount)
					  .endObject();
					}
					jb.endArray().endObject();			
					writer.println(jb.build());*/
