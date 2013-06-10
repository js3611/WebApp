package com.example.helpers;

public class JSONBuilder {
	StringBuilder str;
	String SPACE = " ";
	String DOUBLE_QUOTE = "\"";
	String COLON = ":";

	private JSONBuilder() {	}
	
	public static JSONBuilder jSONString() {
		JSONBuilder jb = new JSONBuilder();
		jb.str = new StringBuilder("{ ");
		return jb;
	}

	public JSONBuilder append(String key, String value) {
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}

	public JSONBuilder append(String key, int value) {
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}

	public JSONBuilder append(String key, double value) {
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}

	public JSONBuilder append(String key, boolean value) {
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}

	public String build() {
		return str.append(" }").toString();
	}
}
