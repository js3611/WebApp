package HttpServletReqWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ModifiedServletRequestMsg extends HttpServletRequestWrapper {

	private int conversationid;
	
	
	public ModifiedServletRequestMsg(HttpServletRequest request, int conversationid) {
		super(request);		
		if(conversationid != -1)
			this.conversationid = conversationid;
		
	}
	
	@Override
	public String getParameter(String param) {
		if (param.equals("conversationid")) {
			return Integer.toString(this.conversationid);
		}else {
			return super.getParameter(param);
		}
			
	}
	
}

