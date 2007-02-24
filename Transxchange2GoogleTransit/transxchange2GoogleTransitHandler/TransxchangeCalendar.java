/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 * 
 * File:    TransxchangeCalendar.java
 * Version:	1.3
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



public class TransxchangeCalendar extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_calendar__service_id = new String[] {"Service", "ServiceCode", "OpenRequired"}; // Google Transit required
	static final String[] key_calendar__monday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "1"}; // Google Transit required
	static final String[] key_calendar__tuesday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "1"}; // Google Transit required
	static final String[] key_calendar__wednesday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "1"}; // Google Transit required
	static final String[] key_calendar__thursday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "1"}; // Google Transit required
	static final String[] key_calendar__friday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "1"}; // Google Transit required
	static final String[] key_calendar__saturday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "0"}; // Google Transit required
	static final String[] key_calendar__sunday = new String[] {"__transxchange2GoogleTransit_drawDefault", "", "0"}; // Google Transit required
	static final String[] key_calendar__start_date = new String[] {"Service", "StartDate", "20060901"}; // Google Transit required
	static final String[] key_calendar__end_date = new String[] {"Service", "EndDate", "20081231"}; // Google Transit required

	// Parsed data
	List listCalendar__service_id;
	ValueList newCalendar__service_id;
	List listCalendar__monday;
	ValueList newCalendar__monday;
	List listCalendar__tuesday;
	ValueList newCalendar__tuesday;
	List listCalendar__wednesday;
	ValueList newCalendar__wednesday;
	List listCalendar__thursday;
	ValueList newCalendar__thursday;
	List listCalendar__friday;
	ValueList newCalendar__friday;
	List listCalendar__saturday;
	ValueList newCalendar__saturday;
	List listCalendar__sunday;
	ValueList newCalendar__sunday;
	List listCalendar__start_date;
	ValueList newCalendar__start_date;
	List listCalendar__end_date;
	ValueList newCalendar__end_date;

	String service = "";
	static final String[] _key_daytype_mofr = {"Service", "RegularDayType", "DaysOfWeek", "MondayToFriday"};
	static final String[] _key_daytype_mosa = {"Service", "RegularDayType", "DaysOfWeek", "MondayToSaturday"};
	static final String[] _key_daytype_mosu = {"Service", "RegularDayType", "DaysOfWeek", "MondayToSunday"};
	static final String[] _key_daytype_sasu = {"Service", "RegularDayType", "DaysOfWeek", "SaturdayToSunday"};
	static final String[] _key_daytype_mo = {"Service", "RegularDayType", "DaysOfWeek", "Monday"};
	static final String[] _key_daytype_tu = {"Service", "RegularDayType", "DaysOfWeek", "Tuesday"};
	static final String[] _key_daytype_we = {"Service", "RegularDayType", "DaysOfWeek", "Wednesday"};
	static final String[] _key_daytype_th = {"Service", "RegularDayType", "DaysOfWeek", "Thursday"};
	static final String[] _key_daytype_fr = {"Service", "RegularDayType", "DaysOfWeek", "Friday"};
	static final String[] _key_daytype_sa = {"Service", "RegularDayType", "DaysOfWeek", "Saturday"};
	static final String[] _key_daytype_su = {"Service", "RegularDayType", "DaysOfWeek", "Sunday"};
	static final String[] _key_daytype_not_mo = {"Service", "RegularDayType", "DaysOfWeek", "NotMonday"};
	static final String[] _key_daytype_not_tu = {"Service", "RegularDayType", "DaysOfWeek", "NotTuesday"};
	static final String[] _key_daytype_not_we = {"Service", "RegularDayType", "DaysOfWeek", "NotWednesday"};
	static final String[] _key_daytype_not_th = {"Service", "RegularDayType", "DaysOfWeek", "NotThursday"};
	static final String[] _key_daytype_not_fr = {"Service", "RegularDayType", "DaysOfWeek", "NotFriday"};
	static final String[] _key_daytype_not_sa = {"Service", "RegularDayType", "DaysOfWeek", "NotSaturday"};
	static final String[] _key_daytype_not_su = {"Service", "RegularDayType", "DaysOfWeek", "NotSunday"};
	String keyDaysOfWeek = "";
	String monday = "";
	String tuesday = "";
	String wednesday = "";
	String thursday = "";
	String friday = "";
	String saturday = "";
	String sunday = "";

	public List getListCalendar__service_id() {
		return listCalendar__service_id;
	}
	public List getListCalendar__monday() {
		return listCalendar__monday;
	}
	public List getListCalendar__tuesday() {
		return listCalendar__tuesday;
	}
	public List getListCalendar__wednesday() {
		return listCalendar__wednesday;
	}
	public List getListCalendar__thursday() {
		return listCalendar__thursday;
	}
	public List getListCalendar__friday() {
		return listCalendar__friday;
	}
	public List getListCalendar__saturday() {
		return listCalendar__saturday;
	}
	public List getListCalendar__sunday() {
		return listCalendar__sunday;
	}
	public List getListCalendar__start_date() {
		return listCalendar__start_date;
	}
	public List getListCalendar__end_date() {
		return listCalendar__end_date;
	}

	public String getService() {
		return service;
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts) {

	    super.startElement(uri, name, qName, atts);
	    if (qName.equals(key_calendar__service_id[0])) 
			key = key_calendar__service_id[0];
	    if (key.equals(key_calendar__service_id[0]) && qName.equals(key_calendar__service_id[1])) { // && keyOperationDays.length() == 0) {
	    	keyNested = key_calendar__service_id[1];
	    	niceString = "";    	
	    }
	    if (key.equals(key_calendar__start_date[0]) && qName.equals(key_calendar__start_date[1])) { // && keyOperationDays.length() == 0) {
			keyNested = key_calendar__start_date[1];
	    	niceString = "";
	    }
	    if (key.equals(key_calendar__end_date[0]) && qName.equals(key_calendar__end_date[1])) { // && keyOperationDays.length() == 0) {
			keyNested = key_calendar__end_date[1];
	    	niceString = "";
	    }
	    if (qName.equals(_key_daytype_mofr[0])) // also covers all other day types 
	    	key = _key_daytype_mofr[0];
	    if (key.equals(_key_daytype_mofr[0]) && qName.equals(_key_daytype_mofr[1])) // also covers all other day types 
	    	keyNested = _key_daytype_mofr[1];
	    if (key.equals(_key_daytype_mofr[0]) && keyNested.equals(_key_daytype_mofr[1]) && qName.equals(_key_daytype_mofr[2])) { // also covers all other day types 
	    	keyDaysOfWeek = _key_daytype_mofr[2];
	    	monday = key_calendar__monday[2];
	    	tuesday = key_calendar__tuesday[2];
	    	wednesday = key_calendar__wednesday[2];
	    	thursday = key_calendar__thursday[2];
	    	friday = key_calendar__friday[2];
	    	saturday = key_calendar__saturday[2];
	    	sunday = key_calendar__sunday[2];
	    }
	}

	public void endElement (String uri, String name, String qName) {
		if (niceString.length() > 0) {
			if (key.equals(key_calendar__service_id[0]) && keyNested.equals(key_calendar__service_id[1])) {
				service = niceString;
				newCalendar__service_id = new ValueList(key_calendar__service_id[0]);
				listCalendar__service_id.add(newCalendar__service_id);
				newCalendar__service_id.addValue(service);
			}
			if (key.equals(key_calendar__start_date[0]) && keyNested.equals(key_calendar__start_date[1])) {
				newCalendar__start_date = new ValueList(key_calendar__start_date[1]);
				listCalendar__start_date.add(newCalendar__start_date);
				newCalendar__start_date.addValue(readTransxchangeDate(niceString));
			}
			if (key.equals(key_calendar__end_date[0]) && keyNested.equals(key_calendar__end_date[1])) {
				newCalendar__end_date = new ValueList(key_calendar__end_date[1]);
				listCalendar__end_date.add(newCalendar__end_date);
				newCalendar__end_date.addValue(readTransxchangeDate(niceString));
			}
		}
		
		if (keyDaysOfWeek.equals(_key_daytype_mofr[2]) && qName.equals(_key_daytype_mofr[3])) {
			monday = "1";
			tuesday = "1";
			wednesday = "1";
			thursday = "1";
			friday = "1";
			saturday = "0";
			sunday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_mosa[2]) && qName.equals(_key_daytype_mosa[3])) {
			monday = "1";
			tuesday = "1";
			wednesday = "1";
			thursday = "1";
			friday = "1";
			saturday = "1";
			sunday = "0";
	    }
		if (keyDaysOfWeek.equals(_key_daytype_mosu[2]) && qName.equals(_key_daytype_mosu[3])) {
			monday = "1";
			tuesday = "1";
			wednesday = "1";
			thursday = "1";
			friday = "1";
			saturday = "1";
			sunday = "1";
	    }
		if (keyDaysOfWeek.equals(_key_daytype_mo[2]) && qName.equals(_key_daytype_mo[3])) {
			monday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_tu[2]) && qName.equals(_key_daytype_tu[3])) {
			tuesday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_we[2]) && qName.equals(_key_daytype_we[3])) {
			wednesday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_th[2]) && qName.equals(_key_daytype_th[3])) {
			thursday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_fr[2]) && qName.equals(_key_daytype_fr[3])) {
			friday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_sa[2]) && qName.equals(_key_daytype_sa[3])) {
			saturday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_su[2]) && qName.equals(_key_daytype_su[3])) {
			sunday = "1";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_mo[2]) && qName.equals(_key_daytype_not_mo[3])) {
			monday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_tu[2]) && qName.equals(_key_daytype_not_tu[3])) {
			tuesday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_we[2]) && qName.equals(_key_daytype_not_we[3])) {
			wednesday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_th[2]) && qName.equals(_key_daytype_not_th[3])) {
			thursday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_fr[2]) && qName.equals(_key_daytype_not_fr[3])) {
			friday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_sa[2]) && qName.equals(_key_daytype_not_sa[3])) {
			saturday = "0";
		}
		if (keyDaysOfWeek.equals(_key_daytype_not_su[2]) && qName.equals(_key_daytype_not_su[3])) {
			sunday = "0";
		}
		if (qName.equals(keyDaysOfWeek)) {
	   		newCalendar__monday = new ValueList(key_calendar__monday[0]);
	   		listCalendar__monday.add(newCalendar__monday);
	   		newCalendar__monday.addValue(monday);
	   		newCalendar__tuesday = new ValueList(key_calendar__tuesday[0]);
	   		listCalendar__tuesday.add(newCalendar__tuesday);
	   		newCalendar__tuesday.addValue(tuesday);
	   		newCalendar__wednesday = new ValueList(key_calendar__wednesday[0]);
	   		listCalendar__wednesday.add(newCalendar__wednesday);
	   		newCalendar__wednesday.addValue(wednesday);
	   		newCalendar__thursday = new ValueList(key_calendar__thursday[0]);
	   		listCalendar__thursday.add(newCalendar__thursday);
	   		newCalendar__thursday.addValue(thursday);
	   		newCalendar__friday = new ValueList(key_calendar__friday[0]);
	   		listCalendar__friday.add(newCalendar__friday);
	   		newCalendar__friday.addValue(friday);
	   		newCalendar__saturday = new ValueList(key_calendar__saturday[0]);
	   		listCalendar__saturday.add(newCalendar__saturday);
	   		newCalendar__saturday.addValue(saturday);
	   		newCalendar__sunday = new ValueList(key_calendar__sunday[0]);
	   		listCalendar__sunday.add(newCalendar__sunday);
	   		newCalendar__sunday.addValue(sunday);
		}
	}

	public void clearKeys (String qName) {
		if (qName.equals(key_calendar__service_id[1])) 
			keyNested = "";
		if (qName.equals(key_calendar__start_date[1]))
			keyNested = "";
		if (qName.equals(key_calendar__end_date[1]))
			keyNested = "";
		if (qName.equals(key_calendar__service_id[0])) {
			key = "";
			service = "";
		}
		if (qName.equals(keyDaysOfWeek))
			keyDaysOfWeek = "";
	}

	public void completeData() {
		int i;
		
		for (i = 0; i < listCalendar__service_id.size(); i++) {
			if (listCalendar__end_date.size() == 0 || listCalendar__end_date.get(i) == null) { // caution: or else
				newCalendar__end_date = new ValueList(key_calendar__end_date[1]); 
				listCalendar__end_date.add(newCalendar__end_date);
				newCalendar__end_date.addValue(key_calendar__end_date[2]);
			}
			if (listCalendar__monday.size() == 0 || listCalendar__monday.get(i) == null) { // caution: or else
				newCalendar__monday = new ValueList(key_calendar__monday[1]); 
				listCalendar__monday.add(newCalendar__monday);
				newCalendar__monday.addValue(key_calendar__monday[2]);
			}
			if (listCalendar__tuesday.size() == 0 || listCalendar__tuesday.get(i) == null) { // caution: or else
				newCalendar__tuesday = new ValueList(key_calendar__tuesday[1]); 
				listCalendar__tuesday.add(newCalendar__tuesday);
				newCalendar__tuesday.addValue(key_calendar__tuesday[2]);
			}
			if (listCalendar__wednesday.size() == 0 || listCalendar__wednesday.get(i) == null) { // caution: or else
				newCalendar__wednesday = new ValueList(key_calendar__wednesday[1]); 
				listCalendar__wednesday.add(newCalendar__wednesday);
				newCalendar__wednesday.addValue(key_calendar__wednesday[2]);
			}
			if (listCalendar__thursday.size() == 0 || listCalendar__thursday.get(i) == null) { // caution: or else
				newCalendar__thursday = new ValueList(key_calendar__thursday[1]); 
				listCalendar__thursday.add(newCalendar__thursday);
				newCalendar__thursday.addValue(key_calendar__thursday[2]);
			}
			if (listCalendar__friday.size() == 0 || listCalendar__friday.get(i) == null) { // caution: or else
				newCalendar__friday = new ValueList(key_calendar__friday[1]); 
				listCalendar__friday.add(newCalendar__friday);
				newCalendar__friday.addValue(key_calendar__friday[2]);
			}
			if (listCalendar__saturday.size() == 0 || listCalendar__saturday.get(i) == null) { // caution: or else
				newCalendar__saturday = new ValueList(key_calendar__saturday[1]); 
				listCalendar__saturday.add(newCalendar__saturday);
				newCalendar__saturday.addValue(key_calendar__saturday[2]);
			}
			if (listCalendar__sunday.size() == 0 || listCalendar__sunday.get(i) == null) { // caution: or else
				newCalendar__sunday = new ValueList(key_calendar__sunday[1]); 
				listCalendar__sunday.add(newCalendar__sunday);
				newCalendar__sunday.addValue(key_calendar__sunday[2]);
			}
		}

		// Add quotes as needed
		csvProofList(listCalendar__service_id);
 	    csvProofList(listCalendar__monday);
 	    csvProofList(listCalendar__tuesday);
 	    csvProofList(listCalendar__wednesday);
 	    csvProofList(listCalendar__thursday);
 	    csvProofList(listCalendar__friday);
 	    csvProofList(listCalendar__saturday);
 	    csvProofList(listCalendar__sunday);
 	    csvProofList(listCalendar__start_date);
 	    csvProofList(listCalendar__end_date);
	}

	public void dumpValues() {
		int i;
		ValueList iterator;
		
	    System.out.println("*** Calendar");
	    for (i = 0; i < listCalendar__service_id.size(); i++) {
	    	iterator = (ValueList)listCalendar__service_id.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__start_date.size(); i++) {
	    	iterator = (ValueList)listCalendar__start_date.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__end_date.size(); i++) {
	    	iterator = (ValueList)listCalendar__end_date.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__monday.size(); i++) {
	    	iterator = (ValueList)listCalendar__monday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__tuesday.size(); i++) {
	    	iterator = (ValueList)listCalendar__tuesday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__wednesday.size(); i++) {
	    	iterator = (ValueList)listCalendar__wednesday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__thursday.size(); i++) {
	    	iterator = (ValueList)listCalendar__thursday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__friday.size(); i++) {
	    	iterator = (ValueList)listCalendar__friday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__saturday.size(); i++) {
	    	iterator = (ValueList)listCalendar__saturday.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendar__sunday.size(); i++) {
	    	iterator = (ValueList)listCalendar__sunday.get(i);
	    	iterator.dumpValues();
	    }
	}

	public TransxchangeCalendar(TransxchangeHandler owner) {
		listCalendar__service_id = new ArrayList();
		listCalendar__monday = new ArrayList();
		listCalendar__tuesday = new ArrayList();
		listCalendar__wednesday = new ArrayList();	
		listCalendar__thursday = new ArrayList();
		listCalendar__friday = new ArrayList();
		listCalendar__saturday = new ArrayList();
		listCalendar__sunday = new ArrayList();
		listCalendar__start_date = new ArrayList();
		listCalendar__end_date = new ArrayList();
	}
}
