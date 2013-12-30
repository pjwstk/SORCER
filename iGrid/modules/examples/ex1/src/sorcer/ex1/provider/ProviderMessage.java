package sorcer.ex1.provider;

import java.io.Serializable;

import sorcer.ex1.Message;

public class ProviderMessage implements Message, Serializable {

	private static final long serialVersionUID = 2010624006315717678L;

	private String echo, prvName, reqName;

	public ProviderMessage(String message, String prvName, String reqName) {
		this.echo = message;
		this.prvName = prvName;
		this.reqName = reqName;
	}

	/* (non-Javadoc)
	 * @see sorcer.ex1.Message#getMessgae()
	 */
	@Override
	public String getMessage() {
		String msg;
		if (echo != null && echo.length() > 0)
			msg = echo + "; " + prvName + ":Hi " + reqName + "!";
		else
			msg = prvName + ":Hi " + reqName + "!";
		return msg;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
}