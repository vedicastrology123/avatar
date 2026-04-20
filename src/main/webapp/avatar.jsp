<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page buffer="64kb" autoFlush="true" %>
<%@ page import="swisseph.SweDate" %>
<%@ page import="jyotish.main.*, jyotish.main.calc.*, time.TimeVSD" %>
<%@ page import="java.util.GregorianCalendar, java.util.Calendar, java.text.DateFormatSymbols, java.text.SimpleDateFormat, java.util.LinkedHashMap, java.util.HashMap, java.util.List, java.util.LinkedList, java.util.Map, java.util.Date" %>


<%@ page import="com.aspose.html.converters.Converter" %>
<%@ page import="com.aspose.html.saving.ImageSaveOptions" %>
<%@ page import="com.aspose.html.rendering.image.ImageFormat" %>
<%@ page import="com.aspose.html.HTMLDocument" %>
<%@ page import="jyotish.main.TeeResponseWrapper" %>

<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="com.aspose.html.rendering.image.ImageDevice" %>
<%@ page import="com.aspose.html.rendering.image.ImageRenderingOptions" %>
<%@ page import="com.aspose.html.rendering.HtmlRenderer" %>
<%@ page import="com.aspose.html.drawing.Resolution" %>

<%@ page import="io.github.bonigarcia.wdm.WebDriverManager" %>
<%@ page import="org.openqa.selenium.OutputType" %>
<%@ page import="org.openqa.selenium.TakesScreenshot" %>
<%@ page import="org.openqa.selenium.WebDriver" %>
<%@ page import="org.openqa.selenium.chrome.ChromeDriver" %>
<%@ page import="org.openqa.selenium.chrome.ChromeOptions" %>
<%@ page import="org.openqa.selenium.support.ui.WebDriverWait" %>
<%@ page import="org.openqa.selenium.support.ui.ExpectedConditions" %>
<%@ page import="org.openqa.selenium.WebElement" %>
<%@ page import="org.openqa.selenium.By" %>
<%@ page import="org.openqa.selenium.JavascriptExecutor" %>

<%@ page import="java.io.IOException" %>
<%@ page import="java.time.Duration" %>
<%@ page import="java.nio.charset.StandardCharsets" %>


<jsp:useBean id="birthdata" class="jyotish.main.BirthData" scope="request" />
<jsp:useBean id="panchang" class="jyotish.main.calc.PanchangBasics" scope="request" />
<jsp:useBean id="inputpref" class="jyotish.main.InputPreference" scope="request" />
<jsp:useBean id="housemap" class="jyotish.main.PlanetHouseMap" scope="request" />
<jsp:useBean id="chartbuilder" class="jyotish.main.ChartBuilder" scope="request" />
<jsp:useBean id="chart" class="jyotish.main.Chart" scope="request" />
<jsp:useBean id="housemapNav" class="jyotish.main.PlanetHouseMap" scope="request" />
<jsp:useBean id="vimDasha" class="time.TimeVSD" scope="request" />
<jsp:useBean id="geoCode" class="jyotish.main.calc.GeoCoder" scope="request" />
<jsp:useBean id="allPlanets" class="jyotish.main.AllPlanets" scope="request" />
<jsp:useBean id="vargCals" class="jyotish.main.calc.VargaDataCalculator" scope="request" />

<%!
    private double safeParse(String val) {
        if (val == null || val.trim().isEmpty()) return 0.0;
        try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; }
    }

    String[] planetNames = {"Lagna", "Surya", "Chandra", "Kuja", "Budha", "Guru", "Shukra", "Shani", "Rahu", "Ketu"};

	public String toDMS(double d) {
		double absD = Math.abs(d) + 0.5/3600./10000.; 
		int deg = (int) absD;
		double m = (absD - deg) * 60;
		int min = (int) m;
		int secs = (int) Math.round((m - min) * 60);

		// Carry-over logic to prevent "60" appearing in the export
		if (secs >= 60) { secs = 0; min++; }
		if (min >= 60) { min = 0; deg++; }

		// Using your updated HTML superscript format
		return String.format("%d<sup>o</sup>%02d'%02d\"", deg, min, secs);
	}

    public Calendar getGregCalDate(BirthData nativeInfo){
        int year = nativeInfo.getBirthYear();
        int mon = nativeInfo.getBirthMonth();
        int day = nativeInfo.getBirthDate();
        int hour = nativeInfo.getBirthHour();
        int min = nativeInfo.getBirthMinute();
        int sec = nativeInfo.getBirthSecond();
        double offset = nativeInfo.getTimeZoneOffset();
        double dst = nativeInfo.getDstOffset();
        Calendar birth = new GregorianCalendar(year, mon - 1, day, hour, min, sec);
        birth.add(Calendar.SECOND, (int) (-3600 * (offset + dst)));
        return birth;
    }
