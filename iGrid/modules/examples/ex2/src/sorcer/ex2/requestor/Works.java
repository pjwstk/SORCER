package sorcer.ex2.requestor;

import java.io.Serializable;

import sorcer.ex2.provider.InvalidWork;
import sorcer.ex2.provider.Work;
import sorcer.service.Context;
import sorcer.service.ContextException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Works implements Serializable {

	public static Work work1, work2, work3, work4;

	static {
		work1 = new Work() {
			public Context exec(Context cxt) throws InvalidWork, ContextException {
				int arg1 = (Integer) cxt.getValue("requestor/operand/1");
				int arg2 = (Integer) cxt.getValue("requestor/operand/2");
				int result = arg1 + arg2;
				cxt.putOutValue("provider/result", result);
				if (cxt.getReturnPath() != null) {
					cxt.setReturnValue(result);
				}
				return cxt;
			}
		};

		work2 = new Work() {
			public Context exec(Context cxt) throws InvalidWork, ContextException {
				int arg1 = (Integer) cxt.getValue("requestor/operand/1");
				int arg2 = (Integer) cxt.getValue("requestor/operand/2");
				int result = arg1 * arg2;
				cxt.putOutValue("provider/result", result);
				if (cxt.getReturnPath() != null) {
					cxt.setReturnValue(result);
				}
				return cxt;
			}
		};

		work3 = new Work() {
			public Context exec(Context cxt) throws InvalidWork, ContextException {
				int arg1 = (Integer) cxt.getValue("requestor/operand/1");
				int arg2 = (Integer) cxt.getValue("requestor/operand/2");
				int result = arg1 - arg2;
				cxt.putOutValue("provider/result", result);
				if (cxt.getReturnPath() != null) {
					cxt.setReturnValue(result);
				}
				return cxt;
			}
		};

		work4 = new Work() {
			public Context exec(Context cxt) throws InvalidWork, ContextException {
				int arg1 = (Integer)cxt.getValue("requestor/operand/1");
				int arg2 = (Integer)cxt.getValue("requestor/operand/2");
				int arg3 = (Integer)cxt.getValue("requestor/operand/3");
				int result = Math.round((arg1 + arg2 + arg3)/3);
				cxt.putOutValue("provider/result", result);
				if (cxt.getReturnPath() != null) {
					cxt.setReturnValue(result);
				}
				return cxt;
			}
		};
	}
}
