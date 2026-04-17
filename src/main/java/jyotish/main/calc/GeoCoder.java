package jyotish.main.calc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;

import java.util.List;

import jyotish.main.BirthData;

public class GeoCoder {

	public void getGeoZoneInfo(BirthData birthdata) throws Exception {
		LocalDateTime localBirthDate = LocalDateTime.of(birthdata.getBirthYear(), birthdata.getBirthMonth(),
				birthdata.getBirthDate(), birthdata.getBirthHour(), birthdata.getBirthMinute());

//System.out.println("GeoCoder GmtOffsetName " + birthdata.getGmtOffsetName());

		ZonedDateTime zoneBirthDate = localBirthDate.atZone(ZoneId.of(birthdata.getGmtOffsetName()));
//System.out.println("Time " + zoneBirthDate);
//System.out.println("Time Zone " + zoneBirthDate.getZone());
		ZoneOffset gmtDST = zoneBirthDate.getOffset();
//System.out.printf("GMT Offset in hr %f", (gmtDST.getTotalSeconds() / 3600.0));

		ZoneId zoneId = zoneBirthDate.getZone();
		
		ZoneRules zoneRules = zoneId.getRules();

		Boolean isDst = zoneRules.isDaylightSavings( zoneBirthDate.toInstant());
		
		double utcOffset = 0.;
		if (!isDst) {
			utcOffset = birthdata.getTimeZoneOffset();
			birthdata.setUtcOffset(utcOffset);
		}
		else {
			utcOffset = birthdata.getDstOffset();
			birthdata.setUtcOffset(utcOffset);
			double gmt = gmtDST.getTotalSeconds() / 3600.0;		  
//System.out.println("gmt " + gmt + ", dst appicable " + (isDst ? "Yes" : "No"));
		}
		
		List<ZoneOffsetTransition> rulesTransition = zoneRules.getTransitions();
//System.out.println("zoneRules " + rulesTransition.size());
		
		  if ( rulesTransition.size() != 0) {
			  for (int i=0;i<rulesTransition.size();i++) {
//System.out.println("rules " + rulesTransition.get(i));
			  }
		  }
		  
	}

	// public static void main(String args[]) throws Exception {
	// 	GeoCoder geoCode = new GeoCoder();
	// 	BirthData birthdata = new BirthData();
	// 	birthdata.setBirthCity("Chennai"); //Heard and McDonald Islands
	// 	birthdata.setBirthDate(8);
	// 	birthdata.setBirthMonth(4);
	// 	birthdata.setBirthYear(1962);
	// 	birthdata.setBirthHour(2);
	// 	birthdata.setBirthMinute(5);
	// 	birthdata.setLatitude(13.0827);
	// 	birthdata.setLongitude(80.2707);
	// 	birthdata.setGmtOffsetName("Asia/Kolkata");
	// 	birthdata.setTimeZoneOffset(5.5);
	// 	birthdata.setDstOffset(0.0);
	// 	geoCode.getGeoZoneInfo(birthdata);		
	// }
}

/* function isDST(d) {
var jan = new Date(d.getFullYear(), 0, 1).getTimezoneOffset();
var jul = new Date(d.getFullYear(), 6, 1).getTimezoneOffset();
return Math.max(jan, jul) !== d.getTimezoneOffset();    
} */