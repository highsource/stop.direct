package org.hisrc.stopdirect.dataccess.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hisrc.stopdirect.dataccess.StopRepository;
import org.hisrc.stopdirect.dataccess.impl.CsvStopRepository;
import org.hisrc.stopdirect.model.StopResult;
import org.junit.Test;

public class CsvStopRepositoryTest {

	private StopRepository stopRepository = new CsvStopRepository(
			getClass().getClassLoader().getResourceAsStream("db/stops.txt"));

	@Test
	public void findsSeulbergById() {
		assertEquals("Seulberg", stopRepository.findById("8005544").getName());
	}

	@Test
	public void findsSeulbergByLonLat() {
		assertEquals("Seulberg", stopRepository.findNearestStopByLonLat(8.657660, 50.239804).getStop().getName());
	}

	@Test
	public void findsFrankfurtNiederradByLonLat() {
		assertEquals("8002050", stopRepository.findNearestStopByLonLat(8.637075, 50.081283).getStop().getId());
	}
	
	@Test
	public void findsFrankfurtNiederradByLonLat1() {
		assertEquals(10, stopRepository.findNearestStopsByLonLat(8.637075, 50.081283, 10, Double.POSITIVE_INFINITY).size());
		assertEquals(2, stopRepository.findNearestStopsByLonLat(8.637075, 50.081283, 10, 1500).size());
		assertEquals(1, stopRepository.findNearestStopsByLonLat(8.637075, 50.081283, 10, 1450).size());
	}
	
}
