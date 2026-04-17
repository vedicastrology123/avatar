package jyotish.main;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;
import java.util.Date;
import org.joda.time.DateTime;
import java.util.Properties;
import java.io.InputStream;   // For reading the file
import java.io.IOException;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
/*import jyotish.main.AllPlanets;
import jyotish.main.BirthData;
import jyotish.main.Chart;
import jyotish.main.HouseData;
import jyotish.main.IChartCalculator;*/
import jyotish.main.calc.AllPlanetsCalculator;

//import jyotish.main.calc.AstakavargaCalculator;
import jyotish.main.calc.BirthDataValidator;
import jyotish.main.calc.FJCalculationInputException;
import jyotish.main.calc.HouseDataCalculator;
import jyotish.main.calc.PanchangBasics;
import jyotish.main.calc.PanchangBasicsCalculator;
import jyotish.main.calc.VargaData;
import jyotish.main.calc.VargaDataCalculator;

/*

* Created 4 April, 2003 by Michael W. Taft

*

*/
public class ChartBuilder implements IChartCalculator
{
	private SwissEph sw;
	private Chart chart;
	private AllPlanets allPlanets;
	private BirthData nativeInfo;
	//private CalculationPreferences calcPrefs;
	private HouseData houseData;
	private VargaData vargaData;
	private String zodiac;
	private double ayanamsa;
	private String node;
	private String houseType;
	private char houseSystem;
	//private static final int iflag_SID = SweConst.SEFLG_SWIEPH + SweConst.SEFLG_SIDEREAL + SweConst.SEFLG_SPEED;
	//private static final int iflag_SID = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_NONUT | SweConst.SEFLG_SPEED;
	// | SweConst.SE_TRUE_NODE;
	public static final int SEFLG_TRUE_NODE = 0x00010000;
	private static int iflag_SID = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | 
            SweConst.SEFLG_NONUT | SweConst.SEFLG_SPEED | 
            SEFLG_TRUE_NODE;

	private static final int iflag_TROP = SweConst.SEFLG_SPEED;
	private static int iflag;

	public ChartBuilder() {
        sw = new SwissEph();
        
        // 1. Get the path from Docker/Render Environment
        String ephePath = System.getenv("SE_PATH");
        String os = System.getProperty("os.name").toLowerCase();

        // 2. THE PURIFIER: Remove invisible \r, \n, spaces, and quotes
        // This is the specific fix for Windows-to-Linux deployment errors
        if (ephePath != null) {
            ephePath = ephePath.replaceAll("[\\s\\r\\n\\t\"']", "");
        }

        // 3. OS Fallback for Local Development
        if (ephePath == null || ephePath.isEmpty()) {
            if (os.contains("win")) {
                // Use forward slashes to avoid "Colon-as-Separator" bugs in JNI
                ephePath = "C:/horoscope/swiseph_data";
            } else {
                // Matchs your successful 'ls -la' path exactly
                ephePath = "/usr/local/tomcat/swiseph_data";
            }
        }

        // 4. Remove any trailing slashes (The C-engine is very picky)
        while (ephePath.endsWith("/") || ephePath.endsWith("\\")) {
            ephePath = ephePath.substring(0, ephePath.length() - 1);
        }

        // 5. Final implementation with the semicolon "Search Terminator"
        // This tells the engine to look ONLY in this specific folder.
        sw.swe_set_ephe_path(ephePath + ";");
        
//	System.out.println("DEBUG: SwissEph Path initialized as: [" + ephePath + "]");
}

	public void setCalculationPreferences() {
	Properties prop = new Properties();
        
	try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
		if (input != null) {
			prop.load(input);
			// Debug log for Render logs
			//System.out.println("SUCCESS: Config loaded. Zodiac: " + prop.getProperty("zodiac"));
		} else {
			System.err.println("FAILURE: config.properties still not found in classpath.");
		}
	} catch (IOException ex) {
		System.err.println("IO Error reading config: " + ex.getMessage());
	}

	this.zodiac = prop.getProperty("zodiac");
	this.node = prop.getProperty("node");
	this.houseType = prop.getProperty("house");

