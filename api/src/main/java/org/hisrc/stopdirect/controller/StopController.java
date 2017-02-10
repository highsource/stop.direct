package org.hisrc.stopdirect.controller;

import java.util.List;

import org.hisrc.stopdirect.dataccess.AgencyStopRepository;
import org.hisrc.stopdirect.model.AgencyStopResults;
import org.hisrc.stopdirect.model.StopResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StopController {

	@Autowired
	private AgencyStopRepository agencyStopRepository;

	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/stop/{lon:.+}/{lat:.+}")
	@ResponseBody
	public StopResult findByLonLat(@RequestParam(value = "agencyId", defaultValue = "db") String agencyId,
			@PathVariable(value = "lon") double lon, @PathVariable(value = "lat") double lat)
			throws StopNotFoundException {
		StopResult stopResult = agencyStopRepository.findNearestStopByAgencyIdAndLonLat(agencyId, lon, lat);
		if (stopResult != null) {
			return stopResult;
		} else {
			throw new StopNotFoundException(lon, lat);
		}
	}

	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/stops")
	@ResponseBody
	public List<AgencyStopResults> findAgencyStopResultsByLonLat(
			@RequestParam(value = "agencyIds", defaultValue = "db") List<String> agencyIds,
			@RequestParam(value = "lon") double lon, @RequestParam(value = "lat") double lat,
			@RequestParam(value = "maxCount", defaultValue = "5") int maxCount,
			@RequestParam(value = "maxDistance", defaultValue = "10000") double maxDistance)
			throws StopNotFoundException {

		return agencyStopRepository.findNearestStopByAgencyIdAndLonLat(agencyIds, lon, lat, maxCount, maxDistance);
	}

}
