package org.hisrc.stopdirect.service.impl;

import org.hisrc.stopdirect.service.CoordinateTransformer;
import org.hisrc.stopdirect.service.impl.CRSCoordinateTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;

public class CRSCoordinateTransformerTest {

	private CoordinateTransformer coordinateTransformer = new CRSCoordinateTransformer();

	@Test
	public void transformsLonLatToXY() throws TransformException {
		double[] xy = coordinateTransformer.lonLatToXY(10.4780801287, 48.8567107775);
		Assert.assertEquals(4388428, xy[0], 1);
		Assert.assertEquals(5414365, xy[1], 1);
	}

	@Test
	public void transformsXYToLonLat() throws TransformException {
		double[] lonLat = coordinateTransformer.xyToLonLat(4388428, 5414365);
		Assert.assertEquals(10.4780801287, lonLat[0], 0.00001);
		Assert.assertEquals(48.8567107775, lonLat[1], 0.00001);
	}

}
