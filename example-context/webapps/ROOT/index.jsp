<html>
<body bgcolor="#667799">

<h1>Test JSP page</h1>

<table border="1">
<%
  int row;
  for( row=0; row<20; row++ )
  {
%>
<tr>
<%
     int col;
     for( col=0; col<20; col++ )
     {
     	out.write( "<td>hello</td>" );
     }
%>
</tr>
<% } %>
</table>

</body>
</html>
