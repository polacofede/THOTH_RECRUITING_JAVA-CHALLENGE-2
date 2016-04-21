package io.thoth.exercises.market;

import java.util.List;

public interface Market {

	/**
	 * Updates the market with a new seller position.
	 * <p>
	 * If there's a position for the same source, it will be replaced. Else, the new position
	 * and source are added to the market
	 * @param pos The position used to update the market
	 */
	public abstract void update(SellerPosition pos);

	/**
	 * Get's the best positions available in the market, in terms of rate.
	 * @param amount The number of positions to return
	 * @return The best positions available. May have less items (even 0) if there are not enough
	 * 		sources registered to the market to provide all the positions requested.
	 */
	public abstract List<SellerPosition> best(int amount);

}