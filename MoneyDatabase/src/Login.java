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
public class Login extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;
	
	int loginCount = 0;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

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
			
			handleOperation(request.getParameter("op"), conn, request, out);
			conn.close();

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
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
		// response.setContentType("text/html");
		PrintWriter out = response.getWriter();

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
			
			handleOperation(request.getParameter("op"), conn, request, out);
			conn.close();

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}

	private void handleOperation(String operation, Connection conn,
			HttpServletRequest request, PrintWriter out) throws Exception {

		JSONBuilder jb = new JSONBuilder();
		if (operation == null) {
			out.println(getReturnCode(jb,0));
			return;
		}
		
		if (operation.equals("newAccount")) {
			Statement stmt = conn.createStatement();
			String fname = request.getParameter("firstname");
			String sname = request.getParameter("surname");
			String pw = request.getParameter("password");
			String phoneNo = request.getParameter("phone");

			ResultSet phoneResults = stmt
					.executeQuery("SELECT userid FROM appuser WHERE phonenumber = '"
							+ phoneNo + "';");
			if (phoneResults.next()) // It failed because phone number exists
				out.print(getReturnCode(jb,6));
			else {

				int rs = stmt
						.executeUpdate("INSERT INTO appuser(firstname, surname, password, phonenumber) values ('"
								+ fname
								+ "', '"
								+ sname
								+ "', '"
								+ pw
								+ "', '"
								+ phoneNo + "')");
				
				if (rs == 0) //failed to insert
					out.print(getReturnCode(jb,7));
				else {
					// Successful signup
					jb.beginObject().append("retCode",5);
					jb = getUserDetails(conn, jb, phoneNo);
					jb.endObject();
					out.print(jb.build());
				}
				stmt.close();
			}

		} else if (operation.equals("checkPassword")) {
			Statement stmt = conn.createStatement();
			String phoneNo = request.getParameter("phone");
			String password = request.getParameter("password");

			ResultSet rs = stmt
					.executeQuery("SELECT password, userid FROM appuser WHERE phonenumber='"
							+ phoneNo + "'");

			if (!rs.next()) { // User account with the phone Number doens't exist
				out.print(getReturnCode(jb,3));
			} else if (rs.getString("password").equals(password)) { // correct
				
				jb.beginObject().append("returnCode",1);
				jb = getUserDetails(conn, jb, phoneNo);
				jb = getFriendList(conn, jb, rs.getInt("userid"));
				jb.endObject();
				
				
//				if (idToNameMap == null)
//					idToNameMap = IDtoNameMap.getInstance(conn,rs.getInt("userid"));

				
				out.print(jb.build());
			} else { // wrong password
				loginCount++;
				if (loginCount >= 3) // tried too many
					out.print(getReturnCode(jb,4));
				else
					out.print(getReturnCode(jb,2));
			}

		}
	}

	private JSONBuilder getFriendList(Connection conn, JSONBuilder jb, int userid)
		throws SQLException {
		
		Statement getFriendsStmt = conn.createStatement();

		ResultSet friendsList = getFriendsStmt
				.executeQuery(
						"SELECT f.friendid, a.firstname, a.surname " +
						"FROM friends AS f INNER JOIN appuser AS a ON f.friendid = a.userid " +
						"WHERE f.userid = "+ userid + " UNION " +
						"SELECT f.userid, a.firstname, a.surname " +
						"FROM friends AS f INNER JOIN appuser AS a ON f.userid = a.userid " +
						"WHERE f.friendid = "+ userid + ";");

		if (friendsList.next()) {
			jb.beginArray();
			do {
				jb.beginObject()
				.append("friendid", friendsList.getInt("friendid"))
				.append("firstname", friendsList.getString("firstname"))
				.append("surname", friendsList.getString("surname"))
				.endObject();
			} while (friendsList.next());
			jb.endArray();
		
		} 
		
		return jb;
	}

	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}

	private JSONBuilder getUserDetails(Connection conn, JSONBuilder jb,
			String phoneNo) throws SQLException {
		Statement userstmt = conn.createStatement();
		// Get user details
		ResultSet userDetails 
		  = userstmt.executeQuery("SELECT * FROM appuser WHERE phonenumber = '" + phoneNo + "';");
		// Get the entry
		userDetails.next();
		jb.beginObject();
		jb.append("userid",userDetails.getInt("userid"))
		  .append("firstname",userDetails.getString("firstname"))
		  .append("surname",userDetails.getString("surname"))
		  .append("calenderid",userDetails.getInt("calendarid"))
		  .append("wishlist",userDetails.getInt("wishlist"))
		  .append("password",userDetails.getString("password"))
		  .append("phonenumber",userDetails.getString("phonenumber"));
		jb.endObject();
		userstmt.close();
		return jb;
	}

}
