package com.example.helpers.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsList {

	public static ArrayList<UserDetails> friends;
	private static Map<Integer, Pair<String, String>> friendMap = new HashMap<Integer, Pair<String,String>>();
	private static boolean created;
	
	private FriendsList() {	}
	
	public static ArrayList<UserDetails> getInstance() {
		if (!created)
			return null;
		return friends;
	}
	
	public static void createInstance(ArrayList<UserDetails> friendList) {
		friends = new ArrayList<UserDetails>();
		for (UserDetails user : friendList) {
			addFriend(user);
		}
		created = true;
	}
	
	public static void addFriend(UserDetails user) {
		friends.add(user);
		friendMap.put(user.getUserid(), new Pair<String, String>(user.getFirstName(), user.getSurname()));
	}
	
	public static String getFirstname(int userid) {
		
		if (friendMap.containsKey(userid))		
			return friendMap.get(userid).getFirst();
		
		return "Not Friend";
	}
	
	public static String getSurname(int userid) {
		
		if (friendMap.containsKey(userid))	
			return friendMap.get(userid).getSecond();
		
		return "Not Friend";
	}
	
	public static String showFriends() {
		String res = "";
		for (UserDetails friend : friends) {
			res += friend.getUserid() + ":" + friend.getFirstName() +" "+ friend.getSurname() +"\n";
		}
		return res;
	}
	
}
