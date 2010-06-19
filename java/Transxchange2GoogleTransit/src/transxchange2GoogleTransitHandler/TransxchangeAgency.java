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
 * 	agencies
 */
public class TransxchangeAgency extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_agency__agency_id = new String[] {"Operator", "", "OpenRequired"}; // v1.5: Read Operator id Google Transit required
	static final String[] key_agency__agency_lid = new String[] {"LicensedOperator", "", "OpenRequired"}; // v1.5: Read Operator id Google Transit required
	static String[] key_agency__agency_name = new String[] {"OperatorNameOnLicence", "", "OpenRequired"}; // Google Transit required
//	static String[] key_agency__agency_name = new String[] {"OperatorShortName", "", "OpenRequired"}; // v1.5: Read OperatorNameOnLicense in place of OperatorShortName
	static final String[] key_agency__agency_url = new String[] {"EmailAddress", "", "OpenRequired"}; // Google Transit required
	static final String[] key_agency__agency_timezone = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""}; // Google Transit required

	// Parsed data
	List listAgency__agency_id; // v1.5: Read Operator ID
	ValueList newAgency__agency_id;
	List listAgency__agency_name;
	ValueList newAgency__agency_name;
	List listAgency__agency_url;
	ValueList newAgency__agency_url;
	List listAgency__agency_timezone;
	ValueList newAgency__agency_timezone;
	
	public List getListAgency__agency_id() { // v1.5: Read Operator ID
		return listAgency__agency_id;
	}
	public List getListAgency__agency_name() {
		return listAgency__agency_name;
	}
	public List getListAgency__agency_url() {
		return listAgency__agency_url;
	}
	public List getListAgency__agency_timezone() {
		return listAgency__agency_timezone;
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {

		int qualifierIx;
	    String qualifierString;
	
		super.startElement(uri, name, qName, atts);
		
		// v1.6.4: Switched to use short name in place of name on license
		if (handler.isAgencyShortName()) {
			key_agency__agency_name = new String[] {"OperatorShortName", "", "OpenRequired"};
		}

		if (qName.equals(key_agency__agency_id[0]) || qName.equals(key_agency__agency_lid[0])) { // v1.5: new: agency id
        	qualifierIx = atts.getIndex("id");
        	qualifierString = atts.getValue(qualifierIx);
        	newAgency__agency_id = new ValueList(key_agency__agency_id[0]);
        	listAgency__agency_id.add(newAgency__agency_id);
        	newAgency__agency_id.addValue(qualifierString);
		}
		if (qName.equals(key_agency__agency_name[0])) 
			key = key_agency__agency_name[0];
		if (qName.equals(key_agency__agency_url[0]))
			key = key_agency__agency_url[0];
	}

	public void endElement (String uri, String name, String qName) {
		if (niceString == null || niceString.length() == 0) 
			return;
	    if (key.equals(key_agency__agency_name[0])) {
	   		newAgency__agency_name = new ValueList(key_agency__agency_name[0]);
	   		listAgency__agency_name.add(newAgency__agency_name);
	   		newAgency__agency_name.addValue(niceString);
	    }
	}

	public void clearKeys (String qName) {
		if (qName.equals(key_agency__agency_id[0])) // v1.5: new: agency ID
			key = "";
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
  	    csvProofList(listAgency__agency_id); // v1.5: new: agency id
  	    csvProofList(listAgency__agency_name);
  	    csvProofList(listAgency__agency_url);
  	    csvProofList(listAgency__agency_timezone);
	}

	public void dumpValues() {
		int i;
		ValueList iterator = null;
		 
		System.out.println("*** Agency");
		for (i = 0; i < listAgency__agency_id.size(); i++) {
		    iterator = (ValueList)listAgency__agency_id.get(i);
		    iterator.dumpValues();
		}
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
	 
	public TransxchangeAgency(TransxchangeHandlerEngine owner) {
		super(owner);
		listAgency__agency_id = new ArrayList(); // v1.5: new: agency ID
		listAgency__agency_name = new ArrayList();
		listAgency__agency_url = new ArrayList();
		listAgency__agency_timezone = new ArrayList();
	}
}
