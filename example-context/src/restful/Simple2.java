package restful;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Simple2 extends HttpServlet {
    int accesses = 0;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
    {
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();
	
	//User user = new User();

	accesses++;
	out.print("Number of times this servlet has been accessed:" + accesses + " asdf "+accesses);
    }
}
