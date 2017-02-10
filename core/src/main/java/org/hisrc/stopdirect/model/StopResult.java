package org.hisrc.stopdirect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StopResult {

	@JsonProperty("stop")
	private Stop stop;
	@JsonProperty("distance")
	private double distance;

	public StopResult(@JsonProperty("stop") Stop stop, @JsonProperty("distance") double distance) {
		super();
		this.stop = stop;
		this.distance = distance;
	}

	public Stop getStop() {
		return stop;
	}

	public double getDistance() {
		return distance;
	}
}
