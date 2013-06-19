package HttpServletReqWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ModifiedServletRequestTrans extends HttpServletRequestWrapper {

	private int transid;
	String old_date;
	String partial_pay_record;
	
	
	public ModifiedServletRequestTrans(HttpServletRequest request, int t_id, String old_date, String partial_pay_record) {
		super(request);
		if(partial_pay_record != null)
			this.partial_pay_record = partial_pay_record;
		
		if(t_id != -1)
			this.transid = t_id;
		
		if (old_date != null)
			this.old_date = old_date;

	}
	
	
	
	@Override
	public String getParameter(String param) {
		if (param.equals("transid")) {
			return Integer.toString(this.transid);
		} else if (param.equals("owerIdPartialPairs")) {
			return this.partial_pay_record;
		} else if (param.equals("_date")){
			return this.old_date;
		}else {
			return super.getParameter(param);
		}
			
	}
	
}


