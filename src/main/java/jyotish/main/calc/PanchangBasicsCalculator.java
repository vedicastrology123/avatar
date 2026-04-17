package jyotish.main.calc;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;

import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
//import jyotish.main.BirthData;
import jyotish.main.Constants;
import jyotish.main.Planet;
import jyotish.main.TimeRange;
/*
*	Created by Michael W. Taft, 25 March, 2003
*/
public class PanchangBasicsCalculator {
	private static String tithi;//yoga, karana;
	private static double diff,
		sum,
		//location_lon,
		//location_lat,
		sunLon,
		moonLon;
	private static PanchangBasics panch_basics;
	//private static SwissEph sw;
	public static PanchangBasics calculatePanchangBasics(
		SwissEph sw,
		double tjd_ut,
		double location_lon,
		double location_lat,
		Planet surya,
		Planet chandra,
		int birthday,
		double offset
		) {
		panch_basics = new PanchangBasics();
		sunLon = surya.getLongitude();
		moonLon = chandra.getLongitude();
		diff = getDiff(sunLon, moonLon);
		sum = getSum(sunLon, moonLon);
		int nak_no =calculateNakshatra(moonLon);
		panch_basics.setNakshathra(Constants.nakshatra[nak_no]);
		panch_basics.setNakshatra_count(nak_no+1);
		
		panch_basics.setPada(calculatePada(moonLon));
		panch_basics.setDay(calculateDay(birthday));

		panch_basics.setYoga(calculateYoga(sum));
		panch_basics.setTithi(calculateTithi(diff));
		panch_basics.setKarana(calculateKarana(diff));
		panch_basics.setSunrise(
			calculateSunrise(sw, location_lon, location_lat, tjd_ut,offset));
		panch_basics.setSunset(
			calculateSunset(sw, location_lon, location_lat, tjd_ut,offset));
		panch_basics.setAyanamsa(calculateAyanamsa(sw, tjd_ut));
		Date sunrise =panch_basics.getSunrise().getTime();
		Date sunset =panch_basics.getSunset().getTime();
		int diffSec = (int)((sunset.getTime()-sunrise.getTime())/1000);
		@SuppressWarnings("unused")
		int period=diffSec/8;
		//System.out.println("period"+period);
		
		//panch_basics.setRahukal(calulateRange(sunrise,sunset,birthday,Constants.rahu,period));
		//panch_basics.setYamaganda(calulateRange(sunrise,sunset,birthday,Constants.yamaganda,period));
		//panch_basics.setGulika(calulateRange(sunrise,sunset,birthday,Constants.gulika,period));
		
		
		return panch_basics;
	}
	@SuppressWarnings("unused")
	private static String calulateRange(Date sunrise, Date sunset,int birthday,int[] constant,int period) {
		//Calculate Rahukal
		//birthday=(birthday>0?birthday:6);
		DateTime from = new DateTime(sunrise);
		DateTime to = new DateTime(sunrise);
		to = to.plusSeconds(constant[birthday]*period);
		from = to.minusSeconds(period);
		TimeRange range = new TimeRange(from,to);
		return range.toString();
	}
	private static double getDiff(double sunLon, double moonLon) {
		double diff = moonLon - sunLon;
		if (diff < 0)
			diff = diff + 360;
		//System.out.println("diff "+ diff);
		return diff;
	}
	private static double getSum(double sunLon, double moonLon) {
		double sum = moonLon + sunLon;
		return sum % 360;
	}
	public static String calculateTithi(double diff) {
		int ti = (int) (diff / 12);
		
		if(ti<0){
			ti=0;
		}
		//System.out.println("ti "+ ti);
		String[] tithiNames =
			{
				"Pratipat",
				"Dvitiya",
				"Tritiya",
				"Chaturthi",
				"Panchami",
				"Shashti",
				"Saptami",
				"Ashtami",
				"Navami",
				"Dashami",
				"Ekadashi",
				"Dvadashi",
				"Trayodashi",
				"Chaturdashi",
				"Purnima",
				"Pratipat",
				"Dvitiya",
				"Tritiya",
				"Chaturthi",
				"Panchami",
				"Shashti",
				"Saptami",
				"Ashtami",
				"Navami",
				"Dashami",
				"Ekadashi",
				"Dvadashi",
				"Trayodashi",
				"Chaturdashi",
				"Amavasya" };
		if (ti < 15)
			tithi = "Shukla paksha " + tithiNames[ti];
		else if (15 <= ti)
			tithi = "Krishna paksha, " + tithiNames[ti];
		return tithi;
	}
	public static String calculateKarana(double diff) {
		int ka = (int) (diff / 6);
		
		if(ka<0){
			ka=0;
		}
		String[] karanaNames =
			{
				"Kinstughna",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Bava",
				"Balava",
				"Kaulava",
				"Taitila",
				"Gara",
				"Vanija",
				"Vishti",
				"Shakuni",
				"Chatushpada",
				"Naga" };
		return karanaNames[ka];
	}
	public static String calculateYoga(double sum) {
		int yo = (int) (sum / (40.0 / 3.0));
		if(yo<0){
			yo=0;
		}
		String[] yogaNames =
			{
				"Vishkambha",
				"Priti",
				"Ayushman",
				"Saubhagya",
				"Shobhana",
				"Atiganda",
				"Sukarma",
				"Dhriti",
				"Shula",
				"Ganda",
				"Vriddhi",
				"Dhruva",
				"Vyaghata",
				"Harshana",
				"Vajra",
				"Siddhi",
				"Vyatipat",
				"Variyana",
				"Parigha",
				"Shiva",
				"Siddha",
				"Sadhya",
				"Shubha",
				"Shukla",
				"Brahma",
				"Indra",
				"Vaidhriti" };
		return yogaNames[yo];
	}
	public static GregorianCalendar calculateSunrise(
		SwissEph sw,
		double lon,
		double lat,
		double tjd_ut,
		double offset) {
		double[] lonLatH = { lon, lat, 0 };
		DblObj sunrise = new DblObj();
		StringBuffer star = new StringBuffer();
		sw.swe_rise_trans(
			tjd_ut,
			SweConst.SE_SUN,
			star,
			SweConst.SEFLG_SWIEPH,
			SweConst.SE_CALC_RISE
				+ SweConst.SE_BIT_DISC_CENTER
				+ SweConst.SE_BIT_NO_REFRACTION,
			lonLatH,
			0.0,
			0.0,
			sunrise,
			star);
		//calculates disc center, no refraction.
		SweDate sr = new SweDate(sunrise.val);
		int sunHour = (int) sr.getHour();
		double rem1 = sr.getHour() - (double) sunHour;
		double sun2 = rem1 * 60.0;
		int sunMin = (int) sun2;
		double rem2 = sun2 - (double) sunMin;
		double sun3 = rem2 * 60.0;
		int sunSec = (int) sun3;
		GregorianCalendar sunriseGreg =
			new GregorianCalendar(
				(int) sr.getYear(),
				(int) sr.getMonth(),
				(int) sr.getDay(),
				sunHour,
				sunMin,
				sunSec);
		//add the timezone offset
		sunriseGreg.add(Calendar.SECOND, (int)(offset*3600));
		/*System.out.println(
			"[PanchangBasics]: Sunrise = "
				+ (int) sr.getYear()
				+ ":"
				+ (int) sr.getMonth()
				+ ":"
				+ (int) sr.getDay()
				+ ":"
				+ sunHour
				+ ":"
				+ sunMin
				+ ":"
				+ sunSec);*/
		return sunriseGreg;
	}
	public static GregorianCalendar calculateSunset(
		SwissEph sw,
		double lon,
		double lat,
		double tjd_ut,
		double offset) {
		double[] lonLatH = { lon, lat, 0 };
		DblObj sunset = new DblObj();
		StringBuffer star = new StringBuffer();
		sw.swe_rise_trans(
			tjd_ut,
			SweConst.SE_SUN,
			star,
			SweConst.SEFLG_SWIEPH,
			SweConst.SE_CALC_SET
				+ SweConst.SE_BIT_DISC_CENTER
				+ SweConst.SE_BIT_NO_REFRACTION,
			lonLatH,
			0.0,
			0.0,
			sunset,
			star);
		//calculates disc center, no refraction.
		SweDate ss = new SweDate(sunset.val);
		int sunHour = (int) ss.getHour();
		double rem1 = ss.getHour() - (double) sunHour;
		double sun2 = rem1 * 60.0;
		int sunMin = (int) sun2;
		double rem2 = sun2 - (double) sunMin;
		double sun3 = rem2 * 60.0;
		int sunSec = (int) sun3;
		GregorianCalendar sunsetGreg =
			new GregorianCalendar(
				(int) ss.getYear(),
				(int) ss.getMonth(),
				(int) ss.getDay(),
				sunHour,
				sunMin,
				sunSec);
		sunsetGreg.add(Calendar.SECOND, (int)(offset*3600));
		/*System.out.println(
			"[PanchangBasics]: Sunset = "
				+ (int) ss.getYear()
				+ ":"
				+ (int) ss.getMonth()
				+ ":"
				+ (int) ss.getDay()
				+ ":"
				+ sunHour
				+ ":"
				+ sunMin
				+ ":"
				+ sunSec);*/
		return sunsetGreg; ///TESTING ONLY
	}
	public static double calculateAyanamsa(SwissEph sw, double tjd_ut) {
		return sw.swe_get_ayanamsa_ut(tjd_ut);
	}
	
	public static String calculateDay(int dayNo) {
		if(0<dayNo && dayNo<7)
			return Constants.weekday[dayNo];
		else
			return Constants.weekday[0];

	}
	

	public static int calculateNakshatra(double moonlog)
	{

		int i = (int) (moonlog / (40.0/3.0));
		if(i<0){
			i=0;
		}
		return i;
	}

	public static String calculatePada (double lon)//returns nakshatra pada
              {
                String pada = "0";
                lon = lon%(40.0/3.0);
                if (0.0<=lon&&lon<(40.0/3.0/4.0))pada = "1";
                if ((40.0/3.0/4.0)<=lon&&lon<(40.0/3.0/2.0))pada = "2";
                if ((40.0/3.0/2.0)<=lon&&lon<10.0) pada = "3";
                if (10.0<=lon&&lon<(40.0/3.0)) pada = "4";
                return pada;
          }

}
