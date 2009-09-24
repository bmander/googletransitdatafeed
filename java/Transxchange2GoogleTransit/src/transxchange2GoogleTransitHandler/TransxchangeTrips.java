/*
 * Copyright 2007, 2009 GoogleTransitDataFeed
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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

/* 
 * This class handles the TransXChange xml input file under the aspect of 
 * 	trips
 */
public class TransxchangeTrips extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_trips__route_id = new String[] {"VehicleJourney", "LineRef", "OpenRequired"}; // Google Transit required
	static final String[] key_trips__service_id = new String[] {"VehicleJourney", "ServiceRef", "OpenRequired"}; // Google Transit required
	static final String[] key_trips__trip_id = new String[] {"VehicleJourney", "VehicleJourneyCode", "OpenRequired"}; // Google Transit required
	static final String[] key_trips__trip_headsign = new String [] {"JourneyPattern", "DestinationDisplay", "OpenRequired"};
	static final String[] key_trips__block_id = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};

	// Parsed data
	List listTrips__route_id;      
	ValueList newTrips__route_id;
	List listTrips__service_id;      
	ValueList newTrips__service_id;
	List listTrips__trip_id;      
	ValueList newTrips__trip_id;
	List listTrips__trip_headsign;      
	ValueList newTrips__trip_headsign;
	List listTrips__block_id;      
	ValueList newTrips__block_id;

	static final String[] _key_trips__trip_journeypatternsection = new String [] {"JourneyPattern", "JourneyPatternSectionRefs"};
	List _listJourneyPatternDestinationDisplays;
	ValueList newJourneyPatternDestinationDisplay;
	List _listJourneyPatternRef;
	ValueList newJourneyPatternRef;
	List _listJourneyPatternSectionRefs;
	ValueList newJourneyPatternSectionRefs;

	String _journeyPattern = "";
	
	static final String[] _key_trips_departure_time = new String [] {"VehicleJourney", "DepartureTime"};
	static final String[] _key_trips_endtime = new String [] {"VehicleJourney", "EndTime"};
	static final String[] _key_trips_frequency = new String [] {"VehicleJourney", "ScheduledFrequency"};
	static final String[] _key_trips_journeypatternref = new String [] {"VehicleJourney", "JourneyPatternRef"};
	static final String[] _key_trips_journeypatternref2 = new String [] {"VehicleJourney", "VehicleJourneyRef"};
	String _vehicleJourneyCode = "";
	String _departureTime = "";
	String _endTime  ="";
	String _scheduledFrequency = "";
	String _serviceCode = "";
	String _lineName = "";
	String _journeyPatternRef = "";
	String _vehicleJourneyRef = "";

    int qualifierIx;
	int i;
	boolean found;
	List oolStart;
	List calendarServices;
	int frequency;
	int[] departureTimehhmmss = {-1, -1, -1};
	int[] endTimehhmmss = {-1, -1, -1};
	int departureTimeInSeconds;
	
	public List getListTrips__route_id() {
		return listTrips__route_id;
	}
	public List getListTrips__service_id() {
		return listTrips__service_id;
	}
	public List getListTrips__trip_id() {
		return listTrips__trip_id;
	}
	public List getListTrips__trip_headsign() {
		return listTrips__trip_headsign;
	}
	public List getListTrips__block_id() {
		return listTrips__block_id;
	}
	public List getListJourneyPatternRef() {
		return _listJourneyPatternRef;
	}
	public List getListJourneyPatternSectionRefs() {
		return _listJourneyPatternSectionRefs;
	}
	String getVehicleJourneyCode() {
		return _vehicleJourneyCode;
	}
	String getDepartureTime() {
		return _departureTime;
	}
	String getJourneyPattern() {
		return _journeyPattern;
	}
	void setJourneyPattern(String jp) {
		_journeyPattern = jp;
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {
	
	    super.startElement(uri, name, qName, atts);
		if (qName.equals(key_trips__route_id[0]))
			key = key_trips__route_id[0]; // this also covers trip_service_id, _trip_id, _departure_time, _endtime, _scheduled_frequency, _journeypatternref, _journetpatternref2
		if (key.equals(key_trips__route_id[0]) && qName.equals(key_trips__route_id[1]))
			keyNested = key_trips__route_id[1];
		if (key.equals(key_trips__service_id[0]) && qName.equals(key_trips__service_id[1]))
			keyNested = key_trips__service_id[1];
		if (key.equals(key_trips__trip_id[0]) && qName.equals(key_trips__trip_id[1]))
			keyNested = key_trips__trip_id[1];
		if (key.equals(key_trips__service_id[0]) && qName.equals(key_trips__service_id[1]))
			keyNested = key_trips__service_id[1];
		if (key.equals(_key_trips_departure_time[0]) && qName.equals(_key_trips_departure_time[1]))
			keyNested = _key_trips_departure_time[1];
		if (key.equals(_key_trips_endtime[0]) && qName.equals(_key_trips_endtime[1]))
			keyNested = _key_trips_endtime[1];
		if (key.equals(_key_trips_frequency[0]) && qName.equals(_key_trips_frequency[1]))
			keyNested = _key_trips_frequency[1];
		if (key.equals(_key_trips_journeypatternref[0]) && qName.equals(_key_trips_journeypatternref[1]))
			keyNested = _key_trips_journeypatternref[1];
		if (key.equals(_key_trips_journeypatternref2[0]) && qName.equals(_key_trips_journeypatternref2[1]))
			keyNested = _key_trips_journeypatternref2[1];
		if (qName.equals(key_trips__trip_headsign[0])) {
	        qualifierIx = atts.getIndex("id");
	        _journeyPattern = atts.getValue(qualifierIx);      
			key = key_trips__trip_headsign[0];
		}
		if (key.equals(key_trips__trip_headsign[0]) && qName.equals(key_trips__trip_headsign[1]))
			keyNested = key_trips__trip_headsign[1];
		if (key.equals(_key_trips__trip_journeypatternsection[0]) && qName.equals(_key_trips__trip_journeypatternsection[1]))
			keyNested = _key_trips__trip_journeypatternsection[1];

	}

	public void endElement (String uri, String name, String qName) {
	    boolean hot;

	    /*
	     * v1.5: Local class to create trip structure either for single trip, or unrolled based on frequency (called further below)
	     */
	    class TripStructure {
	    	 void createTripStructure() {
	    		 /*
	    		  * v1.5: Find out if out-of-line calendar dates where picked up earlier and assign to current VehicleJourney
	    		  */
	    		 oolStart = handler.getCalendarDates().getListOOLDates_start();
//	    		 List oolEnd = handler.getCalendarDates().getListOOLDates_end();

	    		 if (oolStart != null) {
	    			 // found out-of-line dates
	    			 calendarServices = handler.getCalendar().getListCalendar__service_id();
	    			 i = 0;
	    			 found = false;
	    			 
	    			 // find matching service in calendar data structure. This assumes the service has already been defined in XML file. This is normally the case
	    			 while (i < calendarServices.size() && !found) {
	    				 if (((ValueList)calendarServices.get(i)).getValue(0).equals(_serviceCode))
	    					 found = true;
	    				 else
	    					 i++;
	    			 }
	    			 if (found) {
	    				 // matching service found
	    				 // generate service key specifically for current VehicleJourney
		    			 handler.getCalendar().calendarDuplicateService(_serviceCode, _serviceCode + "_" + _vehicleJourneyCode + "@" + _departureTime);
		    			 _serviceCode = _serviceCode + "_" + _vehicleJourneyCode + "@" + _departureTime;
		    			 handler.getCalendarDates().calendarDatesRolloutOOLDates(_serviceCode);
	    			 }
	    			 handler.getCalendarDates().resetOOLDates_start();
	    			 handler.getCalendarDates().resetOOLDates_end();
	    		 }	    		 
	    		 newTrips__trip_id = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    		 listTrips__trip_id.add(newTrips__trip_id);
	    		 newTrips__trip_id.addValue(_departureTime);
	    		 newTrips__route_id = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    		 listTrips__route_id.add(newTrips__route_id);
	    		 newTrips__route_id.addValue(_lineName);
	    		 newTrips__service_id = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    		 listTrips__service_id.add(newTrips__service_id);
	    		 newTrips__service_id.addValue(_serviceCode);
	    		 newJourneyPatternRef = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    		 _listJourneyPatternRef.add(newJourneyPatternRef);
	    		 newJourneyPatternRef.addValue(_journeyPatternRef);
	    		 newTrips__block_id = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    		 listTrips__block_id.add(newTrips__block_id);
	    		 newTrips__block_id.addValue(key_trips__block_id[2]);
	    	 }
	    }
	    
		if (niceString == null || niceString.length() == 0) 
	    	return;
	    
        if (key.equals(key_trips__trip_id[0]) && keyNested.equals(key_trips__trip_id[1])) {
        	_vehicleJourneyCode = niceString;
        	keyNested = "";
        } 
        if (key.equals(key_trips__route_id[0]) && keyNested.equals(key_trips__route_id[1])) {
        	_lineName = niceString;
        	keyNested = "";     	
        }
        if (key.equals(key_trips__service_id[0]) && keyNested.equals(key_trips__service_id[1])) {
        	_serviceCode = niceString;
        	keyNested = "";     	
        }
        if (key.equals(_key_trips_journeypatternref[0]) && keyNested.equals(_key_trips_journeypatternref[1])) {
        	_journeyPatternRef = niceString;
        	keyNested = "";     	
        }
        if (key.equals(_key_trips_journeypatternref2[0]) && keyNested.equals(_key_trips_journeypatternref2[1])) {
        	_vehicleJourneyRef = niceString;
        	keyNested = "";     	
        }
        if (key.equals(_key_trips_departure_time[0]) && keyNested.equals(_key_trips_departure_time[1])) {
			_departureTime = niceString;
			new TripStructure().createTripStructure();
			keyNested = "";
        }
        if (key.equals(_key_trips_endtime[0]) && keyNested.equals(_key_trips_endtime[1])) {
        	_endTime = niceString;
        	keyNested = "";
        }
        if (key.equals(_key_trips_frequency[0]) && keyNested.equals(_key_trips_frequency[1])) {
        	_scheduledFrequency = niceString;
        	// Unroll transxchange:vehicle journeys with frequency to google transit:descrete trips. In the first few versions of GTFS, there did not exist frequency.txt
        	frequency = 0;
        	departureTimehhmmss[0] = -1;
        	departureTimehhmmss[1] = -1;
        	departureTimehhmmss[2] = -1;
			endTimehhmmss[0] = -1;
			endTimehhmmss[1] = -1;
			endTimehhmmss[2] = -1;

        	readTransxchangeTime(departureTimehhmmss, _departureTime);
        	readTransxchangeTime(endTimehhmmss, _endTime);
        	frequency = readTransxchangeFrequency(_scheduledFrequency);
        	hot = (frequency > 0);
        	while (hot) { 
        		departureTimeInSeconds = departureTimehhmmss[2] + departureTimehhmmss[1] * 60 + departureTimehhmmss[0] * 3600;
        		departureTimeInSeconds += frequency;
        		departureTimehhmmss[0] = departureTimeInSeconds / 3600;
        		departureTimehhmmss[1] = (departureTimeInSeconds / 60) % 60;
        		departureTimehhmmss[2] = departureTimeInSeconds % 60;
        		if (departureTimehhmmss[0] > endTimehhmmss[0])
        			hot = false;
        		else
        			if (departureTimehhmmss[0] >= endTimehhmmss[0])
        				if (departureTimehhmmss[1] > endTimehhmmss[1])
        					hot = false;
        		if (hot) {
        			_departureTime = TransxchangeDataAspect.formatTime(departureTimehhmmss[0], departureTimehhmmss[1]);
        			new TripStructure().createTripStructure();
        		}
        	}
        	keyNested = "";
        }
        if (key.equals(key_trips__trip_headsign[0]) && keyNested.equals(key_trips__trip_headsign[1])) {
        	newJourneyPatternDestinationDisplay = new ValueList(_journeyPattern);
        	_listJourneyPatternDestinationDisplays.add(newJourneyPatternDestinationDisplay);
        	newJourneyPatternDestinationDisplay.addValue(niceString);
		}
        if (key.equals(_key_trips__trip_journeypatternsection[0]) && keyNested.equals(_key_trips__trip_journeypatternsection[1])) {
        	newJourneyPatternSectionRefs = new ValueList(_journeyPattern);
        	_listJourneyPatternSectionRefs.add(newJourneyPatternSectionRefs);
        	newJourneyPatternSectionRefs.addValue(niceString);
		}
	}
	
	public void clearKeys (String qName) {
		if (qName.equals(key_trips__trip_headsign[1])) 
			keyNested = "";
		if (qName.equals(_key_trips__trip_journeypatternsection[0]))
			key = "";
        if (key.equals(_key_trips__trip_journeypatternsection[0]) && keyNested.equals(_key_trips__trip_journeypatternsection[1])) {
        	keyNested = "";
        }
		if (qName.equals(key_trips__route_id[0])) { // covers all _trip nested keys
			key = "";
			_journeyPattern = ""; 
			_vehicleJourneyCode = "";
			_departureTime = "";
			_endTime = "";
			_scheduledFrequency = "";
			_serviceCode = "";
			_lineName = "";
	    	_vehicleJourneyCode = "";
//	    	_journeyPatternRef = ""; Do not clear _journeyPatternRef - will be needed if following vehicle journey refers first
	    	_vehicleJourneyRef = "";
		}		
	}

	public void endDocument() {
		int i, j;
		boolean hot;
		ValueList iterator, jterator;
	    String tripHeadsign;
		String journeyPatternRef = "";

	    // Roll out trip headsigns
	    for (i = 0; i < _listJourneyPatternRef.size(); i++) {
	    	iterator = (ValueList)_listJourneyPatternRef.get(i);
	    	journeyPatternRef = (String)iterator.getValue(0);
	    	j = 0;
	    	hot = true;
	    	tripHeadsign = "";
	       	while (hot && j < _listJourneyPatternDestinationDisplays.size()) { // find associated destination
	        	jterator = (ValueList)_listJourneyPatternDestinationDisplays.get(j);
	       		if (jterator.getKeyName().equals(journeyPatternRef)) {
	       			tripHeadsign = (String)jterator.getValue(0);
	       			hot = false;
	       		} else
	       			j++;
	       	}
	       	newTrips__trip_headsign = new ValueList(iterator.getKeyName());
	    	listTrips__trip_headsign.add(newTrips__trip_headsign);
	    	newTrips__trip_headsign.addValue(tripHeadsign);	
	    }
	}

	public void completeData() {
  	    // Add quotes if needed
  	    csvProofList(listTrips__route_id);
  	    csvProofList(listTrips__service_id);
  	    csvProofList(listTrips__trip_id);
  	    csvProofList(listTrips__trip_headsign);
  	    csvProofList(listTrips__block_id);
	}
	
	public void dumpValues() {
		ValueList iterator;

		System.out.println("*** Trips");
		for (i = 0; i < listTrips__trip_id.size(); i++) {
		    iterator = (ValueList)listTrips__trip_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < _listJourneyPatternDestinationDisplays.size(); i++) {
		    iterator = (ValueList)_listJourneyPatternDestinationDisplays.get(i);
		    iterator.dumpValues();
		}
	  	for (i = 0; i < _listJourneyPatternSectionRefs.size(); i++) {
		    iterator = (ValueList)_listJourneyPatternSectionRefs.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listTrips__route_id.size(); i++) {
		    iterator = (ValueList)listTrips__route_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listTrips__service_id.size(); i++) {
		    iterator = (ValueList)listTrips__service_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < _listJourneyPatternRef.size(); i++) {
		    iterator = (ValueList)_listJourneyPatternRef.get(i);
		    iterator.dumpValues();
		} 
	}
	
	public TransxchangeTrips(TransxchangeHandlerEngine owner) {
		super(owner);
		listTrips__route_id = new ArrayList();
		listTrips__service_id = new ArrayList();
		listTrips__trip_id = new ArrayList();
		listTrips__trip_headsign = new ArrayList();
		listTrips__block_id = new ArrayList();
		
		_listJourneyPatternRef = new ArrayList();
		_listJourneyPatternDestinationDisplays = new ArrayList();
		_listJourneyPatternSectionRefs = new ArrayList();
	}
}
