package org.hisrc.stopdirect.dataccess.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.hisrc.stopdirect.dataccess.AgencyStopRepository;
import org.junit.Assert;
import org.junit.Test;

public class CsvAgenciesStopRepositoryTest {

	private final AgencyStopRepository agenciesStopRepository = new CsvAgencyStopRepository();

	@Test
	public void findsAgencyById() {
		Assert.assertNotNull(agenciesStopRepository.findAgencyById("db"));
	}

	@Test
	public void findsNearestStopByAgencyIdAndLonLat() {
		assertEquals("Seulberg", agenciesStopRepository.findNearestStopByAgencyIdAndLonLat("db", 8.657660, 50.239804)
				.getStop().getName());
	}

	@Test
	public void findsNearestStopByAgencyIdAndLonLat1() {
		assertEquals("Seulberg",
				agenciesStopRepository
						.findNearestStopByAgencyIdAndLonLat(Arrays.asList("db"), 8.657660, 50.239804, 1, 1000, true)
						.get(0).getStopResults().get(0).getStop().getName());
	}

}
