package sorcer.arithmetic.provider;

import java.rmi.RemoteException;

import sorcer.core.context.ContextManagementAdapter;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class ContextManager extends ContextManagementAdapter {
	
	public String getContextScript() throws RemoteException {
		StringBuilder sb = new StringBuilder();
		sb.append("context(\"arithmetic\",\n");
		sb.append("\t input(path(\"arg/x1\"), 10.0d),\n");
		sb.append("\t input(path(\"arg/x2\"), 50.0d))");
		return sb.toString();
	}

}
