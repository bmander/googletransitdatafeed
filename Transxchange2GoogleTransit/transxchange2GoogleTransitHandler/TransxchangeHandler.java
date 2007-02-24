/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * Version:	1.2
 * Date: 	24-Feb-2007
 * 
 * Copyright (C) 2007, Joachim Pfeiffer
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

package transxchange2GoogleTransitHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


// This class extends DefaultHandler to parse an transxchange xml file
// and generate corresponding Google Transit data feed structures
public class TransxchangeHandler extends DefaultHandler {
	
	TransxchangeAgency agencies;
	TransxchangeStops stops;
	TransxchangeRoutes routes;
	TransxchangeTrips trips;
	TransxchangeStopTimes stopTimes;
	TransxchangeCalendar calendar;
	TransxchangeCalendarDates calendarDates;

	static String googleTransitUrl = "";
	static String googleTransitTimezone = "";
	static String googleTransitDefaultRouteType = "";
	static String googleTransitOutfile = "";
	
	static String serviceStartDate;

	static final String agencyFilename = "agency";
	static final String stopsFilename = "stops";
	static final String routesFilename = "routes";
	static final String tripsFilename = "trips";
	static final String stop_timesFilename = "stop_times";
	static final String calendarFilename = "calendar";
	static final String calendar_datesFilename = "calendar_dates";
	static final String extension = ".txt";

	static File outfile = null;
	static PrintWriter out = null;

	public void parse(String filename)
	    throws SAXException, IOException, ParserConfigurationException
	{
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
    	parser.parse(new File(filename), this);
	}
	
	public void setUrl(String url) {
		googleTransitUrl = url;
	}

	public void setTimezone(String timezone) {
		googleTransitTimezone = timezone;
	}

	public void setDefaultRouteType(String defaultRouteType) {
		googleTransitDefaultRouteType = defaultRouteType;
	}
	
	public String getUrl() {
		return googleTransitUrl;
	}

	public String getTimezone() {
		return googleTransitTimezone;
	}

	public String getDefaultRouteType() {
		return googleTransitDefaultRouteType;
	}
	
	public TransxchangeAgency getAgencies() {
		return agencies;
	}
	
	public TransxchangeStops getStops() {
		return stops;
	}
	
	public TransxchangeRoutes getRoutes() {
		return routes;
	}
	
	public TransxchangeTrips getTrips() {
		return trips;
	}

	public TransxchangeStopTimes getStopTimes() {
		return stopTimes;
	}
	
	public TransxchangeCalendar getCalendar() {
		return calendar;
	}
	
	public TransxchangeCalendarDates getCalendarDates() {
		return calendarDates;
	}
	
	/*
	 * start element
	 */   	
	public void startElement(String uri, String name, String qName, Attributes atts) {
	
	    agencies.startElement(uri, name, qName, atts);
	    stops.startElement(uri, name, qName, atts);
	    routes.startElement(uri, name, qName, atts);
	    trips.startElement(uri, name, qName, atts);
	    stopTimes.startElement(uri, name, qName, atts);
	    calendar.startElement(uri, name, qName, atts);
	    calendarDates.startElement(uri, name, qName, atts);
	}
	
	/*
	 * parse element
	 */   	
	public void characters (char ch[], int start, int length) {
		agencies.characters(ch, start, length);
		stops.characters(ch, start, length);
		routes.characters(ch, start, length);
		trips.characters(ch, start, length);
		stopTimes.characters(ch, start, length);
		calendar.characters(ch, start, length);
		calendarDates.characters(ch, start, length);
	}
    
	/*
 	 * end element
 	 */   	
	public void endElement (String uri, String name, String qName) {
		// take care of element
		agencies.endElement(uri, name, qName);
		stops.endElement(uri, name, qName);
		routes.endElement(uri, name, qName);
		trips.endElement(uri, name, qName);
		stopTimes.endElement(uri, name, qName);
		calendar.endElement(uri, name, qName);
		calendarDates.endElement(uri, name, qName);
	
		// clear keys
		agencies.clearKeys(qName);
		stops.clearKeys(qName);
		routes.clearKeys(qName);
		trips.clearKeys(qName);
		stopTimes.clearKeys(qName);
		calendar.clearKeys(qName);
		calendarDates.clearKeys(qName);

	}

