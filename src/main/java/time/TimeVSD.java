package time;

//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jyotish.main.BirthData;
import jyotish.main.Chart;
import jyotish.main.ChartBuilder;
import jyotish.main.InputPreference;
import jyotish.main.calc.PanchangBasics;

public class TimeVSD {
	String[] grahaNames = { "Ketu",	"Shukra", "Surya", "Chandra", "Kuja", "Rahu", "Guru", "Shani", "Budha"};
	LinkedHashMap<String, List<LinkedHashMap<String, Double>>> AllGrahaDashas = new LinkedHashMap<String, List<LinkedHashMap<String, Double>>>();

	public LinkedHashMap<String, List<LinkedHashMap<String, Double>>> getAllGrahaDashas() {
		return AllGrahaDashas;
	}
	public void setAllGrahaDashas(LinkedHashMap<String, List<LinkedHashMap<String, Double>>> allGrahaDashas) {
		AllGrahaDashas = allGrahaDashas;
	}
	public TimeVSD() {
		
	}
	public double vimTime(BirthData birthdata, PanchangBasics panchang, int chartType) {
		
		InputPreference inputpref= new InputPreference();
		inputpref.setType(chartType);
		ChartBuilder chartbuilder = new ChartBuilder();
		chartbuilder.setCalculationPreferences();
		Chart chart = chartbuilder.calculateChart(birthdata,inputpref);
		//double birthDate = chartbuilder.getTJD_UT();

		int nakCount = panchang.getNakshatra_count();
		int nak120Count = 0;
		if (nakCount > 9) {
			nak120Count = nakCount % 9;
			if (nak120Count == 0) {
				nak120Count = 9;
			}
		}
		else {
			nak120Count = nakCount;
		}
//System.out.println("nak120Count " + nak120Count);

		int startDashaYears = getDasha(grahaNames[nak120Count-1]);
//System.out.println("grahaNames " + grahaNames[nak120Count-1]);
		double longPlanet = chart.getPlanetInfo().getPlanet(grahaNames[3]).getLongitude();
		double nak = (40./3.) * nakCount;
		double balance = nak - longPlanet;
		double startBalanceYears = (double) startDashaYears * balance / (40./3.);
//System.out.println(startBalanceYears);
		
		double yearVals =  365.24217; //365.2422; 365.24217 365.256364 360.0;
		double monthVals = yearVals / 12.0;
		//double dayVals = monthVals / (yearVals * monthVals);
		
		int ys = (int) startBalanceYears;
		double ms = (startBalanceYears - ys) * 12.;
		int mons = (int) ms;
		double days = ((ms - mons) * monthVals);
		int ds = (int) days;
		double hours = (days - ds) * 24.;
		int hrs = (int) hours;
		double min = (hours - hrs) * 60.;
		int mins = (int) min;
		double sec = (min - mins) * 60.;
		int secs = (int) sec;
System.out.println("Balance Dasha of " + grahaNames[nak120Count-1] + " is "+ ys + " years " +  mons + " months "+ ds + " days "+ hrs + " hour "+ mins + " mins "+ secs + " secs.");
		
//System.out.println("startDashaYears " + startDashaYears);
		
		double bBackBalance = startDashaYears - startBalanceYears;
		
		generateBalanceSubDasas(nak120Count);
		
		return bBackBalance;
	}

	public void generateBalanceSubDasas(int nak120Count) {
		
		double dashaDiv = 120.;
		int startSubDashaYears = 0;
		double subDasa = 0;
		double startDashaYrs = getDasha(grahaNames[nak120Count-1]);

		LinkedHashMap<String, Double> graha1stSubDasha = new LinkedHashMap<String, Double>();
		
		List<LinkedHashMap<String, Double>> Graha1stSubDashas = new ArrayList<LinkedHashMap<String, Double>>();
		
		LinkedHashMap<String, List<LinkedHashMap<String, Double>>> AllGrahaDashas = new LinkedHashMap<String, List<LinkedHashMap<String, Double>>>();
		
		for (int ds=nak120Count-1;ds<grahaNames.length;ds++) {
			startSubDashaYears = getDasha(grahaNames[ds]);
			subDasa = (startDashaYrs * startSubDashaYears) / dashaDiv;
			graha1stSubDasha.put(grahaNames[ds], subDasa);
			//System.out.println("sMahadasha " + grahaNames[ds] + "subDasha " + subDasa);
		}
		for (int ds=0;ds<nak120Count-1;ds++) {
			startSubDashaYears = getDasha(grahaNames[ds]);
			subDasa = (startDashaYrs * startSubDashaYears) / dashaDiv;
			
			graha1stSubDasha.put(grahaNames[ds], subDasa);

			//System.out.println("remMahadasha " + grahaNames[ds] + "subDasha " + subDasa);
		}
		Graha1stSubDashas.add(graha1stSubDasha);
		//System.out.println("rem Mahadasha " + grahaNames[nak120Count-1]);
		AllGrahaDashas.put(grahaNames[nak120Count-1], Graha1stSubDashas);
		int nextGrahaDasha = nak120Count-1;
//System.out.println("nak120Count " + nak120Count);
		int nextD = 0;
		for (int remD=nextGrahaDasha;remD<(nextGrahaDasha+9);++remD) {
			//++nextGrahaDasha;
			
			
			if (remD >= 9){ nextD = remD - 9; } else { nextD = remD; };
			
			
//System.out.println("nextGrahaDasha of " + nextGrahaDasha + " name " + grahaNames[nextGrahaDasha]);
			//nextGrahaDasha = nextGrahaDasha+1;
			addOtherRemDashas(nextD);
			//nextGrahaDasha++;
		}
	}
	//System.out.println("subDashaNo " + subDashaNo  + "subDasha[subDashaNo] " +subDasha[subDashaNo]);