%>

<%
    int chartType = 0;
    String latString = "";
    String longString = "";
    String firstName = "";
    String lastName = "";
    String email = "";
    String date = "";
    String time = "";
    String city = "";
    String state = "";
    String country = "";
    String questions = "";
    String residency = "";


	String nakshatra, nakshatraPada, tithi, yoga, sunRise, vedicWeekday, karana, sunset;
	double ayanamsa;
	int nakshatraCount;
    
    try {
        String chartParam = request.getParameter("chart");
        chartType = (chartParam != null) ? Integer.parseInt(chartParam) : 0;
        pageContext.getAttribute("chartType");

        if (inputpref == null) {
            inputpref = new jyotish.main.InputPreference();
            pageContext.setAttribute("inputpref", inputpref);
        }

		firstName = request.getParameter("firstName");
		lastName = request.getParameter("lastName");
        email = request.getParameter("email");
		date = request.getParameter("date");
		time = request.getParameter("time");
		city = request.getParameter("cityid");
		state = request.getParameter("stateid");
		country = request.getParameter("countryid");
		questions = request.getParameter("questions");
        residency = request.getParameter("residency");

		String countryLatitude = request.getParameter("countryLatitude");
		String countryLongitude = request.getParameter("countryLongitude");
		String countryGmtOffsetName = request.getParameter("countryGmtOffsetName");
		String countryGmtOffset = request.getParameter("countryGmtOffset");
		String countryDstOffset = request.getParameter("countryDstOffset");
		String cityLatitude = request.getParameter("cityLatitude");
		String cityLongitude = request.getParameter("cityLongitude");
		String cityGmtOffsetName = request.getParameter("cityGmtOffsetName");
		String cityGmtOffset = request.getParameter("cityGmtOffset");
		String cityDstOffset = request.getParameter("cityDstOffset");

        if (birthdata == null) {
            birthdata = new jyotish.main.BirthData();
            pageContext.setAttribute("birthdata", birthdata);
        }

		birthdata.setFirstName(firstName);
		birthdata.setLastName(lastName);

        String[] dateVal = date.split("-");
		String[] timeVal = time.split(":");

		birthdata.setBirthDate(Integer.parseInt(dateVal[2]));
		birthdata.setBirthMonth(Integer.parseInt(dateVal[1]));
		birthdata.setBirthYear(Integer.parseInt(dateVal[0]));
		birthdata.setBirthHour(Integer.parseInt(timeVal[0]));
		birthdata.setBirthMinute(Integer.parseInt(timeVal[1]));
		birthdata.setBirthSecond(0);
		
		String latitude="", longitude="", gmtOffsetName="", gmtOffset="", dstOffset="";
		
		if (city.isEmpty() && state.isEmpty() && !country.isEmpty()) { // Aland Islands
			state = country;
			city = country;
									//System.out.println("1 state " + state + " city " + city);	
			latitude = countryLatitude;
			longitude = countryLongitude;
			gmtOffsetName = countryGmtOffsetName;
			gmtOffset = countryGmtOffset;
			dstOffset = countryDstOffset;
		}
		else if (city.isEmpty() && !state.isEmpty() && !country.isEmpty()) { // India, Chandigarh, with NO city-entry
			city = state;
			latitude = countryLatitude;
			longitude = countryLongitude;
			gmtOffsetName = countryGmtOffsetName;
			gmtOffset = countryGmtOffset;
			dstOffset = countryDstOffset;
		}
		else if (city.equalsIgnoreCase(state) && !country.isEmpty()) { // India, Chandigarh, with city-entry as Chandigarh
			latitude = countryLatitude;
			longitude = countryLongitude;
			gmtOffsetName = countryGmtOffsetName;
			gmtOffset = countryGmtOffset;
			dstOffset = countryDstOffset;
		}
		else {
			latitude = cityLatitude;
			longitude = cityLongitude;
			gmtOffsetName = cityGmtOffsetName;
			gmtOffset = cityGmtOffset;
			dstOffset = cityDstOffset;			
		}


		birthdata.setBirthCity(city);
		birthdata.setBirthState(state);
		birthdata.setBirthCountry(country);
		
		double latitudeVal = safeParse(latitude);
		double longitudeVal = safeParse(longitude);

		birthdata.setLatitude(latitudeVal);
		birthdata.setLongitude(longitudeVal);

		birthdata.setTimeZoneOffset(safeParse(gmtOffset));
		birthdata.setDstOffset(safeParse(dstOffset));
		birthdata.setGmtOffsetName(gmtOffsetName);
		
		if (geoCode == null) {
			geoCode = new jyotish.main.calc.GeoCoder();
			pageContext.setAttribute("geoCode", geoCode);
		}
		geoCode.getGeoZoneInfo(birthdata);
	
		if (latitudeVal < 0.) {
			birthdata.setIsSouth(true);
			birthdata.setIsNorth(false);
		}
		else {
			birthdata.setIsSouth(false);
			birthdata.setIsNorth(true);
		}
		
		if (longitudeVal < 0.) {
			birthdata.setIsWest(true);
			birthdata.setIsEast(false);
		}
		else {
			birthdata.setIsWest(false);
			birthdata.setIsEast(true);
		}

		if (chartbuilder == null) {
			chartbuilder = new jyotish.main.ChartBuilder();
			pageContext.setAttribute("chartbuilder", chartbuilder);
		}
		chartbuilder.setCalculationPreferences(); // Don't forget your default settings!
		
		chart = chartbuilder.calculateChart(birthdata,inputpref);
		pageContext.setAttribute("chart", chart);

		if (panchang == null) {
			panchang = new jyotish.main.calc.PanchangBasics();
			pageContext.setAttribute("panchang", panchang);
		}
		panchang = chart.getPanchangBasics();
		
		nakshatra = panchang.getNakshathra();
		nakshatraPada = panchang.getPada();
		tithi = panchang.getTithi();
		yoga = panchang.getYoga();
		ayanamsa = panchang.getAyanamsa();
		sunRise = panchang.getSunriseString();
		nakshatraCount = panchang.getNakshatra_count();
		vedicWeekday = panchang.getDay();
		karana = panchang.getKarana();
		sunset = panchang.getSunsetString();;
		
		Boolean latDir = birthdata.getIsNorth();

        latString = toDMS(Math.abs(birthdata.getLatitude())) + (birthdata.getLatitude() >= 0 ? " N" : " S");
        longString = toDMS(Math.abs(birthdata.getLongitude())) + (birthdata.getLongitude() >= 0 ? " E" : " W");
        pageContext.setAttribute("latString", latString);
        pageContext.setAttribute("longString", longString);

        int[][] vargaData = chart.getVargaData().getVargaData();
        for(int i=0; i<10; i++) {
            housemap.put(vargaData[0][i], housemap.get(vargaData[0][i]) + " " + planetNames[i]);
            housemapNav.put(vargaData[5][i], housemapNav.get(vargaData[5][i]) + " " + planetNames[i]);
        }
        allPlanets = chart.getPlanetInfo();
        
    } catch (Exception e) {
        System.err.println("Birthchart Logic Error: " + e.getMessage());
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${birthdata.firstName} ${birthdata.lastName} - Vedic Report</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html-to-image/1.11.11/html-to-image.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
<style>
    /* 1. RESET & LOCKDOWN */
    * {
        box-sizing: border-box !important;
        margin: 0;
        padding: 0;
    }

      .page-container {
          width: 1050 !important;
          min-width: 1050px !important;
          max-width: 1050px !important;
          height: 1080px !important;
          min-height: 1080px !important;
          background: #ffffff;
          margin: 0 auto;
          /* Prevents background bleed and scrollbar artifacts */
          overflow: hidden;
          position: relative;
      }

    /* 2. CHART GRID - Locked to prevent expansion */
    .chart-grid {
        display: grid !important;
        grid-template-columns: repeat(4, 85px) !important;
        grid-template-rows: repeat(4, 85px) !important;
        width: 342px !important; 
        height: 342px !important;
        margin: 0 auto !important;
        border: 1.5px solid #0f172a !important;
        background: #ffffff !important;
    }

    .house-box {
        width: 85px !important;
        height: 85px !important;
        border: 0.5px solid #0f172a !important;
        display: flex !important;
        flex-direction: column !important;
        align-items: center !important;
        justify-content: center !important;
        overflow: hidden !important;
    }

    .chart-center {
        grid-column: 2 / span 2 !important;
        grid-row: 2 / span 2 !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        font-weight: 900 !important;
        font-size: 1.5rem !important;
        border: 0.5px solid #0f172a !important;
    }

    .planet-data {
        font-size: 12px !important;
        line-height: 1.0 !important;
        text-align: center !important;
        color: #000000 !important;
    }

    /* 4. THE FOOTER - Hard Pinned */
    .absolute-footer {
        position: absolute !important;
        bottom: 40px !important;
        left: 40px !important;
        right: 40px !important;
        height: 60px !important;
        background: #ffffff !important;
        border-top: 3px solid #0f172a !important;
        z-index: 100 !important;
        display: block !important;
    }

    .footer-line {
        height: 1px;
        background: #e2e8f0;
        margin-bottom: 12px;
    }

    /* 5. VISIBILITY CONTROL */
    .hidden, #loading-overlay, #loading-modal {
        display: none !important;
    }

    /* 2. UI Elements - Visible on screen, Hidden in Export */
    .print-ui-only {
        display: inline-block !important; /* Forces it to show on screen */
    }

    /* 3. Media Query for Export/Print Logic */
    @media print {
        .print-ui-only {
            display: none !important; /* Hides it in the final image */
        }
        table thead th {
            background-color: #0ea5e9 !important;
        }
    }

    /* 4. Color & Export Correction */
    .bg-sky-600 { 
        background-color: #0284c7 !important;
        color: #ffffff !important; 
    }
    
    tr[style*="background-color"] {
        background-color: #0ea5e9 !important;
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
    }
    /* 3. TABLE STYLING - Force compactness */
    table {
        width: 100% !important;
        border-collapse: collapse !important;
        table-layout: fixed !important;
    }
    table thead th {
        background-color: #0ea5e9 !important; /* Sky 500 */
        color: #ffffff !important;
        padding: 8px !important;
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
    }
   
    table td {
        border: 1px solid #e2e8f0 !important;
        padding: 6px !important;
    }
    #loading-overlay {
        z-index: 9999 !important;
        position: fixed !important;
    }
    #loading-modal {
        z-index: 10000 !important;
        position: fixed !important;
    }