	/*
	 * complete and dump Google Transit data feed data structure
	 */   	
	public void endDocument() {
    
		// wrap up document parsing
		agencies.endDocument();
		stops.endDocument();
		routes.endDocument();
		trips.endDocument();
		stopTimes.endDocument();
		calendar.endDocument();
		calendarDates.endDocument();
        
		// Complete data structures (by filling in default values if necessary)
		agencies.completeData();
		stops.completeData();
		routes.completeData();
		trips.completeData();
		stopTimes.completeData();
		calendar.completeData();
		calendarDates.completeData();
    
		// Dump parsed data to System.out
/*
		agencies.dumpValues();
		stops.dumpValues();
		routes.dumpValues();
		trips.dumpValues();
		stopTimes.dumpValues();
		calendar.dumpValues();
		calendarDates.dumpValues();
*/
	}

	public void writeOutput(String outdir) 
	throws IOException
	{
        // Service start date - append to file names
        serviceStartDate = "_" + (String)((ValueList)this.getCalendar().getListCalendar__start_date().get(0)).getValue(0);
            
    	// agency.txt
        outfile = new File(outdir + "/" + agencyFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("agency_name,agency_url,agency_timezone");
        for (int i = 0; i < this.getAgencies().getListAgency__agency_name().size(); i++) {
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_url().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getAgencies().getListAgency__agency_timezone().get(i)).getValue(0));
/* Java 1.5        		out.printf("%s,%s,%s", 
/*        			((ValueList)handler.getAgencies().getListAgency__agency_name().get(i)).getValue(0),
/*        			((ValueList)handler.getAgencies().getListAgency__agency_url().get(i)).getValue(0),
/*        			((ValueList)handler.getAgencies().getListAgency__agency_timezone().get(i)).getValue(0));
/*        		out.println();
*/
        }
        out.close();
       
        // stops.txt
        outfile = new File(outdir + "/" + stopsFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("stop_id,stop_name,stop_desc,stop_lat,stop_lon,stop_street,stop_city,stop_region,stop_postcode,stop_country");
        for (int i = 0; i < this.getStops().getListStops__stop_id().size(); i++) {
        	out.print(((ValueList)this.getStops().getListStops__stop_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_desc().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_lat().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_lon().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_street().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_city().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_postcode().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStops().getListStops__stop_region().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getStops().getListStops__stop_country().get(i)).getValue(0));
        	
/* Java 1.5        		out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
/*        			((ValueList)this.getStops().getListStops__stop_id().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_name().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_desc().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_lat().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_lon().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_street().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_city().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_postcode().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_region().get(i)).getValue(0),
/*        			((ValueList)this.getStops().getListStops__stop_country().get(i)).getValue(0));
/*        		out.println();
 */
        }       
        out.close();
       
        // routes.txt
        outfile = new File(outdir + "/" + routesFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("route_id,route_short_name,route_long_name,route_desc,route_type");
        for (int i = 0; i < this.getRoutes().getListRoutes__route_id().size(); i++) {
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_short_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_long_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_desc().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getRoutes().getListRoutes__route_type().get(i)).getValue(0));     

/* Java 1.5        		out.printf("%s,%s,%s,%s,%s", 
/*        			((ValueList)this.getRoutes().getListRoutes__route_id().get(i)).getValue(0),
/*        			((ValueList)this.getRoutes().getListRoutes__route_short_name().get(i)).getValue(0),
/*        			((ValueList)this.getRoutes().getListRoutes__route_long_name().get(i)).getValue(0),
/*        			((ValueList)this.getRoutes().getListRoutes__route_desc().get(i)).getValue(0),
/*        			((ValueList)this.getRoutes().getListRoutes__route_type().get(i)).getValue(0));     
/*      		out.println();
*/
        }       
        out.close();

