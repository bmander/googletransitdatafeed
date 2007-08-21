/*
 * Copyright 2007 GoogleTransitDataFeed
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package transxchange2GoogleTransitHandler;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.zip.*;
import java.util.ArrayList;

/*
 * This class extends DefaultHandler to parse a TransXChange v2.1 xml file,	
 * 	build corresponding Google Transit Feed data structures
 *  and write these to a Google Transit Feed Specification (9-Apr-2007) compliant file set
 */
public class TransxchangeHandlerEngine extends DefaultHandler {

	// Google Transit Feed structures
	TransxchangeAgency agencies;
	TransxchangeStops stops;
	TransxchangeRoutes routes;
	TransxchangeTrips trips;
	TransxchangeStopTimes stopTimes;
	TransxchangeCalendar calendar;
	TransxchangeCalendarDates calendarDates;
	
	// Parse comments
	static String parseError = "";
	static String parseInfo = "";

	// Additional contributions to resulting Google Transit file set which cannot be extracted from a TransXChange input file
	static String googleTransitUrl = "";
	static String googleTransitTimezone = "";
	static String googleTransitDefaultRouteType = "";
	static String googleTransitOutfile = "";
	
	// Google Transit Feed Specification file names
	static final String agencyFilename = "agency";
	static final String stopsFilename = "stops";
	static final String routesFilename = "routes";
	static final String tripsFilename = "trips";
	static final String stop_timesFilename = "stop_times";
	static final String calendarFilename = "calendar";
	static final String calendar_datesFilename = "calendar_dates";
	static final String extension = ".txt";
	static final String googleTransitZipfileName = "google_transit.zip";
	
	// output files
	static PrintWriter agenciesOut = null;
	static PrintWriter stopsOut = null;
	static PrintWriter routesOut = null;
	static PrintWriter tripsOut = null;
	static PrintWriter stop_timesOut = null;
	static PrintWriter calendarsOut = null;
	static PrintWriter calendarDatesOut = null;
	
	static ArrayList filenames = null;
	static String outdir = "";

	/*
	 * Utility methods to set and get attribute values
	 */
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
	
	public void setParseError(String txt) {
		parseError = txt;
	}
	
	public String getParseError() {
		return parseError;
	}
	
	public void setParseInfo(String txt) {
		parseInfo = txt;
	}
	
	public String getParseInfo() {
		return parseInfo;
	}
	
	/*
	 * Generate Google Transit Feed structures
	 */
	public void parse(String filename, String url, String timezone, String defaultRouteType)
	    throws SAXException, SAXParseException, IOException, ParserConfigurationException
	{
		ZipFile zipfile = null;
		boolean zipinput = true; // v1.5.1: Handle zip files
		
		this.setUrl(url);
		this.setTimezone(timezone);
		this.setDefaultRouteType(defaultRouteType);

		// v1.5.1: Try to open filename as zip file
		try {
			zipfile = new ZipFile(filename);
		} catch (IOException e) {
			zipinput = false; // Opening file as zip file crashed; assume it is a single XML file
		}
		if (zipinput) {
			for (java.util.Enumeration enumer = zipfile.entries(); enumer.hasMoreElements();) {
				ZipEntry zipentry = (ZipEntry)enumer.nextElement();
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser parser = parserFactory.newSAXParser();
				System.out.println(zipentry.getName());
				InputStream in = zipfile.getInputStream(zipentry);
//				BufferedInputStream bis = new BufferedInputStream(in);
				parser.parse(in, this);	
			}
		} else {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(new File(filename), this);	
		}
	}
	
	/*
	 * Start element. Called by parser when start of element found <element>
	 */   	
	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {
	    agencies.startElement(uri, name, qName, atts);
	    stops.startElement(uri, name, qName, atts);
	    routes.startElement(uri, name, qName, atts);
	    trips.startElement(uri, name, qName, atts);
	    stopTimes.startElement(uri, name, qName, atts);
	    calendar.startElement(uri, name, qName, atts);
	    calendarDates.startElement(uri, name, qName, atts);
	}
	
