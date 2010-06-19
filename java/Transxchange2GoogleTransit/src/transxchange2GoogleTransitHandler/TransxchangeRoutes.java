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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

/* 
 * This class handles the TransXChange xml input file under the aspect of 
 * 	routes
 */
public class TransxchangeRoutes extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_routes__route_id = new String[] {"Service", "Line", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__agency_id = new String[] {"Service", "RegisteredOperatorRef", ""}; // v1.5: Agency from RegisteredOperatorRef
	static final String[] key_routes__route_short_name = new String[] {"Service", "LineName", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__route_long_name = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__route_desc = new String[] {"Service", "Description", ""};
	static final String[] key_routes__route_origin = new String[] {"Service", "Origin", ""}; // v1.5: user Origin - Destination as route description if there is no service description in the TransXChange file
	static final String[] key_routes__route_destination = new String[] {"Service", "Destination", ""};
	static final String[] key_routes__route_type = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "3"}; // Google Transit required

	// Parsed data 
	List listRoutes__route_id;      
	ValueList newRoutes__route_id;
	List listRoutes__agency_id; // v1.5: Agency ID      
	ValueList newRoutes__agency_id; // v1.5: Agency ID
	List listRoutes__route_short_name;      
	ValueList newRoutes__route_short_name;
	List listRoutes__route_long_name;      
	ValueList newRoutes__route_long_name;
	List listRoutes__route_desc;      
	ValueList newRoutes__route_desc;
	List listRoutes__route_type;      
	ValueList newRoutes__route_type;
	List listRoutes__service_id;      
	ValueList newRoutes__service_id;

	String _origin = "";
	String _agencyId = ""; // v1.5: agency ID
	List _listRouteDesc;
	ValueList _newRouteDesc;

