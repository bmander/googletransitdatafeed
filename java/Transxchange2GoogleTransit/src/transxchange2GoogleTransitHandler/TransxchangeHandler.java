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
public class TransxchangeHandler extends DefaultHandler {

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
		this.setUrl(url);
		this.setTimezone(timezone);
		this.setDefaultRouteType(defaultRouteType);
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(new File(filename), this);	
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
	 * Create Google Transit Feed file set from Google Transit Feed data structures
	 */
	public String writeOutput(String rootDirectory, String workDirectory)
	throws IOException
	{		
		ArrayList filenames = new ArrayList();
		
		String outdir = rootDirectory + workDirectory;
	
		// Note service start date - used to determine directory name for outfiles
        String serviceStartDate = (String)((ValueList)this.getCalendar().getListCalendar__start_date().get(0)).getValue(0);        
        new File(outdir + "/" + serviceStartDate).mkdirs();
      
    	// agency.txt
        String outfileName = agencyFilename + /* "_" + serviceStartDate + */ extension;
        File outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        filenames.add(outfileName);
        PrintWriter out = new PrintWriter(new FileWriter(outfile));
        out.println("agency_id,agency_name,agency_url,agency_timezone,agency_lang");
        for (int i = 0; i < this.getAgencies().getListAgency__agency_name().size(); i++) {
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_id().get(i)).getValue(0)); // v1.5: new: agency id
        	out.print(","); 
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_url().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getAgencies().getListAgency__agency_timezone().get(i)).getValue(0));
        	out.println(","); // no agency language
        }
        out.close();
       
        // stops.txt
        outfileName = stopsFilename + /* "_" + serviceStartDate + */ extension;
        outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        filenames.add(outfileName);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("stop_id,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url");
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
        	out.print(","); // no zone id
        	out.println(","); // no stop URL
