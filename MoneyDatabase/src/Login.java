import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
		
		PrintWriter writer = response.getWriter();
		JSONBuilder jb = new JSONBuilder();
		int i = 0;
		jb.beginObject().append("returnCode",1).beginArray();
		while(i < 3) {	
			int amount = 30;
			jb.beginObject().append("userfname", "joo")
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
	/*	
		StringBuilder strB = getBuilder();
		append(strB, "returnCode", 1);
		append(strB, "status", 2);
		append(strB, "first", "terence");
		append(strB, "last", "tse");
		out.print(build(strB));
*/
		String operation = request.getParameter("op");

		if (operation == null) {
			//out.println("<br>no operation specified</br>");
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

			String uri = "jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
					+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory";

			Connection conn = DriverManager.getConnection(uri, "g1227132_u",
					"W0zFGMaqup");
			Statement stmt = conn.createStatement();

			if (operation.equals("checkPassword")) {

				// Check if userID exists

				String phoneNo = request.getParameter("phone");
				String password = request.getParameter("password");

				ResultSet rs = stmt
						.executeQuery("SELECT password FROM appuser WHERE phonenumber='"
								+ phoneNo + "'");

				out.println("op:" + operation + "phone:" + phoneNo + "pass:"
						+ password);

				rs.next();
				if (rs.getString("password").equals(password)) {
					out.print("valid");
				} else {
					out.print("invalid");
				}
			}
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

		String operation = request.getParameter("op");

		if (operation == null) {
			out.println("<br>no operation specified</br>");
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
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u?&ssl=true"
					+ "&sslfactory=org.postgresql.ssl.NonValidatingFactory",
					"g1227132_u", "W0zFGMaqup");
			Statement stmt = conn.createStatement();
			handleOperation(operation, stmt, request, out);

		} catch (Exception e) {
			out.println("<h1>exception: " + e + e.getMessage() + "</h1>");
		}
	}

	private void handleOperation(String operation, Statement stmt,
			HttpServletRequest request, PrintWriter out) throws Exception {

		if (operation.equals("newAccount")) {
			String fname = request.getParameter("firstname");
			String sname = request.getParameter("surname");
			String pw = request.getParameter("password");
			String phoneNo = request.getParameter("phone");

			ResultSet phoneResults = stmt
					.executeQuery("SELECT userid FROM appuser WHERE phonenumber = '"
							+ phoneNo + "';");
			if (phoneResults.next()) // It failed because phone number exists
				out.print(build(append(getBuilder(), "returnCode", 6)));// out.print("6");
			else {

				int rs = stmt
						.executeUpdate("INSERT INTO appuser(firstname, surname, password, phonenumber) values ('"
								+ fname
								+ "', '"
								+ sname
								+ "', '"
								+ pw
								+ "', '"
								+ phoneNo + "')");// RETURNING userid;");

				if (rs == 0)
					out.print(build(append(getBuilder(), "returnCode", 7)));// out.print("7");
				else
					// Successful signup
					out.print(build(append(getBuilder(), "returnCode", 5)));// out.print("5");

				stmt.close();
			}

		} else if (operation.equals("checkPassword")) {

			String phoneNo = request.getParameter("phone");
			String password = request.getParameter("password");

			ResultSet rs = stmt
					.executeQuery("SELECT password FROM appuser WHERE phonenumber='"
							+ phoneNo + "'");

			if (!rs.next()) { // User account with the phone Number doens't
				// exist
				out.print(build(append(getBuilder(), "returnCode", 3)));// out.print(
				// "3"
				// );
			} else if (rs.getString("password").equals(password)) { // correct
				// password
				out.print(build(append(getBuilder(), "returnCode", 1)));// out.print(
				// "1"
				// );
			} else { // wrong password
				loginCount++;
				if (loginCount >= 3) // tried too many
					out.print(build(append(getBuilder(), "returnCode", 4)));// out.print("4");
				else
					out.print(build(append(getBuilder(), "returnCode", 2)));// out.print(
				// "2"
				// );
			}

		}
	}

	private String DOUBLE_QUOTE = "\"";
	private String COLON = ":";
	private String COMMA = ",";
	private boolean firstEntry = true;

	public StringBuilder getBuilder() {
		StringBuilder str = new StringBuilder("{ ");
		return str;
	}

	public StringBuilder append(StringBuilder str, String key, String value) {
		if (!firstEntry)
			str.append(COMMA);
		firstEntry = false;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return str;
	}

	public StringBuilder append(StringBuilder str, String key, int value) {
		if (!firstEntry) {
			str.append(COMMA);
		}
		firstEntry = false;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return str;
	}

	public StringBuilder append(StringBuilder str, String key, double value) {
		if (!firstEntry) {
			str.append(COMMA);
		}
		firstEntry = false;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return str;
	}

	public StringBuilder append(StringBuilder str, String key, boolean value) {
		if (!firstEntry) {
			str.append(COMMA);
		}
		firstEntry = false;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return str;
	}

	public String build(StringBuilder str) {
		firstEntry = true;
		return str.append("}").toString();
	}

}
