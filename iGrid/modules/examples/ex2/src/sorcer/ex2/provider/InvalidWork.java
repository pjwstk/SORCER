package sorcer.ex2.provider;

public class InvalidWork extends Exception {
	
	private String message;

	InvalidWork(String s) {
		message = s;
	}

	public String getMessage() {
		return message;
	}
}