
/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 * 
 * File:    Transxchange2GoogleTransit.java
 * Version:	1.0
 * Date: 	17-Oct-2006
 * 
 * Copyright (C) 2006, Joachim Pfeiffer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * http://www.gnu.org
 * 
 */

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import transxchange2GoogleTransitHandler.*;


public class Transxchange2GoogleTransit {
	/**
	 * Transxchange2GoogleTransit 
	 * 	$ transxchange2GoogleTransit <transxchange input filename> <url> <timezone> <default route type> [<output-directory>]
	 * 
	 * <default route type>: 0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry (global default value)
	 */

	// Google Transit data feet filenames
	static final String agencyFilename = "agency";
	static final String stopsFilename = "stops";
	static final String routesFilename = "routes";
	static final String tripsFilename = "trips";
	static final String stop_timesFilename = "stop_times";
	static final String calendarFilename = "calendar";
	static final String calendar_datesFilename = "calendar_dates";
	static final String extension = ".txt";
	
	static String filename = "";
	static String googleTransitUrl = "";
	static String googleTransitTimezone = "";
	static String googleTransitDefaultRouteType = "";
	static String outdir = "";
	static File outfile = null;
	static PrintWriter out = null;
	
	static String serviceStartDate;
	
	public static void main(String[] args)
    throws SAXException, IOException, ParserConfigurationException
    {
        /*
         * Process input file name and output directory
         */ 
    	System.out.println();
        System.out.println("transxchange2GoogleTransit 1.0");
        if (args.length < 4 || args.length > 5) {
        	System.out.println();
        	System.out.println("Usage: $ transxchange2GoogleTransit <transxchange input filename> -");
        	System.out.println("         <url> <timezone> <default route type> [<output-directory>]");
        	System.out.println();
        	System.out.println("         <timezone>: Please refer to ");
        	System.out.println("             http://en.wikipedia.org/wiki/List_of_tz_zones");
        	System.out.println("         <default route type>:");
        	System.out.println("             0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry");
        	System.exit(1);
        }
        
        filename = args[0];
        googleTransitUrl = args[1];
        googleTransitTimezone = args[2];
        googleTransitDefaultRouteType = args[3];
        if (args.length == 5)
        	outdir = args[4];
 
        /*
         * Parse transxchange input file
         */ 
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        TransxchangeHandler handler = new TransxchangeHandler();
        handler.setUrl(googleTransitUrl);
        handler.setTimezone(googleTransitTimezone);
        handler.setDefaultRouteType(googleTransitDefaultRouteType);
        try {
        	parser.parse(new File(filename), handler);
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit parse error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
     
        /*
         * Create Google Transit output files
         */ 
        // Service start date - append to file names
        serviceStartDate = "_" + (String)((ValueList)handler.getCalendar().getListCalendar__start_date().get(0)).getValue(0);
        
        
    	// agency.txt
        try {
        	outfile = new File(outdir + "/" + agencyFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("agency_name,agency_url,agency_timezone");
        	for (int i = 0; i < handler.getAgencies().getListAgency__agency_name().size(); i++) {
        		out.printf("%s,%s,%s", 
        			((ValueList)handler.getAgencies().getListAgency__agency_name().get(i)).getValue(0),
        			((ValueList)handler.getAgencies().getListAgency__agency_url().get(i)).getValue(0),
        			((ValueList)handler.getAgencies().getListAgency__agency_timezone().get(i)).getValue(0));
        		out.println();
        	}
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
       
        // stops.txt
        try {
        	outfile = new File(outdir + "/" + stopsFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("stop_id,stop_name,stop_desc,stop_lat,stop_lon,stop_street,stop_city,stop_region,stop_postcode,stop_country");
        	for (int i = 0; i < handler.getStops().getListStops__stop_id().size(); i++) {
        		out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
        			((ValueList)handler.getStops().getListStops__stop_id().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_name().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_desc().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_lat().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_lon().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_street().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_city().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_postcode().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_region().get(i)).getValue(0),
        			((ValueList)handler.getStops().getListStops__stop_country().get(i)).getValue(0));
        		out.println();
        	}       
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
       
        // routes.txt
        try {
        	outfile = new File(outdir + "/" + routesFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("route_id,route_short_name,route_long_name,route_desc,route_type");
        	for (int i = 0; i < handler.getRoutes().getListRoutes__route_id().size(); i++) {
        		out.printf("%s,%s,%s,%s,%s", 
        			((ValueList)handler.getRoutes().getListRoutes__route_id().get(i)).getValue(0),
        			((ValueList)handler.getRoutes().getListRoutes__route_short_name().get(i)).getValue(0),
        			((ValueList)handler.getRoutes().getListRoutes__route_long_name().get(i)).getValue(0),
        			((ValueList)handler.getRoutes().getListRoutes__route_desc().get(i)).getValue(0),
        			((ValueList)handler.getRoutes().getListRoutes__route_type().get(i)).getValue(0));     
        		out.println();
        	}       
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }

        // trips.txt
        try {
        	outfile = new File(outdir + "/" + tripsFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("route_id,service_id,trip_id,trip_headsign,block_id");
        	for (int i = 0; i < handler.getTrips().getListTrips__route_id().size(); i++) {
        		out.printf("%s,%s,%s,%s,%s", 
        			((ValueList)handler.getTrips().getListTrips__route_id().get(i)).getValue(0),
        			((ValueList)handler.getTrips().getListTrips__service_id().get(i)).getValue(0),
        			((ValueList)handler.getTrips().getListTrips__trip_id().get(i)).getKeyName(),
        			((ValueList)handler.getTrips().getListTrips__trip_headsign().get(i)).getValue(0),
        			((ValueList)handler.getTrips().getListTrips__block_id().get(i)).getValue(0));     
        		out.println();
        	}       
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }

        // stop_times.txt
        try {
        	outfile = new File(outdir + "/" + stop_timesFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type");
        	for (int i = 0; i < handler.getStopTimes().getListStoptimes__trip_id().size(); i++) {
        		out.printf("%s,%s,%s,%s,%s,%s,%s", 
           			((ValueList)handler.getStopTimes().getListStoptimes__trip_id().get(i)).getKeyName(),
           			((ValueList)handler.getStopTimes().getListStoptimes__arrival_time().get(i)).getValue(0),
           			((ValueList)handler.getStopTimes().getListStoptimes__departure_time().get(i)).getValue(0),
           			((ValueList)handler.getStopTimes().getListStoptimes__stop_id().get(i)).getValue(0),
           			((ValueList)handler.getStopTimes().getListStoptimes__stop_sequence().get(i)).getValue(0),
           			((ValueList)handler.getStopTimes().getListStoptimes__pickup_type().get(i)).getValue(0),
           			((ValueList)handler.getStopTimes().getListStoptimes__drop_off_type().get(i)).getValue(0));
        		out.println();
        	}       
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
        
        // calendar.txt
        try {
        	outfile = new File(outdir + "/" + calendarFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date");
        	for (int i = 0; i < handler.getCalendar().getListCalendar__service_id().size(); i++) {
        		out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
           			((ValueList)handler.getCalendar().getListCalendar__service_id().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__monday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__tuesday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__wednesday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__thursday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__friday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__saturday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__sunday().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__start_date().get(i)).getValue(0),
           			((ValueList)handler.getCalendar().getListCalendar__end_date().get(i)).getValue(0));           			
        		out.println();
        	}       
        	out.close();
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }

        // calendar_dates.txt
        try {
        	outfile = new File(outdir + "/" + calendar_datesFilename + serviceStartDate + extension);
        	out = new PrintWriter(new FileWriter(outfile));
        	out.println("service_id,date,exception_type");
        	for (int i = 0; i < handler.getCalendarDates().getListCalendarDates__service_id().size(); i++) {
        		out.printf("%s,%s,%s", 
           			((ValueList)handler.getCalendarDates().getListCalendarDates__service_id().get(i)).getValue(0),
           			((ValueList)handler.getCalendarDates().getListCalendarDates__date().get(i)).getValue(0),
           			((ValueList)handler.getCalendarDates().getListCalendarDates__exception_type().get(i)).getValue(0));           			
        		out.println();
        	}       
        	out.close();          
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }

    	System.exit(0);
    }
}