	/*
	 * Parse element. Called to extract contents of elements <element>contents</element>
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
 	 * End element. Called by parser when end of element reached </element>
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
	 * Complete (and dump) Google Transit Feed data structures. Called when end of TransXChange input file is reached
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
/* ... not
		agencies.dumpValues();
		stops.dumpValues(); 
		routes.dumpValues(); 
		trips.dumpValues(); 
		stopTimes.dumpValues(); 
		calendar.dumpValues();
		calendarDates.dumpValues();
*/	}


	/*
	 * Prepare Google Trandit Feed file set files
	 */
	public static void prepareOutput(String rootDirectory, String workDirectory)
	throws IOException
	{
		outdir = rootDirectory + workDirectory;
		filenames = new ArrayList();

		// Delete existing GTFS files in output directory
		new File(outdir + "/" + agencyFilename + extension).delete();
		new File(outdir + "/" + stopsFilename + extension).delete();
		new File(outdir + "/" + routesFilename + extension).delete();
		new File(outdir + "/" + tripsFilename + extension).delete();
		new File(outdir + "/" + stop_timesFilename + extension).delete();
		new File(outdir + "/" + calendarFilename + extension).delete();
		new File(outdir + "/" + calendar_datesFilename + extension).delete();
		new File(outdir + "/" + googleTransitZipfileName).delete();
		
		// Create output directory
		// Note service start date not any longer used to determine directory name for outfiles
//      String serviceStartDate = (String)((ValueList)this.getCalendar().getListCalendar__start_date().get(0)).getValue(0);        
		new File(outdir /* + "/" + serviceStartDate*/ ).mkdirs();
	}
	
	/*
	 * Create Google Transit Feed file set from Google Transit Feed data structures except for stops
	 */
	public void writeOutputSansAgenciesStops() 
	throws IOException
	{		
		String outfileName = "";
		File outfile = null;
		
        // routes.txt
		if (routesOut == null) {
	        outfileName = routesFilename + /* "_" + serviceStartDate + */ extension;
	        outfile = new File(outdir + /* "/" + serviceStartDate + */ "/" + outfileName);
	        filenames.add(outfileName);      
	        routesOut = new PrintWriter(new FileWriter(outfile));
	        routesOut.println("route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color");
		}
		for (int i = 0; i < this.getRoutes().getListRoutes__route_id().size(); i++) {
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__route_id().get(i)).getValue(0));
        	routesOut.print(",");
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__agency_id().get(i)).getValue(0)); // v1.5: agency ID
        	routesOut.print(",");
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__route_short_name().get(i)).getValue(0));
        	routesOut.print(",");
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__route_long_name().get(i)).getValue(0));
        	routesOut.print(",");
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__route_desc().get(i)).getValue(0));
        	routesOut.print(",");
        	routesOut.print(((ValueList)this.getRoutes().getListRoutes__route_type().get(i)).getValue(0));
        	routesOut.print(","); // no route url
        	routesOut.print(","); // no route color
        	routesOut.println(","); // no route text color
        }       

        // trips.txt
		if (tripsOut == null) {
	        outfileName = tripsFilename + /* "_" + serviceStartDate + */ extension;
	        outfile = new File(outdir + /* "/" + serviceStartDate + */ "/" + outfileName);
	        filenames.add(outfileName);      
	        tripsOut = new PrintWriter(new FileWriter(outfile));
	        tripsOut.println("route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id");
		}
        for (int i = 0; i < this.getTrips().getListTrips__route_id().size(); i++) {
        	tripsOut.print(((ValueList)this.getTrips().getListTrips__route_id().get(i)).getValue(0));
        	tripsOut.print(",");
        	tripsOut.print(((ValueList)this.getTrips().getListTrips__service_id().get(i)).getValue(0));
        	tripsOut.print(",");
        	tripsOut.print(((ValueList)this.getTrips().getListTrips__trip_id().get(i)).getKeyName());
        	tripsOut.print(",");
        	tripsOut.print(((ValueList)this.getTrips().getListTrips__trip_headsign().get(i)).getValue(0));
        	tripsOut.print(",");
        	tripsOut.print(",");
        	tripsOut.print(((ValueList)this.getTrips().getListTrips__block_id().get(i)).getValue(0));
        	tripsOut.println(",");
        }       

        // stop_times.txt
        if (stop_timesOut == null) {
            outfileName = stop_timesFilename + /* "_" + serviceStartDate + */ extension;
            outfile = new File(outdir + /* "/" + serviceStartDate + */ "/"  + outfileName);
            filenames.add(outfileName);      
            stop_timesOut = new PrintWriter(new FileWriter(outfile));
            stop_timesOut.println("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled");        	
        }
        for (int i = 0; i < this.getStopTimes().getListStoptimes__trip_id().size(); i++) {
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__trip_id().get(i)).getKeyName());
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__arrival_time().get(i)).getValue(0));
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__departure_time().get(i)).getValue(0));
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__stop_id().get(i)).getValue(0));
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__stop_sequence().get(i)).getValue(0));
        	stop_timesOut.print(",");
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__pickup_type().get(i)).getValue(0));
        	stop_timesOut.print(",");
        	stop_timesOut.print(((ValueList)this.getStopTimes().getListStoptimes__drop_off_type().get(i)).getValue(0));
        	stop_timesOut.println(",");
        }       
        
        // calendar.txt
        String daytypesJourneyPattern;
        String daytypesService;
        String serviceId;
        
        if (calendarsOut == null) {
            outfileName = calendarFilename + /* "_" + serviceStartDate + */ extension;
            outfile = new File(outdir + /* "/" + serviceStartDate + */ "/" + outfileName);
            filenames.add(outfileName);
            calendarsOut = new PrintWriter(new FileWriter(outfile));
            calendarsOut.println("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date");        	
        }
        for (int i = 0; i < this.getCalendar().getListCalendar__service_id().size(); i++) {
        	serviceId = (String)(((ValueList)this.getCalendar().getListCalendar__service_id().get(i))).getValue(0);
        	calendarsOut.print(serviceId);
        	calendarsOut.print(",");
        	// v1.5: Service ID added to calendar data structure in class TransxchangeCalendar. 
        	// 	If match and no journey pattern associated with daytype, 
        	//  then daytype applies to service, not journey pattern. Otherwise daytpe is set to 0 as daytype applies to journey pattern, not service
        	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");

        	// Tuesday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");
        	
        	// Wednesday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");
        	
        	// Thursday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");

        	// Friday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");

        	// Saturday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");
        	
        	// Sunday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(0));
        	else
        		calendarsOut.print("0");
        	calendarsOut.print(",");

        	// Start and end dates
        	calendarsOut.print(((ValueList)this.getCalendar().getListCalendar__start_date().get(i)).getValue(0));
        	calendarsOut.print(",");
        	calendarsOut.println(((ValueList)this.getCalendar().getListCalendar__end_date().get(i)).getValue(0));           			
        }       

        // calendar_dates.txt
        // Create file only if there are exceptions or additions
        if (this.getCalendarDates().getListCalendarDates__service_id().size() > 0) { 
        	if (calendarDatesOut == null) {
            	outfileName = calendar_datesFilename + /* "_" + serviceStartDate + */ extension;
            	outfile = new File(outdir + /* "/" + serviceStartDate + */ "/" + outfileName);
            	calendarDatesOut = new PrintWriter(new FileWriter(outfile));
            	filenames.add(outfileName);
            	calendarDatesOut.println("service_id,date,exception_type");        		
        	}
        	for (int i = 0; i < this.getCalendarDates().getListCalendarDates__service_id().size(); i++) {
        		calendarDatesOut.print(((ValueList)this.getCalendarDates().getListCalendarDates__service_id().get(i)).getValue(0));
        		calendarDatesOut.print(",");
        		calendarDatesOut.print(((ValueList)this.getCalendarDates().getListCalendarDates__date().get(i)).getValue(0));
        		calendarDatesOut.print(",");
        		calendarDatesOut.println(((ValueList)this.getCalendarDates().getListCalendarDates__exception_type().get(i)).getValue(0));           			
        	}       
        }
   	}
	
	/*
	 * Create Google Transit Feed file set from Google Transit Feed data structures except for stops
	 */
	public void writeOutputAgenciesStops() 
	throws IOException
	{		
		String outfileName = "";
		File outfile = null;
		
		// agencies.txt
		if (agenciesOut == null) {
			outfileName = agencyFilename + /* "_" + serviceStartDate + */ extension;
			outfile = new File(outdir + /* "/" + serviceStartDate + */ "/"  + outfileName);
			filenames.add(outfileName);
			agenciesOut = new PrintWriter(new FileWriter(outfile));
			agenciesOut.println("agency_id,agency_name,agency_url,agency_timezone,agency_lang");
		}
		for (int i = 0; i < this.getAgencies().getListAgency__agency_name().size(); i++) {
			if (((String)(((ValueList)this.getAgencies().getListAgency__agency_id().get(i))).getValue(0)).length() > 0) {
				agenciesOut.print(((ValueList)this.getAgencies().getListAgency__agency_id().get(i)).getValue(0)); // v1.5: new: agency id
				agenciesOut.print(","); 
				agenciesOut.print(((ValueList)this.getAgencies().getListAgency__agency_name().get(i)).getValue(0));
				agenciesOut.print(",");
				agenciesOut.print(((ValueList)this.getAgencies().getListAgency__agency_url().get(i)).getValue(0));
				agenciesOut.print(",");
				agenciesOut.print(((ValueList)this.getAgencies().getListAgency__agency_timezone().get(i)).getValue(0));
				agenciesOut.println(","); // no agency language
	        }
        }
       
        // stops.txt
		if (stopsOut == null) {
	        outfileName = stopsFilename + /* "_" + serviceStartDate + */ extension;
	        outfile = new File(outdir + /* "/" + serviceStartDate + */ "/" + outfileName);
	        filenames.add(outfileName);
	        stopsOut = new PrintWriter(new FileWriter(outfile));
	        stopsOut.println("stop_id,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url");
		}
		for (int i = 0; i < this.getStops().getListStops__stop_id().size(); i++) {
			if (((String)(((ValueList)this.getStops().getListStops__stop_id().get(i))).getValue(0)).length() > 0) {
				stopsOut.print(((ValueList)this.getStops().getListStops__stop_id().get(i)).getValue(0));
				stopsOut.print(",");
				stopsOut.print(((ValueList)this.getStops().getListStops__stop_name().get(i)).getValue(0));
				stopsOut.print(",");
				stopsOut.print(((ValueList)this.getStops().getListStops__stop_desc().get(i)).getValue(0));
				stopsOut.print(",");
				stopsOut.print(((ValueList)this.getStops().getListStops__stop_lat().get(i)).getValue(0));
				stopsOut.print(",");
				stopsOut.print(((ValueList)this.getStops().getListStops__stop_lon().get(i)).getValue(0));
				stopsOut.print(","); // no zone id
				stopsOut.println(","); // no stop URL
// Below a number of attributes (stop_street to stop_country) which have been deprecated in the Google Transit Feed Specification (9-Apr-2007 release of the spec)
//        		stopsOut.print(((ValueList)this.getStops().getListStops__stop_street().get(i)).getValue(0));
//        		stopsOut.print(",");
//        		stopsOut.print(((ValueList)this.getStops().getListStops__stop_city().get(i)).getValue(0));
//        		stopsOut.print(",");
//        		stopsOut.print(((ValueList)this.getStops().getListStops__stop_postcode().get(i)).getValue(0));
//        		stopsOut.print(",");
//        		stopsOut.print(((ValueList)this.getStops().getListStops__stop_region().get(i)).getValue(0));
//        		stopsOut.print(",");
//        		stopsOut.println(((ValueList)this.getStops().getListStops__stop_country().get(i)).getValue(0));
			}
		}		
	}
	
	/*
	 * Clear data structures except for stops
	 */
	public void clearDataSansAgenciesStops() {
		routes = null;
		trips = null;
		stopTimes = null;
		calendar = null;
		calendarDates = null;
	}

	/*
	 * Close Google Transit Feed file set from Google Transit Feed data structures
	 */
	public static String closeOutput(String rootDirectory, String workDirectory) 
	throws IOException
	{	
		// Close out PrintWriter's
		agenciesOut.close();
		stopsOut.close();
		routesOut.close();
		tripsOut.close();
		stop_timesOut.close();
		calendarsOut.close();
		if (calendarDatesOut != null) // calendar_dates is optional; might not have been created
			calendarDatesOut.close();

		agenciesOut = null;
		stopsOut = null;
		routesOut = null;
		tripsOut = null;
		stop_timesOut = null;
		calendarsOut = null;
		calendarDatesOut = null;
		
		// Compress the files
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outdir + /* "/" + serviceStartDate + */ "/" + googleTransitZipfileName));
        byte[] buf = new byte[1024]; // Create a buffer for reading the files
        for (int i = 0; i < filenames.size(); i++) {
            FileInputStream in = new FileInputStream(outdir + /* "/" + serviceStartDate + */ "/" + (String)filenames.get(i));
    
            // Add ZIP entry to output stream.
            zipOut.putNextEntry(new ZipEntry((String)filenames.get(i)));
    
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                zipOut.write(buf, 0, len);
            }
    
            // Complete the entry
            zipOut.closeEntry();
            in.close();
        }
    
        // Complete the ZIP file
        zipOut.close();

        // Return path and name of google_transit zip file
        return workDirectory + /* "/" + serviceStartDate + */ "/" + "google_transit.zip";
	}
		
	/*
	 * Initialize Google Transit Feed data structures
	 */
	public TransxchangeHandlerEngine () 
		throws UnsupportedEncodingException, IOException {
		agencies = new TransxchangeAgency(this);
		stops = new TransxchangeStops(this);
		routes = new TransxchangeRoutes(this);
		trips = new TransxchangeTrips(this);
		stopTimes = new TransxchangeStopTimes(this);
		calendar = new TransxchangeCalendar(this);
		calendarDates = new TransxchangeCalendarDates(this);
	}
}
