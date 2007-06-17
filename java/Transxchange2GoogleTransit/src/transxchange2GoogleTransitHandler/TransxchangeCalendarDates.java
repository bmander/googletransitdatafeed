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

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

/* 
 * This class handles the TransXChange xml input file under the aspect of
 * 	calendar dates which might have been excluded from or added to a service
 */
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
	List listCalendar_OOL_start_date = null; // v1.5: Out-of-line date range start date. A out-of-line date range is a date range which is not associated to a service
	ValueList newCalendar_OOL_start_date;
	List listCalendar_OOL_end_date = null;  // v1.5: Out-of-line date range end date
	ValueList newCalendar_OOL_end_date;
	
	// XML markups
	static final String[] _key_calendar_dates_start = {"Service", "SpecialDaysOperation", "DaysOfOperation", "StartDate", "1"};
	static final String[] _key_calendar_dates_end = {"Service", "SpecialDaysOperation", "DaysOfOperation", "EndDate", "1"};
	static final String[] _key_calendar_no_dates_start = {"Service", "SpecialDaysOperation", "DaysOfNonOperation", "StartDate", "2"};
	static final String[] _key_calendar_no_dates_end = {"Service", "SpecialDaysOperation", "DaysOfNonOperation", "EndDate", "2"};

	// v1.5: Bank holiday XML markups
	static final String[] _key_calendar_bankholiday_operation_spring = {"Service", "BankHolidayOperation", "DaysOfOperation", "SpringHoliday", "1"};
	static final String[] _key_calendar_bankholiday_nooperation_all = {"Service", "BankHolidayOperation", "DaysOfNonOperation", "AllBankHolidays", "2"};
	
	// Parse keys
	String keyOperationDays = "";
	String keyOperationDaysStart = "";
	String keyOperationDaysBank = ""; // v1.5: key for bank holidays

	// Some support variables
	String calendarDateOperationDayStart = "";
	boolean dayOfNoOperation = false;

	// v1.5: Bank holidays support map
	Map bankHolidays2007;
	Map bankHolidays2008;
	Map bankHolidays2009;
	
	/*
	 * Utility methods to retrieve Google Transit feed structures
	 */
	public List getListCalendarDates__service_id() {
		return listCalendarDates__service_id;
	}

	public List getListCalendarDates__date() {
		return listCalendarDates__date;
	}

	public List getListCalendarDates__exception_type() {
		return listCalendarDates__exception_type;
	}

	// v1.5: Out-of-line dates start
	public List getListOOLDates_start() {
		return listCalendar_OOL_start_date;
	}

	// v1.5: Out-of-line dates end
	public List getListOOLDates_end() {
		return listCalendar_OOL_end_date;
	}

	// v1.5: Reset out-of-line date list
	public void resetOOLDates_start() {
		listCalendar_OOL_start_date = null;
	}

	// v1.5: Reset out-of-line date list
	public void resetOOLDates_end() {
		listCalendar_OOL_end_date = null;		
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
		throws SAXParseException {
		
	    super.startElement(uri, name, qName, atts);
	    if (qName.equals(_key_calendar_dates_start[0])) // also covers no_dates and bank holidays
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
	    
	    // v1.5 Bank holiday keys
	    if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && qName.equals(_key_calendar_bankholiday_nooperation_all[1])) // also covers all other bank holiday cases
	    	keyNested = _key_calendar_bankholiday_nooperation_all[1];
	    if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && keyNested.equals(_key_calendar_bankholiday_nooperation_all[1]) && qName.equals(_key_calendar_bankholiday_nooperation_all[2]))
	    	keyOperationDaysBank = _key_calendar_bankholiday_nooperation_all[2];
	    if (key.equals(_key_calendar_bankholiday_operation_spring[0]) && keyNested.equals(_key_calendar_bankholiday_operation_spring[1]) && qName.equals(_key_calendar_bankholiday_operation_spring[2]))
	    	keyOperationDaysBank = _key_calendar_bankholiday_operation_spring[2];
	}
	
	public void endElement (String uri, String name, String qName) {

		String service;
		
		if (niceString.length() > 0) {
		
			if (key.equals(_key_calendar_dates_start[0]) && keyNested.equals(_key_calendar_dates_start[1]) && (keyOperationDays.equals(_key_calendar_dates_start[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.equals(_key_calendar_dates_start[3]))
				calendarDateOperationDayStart = niceString;
			if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.equals(_key_calendar_dates_end[3])) {       		
				try {
					SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // US - determined by location of Google Labs, not transit operator
					sdfIn.setCalendar(Calendar.getInstance());
					Date calendarDatesOperationDay = sdfIn.parse(calendarDateOperationDayStart);
					Date calendarDateOperationDayEnd = sdfIn.parse(niceString);
					GregorianCalendar gcOperationDay = new GregorianCalendar();
					gcOperationDay.setTime(calendarDatesOperationDay);           		
					service = handler.getCalendar().getService();
					if (service.length() == 0) { // v1.5: Out-of-line OperatingProfile? E.g. special operations days for a single vehicle journey as opposed for a service. 
						if (listCalendar_OOL_start_date == null)
							listCalendar_OOL_start_date = new ArrayList(); // If previously found OOL dates were read and reset some place else, recreate list 
						if (listCalendar_OOL_end_date == null)
							listCalendar_OOL_end_date = new ArrayList(); 
						newCalendar_OOL_start_date = new ValueList(_key_calendar_dates_start[0]);
						listCalendar_OOL_start_date.add(newCalendar_OOL_start_date);
						newCalendar_OOL_start_date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
						gcOperationDay.setTime(calendarDateOperationDayEnd);           		
						newCalendar_OOL_end_date = new ValueList(_key_calendar_dates_end[0]);
						listCalendar_OOL_end_date.add(newCalendar_OOL_end_date);
						newCalendar_OOL_end_date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
						
    				
    				
    				
    				//       				System.out.println("Out of line SpecialDaysOperation. Start: " + calendarDateOperationDayStart + "End: " + calendarDateOperationDayEnd);
//      				service = "hier geht's weiter - Spezielle Datenstruktur, die in TransxchangeTrips abgerufen wird"; // set flag in service. Resolved in TransxchangeTrips 
//					throw new SAXParseException("Out of line SpecialDaysOperation: " + calendarDatesOperationDaysStart + " must be associated to a service.", null);
    				
    				
					} else {
						while (calendarDatesOperationDay.compareTo(calendarDateOperationDayEnd) <= 0) {
							newCalendarDates__service_id = new ValueList(_key_calendar_dates_start[0]);
							listCalendarDates__service_id.add(newCalendarDates__service_id);
							newCalendarDates__service_id.addValue(service);
							newCalendarDates__date = new ValueList(_key_calendar_dates_start[2]);
							listCalendarDates__date.add(newCalendarDates__date);
							newCalendarDates__date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
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
					}
				} catch (Exception e) {
					handler.setParseError(e.getMessage()); //"Exception: Calendar start date does not read " + calendarDatesOperationDaysStart);
//        		System.out.println("Exception: Calendar start date does not read");
//        		System.out.println(calendarDatesOperationDaysStart);
	        	}        
        	}        
        } else {

        	// v1.5: handle bank holidays no-operation
        	if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && keyNested.equals(_key_calendar_bankholiday_nooperation_all[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_nooperation_all[2])) {       
        		service = handler.getCalendar().getService();
        		createBankHolidaysAll(service, bankHolidays2007, _key_calendar_bankholiday_nooperation_all[4]);
        		createBankHolidaysAll(service, bankHolidays2008, _key_calendar_bankholiday_nooperation_all[4]);
        		createBankHolidaysAll(service, bankHolidays2009, _key_calendar_bankholiday_nooperation_all[4]);
        	}
        	
        	// v1.5 handle bank holidays operation
        	if (key.equals(_key_calendar_bankholiday_operation_spring[0]) && keyNested.equals(_key_calendar_bankholiday_operation_spring[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_operation_spring[2])) {       
        		service = handler.getCalendar().getService();
        		createBankHoliday(service, qName, bankHolidays2007, _key_calendar_bankholiday_nooperation_all[4]);
        		createBankHoliday(service, qName, bankHolidays2008, _key_calendar_bankholiday_nooperation_all[4]);
        		createBankHoliday(service, qName, bankHolidays2009, _key_calendar_bankholiday_nooperation_all[4]);
        	}
        	
        	
        }    
	}

	public void clearKeys (String qName) {
	    if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2]))&& keyOperationDaysStart.equals(_key_calendar_dates_end[3]))
	    	keyOperationDaysStart = "";
	    	else
	    		if (key.equals(_key_calendar_dates_end[0]) && keyNested.equals(_key_calendar_dates_end[1]) && (keyOperationDays.equals(_key_calendar_dates_end[2]) || keyOperationDays.equals(_key_calendar_no_dates_end[2])) && keyOperationDaysStart.length() == 0 && (qName.equals(_key_calendar_dates_start[2]) || qName.equals(_key_calendar_no_dates_start[2])))
	    			keyOperationDays = "";	

	    if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && keyNested.equals(_key_calendar_bankholiday_nooperation_all[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_nooperation_all[2]))
   			keyOperationDaysBank = "";	
	    if (key.equals(_key_calendar_bankholiday_operation_spring[0]) && keyNested.equals(_key_calendar_bankholiday_operation_spring[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_operation_spring[2]))
   			keyOperationDaysBank = "";	
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
		super(owner);
		listCalendarDates__service_id  = new ArrayList();
		listCalendarDates__date  = new ArrayList();
		listCalendarDates__exception_type  = new ArrayList();
		
		/*
		 * v1.5: Initialize bank holidays maps
		 */
		bankHolidays2007 = new HashMap();
		bankHolidays2007.put("NewYearsDay", "20070101");
//		bankHolidays2007.put("Jan2ndScotland", "20070102");
		bankHolidays2007.put("GoodFriday", "20070406");
		bankHolidays2007.put("EasterMonday", "20070409");
		bankHolidays2007.put("MayDay", "20070507");
		bankHolidays2007.put("SpringBank", "20070528");
//		bankHolidays2007.put("AugustBankHolidayScotland", "20070806");
		bankHolidays2007.put("LateSummerBankHolidayDayNotScotland", "20070827");
		bankHolidays2007.put("ChristmasDay", "20071225");
		bankHolidays2007.put("BoxingDay", "20071226");
		bankHolidays2008 = new HashMap();
		bankHolidays2008.put("NewYearsDay", "20080101");
//		bankHolidays2008.put("Jan2ndScotland", "20080102");
		bankHolidays2008.put("GoodFriday", "20080321");
		bankHolidays2008.put("EasterMonday", "20080324");
		bankHolidays2008.put("MayDay", "20080505");
		bankHolidays2008.put("SpringBank", "20080526");
//		bankHolidays2008.put("AugustBankHolidayScotland", "20080804");
		bankHolidays2008.put("LateSummerBankHolidayDayNotScotland", "20080825");
		bankHolidays2008.put("ChristmasDay", "20081225");
		bankHolidays2008.put("BoxingDay", "20081226");
		bankHolidays2009 = new HashMap();
		bankHolidays2009.put("NewYearsDay", "20090101");
//		bankHolidays2009.put("Jan2ndScotland", "20090102");
		bankHolidays2009.put("GoodFriday", "20090410");
		bankHolidays2009.put("EasterMonday", "20090413");
		bankHolidays2009.put("MayDay", "20090504");
		bankHolidays2009.put("SpringBank", "20090525");
//		bankHolidays2009.put("AugustBankHolidayScotland", "20090803");
		bankHolidays2009.put("LateSummerBankHolidayDayNotScotland", "20090831");
		bankHolidays2009.put("ChristmasDay", "20091225");
		bankHolidays2009.put("BoxingDay", "20091228");
	}
	
	private void createBankHolidaysAll(String bankService, Map bankHolidayMap, String exceptionType) {        		
		
		Iterator iter = bankHolidayMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry e = (Map.Entry)iter.next();
			newCalendarDates__service_id = new ValueList(_key_calendar_bankholiday_nooperation_all[0]);
			listCalendarDates__service_id.add(newCalendarDates__service_id);
			newCalendarDates__service_id.addValue(bankService);
			newCalendarDates__date = new ValueList(_key_calendar_bankholiday_nooperation_all[2]);
			listCalendarDates__date.add(newCalendarDates__date);
			newCalendarDates__date.addValue((String)e.getValue());
			newCalendarDates__exception_type = new ValueList(_key_calendar_bankholiday_nooperation_all[2]);
			listCalendarDates__exception_type.add(newCalendarDates__exception_type);
			newCalendarDates__exception_type.addValue(exceptionType);
		}
	}

	private void createBankHoliday(String bankService, String holiday, Map bankHolidayMap, String exceptionType) {        		

		newCalendarDates__service_id = new ValueList(_key_calendar_bankholiday_operation_spring[0]);
		listCalendarDates__service_id.add(newCalendarDates__service_id);
		newCalendarDates__service_id.addValue(bankService);
		newCalendarDates__date = new ValueList(_key_calendar_bankholiday_operation_spring[2]);
		listCalendarDates__date.add(newCalendarDates__date);
		newCalendarDates__date.addValue((String)bankHolidayMap.get(holiday));
		newCalendarDates__exception_type = new ValueList(_key_calendar_bankholiday_operation_spring[2]);
		listCalendarDates__exception_type.add(newCalendarDates__exception_type);
		newCalendarDates__exception_type.addValue(_key_calendar_bankholiday_operation_spring[4]);
	}
}