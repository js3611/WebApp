package JSONBuilder;

public class JSONBuilder {
	StringBuilder str;

	private String SPACE = " ";
	private String DOUBLE_QUOTE = "\"";
	private String COLON = ":";
	private String COMMA = ",";
	private boolean btwnEntry = false;
	private boolean startedObj = false;
	private boolean startedArray = false;
	private boolean endedArray = false;
	private boolean endedObj = false;
	private int nestedArrCount = 0;
	private int nestedObjCount = 0;
	

	public JSONBuilder() {
		str = new StringBuilder();
	}
	
	/*
	 * private JSONBuilder() { }
	 * 
	 * public static JSONBuilder jSONString() { JSONBuilder jb = new
	 * JSONBuilder(); jb.str = new StringBuilder("{"); return jb; }
	 */
	
	private void addComma() {
		if (btwnEntry)
			str.append(COMMA);
	}
	
	private void addArrayComma() {
		if (endedArray || (startedObj && btwnEntry))
			str.append(COMMA);
		endedArray = false;
	}
	
	private void addObjComma() {
		if (endedObj)
			str.append(COMMA);
		endedObj = false;
	}
	

	public JSONBuilder beginArray() {
		addArrayComma();
		str.append(DOUBLE_QUOTE+"data"+DOUBLE_QUOTE+COLON+"[");
		btwnEntry = false;
		startedArray = true;
		return this;
	}


	public JSONBuilder endArray() {
		str.append("]");
		nestedArrCount--;
		endedArray = true;
		startedArray = false;
		btwnEntry = false;
		return this;
	}

	public JSONBuilder beginObject() {
		addObjComma();
		str.append("{");
		startedObj = true;
		endedObj = false;
		btwnEntry = false;
		return this;
	}

	public JSONBuilder endObject() {
		str.append("}");
		endedObj = true;
		startedObj = false;
		btwnEntry = false;
		return this;
	}

	public JSONBuilder getBuilder() {
		StringBuilder str = new StringBuilder("");
		return this;
	}

	public JSONBuilder append(String key, String value) {
		addComma();
		btwnEntry = true;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}
	
	public JSONBuilder appendArrVal(String value) {
		addComma();
		btwnEntry = true;
		str.append(DOUBLE_QUOTE
				+ value + DOUBLE_QUOTE);
		return this;
	}

	public JSONBuilder append(String key, int value) {
		addComma();
		btwnEntry = true;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return this;
	}

	public JSONBuilder append(String key, double value) {
		addComma();
		btwnEntry = true;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return this;
	}

	public JSONBuilder append(String key, boolean value) {
		addComma();
		btwnEntry = true;
		str.append(DOUBLE_QUOTE + key + DOUBLE_QUOTE + COLON + value);
		return this;
	}

	public String build() {
		btwnEntry = false;
		return str.toString();
	}

}
