<%@ page language="java" import="java.sql.*"%>
<html>
<head>
<title> Films Example: JSP, Postgres version</title>
</head>
<body bgcolor="white">
<%
        try {
 	    Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            out.println("<h1>Driver not found:" + e + e.getMessage() + "</h1>" );
        }
	try {
	    Connection conn = DriverManager.getConnection (
	    	"jdbc:postgresql://db.doc.ic.ac.uk/films",
		"lab", "lab" );

            Statement stmt = conn.createStatement();
            ResultSet rs;

            rs = stmt.executeQuery("SELECT * FROM films");
	    out.println( "<table border=1>" );
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

            conn.close();
        } catch (Exception e) {
            out.println( "<h1>exception: "+e+e.getMessage()+"</h1>" );
        }
%>
</body>
</html>
