package org.hisrc.stopdirect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hisrc.stopdirect.model.Stop;
import org.hisrc.stopdirect.service.MatrixService;

import com.graphhopper.api.GHMRequest;
import com.graphhopper.api.GraphHopperMatrixWeb;
import com.graphhopper.api.MatrixResponse;
import com.graphhopper.util.shapes.GHPoint;

public class GraphHopperMatrixService implements MatrixService {

	private final String apiKey = System.getProperty("GRAPHHOPPER_API_KEY");

	@Override
	public List<Double> calculateDistances(double fromLon, double fromLat, List<Stop> toStops) {

		GraphHopperMatrixWeb matrixClient = new GraphHopperMatrixWeb();
		matrixClient.setKey(apiKey);
		GHMRequest ghmRequest = new GHMRequest();
		ghmRequest.addOutArray("distances");
		ghmRequest.setVehicle("foot");

		ghmRequest.addFromPoint(new GHPoint(fromLat, fromLon));
		toStops.stream().map(stop -> new GHPoint(stop.getLat(), stop.getLon())).forEach(ghmRequest::addToPoint);
		MatrixResponse response = matrixClient.route(ghmRequest);
		if (response.hasErrors()) {
			return null;
		} else {
			final List<Double> distances = new ArrayList<>(toStops.size());
			for (int to = 0; to < toStops.size(); to++) {
				distances.add(response.getDistance(0, to));
			}
			return distances;
		}
	}

}
