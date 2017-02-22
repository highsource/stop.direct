package org.hisrc.stopdirect.dataccess.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hisrc.stopdirect.dataccess.AgencyStopRepository;
import org.hisrc.stopdirect.dataccess.StopRepository;
import org.hisrc.stopdirect.model.Agency;
import org.hisrc.stopdirect.model.AgencyStopResults;
import org.hisrc.stopdirect.model.Stop;
import org.hisrc.stopdirect.model.StopResult;
import org.hisrc.stopdirect.service.MatrixService;
import org.hisrc.stopdirect.service.impl.GraphHopperMatrixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvAgencyStopRepository implements AgencyStopRepository {

	private final Logger LOGGER = LoggerFactory.getLogger(CsvAgencyStopRepository.class);

	private final static String DEFAULT_RESOURCE_NAME = "agencies.txt";
	private final List<Agency> agencies;
	private final List<String> agencyIds;
	private final Map<String, Agency> agencyById;
	private final Map<Agency, StopRepository> stopRepositoryByAgency;
	private final MatrixService matrixService = new GraphHopperMatrixService();

	public CsvAgencyStopRepository() {
		try {
			this.agencies = loadAgencies(getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_NAME));
			this.agencyIds = createAgencyIds(this.agencies);
			this.agencyById = createAgencyById(this.agencies);
			this.stopRepositoryByAgency = createStopRepositoriesByAgency(agencies);
		} catch (IOException ioex) {
			throw new ExceptionInInitializerError(ioex);
		}
	}

	private List<String> createAgencyIds(List<Agency> agencies) {
		return Collections.unmodifiableList(this.agencies.stream().map(Agency::getId).collect(Collectors.toList()));
	}

	private Map<String, Agency> createAgencyById(List<Agency> agencies) {
		return agencies.stream().collect(Collectors.toMap(Agency::getId, Function.identity()));
	}

	private List<Agency> loadAgencies(InputStream is) throws IOException {
		final List<Agency> stops = new LinkedList<>();
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = mapper.schemaFor(Agency.class).withHeader();

		final MappingIterator<Agency> agenciesIterator = mapper.readerFor(Agency.class).with(schema)
				.readValues(new InputStreamReader(is, "UTF-8"));
		while (agenciesIterator.hasNext()) {
			try {
				final Agency stop = agenciesIterator.next();
				stops.add(stop);
			} catch (RuntimeException rex) {
				LOGGER.warn("Could not read stop from [{}].", agenciesIterator.getCurrentLocation(), rex);
			}
		}
		return Collections.unmodifiableList(stops);
	}

	private Map<Agency, StopRepository> createStopRepositoriesByAgency(List<Agency> agencies) {
		return agencies.stream().collect(Collectors.toMap(Function.identity(), this::createAgencyStopRepository));
	}

	private StopRepository createAgencyStopRepository(Agency agency) {
		return new CsvStopRepository(getClass().getClassLoader().getResourceAsStream(agency.getId() + "/stops.txt"));
	}

	@Override
	public Agency findAgencyById(String agencyId) {
		return this.agencyById.get(agencyId);
	}
	
	@Override
	public List<String> findAllAgencyIds() {
		return this.agencyIds;
	}

	@Override
	public StopResult findNearestStopByAgencyIdAndLonLat(String agencyId, double lon, double lat) {
		final Agency agency = findAgencyById(agencyId);
		if (agency == null) {
			return null;
		} else {
			final StopRepository stopRepository = this.stopRepositoryByAgency.get(agency);
			return stopRepository.findNearestStopByLonLat(lon, lat);
		}
	}

	public AgencyStopResults findNearestStopByAgencyIdAndLonLat(String agencyId, double lon, double lat, int maxCount,
			double maxDistance) {
		final Agency agency = findAgencyById(agencyId);
		if (agency == null) {
			return null;
		} else {
			final StopRepository stopRepository = this.stopRepositoryByAgency.get(agency);
			List<StopResult> stopResults = stopRepository.findNearestStopsByLonLat(lon, lat, maxCount, maxDistance);
			return new AgencyStopResults(agency, stopResults);
		}
	}

	public List<AgencyStopResults> findNearestStopByAgencyIdAndLonLat(List<String> agencyIds, double lon, double lat,
			int maxCount, double maxDistance, boolean walkingDistance) {
		final List<String> ids;
		if (agencyIds == null || agencyIds.isEmpty()) {
			ids = this.agencyIds;
		} else {
			ids = agencyIds;
		}

		List<AgencyStopResults> agency = ids.stream()
				.filter(Objects::nonNull)
				.map(agencyId -> this.findNearestStopByAgencyIdAndLonLat(agencyId, lon, lat, maxCount, maxDistance))
				.filter(Objects::nonNull).collect(Collectors.toList());
		if (walkingDistance) {
			List<StopResult> stopResults = agency.stream().map(AgencyStopResults::getStopResults)
					.flatMap(Collection::stream).collect(Collectors.toList());
			List<Stop> stops = stopResults.stream().map(StopResult::getStop).collect(Collectors.toList());
			try {
				List<Double> distances = matrixService.calculateDistances(lon, lat, stops);
				if (stopResults.size() == distances.size()) {
					for (int index = 0; index < distances.size(); index++) {
						final double oldDistance = stopResults.get(index).getDistance();
						final double newDistance = distances.get(index);
						stopResults.get(index).setDistance(newDistance);
					}
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
		return agency;
	}
}
