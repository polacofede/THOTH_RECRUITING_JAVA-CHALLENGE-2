package io.thoth.exercises.market;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.thoth.exercises.market.utils.RandomString;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class MarketTest {
	private static final int PERFORMANCE_OPERATIONS = 100_000_000;
	private static final int SAMPLING_FACTOR = 1_000;
	
	/* This is a helper variable that can toggle the performance test 
	 * to print baselines, timeouts and progress information */
	private static boolean SHOW_PROFILE_INFO = false;
	
	private final Supplier<Market> marketSupplier;
	private Market market;
	
	public MarketTest(String type, Supplier<Market> marketSupplier) {
		super();
		this.marketSupplier = marketSupplier;
	}
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[][] {
				{ "naive market", (Supplier<Market>) NaiveMarket::new },
				{ "efficient market", (Supplier<Market>) EfficientMarket::new }
		});
	}
	
	@Before
	public void setup() {
		this.market = marketSupplier.get();
	}
	
	@Test
	public void emptyMarketHasNoBest() {
		assertTrue(market.best(3).isEmpty());
	}

	@Test
	public void addingReplacesExisitingSource() {
		market.update(new SellerPosition("a", 1.0, 100));
		market.update(new SellerPosition("a", 3.0, 50));
		market.update(new SellerPosition("b", 2.0, 10));
		market.update(new SellerPosition("b", 1.0, 1));
		
		List<SellerPosition> best = market.best(3);
		assertEquals(2, best.size());
		assertEquals(50, best.get(0).getAmount());
		assertEquals(1, best.get(1).getAmount());
	}

	@Test
	public void bestReturnsOrdered() {
		market.update(new SellerPosition("a", 1.0, 100));
		market.update(new SellerPosition("b", 2.0, 200));
		market.update(new SellerPosition("a", 3.0, 50));
		
		List<SellerPosition> best = market.best(3);
		assertEquals(2, best.size());
		assertEquals(50, best.get(0).getAmount());
		assertEquals(200, best.get(1).getAmount());
	}

	@Test
	public void interleavedOperations() {
		setupMarket(market, 100, 1000, 20);

		List<SellerPosition> best = market.best(3);
		
		// Possible because the result is deterministic!
		assertEquals(22, best.get(0).getAmount());
		assertEquals(57, best.get(1).getAmount());
		assertEquals(87, best.get(2).getAmount());
	}
	

	@Test
	public void implementationPerformance() {
		Assume.assumeFalse("Naive implementation is too slow", market instanceof NaiveMarket);
		
		long timeout = estimateTimeout();
		long elapsed = setupMarket(market, PERFORMANCE_OPERATIONS);

		if (SHOW_PROFILE_INFO) {
			System.out.println("Actual time: " + elapsed);
		}
		assertTrue("Implementation takes too long", elapsed < timeout);

		List<SellerPosition> best = market.best(3);
		assertEquals(3, best.size());
		assertEquals("phkswpb16k", best.get(0).getSource());
		assertEquals("so4sw8sinc", best.get(1).getSource());
		assertEquals("jnqaeb903w", best.get(2).getSource());
	}

	private long estimateTimeout() {
		long base = setupMarket(new NoopMarket(), PERFORMANCE_OPERATIONS);
		long sample = setupMarket(marketSupplier.get(), PERFORMANCE_OPERATIONS / SAMPLING_FACTOR);
		// linear will not be enough
		long timeout = base + sample * SAMPLING_FACTOR;

		if (SHOW_PROFILE_INFO) {
			System.out.println("Baseline (no-op): " + base);
			System.out.println("Sample: " + sample);
			System.out.println("Derived timeout: " + timeout);
		}
		return timeout;
	}

	private long setupMarket(Market market, int positionCount) {
		return setupMarket(market, 10_000, positionCount, 100);
	}

	private long setupMarket(Market market, int sourceCount, int positionCount, int interleave) {
		Random gen = new Random(1234);
		RandomString strGen = new RandomString(10, 4321);

		// Create sources
		List<String> sources = new ArrayList<String>(sourceCount);
		for (int i = 0; i < sourceCount; i++) {
			sources.add(strGen.nextString());
		}
		
		Instant benchmarkStart = Instant.now();
		// Feed in position updates
		for (int i = 0; i < positionCount; i++) {
			SellerPosition pos = new SellerPosition(
					sources.get(gen.nextInt(sourceCount)), 
					1000.0 + gen.nextGaussian() * 100.0,
					gen.nextInt(100));
			market.update(pos);
			
			if (i % interleave == 0) {
				// This simulates best positions being taken
				for (SellerPosition bestPos : market.best(3)) {
					SellerPosition voidPos = new SellerPosition(bestPos.getSource(), 0.0, 0);
					market.update(voidPos);
				}
			}
			if (SHOW_PROFILE_INFO) {
				if (i > 0 && i % 100_000 == 0) {
					Duration elapsed = Duration.between(benchmarkStart, Instant.now());
					System.out.printf("Elapsed: %,8dms, processed: %,d\n", elapsed.toMillis(), i);
				}
			}
		}
		Instant benchmarkEnd = Instant.now();
		Duration baseline = Duration.between(benchmarkStart, benchmarkEnd);
		return baseline.toMillis();
	}
}
