package io.thoth.exercises.market;

import java.util.Collections;
import java.util.List;

public class NoopMarket implements Market {

	@Override
	public void update(SellerPosition pos) {
		// No-op
	}

	@Override
	public List<SellerPosition> best(int amount) {
		return Collections.emptyList();
	}
}
