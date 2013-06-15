package IdMap;

public class IDtoNameMap {

	private final Map<Integer,Pair<String,String>> IDtoNameMap = null;
	//How can we possibly put so much strain on the server, especially if this reaches a couple thousand users?
	//changing the map to be made just for the users' friends
	
	public IDtoNameMap(HttpServletResponse response, Connection conn, int userid) {

		try{
			PrintWriter writer = response.getWriter();					
			this.IDtoNameMap = new HashMap<Integer,Pair<String,String>>();
			
			Statement IdToUser = conn.createStatement();
			ResultSet rs = IdToUser.executeQuery("SELECT userid, firstname, surname FROM " +
									"(SELECT userid, friendid FROM friends WHERE " +
									" (userid = " +userid+ " OR friendid = "+userid+")) WHERE ;");
			
			if(!rs.isBeforeFirst()) {
				writer.println("Oh sheet");
			}
	
			while(rs.next()) {
				Pair<String, String> name = new Pair<String,String>();
				name.setFirst(rs.getString("firstname"));
				name.setSecond(rs.getString("surname"));
				IDtoNameMap.put(rs.getInt("userid"),name);
			}	
		}
		catch (SQLException e) {
			//abandon all hope 
		}
		catch (IOException e ){
			//still gg slash ff
		}

	}
	
	public Map<Integer,Pair<String,String>> getMap() {
		return this.IDtoNameMap;
	}
	
	public String getFirstname(int userid) {
		return IDtoNameMap.get(userid).getFirst();
	}
	
	public String getSurname(int userid) {
		return IDtoNameMap.get(userid).get.Second();
	}
	
}


   // NEVER EVER DELETE THIS TILL THE END, RETURNS MAP OF FRIENDS FOR A SPECIFIC USER ID		
			/*
			SELECT m.userid, m.firstname as user_firstname, m.surname as user_surname, m.friendid , n.firstname as friend_firstname, n.surname as friend_surname FROM appuser n inner join (SELECT b.userid, a.firstname, a.surname, b.friendid FROM appuser a inner join (SELECT * FROM friends where (userid = 3 or friendid = 3)) as b on (a.userid = b.userid)) as m on (n.userid = m.friendid);
*/	