// Below a number of attributes (stop_street to stop_country) which have been deprecated in the Google Transit Feed Specification (9-Apr-2007 release of the spec)
//        	out.print(((ValueList)this.getStops().getListStops__stop_street().get(i)).getValue(0));
//        	out.print(",");
//        	out.print(((ValueList)this.getStops().getListStops__stop_city().get(i)).getValue(0));
//        	out.print(",");
//        	out.print(((ValueList)this.getStops().getListStops__stop_postcode().get(i)).getValue(0));
//        	out.print(",");
//        	out.print(((ValueList)this.getStops().getListStops__stop_region().get(i)).getValue(0));
//        	out.print(",");
//        	out.println(((ValueList)this.getStops().getListStops__stop_country().get(i)).getValue(0));
        }       
        out.close();
       
        // routes.txt
        outfileName = routesFilename + /* "_" + serviceStartDate + */ extension;
        outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        filenames.add(outfileName);      
        out = new PrintWriter(new FileWriter(outfile));
        out.println("route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color");
        for (int i = 0; i < this.getRoutes().getListRoutes__route_id().size(); i++) {
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__agency_id().get(i)).getValue(0)); // v1.5: agency ID
           	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_short_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_long_name().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_desc().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getRoutes().getListRoutes__route_type().get(i)).getValue(0));
        	out.print(","); // no route url
        	out.print(","); // no route color
        	out.println(","); // no route text color
        }       
        out.close();

        // trips.txt
        outfileName = tripsFilename + /* "_" + serviceStartDate + */ extension;
        outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        out = new PrintWriter(new FileWriter(outfile));
        filenames.add(outfileName);      
        out.println("route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id");
        for (int i = 0; i < this.getTrips().getListTrips__route_id().size(); i++) {
        	out.print(((ValueList)this.getTrips().getListTrips__route_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__service_id().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__trip_id().get(i)).getKeyName());
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__trip_headsign().get(i)).getValue(0));
        	out.print(",");
        	out.print(",");
        	out.print(((ValueList)this.getTrips().getListTrips__block_id().get(i)).getValue(0));
        	out.println(",");
        }       
        out.close();

        // stop_times.txt
        outfileName = stop_timesFilename + /* "_" + serviceStartDate + */ extension;
        outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        filenames.add(outfileName);      
        out = new PrintWriter(new FileWriter(outfile));
        out.println("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled");
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
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__pickup_type().get(i)).getValue(0));
        	out.print(",");
        	out.print(((ValueList)this.getStopTimes().getListStoptimes__drop_off_type().get(i)).getValue(0));
        	out.println(",");
        }       
        out.close();
        
        // calendar.txt
        String daytypesJourneyPattern;
        String daytypesService;
        String serviceId;
        
        outfileName = calendarFilename + /* "_" + serviceStartDate + */ extension;
        outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        filenames.add(outfileName);
        out = new PrintWriter(new FileWriter(outfile));
        out.println("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date");
        for (int i = 0; i < this.getCalendar().getListCalendar__service_id().size(); i++) {
        	serviceId = (String)(((ValueList)this.getCalendar().getListCalendar__service_id().get(i))).getValue(0);
        	out.print(serviceId);
        	out.print(",");
        	// v1.5: Service ID added to calendar data structure in class TransxchangeCalendar. 
        	// 	If match and no journey pattern associated with daytype, 
        	//  then daytype applies to service, not journey pattern. Otherwise daytpe is set to 0 as daytype applies to journey pattern, not service
        	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		out.print(((ValueList)this.getCalendar().getListCalendar__monday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");

        	// Tuesday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
               	out.print(((ValueList)this.getCalendar().getListCalendar__tuesday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");
        	
        	// Wednesday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
            	out.print(((ValueList)this.getCalendar().getListCalendar__wednesday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");
        	
        	// Thursday
           	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
            	out.print(((ValueList)this.getCalendar().getListCalendar__thursday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");

        	// Friday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
            	out.print(((ValueList)this.getCalendar().getListCalendar__friday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");

        	// Saturday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		out.print(((ValueList)this.getCalendar().getListCalendar__saturday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");
        	
        	// Sunday
          	daytypesJourneyPattern = (String)((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(1); 
        	daytypesService = (String)((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(2); 
        	if (daytypesService == null)
        		daytypesService = "";
        	if (daytypesService.equals(serviceId) && daytypesJourneyPattern.length() == 0)
        		out.print(((ValueList)this.getCalendar().getListCalendar__sunday().get(i)).getValue(0));
        	else
        		out.print("0");
        	out.print(",");

        	// Start and end dates
        	out.print(((ValueList)this.getCalendar().getListCalendar__start_date().get(i)).getValue(0));
        	out.print(",");
        	out.println(((ValueList)this.getCalendar().getListCalendar__end_date().get(i)).getValue(0));           			
        }       
        out.close();

        // calendar_dates.txt
        // Create file only of there are exceptions or additions
        if (this.getCalendarDates().getListCalendarDates__service_id().size() > 0) { 
        	outfileName = calendar_datesFilename + /* "_" + serviceStartDate + */ extension;
        	outfile = new File(outdir + "/" + serviceStartDate + "/" + outfileName);
        	out = new PrintWriter(new FileWriter(outfile));
        	filenames.add(outfileName);
        	out.println("service_id,date,exception_type");
        	for (int i = 0; i < this.getCalendarDates().getListCalendarDates__service_id().size(); i++) {
        		out.print(((ValueList)this.getCalendarDates().getListCalendarDates__service_id().get(i)).getValue(0));
        		out.print(",");
        		out.print(((ValueList)this.getCalendarDates().getListCalendarDates__date().get(i)).getValue(0));
        		out.print(",");
        		out.println(((ValueList)this.getCalendarDates().getListCalendarDates__exception_type().get(i)).getValue(0));           			
        	}       
        	out.close();
        }
        
        // Compress the files
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outdir + "/" + serviceStartDate + "/" + "google_transit.zip"));
        byte[] buf = new byte[1024]; // Create a buffer for reading the files
        for (int i = 0; i < filenames.size(); i++) {
            FileInputStream in = new FileInputStream(outdir + "/" + serviceStartDate + "/" + (String)filenames.get(i));
    
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
        return workDirectory + "/" + serviceStartDate + "/" + "google_transit.zip";
	}
		
	/*
	 * Initialize Google Transit Feed data structures
	 */
	public TransxchangeHandler () 
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
