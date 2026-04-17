function locationInfo() {
	var countriesArray = [];
	var statesArray = [];
	var citiesArray = [];
	var jsonData;
	var countryLatitude;
	var countryLongitude;
	var countryGmtOffsetName;
	var countryGmtOffset;
	var countryDstOffset;
	
	var cityLatitude;
	var cityLongitude;
	var cityGmtOffsetName;
	var cityGmtOffset;
	var cityDstOffset;
	var geoapifyData;
	
	this.loadCountries = function() {

 		jQuery.getJSON('/data/places.json', function(data){
			
			jsonData = data;
				
			var countryselectBox = document.getElementById('countryid');
			for (var c = 0; c < data.length; c++) {
				countriesArray.push(data[c].name);
				var optionC = data[c];
				countryselectBox.options.add( new Option(optionC.name, optionC.name));
			}
			$('#countryid').val("India").change();
			$('#countryid').find('select').trigger('change');
		});
	};

	this.getCountryGeozones = function(country) {
 		for (var cs = 0; cs < countriesArray.length; cs++) {
			if (countriesArray[cs] == country) {
				var optionCS = jsonData[cs];
				countryLatitude = optionCS.latitude;
				countryLongitude = optionCS.longitude;
				countryGmtOffsetName = optionCS.timezones[0].gmtOffsetName;
				countryGmtOffset = optionCS.timezones[0].gmtOffset / 3600.0;
				countryDstOffset = 0.0;
				$('#countryLatitude').val(countryLatitude);
				$('#countryLongitude').val(countryLongitude);
				$('#countryGmtOffsetName').val(countryGmtOffsetName);
				$('#countryGmtOffset').val(countryGmtOffset);
				$('#countryDstOffset').val(countryDstOffset);
//alert(countryLatitude+" "+countryLongitude+" "+countryGmtOffsetName +" "+ countryGmtOffset+" "+countryDstOffset);				
				break;
			}
		}
	};
	
 	this.loadStates = function(country) {

			var stateselectBox = document.getElementById('stateid');

			for (var s = 0; s < jsonData.length; s++) {
			if (jsonData[s].name == country) {
				var optionS = jsonData[s];
				for (var ss = 0; ss < optionS.states.length; ss++) {
					var state = optionS.states[ss];
					statesArray.push(state.name);
					stateselectBox.options.add( new Option(state.name, state.name) );
				}					
			}
		}
	};

	this.loadCities = function(country, state) {

		var cityselectBox = document.getElementById('cityid');
 		for (var cs = 0; cs < countriesArray.length; cs++) {
			if (countriesArray[cs] == country) {
				var optionCS = jsonData[cs];
				for (var ss = 0; ss < optionCS.states.length; ss++) {
					var statesAll = optionCS.states[ss];
					if (statesAll.name == state) {
						for (var css = 0; css < statesAll.cities.length; css++) {							
							var cityAll = statesAll.cities;
							citiesArray.push(cityAll[css].name);
							cityselectBox.options.add( new Option(cityAll[css].name, cityAll[css].name) );
						}						
						break;
					}	
				}
				break;				
			}
		}
	};
	
	this.getCityData = function(country,state,city) {

 		for (var cs = 0; cs < countriesArray.length; cs++) {
			if (countriesArray[cs] == country) {
				var optionCS = jsonData[cs];
				for (var ss = 0; ss < optionCS.states.length; ss++) {
					var statesAll = optionCS.states[ss];
					if (statesAll.name == state) {
						
						for (var css = 0; css < statesAll.cities.length; css++) {							
							var cityAll = statesAll.cities;							
							if (cityAll[css].name == city) {								
								
								cityLatitude = cityAll[css].latitude;
								cityLongitude = cityAll[css].longitude;
								getGeoApiData(cityLatitude,cityLongitude,setGeoData);
							break;	
							}
						}						
						break;
					}	
				}
				break;				
			}
		}
	};

	function getGeoApiData(lat,lon, myCallback) {
		$('#cityLatitude').val(lat);
		$('#cityLongitude').val(lon);
		var apiKey = "5b52e5d78d054706bc59249d27bb666d";
		var urlName = "https://api.geoapify.com/v1/geocode/reverse?lat=" + lat + "&lon=" + lon + "&apiKey=" + apiKey;	

		var promise = jQuery.getJSON(urlName)
        		.success(function(data){
					geoapifyData = data;
					myCallback(geoapifyData);
				})
				.fail(function() { alert("Server is busy, Please try after sometime, Sorry."); });
		
	}
	
	function setGeoData() {
console.log(geoapifyData);
        	if (geoapifyData.features.length) {
	        	//geoapifyData = data;
				cityGmtOffsetName = geoapifyData.features[0].properties.timezone.name;
				cityGmtOffset = geoapifyData.features[0].properties.timezone.offset_STD_seconds / 3600.0;
				cityDstOffset = geoapifyData.features[0].properties.timezone.offset_DST_seconds / 3600.0;

console.log(cityGmtOffsetName + " js " + cityGmtOffset + " " + cityDstOffset);
$('input[id=cityGmtOffsetName]').val(cityGmtOffsetName);
$('input[id=cityGmtOffset]').val(cityGmtOffset);
$('input[id=cityDstOffset]').val(cityDstOffset);		
console.log($('#cityGmtOffsetName').val() + " jsp " + $('#cityGmtOffset').val() + " " + $('#cityDstOffset').val());
			}
			else {
				alert(" Geo Data for your place is not available, please try a nearby city");
			}
			//$('#cityGmtOffsetName').val(cityGmtOffsetName);
			//$('#cityGmtOffset').val(cityGmtOffset);
			//$('#cityDstOffset').val(cityDstOffset);
//console.log(cityGmtOffsetName + " " + cityGmtOffset + " " + cityDstOffset);			
		//console.log(cityGmtOffsetName + " " + cityGmtOffset + " " + cityDstOffset);
		if (cityGmtOffsetName == "" || cityGmtOffset == "" || cityDstOffset == "" )	{
			alert(" Geo Data for your place is not available, please try a nearby city");
		}
	}
}

jQuery(function() {

    var loc = new locationInfo();
	var country, state, city;
	loc.loadCountries();
	
	jQuery("#countryid").on("change", function(evco) {
		country = $("#countryid option:selected").val();
		$('#stateid').find('option').not(':first').remove();
		$('#cityid').find('option').not(':first').remove();
		
		if(country != ''){
			loc.getCountryGeozones(country);
            loc.loadStates(country);
        }
        else{
			$('#stateid').find('option').not(':first').remove();
        }
    });
	
	jQuery("#stateid").on("change", function(evst) {
		$('#cityid').find('option').not(':first').remove();
        state = jQuery("#stateid option:selected").val();
        if(state != ''){
            loc.loadCities(country, state);
        }
        else{
			$('#cityid').find('option').not(':first').remove();
        }
    });
	
	jQuery("#cityid").on("change", function(evc) {
        $('#cityid').trigger('focusout');
        city = jQuery("#cityid option:selected").val();
        $('#cityid').trigger('focus');
        if(city != ''){
            loc.getCityData(country,state,city);
        }
        else{
			$('#cityid').find('option').not(':first').remove();
        }
    });
});