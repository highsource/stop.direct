package org.hisrc.stopdirect.service;

import org.opengis.referencing.operation.TransformException;

public interface CoordinateTransformer {

	public double[] lonLatToXY(double lon, double lat) throws TransformException;

	public double[] xyToLonLat(double x, double y) throws TransformException;

}
