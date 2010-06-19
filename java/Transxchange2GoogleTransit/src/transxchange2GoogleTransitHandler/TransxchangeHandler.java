/*
 * Copyright 2007, 2008, 2009, 2010 GoogleTransitDataFeed
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

import java.util.zip.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/*
 * This class extends DefaultHandler to parse a TransXChange v2.1 xml file,	
 * 	build corresponding Google Transit Feed data structures
 *  and write these to a Google Transit Feed Specification (9-Apr-2007) compliant file set
 */
public class TransxchangeHandler {

	// Additional contributions to resulting Google Transit file set which cannot be extracted from a TransXChange input file
	static String googleTransitUrl = "";
	static String googleTransitTimezone = "";
	static String googleTransitDefaultRouteType = "";
	static String googleTransitOutfile = "";
	
	static TransxchangeHandlerEngine parseHandler = null;
	static List parseHandlers = null;
	
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
		
	/*
	 * Generate Google Transit Feed structures
	 */
	public void parse(String filename, String url, String timezone, String defaultRouteType,
			String rootDirectory, String workDirectory, String stopFile,
			boolean useAgencyShortName)
	    throws SAXException, SAXParseException, IOException, ParserConfigurationException
	{
		ZipFile zipfile = null;
		boolean zipinput = true; // v1.5.1: Handle zip files
		boolean processing = true;
		java.util.Enumeration enumer = null;
		
		// Open infile, zip or single xml
		try { // v1.5.1: Try to open filename as zip file
			zipfile = new ZipFile(filename);
		} catch (IOException e) {
			zipinput = false; // Opening file as zip file crashed; assume it is a single XML file
		}
		
		try {
		
			// Prepare output files
			TransxchangeHandlerEngine.prepareOutput(rootDirectory, workDirectory);	
			
			// Read stopfile
			if (stopFile != null && stopFile.length() > 0)
				TransxchangeStops.readStopfile(stopFile);
	
			// Roll single as well as zipped infiles into a unified data structure for later transparent processing (stops only, rest goes straight to output files)
			parseHandlers = new ArrayList();
			if (zipinput)
				enumer = zipfile.entries(); 
			do { 
				parseHandler = new TransxchangeHandlerEngine();	// v1.6.2
				parseHandler.setUrl(url);
				parseHandler.setTimezone(timezone);
				parseHandler.setDefaultRouteType(defaultRouteType);
				parseHandler.setUseAgencyShortname(useAgencyShortName);
		
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser parser = parserFactory.newSAXParser();
	
				if (zipinput) {
					if (processing = enumer.hasMoreElements()) {			
						ZipEntry zipentry = (ZipEntry)enumer.nextElement();
						System.out.println(zipentry.getName());
						InputStream in = zipfile.getInputStream(zipentry);
						parser.parse(in, parseHandler);	
						parseHandler.writeOutputSansAgenciesStopsRoutes(); // Dump data structure with exception of stops which need later consolidation over all input files
						parseHandler.clearDataSansAgenciesStopsRoutes(); // No need to keep the data structures written
					}				
				} else {
					parser.parse(new File(filename), parseHandler);	
					parseHandler.writeOutputSansAgenciesStopsRoutes(); // Dump data structure with exception of stops which need later consolidation over all input files
					processing = false;
				}
				parseHandlers.add(parseHandler);
			} while (processing);
		} catch (IOException e) {
        	System.out.println("TransxchangeHandler Parse Exception: " + e.getMessage());
		}
	}
	
	/*
	 * Create Google Transit Feed file set from Google Transit Feed data structures
	 */
	public String writeOutput(String rootDirectory, String workDirectory)
		throws IOException
	{		
		consolidateAgencies(); // Eliminiate possible duplicates from multiple input files in zip archive
		consolidateStops(); // Eliminiate possible duplicates from multiple input files in zip archive
		consolidateRoutes(); // Eliminiate possible duplicates from multiple input files in zip archive
		Iterator parsers = parseHandlers.iterator(); 
		while (parsers.hasNext())
			((TransxchangeHandlerEngine)parsers.next()).writeOutputAgenciesStopsRoutes(); // Now write agencies, stops
		return TransxchangeHandlerEngine.closeOutput(rootDirectory, workDirectory);
	}
	