        // trips.txt
        outfile = new File(outdir + "/" + tripsFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("route_id,service_id,trip_id,trip_headsign,block_id");
        for (int i = 0; i < this.getTrips().getListTrips__route_id().size(); i++) {
        	out.print(((ValueList)this.getTrips().getListTrips__route_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__service_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__trip_id().get(i)).getKeyName());
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__trip_headsign().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getTrips().getListTrips__block_id().get(i)).getValue(0));

/* Java 1.5       	 	out.printf("%s,%s,%s,%s,%s", 
/*        			((ValueList)this.getTrips().getListTrips__route_id().get(i)).getValue(0),
/*        			((ValueList)this.getTrips().getListTrips__service_id().get(i)).getValue(0),
/*        			((ValueList)this.getTrips().getListTrips__trip_id().get(i)).getKeyName(),
/*        			((ValueList)this.getTrips().getListTrips__trip_headsign().get(i)).getValue(0),
/*        			((ValueList)this.getTrips().getListTrips__block_id().get(i)).getValue(0));
/*        		out.println();
*/
        }       
        out.close();

        // stop_times.txt
        outfile = new File(outdir + "/" + stop_timesFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type");
        for (int i = 0; i < this.getStopTimes().getListStoptimes__trip_id().size(); i++) {
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__trip_id().get(i)).getKeyName());
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__arrival_time().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__departure_time().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__stop_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__stop_sequence().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__pickup_type().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getStopTimes().getListStoptimes__drop_off_type().get(i)).getValue(0));

/* Java 1.5        		out.printf("%s,%s,%s,%s,%s,%s,%s", 
/*           			((ValueList)this.getStopTimes().getListStoptimes__trip_id().get(i)).getKeyName(),
/*           			((ValueList)this.getStopTimes().getListStoptimes__arrival_time().get(i)).getValue(0),
/*           			((ValueList)this.getStopTimes().getListStoptimes__departure_time().get(i)).getValue(0),
/*           			((ValueList)this.getStopTimes().getListStoptimes__stop_id().get(i)).getValue(0),
/*           			((ValueList)this.getStopTimes().getListStoptimes__stop_sequence().get(i)).getValue(0),
/*           			((ValueList)this.getStopTimes().getListStoptimes__pickup_type().get(i)).getValue(0),
/*           			((ValueList)this.getStopTimes().getListStoptimes__drop_off_type().get(i)).getValue(0));
/*        		out.println();
*/
        }       
        out.close();
        
        // calendar.txt
        outfile = new File(outdir + "/" + calendarFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date");
        for (int i = 0; i < this.getCalendar().getListCalendar__service_id().size(); i++) {
        	out.print(((ValueList)this.getCalendar().getListCalendar__service_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendar().getListCalendar__start_date().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getCalendar().getListCalendar__end_date().get(i)).getValue(0));           			

/* Java 1.5        		out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
/*           			((ValueList)this.getCalendar().getListCalendar__service_id().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__start_date().get(i)).getValue(0),
/*           			((ValueList)this.getCalendar().getListCalendar__end_date().get(i)).getValue(0));           			
/*        		out.println();
*/
        }       
        out.close();

        // calendar_dates.txt
        outfile = new File(outdir + "/" + calendar_datesFilename + serviceStartDate + extension);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("service_id,date,exception_type");
        for (int i = 0; i < this.getCalendarDates().getListCalendarDates__service_id().size(); i++) {
        	out.print(((ValueList)this.getCalendarDates().getListCalendarDates__service_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getCalendarDates().getListCalendarDates__date().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getCalendarDates().getListCalendarDates__exception_type().get(i)).getValue(0));           			

/* Java 1.5        		out.printf("%s,%s,%s", 
/*           			((ValueList)this.getCalendarDates().getListCalendarDates__service_id().get(i)).getValue(0),
/*           			((ValueList)this.getCalendarDates().getListCalendarDates__date().get(i)).getValue(0),
/*           			((ValueList)this.getCalendarDates().getListCalendarDates__exception_type().get(i)).getValue(0));           			
/*        		out.println();
*/
        }       
        out.close();          
	}
	
	public TransxchangeHandler () {
		agencies = new TransxchangeAgency(this);
		stops = new TransxchangeStops(this);
		routes = new TransxchangeRoutes(this);
		trips = new TransxchangeTrips(this);
		stopTimes = new TransxchangeStopTimes(this);
		calendar = new TransxchangeCalendar(this);
		calendarDates = new TransxchangeCalendarDates(this);
	}
}
