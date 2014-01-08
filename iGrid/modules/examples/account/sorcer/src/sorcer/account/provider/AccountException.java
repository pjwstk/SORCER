package sorcer.account.provider;

public class AccountException extends Exception {
	public boolean withdrawalSucceeded;

	public AccountException(Exception cause) {
		super(cause);
	}
}
