package org.hisrc.stopdirect.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "stop_id", "stop_name", "stop_lon", "stop_lat", "stop_code"})
public class Stop {

	@JsonProperty("stop_id")
	private final String id;
	@JsonProperty("stop_name")
	private final String name;
	@JsonProperty("stop_lon")
	private final double lon;
	@JsonProperty("stop_lat")
	private final double lat;
	@JsonProperty("stop_code")
	private final String code;

	@JsonCreator
	public Stop(@JsonProperty("stop_id") String id,
			@JsonProperty("stop_name") String name,
			@JsonProperty("stop_lon") double lon,
			@JsonProperty("stop_lat") double lat,
			@JsonProperty("stop_code") String code) {
		super();
		this.id = id;
		this.name = name;
		this.lon = lon;
		this.lat = lat;
		this.code = code;
	}

	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLon() {
		return lon;
	}

	public double getLat() {
		return lat;
	}
	
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "Stop [" + name + " (" + id + " " + lon + ", " + lat + ", " + code + ")]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, lon, lat, code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Stop other = (Stop) obj;
		return Objects.equals(this.id, other.id)
				&& Objects.equals(this.name, other.name)
				&& Objects.equals(this.lon, other.lon)
				&& Objects.equals(this.lon, other.lat)
				&& Objects.equals(this.code, other.code);
	}

}
