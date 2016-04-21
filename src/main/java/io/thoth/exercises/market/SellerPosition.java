package io.thoth.exercises.market;


public final class SellerPosition {
	private final String source;
	private final double rate;
	private final int amount;
	
	public SellerPosition(String source, double rate, int amount) {
		super();
		this.source = source;
		this.rate = rate;
		this.amount = amount;
	}

	public String getSource() {
		return source;
	}

	public double getRate() {
		return rate;
	}

	public int getAmount() {
		return amount;
	}
	
	public boolean superceedes(SellerPosition other) {
		return source.equals(other.source);
	}
	
	public boolean isBetterThan(SellerPosition other) {
		return rate > other.rate;
	}
	
	@Override
	public String toString() {
		return String.format("SellerPosition [source = %s, rate = %s, amount = %s",
				source,
				rate,
				amount);
	}
}
