package org.hisrc.stopdirect.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgencyStopResults {

	@JsonProperty("agency")
	private Agency agency;
	@JsonProperty("stopResults")
	private List<StopResult> stopResults;

	public AgencyStopResults(@JsonProperty("agency") Agency agency,
			@JsonProperty("stopResults") List<StopResult> stopResults) {
		this.agency = agency;
		this.stopResults = stopResults;
	}

	public Agency getAgency() {
		return agency;
	}

	public List<StopResult> getStopResults() {
		return stopResults;
	}
}
