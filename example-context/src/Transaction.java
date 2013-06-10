// Servlet_Postgres.java - example connection to Postgres
import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class Transaction extends HTTPservlet {


public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
{
	PrintWriter out = response.getWriter();
	Stringoperation = request.getParameter("op")

	if (operation == null) {
		outprintln("br>no operation specified</br>");
		return;
	}

	try {
	    Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
	    out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>" );
	}		

	try {
	    Connection conn = DriverManager.getConnection ("jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
							   "g1227132_u", "W0zFGMaqup");	
	    handleGetOperation(operation, conn, request, out);

	} catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }
}


private void handleGetOperation(String operation, Connection conn, 
				HttpServletRequest request, PrintWriter out) throws Exception {

	if (operation.equals("viewLiveTransactions"){
		Statement transStmt = conn.createStatement();
		int userid = request.getParameter("userid");		

		ResultSet transactionsSet = ("SELECT (t.transid, t.desciption, t._date, t.name, t.urgency t.total_amount, d.userid, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = " + userid + " OR d.owesuserid = " + userid +") AND t.total_paid_off = false GROUP BY t.transid, t.desciption, t._date, t.name, t.urgency, t.total_amount, d.userid, d.owesuserid ORDER BY t._date DESC");		
		
		if (!transactionSet.next()) {
			out.print("4"); //NO CURRENT TRANSACTIONS

		while (transactionSet.next()) {
			// TODO: So we got the transactions, now need to display them
	


	} else if (operation.equals("viewDeadTransactions"){
		Statement transStmt = conn.createStatement();
		int userid = request.getParameter("userid");		

		ResultSet transactionsSet = ("SELECT (t.transid, t.desciption, t._date, t.name, t.urgency t.total_amount, d.userid, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = " + userid + " OR d.owesuserid = " + userid +") AND t.total_paid_off = true GROUP BY t.transid, t.desciption, t._date, t.name, t.urgency, t.total_amount, d.userid, d.owesuserid ORDER BY t._date DESC");		
	
	} else if (operation.equals("transactionDetails"){
		Statement detailsStmt = conn.createStatement();
		
		int transid = request.getparameter("transid");
		


	}



}











public void doPost(HttpServletRequest request,
	  	      HttpServletResponse response)
	throws IOException, ServletException
{
	PrintWriter out = response.getWriter();
	Stringoperation = request.getParameter("op")

	if (operation == null) {
		outprintln("br>no operation specified</br>");
		return;
	}

	try {
	    Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
	    out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>" );
	}		

	try {
	    Connection conn = DriverManager.getConnection ("jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
							   "g1227132_u", "W0zFGMaqup");	
	    handlePostOperation(operation, conn, request, out);

	} catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }

}


private void handlePostOperation(String operation, Connection conn, 
				HttpServletRequest request, PrintWriter out) throws Exception {

// ADDS A NEW TRANSACTION TO THE DATABASE.
	if (operation.equals("newTransaction")) {
	//Add to transactions table
		Statement transStmt = conn.createStatment();
	
		int user_id = request.getparameter("userid");
		String trans_name = request.getParameter("name");
		String trans_desc = request.getParameter("desc");
		trans_date = request.getParameter("date");
		BigDecimal trans_amount = request.getParameter("total_amount");
		int trans_urgency = request.getParameter("urgency");	

	//Should return only 1 value
		ResultSet new_trans = transStmt.executeQuery("INSERT INTO transactions(name, description, _date, total_amount, urgency) values ('" + trans_name + "', '" + trans_desc + "', '" + trans_date +"', '£" + trans_amount + "', " + trans_urgency + ") RETURNING transid;");
	// The transaction id of just added transaction above
		int trans_id = new_trans.getInt("transid");
		transStmt.close();

	//Add to debt table
		List<Pair<String,BigDecimal>> trans_owersList = request.getParameter("owersList");
	//Iterator owersIterator = trans_owersList.iterator();

		Statement insertStmt = conn.createStatement();
		Statement queryStmt = conn.createStatement();
			 

		for (Pair<String, BigDecimal> ower : trans_owersList) {
			String ower_phone = ower.first;
			BigDecimal ower_amount = ower.second;
			ResultSet rs = queryStmt.executeQuery("SELECT * FROM appuser WHERE phonenumber = '" + ower_phone +"';");
			int owers_id = rs.getString("userid"); 		
			int result = insertStmt.executeUpdate("INSERT INTO debt(transid, userid, owesuserid, amount) values(" + trans_id + ", " + user_id + ", " + owers_id + ", " + "'£"+ ower_amount + "');");  
		}
		queryStmt.close();
		insertStmt.close();

		if (result == 1)
			out.print("1");	 // SUCCESSFUL ADD
		else
			out.print("2");   // DATABASE INSERT WENT WRONG



// WHEN THE USER WANTS TO EDIT A TRANSACTION, E.G, A MISTAKE
}  else if (operation.equals("updateTransaction")) {


// WHEN A PART OF THE DEBT HAS BEEN PAID BACK
}  else if (operation.equals("debtRepaid")) {
	Statement updateStmt = conn.createStatement();
	Statement checkStmt = conn.createStatement();

	int transid = request.getParameter("transid");
	int userid = request.getParameter("userid");
	int owesuserid = request.getparameter("owesuserid");

	int rs = updateStmt.executeUpdate("UPDATE debt SET paid_off = true WHERE transid = " + transid + " AND userid = " + userid + " AND owesuserid = " + owesuserid + ";");

	if (rs != 0)
		out.print("8");
	else
		out.print("9");

	ResultSet results = checkStmt("SELECT * FROM debt WHERE transid = "+transid+";");
	
	if (!results.next()) {	//NO RESULTS SO ALL DEBTS HAVE BEEN PAID
		rs = updateStmt.executeUpdate("UPDATE transactions SET paid_off = true WHERE transid = " + transid +";");
		if (rs == 0)
			out.print("11"); //statement didnt work
	
	 

// WHEN THE USER WANTS TO DELETE A TRANSACTION
}  else if (operation.equals("deleteTransaction")) {
	Statement dltStmt = conn.createStatement();
	Statement queryStmt = conn.createStatemet();	

	int transid = request.getParameter("transid");
	ResultSet rs = queryStmt.executeQuery("SELECT userid FROM transactions WHERE transid = '" + transid + "';");
	
	int userid = request.getParameter("userid");
	int useridcheck = rs.getInt("userid");

	int result = dltStmt.executeUpdate("DELETE FROM transactions WHERE transid = " + transid ";");	
	if (result != 0)
		out.print("10"); //User is the person who is owed money
	else
		out.print("11"); //The DELETE statement didnt execute correctly

	dltStmt.close();
	queryStmt.close();

}  else
		out.print("2");  // COULD NOT RECOGNISE OPERATION

}






