// Servlet_Postgres.java - example connection to Postgres
import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class Transaction extends HTTPservlet


public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
{

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
	    handleOperation(operation, conn, request, out);

	} catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }

}


//WHEN THE USER JUST WANTS TO DELETE THE TRANSACTION FOR WHATEVER REASON
public void doDelete(HttpServletRequest request,
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
	    handleOperation(operation, conn, request, out);

	} catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }

}

private void handleOperation(String operation, Connection conn, 
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
		ResultSet new_trans = transStmt.executeQuery("INSERT INTO transactions(name, description, _date, total_amount, urgency) values ('" + trans_name + "', '" + trans_desc + "', '" + CHANGED DATE SOMEHOW +"', '£" + trans_amount + "', " + trans_urgency + ") RETURNING transid;");
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
			insertStmt.executeUpdate("INSERT INTO debt(transid, userid, owesuserid, amount) values(" + trans_id + ", " + user_id + ", " + owers_id + ", " + "'£"+ ower_amount + "');");  
		}
		queryStmt.close();
		insertStmt.close();

		out.print("1");	 // SUCCESSFUL ADD

// WHEN THE USER WANTS TO EDIT A TRANSACTION, E.G, A MISTAKE
}  else if (operation.equals("updateTransaction")) {


// WHEN A PART OF THE DEBT HAS BEEN PAID BACK
}  else if (operation.equals("debtRepaid")) {

}  else if (operation.equals("deleteTransaction")) {
	Statement dltStmt = conn.createStatement();
	
	int transid = request.getParameter("transid");
	int result = dltStmt.executeUpdate("DELETE FROM transactions WHERE transid = " + transid ";");	
 	

}  else
		out.print("2");  // COULD NOT RECOGNISE OPERATION

}






