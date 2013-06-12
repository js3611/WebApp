import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import JSONBuilder.JSONBuilder;

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

		// TODO FIND A WAY OF GETTING THE VIEWMODE FROM THE DEVICE
		
		if (operation == null) {
			writer.println("br>no operation specified</br>");
			return;
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			writer
					.println("<h1>Driver not found: " + e + e.getMessage()
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
		
		if (viewMode.equals("perPerson")) {
		
		if (operation.equals("viewLiveTransactions")) {
			Statement transStmt = conn.createStatement();
			JSONBuilder jb = new JSONBuilder();
			
			int userid = Integer.parseInt(request.getParameter("userid"));
			
			// Shows all transactions that have yet to be completed
			ResultSet transactionSet = transStmt
					.executeQuery("SELECT (t.transid, t.name, t._date, t.total_amount, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = "
							+ userid
							+ " OR d.owesuserid = "
							+ userid
							+ ") AND t.total_paid_off = false GROUP BY t.transid, t.name, t._date, t.total_amount, d.owesuserid ORDER BY t._date DESC");

			if (!transactionSet.next()) {
				writer.println(jb.beginObject().append("returnCode",4).endObject().build());
			}
			else {
				jb.beginObject().append("returnCode",1).beginArray();
				while (transactionSet.next()) {
					String transId = transactionSet.getString("transid");
					String transName = transactionSet.getString("name");
					String transDate = transactionSet.getString("_date");
					String transAmount = transactionSet.getString("total_amount");
					
					jb.beginObject().append("transid", transId)
									.append("name",transName)
									.append("total_amount",transAmount)
									.append("_date",transDate)
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
					.executeQuery("SELECT (t.transid, t.name, t.complete_date t.total_amount, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = "
							+ userid
							+ " OR d.owesuserid = "
							+ userid
							+ ") AND t.total_paid_off = true GROUP BY t.transid, t.name, t.complete_date, t.total_amount, d.owesuserid ORDER BY t._date DESC");

			if (!transactionSet.next()) {
				writer.println(jb.beginObject().append("returnCode",4).endObject().build());
			}
			else {
				jb.beginObject().append("returnCode",1).beginArray();
				while (transactionSet.next()) {
					String transId = transactionSet.getString("transid");
					String transName = transactionSet.getString("name");
					String transCompleteDate = transactionSet.getString("complete_date");
					String transAmount = transactionSet.getString("total_amount");
					
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
				
				int transid = Integer.parseInt(request.getParameter("transid"));
				// Gets the transaction details
				ResultSet detailsSet = detailsStmt
						.executeQuery("SELECT * FROM transactions WHERE transid = "
								+ transid + " ;");				
				
				// Gets the individual debts of each transaction (should have 1
				// result if user owes, multiple if user is owed)
				ResultSet debtSet = debtsStmt
						.executeQuery("SELECT (d.transid, d.amount, a.userid, a.firstname, a.surname, amount) FROM" 
								+ "  debt d INNER JOIN appuser a ON (d.owesuserid = a.userid OR d.userid = a.userid) AND d.transid = " 
								+ transid +" ;" );

				if (!detailsSet.next() || debtSet.next()) { // SELECTs returned
															// nothing , i.e.
															// something went
															// wrong
					writer.println(jbDebt.beginObject().append("returnCode",4).endObject().build());
				} 
					
				// Print the details of the transaction first then the people
				// involved in it after
				String details_transid = detailsSet.getString("transid");
				String details_name = detailsSet.getString("name");
				String details_amount = detailsSet.getString("total_amount");
				String details_date = detailsSet.getString("_date");
				String details_urgency = detailsSet.getString("urgency");
				String details_complete_date = detailsSet.getString("complete_date");
				String details_description = detailsSet.getString("description");
				String details_paid_off = detailsSet.getString("total_paid_off");
				
				jbDetails.beginObject().append("transid", details_transid)
									   .append("name",details_name)
									   .append("total_amount",details_amount)
									   .append("date",details_date)
									   .append("urgency",details_urgency)
									   .append("description",details_description)
									   .append("complete_date",details_complete_date)
									   .append("total_paid_off",details_paid_off)
						.endObject();
				writer.println(jbDetails.build());
				
				while (debtSet.next()) {
					String transId = debtSet.getString("transid");
					String amount = debtSet.getString("amount");
					String friendid = debtSet.getString("userid");
					String friendfname = debtSet.getString("firstname");					
					
					jbDebt.beginObject().append("transid", transId)
										.append("userid",friendid)
										.append("firstname",friendfname)
										.append("amount",amount)
										.endObject();
					}
					jbDebt.endArray().endObject();			
					writer.println(jbDebt.build());
			}
		} else if (viewMode.equals("perItem")) {
			if (operation.equals("viewFriendsPay")) {
				Statement friendsGetStmt = conn.createStatement();
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				// TODO CHECK ALL QUERIES AGAINST DATABASE JUST IN CASE
				
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
				int userid = Integer.parseInt(request.getParameter("userid"));
				
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
					writer.println(jb.build());
				
			} else if (operation.equals("viewFriendsProfile")){
				Statement friendsProfStmt = conn.createStatement();
				int friendid = Integer.parseInt(request.getParameter("friendId"));
				
				JSONBuilder jb = new JSONBuilder();
				ResultSet friendSet = friendsProfStmt.executeQuery("SELECT userid as friendId, firstname"+
									   " surname FROM appuser WHERE userid = "+ friendid +";");
				// TODO MUST JOIN WITH THE AMOUNT SOMEHOW FROM A PRVIOUS QUERY
				// OR ANOTHER QUERY
				// currently only gets the user profile details not the amount
				
				if (friendSet.next()) {
					jb.beginObject().append("returnCode",1);
					String friend_id = friendSet.getString("friendid");
					String friend_firstname = friendSet.getString("firstname");
					String friend_surname = friendSet.getString("surname");
					// TODO String friend_amount = MUST CALCULATE SOMEHOW
					
					jb.beginObject().append("friend_id", friend_id)
									.append("friend_firstname", friend_firstname)
									.append("friend_surname",friend_surname)
				// TODO .append("amount",friend_amount)
					  .endObject();
					}
					jb.endObject();			
					writer.println(jb.build());
					
			} else if (operation.equals("viewFriendsLog")){
				Statement friendsLogStmt = conn.createStatement();
				int friendid = Integer.parseInt(request.getParameter("friendid"));
				int userid = Integer.parseInt(request.getParameter("userid"));
				
				JSONBuilder jb = new JSONBuilder();
				ResultSet friendsTrans = friendsLogStmt.executeQuery("SELECT (d.transid, t.name d.userid,d.owesuserid " +
						"d.amount, d.partial_pay, t._date) from debt d INNER JOIN transactions t ON (t.transid =" +
						" d.transid) WHERE d.oweruserid = "+ friendid +" OR (d.userid = "+friendid + " AND d.owesuserid = " + userid + ") AND total_paid_off = false;");
				
				if (!friendsTrans.next()) {
					writer.println(jb.beginObject().append("returnCode",4).endObject().build());
				}
				// Gets a list of the live transactions involving user and
				// friend selected
				else {
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
			}			
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		String operation = request.getParameter("op");

		if (operation == null) {
			out.println("br>no operation specified</br>");
			return;
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			out
					.println("<h1>Driver not found: " + e + e.getMessage()
							+ "</h1>");
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

// ADDS A NEW TRANSACTION TO THE DATABASE.
if (operation.equals("newTransaction")) {
	// Add to transactions table
	Statement transStmt = conn.createStatement();
	JSONBuilder jb = new JSONBuilder();
	
	int user_id = Integer.parseInt(request.getParameter("userid"));
	String trans_name = request.getParameter("name");
	String trans_desc = request.getParameter("desc");
	int trans_date = Integer.parseInt(request.getParameter("date"));
	BigDecimal trans_amount = new BigDecimal(request.getParameter(
			"total_amount").replaceAll(",", ""));
	int trans_urgency = Integer.parseInt(request
			.getParameter("urgency"));

	// Should return only 1 value
	ResultSet new_trans = transStmt
			.executeQuery("INSERT INTO transactions(name, description, _date, total_amount, urgency) values ('"
					+ trans_name
					+ "', '"
					+ trans_desc
					+ "', '"
					+ trans_date
					+ "', "
					+ trans_amount
					+ ", "
					+ trans_urgency + ") RETURNING transid;");
	// The transaction id of just added transaction above
	int trans_id = new_trans.getInt("transid");
	transStmt.close();

	// use string.split and have one parameter, usersids etc.
	// Add to debt table
	List<Pair<String, BigDecimal>> trans_owersList = null;// request.getParameter("owersList");
	// Iterator owersIterator = trans_owersList.iterator();

	Statement insertStmt = conn.createStatement();
	Statement queryStmt = conn.createStatement();

	for (Pair<String, BigDecimal> ower : trans_owersList) {
		String ower_phone = ower.first;
		BigDecimal ower_amount = ower.second;
		ResultSet rs = queryStmt
				.executeQuery("SELECT * FROM appuser WHERE phonenumber = '"
						+ ower_phone + "';");
		int owers_id = rs.getInt("userid");
		int result = insertStmt
				.executeUpdate("INSERT INTO debt(transid, userid, owesuserid, amount) values("
						+ trans_id + ", " + user_id + ", " + owers_id
						+ ", " + "'£" + ower_amount + "');");

		if (result != 0) // SUCCESSFUL ADD
			writer.print(jb.beginObject().append("returnCode",1).endObject()); 
		else		// DATABASE INSERT WENT WRONG NOTHING INSERTED
			writer.print(jb.beginObject().append("returnCode",2).endObject()); 
	}
	queryStmt.close();
	insertStmt.close();

	// WHEN THE USER WANTS TO EDIT A TRANSACTION, E.G, A MISTAKE
} else if (operation.equals("updateTransaction")) {
	Statement updateTransStmt = conn.createStatement();
	Statement updateDebtStmt = conn.createStatement();
	Statement countStmt = conn.createStatement();
	JSONBuilder jb = new JSONBuilder();
	
	// subOp is a pair which tells if this update is "normal" (just changing
	// transaction details)
	// or "delete"(removes a debtor) or "adds"(adds a debtor)
	String subOp = request.getParameter("supOp");
	
	int transid = Integer.parseInt(request.getParameter("transid"));
	String name = request.getParameter("name");
	String description = request.getParameter("description");
	int urgency = Integer.parseInt(request.getParameter("urgency"));
	int total_amount = Integer.parseInt(request.getParameter("total_amount"));
	
	ResultSet rs = countStmt.executeQuery("SELECT count(userid) FROM transactions t INNER JOIN "
							+" debt d ON (t.transid = d.transid) WHERE t.transid = " + transid + ";");
	if (!rs.next()) // select statement error, something went wrong
		writer.println(jb.beginObject().append("returnCode",3).endObject());
	else {
	// int
	}
	// CANNOT CONCENTRATE
	
} else if (operation.equals("partialRepay")){
	
} else if (operation.equals("personRepay")){
	// This is for when in per person view, the whole thing is paid
	// Statement updateStmt = conn.createStatement();
	// JSONBuilder jb = new JSONBuilder();
	
	// int rs = updateStmt.executeUpdate("Update")
	

} else if (operation.equals("debtRepaid")) {
	// WHEN A PERSON PAYS THEIR PART OF A TRANSACTION
	Statement updateStmt = conn.createStatement();
	Statement checkStmt = conn.createStatement();
	JSONBuilder jb = new JSONBuilder();

	int transid = Integer.parseInt(request.getParameter("transid"));
	int userid = Integer.parseInt(request.getParameter("userid"));
	int owesuserid = Integer.parseInt(request.getParameter("owesuserid"));
	String date = request.getParameter("date");

	int rs = updateStmt.executeUpdate("UPDATE debt SET paid_off = true,complete_date = "+ date+ " WHERE transid = "
					+ transid + " AND userid = " + userid
					+ " AND owesuserid = " + owesuserid + ";");

	if (rs != 0) // update correctly, debt marked as paid
		writer.print(jb.beginObject().append("returnCode",5).endObject());
	else  // update went wrong, nothing was changed
		writer.print(jb.beginObject().append("returnCode",6).endObject());

	ResultSet results = checkStmt
			.executeQuery("SELECT * FROM debt WHERE transid = "
					+ transid + ";");

	if (!results.next()) { // NO RESULTS SO ALL DEBTS HAVE BEEN PAID
							// transaction completion
		rs = updateStmt.executeUpdate("UPDATE transactions SET total_paid_off = true, complete_date = " + date + " WHERE transid = "
						+ transid + ";");
		
		if (rs == 0)	// Update didn't go through
			writer.print(jb.beginObject().append("returnCode",7).endObject());

		// else do nothing
	}

	
} else if (operation.equals("deleteTransaction")) {
	// WHEN THE USER WANTS TO DELETE A TRANSACTION
	// TODO check that the delete button only appears if transaction is owned by
	// user
	Statement dltStmt = conn.createStatement();
	JSONBuilder jb = new JSONBuilder();
	
	int transid = Integer.parseInt(request.getParameter("transid"));

	int result = dltStmt.executeUpdate("DELETE FROM transactions WHERE transid = " + transid + ";");
	
	if (result != 0)  // Delete worked
		writer.print(jb.beginObject().append("returnCode",8).endObject());
	else		 // The DELETE statement didnt execute correctly
		writer.print(jb.beginObject().append("returnCode",9).endObject());

	dltStmt.close();

} else {
	JSONBuilder jb = new JSONBuilder();
	writer.print(jb.beginObject().append("returnCode",10).endObject());; // COULD
																			// NOT
																			// RECOGNISE
																			// OPERATION

}
	
	class Pair<S, T> {

		public BigDecimal second;
		public String first;

	}

}
