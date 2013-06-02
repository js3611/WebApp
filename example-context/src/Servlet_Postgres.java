// Servlet_Postgres.java - example connection to Postgres
import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class Servlet_Postgres extends HttpServlet {
 
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
	out.println("<title> Films Example: Servlet, Postgres version</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");

        try {
 	    Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>" );
        }

	String query = "";

	try {

	    Connection conn = DriverManager.getConnection (
							   "jdbc:postgresql://db.doc.ic.ac.uk/g1227132_u",
							   "g1227132_u", "W0zFGMaqup" );
            Statement stmt = conn.createStatement();

	    String select = request.getParameter("select");
	    String from = request.getParameter("from");
	    String where = request.getParameter("where");
	    String group_by = request.getParameter("group_by");
	    String having = request.getParameter("having");
	    String order = request.getParameter("order");
	    query = generateQuery(select,from,where,group_by,having,order);
	    
	    ResultSet rs;
	    
	    if (query == null) {
	    	out.println( "<p>Your query was ill formatted</p>");
		rs = stmt.executeQuery("SELECT * FROM films");
		    
		out.println( "<table>" );
		while ( rs.next() ) {
		    String title = rs.getString("title");
		    String director = rs.getString("director");
		    String origin = rs.getString("origin");
		    String made = rs.getString("made");
		    String length = rs.getString("length");
		    out.println("<tr><td>"+title+"</td><td>"+director+"</td><td>"+origin+"</td><td>"+
				made+"</td><td>"+length+"</td></tr>" );
		}
		out.println( "</table>" );
	    } else {
		rs = stmt.executeQuery(query);
		out.println( "<p>Your query was: "+query+"</p>");
		    
		out.println( "<table>" );
		while ( rs.next() ) {
		    out.print("<tr>");
		    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			out.print("<td>"+rs.getString(i)+"</td>");
		    }
		    out.print("</tr>" );
		}
		out.println( "</table>" );
	    }
	    
	    conn.close();
        } catch (Exception e) {
	    out.println( "<p>Your query was: "+query+"</p>");
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }
        out.println("</body>");
        out.println("</html>");
    }

    public void doPost(HttpServletRequest request,
		       HttpServletResponse response)
	throws IOException, ServletException
    {
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	out.println("<html>");
	out.println("<head>");
	out.println("<title> Films Example: Servlet, Postgres version</title>");
	out.println("</head>");
	out.println("<body bgcolor=\"white\">");

	out.println("<br>doPost Invoked</br>");
	
	try {
	    Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e) {
	    out.println("<h1>Driver not found: " + e + e.getMessage() + "</h1>" );
	}

	out.println("</body>");
	out.println("</html>");

    }

    private String generateQuery(String select, String from, String where,
				 String group_by, String having, String order) {
	    
	String query = "";
	String[] selects = select.split(",");
	    
	if (select==null || from ==null) {
	    return null;
	}
	query += "SELECT " + select + "\n";
	
	query += "FROM " + from + "\n";

	if (where!=null) 
	    query += "WHERE " + where + "\n";
	    
	if (group_by!=null) 
	    query += "GROUP BY " + group_by + "\n";
	    
	if (having!=null) 
	    query += "HAVING " + having + "\n";
	    
	if (order!=null) 
	    query += "ORDER BY " + order + "ASC";
	    
	return query;
    }

}
