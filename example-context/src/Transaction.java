// Servlet_Postgres.java - example connection to Postgres
import java.io.*;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

public class Transaction extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
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
			out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>");
		}

		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
					"g1227132_u", "W0zFGMaqup");
			handleGetOperation(operation, conn, request, out);

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}

	private void handleGetOperation(String operation, Connection conn,
			HttpServletRequest request, PrintWriter out) throws Exception {

		if (operation.equals("viewLiveTransactions")) {
			Statement transStmt = conn.createStatement();
			int userid = Integer.parseInt(request.getParameter("userid"));

			ResultSet transactionSet = transStmt
					.executeQuery("SELECT (t.transid, t.name, t._date, t.total_amount, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = "
							+ userid
							+ " OR d.owesuserid = "
							+ userid
							+ ") AND t.total_paid_off = false GROUP BY t.transid, t.name, t._date, t.total_amount, d.owesuserid ORDER BY t._date DESC");

			if (!transactionSet.next()) {
				out.print(build(append(getBuilder(),"returnCode",4))); // NO CURRENT TRANSACTIONS

				while (transactionSet.next()) {
					// TODO: So we got the transactions, now need to display
					// them
				}
			}
		} else if (operation.equals("viewDeadTransactions")) {
			Statement transStmt = conn.createStatement();
			int userid = Integer.parseInt(request.getParameter("userid"));

			ResultSet transactionSet = transStmt
					.executeQuery("SELECT (t.transid, t._date, t.name, t.total_amount, t.complete_date, d.userid, d.owesuserid) FROM transactions t INNER JOIN debt d on t.transid = d.transid WHERE (t. userid = "
							+ userid
							+ " OR d.owesuserid = "
							+ userid
							+ ") AND t.total_paid_off = true GROUP BY t.transid, t.desciption, t._date, t.name, t.total_amount, t.complete_date, d.userid, d.owesuserid ORDER BY t._date DESC");

			if (!transactionSet.next()) {
				out.print("4"); // NO CURRENT TRANSACTIONS

				while (transactionSet.next()) {
					// TODO: So we got the transactions, now need to display
					// them
				}

			} else if (operation.equals("transactionLiveDetails")) {
				Statement detailsStmt = conn.createStatement();
				Statement debtStmt = conn.createStatement();

				// TODO Currently just gets the transactions in which the user
				// is the one who is owed (not owes)

				int transid = Integer.parseInt(request.getParameter("transid"));
				ResultSet detailsSet = detailsStmt
						.executeQuery("SELECT (transid, name, total_amount, _date, description, total_paid_off) FROM transactions WHERE transid = "
								+ transid + " AND total_paid_off = false;");

				ResultSet rs = debtStmt
						.executeQuery("SELECT (d.transid, d.amount, a.userid, a.firstname, a.surname) FROM debt d INNER JOIN appuser a ON d.owesuserid = appuser.userid");

				if (!rs.next()) 	//SELECT returned nothing, not transactions
					out.print(build(append(getBuilder(),"returnCode",5)));
				while (rs.next())
					out.println(rs.getInt("transid") + ", "
							+ rs.getInt("amount") + ", " + rs.getInt("userid")
							+ ", " + rs.getInt("firstname") + ", "
							+ rs.getString("firstname") + ", "
							+ rs.getString("Surname"));

			} else if (operation.equals("transactionDeadDetails")) {
				Statement detailsStmt = conn.createStatement();

				int transid = Integer.parseInt(request.getParameter("transid"));
				ResultSet detailsSet = detailsStmt
						.executeQuery("SELECT (transid, name, total_amount, _date, description, total_paid_off) FROM transactions WHERE transid = "
								+ transid + " AND total_paid_off = true;");

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
			out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>");
		}

		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
					"g1227132_u", "W0zFGMaqup");
			handlePostOperation(operation, conn, request, out);

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}

	}
    /* TODO operations for doPost
       1.New Transaction 
       2.Update/Edit Transaction
       3.Per person repay
       5.Partial payment
     */


	private void handlePostOperation(String operation, Connection conn,
			HttpServletRequest request, PrintWriter out) throws Exception {

		// ADDS A NEW TRANSACTION TO THE DATABASE.
		if (operation.equals("newTransaction")) {
			// Add to transactions table
			Statement transStmt = conn.createStatement();

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
								+ trans_id
								+ ", "
								+ user_id
								+ ", "
								+ owers_id
								+ ", " + "'£" + ower_amount + "');");

				if (result == 1) // SUCCESSFUL ADD
					out.printbuild(append(getBuilder(),"returnCode",1)));  
				else		// DATABASE INSERT WENT WRONG
					out.printbuild(append(getBuilder(),"returnCode",2)));  
			}
			queryStmt.close();
			insertStmt.close();

			// WHEN THE USER WANTS TO EDIT A TRANSACTION, E.G, A MISTAKE
		} else if (operation.equals("updateTransaction")) {

			// WHEN A PART OF THE DEBT HAS BEEN PAID BACK
		} else if (operation.equals("debtRepaid")) {
			Statement updateStmt = conn.createStatement();
			Statement checkStmt = conn.createStatement();

			int transid = Integer.parseInt(request.getParameter("transid"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			int owesuserid = Integer.parseInt(request
					.getParameter("owesuserid"));

			int rs = updateStmt
					.executeUpdate("UPDATE debt SET paid_off = true WHERE transid = "
							+ transid
							+ " AND userid = "
							+ userid
							+ " AND owesuserid = " + owesuserid + ";");

			if (rs != 0) // update correctly, debt marked as paid
				out.printbuild(append(getBuilder(),"returnCode",8))); 
			else  //update went wrong, nothing was changed
				out.printbuild(append(getBuilder(),"returnCode",9))); 

			ResultSet results = checkStmt
					.executeQuery("SELECT * FROM debt WHERE transid = "
							+ transid + ";");

			if (!results.next()) { // NO RESULTS SO ALL DEBTS HAVE BEEN PAID
				rs = updateStmt
						.executeUpdate("UPDATE transactions SET paid_off = true WHERE transid = "
								+ transid + ";");
				if (rs == 0)	//Update didn't go through
					out.printbuild(append(getBuilder(),"returnCode",12))); 
			}

			// WHEN THE USER WANTS TO DELETE A TRANSACTION
		} else if (operation.equals("deleteTransaction")) {
			Statement dltStmt = conn.createStatement();
			Statement queryStmt = conn.createStatement();

			int transid = Integer.parseInt(request.getParameter("transid"));
			ResultSet rs = queryStmt
					.executeQuery("SELECT userid FROM transactions WHERE transid = '"
							+ transid + "';");

			//TODO client side checks if transaction is users

			int userid = Integer.parseInt(request.getParameter("userid"));
			int useridcheck = rs.getInt("userid");

			int result = dltStmt
					.executeUpdate("DELETE FROM transactions WHERE transid = "
							+ transid + ";");
			if (result != 0)  // Delete worked
				out.print(build(append(getBuilder(),"returnCode", 10)));
			else		 // The DELETE statement didnt execute correctly
				out.printbuild(append(getBuilder(),"returnCode",11))); 

			dltStmt.close();
			queryStmt.close();

		} else
			out.print("3"); // COULD NOT RECOGNISE OPERATION

	}

	class Pair<S, T> {

		public BigDecimal second;
		public String first;

	}

}
