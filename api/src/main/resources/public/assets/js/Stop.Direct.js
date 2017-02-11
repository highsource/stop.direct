var BASE_URL = '/stops';
var MAX_DISTANCE = 10000;
var MAX_COUNT = 10;
var AGENCY_IDS = 'db,mvv,nvbw,vbb,vgn';


var execute = function() {
	navigator.geolocation.getCurrentPosition(onReceiveCurrentPosition, onErrorReceivingCurrentPosition, { enableHighAccuracy: true, timeout: 30000, maximumAge: 120000});
};

var onReceiveCurrentPosition = function(position) {
	requestStops(position.coords.longitude, position.coords.latitude);
};

var onErrorReceivingCurrentPosition = function(error) {
	// TODO error handling
};

var requestStops = function(lon, lat) {
	$.ajax({
		url: BASE_URL,
		data: {
			maxDistance: MAX_DISTANCE,
			maxCount: MAX_COUNT,
			lon: lon,
			lat: lat,
			agencyIds: AGENCY_IDS
		}
	}).then(processAgenciesStopResults);
};

var processAgenciesStopResults = function(agenciesStopResults) {
	if (agenciesStopResults.length > 0 && agenciesStopResults[0].stopResults.length > 0) {
		var selectedAgencyId = agenciesStopResults[0].agency.agency_id;
		var selectedStopId = agenciesStopResults[0].stopResults[0].stop.stop_id;
		showAgenciesStopResults(agenciesStopResults, selectedAgencyId, selectedStopId);
	}
	else {
		// TODO nothing found
	}
};

var showAgenciesStopResults = function(agenciesStopResults, selectedAgencyId, selectedStopId) {
	var selectedAgencyStopResult;
	var agencyStopResults = [];
	for (var index = 0; index < agenciesStopResults.length; index++) {
		var agencyStopResult = agenciesStopResults[index];
		for (var jndex = 0; jndex < agencyStopResult.stopResults.length; jndex++) {
			var stopResult = agencyStopResult.stopResults[jndex];
			var currentResult = {
				agency: agencyStopResult.agency,
				stopResult: stopResult
			};
			if (agencyStopResult.agency.agency_id === selectedAgencyId && stopResult.stop.stop_id === selectedStopId) {
				selectedAgencyStopResult = currentResult;
			}
			else {
				agencyStopResults.push(currentResult);
			}
		}
	}
	agencyStopResults.sort(compareAgencyStopResults);
	agencyStopResults.unshift(selectedAgencyStopResult);

	showAgencyStopResults(agencyStopResults);
	showSelectedAgencyStopResult(selectedAgencyStopResult);
};

var showAgenciesStopResults1 = function(agencyStopResults, selectedAgencyId, selectedStopId) {
	var selectedAgencyStopResult;
	for (var index = 0; index < agencyStopResults.length; index++) {
		var agencyStopResult = agencyStopResults[index];
		var stopResult = agencyStopResult.stopResult; 
		if (agencyStopResult.agency.agency_id === selectedAgencyId && stopResult.stop.stop_id === selectedStopId) {
			showSelectedAgencyStopResult(agencyStopResult);
		}
	}
};


var compareAgencyStopResults = function(left, right) {
	return left.stopResult.distance - right.stopResult.distance;
};

var showSelectedAgencyStopResult = function(agencyStopResult) {
	$("#selectedAgencyStopResult").empty();

	var agency = agencyStopResult.agency;
	var stopResult = agencyStopResult.stopResult;
	var stop = stopResult.stop;

	var agencyDepartureBoardUrl = createAgencyDepartureBoardUrl(agency, stop);
	
	var stopResultFrame = $('<iframe/>').addClass('stopResultFrame').attr({
		src: agencyDepartureBoardUrl,
		width: "100%",
		height: "100%",
		frameborder: "0",
		'border-width':'0px'
	});
	$("#selectedAgencyStopResult").append(stopResultFrame);
};

var createAgencyDepartureBoardUrl = function(agency, stop) {
	var url = agency.agency_departure_board_url_template
		.replace('{stop_id}', stop.stop_id)
		.replace('{stop_code}', stop.stop_code);
	return url;
};


var showAgencyStopResults = function(agencyStopResults) {
	$("#agencyStopResults").empty();
	var selectAgencyStopResultsElement = $("<select/>").addClass('selectAgencyStopResult');
	for (var index = 0; index < agencyStopResults.length; index++) {
		var agencyStopResult = agencyStopResults[index];
		selectAgencyStopResultsElement.append(createAgencyStopResultOption(agencyStopResult));
	}
	selectAgencyStopResultsElement.change(function(){
		var data = selectAgencyStopResultsElement.val().split('-');
		var agencyId = data[0];
		var stopId = data[1];
		showAgenciesStopResults1(agencyStopResults, agencyId, stopId);
	});
	$("#agencyStopResults").append(selectAgencyStopResultsElement);
};


var createAgencyStopResultOption = function(agencyStopResult) {
	var agency = agencyStopResult.agency;
	var stopResult = agencyStopResult.stopResult;
	var stop = stopResult.stop;
	var distance = stopResult.distance;
	var distanceText;
	if (distance < 1000) {
		distanceText = Math.floor(distance) + 'm';
	}
	else {
		distanceText = Math.floor(distance/1000) + 'km';
	}
	var agencyStopResultOption = $("<option>").attr({
		value: agency.agency_id + '-' + stop.stop_id
	}).append(stop.stop_name + ' (' + distanceText + ')');
	return agencyStopResultOption;
};