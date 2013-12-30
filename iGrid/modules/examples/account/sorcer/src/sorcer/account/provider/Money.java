package sorcer.account.provider;

import java.io.Serializable;

public class Money implements Serializable {
	
	protected int cents;

	public Money(Integer cents) {
		this(cents.intValue());
	}

	public Money(int cents) {
		this.cents = cents;
	}

	public int getCents() {
		return cents;
	}

	public void add(Money otherMoney) {
		cents += otherMoney.getCents();
	}

	public void subtract(Money otherMoney) {
		cents -= otherMoney.getCents();
	}

	public boolean greaterThan(Money otherMoney) {
		if (cents > otherMoney.getCents()) {
			return true;
		}
		return false;
	}

	public boolean equals(Object object) {
		if (object instanceof Money) {
			Money otherMoney = (Money) object;

			return (cents == otherMoney.getCents());
		}
		return false;
	}

	public String toString() {
		return cents + " cents";
	}

	public String value() {
		return "" + cents;
	}
}
