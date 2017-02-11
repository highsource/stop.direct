package org.hisrc.stopdirect.service;

import java.util.List;

import org.hisrc.stopdirect.model.Stop;

public interface MatrixService {
	
	public List<Double> calculateDistances(double fromLon, double fromLat, List<Stop> toStops);

}
