package org.hisrc.stopdirect.dataccess.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hisrc.stopdirect.dataccess.StopRepository;
import org.hisrc.stopdirect.model.AgencyStopResults;
import org.hisrc.stopdirect.model.Stop;
import org.hisrc.stopdirect.model.StopEntry;
import org.hisrc.stopdirect.model.StopResult;
import org.hisrc.stopdirect.service.CoordinateTransformer;
import org.hisrc.stopdirect.service.impl.CRSCoordinateTransformer;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import gnu.trove.procedure.TIntProcedure;
import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

public class CsvStopRepository implements StopRepository {

	private final Logger LOGGER = LoggerFactory.getLogger(CsvStopRepository.class);

	private CoordinateTransformer coordinateTransformer = new CRSCoordinateTransformer();
	private final List<StopEntry> stopEntries;
	private final Map<String, StopEntry> stopEntryById;
	private final SpatialIndex stopEntrySpatialIndex;

	public CsvStopRepository(InputStream is) {
		try {
			this.stopEntries = loadStopEntries(is);
			this.stopEntryById = createStopEntryById(this.stopEntries);
			this.stopEntrySpatialIndex = createSpatialIndex(this.stopEntries);
		} catch (IOException ioex) {
			throw new ExceptionInInitializerError(ioex);
		} finally {
			try {
				is.close();
			} catch (IOException ioex) {
				throw new ExceptionInInitializerError(ioex);
			}
		}
	}

	private Map<String, StopEntry> createStopEntryById(List<StopEntry> stopEntries) {
		return Collections.unmodifiableMap(stopEntries.stream()
				.collect(Collectors.toMap(stopEntry -> stopEntry.getStop().getId(), Function.identity())));
	}

	private List<StopEntry> loadStopEntries(InputStream is) throws IOException {
		final List<StopEntry> stopEntries = new LinkedList<>();
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = mapper.schemaFor(Stop.class).withHeader().withQuoteChar('"');

		final MappingIterator<Stop> stopsIterator = mapper.readerFor(Stop.class).with(schema)
				.readValues(new InputStreamReader(is, "UTF-8"));
		while (stopsIterator.hasNext()) {
			try {
				final Stop stop = stopsIterator.next();
				final double lon = stop.getLon();
				final double lat = stop.getLat();
				try {
					double[] xy = coordinateTransformer.lonLatToXY(lon, lat);
					stopEntries.add(new StopEntry(stop, xy[0], xy[1]));
				} catch (TransformException tex) {
					LOGGER.warn("Could convert lon/lat {}{} to x/y coordinates.", lon, lat);
				}
			} catch (RuntimeException rex) {
				LOGGER.warn("Could not read stop from [{}].", stopsIterator.getCurrentLocation(), rex);
			}
		}
		return Collections.unmodifiableList(stopEntries);
	}

	private SpatialIndex createSpatialIndex(List<StopEntry> stopEntries) {
		final SpatialIndex spatialIndex = new RTree();
		spatialIndex.init(null);
		for (int index = 0; index < stopEntries.size(); index++) {
			final StopEntry stopEntry = stopEntries.get(index);
			float x = (float) stopEntry.getX();
			float y = (float) stopEntry.getY();

			spatialIndex.add(new Rectangle(x, y, x, y), index);
		}
		return spatialIndex;
	}

	@Override
	public Stop findById(String id) {
		final StopEntry stopEntry = stopEntryById.get(id);
		return stopEntry == null ? null : stopEntry.getStop();
	}

	@Override
	public StopResult findNearestStopByLonLat(double lon, double lat) {
		final AtomicInteger indexResult = new AtomicInteger(-1);
		double[] xy = null;
		try {
			xy = coordinateTransformer.lonLatToXY(lon, lat);
		} catch (TransformException tex) {
			LOGGER.warn("Could convert lon/lat {}{} to x/y coordinates.", lon, lat);
			return null;
		}
		float x = (float) xy[0];
		float y = (float) xy[1];
		stopEntrySpatialIndex.nearest(new Point(x, y), new TIntProcedure() {

			@Override
			public boolean execute(int value) {
				indexResult.set(value);
				return true;
			}
		}, Float.POSITIVE_INFINITY);

		final int index = indexResult.get();
		final StopResult stopResult = createStopResult(index, xy);
		return stopResult;
	}

	@Override
	public List<StopResult> findNearestStopsByLonLat(double lon, double lat, int maxCount, double maxDistance) {
		try {
			double[] xy = coordinateTransformer.lonLatToXY(lon, lat);
			float x = (float) xy[0];
			float y = (float) xy[1];
			final List<StopResult> stopResults = new ArrayList<>();
			stopEntrySpatialIndex.nearestN(new Point(x, y), new TIntProcedure() {
				@Override
				public boolean execute(int value) {
					StopResult stopResult = createStopResult(value, xy);
					stopResults.add(stopResult);
					return true;
				}
			}, maxCount, Math.round((float) maxDistance));

			return stopResults;
		} catch (TransformException tex) {
			LOGGER.warn("Could convert lon/lat {}{} to x/y coordinates.", lon, lat);
			return null;
		}
	}

	private StopResult createStopResult(final int index, double[] xy) {
		final StopEntry stopEntry = stopEntries.get(index);
		final double dx = xy[0] - stopEntry.getX();
		final double dy = xy[1] - stopEntry.getY();
		final double distance = Math.sqrt(dx * dx + dy * dy);
		final StopResult stopResult = new StopResult(stopEntry.getStop(), distance);
		return stopResult;
	}

}