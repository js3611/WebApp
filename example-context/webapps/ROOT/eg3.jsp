<%!
  public static int n = 0;
%>
<h1>Example With State</h1>
<%
  out.println("<p><b>I remember, n="+n+"</b>");
  n++;
%>