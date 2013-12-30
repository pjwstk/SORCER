package sorcer.account.provider;

public class OverdraftException extends Exception {
	public boolean withdrawalSucceeded;

	public OverdraftException(boolean withdrawalSucceeded) {
		this.withdrawalSucceeded = withdrawalSucceeded;
	}

	public boolean isWithdrawalSucceeded() {
		return withdrawalSucceeded;
	}
}
