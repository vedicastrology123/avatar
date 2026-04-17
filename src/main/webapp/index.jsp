<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.Date" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Vedic Astrology - Steve Hora</title>
    <link rel="icon" href="img/favicon.png" type="image/png">
    
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700&display=swap" rel="stylesheet">
    
    <%-- <script src="https://unpkg.com/@tailwindcss/browser@4"></script> --%>
    <%-- <link href="/css/style.css" rel="stylesheet"> --%>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="js/countrystatecity.js"></script>
    <script src="js/bigcloud.js"></script>

    <style>
        body { font-family: 'Nunito', sans-serif; }
    </style>

    <script>
        window.onload = function() {
            const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
            document.getElementById('userTz').value = tz;

            const now = new Date();
            document.getElementById('date').value = now.toISOString().split('T')[0];
            document.getElementById('time').value = now.toTimeString().split(' ')[0].substring(0, 5);
        };
    </script>
</head>

<body class="bg-slate-50 text-slate-800 antialiased min-h-screen flex items-center justify-center p-4">

    <div class="max-w-2xl w-full bg-white shadow-xl rounded-2xl p-8 border border-slate-100">
        
        <header class="mb-8 border-b border-slate-100 pb-6">
            <h1 class="text-3xl font-bold text-slate-900">Vedic Astrology</h1>
            <p class="text-slate-500 mt-1 text-lg">Birth Horoscope & Kundali Generation</p>
        </header>

        <form id="birthchartInput" name="birthchartInput" action="avatar.jsp" method="post" enctype="application/x-www-form-urlencoded" class="space-y-6">
            <input type="hidden" id="userTz" name="userTz">
            <input type="hidden" id="chart" name="chart" value="0">
            <input type="hidden" id="lastName" name="lastName" value="- Birth Horoscope">
            <input type="hidden" id="countryLatitude" name="countryLatitude" value="">
            <input type="hidden" id="countryLongitude" name="countryLongitude" value="">
            <input type="hidden" id="countryGmtOffsetName" name="countryGmtOffsetName" value="">
            <input type="hidden" id="countryGmtOffset" name="countryGmtOffset" value="">
            <input type="hidden" id="countryDstOffset" name="countryDstOffset" value="">
            <input type="hidden" id="cityLatitude" name="cityLatitude" value="">
            <input type="hidden" id="cityLongitude" name="cityLongitude" value="">
            <input type="hidden" id="cityGmtOffsetName" name="cityGmtOffsetName" value="">
            <input type="hidden" id="cityGmtOffset" name="cityGmtOffset" value="">
            <input type="hidden" id="cityDstOffset" name="cityDstOffset" value="">

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                
                <div class="flex flex-col space-y-1">
                    <label for="firstName" class="text-sm font-semibold text-slate-600 ml-1">Full Name</label>
                    <input type="text" id="firstName" name="firstName" maxlength="15" required 
                        class="w-full px-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-teal-500 focus:border-teal-500 outline-none transition-all">
                </div>

                <div class="flex flex-col space-y-1">
                    <label for="email" class="text-sm font-semibold text-slate-600 ml-1">Email Address</label>
                    <input type="email" id="email" name="email" required
                        class="w-full px-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-teal-500 focus:border-teal-500 outline-none transition-all">
                </div>

                <div class="flex flex-col space-y-1">
                    <label for="date" class="text-sm font-semibold text-slate-600 ml-1">Date of Birth</label>
                    <input type="date" id="date" name="date" required
                        class="w-full px-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-teal-500 focus:border-teal-500 outline-none transition-all">
                </div>

                <div class="flex flex-col space-y-1">
                    <label for="time" class="text-sm font-semibold text-slate-600 ml-1">Time of Birth</label>
                    <input type="time" id="time" name="time" required
                        class="w-full px-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-teal-500 focus:border-teal-500 outline-none transition-all">
                </div>
            </div>

            <div class="space-y-4 pt-4">
                <div class="flex flex-col space-y-1">
                    <label for="countryid" class="text-sm font-semibold text-slate-600 ml-1">Birth Country</label>
                    <select name="countryid" id="countryid" required
                        class="countries w-full px-4 py-2 rounded-lg border border-slate-300 bg-white focus:ring-2 focus:ring-teal-500 outline-none transition-all cursor-pointer">
                        <option value="" selected="selected">Select Country</option>
                    </select>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="flex flex-col space-y-1">
                        <label for="stateid" class="text-sm font-semibold text-slate-600 ml-1">Birth State</label>
                        <select name="stateid" id="stateid" required
                            class="states w-full px-4 py-2 rounded-lg border border-slate-300 bg-white focus:ring-2 focus:ring-teal-500 outline-none transition-all cursor-pointer">
                            <option value="" selected="selected">Select State</option>
                        </select>
                    </div>

                    <div class="flex flex-col space-y-1">
                        <label for="cityid" class="text-sm font-semibold text-slate-600 ml-1">Birth City</label>
                        <select name="cityid" id="cityid" required
                            class="cities w-full px-4 py-2 rounded-lg border border-slate-300 bg-white focus:ring-2 focus:ring-teal-500 outline-none transition-all cursor-pointer">
                            <option value="" selected="selected">Select City</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="flex flex-col space-y-1">
                <label for="questions" class="text-sm font-semibold text-slate-600 ml-1">Life Questions</label>
                <textarea required id="questions" name="questions" class="w-full h-32 px-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-teal-500 focus:border-teal-500 outline-none transition-all" placeholder-slate-400 italic 
         focus:placeholder-transparent focus:ring-2 placeholder=""></textarea>
            </div>            
            <div class="flex flex-col space-y-1">
                <label for="residency" class="text-sm font-semibold text-slate-600 ml-1">Current Residence</label>
                <select name="residency" id="residency" required
                    class="residency w-full px-4 py-2 rounded-lg border border-slate-300 bg-white focus:ring-2 focus:ring-teal-500 outline-none transition-all cursor-pointer">
                    <option value="">Current Residence</option>
                    <option value="Outside India">Outside India</option>
                    <option value="India">India</option>
                </select>
            </div>                
            <div class="pt-6">
                <button type="submit" id="submitForm" 
                    class="w-full md:w-auto px-8 py-3 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl shadow-lg hover:shadow-teal-200/50 transform transition hover:-translate-y-0.5 active:scale-95">
                    Generate Horoscope
                </button>
            </div>
        </form>
    </div>

    <script src='js/vendor/jquery.js'></script> 
</body>
</html>