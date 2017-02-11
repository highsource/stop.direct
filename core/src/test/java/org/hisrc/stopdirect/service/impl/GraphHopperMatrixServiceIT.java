package org.hisrc.stopdirect.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.graphhopper.api.GHMRequest;
import com.graphhopper.api.GraphHopperMatrixWeb;
import com.graphhopper.api.MatrixResponse;
import com.graphhopper.util.shapes.GHPoint;

public class GraphHopperMatrixServiceIT {

	@Test
	public void test() {
		GraphHopperMatrixWeb matrixClient = new GraphHopperMatrixWeb();
		matrixClient.setKey(System.getProperty("GRAPHHOPPER_API_KEY"));

		GHMRequest ghmRequest = new GHMRequest();
		ghmRequest.addOutArray("distances");
		ghmRequest.addOutArray("times");
		ghmRequest.setVehicle("foot");

		ghmRequest.addToPoint(new GHPoint(49.44685252153387, 11.075653867741845));
		ghmRequest.addFromPoint(new GHPoint(49.445794199999995, 11.074319));
		ghmRequest.addFromPoint(new GHPoint(50.241906, 8.646005));
		MatrixResponse response = matrixClient.route(ghmRequest);
		assertEquals(182, response.getDistance(0, 0), 50);
		assertEquals(226000, response.getDistance(1, 0), 5000);
	}
}
