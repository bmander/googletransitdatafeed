/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * Version:	1.3
 * Date: 	11-Mar-2007
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



public class TransxchangeAgency extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_agency__agency_name = new String[] {"OperatorShortName", "", "OpenRequired"}; // Google Transit required
	static final String[] key_agency__agency_url = new String[] {"EmailAddress", "", "OpenRequired"}; // Google Transit required
	static final String[] key_agency__agency_timezone = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""}; // Google Transit required

	// Parsed data
	List listAgency__agency_name;
	ValueList newAgency__agency_name;
	List listAgency__agency_url;
	ValueList newAgency__agency_url;
	List listAgency__agency_timezone;
	ValueList newAgency__agency_timezone;
	
	public List getListAgency__agency_name() {
		return listAgency__agency_name;
	}
	public List getListAgency__agency_url() {
		return listAgency__agency_url;
	}
	public List getListAgency__agency_timezone() {
		return listAgency__agency_timezone;
	}

	TransxchangeHandler handler;
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		super.startElement(uri, name, qName, atts);
		if (qName.equals(key_agency__agency_name[0])) 
			key = key_agency__agency_name[0];
		if (qName.equals(key_agency__agency_url[0]))
			key = key_agency__agency_url[0];
	}

	public void endElement (String uri, String name, String qName) {
		if (niceString.length() == 0) 
			return;
	    if (key.equals(key_agency__agency_name[0])) {
	   		newAgency__agency_name = new ValueList(key_agency__agency_name[0]);
	   		listAgency__agency_name.add(newAgency__agency_name);
	   		newAgency__agency_name.addValue(niceString);
	    }
	}

	public void clearKeys (String qName) {
		if (qName.equals(key_agency__agency_name[0])) 
			key = "";
		if (qName.equals(key_agency__agency_url[0])) 
			key = "";		 
	}

	public void completeData() {
		int i, j;
	    boolean hot;
		
   		newAgency__agency_url = new ValueList(handler.getUrl());
   		listAgency__agency_url.add(newAgency__agency_url);
   		newAgency__agency_url.addValue(handler.getUrl());

   		for (i = 0; i < listAgency__agency_name.size(); i++) {
  	    	newAgency__agency_timezone = new ValueList(handler.getTimezone());
	    	listAgency__agency_timezone.add(newAgency__agency_timezone);
	    	newAgency__agency_timezone.addValue(handler.getTimezone());
	    	j = 0;
	    	hot = true;
	    	while (hot && j < listAgency__agency_url.size()) {
	    		if (((String)((ValueList)listAgency__agency_name.get(i)).getValue(0)).equals(((String)((ValueList)listAgency__agency_url.get(j)).getKeyName()))) 
	    			hot = false;
	    		else
	    			j++;    	 
	    	}
	    	if (!hot || listAgency__agency_url.size() == 0) {
	        	newAgency__agency_url = new ValueList(key_agency__agency_url[0]);
	        	listAgency__agency_url.add(j, newAgency__agency_url);
	        	newAgency__agency_url.addValue(key_agency__agency_url[2]);    		
	    	}
  	    }
  	    
  	    // Add quotes if needed
  	    csvProofList(listAgency__agency_name);
  	    csvProofList(listAgency__agency_url);
  	    csvProofList(listAgency__agency_timezone);
	}

	public void dumpValues() {
		int i;
		ValueList iterator = null;
		 
		System.out.println("*** Agency");
		for (i = 0; i < listAgency__agency_name.size(); i++) {
		    iterator = (ValueList)listAgency__agency_name.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listAgency__agency_url.size(); i++) {
		    iterator = (ValueList)listAgency__agency_url.get(i);
		    iterator.dumpValues();
		}
		for (i = 0; i < listAgency__agency_timezone.size(); i++) {
		    iterator = (ValueList)listAgency__agency_timezone.get(i);
		    iterator.dumpValues();
		}
	}
	 
	public TransxchangeAgency(TransxchangeHandler owner) {
		listAgency__agency_name = new ArrayList();
		listAgency__agency_url = new ArrayList();
		listAgency__agency_timezone = new ArrayList();
		
		handler = owner;
	}
}
