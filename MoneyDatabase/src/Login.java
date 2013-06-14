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

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
/*		JSONBuilder jb = new JSONBuilder();
		int i = 0;
		jb.beginObject().append("returnCode",1).beginArray();
		while(i < 3) {	
			int amount = 30;
			jb.beginObject().append("userfname", "jo")
							.append("owesfname","terence")
							.append("price",amount)
							.beginArray()
								.appendArrVal("tae").appendArrVal("yeong")
							.endArray()
			  .endObject();
			i++;
		}
		jb.endArray().endObject();
		
		writer.println(jb.build());
*/
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
					String json = getUserDetails(conn, jb, phoneNo,5);
					out.print(json);
				}
				stmt.close();
			}

		} else if (operation.equals("checkPassword")) {
			Statement stmt = conn.createStatement();
			String phoneNo = request.getParameter("phone");
			String password = request.getParameter("password");

			ResultSet rs = stmt
					.executeQuery("SELECT password FROM appuser WHERE phonenumber='"
							+ phoneNo + "'");

			if (!rs.next()) { // User account with the phone Number doens't exist
				out.print(getReturnCode(jb,3));
			} else if (rs.getString("password").equals(password)) { // correct
				String json = getUserDetails(conn, jb, phoneNo,1);
				out.print(json);
			} else { // wrong password
				loginCount++;
				if (loginCount >= 3) // tried too many
					out.print(getReturnCode(jb,4));
				else
					out.print(getReturnCode(jb,2));
			}

		}
	}

	private String getReturnCode(JSONBuilder jb, int ret) {
		return jb.beginObject().append("returnCode",ret).endObject().build();
	}

	private String getUserDetails(Connection conn, JSONBuilder jb,
			String phoneNo,int retCode) throws SQLException {
		Statement userstmt = conn.createStatement();
		// Get user details
		ResultSet userDetails 
		  = userstmt.executeQuery("SELECT * FROM appuser WHERE phonenumber = '" + phoneNo + "';");
		// Get the entry
		userDetails.next();
		String json = jb.beginObject().append("returnCode",retCode)
									  .append("userid",userDetails.getInt("userid"))
									  .append("firstname",userDetails.getString("firstname"))
									  .append("surname",userDetails.getString("surname"))
									  .append("calenderid",userDetails.getInt("calendarid"))
									  .append("wishlist",userDetails.getInt("wishlist"))
									  .append("password",userDetails.getString("password"))
									  .append("phonenumber",userDetails.getString("phonenumber"))
						.endObject().build();
		userstmt.close();
		return json;
	}

}
