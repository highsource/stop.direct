package org.hisrc.stopdirect.dataccess;

import java.util.List;

import org.hisrc.stopdirect.model.Agency;
import org.hisrc.stopdirect.model.AgencyStopResults;
import org.hisrc.stopdirect.model.StopResult;

public interface AgencyStopRepository {

	public Agency findAgencyById(String agencyId);

	public StopResult findNearestStopByAgencyIdAndLonLat(String agencyId, double lon, double lat);

	public List<AgencyStopResults> findNearestStopByAgencyIdAndLonLat(List<String> agencyIds, double lon, double lat,
			int maxCount, double maxDistance, boolean walkingDistance);

}