	public void addOtherRemDashas(int nextGrahaDasha) {
		double dashaDiv = 120.;
		int startSubDashaYears = 0;
		double subDasa = 0;
		double startDashaYrs = getDasha(grahaNames[nextGrahaDasha]);
		LinkedHashMap<String, Double> graha1stSubDasha = new LinkedHashMap<String, Double>();
		
		List<LinkedHashMap<String, Double>> Graha1stSubDashas = new LinkedList<LinkedHashMap<String, Double>>();
				
		for (int ds=nextGrahaDasha;ds<grahaNames.length;ds++) {
			startSubDashaYears = getDasha(grahaNames[ds]);
			subDasa = (startDashaYrs * startSubDashaYears) / dashaDiv;

			graha1stSubDasha.put(grahaNames[ds], subDasa);
			
//System.out.println("Sub dasha " + grahaNames[ds] + "of mahadasa " + grahaNames[nextGrahaDasha]);
		}

		for (int ds=0;ds<nextGrahaDasha;ds++) {
			startSubDashaYears = getDasha(grahaNames[ds]);
			subDasa = (startDashaYrs * startSubDashaYears) / dashaDiv;
			
			graha1stSubDasha.put(grahaNames[ds], subDasa);

//System.out.println("remSub dasha " + grahaNames[ds] + "of mahadasa " + grahaNames[nextGrahaDasha]);
		}
		Graha1stSubDashas.add(graha1stSubDasha);
		AllGrahaDashas.put(grahaNames[nextGrahaDasha], Graha1stSubDashas);	
	}
	
	public Integer getDasha(String startGrahaName) {
		
		Map<String, List<Integer>> GrahaDashas = new HashMap<String, List<Integer>>();
		List<Integer> ketuValues = new ArrayList<Integer>();
		ketuValues.add(0);
		ketuValues.add(7);
		GrahaDashas.put("Ketu", ketuValues);
		List<Integer> shukraValues = new ArrayList<Integer>();
		shukraValues.add(1);
		shukraValues.add(20);
		GrahaDashas.put("Shukra", shukraValues);
		List<Integer> suryaValues = new ArrayList<Integer>();
		suryaValues.add(2);
		suryaValues.add(6);
		GrahaDashas.put("Surya", suryaValues);
		List<Integer> chandraValues = new ArrayList<Integer>();
		chandraValues.add(3);
		chandraValues.add(10);
		GrahaDashas.put("Chandra", chandraValues);
		List<Integer> kujaValues = new ArrayList<Integer>();
		kujaValues.add(4);
		kujaValues.add(7);
		GrahaDashas.put("Kuja", kujaValues);
		List<Integer> rahuValues = new ArrayList<Integer>();
		rahuValues.add(5);
		rahuValues.add(18);
		GrahaDashas.put("Rahu", rahuValues);
		List<Integer> guruValues = new ArrayList<Integer>();
		guruValues.add(6);
		guruValues.add(16);
		GrahaDashas.put("Guru", guruValues);
		List<Integer> shaniValues = new ArrayList<Integer>();
		shaniValues.add(7);
		shaniValues.add(19);
		GrahaDashas.put("Shani", shaniValues);
		List<Integer> budhaValues = new ArrayList<Integer>();
		budhaValues.add(8);
		budhaValues.add(17);
		GrahaDashas.put("Budha", budhaValues);
		
		// to get the arraylist
		//System.out.println("GrahaDashas.get(startGrahaName).get(1) " + GrahaDashas.get(startGrahaName).get(1));
		return GrahaDashas.get(startGrahaName).get(1);
	}
}
