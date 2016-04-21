package io.thoth.exercises.market;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NaiveMarket implements Market {
	public final List<SellerPosition> ev = new ArrayList<>();
	
	public NaiveMarket() {
		super();
	}


	@Override
	public void update(SellerPosition pos) {
		int i;
		for (i=0; i < ev.size(); i++) {
			SellerPosition existing = ev.get(i);
			if (pos.superceedes(existing)) {
				ev.set(i, pos);
				break;
			}
		}
		if (i == ev.size()) {
			ev.add(pos);
		}
	}
	
	@Override
	public List<SellerPosition> best(int amount) {
		ev.sort((pos1, pos2) -> Double.compare(pos2.getRate(), pos1.getRate()));
		return ev.stream()
				.limit(amount)
				.collect(Collectors.toList());
	}
}
