package org.hisrc.stopdirect.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "agency_id", "agency_departure_board_url_template" })
@JsonIgnoreProperties
public class Agency {

	@JsonProperty("agency_id")
	private final String id;

	@JsonProperty("agency_departure_board_url_template")
	private final String departureBoardUrlTemplate;

	public Agency(@JsonProperty("agency_id") String id,
			@JsonProperty("agency_departure_board_url_template") String departureBoardUrlTemplate) {
		this.id = id;
		this.departureBoardUrlTemplate = departureBoardUrlTemplate;
	}

	public String getId() {
		return id;
	}

	public String getDepartureBoardUrlTemplate() {
		return departureBoardUrlTemplate;
	}

	@Override
	public String toString() {
		return "Agency [" + id + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, departureBoardUrlTemplate);
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
		Agency other = (Agency) obj;
		return Objects.equals(this.id, other.id)
				&& Objects.equals(this.departureBoardUrlTemplate, other.departureBoardUrlTemplate);
	}
}