</style>
</head>
<body class="bg-slate-100 p-4">
    <div id="loading-overlay" class="hidden fixed inset-0 bg-slate-900/80 z-50 flex flex-col backdrop-blur-md items-center justify-center text-white">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-white mb-4"></div>
        <p class="text-xl font-semibold">Emailing Your horoscope..., Please wait.</p>
    </div>

    <div id="loading-modal" class="hidden fixed inset-0 z-50 flex items-center justify-center bg-slate-900/80 backdrop-blur-md">
        <div class="bg-white p-10 rounded-3xl shadow-[0_35px_60px_-15px_rgba(0,0,0,0.5)] border-2 border-teal-500 text-center max-w-lg h-32 mx-4">                       
            <h2 id="ok-head" class="text-2xl font-black text-slate-900">Sending Email</h2>
            <p id="ok-msg" class="text-slate-500 mt-3 font-medium">Chat to get predictions, Check your email .</p>
            <button id="ok-btn" type="button" 
                    style="display: none !important; background: #0d9488 !important; color: white !important; margin: 20px auto; padding: 10px 30px; border-radius: 99px; font-weight: bold;">
                Done
            </button>
            <div id="modal-spinner" class="relative inline-block mb-6">
                <div class="w-16 h-16 border-4 border-teal-100 rounded-full"></div>
                <div class="absolute top-0 left-0 w-16 h-16 border-4 border-teal-600 border-t-transparent rounded-full animate-spin"></div>
            </div>
        </div>
    </div>    
    <div id="page-container" class="page-container">       

        <div class="border-b-2 border-slate-900 pb-16 mt-8 flex justify-between items-end h-28">
            <div id="birth-header-info" class="flex flex-col justify-end">
                <h1 class="text-2xl font-black camelcase tracking-[0.3em] text-slate-700 leading-none">
                    <%= birthdata.getFirstName() %> <%= birthdata.getLastName() %>
                </h1>

                <div class="h-10">
                    <a href="https://stevehora.com" class="print-ui-only text-[15px] font-bold text-indigo-600 camelcase tracking-widest hover:text-indigo-800">
                        stevehora.com
                    </a>
                </div>
            </div>
            
            <div class="flex items-center gap-4 mb-1">
                <span class="text-[15px] font-bold text-slate-400 camelcase tracking-widest text-right"></span>
                <div class="h-10">
                    <button onclick="downloadImage()" 
                            class="print-ui-only bg-sky-600 text-white camelcase tracking-widest h-12 rounded-md px-4 py-2 hover:bg-slate-700 transition-colors font-bold shadow-lg flex items-center justify-center mx-auto">
                        Print / Export Image
                    </button>
                </div>
            </div>
        </div>

        <div class="flex flex-row gap-12 items-start pb-6 pt-6">
            
            <div class="flex-1 space-y-3">
                
                <div class="bg-slate-50 p-4 rounded border border-slate-200 shadow-sm text-xs">
                    <div class="space-y-1">
                        <p><span class="font-bold text-slate-700 text-[15px] w-12 inline-block">Born:</span> <span class="text-[15px] text-slate-700"><%= birthdata.getBirthDate() %>-<%= new java.text.DateFormatSymbols().getShortMonths()[birthdata.getBirthMonth()-1] %>-<%= birthdata.getBirthYear() %></span></p>
                        <p><span class="font-bold text-slate-700 text-[15px] w-12 inline-block">Time:</span> <span class="text-[15px] text-slate-700">${birthdata.birthHour}:${birthdata.birthMinute}</span></p>
                        <p><span class="font-bold text-slate-700 text-[15px] w-12 inline-block">Place:</span> <span class="text-[15px] text-slate-700"><%= birthdata.getBirthCity() %>, <%= birthdata.getBirthState() %>, <%= birthdata.getBirthCountry() %>.</span></p>
                    </div>
                    <div class="mt-2 pt-2 border-t border-slate-200 text-indigo-700 font-mono text-[15px] flex gap-2">
                        <span>Lat: <%= latString %></span> • <span>Long: <%= longString %></span> • <span>GMT: <%= birthdata.getTimeZoneOffset() %></span>
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-2">
                    <div class="bg-white p-3 rounded border border-slate-200 shadow-sm flex flex-col">
                        <span class="text-slate-500 text-[15px] font-bold camelcase tracking-wider">Nakshatra</span>
                        <span class="text-[15px] font-bold text-slate-700"><%= panchang.getNakshathra() %></span>
                        <span class="text-[15px] text-slate-600">Pada: <%= panchang.getPada() %> • #<%= panchang.getNakshatra_count() %></span>
                    </div>

                    <div class="bg-white p-3 rounded border border-slate-200 shadow-sm flex flex-col">
                        <span class="text-slate-500 text-[15px] font-bold camelcase tracking-wider">Tithi & Day</span>
                        <span class="text-[15px] font-bold text-slate-700"><%= panchang.getTithi() %></span>
                        <span class="text-[15px] text-slate-600"><%= panchang.getDay() %></span>
                    </div>

                    <div class="bg-white p-3 rounded border border-slate-200 shadow-sm flex flex-col justify-center">
                        <div class="flex gap-2 items-baseline">
                            <span class="text-slate-700 text-[15px] font-bold">Yoga:</span>
                            <span class="text-[15px] text-slate-700"><%= panchang.getYoga() %></span>
                        </div>
                        <div class="flex gap-2 items-baseline">
                            <span class="text-slate-700 text-[15px] font-bold">Karana:</span>
                            <span class="text-[15px] text-slate-700"><%= panchang.getKarana() %></span>
                        </div>
                    </div>

                    <div class="bg-white p-3 rounded border border-slate-200 shadow-sm flex flex-col justify-center">
                        <div class="text-[15px] text-slate-600 font-medium">↑ <%= panchang.getSunriseString() %> • ↓ <%= panchang.getSunsetString() %></div>
                        <div class="mt-1 text-[15px] font-bold text-blue-700 border-t border-blue-50 pt-1 w-full camelcase tracking-tighter">
                            Ayanamsa: <%= toDMS(panchang.getAyanamsa()) %>
                        </div>
                    </div>
                </div>
            </div>

            <div class="w-[500px] flex-none">
                <table class="shadow-sm border border-slate-200 w-full text-[15px] bg-white rounded overflow-hidden">
                    <thead>
                        <tr class="bg-slate-900 text-white" style="background-color: #0f172a !important; color: white !important;">
                            <th class="p-2 text-left camelcase tracking-wider">Graha</th>
                            <th class="p-2 text-left camelcase tracking-wider">Longitude</th>
                            <th class="p-2 text-left camelcase tracking-wider">Nakshatra</th>
                            <th class="p-2 text-left camelcase tracking-wider">Rashi</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100">


                    <% for(int i = 0; i < planetNames.length; i++) { 
                        Planet p = allPlanets.getPlanet(planetNames[i]); 
                        if(p != null) { %>
                    <tr class="hover:bg-slate-50">
                        <td class="p-2 font-bold text-slate-700"><% String name=p.getPlanetName(); out.print((name!=null && !name.trim().isEmpty())?name.trim():"-"); %><%= p.getRetrograde() != 'D' ? " (R)" : "" %></td>
                        <td class="p-2 font-mono text-blue-700"><% String dms=toDMS(p.getLongitude()); out.print((dms!=null && !dms.trim().isEmpty())?dms.trim():"-"); %></td>
                        <td class="p-2 text-slate-700"><% String nak=p.getNakshatra(); out.print((nak!=null && !nak.trim().isEmpty())?nak.trim():"-"); %></td>
                        <td class="p-2 text-slate-700"><% String rashi=Constants.rashiNames[vargCals.getRasi(p.getLongitude())-1]; out.print((rashi!=null && !rashi.trim().isEmpty())?rashi.trim():"-"); %></td>
                    </tr>
                    <% } } %>                       
                    </tbody>
                </table>
            </div>
        </div>

        <div class="flex flex-row justify-center items-start pt-12 mb-12 border-t-4 border-slate-900 gap-16">
                
            <div class="text-center">
                <h3 class="text-[15px] font-black camelcase tracking-[0.2em] mb-4 text-slate-800">Rashi Chart (D1)</h3>
                <div class="chart-grid">
                    <% int[] rashiMap = {12,1,2,3,11,4,10,5,9,8,7,6};
                    for(int i=0; i<12; i++) { 
                        if(i==4) { %><div class="chart-center">D1</div><% } %>
                            <div class="house-box">
                                <div class="planet-data">
                                <%
                                String pData = (housemap != null) ? housemap.get(rashiMap[i]) : null;
                                String pClean = (pData != null) ? pData.trim() : "";
                                if (pClean.isEmpty()) {
                                    out.print("<span class='opacity-0'>.</span>"); // Better than white color
                                } else {
                                    // Replace spaces with <br/> or ensure a space exists to trigger wrapping
                                    out.print(pClean.replace(" ", "<br/>")); 
                                }
                                %>
                            </div>
                        </div>            
                    <% } %>
                </div>
            </div>

            <div class="text-center">
                <h3 class="text-[15px] font-black camelcase tracking-[0.2em] mb-4 text-slate-800">Navamsa Chart (D9)</h3>
                <div class="chart-grid">
                    <% int[] navMap = {12,1,2,3,11,4,10,5,9,8,7,6};
                    for(int i=0; i<12; i++) { 
                        if(i==4) { %><div class="chart-center">D9</div><% } %>
                            <div class="house-box">
                                <div class="planet-data">
                                <%
                                String pNavData = (housemapNav != null) ? housemapNav.get(navMap[i]) : null;
                                String navClean = (pNavData != null) ? pNavData.trim() : "";
                                if (navClean.isEmpty()) {
                                    out.print("<span class='opacity-0'>.</span>"); // Better than white color
                                } else {
                                    // Replace spaces with <br/> or ensure a space exists to trigger wrapping
                                    out.print(navClean.replace(" ", "<br/>")); 
                                }
                                %>
                            </div>
                        </div>            
                    <% } %>
                </div>
            </div>
        </div>
        <div class="absolute-footer">
            <div class="footer-line"></div>
            <div class="flex justify-between items-center px-2">
                <div class="text-[14px] font-bold text-slate-900">
                    Verified by Steve Hora 
                </div>
                
                <div class="text-[14px] text-slate-900 text-right">
                    <span class="text-indigo-600 font-black">stevehora.com</span> | 15-Apr-2026 | Powered by Swiss Ephemeris 2.0
                </div>
            </div>
        </div>
    </div>

    <script>
        async function downloadImage() {
            const proceed = confirm("Would you like to generate and download the horoscope image?");
            if (!proceed) return;

            // Correct check for the cdnjs global object
            const lib = window.htmlToImage;
            if (!lib) {
                alert("The image library hasn't loaded yet. Please wait a moment or refresh.");
                return;
            }

            const node = document.getElementById('page-container');
            const loader = document.getElementById('loading-overlay');
            
            try {
                loader.classList.remove('hidden');

                // Capture logic locked to your 1050x1080 dimensions
                const dataUrl = await lib.toPng(node, {
                    width: 1485,
                    height: 1080,
                    backgroundColor: '#ffffff',
                    pixelRatio: 1, // Forces 1:1 scale to prevent height shrinking
                    filter: (node) => {
                        // Properly exclude UI elements from the export
                        if (node.classList && (node.classList.contains('print-ui-only') || node.tagName === 'BUTTON')) {
                            return false;
                        }
                        return true;
                    }
                });

                const name = "<%= firstName %>";
                const link = document.createElement('a');
                link.download = name + '-horoscope.png';
                link.href = dataUrl;
                link.click();

            } catch (error) {
                console.error("Capture failed:", error);
                alert("Capture failed. Error: " + error.message);
            } finally {
                loader.classList.add('hidden');
            }
        }
    </script>
    <script>
        const overlay = document.getElementById('loading-overlay');
        overlay.style.setProperty('display', 'flex', 'important');
        overlay.classList.remove('hidden');
    </script>
    <%@ page import="java.net.http.HttpClient" %>
    <%@ page import="java.net.http.HttpRequest" %>
    <%@ page import="java.net.http.HttpResponse" %>
    <%@ page import="java.net.URI" %>
    <%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
    
    <%!
        public byte[] processAndRender(String finalHtml) throws Exception {
            // 2. Wrap in JSON for the Sidecar
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("html", finalHtml));

            // 3. Push to the Local Satori Sidecar
            // String token = System.getenv("SIDECAR_TOKEN");
            String token = "ibh7JSXJPdWu4DBq";
            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:3000/render"))
                .header("Content-Type", "application/json")
                .header("X-Sidecar-token", token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            byte[] imageBytes = null;

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                imageBytes = response.body();
                long expectedSize = response.headers().firstValueAsLong("Content-Length").orElse(-1L);
                int receivedSize = (imageBytes != null) ? imageBytes.length : 0;
                    if (receivedSize > 0 && (expectedSize == -1 || receivedSize == expectedSize)) {
                        System.out.println("✅ Integrity Verified: Received " + receivedSize + " bytes.");
                        return imageBytes; // Your final Vedic Chart PNG
                    } else {
                        System.out.println("❌ Integrity Failed: Expected " + expectedSize + " but got " + receivedSize);
                    }
            } else {
                String errorInfo = new String(response.body());
                System.out.println("Satori Error: " + response.statusCode() + " - " + errorInfo);
            }
            return imageBytes;
        }
    %>

    <%
        out.flush();
        String success = "";
        jyotish.main.TeeResponseWrapper wrapperAttr = (jyotish.main.TeeResponseWrapper) request.getAttribute("TeeResponseWrapper");

        String capturedHtml = "";
        
        if (wrapperAttr != null && wrapperAttr instanceof jyotish.main.TeeResponseWrapper) {
            jyotish.main.TeeResponseWrapper myWrapper = (jyotish.main.TeeResponseWrapper) wrapperAttr;

            capturedHtml = myWrapper.getCapturedHtml().toString().trim();

            if (capturedHtml != null && !capturedHtml.trim().isEmpty()) {
                System.out.println("Verified: HTML written to screen block.");
            } else {
                out.println("<b>Debug: capturedHtml was NULL at time of rendering.</b>");
            }
        } else {
            System.err.println("Error: TeeResponseWrapper not found in request attributes.");
        }
        // Java side
        String json = "{\"html\": \"" + capturedHtml.replace("\"", "\\\"") + "\"}";
        byte[] imageBytes = processAndRender(json);
        if (email != null && !email.isEmpty()) {
            jyotish.main.SendUserEmail emailer = new jyotish.main.SendUserEmail();
            System.out.println("Email sent out to " + firstName + " using satori twcss");
            emailer.generateAndEmail(email, imageBytes, firstName, lastName, date, time, city, state, country, questions, residency);
            success = "Success";
        }
        
        /* WebDriver driver = null;
        
        try {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-setuid-sandbox");        
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080"); 
            options.addArguments("--force-device-scale-factor=1"); 
            options.addArguments("--hide-scrollbars");
            options.addArguments("--blink-settings=primaryHoverType=2,availableHoverTypes=2,primaryPointerType=4,availablePointerTypes=4");
            options.setBinary("/usr/bin/google-chrome");
            
            driver = new ChromeDriver(options);
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("width", 1920);
            metrics.put("height", 1200);
            metrics.put("deviceScaleFactor", 1);
            metrics.put("mobile", false); // This prevents the 'shrunken' mobile layout
            metrics.put("pixelRatio", 1.0);
            metrics.put("format", "png");
            metrics.put("fromSurface", true);
            metrics.put("captureBeyondViewport", true);

            ((ChromeDriver) driver).executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);

            String base64Html = java.util.Base64.getEncoder().encodeToString(capturedHtml.getBytes(StandardCharsets.UTF_8));

            driver.get("data:text/html;charset=utf-8;base64," + base64Html);                       

            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript(
                "var modal = document.getElementById('loading-modal'); if(modal) modal.style.display='none';" +
                "var overlay = document.getElementById('loading-overlay'); if(overlay) overlay.style.display='none';" +
                "var ui = document.getElementsByClassName('print-ui-only'); " +
                "for(var i=0; i<ui.length; i++) { ui[i].style.display='none'; }" + 
                "var hidden = document.querySelectorAll('.hidden, [class*=\"md:hidden\"]'); for(var i=0; i<hidden.length; i++) hidden[i].remove();"
            );

            Thread.sleep(1000); 

            WebElement chartElement = driver.findElement(By.tagName("body"));
            byte[] imageBytes = chartElement.getScreenshotAs(OutputType.BYTES);

            if (email != null && !email.isEmpty()) {
                jyotish.main.SendUserEmail emailer = new jyotish.main.SendUserEmail();
                System.out.println("Email sent out to " + firstName);
                emailer.generateAndEmail(email, imageBytes, firstName, lastName, date, time, city, state, country, questions, residency);
                success = "Success";
            }
        } catch (Exception e) {
            System.out.println("bytes not there ..." + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }*/
    %>

    <script>
        var successJs = "<%= success %>";
        // Assume JSP sets a hidden input or session attribute
        if(successJs === "Success") {

            const overlay = document.getElementById('loading-overlay');
            if (overlay) {
                overlay.classList.add('hidden');
                overlay.classList.toggle('hidden');
            }

            const modal = document.getElementById('loading-modal');
            if (modal) {
                modal.style.setProperty('display', 'flex', 'important');
                modal.classList.remove('hidden');
            }

            const btn = document.getElementById('ok-btn');
            const head = document.getElementById('ok-head');
            const msg = document.getElementById('ok-msg');
            const spinner = document.getElementById('modal-spinner');

            if (btn) {
                if (spinner) spinner.style.display = 'none';
                head.innerText = "Check your email.";
                msg.innerText = "Chat to get predictions.";
            
                btn.style.setProperty('display', 'block', 'important');
                btn.onclick = function() {
                    document.getElementById('loading-overlay').style.display = 'none';
                    document.getElementById('loading-modal').style.display = 'none';
                };
            }
        }
    </script>
</body>
</html>