		// Handling the numeric conversion safely
		String ayanStr = prop.getProperty("ayanamsa");
		try {
			this.ayanamsa = Double.parseDouble(ayanStr);
		} catch (NumberFormatException e) {
			this.ayanamsa = 24.12; 
		}

		if (zodiac.equals("tropical"))
			this.iflag = this.iflag_TROP;
		else
			this.iflag = this.iflag_SID;
		
		//houseType = prefs.get("house", "Shripati");
		if (houseType.equals("shripati")) this.houseSystem = 'E';
		else if (houseType.equals("Koch")) this.houseSystem = 'K';
		else if (houseType.equals("Placidus")) this.houseSystem = 'P';
		else if (houseType.equals("Alcabitus")) this.houseSystem = 'B';
		else if (houseType.equals("Regiomontanus")) this.houseSystem = 'R';
		else if (houseType.equals("Campanus")) this.houseSystem = 'C';
		else this.houseSystem = 'E'; //IF ALL ELSE FAILS, USE SHRIPATI HOUSES
	}
	
// 	public void calc() {


// // Initialize SwissEph (ensure ephe path is set)
// SwissEph sw = new SwissEph(); 
// StringBuffer serr = new StringBuffer();
// double[] cusps = new double[13];
// double[] acsc = new double[10];
// double[] xp = new double[6];

// // Set Sidereal Mode (e.g., Lahiri)
// sw.swe_set_sid_mode(SweConst.SE_SIDM_LAHIRI, 0, 0);

// // Input Parameters
// double tjd_ut = SweDate.getJulDay(2026, 4, 5, 12.0); // April 5, 2026 12:00 UTC
// double latitude = 13.0827; // E.g., Chennai
// double longitude = 80.2707;
// int flags = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL;

// // 1. Calculate Ascendant (Lagna)
// int result = sw.swe_houses(tjd_ut, flags, latitude, longitude, 'P', cusps, acsc);
// double ascendant = acsc[0]; // Acsc[0] is the Ascendant
// System.out.println("Ascendantc: " + ascendant);

