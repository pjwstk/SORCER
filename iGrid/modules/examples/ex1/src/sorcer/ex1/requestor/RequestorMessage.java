package sorcer.ex1.requestor;

import java.io.Serializable;

import sorcer.ex1.Message;

public class RequestorMessage implements Message, Serializable {

	private static final long serialVersionUID = 2010624006315717678L;

	private String name;

	public RequestorMessage(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see sorcer.ex1.Message#getMessgae()
	 */
	@Override
	public String getMessage() {
		return "Hi " + name + "!";
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
}