	public List getListRoutes__route_id() {
		return listRoutes__route_id;
	}
	public List getListRoutes__agency_id() {
		return listRoutes__agency_id;
	}
	public List getListRoutes__route_short_name() {
		return listRoutes__route_short_name;
	}
	public List getListRoutes__route_long_name() {
		return listRoutes__route_long_name;
	}
	public List getListRoutes__route_desc() {
		return listRoutes__route_desc;
	}
	public List getListRoutes__route_type() {
		return listRoutes__route_type;
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {
	
	    int qualifierIx;
	    String qualifierString;

	    super.startElement(uri, name, qName, atts);
		if (key.equals(key_routes__route_id[0]) && qName.equals(key_routes__route_id[1])) {
	        qualifierIx = atts.getIndex("id");
	        qualifierString = atts.getValue(qualifierIx);
	        newRoutes__route_id = new ValueList("");
	        listRoutes__route_id.add(newRoutes__route_id);
	    	newRoutes__route_id.addValue(qualifierString);
	    }
		if (qName.equals(key_routes__agency_id[0])) 
			key = key_routes__agency_id[0]; // v1.5 agency ID
		if (key.equals(key_routes__agency_id[0]) && qName.equals(key_routes__agency_id[1]))
			keyNested = key_routes__agency_id[1]; // v1.5 agency ID
		if (qName.equals(key_routes__route_short_name[0])) 
			key = key_routes__route_short_name[0];
		if (key.equals(key_routes__route_short_name[0]) && qName.equals(key_routes__route_short_name[1]))
			keyNested = key_routes__route_short_name[1];
		if (qName.equals(key_routes__route_desc[0])) 
			key = key_routes__route_desc[0];
		if (key.equals(key_routes__route_desc[0]) && qName.equals(key_routes__route_desc[1]))
			keyNested = key_routes__route_desc[1];
		if (qName.equals(key_routes__route_origin[0])) // v1.5: origin
			key = key_routes__route_origin[0];
		if (key.equals(key_routes__route_origin[0]) && qName.equals(key_routes__route_origin[1]))
			keyNested = key_routes__route_origin[1];
		if (qName.equals(key_routes__route_destination[0])) // v1.5: destination
			key = key_routes__route_destination[0];
		if (key.equals(key_routes__route_destination[0]) && qName.equals(key_routes__route_destination[1]))
			keyNested = key_routes__route_destination[1];	
	}
	
	public void endElement (String uri, String name, String qName) {
		if (niceString == null || niceString.length() == 0)
			return;
		
		if (key.equals(key_routes__route_short_name[0]) && keyNested.equals(key_routes__route_short_name[1])) {
			newRoutes__route_short_name = new ValueList(key_routes__route_short_name[1]);
			listRoutes__route_short_name.add(newRoutes__route_short_name);
        	newRoutes__route_short_name.addValue(niceString);
			newRoutes__route_long_name = new ValueList(key_routes__route_long_name[1]);
			listRoutes__route_long_name.add(newRoutes__route_long_name);
//        	newRoutes__route_long_name.addValue(niceString);
        	newRoutes__route_long_name.addValue(""); // no long name if not different from short name
        	keyNested = "";
			newRoutes__route_type = new ValueList(key_routes__route_type[0]); // Default for _type
			listRoutes__route_type.add(newRoutes__route_type);
//			newRoutes__route_type.addValue(key_routes__route_type[2]);
			newRoutes__route_type.addValue(handler.getDefaultRouteType());
			newRoutes__service_id = new ValueList("");
			listRoutes__service_id.add(newRoutes__service_id);
        	newRoutes__service_id.addValue(((TransxchangeCalendar)handler.getCalendar()).getService());
		}
		if (key.equals(key_routes__agency_id[0]) && keyNested.equals(key_routes__agency_id[1])) {
			_agencyId = niceString;
		}
		if (key.equals(key_routes__route_origin[0]) && keyNested.equals(key_routes__route_origin[1])) { // v1.5: Origin/Destination
			_origin = niceString;
		}		
		if (key.equals(key_routes__route_destination[0]) && keyNested.equals(key_routes__route_destination[1])) { // v1.5: Origin/Destination
			_newRouteDesc = new ValueList("");
			_listRouteDesc.add(_newRouteDesc);
			_newRouteDesc.addValue(_origin + " - " + niceString);
        	keyNested = "";
		}		
		if (key.equals(key_routes__route_desc[0]) && keyNested.equals(key_routes__route_desc[1])) {
			_newRouteDesc = new ValueList("");
			_listRouteDesc.add(_newRouteDesc);
			_newRouteDesc.addValue(niceString);
        	keyNested = "";
		}		
	}
	
	public void clearKeys (String qName) {
		if (key.equals(key_routes__agency_id[0]) && keyNested.equals(key_routes__agency_id[1])) { // v1.5: Agency ID
			ValueList iterator;

			// Backfill agency id to route short names
			for (int i = 0; i < listRoutes__service_id.size(); i++) {
			    iterator = (ValueList)listRoutes__service_id.get(i);
			    if (iterator.getValue(0).equals(((TransxchangeCalendar)handler.getCalendar()).getService())) {
			    	newRoutes__agency_id = new ValueList(key_routes__agency_id[1]); // v1.5: agency ID
			    	listRoutes__agency_id.add(newRoutes__agency_id);
			    	newRoutes__agency_id.addValue(_agencyId);
			    }
			}
			keyNested = "";
		}
		if (key.equals(key_routes__route_short_name[0]))
			keyNested = "";
		if (qName.equals(key_routes__route_short_name[0])) 
			key = "";
		if (key.equals(key_routes__route_desc[0]))
			keyNested = "";
		if (qName.equals(key_routes__route_desc[0])) 
			key = "";
		if (key.equals(key_routes__route_origin[1])) // v1.5: Origin
			keyNested = "";
		if (qName.equals(key_routes__route_origin[0])) 
			key = "";
		if (key.equals(key_routes__route_destination[1])) { // v1.5: Destination
			keyNested = "";
			_origin = "";
		}
		if (qName.equals(key_routes__route_destination[0])) 
			key = "";
	}

	public void endDocument() {
		int i, j;
		boolean hot;
		
		// Route descriptions
		for (i = 0; i < listRoutes__route_id.size(); i++) {
			j = 0;
			hot = true;
			while (hot && j < _listRouteDesc.size()) {
				if (((String)((ValueList)listRoutes__route_id.get(i)).getKeyName()).equals(((String)((ValueList)_listRouteDesc.get(j)).getKeyName()))) 
					hot = false;
				else
					j++; 
			}
			if (!hot) {
				newRoutes__route_desc = new ValueList((String)((ValueList)_listRouteDesc.get(j)).getKeyName());
				listRoutes__route_desc.add(newRoutes__route_desc);
				newRoutes__route_desc.addValue(((String)((ValueList)_listRouteDesc.get(j)).getValue(0)));
			} else {
				newRoutes__route_desc = new ValueList(key_routes__route_desc[2]);
				listRoutes__route_desc.add(newRoutes__route_desc);
				newRoutes__route_desc.addValue(key_routes__route_desc[2]);
	    	}
    	}
    }
	
	public void completeData() {
  	    // Add quotes if needed
  	    csvProofList(listRoutes__route_id);
  	    csvProofList(listRoutes__route_short_name);
  	    csvProofList(listRoutes__route_long_name);
  	    csvProofList(listRoutes__route_desc);
  	    csvProofList(listRoutes__route_type);
	}
	
	public void dumpValues() {
		int i;
		ValueList iterator;

		System.out.println("*** Routes");
		for (i = 0; i < listRoutes__route_id.size(); i++) {
		    iterator = (ValueList)listRoutes__route_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listRoutes__agency_id.size(); i++) {
		    iterator = (ValueList)listRoutes__agency_id.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listRoutes__route_short_name.size(); i++) {
		    iterator = (ValueList)listRoutes__route_short_name.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listRoutes__route_long_name.size(); i++) {
		    iterator = (ValueList)listRoutes__route_long_name.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listRoutes__route_desc.size(); i++) {
		    iterator = (ValueList)listRoutes__route_desc.get(i);
		    iterator.dumpValues();
		}
	}

	public TransxchangeRoutes(TransxchangeHandlerEngine owner) {
		super(owner);
		listRoutes__route_id = new ArrayList();
		listRoutes__agency_id = new ArrayList(); // v1.5 Agency ID
		listRoutes__route_short_name = new ArrayList();
		listRoutes__route_long_name = new ArrayList();
		listRoutes__route_desc = new ArrayList();
		listRoutes__route_type = new ArrayList();
		listRoutes__service_id = new ArrayList();
		
		_listRouteDesc = new ArrayList();
	}
}



