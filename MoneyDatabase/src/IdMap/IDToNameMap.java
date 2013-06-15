package IdMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;



import Pair.Pair;


public class IDtoNameMap {

	private static IDtoNameMap theMap;
	private Map<Integer,Pair<String,String>> friendMap = null;
	//How can we possibly put so much strain on the server, especially if this reaches a couple thousand users?
	//changing the map to be made just for the users' friends
	
	
	public static IDtoNameMap getInstance(Connection conn, int userid) throws SQLException {
		if (theMap == null) {
			theMap = new IDtoNameMap(conn, userid);
		}
		
		return theMap;
	}
	
	private IDtoNameMap(Connection conn, int userid) throws SQLException {			
			
			this.friendMap = new HashMap<Integer,Pair<String,String>>();
			
			Statement IdToUser = conn.createStatement();
			
			ResultSet rs = IdToUser.executeQuery("SELECT m.userid, m.firstname as user_firstname, m.surname as user_surname, m.friendid , n.firstname as friend_firstname, n.surname as friend_surname FROM appuser n inner join (SELECT b.userid, a.firstname, a.surname, b.friendid FROM appuser a inner join (SELECT * FROM friends where (userid = 3 or friendid = 3)) as b on (a.userid = b.userid)) as m on (n.userid = m.friendid);");
			
			if(!rs.isBeforeFirst()) {
			}
	
			Pair<String, String> name = new Pair<String,String>(null,null);
			while(rs.next()) {
				if (userid == rs.getInt("userid")) {
					String firstname = rs.getString("user_firstname");
					String surname = rs.getString("user_surname");
					name.setFirst(firstname);
					name.setSecond(surname);
					friendMap.put(rs.getInt("userid"),name);
				} else {
					String firstname = rs.getString("friend_firstname");
					String surname = rs.getString("friend_surname");
					name.setFirst(firstname);
					name.setSecond(surname);
					friendMap.put(rs.getInt("friendid"),name);
				}
			}

	}
	
	public String getFirstname(int userid) {
		return friendMap.get(userid).getFirst();
	}
	
	public String getSurname(int userid) {
		return friendMap.get(userid).getSecond();
	}
	
	
}


   // NEVER EVER DELETE THIS TILL THE END, RETURNS MAP OF FRIENDS FOR A SPECIFIC USER ID		
			/*
			
*/	