// // 2. Calculate Rahu (True Node)
// sw.swe_calc_ut(tjd_ut, SweConst.SE_TRUE_NODE, flags, xp, serr);
// double rahuPosition = xp[0]; // xp[0] is the longitude
// System.out.println("Rahu Position: " + rahuPosition);
// 	}

	public Chart calculateChart(BirthData ni, InputPreference ip) {
		this.nativeInfo = ni;


		try {
			BirthDataValidator.checkBirthDataValidity(nativeInfo);
		} catch (FJCalculationInputException fje) {
			///Currently catches exceptions, but doesn't stop the calculation. In the future, this needs to stop the calculation.
			// System.out.println(fje);
		}
		this.chart = new Chart();
		double tjd_ut = getTJD_UT();

		DateTime birthdatetime = new DateTime(ni.getBirthYear(),ni.getBirthMonth(),ni.getBirthDate(),ni.getBirthHour(),ni.getBirthMinute());
		ni.setBirthDay(birthdatetime.getDayOfWeek());


		double gmt = ni.getUtcOffset();

		double hour = ni.getBirthHour() + (ni.getBirthMinute()/60.) + (ni.getBirthSecond()/60./60.) - gmt;
		this.allPlanets =
			AllPlanetsCalculator.calculateAllPlanets(
				sw,
				tjd_ut,
				this.zodiac,
				this.ayanamsa,
				this.node,
				this.iflag,
				gmt,
				hour,
				ni.getBirthYear(),ni.getBirthMonth(),ni.getBirthDate(),
				ni.getLatitude(),
				ni.getLongitude());

// System.out.println("Place of Birth : " + ni.getBirthCity() + ", " + ni.getBirthState() +", "+ ni.getBirthCountry());
// System.out.println("Born on "+ni.getBirthDate()+"-"+birthdatetime.monthOfYear().getAsText()+"-"+ni.getBirthYear()+ ", Weekday "+birthdatetime.dayOfWeek().getAsText() + ", at Time "+ni.getBirthHour()+":"+ni.getBirthMinute()+":"+ni.getBirthSecond());
// System.out.println("GMT: " + gmt + "GMT time of birth hour: " + hour);
// System.out.println("Julian day: " + tjd_ut);
// System.out.println("Zodiac: " + this.zodiac);
// System.out.println("Ayanamsa: " + this.ayanamsa);
// System.out.println("Node: " + this.node);
// System.out.println("iflag: " + this.iflag);
// System.out.println("house: " + this.houseType);
// System.out.println("house system: " + this.houseSystem);

		double lon = getDecimalLongitude();
		double lat = getDecimalLatitude();
		
		this.houseData =
			HouseDataCalculator.calculateHouseData(
				sw,
				tjd_ut,
				lon,
				lat,
				this.iflag,
				this.houseSystem);
		chart.setHouseData(houseData);
		
// System.out.println("lon: " + lon);
// System.out.println("lat: " + lat);

		VargaDataCalculator vargaCalculator = new VargaDataCalculator();
		this.vargaData = vargaCalculator.calculateVargaData(allPlanets,houseData.getAscendant());

		PanchangBasics panchangBasics = null;
		panchangBasics =
			PanchangBasicsCalculator.calculatePanchangBasics(
				sw,
				tjd_ut,
				lon,
				lat,
				allPlanets.getPlanet("Surya"),
				allPlanets.getPlanet("Chandra"),
				ni.getBirthDay(),
				ni.getUtcOffset()
			);	
		chart.setPanchangBasics(panchangBasics);

		chart.setNativeInfo2(nativeInfo);
		chart.setPlanetInfo(allPlanets);
		
		chart.setVargaData(vargaData);

		return chart;
	}
	
	public double getTJD_UT(){ //converts local time to the Julian Date of GMT
		
		int year = nativeInfo.getBirthYear();
		int mon = nativeInfo.getBirthMonth();
		int day = nativeInfo.getBirthDate();
		
		int hour = nativeInfo.getBirthHour();
		int min = nativeInfo.getBirthMinute();
		int sec = nativeInfo.getBirthSecond();
		
		/*
		 * double gmt = nativeInfo.getTimeZoneOffset(); double dst =
		 * nativeInfo.getDSTOffset();
		 */

		double utcOffset = nativeInfo.getUtcOffset();
		
		Calendar birth = new GregorianCalendar(year, mon - 1, day, hour, min, sec);
		birth.add(Calendar.SECOND, (int) (-3600. * utcOffset));
		//Changes time to UTC
		double UTHour = (double) birth.get(Calendar.HOUR_OF_DAY);
		double UTMin = (double) birth.get(Calendar.MINUTE);
		double UTSec = (double) birth.get(Calendar.SECOND);
		double ut = UTHour + UTMin / 60. + UTSec / 3600.;
//System.out.println("year " + year + " month " + mon + "day " + day + " gBirth " + birth);
		SweDate sd =
			new SweDate(
				birth.get(Calendar.YEAR),
				birth.get(Calendar.MONTH) + 1,
				birth.get(Calendar.DATE),
				ut);
		return sd.getJulDay();
	}
	
	private double getDecimalLongitude() {
		double longitude =
			(double) Math.abs(nativeInfo.getLongitude());
		if (nativeInfo.getLongitude() < 0)
			longitude = -1 * longitude;
//System.out.println("Longitude = " + longitude);///TEST CODE
		return longitude;
	}
	
	private double getDecimalLatitude() {
		double latitude =
			(double) Math.abs(nativeInfo.getLatitude());
		if (nativeInfo.getLatitude() < 0)
			latitude = -1 * latitude;
//System.out.println("Latitude = " + latitude);///TEST CODE
		return latitude;
	}
	
	static String toHMS(double d) {
		d += 0.5/3600.;	// round to one second
		int h = (int) d;
		d = (d - h) * 60;
		int min = (int) d;
		int sec = (int)((d - min) * 60);

		return String.format("%2d:%02d:%02d", h, min, sec);
	}

	static String toDMS(double d) {
		d += 0.5/3600./10000.;	// round to 1/1000 of a second
		int deg = (int) d;
		d = (d - deg) * 60;
		int min = (int) d;
		d = (d - min) * 60;
		double sec = d;
// return String.format("%d&#176;%02d'%02d\"", deg, min, sec);
		return String.format("%3d°%02d'%05.2f\"", deg, min, sec);
	}	
}
