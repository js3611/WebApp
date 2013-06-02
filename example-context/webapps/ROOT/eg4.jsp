<html>
<body>
<%-- Create an instance of the bean --%>
<jsp:useBean id="b" class="dcwbeans.bean1" />

<table> 

<tr>
  <th>b.name is </th>
  <tb><jsp:getProperty name="b" property="name" /> </tb>
</tr>

<tr>
  <th>modifying b.name </th>
  <tb><jsp:setProperty name="b" property="name" value="wobble"/> </tb>
</tr>

<tr>
  <th>b.name is now </th>
  <tb><jsp:getProperty name="b" property="name" /> </tb>
</tr>

<tr>
  <th> <%= b.stars(10) %> </th>
</tr>


<tr>
  <th> b.name is <%= b.getName() %> </th>
</tr>


<tr>
  <th> b.name is <%= b.getName() %> </th>
</tr>

</table>

 <%
  b.setName("h");
 %>


</body>
</html>

