package org.hisrc.stopdirect.service.impl;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.hisrc.stopdirect.service.CoordinateTransformer;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class CRSCoordinateTransformer implements CoordinateTransformer {
	private final MathTransform forwardMathTransform;
	private final MathTransform reverseMathTransform;
	private final CoordinateReferenceSystem sourceCoordinateReferenceSystem;
	private final CoordinateReferenceSystem targetCoordinateReferenceSystem;

	public CRSCoordinateTransformer() {
		try {
			sourceCoordinateReferenceSystem = CRS.decode("EPSG:31468");
			targetCoordinateReferenceSystem = CRS.decode("EPSG:4326");
			this.forwardMathTransform = CRS.findMathTransform(sourceCoordinateReferenceSystem,
					targetCoordinateReferenceSystem, true);
			this.reverseMathTransform = CRS.findMathTransform(targetCoordinateReferenceSystem,
					sourceCoordinateReferenceSystem, true);
		} catch (FactoryException fex) {
			throw new ExceptionInInitializerError(fex);
		}

	}

	@Override
	public double[] lonLatToXY(double lon, double lat) throws TransformException {
		DirectPosition2D srcDirectPosition2D = new DirectPosition2D(sourceCoordinateReferenceSystem, lat, lon);
		DirectPosition2D destDirectPosition2D = new DirectPosition2D();
		try {
			reverseMathTransform.transform(srcDirectPosition2D, destDirectPosition2D);
			return new double[] { destDirectPosition2D.y, destDirectPosition2D.x };
		} catch (Error error) {
			throw error;
		}
	}

	@Override
	public double[] xyToLonLat(double x, double y) throws TransformException {

		DirectPosition2D srcDirectPosition2D = new DirectPosition2D(sourceCoordinateReferenceSystem, y, x);
		DirectPosition2D destDirectPosition2D = new DirectPosition2D();
		forwardMathTransform.transform(srcDirectPosition2D, destDirectPosition2D);

		return new double[] { destDirectPosition2D.y, destDirectPosition2D.x };
	}
}
