// Servlet_Postgres.java - example connection to Postgres
import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class Login extends HttpServlet {

    //A method to check existence 
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
	out.println("<title> Login </title>");
        out.println("</head>");
	out.println("<body>");
	out.println("<p>doGet Invoked</p>");
        out.println("</body>");
        out.println("</html>");
    }

    public void doPost(HttpServletRequest request,
		       HttpServletResponse response)
	throws IOException, ServletException
    {
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	String operation = request.getParameter("op");

	if (operation == null) {
	    out.println("<br>no operation specified</br>");
	    return;
	}	

	try {
	    Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
	    out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>" );
	}

	try {
	    Connection conn = DriverManager.getConnection (
							   "jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
							   "g1227132_u", "W0zFGMaqup" );
	    Statement stmt = conn.createStatement();

	    if (operation.equals("checkPassword")) {

		    String phoneNo = request.getParameter("phone");
		    String password = request.getParameter("password");

		    ResultSet rs = stmt.executeQuery("SELECT password FROM appuser WHERE phonenumber='"+phoneNo+"'");

		    rs.next();
		    if (rs.getString("password").equals(password)) {
			out.println( "valid" );
		    } else {
			out.println( "invalid" );
		    }
		}
	} catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }
    }
}
