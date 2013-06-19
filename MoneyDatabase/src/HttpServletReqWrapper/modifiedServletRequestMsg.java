package HttpServletReqWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ModifiedServletRequestMsg extends HttpServletRequestWrapper {

	private int conversationid;
	private String time;
	private String date;
	private String content;
	
	
	public ModifiedServletRequestMsg(HttpServletRequest request, int conversationid, String time, String date, String content) {
		super(request);		
		if(conversationid != -1)
			this.conversationid = conversationid;
		if(time!=null)
			this.time = time;
		if(date!=null)
			this.date = date;
		if(content!=null)
			this.content=content;
		
	}
	
	@Override
	public String getParameter(String param) {
		if (param.equals("conversationid")) {
			return Integer.toString(this.conversationid);
		}else if (param.equals("_time")) {
			return this.time;
		}else if (param.equals("_date")) {
			return this.date;
		}else if (param.equals("content")) {
			return this.content;
		}else {
			return super.getParameter(param);
		}
			
	}
	
}

