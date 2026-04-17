package jyotish.main.calc;

import swisseph.*;
import jyotish.main.*;

/*
*	Created 5 April, 2003 by Michael W. Taft
*
*/

public class AllPlanetsCalculator{

	private static AllPlanets planets;
	private static String snam = null;
	private static char retro = ' ';
	private static StringBuffer serr = new StringBuffer();
	private static double x2[] = new double[6];
	private static long iflgret;
	private static final int SID_METHOD = SweConst.SE_SIDM_TRUE_CITRA;
	
	public static AllPlanets calculateAllPlanets(SwissEph sw, double tjd_ut, String zodiac, double ayanamsa, String node, int iflag, double gmt, double hour, int year, int month, int day, double latitude, double longitude){	
		
		// Calculate all planets:
		int[] planetsNeeded =
			{ SweConst.SE_SUN,
				SweConst.SE_MOON,
				SweConst.SE_MARS,
				SweConst.SE_MERCURY,
				SweConst.SE_JUPITER,
				SweConst.SE_VENUS,
				SweConst.SE_SATURN,
				SweConst.SE_MEAN_NODE
			};	// Some systems prefer SE_MEAN_NODE
		
		String[] planetNames =
			{	"Surya",
				"Chandra",
				"Kuja",
				"Budha",
				"Guru",
				"Shukra",
				"Shani",
				"Rahu"
			};
		double[] cusps = new double[13];
		double[] acsc = new double[10];
		
		SweDate sd = new SweDate(year,month,day,hour);
		
		sw.swe_set_sid_mode(SID_METHOD, 0, 0);
		ayanamsa = sw.swe_get_ayanamsa(sd.getJulDay());

//System.out.println("True Chitra Lahiri Ayanamsa: " + toDMS(ayanamsa)); //SID_METHOD is True Chitra Lahiri
		
		@SuppressWarnings("unused")
		int result = sw.swe_houses(sd.getJulDay(),
				iflag,
				latitude,
				longitude,
				'E',
				cusps,
				acsc);

		double d = acsc[0];
		d = d + 0.5/3600./10000.;	// round to 1/1000 of a second
		int deg = (int) d;
		d = (d - deg) * 60;
		int min = (int) d;
		d = (d - min) * 60;
		//double sec = Math.round(d * 100.0) / 100.0;

//System.out.println("Ascendant: " + toDMS(acsc[0])); 

		int ascSign = (int)(acsc[0] / 30) + 1;
		//int aschouse = (ascSign + 12 - ascSign) % 12 +1;
		
//System.out.printf("%-12s: %s  ; sign: %2d; %s\n", "Lagna", toDMS(acsc[0]), ascSign, toDMS(acsc[0] % 30));
		
		planets = new AllPlanets();
		Planet planet;
		
		planet = new Planet("Lagna", 0);
		planet.setLongitude(acsc[0]);//longitude
		planet.setLatitude(acsc[1]);//latitude
		planets.setPlanet(planet);
		
		int p;
		int nd;
		// if (zodiac.equals("sidereal")){
		// 	sw.swe_set_sid_mode( (int)ayanamsa, 0, 0);
		// }
		
		/*if (node.equals("true")) nd = 11;
		else nd = 10;*/
			//System.out.println("node = " + nd);
		
		int sign;
		@SuppressWarnings("unused")
		int house;
		@SuppressWarnings("unused")
		boolean retrograde = false;
		
		for (p = 0; p <= planetsNeeded.length-1; p++){
			/*if (nd == 11 && p == 10){
				continue;
			}*/
			int planetNo = planetsNeeded[p];
			String planetName = planetNames[p];
			
			//System.out.println("Calculating p#: " + p);
			iflgret = sw.swe_calc_ut(tjd_ut, planetNo, (int) iflag, x2, serr);
			
			if (iflgret < 0){
				//System.out.print("error: " + serr.toString() + "\n");
			}
			else if (iflgret != iflag){
				//System.out.print("warning: iflgret != iflag. " + serr.toString() + "\n");
			}
			
			//if (p <= planetsNeeded.length-1){
			snam = planetName;
			planet = new Planet(snam, p+1);
			planet.setLongitude(x2[0]);//longitude
			planet.setLatitude(x2[1]);//latitude
			planet.setVelocity(x2[3]);//velocity in longitude
			
			if(x2[3]<0) retro = 'R';
			else if(x2[3]>=0) retro = 'D';
			else retro = ' ';
			
			planet.setRetrograde(retro);
			
			planets.setPlanet(planet);
			
			sign = (int)(x2[0] / 30) + 1;
			house = (sign + 12 - ascSign) % 12 +1;
			retrograde = (x2[3] < 0);

			d = x2[0];
			d = d + 0.5/3600./10000.;	// round to 1/1000 of a second
			deg = (int) d;
			d = (d - deg) * 60;
			min = (int) d;
			d = (d - min) * 60;
			//double sec = Math.round(d * 100.0) / 100.0;
			
//System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n", planetName, toDMS(x2[0]), (retrograde ? 'R' : 'D'), sign, toDMS(x2[0] % 30), house);
			//}
		}
		snam = "Ketu";
		planet = new Planet(snam, 10);
		double rahuLong = planets.getPlanet("Rahu").getLongitude();
		double ketuLong;
		ketuLong = (rahuLong+180.)%360.;

		//System.out.printf("%f ",rahuLong+180.);
		
		planet.setLongitude(ketuLong);
		planet.setLatitude(planets.getPlanet("Rahu").getLatitude());
		planet.setVelocity(planets.getPlanet("Rahu").getVelocity());
		
		sign = (int)(ketuLong / 30) + 1;
		house = (sign + 12 - ascSign) % 12 +1;
		retrograde = true;
		
//System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n", snam, toDMS(ketuLong), (retrograde ? 'R' : 'D'), sign, toDMS(x2[0] % 30), house);
		planets.setPlanet(planet);
		return planets;
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
