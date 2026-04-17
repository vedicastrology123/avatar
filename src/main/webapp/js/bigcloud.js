/**
 * Consolidated Location & Geoapify Logic
 */

function locationInfo() {
    var countriesArray = [];
    var jsonData = [];
    window.isGeoLoading = false; // THE GUARD FLAG

    this.loadCountries = function() {
        jQuery.getJSON('/data/places.json', function(data) {
            jsonData = data;
            var countryselectBox = document.getElementById('countryid');
            for (var c = 0; c < data.length; c++) {
                countriesArray.push(data[c].name);
                countryselectBox.options.add(new Option(data[c].name, data[c].name));
            }
            // Default to India
            $('#countryid').val("India").trigger('change');
        });
    };

    this.getCountryGeozones = function(country) {
        var found = jsonData.find(c => c.name === country);
        if (found) {
            $('#countryLatitude').val(found.latitude);
            $('#countryLongitude').val(found.longitude);
            $('#countryGmtOffsetName').val(found.timezones[0].gmtOffsetName);
            $('#countryGmtOffset').val(found.timezones[0].gmtOffset / 3600.0);
            $('#countryDstOffset').val(0.0);
        }
    };

    this.loadStates = function(country) {
        var stateselectBox = document.getElementById('stateid');
        var found = jsonData.find(c => c.name === country);
        if (found && found.states) {
            found.states.forEach(state => {
                stateselectBox.options.add(new Option(state.name, state.name));
            });
        }
    };

    this.loadCities = function(country, state) {
        var cityselectBox = document.getElementById('cityid');
        var countryObj = jsonData.find(c => c.name === country);
        if (countryObj) {
            var stateObj = countryObj.states.find(s => s.name === state);
            if (stateObj && stateObj.cities) {
                stateObj.cities.forEach(city => {
                    cityselectBox.options.add(new Option(city.name, city.name));
                });
            }
        }
    };

    this.getCityData = function(country, state, city) {
        var countryObj = jsonData.find(c => c.name === country);
        if (countryObj) {
            var stateObj = countryObj.states.find(s => s.name === state);
            if (stateObj) {
                var cityObj = stateObj.cities.find(ci => ci.name === city);
                if (cityObj) {
                    // LOCK THE FORM & FETCH
                    window.isGeoLoading = true;
                    getGeoApiData(cityObj.latitude, cityObj.longitude, setGeoData);
                }
            }
        }
    };
}

/** --- API HELPERS --- **/

function getGeoApiData(lat, lon, callback) {
    $('#cityLatitude').val(lat);
    $('#cityLongitude').val(lon);
    
    var apiKey = "5b52e5d78d054706bc59249d27bb666d";
    // Ensure format=json is used for the results[0] structure
    var url = "https://api.geoapify.com/v1/geocode/reverse?lat=" + lat + "&lon=" + lon + "&format=json&include_timezone=true&apiKey=" + apiKey;

    console.log("Fetching Geoapify...");
    
    jQuery.getJSON(url)
        .done(function(data) {
            callback(data);
        })
        .fail(function() {
            window.isGeoLoading = false;
            alert("Geo Service busy. Try re-selecting city.");
        });
}

function setGeoData(data) {
    var result = (data && data.results && data.results.length > 0) ? data.results[0] : null;
    
    if (result && result.timezone) {
        var tz = result.timezone;
        var gmtName = tz.name || "Unknown";
        var calculatedGmt = parseFloat(tz.offset_STD_seconds || 0) / 3600.0;
        var calculatedDst = parseFloat(tz.offset_DST_seconds || 0) / 3600.0;

        // TARGET BY BOTH ID (#) AND NAME ([name=]) TO BE SAFE
        $('#cityGmtOffsetName, input[name="cityGmtOffsetName"]').val(gmtName);
        $('#cityGmtOffset, input[name="cityGmtOffset"]').val(calculatedGmt);
        $('#cityDstOffset, input[name="cityDstOffset"]').val(calculatedDst);
        
        console.log("Values pushed to HTML: " + gmtName + " | GMT: " + calculatedGmt);
    }
    window.isGeoLoading = false;
}

// THE FORM SUBMIT GUARD (Update this part too)
$('#birthchartInput').on('submit', function(e) {
    if (window.isGeoLoading) {
        e.preventDefault();
        alert("Still fetching timezone data... please wait.");
        return false;
    }

    // Check the actual values inside the hidden inputs
    var finalGmt = $('#cityGmtOffset').val();
    var finalName = $('#cityGmtOffsetName').val();

    if (!finalGmt || finalGmt === "" || finalName === "undefined") {
        e.preventDefault();
        alert("Timezone data missing for this city. Please re-select the city from the dropdown.");
        console.log("Submit blocked. Current GMT value in field: ", finalGmt);
        return false;
    }
    
    console.log("Form valid. Submitting to birthchart.jsp with GMT: " + finalGmt);
});

/** --- MAIN EXECUTION --- **/

jQuery(function() {
    var loc = new locationInfo();
    loc.loadCountries();

    $("#countryid").on("change", function() {
        var val = $(this).val();
        $('#stateid, #cityid').find('option').not(':first').remove();
        if(val) { loc.getCountryGeozones(val); loc.loadStates(val); }
    });

    $("#stateid").on("change", function() {
        var val = $(this).val();
        $('#cityid').find('option').not(':first').remove();
        if(val) loc.loadCities($("#countryid").val(), val);
    });

    $("#cityid").on("change", function() {
        var val = $(this).val();
        if(val) loc.getCityData($("#countryid").val(), $("#stateid").val(), val);
    });

    // THE FORM GUARD
    $('#birthchartInput').on('submit', function(e) {
        // If the API hasn't finished, block the user
        if (window.isGeoLoading) {
            e.preventDefault();
            alert("Accurate city data is still loading... please wait a second.");
            return false;
        }

        // If the fields are STILL empty after loading finished, we have a real problem
        var gmt = $('#cityGmtOffset').val();
        if (!gmt || gmt === "") {
            e.preventDefault();
            alert("Timezone data missing for this city. Please try a nearby major city.");
            return false;
        }
        
        console.log("Submitting with GMT:", gmt);
    });
});