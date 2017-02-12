var BASE_URL = '/stops';
var MAX_DISTANCE = 10000;
var MAX_COUNT = 10;
var AGENCY_IDS = 'db,mvv,nvbw,vbb,vgn,by';
var WALKING_DISTANCE = false;
var MAX_COUNT_PREFERRED_AGENCY_STOP_RESULTS = 25;

var parseUrlParams = function(url) {
		var params = {};
		var queryString = url.lastIndexOf('?') === -1 ? '' : url.substring(url.lastIndexOf('?') + 1);
		var queryStringBeforeHash = queryString.indexOf('#') === -1 ? queryString : queryString.substring(0, queryString.indexOf('#'));  
	    var kvps = queryStringBeforeHash.split('&');
	    for(var index = 0; index < kvps.length; index++)
	    {
	        var kvp = kvps[index].split('=');
	        if (kvp.length >= 2) {
	        	params[kvp[0]] = decodeURIComponent(kvp[1]);
	        } else if (kvp.length == 1 && kvp[0]) {
	        	params[kvp[0]] = kvp[0];
	        }
	    }
	    return params;
};

var PARAMS = parseUrlParams(window.location.href);

var createQueryString = function(params) {
	var queryString = '';
	var first = true;
	for (var paramName in  params) {
		if (params.hasOwnProperty(paramName)) {
			queryString = queryString + (first ? '?' : '&') + paramName + '=' + encodeURIComponent(params[paramName]);
			first = false;
		}
	}
	return queryString;
};

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
			agencyIds: AGENCY_IDS,
			walkingDistance: WALKING_DISTANCE
		}
	}).then(function(results) {
		processAgenciesStopResults(results, lon, lat);
	});
};

var processAgenciesStopResults = function(agenciesStopResults, lon, lat) {
	if (agenciesStopResults.length > 0 && agenciesStopResults[0].stopResults.length > 0) {
		var selectedAgencyId = agenciesStopResults[0].agency.agency_id;
		var selectedStopId = agenciesStopResults[0].stopResults[0].stop.stop_id;
		var agencyStopResults = createAgencyStopResults(agenciesStopResults);
		renderAgencyStopResults(agencyStopResults, lon, lat);
		var preferredAgencyStopResult = loadPreferredAgencyStopResult(lon, lat, agencyStopResults);
		if (preferredAgencyStopResult) {
			renderSelectedAgencyStopResult(preferredAgencyStopResult);
		}
		else {
			renderSelectedAgencyStopResult(agencyStopResults[0]);
		}
	}
	else {
		// TODO nothing found
	}
};

var createAgencyStopResults = function(agenciesStopResults) {
	var agencyStopResults = [];
	for (var index = 0; index < agenciesStopResults.length; index++) {
		var agencyStopResult = agenciesStopResults[index];
		for (var jndex = 0; jndex < agencyStopResult.stopResults.length; jndex++) {
			var stopResult = agencyStopResult.stopResults[jndex];
			var currentResult = {
				agency: agencyStopResult.agency,
				stopResult: stopResult
			};
			agencyStopResults.push(currentResult);
		}
	}
	agencyStopResults.sort(compareAgencyStopResults);
	return agencyStopResults;
};
 
var findAgencyStopResult = function(agencyStopResults, selectedAgencyId, selectedStopId) {
	for (var index = 0; index < agencyStopResults.length; index++) {
		var agencyStopResult = agencyStopResults[index];
		var stopResult = agencyStopResult.stopResult; 
		if (agencyStopResult.agency.agency_id === selectedAgencyId && stopResult.stop.stop_id === selectedStopId) {
			return agencyStopResult;
		}
	}
	return null;
};

var showSelectedAgencyStopResult = function(agencyStopResults, selectedAgencyId, selectedStopId) {
	var agencyStopResult = findAgencyStopResult(agencyStopResults, selectedAgencyId, selectedStopId);
	if (agencyStopResult) {
		renderSelectedAgencyStopResult(agencyStopResult);
	}
};

var compareAgencyStopResults = function(left, right) {
	return left.stopResult.distance - right.stopResult.distance;
};

