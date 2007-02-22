/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * File:    TransxchangeCalendarDates.java
 * Version:	1.0
 * Date: 	17-Oct-2006
 * 
 * Copyright (C) 2006, Joachim Pfeiffer
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.xml.sax.Attributes;



public class TransxchangeCalendarDates extends TransxchangeDataAspect {

	// xml keys and output field fillers
	static final String[] key_calendar_dates__service_id = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_calendar_dates__date = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};
	static final String[] key_calendar_dates__exception_type = new String[] {"__transxchange2GoogleTransit_drawDefault", "", ""};

	// Parsed data 
	List listCalendarDates__service_id;
	ValueList newCalendarDates__service_id;
	List listCalendarDates__date;
	ValueList newCalendarDates__date;
	List listCalendarDates__exception_type;
	ValueList newCalendarDates__exception_type;

	static final String[] _key_calendar_dates_start = {"Service", "SpecialDaysOperation", "DaysOfOperation", "StartDate", "1"};
	static final String[] _key_calendar_dates_end = {"Service", "SpecialDaysOperation", "DaysOfOperation", "EndDate", "1"};
	static final String[] _key_calendar_no_dates_start = {"Service", "SpecialDaysOperation", "DaysOfNonOperation", "StartDate", "2"};
	static final String[] _key_calendar_no_dates_end = {"Service", "SpecialDaysOperation", "DaysOfNonOperation", "EndDate", "2"};
	String keyOperationDays = "";
	String keyOperationDaysStart = "";
	String calendarDatesOperationDaysStart = "";
	boolean dayOfNoOperation = false;
	TransxchangeHandler handler;
	
	public List getListCalendarDates__service_id() {
		return listCalendarDates__service_id;
	}

	public List getListCalendarDates__date() {
		return listCalendarDates__date;
	}

	public List getListCalendarDates__exception_type() {
		return listCalendarDates__exception_type;
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

	    super.startElement(uri, name, qName, atts);
	    if (qName.equals(_key_calendar_dates_start[0])) // also covers no_dates
			key = _key_calendar_dates_start[0];
	    if (key.equals(_key_calendar_dates_start[0]) && qName.equals(_key_calendar_dates_start[1]) && keyOperationDays.length() == 0) {
	    	keyNested = _key_calendar_dates_start[1];
	    }
	    if (key.equals(_key_calendar_dates_start[0]) && keyNested.equals(_key_calendar_dates_start[1]) && qName.equals(_key_calendar_dates_start[2])) {
	    	keyOperationDays = _key_calendar_dates_start[2];
	    }
	    if (key.equals(_key_calendar_dates_start[0]) && keyNested.equals(_key_calendar_dates_start[1]) && keyOperationDays.equals(_key_calendar_dates_start[2]) && qName.equals(_key_calendar_dates_start[3])) {
	    	keyOperationDaysStart = _key_calendar_dates_start[3];
	    	niceString = "";
	    	dayOfNoOperation = false;
	    }
	    if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && keyOperationDays.equals(_key_calendar_dates_end[2]) && qName.equals(_key_calendar_dates_end[3])) {
	    	keyOperationDaysStart = _key_calendar_dates_end[3];
	    	niceString = "";    	
	    }
	    if (key.equals(_key_calendar_no_dates_start[0]) && keyNested.equals(_key_calendar_no_dates_start[1]) && qName.equals(_key_calendar_no_dates_start[2])) {
	    	keyOperationDays = _key_calendar_no_dates_start[2];
	    }
	    if (key.equals(_key_calendar_no_dates_start[0]) && keyNested.equals(_key_calendar_no_dates_start[1]) && keyOperationDays.equals(_key_calendar_no_dates_start[2]) && qName.equals(_key_calendar_no_dates_start[3])) {
	    	keyOperationDaysStart = _key_calendar_no_dates_start[3]; // equals operation day
	    	niceString = "";    	
	    	dayOfNoOperation = true;
	    }
	    if (key.equals(_key_calendar_no_dates_end[0]) && keyNested.equals(_key_calendar_no_dates_end[1]) && keyOperationDays.equals(_key_calendar_no_dates_end[2]) && qName.equals(_key_calendar_no_dates_end[3])) {
	    	keyOperationDaysStart = _key_calendar_no_dates_end[3]; // equals operation day
	    	niceString = "";    	
	    }
	}
	
	public void endElement (String uri, String name, String qName) {
		
		if (niceString.length() == 0)
			return;
		
        if (key.equals(_key_calendar_dates_start[0]) && keyNested.equals(_key_calendar_dates_start[1]) && (keyOperationDays.equals(_key_calendar_dates_start[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.equals(_key_calendar_dates_start[3]))
        	calendarDatesOperationDaysStart = niceString;
        if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.equals(_key_calendar_dates_end[3])) {       		
        	try {
        		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // US - determined by location of Google Labs, not transit operator
        		sdfIn.setCalendar(Calendar.getInstance());
            	Date calendarDatesOperationDay = sdfIn.parse(calendarDatesOperationDaysStart);
            	Date calendarDateOperationDayEnd = sdfIn.parse(niceString);
            	GregorianCalendar gcOperationDay = new GregorianCalendar();
           		gcOperationDay.setTime(calendarDatesOperationDay);           		
           		while (calendarDatesOperationDay.compareTo(calendarDateOperationDayEnd) <= 0) {
           			newCalendarDates__service_id = new ValueList(_key_calendar_dates_start[0]);
           			listCalendarDates__service_id.add(newCalendarDates__service_id);
           			newCalendarDates__service_id.addValue(handler.getCalendar().getService());
           			newCalendarDates__date = new ValueList(_key_calendar_dates_start[2]);
           			listCalendarDates__date.add(newCalendarDates__date);
/* Java 1.5           			newCalendarDates__date.addValue(String.format("%04d%02d%02d", gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
*/					newCalendarDates__date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
           			newCalendarDates__exception_type = new ValueList(_key_calendar_dates_start[2]);
           			listCalendarDates__exception_type.add(newCalendarDates__exception_type);
                	if (dayOfNoOperation) {
               			newCalendarDates__exception_type.addValue(_key_calendar_no_dates_start[4]);
                	} else {
               			newCalendarDates__exception_type.addValue(_key_calendar_dates_start[4]);
                	}
                	gcOperationDay.add(Calendar.DAY_OF_YEAR, 1);
            		calendarDatesOperationDay = gcOperationDay.getTime();
           		}
        	} catch (Exception e) {
        		System.out.println("Exception: Calendar start date does not read");
        		System.out.println(calendarDatesOperationDaysStart);
        	}
        }
	}

	public void clearKeys (String qName) {
	    if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2]))&& keyOperationDaysStart.equals(_key_calendar_dates_end[3]))
	    	keyOperationDaysStart = "";
	    	else
	    		if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.length() == 0)
	    			keyOperationDays = "";
}

	
	public void completeData() {
  	    // Add quotes if needed
  	    csvProofList(listCalendarDates__service_id);
  	    csvProofList(listCalendarDates__date);
  	    csvProofList(listCalendarDates__exception_type);
	}
	
	public void dumpValues() {
		int i;
		ValueList iterator;

	    System.out.println("*** Calendar dates");
	    for (i = 0; i < listCalendarDates__service_id.size(); i++) {
	    	iterator = (ValueList)listCalendarDates__service_id.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendarDates__date.size(); i++) {
	    	iterator = (ValueList)listCalendarDates__date.get(i);
	    	iterator.dumpValues();
	    }
	    for (i = 0; i < listCalendarDates__exception_type.size(); i++) {
	    	iterator = (ValueList)listCalendarDates__exception_type.get(i);
	    	iterator.dumpValues();
	    }
	}
	
	public TransxchangeCalendarDates(TransxchangeHandler owner) {
		listCalendarDates__service_id  = new ArrayList();
		listCalendarDates__date  = new ArrayList();
		listCalendarDates__exception_type  = new ArrayList();
		
		handler = owner;
	}
}