	/*
	 * Eliminate possible duplicates from multiple input files in zip archive
	 */
	public void consolidateStops() {
		Iterator parsers = parseHandlers.iterator();
		int parseHandlersCount = 0;
		int j;
		String curStopId;
		ArrayList followStopIds;
		TransxchangeStops followStops;
		Iterator followParser;
		
		while (parsers.hasNext()) {
			TransxchangeStops stops = ((TransxchangeHandlerEngine)parsers.next()).getStops();
			parseHandlersCount += 1;
			ArrayList stopIds = (ArrayList)stops.getListStops__stop_id();
			for (int i = 0; i < stopIds.size(); i++) {
				followParser = parseHandlers.iterator(); // Set follow parser to parsed input files following current; Iterator is not Cloneable; need to create a new Iterator and step forward to get to the right position (anybody know a more elegant solution?)
				j = 0;
				while (j < parseHandlersCount && followParser.hasNext()) {
					j++;
					followParser.next();
				}
				curStopId = (String)((ValueList)stopIds.get(i)).getValue(0);
				while (followParser.hasNext()) { // Run through stops of following parsed input files and eliminate duplicates there
					followStops = ((TransxchangeHandlerEngine)followParser.next()).getStops();
					followStopIds = (ArrayList)followStops.getListStops__stop_id();
					for (j = 0; j < followStopIds.size(); j++) {
						if (curStopId.equals((String)((ValueList)followStopIds.get(j)).getValue(0))) {
							((ValueList)followStopIds.get(j)).setValue(0, "");
						}
					}		
				}		
			}
		}
	}

	/*
	 * Eliminate possible duplicates from multiple input files in zip archive
	 */
	public void consolidateAgencies() {
		Iterator parsers = parseHandlers.iterator();
		int parseHandlersCount = 0;
		int j;
		String curAgencyId;
		ArrayList followAgencyIds;
		TransxchangeAgency followAgencies;
		Iterator followParser;
		
		while (parsers.hasNext()) {
			TransxchangeAgency agencies = ((TransxchangeHandlerEngine)parsers.next()).getAgencies();
			parseHandlersCount += 1;
			ArrayList agencyIds = (ArrayList)agencies.getListAgency__agency_id();
			for (int i = 0; i < agencyIds.size(); i++) {
				followParser = parseHandlers.iterator(); // Set follow parser to parsed input files following current; Iterator is not Cloneable; need to create a new Iterator and step forward to get to the right position (anybody know a more elegant solution?)
				j = 0;
				while (j < parseHandlersCount && followParser.hasNext()) {
					j++;
					followParser.next();
				}
				curAgencyId = (String)((ValueList)agencyIds.get(i)).getValue(0);
				while (followParser.hasNext()) { // Run through stops of following parsed input files and eliminate duplicates there
					followAgencies = ((TransxchangeHandlerEngine)followParser.next()).getAgencies();
					followAgencyIds = (ArrayList)followAgencies.getListAgency__agency_id();
					for (j = 0; j < followAgencyIds.size(); j++) {
						if (curAgencyId.equals((String)((ValueList)followAgencyIds.get(j)).getValue(0))) {
							((ValueList)followAgencyIds.get(j)).setValue(0, "");
						}
					}		
				}		
			}
		}
	}

	/*
	 * Eliminate possible duplicates from multiple input files in zip archive
	 */
	public void consolidateRoutes() {
		Iterator parsers = parseHandlers.iterator();
		int parseHandlersCount = 0;
		int j;
		String curRouteId;
		ArrayList followRouteIds;
		TransxchangeRoutes followRoutes;
		Iterator followParser;
		
		while (parsers.hasNext()) {
			TransxchangeRoutes routes = ((TransxchangeHandlerEngine)parsers.next()).getRoutes();
			parseHandlersCount += 1;
			ArrayList routeIds = (ArrayList)routes.getListRoutes__route_id();
			for (int i = 0; i < routeIds.size(); i++) {
				followParser = parseHandlers.iterator(); // Set follow parser to parsed input files following current; Iterator is not Cloneable; need to create a new Iterator and step forward to get to the right position (anybody know a more elegant solution?)
				j = 0;
				while (j < parseHandlersCount && followParser.hasNext()) {
					j++;
					followParser.next();
				}
				curRouteId = (String)((ValueList)routeIds.get(i)).getValue(0);
				while (followParser.hasNext()) { // Run through stops of following parsed input files and eliminate duplicates there
					followRoutes = ((TransxchangeHandlerEngine)followParser.next()).getRoutes();
					followRouteIds = (ArrayList)followRoutes.getListRoutes__route_id();
					for (j = 0; j < followRouteIds.size(); j++) {
						if (curRouteId.equals((String)((ValueList)followRouteIds.get(j)).getValue(0))) {
							((ValueList)followRouteIds.get(j)).setValue(0, "");
						}
					}		
				}		
			}
		}
	}
}