var renderSelectedAgencyStopResult = function(agencyStopResult) {
	$("#selectedAgencyStopResult").empty();
	var agency = agencyStopResult.agency;
	var stopResult = agencyStopResult.stopResult;
	var stop = stopResult.stop;
	
	$("#selectAgencyStopResult").val(agency.agency_id + '-' + stop.stop_id);
	
	var agencyDepartureBoardUrl = createAgencyDepartureBoardUrl(agency, stop);
	
	if (agencyDepartureBoardUrl.indexOf('https://') === 0) {
		var stopResultFrame = $('<iframe/>').addClass('stopResultFrame').attr({
			src: agencyDepartureBoardUrl,
			width: "100%",
			height: "100%",
			frameborder: "0",
			'border-width':'0px'
		});
		$("#selectedAgencyStopResult").append(stopResultFrame);
	}
	else
	{
		var stopResultContainer = $('<p/>').addClass('stopResultContainer');
		var stopResultLink = $('<a id="stopResultLink"/>').addClass('stopResultLink').attr({
			href: agencyDepartureBoardUrl,
		}).append(stop.stop_name);
		stopResultContainer.append('Weiterleitrung zur Abfahrtstafel von ').append(stopResultLink);
		$("#selectedAgencyStopResult").append(stopResultContainer);

		var currentUrl = window.location.href;

		var targetAgencyIdAndStopId = agency.agency_id + '-' + stop.stop_id;
		var redirectedToAgencyIdAndStopId = PARAMS['selectedAgencyStopResult'] || null; 
		if (!redirectedToAgencyIdAndStopId || redirectedToAgencyIdAndStopId !== targetAgencyIdAndStopId) {
			if (window.history && window.history.pushState) {
				PARAMS['selectedAgencyStopResult'] = targetAgencyIdAndStopId;
				window.history.pushState('forward', null, '.' + createQueryString(PARAMS));
			}
			window.location = agencyDepartureBoardUrl;
		}
		else {
			/*
			 * TODO Remove param
			 * 
			// delete PARAMS['selectedAgencyStopResult'];
			console.log('Removing params');
			var newParams = $.extend({}, PARAMS);
			delete newParams['selectedAgencyStopResult'];
			if (window.history && window.history.pushState) {
				window.history.replaceState('forward', null, '.' + createQueryString(newParams));
			}*/
		}
	}
};

var createAgencyDepartureBoardUrl = function(agency, stop) {
	var url = agency.agency_departure_board_url_template
		.replace('{stop_id}', stop.stop_id)
		.replace('{stop_code}', stop.stop_code);
	return url;
};


var renderAgencyStopResults = function(agencyStopResults, lon, lat) {
	$("#agencyStopResults").empty();
	var selectAgencyStopResultsElement = $("<select id=\"selectAgencyStopResult\"/>").addClass('selectAgencyStopResult');
	for (var index = 0; index < agencyStopResults.length; index++) {
		var agencyStopResult = agencyStopResults[index];
		selectAgencyStopResultsElement.append(createAgencyStopResultOption(agencyStopResult));
	}
	selectAgencyStopResultsElement.change(function(){
		var data = selectAgencyStopResultsElement.val().split('-');
		var agencyId = data[0];
		var stopId = data[1];
		var agencyStopResult = findAgencyStopResult(agencyStopResults, agencyId, stopId);
		if (agencyStopResult) {
			renderSelectedAgencyStopResult(agencyStopResult);
		}
		showSelectedAgencyStopResult(agencyStopResults, agencyId, stopId);
		savePreferredAgencyStopResult(lon, lat, agencyStopResult);
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

var loadPreferredAgencyStopResult = function(lon, lat, agencyStopResults) {
	var preferredAgencyStopResultsString = localStorage.getItem('preferredAgencyStopResults') || '[]';
	var preferredAgencyStopResults = JSON.parse(preferredAgencyStopResultsString);
	for (var index = 0; index < preferredAgencyStopResults.length; index++){
		var preferredAgencyStopResult = preferredAgencyStopResults[index];
		var dlon = lon - preferredAgencyStopResult.lon;
		var dlat = lat - preferredAgencyStopResult.lat;
		var distance = calculateDistance(lon, lat, preferredAgencyStopResult.lon, preferredAgencyStopResult.lat);
		if (distance < 250) {
			var r = preferredAgencyStopResult.r;
			var agencyId = r.split('-')[0];
			var stopId = r.split('-')[1];
			var agencyStopResult = findAgencyStopResult(agencyStopResults, agencyId, stopId);
			if (agencyStopResult) {
				return agencyStopResult;
			}
		};
	}
	return null;
};

var savePreferredAgencyStopResult = function(lon, lat, agencyStopResult) {
	var preferredAgencyStopResultsString = localStorage.getItem('preferredAgencyStopResults') || '[]';
	var preferredAgencyStopResults = JSON.parse(preferredAgencyStopResultsString);
	var agency = agencyStopResult.agency;
	var stopResult = agencyStopResult.stopResult;
	var stop = stopResult.stop;
	var preferredAgencyStopResult = {
			lon: lon,
			lat: lat,
			r : agency.agency_id + '-' + stop.stop_id
	};
	preferredAgencyStopResults.unshift(preferredAgencyStopResult);
	preferredAgencyStopResults.slice(0, MAX_COUNT_PREFERRED_AGENCY_STOP_RESULTS);
	preferredAgencyStopResultsString = JSON.stringify(preferredAgencyStopResults);
	localStorage.setItem('preferredAgencyStopResults', preferredAgencyStopResultsString);
};

function calculateDistance(lon1, lat1, lon2, lat2) {
	  var R = 6371; // Radius of the earth in km
	  var dLat = deg2rad(lat2-lat1);  // deg2rad below
	  var dLon = deg2rad(lon2-lon1); 
	  var a = 
	    Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
	    Math.sin(dLon/2) * Math.sin(dLon/2); 
	  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	  var d = R * c; // Distance in km
	  return d;
	}

	function deg2rad(deg) {
	  return deg * (Math.PI/180)
	}
	
var log = function(text) {
	$("#log").append(text).append("<br/>")
}