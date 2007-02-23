/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * File:    TransxchangeStopTimes.java 
 * Version:	1.1
 * Date: 	22-Feb-2007
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
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;



public class TransxchangeStopTimes extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_stop_times__trip_id = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "OpenRequired"}; // Google Transit required
	static final String[] key_stop_times__arrival_time = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "OpenRequired"}; // Google Transit required
	static final String[] key_stop_times__departure_time = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "OpenRequired"}; // Google Transit required
	static final String[] key_stop_times__stop_id = new String[] {"StopPointRef", "OpenRequired"}; // Google Transit required
	static final String[] key_stop_times__stop_sequence = new String[] {"__transxchange2GoogleTransit_drawDefault", "OpenRequired"}; // Google Transit required
	static final String[] key_stop_times__pickup_type = new String[] {"NormalStopping", "", "0"}; // Google Transit required
	static final String[] key_stop_times__drop_off_type = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "0"}; // Google Transit required

	// Parsed data 
	List listStoptimes__trip_id;
	ValueList newStoptimes__trip_id;
	List listStoptimes__arrival_time;
	ValueList newStoptimes__arrival_time;
	List listStoptimes__departure_time;
	ValueList newStoptimes__departure_time;
	List listStoptimes__stop_id;
	ValueList newStoptimes__stop_id;
	List listStoptimes__stop_sequence;
	ValueList newStoptimes__stop_sequence;
	List listStoptimes__pickup_type;
	ValueList newStoptimes__pickup_type;
	List listStoptimes__drop_off_type;
	ValueList newStoptimes__drop_off_type;

	// in support of: activity in VehicleJourny at timing point, e.g. pass timing point
	static final String[] _key_trips_activity_pass = new String [] {"VehicleJourney", "JourneyPatternTimingLinkRef", "To", "Activity", "pass"};
	static final String[] _key_trips_activity_pickup = new String [] {"VehicleJourney", "JourneyPatternTimingLinkRef", "From", "Activity", "pickUp"};
	static final String[] _key_trips_activity_setdown = new String [] {"VehicleJourney", "JourneyPatternTimingLinkRef", "To", "Activity", "setDown"};
	String _journeyPatternTimingLinkRefPass = "";
	List _listTripsJourneyPatternTimingLinkRefPass;
	ValueList newTripsJourneyPatternTimingLinkRefPass;
	String _journeyPatternTimingLinkRefPickup = "";
	List _listTripsJourneyPatternTimingLinkRefPickup;
	ValueList newTripsJourneyPatternTimingLinkRefPickup;
	String _journeyPatternTimingLinkRefSetdown = "";
	List _listTripsJourneyPatternTimingLinkRefSetdown;
	ValueList newTripsJourneyPatternTimingLinkRefSetdown;
	String keyNestedActivity = "";

	// in support of: override runtimes of timing links for individual vehiclejourney
	static final String[] _key_trips_vehicle_journey_runtime = new String [] {"VehicleJourney", "JourneyPatternTimingLinkRef", "RunTime"};
	String _journeyPatternTimingLinkRefRunTime = "";
	List _listTripsTimingLinkRunTime;
	ValueList newTripsTimingLinkRunTime;

	// in support of: unroll stop times in individual trips
	static final String[] _key_journeypattern_section = {"JourneyPatternSection"};
	static final String[] _key_journeypattern_timinglink = {"JourneyPatternSection", "JourneyPatternTimingLink"};
	static final String[] _key_stoptimes_from = new String [] {"JourneyPatternTimingLink", "From", "StopPointRef"};
	static final String[] _key_stoptimes_waittime = new String [] {"JourneyPatternTimingLink", "From", "WaitTime"};
	static final String[] _key_stoptimes_to = new String [] {"JourneyPatternTimingLink", "To", "StopPointRef"};
	static final String[] _key_stoptimes_runtime = new String [] {"JourneyPatternTimingLink", "RunTime"};
	boolean inJourneyPatternSection = false;
	String keyNestedRunTime = "";
	String runTime = "";
	String waitTime = "";
	String journeyPatternTimingLink = "";
	String journeyPatternSection = "";
	List _listTimingLinksJourneyPatternTimingLink;
	ValueList newTimingLinksJourneyPatternTimingLink;
	List _listTimingLinksJourneyPatternSection;
	ValueList newTimingLinksJourneyPatternSection;
	List _listTimingLinksFromStop;
	ValueList newTimingLinksFromStop;
	List _listTimingLinksToStop;
	ValueList newTimingLinksToStop;
	List _listTimingLinksRunTime;
	ValueList newTimingLinksRunTime;

	boolean capturedJourneyPatternTimingLinkRef = false;
	String stopPointFrom = "";
	String stopPointTo = "";
	TransxchangeHandler handler;
	
	public List getListStoptimes__trip_id() {
		return listStoptimes__trip_id;
	}
	public List getListStoptimes__arrival_time() {
		return listStoptimes__arrival_time;
	}
	public List getListStoptimes__departure_time() {
		return listStoptimes__departure_time;
	}
	public List getListStoptimes__stop_id() {
		return listStoptimes__stop_id;
	}
	public List getListStoptimes__stop_sequence() {
		return listStoptimes__stop_sequence;
	}
	public List getListStoptimes__pickup_type() {
		return listStoptimes__pickup_type;
	}
	public List getListStoptimes__drop_off_type() {
		return listStoptimes__drop_off_type;
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
	    int qualifierIx;

	    super.startElement(uri, name, qName, atts);
		if (key.equals(_key_trips_activity_pass[0]) && qName.equals(_key_trips_activity_pass[1]))
			keyNested = _key_trips_activity_pass[1];
		if (key.equals(_key_trips_activity_pass[0]) && keyNested.equals(_key_trips_activity_pass[1]) && qName.equals(_key_trips_activity_pass[2]))
			keyNestedActivity = _key_trips_activity_pass[2];
		if (key.equals(_key_trips_activity_pickup[0]) && qName.equals(_key_trips_activity_pickup[1])) 
			keyNested = _key_trips_activity_pickup[1];
		if (key.equals(_key_trips_activity_pickup[0]) && keyNested.equals(_key_trips_activity_pickup[1]) && qName.equals(_key_trips_activity_pickup[2])) 
			keyNestedActivity = _key_trips_activity_pickup[2];
		if (key.equals(_key_trips_activity_pickup[0]) && keyNested.equals(_key_trips_activity_pickup[1]) && keyNestedActivity.equals(_key_trips_activity_pickup[2]) && qName.equals(_key_trips_activity_pickup[3]))
			key = _key_trips_activity_pickup[0];
		if (key.equals(_key_trips_activity_setdown[0]) && qName.equals(_key_trips_activity_setdown[1])) {
			keyNested = _key_trips_activity_setdown[1];
			capturedJourneyPatternTimingLinkRef = false;
		}
		if (key.equals(_key_trips_activity_setdown[0]) && keyNested.equals(_key_trips_activity_setdown[1]) && qName.equals(_key_trips_activity_setdown[2]))
			keyNestedActivity = _key_trips_activity_setdown[2];
		if (qName.equals(_key_trips_vehicle_journey_runtime[0]))
			key = _key_trips_vehicle_journey_runtime[0];
		if (key.equals(_key_trips_vehicle_journey_runtime[0]) && qName.equals(_key_trips_vehicle_journey_runtime[1]))
			keyNested = _key_trips_vehicle_journey_runtime[1];
		if (key.equals(_key_trips_vehicle_journey_runtime[0]) && keyNested.equals(_key_trips_vehicle_journey_runtime[1]) && qName.equals(_key_trips_vehicle_journey_runtime[2]))
			keyNestedActivity = _key_trips_vehicle_journey_runtime[2];

		// Journey pattern runtimes from and to stop points
		if (qName.equals(_key_journeypattern_section[0])) {
			inJourneyPatternSection = !inJourneyPatternSection;
	        qualifierIx = atts.getIndex("id");
	        journeyPatternSection = atts.getValue(qualifierIx);      
		}
		if (inJourneyPatternSection && qName.equals(_key_journeypattern_timinglink[1])) {
	        qualifierIx = atts.getIndex("id");
	        journeyPatternTimingLink = atts.getValue(qualifierIx);      
		}
		if (key.equals(_key_stoptimes_from[0]) && (keyNested.equals(_key_stoptimes_from[1])) && qName.equals(_key_stoptimes_from[2]))
			keyNestedRunTime = _key_stoptimes_from[2];
		if (key.equals(_key_stoptimes_waittime[0]) && (keyNested.equals(_key_stoptimes_waittime[1])) && qName.equals(_key_stoptimes_waittime[2]))
			keyNestedRunTime = _key_stoptimes_waittime[2];
		if (key.equals(_key_stoptimes_to[0]) && (keyNested.equals(_key_stoptimes_to[1])) && qName.equals(_key_stoptimes_to[2]))
			keyNestedRunTime = _key_stoptimes_to[2];
		if (key.equals(_key_stoptimes_to[0]) && qName.equals(_key_stoptimes_to[1]))
			keyNested = _key_stoptimes_to[1];
		if (key.equals(_key_stoptimes_from[0]) && qName.equals(_key_stoptimes_from[1]))
			keyNested = _key_stoptimes_from[1];
		if (key.equals(_key_stoptimes_waittime[0]) && qName.equals(_key_stoptimes_waittime[1]))
			keyNested = _key_stoptimes_waittime[1];
		if (key.equals(_key_stoptimes_runtime[0]) && qName.equals(_key_stoptimes_runtime[1]))
			keyNested = _key_stoptimes_runtime[1];
		if (qName.equals(_key_stoptimes_from[0])) 	// this also covers _runtime and _waittime
			key = _key_stoptimes_from[0];
	}
	

	public void endElement (String uri, String name, String qName) {
		if (niceString.length() == 0)
			return;
		
		String _vehicleJourneyCode = handler.getTrips().getVehicleJourneyCode();
		String _departureTime = handler.getTrips().getDepartureTime();
		
	    if (key.equals(_key_trips_activity_pass[0]) && keyNested.equals(_key_trips_activity_pass[1]) && keyNestedActivity.length() == 0) 
	    	_journeyPatternTimingLinkRefPass = niceString;       	
	    if (key.equals(_key_trips_activity_pickup[0]) && keyNested.equals(_key_trips_activity_pickup[1]) && keyNestedActivity.length() == 0) 
	    	_journeyPatternTimingLinkRefPickup = niceString;       	
	    if (key.equals(_key_trips_activity_setdown[0]) && keyNested.equals(_key_trips_activity_setdown[1]) && keyNestedActivity.length() == 0 && !capturedJourneyPatternTimingLinkRef)
	   		_journeyPatternTimingLinkRefSetdown = niceString;
	    if (key.equals(_key_trips_vehicle_journey_runtime[0]) && keyNested.equals(_key_trips_vehicle_journey_runtime[1]) && keyNestedActivity.length() == 0) 
	    	_journeyPatternTimingLinkRefRunTime = niceString;       	
	    if (key.equals(_key_trips_activity_pass[0]) && keyNested.equals(_key_trips_activity_pass[1]) && keyNestedActivity.equals(_key_trips_activity_pass[2]) && niceString.equals(_key_trips_activity_pass[4])) {
	    	newTripsJourneyPatternTimingLinkRefPass = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    	_listTripsJourneyPatternTimingLinkRefPass.add(newTripsJourneyPatternTimingLinkRefPass);
	    	newTripsJourneyPatternTimingLinkRefPass.addValue(_journeyPatternTimingLinkRefPass);
	    }
	    if (key.equals(_key_trips_activity_pickup[0]) && keyNested.equals(_key_trips_activity_pickup[1]) && keyNestedActivity.equals(_key_trips_activity_pickup[2]) && niceString.equals(_key_trips_activity_pickup[4])) {
	    	newTripsJourneyPatternTimingLinkRefPickup = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    	_listTripsJourneyPatternTimingLinkRefPickup.add(newTripsJourneyPatternTimingLinkRefPickup);
	    	newTripsJourneyPatternTimingLinkRefPickup.addValue(_journeyPatternTimingLinkRefPickup);
	    }
	    if (key.equals(_key_trips_activity_setdown[0]) && keyNested.equals(_key_trips_activity_setdown[1]) && keyNestedActivity.equals(_key_trips_activity_setdown[2]) && niceString.equals(_key_trips_activity_setdown[4])) {
	    	newTripsJourneyPatternTimingLinkRefSetdown = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    	_listTripsJourneyPatternTimingLinkRefSetdown.add(newTripsJourneyPatternTimingLinkRefSetdown);
	    	newTripsJourneyPatternTimingLinkRefSetdown.addValue(_journeyPatternTimingLinkRefSetdown);
			_journeyPatternTimingLinkRefSetdown = "";
	    }
	    if (key.equals(_key_trips_vehicle_journey_runtime[0]) && keyNested.equals(_key_trips_vehicle_journey_runtime[1]) && keyNestedActivity.equals(_key_trips_vehicle_journey_runtime[2])) {
	    	newTripsTimingLinkRunTime = new ValueList(_vehicleJourneyCode + "@" + _departureTime);
	    	_listTripsTimingLinkRunTime.add(newTripsTimingLinkRunTime);
	    	newTripsTimingLinkRunTime.addValue(_journeyPatternTimingLinkRefRunTime);
	    	newTripsTimingLinkRunTime.addValue(niceString);
	    }

		// Journey pattern runtimes from and to stop points
	    if (key.equals(_key_stoptimes_from[0]) && keyNested.equals(_key_stoptimes_from[1])&& keyNestedRunTime.equals(_key_stoptimes_from[2])) {
	    	stopPointFrom = niceString; 
	    	keyNestedRunTime = "";
	    }
	    if (key.equals(_key_stoptimes_waittime[0]) && keyNested.equals(_key_stoptimes_waittime[1])&& keyNestedRunTime.equals(_key_stoptimes_waittime[2])) {
	    	waitTime = niceString; 
	    	keyNestedRunTime = "";
	    }
	    if (key.equals(_key_stoptimes_to[0]) && keyNested.equals(_key_stoptimes_to[1])&& keyNestedRunTime.equals(_key_stoptimes_to[2])) {
	    	stopPointTo = niceString; 
	    	keyNestedRunTime = "";
	    	keyNested = "";
	    }
	    if (key.equals(_key_stoptimes_runtime[0]) && keyNested.equals(_key_stoptimes_runtime[1])) {  
	    	runTime = niceString;
	    }
	    if (key.equals(_key_stoptimes_runtime[0]) && keyNested.equals(_key_stoptimes_runtime[1]) && stopPointFrom.length() > 0) {        
	    	newTimingLinksFromStop = new ValueList(_key_stoptimes_from[1]);
	    	_listTimingLinksFromStop.add(newTimingLinksFromStop);
	    	newTimingLinksFromStop.addValue(stopPointFrom);
	    	newTimingLinksToStop = new ValueList(_key_stoptimes_to[1]);
	    	_listTimingLinksToStop.add(newTimingLinksToStop);
	    	newTimingLinksToStop.addValue(stopPointTo);
	    	newTimingLinksRunTime = new ValueList(stopPointFrom);
	    	_listTimingLinksRunTime.add(newTimingLinksRunTime);
	    	newTimingLinksRunTime.addValue(niceString);
	    	if (waitTime.length() > 0) {
	        	newTimingLinksRunTime.addValue(waitTime);
	        	waitTime = "";
	      	}
	    	stopPointFrom = "";
	    	newTimingLinksJourneyPatternSection = new ValueList(_key_journeypattern_section[0]);
	    	_listTimingLinksJourneyPatternSection.add(newTimingLinksJourneyPatternSection);
	    	newTimingLinksJourneyPatternSection.addValue(journeyPatternSection);
	    	newTimingLinksJourneyPatternTimingLink = new ValueList(_key_journeypattern_timinglink[0]);
	    	_listTimingLinksJourneyPatternTimingLink.add(newTimingLinksJourneyPatternTimingLink);
	    	newTimingLinksJourneyPatternTimingLink.addValue(journeyPatternTimingLink);
	    }		
	}

	
	public void clearKeys (String qName) {
		if (key.equals(_key_trips_activity_pass[0]) && keyNested.equals(_key_trips_activity_pass[1]) && keyNestedActivity.equals(_key_trips_activity_pass[2])) {
			keyNestedActivity = "";
			_journeyPatternTimingLinkRefSetdown = "";
		}
		if (key.equals(_key_trips_activity_pickup[0]) && keyNested.equals(_key_trips_activity_pickup[1]) && keyNestedActivity.equals(_key_trips_activity_pickup[2])) { 
			keyNestedActivity = "";
		}
		if (key.equals(_key_trips_activity_setdown[0]) && qName.equals(_key_trips_activity_setdown[1]))
			capturedJourneyPatternTimingLinkRef = true;
		if (key.equals(_key_trips_vehicle_journey_runtime[0]) && keyNested.equals(_key_trips_vehicle_journey_runtime[1]) && keyNestedActivity.equals(_key_trips_vehicle_journey_runtime[2]))
			keyNestedActivity = "";
	    
		// VehicleJourneys
		if (keyNestedRunTime.equals(_key_stoptimes_from[2]))
			keyNestedRunTime = "";
		if (qName.equals(_key_journeypattern_section[0])) {
			inJourneyPatternSection = !inJourneyPatternSection;
			journeyPatternSection = "";
			key = "";
		}		
	}
	
	public void endDocument() {
	    List _listJourneyPatternRef;
	    List _listJourneyPatternSectionRefs;
	    List listTrips__trip_id;
	    int i, j, k, l, jp;
	    ValueList iterator, jterator, jpterator;
	    String journeyPatternRef, journeyPatternSectionRef = "", setDownTimingLink, vehicleJourneyRef, pickupTimingLink, passTimingLink;
	    boolean hot, jps, setDownReached, pickedUp, notPassed = true, timingLinkOverrideFound;
	    int waitTimeAdd, runTimeAdd, lastStopOnPattern = 0;

		// Roll out stop times
	    _listJourneyPatternRef = handler.getTrips().getListJourneyPatternRef();
	    _listJourneyPatternSectionRefs = handler.getTrips().getListJourneyPatternSectionRefs();
	    listTrips__trip_id = handler.getTrips().getListTrips__trip_id();
	    for (i = 0; i < _listJourneyPatternRef.size(); i++) { // for all trips
	    	iterator = (ValueList)_listJourneyPatternRef.get(i);
	    	journeyPatternRef = (String)iterator.getValue(0);
	    	jp = 0;
/* Java 1.5	       	Integer sequenceNumber = 1;     	
	       	Integer stopTimehhmmss[] = {-1, -1, -1};
*/	       	int sequenceNumber = 1;     	
	       	int stopTimehhmmss[] = {-1, -1, -1};
	       	while (jp < _listJourneyPatternSectionRefs.size()) { // for all referenced journeyPatternSections (stop sequence with timing links)
	        	jps = true;
	        	jpterator = (ValueList)_listJourneyPatternSectionRefs.get(jp);
	       		if (jpterator.getKeyName().equals(journeyPatternRef)) {
	       			journeyPatternSectionRef = (String)jpterator.getValue(0);
	       			jps = false;
	       		}
	       		jp++;
	       		if (!jps) { // JourneyPatternSection found 
	       			j = 0; // Find out if this vehicle journey (as identified by iterator.geyKeyName())has a setDown (= premature end) and store link in setDownTimingLink
	       			hot = true;
	       			setDownTimingLink = "";
	       			while (hot && j < _listTripsJourneyPatternTimingLinkRefSetdown.size()) {
	       				vehicleJourneyRef = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefSetdown.get(j)).getKeyName();
	       				if (iterator.getKeyName().equals(vehicleJourneyRef)) {
	       					hot = false;
	       					setDownTimingLink = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefSetdown.get(j)).getValue(0);
	       				} else
	       					j++;
	       			}

	       			j = 0; // Find out if this vehicle journey has a late pickup and store link in pickUpTimingLink
	       			hot = true;
	       			pickupTimingLink = "";
	       			while (hot && j < _listTripsJourneyPatternTimingLinkRefPickup.size()) {
	       				vehicleJourneyRef = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefPickup.get(j)).getKeyName();
	       				if (iterator.getKeyName().equals(vehicleJourneyRef)) {
	       					hot = false;
	       					pickupTimingLink = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefPickup.get(j)).getValue(0);
	       				} else
	       					j++;
	       			}
	       	
	       			j = 0;
	       			setDownReached = false;
	       			pickedUp = (pickupTimingLink.length() == 0); // If no late pickup in vehicle journey, then start at first stop
	       			while (!setDownReached && j < _listTimingLinksJourneyPatternSection.size()) { // Unroll stop sequence in trip
	       				jterator = (ValueList)_listTimingLinksJourneyPatternSection.get(j);
	       				if (jterator.getValue(0).equals(journeyPatternSectionRef)) {
	       					if (sequenceNumber == 1)
	       						readTransxchangeTime(stopTimehhmmss, (String)((ValueList)listTrips__trip_id.get(i)).getValue(0));
	       					hot = true;
	       					k = 0; // Find out if this stop is being passed
	       					while (hot && k < _listTripsJourneyPatternTimingLinkRefPass.size()) {
	       						passTimingLink = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefPass.get(k)).getValue(0);
	       						vehicleJourneyRef = (String)((ValueList)_listTripsJourneyPatternTimingLinkRefPass.get(k)).getKeyName();
	       						if (iterator.getKeyName().equals(vehicleJourneyRef) && passTimingLink.equals((String)((ValueList)_listTimingLinksJourneyPatternTimingLink.get(j)).getValue(0)))
	       							hot = false;
	       						else
	       							k++;
	       					}
	       					// Find out if we reached late pickup stop
	       					if (!pickedUp && pickupTimingLink.equals((String)((ValueList)_listTimingLinksJourneyPatternTimingLink.get(j)).getValue(0)))
	       						pickedUp = true;
	       					if (pickedUp) {
	       						newStoptimes__trip_id = new ValueList(iterator.getKeyName());
	       						listStoptimes__trip_id.add(newStoptimes__trip_id);
	       						newStoptimes__trip_id.addValue(journeyPatternRef);			
	       						newStoptimes__arrival_time = new ValueList(iterator.getKeyName());
	       						listStoptimes__arrival_time.add(newStoptimes__arrival_time);
	       						if (sequenceNumber > 1 && notPassed && !setDownReached) // Arrival time if not first stop and not a pass
/* Java 1.5	       							newStoptimes__arrival_time.addValue(String.format("%02d:%02d:00", stopTimehhmmss[0], stopTimehhmmss[1]));
*/       							newStoptimes__arrival_time.addValue(TransxchangeDataAspect.formatTime(stopTimehhmmss[0], stopTimehhmmss[1]));
	       						else
	       							newStoptimes__arrival_time.addValue("");
	       						if (((ValueList)_listTimingLinksRunTime.get(j)).getValue(1) != null) { // add wait time?
	       							waitTimeAdd = readTransxchangeFrequency((String)((ValueList)_listTimingLinksRunTime.get(j)).getValue(1));
	           						stopTimehhmmss[1] += waitTimeAdd;
	           						if (stopTimehhmmss[1] >= 60) {
	           							stopTimehhmmss[1] -= 60;
	           							stopTimehhmmss[0] += 1;
	           						}
	       						}
	       						newStoptimes__departure_time = new ValueList(iterator.getKeyName());
	       						listStoptimes__departure_time.add(newStoptimes__departure_time);
	       						if (notPassed && !setDownReached) // Departure time if no pass, else empty
/* Java 1.5	       							newStoptimes__departure_time.addValue(String.format("%02d:%02d:00", stopTimehhmmss[0], stopTimehhmmss[1]));
*/	       							newStoptimes__departure_time.addValue(TransxchangeDataAspect.formatTime(stopTimehhmmss[0], stopTimehhmmss[1]));
	       						else
	       							newStoptimes__departure_time.addValue("");
	       						newStoptimes__stop_id = new ValueList(journeyPatternSectionRef); 
	       						listStoptimes__stop_id.add(newStoptimes__stop_id);
	       						newStoptimes__stop_id.addValue((String)((ValueList)_listTimingLinksFromStop.get(j)).getValue(0));
	       						newStoptimes__stop_sequence = new ValueList(journeyPatternSectionRef); 
	       						listStoptimes__stop_sequence.add(newStoptimes__stop_sequence);
/* Java 1.5	       						newStoptimes__stop_sequence.addValue(sequenceNumber.toString());
*/								{
									Integer sn;
									sn = new Integer(sequenceNumber);
									newStoptimes__stop_sequence.addValue(sn.toString());
								}
	       						sequenceNumber++;
	            	
	       						// Find out if timing link runtime is overridden by vehicle journey specific run times
	       						l = 0;
	       						timingLinkOverrideFound = false;
	       						while (!timingLinkOverrideFound && l < _listTripsTimingLinkRunTime.size()) {
	       							if (iterator.getKeyName().equals((String)((ValueList)_listTripsTimingLinkRunTime.get(l)).getKeyName()) && ((String)((ValueList)_listTimingLinksJourneyPatternTimingLink.get(j)).getValue(0)).equals((String)((ValueList)_listTripsTimingLinkRunTime.get(l)).getValue(0))) 
	       								timingLinkOverrideFound = true;
	       							else
	       								l++;
	       						}
	       						if (timingLinkOverrideFound)
	       							runTimeAdd = readTransxchangeFrequency((String)((ValueList)_listTripsTimingLinkRunTime.get(l)).getValue(1));
	       						else
	       							runTimeAdd = readTransxchangeFrequency((String)((ValueList)_listTimingLinksRunTime.get(j)).getValue(0));
	       						stopTimehhmmss[1] += runTimeAdd;
	       						if (stopTimehhmmss[1] >= 60) {
	       							stopTimehhmmss[1] -= 60;
	       							stopTimehhmmss[0] += 1;
	       						}
	       						newStoptimes__pickup_type = new ValueList(key_stop_times__pickup_type[0]);
	       						listStoptimes__pickup_type.add(newStoptimes__pickup_type);
	       						newStoptimes__pickup_type.addValue(key_stop_times__pickup_type[2]);
	       						newStoptimes__drop_off_type = new ValueList(key_stop_times__drop_off_type[0]);
	       						listStoptimes__drop_off_type.add(newStoptimes__drop_off_type);
	       						newStoptimes__drop_off_type.addValue(key_stop_times__drop_off_type[2]);
	   			
	       						lastStopOnPattern = j;   			
	       						notPassed = hot;
	       					}
	    			
	       					// Find out if setdown has been reached
	       					if (setDownTimingLink.equals((String)((ValueList)_listTimingLinksJourneyPatternTimingLink.get(j)).getValue(0)))
	       						setDownReached = true;
	       				} 
	       				j++;
	       			}
	       		}
	       	}
	   		// Add last stop in vehicle journey
	   		if (_listTimingLinksJourneyPatternSection.size() > 0) { // && jp == _listJourneyPatternSectionRefs.size()) { 
	   			newStoptimes__trip_id = new ValueList(iterator.getKeyName());
	   			listStoptimes__trip_id.add(newStoptimes__trip_id);
	   			newStoptimes__trip_id.addValue(journeyPatternRef);			
	   			newStoptimes__arrival_time = new ValueList(iterator.getKeyName());
	   			listStoptimes__arrival_time.add(newStoptimes__arrival_time);
	   			if (notPassed)
/* Java 1.5	   				newStoptimes__arrival_time.addValue(String.format("%02d:%02d:00", stopTimehhmmss[0], stopTimehhmmss[1]));
*/   				newStoptimes__arrival_time.addValue(TransxchangeDataAspect.formatTime(stopTimehhmmss[0], stopTimehhmmss[1]));
	   			else
	   				newStoptimes__arrival_time.addValue("");
	   			newStoptimes__departure_time = new ValueList(iterator.getKeyName()); // empty departure time
	   			listStoptimes__departure_time.add(newStoptimes__departure_time);
	   			newStoptimes__departure_time.addValue("");   	    	
	   			newStoptimes__stop_id = new ValueList(journeyPatternSectionRef); 
	   			listStoptimes__stop_id.add(newStoptimes__stop_id);
	   			newStoptimes__stop_id.addValue((String)((ValueList)_listTimingLinksToStop.get(lastStopOnPattern)).getValue(0));   			
	   			newStoptimes__stop_sequence = new ValueList(journeyPatternSectionRef); 
	   			listStoptimes__stop_sequence.add(newStoptimes__stop_sequence);
/* Java 1.5	   			newStoptimes__stop_sequence.addValue(sequenceNumber.toString());
*/				{
					Integer sn;
					sn = new Integer(sequenceNumber);
					newStoptimes__stop_sequence.addValue(sn.toString());
				}
	   			newStoptimes__pickup_type = new ValueList(key_stop_times__pickup_type[0]);
	   			listStoptimes__pickup_type.add(newStoptimes__pickup_type);
	   			newStoptimes__pickup_type.addValue(key_stop_times__pickup_type[2]);
	   			newStoptimes__drop_off_type = new ValueList(key_stop_times__drop_off_type[0]);
	   			listStoptimes__drop_off_type.add(newStoptimes__drop_off_type);
	   			newStoptimes__drop_off_type.addValue(key_stop_times__drop_off_type[2]);
	   		}
	    }
		
	}
	
	public void completeData() {
  	    // Add quotes if needed
  	    csvProofList(listStoptimes__trip_id);
  	    csvProofList(listStoptimes__arrival_time);
  	    csvProofList(listStoptimes__departure_time);
  	    csvProofList(listStoptimes__stop_id);
  	    csvProofList(listStoptimes__stop_sequence);
  	    csvProofList(listStoptimes__pickup_type);
  	    csvProofList(listStoptimes__drop_off_type);
	}

	public void dumpValues() {
		int i;
		ValueList iterator;

		System.out.println("*** Timing Links Pass");
	    for (i = 0; i < _listTripsJourneyPatternTimingLinkRefPass.size(); i++) {
		    iterator = (ValueList)_listTripsJourneyPatternTimingLinkRefPass.get(i);
		    iterator.dumpValues();
		}
		System.out.println("*** Timing Links Pickup");  
	    for (i = 0; i < _listTripsJourneyPatternTimingLinkRefPickup.size(); i++) {
		    iterator = (ValueList)_listTripsJourneyPatternTimingLinkRefPickup.get(i);
		    iterator.dumpValues();
		}
		System.out.println("*** Timing Links Setdown");  
	    for (i = 0; i < _listTripsJourneyPatternTimingLinkRefSetdown.size(); i++) {
		    iterator = (ValueList)_listTripsJourneyPatternTimingLinkRefSetdown.get(i);
		    iterator.dumpValues();
		}
		System.out.println("*** Timing Links Runtimes");  
	    for (i = 0; i < _listTripsTimingLinkRunTime.size(); i++) {
		    iterator = (ValueList)_listTripsTimingLinkRunTime.get(i);
		    iterator.dumpValues();
		}
//		for (int i = 0; i < listTrips__block_id.size(); i++) {
//		    iterator = (ValueList)listTrips__block_id.get(i);
//		    iterator.dumpValues();
//		}
		System.out.println("*** Timing Links");
		for (i = 0; i < _listTimingLinksFromStop.size(); i++) {
		    iterator = (ValueList)_listTimingLinksFromStop.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < _listTimingLinksToStop.size(); i++) {
		    iterator = (ValueList)_listTimingLinksToStop.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < _listTimingLinksRunTime.size(); i++) {
		    iterator = (ValueList)_listTimingLinksRunTime.get(i);
		    iterator.dumpValues();
		} 
		for (i = 0; i < _listTimingLinksJourneyPatternSection.size(); i++) {
		    iterator = (ValueList)_listTimingLinksJourneyPatternSection.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < _listTimingLinksJourneyPatternTimingLink.size(); i++) {
		    iterator = (ValueList)_listTimingLinksJourneyPatternTimingLink.get(i);
		    iterator.dumpValues();
		}

		System.out.println("*** Stop Times");
		for (i = 0; i < listStoptimes__trip_id.size(); i++) {
		    iterator = (ValueList)listStoptimes__trip_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listStoptimes__stop_id.size(); i++) {
		    iterator = (ValueList)listStoptimes__stop_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listStoptimes__stop_sequence.size(); i++) {
		    iterator = (ValueList)listStoptimes__stop_sequence.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listStoptimes__arrival_time.size(); i++) {
		    iterator = (ValueList)listStoptimes__arrival_time.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listStoptimes__departure_time.size(); i++) {
		    iterator = (ValueList)listStoptimes__departure_time.get(i);
		    iterator.dumpValues();
		}
//		for (i = 0; i < listStoptimes__pickup_type.size(); i++) {
//		    iterator = (ValueList)listStoptimes__pickup_type.get(i);
//		    iterator.dumpValues();
//		}
//		for (i = 0; i < listStoptimes__drop_off_type.size(); i++) {
//		    iterator = (ValueList)listStoptimes__drop_off_type.get(i);
//		    iterator.dumpValues();
//		}		
	}
	
	
	public TransxchangeStopTimes(TransxchangeHandler owner) {
		listStoptimes__trip_id = new ArrayList();
		listStoptimes__arrival_time = new ArrayList();
		listStoptimes__departure_time = new ArrayList();
		listStoptimes__stop_id = new ArrayList();
		listStoptimes__stop_sequence = new ArrayList();
		listStoptimes__pickup_type = new ArrayList();
		listStoptimes__drop_off_type = new ArrayList();
		
		_listTripsJourneyPatternTimingLinkRefPass = new ArrayList();
		_listTripsJourneyPatternTimingLinkRefPickup = new ArrayList();
		_listTripsJourneyPatternTimingLinkRefSetdown = new ArrayList();
		_listTripsTimingLinkRunTime = new ArrayList();
		_listTimingLinksFromStop = new ArrayList();
		_listTimingLinksToStop = new ArrayList();
		_listTimingLinksRunTime = new ArrayList();
		_listTimingLinksJourneyPatternSection = new ArrayList();
		_listTimingLinksJourneyPatternTimingLink = new ArrayList();

		handler = owner;
	}	
}
