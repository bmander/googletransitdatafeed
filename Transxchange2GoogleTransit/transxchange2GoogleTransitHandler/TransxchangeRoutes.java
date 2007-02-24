/*
 * The Google Transit Data Feed project
 * TransXChange2GoogleTransit
 *
 * File:    TransxchangeRoutes.java
 * Version:	1.2
 * Date: 	23-Feb-2007
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



public class TransxchangeRoutes extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_routes__route_id = new String[] {"Service", "Line", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__route_short_name = new String[] {"Service", "LineName", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__route_long_name = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "OpenRequired"}; // Google Transit required
	static final String[] key_routes__route_desc = new String[] {"Service", "Description", "n/a"};
	static final String[] key_routes__route_type = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "3"}; // Google Transit required

	// Parsed data 
	List listRoutes__route_id;      
	ValueList newRoutes__route_id;
	List listRoutes__route_short_name;      
	ValueList newRoutes__route_short_name;
	List listRoutes__route_long_name;      
	ValueList newRoutes__route_long_name;
	List listRoutes__route_desc;      
	ValueList newRoutes__route_desc;
	List listRoutes__route_type;      
	ValueList newRoutes__route_type;

	TransxchangeHandler handler;
	String service = "";
	List _listRouteDesc;
	ValueList _newRouteDesc;

	public List getListRoutes__route_id() {
		return listRoutes__route_id;
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

	public void startElement(String uri, String name, String qName, Attributes atts) {
	    int qualifierIx;
	    String qualifierString;

	    super.startElement(uri, name, qName, atts);
		if (key.equals(key_routes__route_id[0]) && qName.equals(key_routes__route_id[1])) {
	        qualifierIx = atts.getIndex("id");
	        qualifierString = atts.getValue(qualifierIx);
	        newRoutes__route_id = new ValueList(service);

	        listRoutes__route_id.add(newRoutes__route_id);
	    	newRoutes__route_id.addValue(qualifierString);
	    }
		if (qName.equals(key_routes__route_short_name[0])) 
			key = key_routes__route_short_name[0];
		if (key.equals(key_routes__route_short_name[0]) && qName.equals(key_routes__route_short_name[1])) {
			keyNested = key_routes__route_short_name[1];
		}
		if (qName.equals(key_routes__route_desc[0])) 
			key = key_routes__route_desc[0];
		if (key.equals(key_routes__route_desc[0]) && qName.equals(key_routes__route_desc[1])) {
			keyNested = key_routes__route_desc[1];
		}
	}
	
	public void endElement (String uri, String name, String qName) {
		if (niceString.length() == 0)
			return;
		
		if (key.equals(key_routes__route_short_name[0]) && keyNested.equals(key_routes__route_short_name[1])) {
			newRoutes__route_short_name = new ValueList(key_routes__route_short_name[1]);
			listRoutes__route_short_name.add(newRoutes__route_short_name);
        	newRoutes__route_short_name.addValue(niceString);
			newRoutes__route_long_name = new ValueList(key_routes__route_long_name[1]);
			listRoutes__route_long_name.add(newRoutes__route_short_name);
        	newRoutes__route_long_name.addValue(niceString);
        	keyNested = "";
			newRoutes__route_type = new ValueList(key_routes__route_type[0]); // Default for _type
			listRoutes__route_type.add(newRoutes__route_type);
//			newRoutes__route_type.addValue(key_routes__route_type[2]);
			newRoutes__route_type.addValue(handler.getDefaultRouteType());
		}
        if (key.equals(key_routes__route_desc[0]) && keyNested.equals(key_routes__route_desc[1])) {
			_newRouteDesc = new ValueList(service);
			_listRouteDesc.add(_newRouteDesc);
			_newRouteDesc.addValue(niceString);
        	keyNested = "";
		}		
	}
	
	public void clearKeys (String qName) {
		if (key.equals(key_routes__route_short_name[0]))
			keyNested = "";
		if (qName.equals(key_routes__route_short_name[0])) 
			key = "";
		if (key.equals(key_routes__route_desc[0]))
			keyNested = "";
		if (qName.equals(key_routes__route_desc[0])) 
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

	public TransxchangeRoutes(TransxchangeHandler owner) {
		listRoutes__route_id = new ArrayList();
		listRoutes__route_short_name = new ArrayList();
		listRoutes__route_long_name = new ArrayList();
		listRoutes__route_desc = new ArrayList();
		listRoutes__route_type = new ArrayList();
		
		_listRouteDesc = new ArrayList();
		
		handler = owner;
	}
}



