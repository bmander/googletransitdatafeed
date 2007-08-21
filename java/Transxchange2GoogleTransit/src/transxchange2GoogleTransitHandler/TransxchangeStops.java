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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

/* 
 * This class handles the TransXChange xml input file under the aspect of 
 * 	stops
 */
public class TransxchangeStops extends TransxchangeDataAspect{

	// xml keys and output field fillers
	static final String[] key_stops__stop_id = new String[] {"StopPoints", "AtcoCode", "OpenRequired"}; // Google Transit required
	static final String[] key_stops__stop_id2 = new String[] {"StopPoints", "StopPointRef", "OpenRequired"}; // Google Transit required
	static final String[] key_stops__stop_name = new String[] {"StopPoints", "CommonName", "OpenRequired"}; // Google Transit required
	static final String[] key_stops__stop_desc = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_stops__stop_lat = new String[] {"StopPoints", "Easting", "OpenRequired"}; // Google Transit required
	static final String[] key_stops__stop_lon = new String[] {"StopPoints", "Northing", "OpenRequired"}; // Google Transit required
	static final String[] key_stops__stop_street = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_stops__stop_city = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_stops__stop_region = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_stops__stop_postcode = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_stops__stop_country = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};

	static final String [] _key_stops__stop_locality = new String[] {"StopPoints", "LocalityName"}; 
	List _listStops__stop_locality;
	ValueList _newStops__stop_locality;
	static final String [] _key_stops__stop_indicator = new String[] {"StopPoints", "Indicator"}; 
	List _listStops__stop_indicator;
	ValueList _newStops__stop_indicator;
	
	static final String[] _key_route_section = {"RouteSection"};
	static final String[] _key_route_link_from = new String [] {"RouteLink", "From", "StopPointRef"};
	static final String[] _key_route_link_to = new String [] {"RouteLink", "To", "StopPointRef"};
	static final String[] _key_route_link_location_x = new String [] {"RouteLink", "Easting"};
	static final String[] _key_route_link_location_y = new String [] {"RouteLink", "Northing"};
	boolean inRouteSection = false;
	String keyNestedLocation = "";
	String stopPointFrom = "";
	String stopPointTo = "";
	String stopPointToLat = ""; // Store current lat of stop-to to maintain lat of last stop in route link
	String stopPointToLon = ""; // same for lon
	
	Map lat; // v1.5 lat, lon
	Map lon; // v1.5 lat, lon
	
	static final String[] _key_stops_alternative_descriptor = new String[] {"StopPoints", "AlternativeDescriptors", "CommonName"};

	String keyRef = "";
		
	// Parsed data 
	List listStops__stop_id;
	ValueList newStops__stop_id;
	List listStops__stop_name;
	ValueList newStops__stop_name;
	List listStops__stop_desc;
	ValueList newStops__stop_desc;
	List listStops__stop_lat;
	ValueList newStops__stop_lat;
	List listStops__stop_lon;
	ValueList newStops__stop_lon;
	List listStops__stop_street;
	ValueList newStops__stop_street;
	List listStops__stop_city;
	ValueList newStops__stop_city;
	List listStops__stop_postcode;
	ValueList newStops__stop_postcode;
	List listStops__stop_region;
	ValueList newStops__stop_region;
	List listStops__stop_country;
	ValueList newStops__stop_country;

	public List getListStops__stop_id() {
		return listStops__stop_id;
	}
	public List getListStops__stop_name() {
		return listStops__stop_name;
	}
	public List getListStops__stop_desc() {
		return listStops__stop_desc;
	}
	public List getListStops__stop_lat() {
		return listStops__stop_lat;
	}
	public List getListStops__stop_lon() {
		return listStops__stop_lon;
	}
	public List getListStops__stop_street() {
		return listStops__stop_street;
	}
	public List getListStops__stop_city() {
		return listStops__stop_city;
	}
	public List getListStops__stop_postcode() {
		return listStops__stop_postcode;
	}
	public List getListStops__stop_region() {
		return listStops__stop_region;
	}
	public List getListStops__stop_country() {
		return listStops__stop_country;
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {
	
		super.startElement(uri, name, qName, atts);
		if (key.equals(key_stops__stop_id[0])) 
			if (qName.equals(key_stops__stop_id[1])) {
				keyNested = key_stops__stop_id[1];
			} 
		if (key.equals(key_stops__stop_id2[0]))
			if (qName.equals(key_stops__stop_id2[1])) {
				keyNested = key_stops__stop_id2[1];
			} 
		if (key.equals(key_stops__stop_name[0]) && keyNested.length() == 0) 
			if (qName.equals(key_stops__stop_name[1])) {
				keyNested = key_stops__stop_name[1];
			} 
		if (key.equals(key_stops__stop_lat[0])) 
			if (qName.equals(key_stops__stop_lat[1])) {
				keyNested = key_stops__stop_lat[1];
			} 
		if (key.equals(key_stops__stop_lon[0])) 
			if (qName.equals(key_stops__stop_lon[1])) {
				keyNested = key_stops__stop_lon[1];
			} 
		if (qName.equals(key_stops__stop_id[0])) 
			key = key_stops__stop_id[0];
		if (key.equals(_key_stops__stop_locality[0]) && qName.equals(_key_stops__stop_locality[1])) {
			keyNested = _key_stops__stop_locality[1];
		}
		if (key.equals(_key_stops__stop_indicator[0]) && qName.equals(_key_stops__stop_indicator[1])) {
			keyNested = _key_stops__stop_indicator[1];
		}
		
		// Route sections (to helper structures)
		// From and to stop points
		if (qName.equals(_key_route_section[0]))
			inRouteSection = !inRouteSection;
		if (key.equals(_key_route_link_from[0]) && (keyNested.equals(_key_route_link_from[1]) || keyNested.equals(_key_route_link_to[1])) && qName.equals(_key_route_link_from[2])) {
			keyNestedLocation = _key_route_link_from[2];
		}
		if (key.equals(_key_route_link_to[0]) && qName.equals(_key_route_link_to[1])) {
			keyNested = _key_route_link_to[1];
		}
		if (key.equals(_key_route_link_from[0]) && qName.equals(_key_route_link_from[1])) {
			keyNested = _key_route_link_from[1];
		}
		if (key.equals(_key_route_link_location_x[0]) && qName.equals(_key_route_link_location_x[1])) {
			keyNested = _key_route_link_location_x[1];
		}
		if (key.equals(_key_route_link_location_y[0]) && qName.equals(_key_route_link_location_y[1])) {
			keyNested = _key_route_link_location_y[1];
		}
		if (qName.equals(_key_route_link_from[0])) 	// this also covers route_link_location_x and _y	
			key = _key_route_link_from[0];
		
		//  Alternative description
		if (key.equals(_key_stops_alternative_descriptor[0]) && qName.equals(_key_stops_alternative_descriptor[1]))
			keyNested = _key_stops_alternative_descriptor[1];
	}

	public void endElement (String uri, String name, String qName) {
		int i;
	    boolean hot;
	    
	    if (niceString.length() == 0) 
	    	return;
	    
	    if (key.equals(key_stops__stop_id[0]) && keyNested.equals(key_stops__stop_id[1])) {
	    	newStops__stop_id = new ValueList(key_stops__stop_id[0]);
	    	listStops__stop_id.add(newStops__stop_id);
	    	newStops__stop_id.addValue(niceString);
	    	keyRef = niceString;
	    }
	    if (key.equals(key_stops__stop_id2[0]) && keyNested.equals(key_stops__stop_id2[1])) { 
	    	newStops__stop_id = new ValueList(key_stops__stop_id2[0]);
	    	listStops__stop_id.add(newStops__stop_id);
	    	newStops__stop_id.addValue(niceString);
	    	keyRef = niceString;
	    }
	    if (key.equals(key_stops__stop_name[0]) && keyNested.equals(key_stops__stop_name[1])) { 
	    	newStops__stop_name = new ValueList(keyRef);
	    	listStops__stop_name.add(newStops__stop_name);
	    	newStops__stop_name.addValue(niceString);
	    	newStops__stop_desc = new ValueList(keyRef); // Default for _desc
	    	listStops__stop_desc.add(newStops__stop_desc);
	    	newStops__stop_desc.addValue(key_stops__stop_desc[2]);
	    	newStops__stop_street = new ValueList(keyRef); // Default for _street
	    	listStops__stop_street.add(newStops__stop_street);
	    	newStops__stop_street.addValue(key_stops__stop_street[2]);
	    	newStops__stop_city = new ValueList(keyRef); // Default for _city
	    	listStops__stop_city.add(newStops__stop_city);
	    	newStops__stop_city.addValue(key_stops__stop_city[2]);
	    	newStops__stop_postcode = new ValueList(keyRef); // Default for _postcode
	    	listStops__stop_postcode.add(newStops__stop_postcode);
	    	newStops__stop_postcode.addValue(key_stops__stop_postcode[2]);
	    	newStops__stop_region = new ValueList(keyRef); // Default for _region
	    	listStops__stop_region.add(newStops__stop_region);
	    	newStops__stop_region.addValue(key_stops__stop_region[2]);
	    	newStops__stop_country = new ValueList(keyRef); // Default for _country
	    	listStops__stop_country.add(newStops__stop_country);
	    	newStops__stop_country.addValue(key_stops__stop_country[2]);
	    }
	    if (key.equals(_key_stops__stop_locality[0]) && keyNested.equals(_key_stops__stop_locality[1])) {
	    	_newStops__stop_locality = new ValueList(keyRef);
	    	_listStops__stop_locality.add(_newStops__stop_locality);
	    	_newStops__stop_locality.addValue(niceString); 	
	    }
	    if (key.equals(_key_stops__stop_indicator[0]) && keyNested.equals(_key_stops__stop_indicator[1])) {
	    	_newStops__stop_indicator = new ValueList(keyRef);
	    	_listStops__stop_indicator.add(_newStops__stop_indicator);
	    	_newStops__stop_indicator.addValue(niceString); 	
	    }
	    if (key.equals(key_stops__stop_lat[0]) && keyNested.equals(key_stops__stop_lat[1])) { 
	    	newStops__stop_lat = new ValueList(keyRef);
	    	listStops__stop_lat.add(newStops__stop_lat);
	    	newStops__stop_lat.addValue(niceString);
       	}
	    if (key.equals(key_stops__stop_lon[0]) && keyNested.equals(key_stops__stop_lon[1])) { 
	       	newStops__stop_lon = new ValueList(keyRef);
	       	listStops__stop_lon.add(newStops__stop_lon);
	       	newStops__stop_lon.addValue(niceString);
       	}

	    // if location of stop unknown from stop points, add location from route section
	    if (key.equals(_key_route_link_location_x[0]) && keyNested.equals(_key_route_link_location_x[1]) && stopPointFrom.length() > 0) {                	
	       	i = 0;
	       	hot = true;
	       	while (hot && i < listStops__stop_lat.size()) {
	       		if (stopPointFrom.equals((String)((ValueList)listStops__stop_lat.get(i)).getKeyName()))
	       			hot = false;
	       		else
	       			i++;
	       	}
	    	if (hot) {
       			newStops__stop_lat = new ValueList(stopPointFrom);
       			listStops__stop_lat.add(newStops__stop_lat);
       			newStops__stop_lat.addValue(niceString);
       		}
	   }
	   if (key.equals(_key_route_link_location_y[0]) && keyNested.equals(_key_route_link_location_y[1]) && stopPointFrom.length() > 0) {        
	    	i = 0;
	    	hot = true;
	    	while (hot && i < listStops__stop_lon.size()) {
	    		if (stopPointFrom.equals((String)((ValueList)listStops__stop_lon.get(i)).getKeyName()))
	    			hot = false;
	    		else
	    			i++;
	    	}
	    	if (hot) {
	    		newStops__stop_lon = new ValueList(stopPointFrom);
	    		listStops__stop_lon.add(newStops__stop_lon);
	    		newStops__stop_lon.addValue(niceString);
	    		stopPointFrom = "";
	    	}
	   }

	   // Route sections (to stop point lat and lon), based on from- and to-stop points
	   if (key.equals(_key_route_link_from[0]) && keyNested.equals(_key_route_link_from[1])&& keyNestedLocation.equals(_key_route_link_from[2])) {
	    	stopPointFrom = niceString; 
	    	keyNestedLocation = "";
	   }
	   if (key.equals(_key_route_link_to[0]) && keyNested.equals(_key_route_link_to[1])&& keyNestedLocation.equals(_key_route_link_to[2])) {
	      	stopPointTo = niceString; 
	    	keyNestedLocation = "";
	    	keyNested = "";
	    }
	    if (key.equals(_key_route_link_location_x[0]) && keyNested.equals(_key_route_link_location_x[1])) {  
	    	stopPointToLat = niceString;
	    }
	    if (key.equals(_key_route_link_location_y[0]) && keyNested.equals(_key_route_link_location_y[1])) {  
	    	stopPointToLon = niceString;
	    }
	}        
        
   	public void clearKeys (String qName) {
    	if (inRouteSection) {
    		if (keyNested.equals(_key_route_link_location_x[1]))
    			keyNestedLocation = "";
    		if (keyNestedLocation.equals(_key_route_link_from[2]))
    			keyNestedLocation = "";
    		if (keyNested.equals(_key_route_link_from[1]))
    			keyNested = "";
    		if (qName.equals(_key_route_link_location_y[1]))
    			keyNested = "";
    	}
    	if (qName.equals(_key_route_section[0])) {
    		if (inRouteSection) {
    			if (stopPointToLat.length() > 0) {
    				newStops__stop_lat = new ValueList(stopPointTo); // last stop in route section
    				listStops__stop_lat.add(newStops__stop_lat);
    				newStops__stop_lat.addValue(stopPointToLat);
    			}
    			if (stopPointToLon.length() > 0) {
    				newStops__stop_lon = new ValueList(stopPointTo); // last stop in route section
    				listStops__stop_lon.add(newStops__stop_lon);
    				newStops__stop_lon.addValue(stopPointToLon);
    			}
    		}
    		inRouteSection = !inRouteSection;
    		key = "";
    	}
    	if (key.equals(key_stops__stop_id[0]))
    		keyNested = "";
    	if (qName.equals(key_stops__stop_id[0]))
    		key = "";
    	if (key.equals(key_stops__stop_id[0]))
    		keyNested = "";
    	if (key.equals(key_stops__stop_lat[0]))
    		keyNested = "";
    	if (key.equals(key_stops__stop_lon[0]))
    		keyNested = "";
 	}
	
	public void endDocument() {
	    int i, j;
	    ValueList iterator, jterator;
	    String stopId;
	    boolean hot;
	    String indicator, locality;

	    // Backfill missing stop coordinates with default lat/lon
	    for (i = 0; i < listStops__stop_id.size(); i++) {
	    	iterator = (ValueList)listStops__stop_id.get(i);
	    	stopId = (String)iterator.getValue(0);
	    	j = 0;
	    	hot = true;
	    	while (hot && j < listStops__stop_lat.size()) {
	    		jterator = (ValueList)listStops__stop_lat.get(j);
	    		if (jterator.getKeyName().equals(stopId))
	    			hot = false;
	    		else
	    			j++;
	    	}
	    	if (hot) {
	       		newStops__stop_lat = new ValueList(stopId);
				listStops__stop_lat.add(i, newStops__stop_lat);
				newStops__stop_lat.addValue(getLat(stopId));
	       		newStops__stop_lon = new ValueList(stopId);
				listStops__stop_lon.add(i, newStops__stop_lon);
				newStops__stop_lon.addValue(getLon(stopId));
	    	} 
	    }
	    
	    // Roll stop locality and indicator into stopname
	    for (i = 0; i < listStops__stop_name.size(); i++) {
	    	indicator = "";
	    	locality = "";
	    	iterator = (ValueList)listStops__stop_name.get(i);
	    	stopId = (String)iterator.getKeyName();
	    	j = 0; // Find locality
	    	hot = true;
	    	jterator = null;
	    	while (hot && j < _listStops__stop_locality.size()) {
	    		jterator = (ValueList)_listStops__stop_locality.get(j);
	    		if (jterator.getKeyName().equals(stopId))
	    			hot = false;
	    		else
	    			j++;
	    	}
	    	if (!hot)
	    		locality = (String)jterator.getValue(0);
	    	j = 0; // Find indicator
	    	hot = true;
	    	jterator = null;
	    	while (hot && j < _listStops__stop_indicator.size()) {
	    		jterator = (ValueList)_listStops__stop_indicator.get(j);
	    		if (jterator.getKeyName().equals(stopId))
	    			hot = false;
	    		else
	    			j++;
	    	}
	    	if (!hot)
	    		indicator = (String)jterator.getValue(0);
	    	
	    	if (locality.length() > 0 && iterator != null) // Prefix locality
	    		iterator.setValue(0, locality + ", " + (String)iterator.getValue(0));
	    	if (indicator.length() > 0 && iterator != null) // Posfix indicator
	        	iterator.setValue(0, (String)iterator.getValue(0) + ", "+ indicator);
	    }
	}
	
	public void completeData() {
  	    // Add quotes if needed
  	    csvProofList(listStops__stop_id);
  	    csvProofList(listStops__stop_name);
  	    csvProofList(listStops__stop_desc);
  	    csvProofList(listStops__stop_lat);
  	    csvProofList(listStops__stop_lon);
  	    csvProofList(listStops__stop_street);
  	    csvProofList(listStops__stop_city);
  	    csvProofList(listStops__stop_postcode);
  	    csvProofList(listStops__stop_region);
 	    csvProofList(listStops__stop_country);
	}

	public void dumpValues() {
		int i;
		ValueList iterator;
		
	    System.out.println("*** Stops");
	    for (i = 0; i < listStops__stop_id.size(); i++) {
	    	iterator = (ValueList)listStops__stop_id.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listStops__stop_name.size(); i++) {
	    	iterator = (ValueList)listStops__stop_name.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listStops__stop_lat.size(); i++) {
	    	iterator = (ValueList)listStops__stop_lat.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listStops__stop_lon.size(); i++) {
	    	iterator = (ValueList)listStops__stop_lon.get(i);
	    	iterator.dumpValues();
	    }
	    
	}

	public String getLat(String stop) { // v1.5: Return lat
		if (lat.containsKey(stop))
			return (String)lat.get(stop);
		else
			return key_stops__stop_lat[2];
	}
	public String getLon(String stop) { // v1.5: Return lon
		if (lon.containsKey(stop))
			return (String)lon.get(stop);
		else
			return key_stops__stop_lon[2];
	}

	public TransxchangeStops(TransxchangeHandlerEngine owner) 
		throws UnsupportedEncodingException, IOException {
		super(owner);
		listStops__stop_id = new ArrayList();
		listStops__stop_name = new ArrayList();
		listStops__stop_desc = new ArrayList();
		listStops__stop_lat = new ArrayList();
		listStops__stop_lon = new ArrayList();
		listStops__stop_street = new ArrayList();
		listStops__stop_city = new ArrayList();
		listStops__stop_postcode = new ArrayList();
		listStops__stop_region = new ArrayList();
		listStops__stop_country = new ArrayList();
		
		_listStops__stop_locality = new ArrayList();
		_listStops__stop_indicator = new ArrayList();
		
		/*
		 * v1.5: Read stop coordinates from uk stop file
		 */
		String ukStopsFileName = "/data/ukstops.csv";
		URL urlFileName = getClass().getResource(ukStopsFileName);
		URLDecoder.decode(urlFileName.getFile(), "UTF-8");
		InputStream inStream = urlFileName.openStream();
		InputStreamReader inStreamReader = new InputStreamReader(inStream);
		BufferedReader bufFileIn = new BufferedReader(inStreamReader);

		lat = new HashMap();		
		lon = new HashMap();		
		String line;
		String tokens[] = {"", "", ""};
		while((line = bufFileIn.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			int i = 0;
			while (st.hasMoreTokens() && i < 3) {
				tokens[i] = st.nextToken();
				i++;
			}
			lat.put(tokens[0], tokens[1]);
			lon.put(tokens[0], tokens[2]);
			tokens[0] = "";
			tokens[1] = "";
			tokens[2] = "";
		}
		inStream.close();
	}
}
