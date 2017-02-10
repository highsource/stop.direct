package org.hisrc.stopdirect.dataccess;

import java.util.List;

import org.hisrc.stopdirect.model.Stop;
import org.hisrc.stopdirect.model.StopResult;

public interface StopRepository {

	public Stop findById(String id);
	
	public StopResult findNearestStopByLonLat(double lon, double lat);

	public List<StopResult> findNearestStopsByLonLat(double lon, double lat, int maxCount, double maxDistance);
}
