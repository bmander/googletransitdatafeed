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
	List listCalendar_OOL_exception_type = null;  // v1.5: Out-of-line date range end date
	ValueList newCalendarDates__OOL_exception_type;
	
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
	// V1.6.3 ArrayList to dynamically create the years covered by the service 
	ArrayList bankHolidays;
	HashMap years = new HashMap(); //  years as HashMap to maintain unique entries
	ArrayList yearsList = new ArrayList(); // years as list to allow iterating through years
//	Map bankHolidays2007;
//	Map bankHolidays2008;
//	Map bankHolidays2009;
	
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
		
		if (niceString == null || niceString.length() > 0) {
		
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
						if (listCalendar_OOL_exception_type == null)
							listCalendar_OOL_exception_type = new ArrayList(); 
						newCalendar_OOL_start_date = new ValueList(_key_calendar_dates_start[0]);
						listCalendar_OOL_start_date.add(newCalendar_OOL_start_date);
						newCalendar_OOL_start_date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
						gcOperationDay.setTime(calendarDateOperationDayEnd);           		
						newCalendar_OOL_end_date = new ValueList(_key_calendar_dates_end[0]);
						listCalendar_OOL_end_date.add(newCalendar_OOL_end_date);
						newCalendar_OOL_end_date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
						newCalendarDates__OOL_exception_type = new ValueList(_key_calendar_no_dates_start[0]);
						listCalendar_OOL_exception_type.add(newCalendarDates__OOL_exception_type);
						if (dayOfNoOperation)
							newCalendarDates__OOL_exception_type.addValue(_key_calendar_no_dates_start[4]);
						else
							newCalendarDates__OOL_exception_type.addValue(_key_calendar_dates_start[4]);
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
							if (dayOfNoOperation)
								newCalendarDates__exception_type.addValue(_key_calendar_no_dates_start[4]);
							else
								newCalendarDates__exception_type.addValue(_key_calendar_dates_start[4]);
							gcOperationDay.add(Calendar.DAY_OF_YEAR, 1);
							calendarDatesOperationDay = gcOperationDay.getTime();
						}
					}
				} catch (Exception e) {
					handler.setParseError(e.getMessage()); 
				}        
        	}        
        } else {

        	// v1.6.3: Roll out years covered by the service 
        	TransxchangeCalendar calendar = handler.getCalendar();
        	int startYear = calendar.getStartYear();
        	int endYear = calendar.getEndYear();
        	if (startYear != -1 && endYear != -1 && endYear >= startYear && yearsList.size() == 0) {
        		for (int i = startYear; i <= endYear; i++) 
        			if (!years.containsKey(i)) {
        				years.put(i, createHolidays(i));
        				yearsList.add(i);
        		}
    		}
    		service = handler.getCalendar().getService();
    		HashMap holidays;
        	Iterator i;
        	i = yearsList.iterator();
        	while (i.hasNext()) {
        		holidays = (HashMap)years.get(i.next());
            	if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && keyNested.equals(_key_calendar_bankholiday_nooperation_all[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_nooperation_all[2]))       
            		createBankHolidaysAll(service, holidays, _key_calendar_bankholiday_nooperation_all[4]);
           		if (key.equals(_key_calendar_bankholiday_operation_spring[0]) && keyNested.equals(_key_calendar_bankholiday_operation_spring[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_operation_spring[2]))       
           			createBankHoliday(service, qName, holidays, _key_calendar_bankholiday_nooperation_all[4]);
        	}

        	// v1.5: handle bank holidays no-operation
/*        	if (key.equals(_key_calendar_bankholiday_nooperation_all[0]) && keyNested.equals(_key_calendar_bankholiday_nooperation_all[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_nooperation_all[2])) {       
        		service = handler.getCalendar().getService();
//        		createBankHolidaysAll(service, bankHolidays2007, _key_calendar_bankholiday_nooperation_all[4]);
//        		createBankHolidaysAll(service, bankHolidays2008, _key_calendar_bankholiday_nooperation_all[4]);
//        		createBankHolidaysAll(service, bankHolidays2009, _key_calendar_bankholiday_nooperation_all[4]);
        	}
        	
        	// v1.5 handle bank holidays operation
        	if (key.equals(_key_calendar_bankholiday_operation_spring[0]) && keyNested.equals(_key_calendar_bankholiday_operation_spring[1]) && keyOperationDaysBank.equals(_key_calendar_bankholiday_operation_spring[2])) {       
        		service = handler.getCalendar().getService();
//        		createBankHoliday(service, qName, bankHolidays2007, _key_calendar_bankholiday_nooperation_all[4]);
//        		createBankHoliday(service, qName, bankHolidays2008, _key_calendar_bankholiday_nooperation_all[4]);
//        		createBankHoliday(service, qName, bankHolidays2009, _key_calendar_bankholiday_nooperation_all[4]);
        	}
*/
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
	
	private HashMap createHolidays(int year) {

		HashMap bankHolidays = new HashMap();

		// NewYearsDay - consider replacement holiday of January 1st falls on Saturday or Sunday
		Calendar newyearsDay = Calendar.getInstance();
		newyearsDay.set(year, 0, 1); // Java starts counting months at 0
		int dayOfWeek = newyearsDay.get(Calendar.DAY_OF_WEEK);
		String theDay;
		switch (dayOfWeek) {
			case Calendar.SATURDAY:
			theDay = "0103";
			break;
			case Calendar.SUNDAY:
			theDay = "0102";
			break;
			default:
			theDay = "0101";
			break;
		}
		bankHolidays.put("NewYearsDay", year + theDay);

		// GoodFriday and EasterMonday
		int a = year % 19;
		int b = year / 100;
		int c = year % 100;
		int d = b / 4;
		int e = b % 4;
		int f = ( b + 8 ) / 25;
		int g = ( b - f + 1 ) / 3;
		int h = ( 19 * a + b - d - g + 15 ) % 30;
		int i = c / 4;
		int k = c % 4;
		int l = (32 + 2 * e + 2 * i - h - k) % 7;
		int m = (a + 11 * h + 22 * l) / 451;
		int p = (h + l - 7 * m + 114) % 31;
		int month = ( h + l - 7 * m + 114 ) / 31;
		int day = p + 1;
		Calendar gc = Calendar.getInstance();
		gc.set(year, month, day);
		long easterSunday = gc.getTimeInMillis();
		long easterFriday = easterSunday - 2 * 24 * 60 * 60 * 1000; // Two days before Easter Sunday
		long easterMonday = easterSunday + 24 * 60 * 60 * 1000; // One day after Easter Sunday
		Calendar easterHoliday = Calendar.getInstance();
		easterHoliday.setTimeInMillis(easterFriday);
		bankHolidays.put("GoodFriday", TransxchangeDataAspect.formatDate(easterHoliday.get(Calendar.YEAR), easterHoliday.get(Calendar.MONTH), easterHoliday.get(Calendar.DAY_OF_MONTH)));
		easterHoliday.setTimeInMillis(easterMonday);
		bankHolidays.put("EasterMonday", TransxchangeDataAspect.formatDate(easterHoliday.get(Calendar.YEAR), easterHoliday.get(Calendar.MONTH), easterHoliday.get(Calendar.DAY_OF_MONTH)));

		// MayDay: First Monday in May
		Calendar mayDayHoliday = Calendar.getInstance();
		mayDayHoliday.set(Calendar.YEAR, year);
		mayDayHoliday.set(Calendar.MONTH, Calendar.MAY);
		mayDayHoliday.set(Calendar.DAY_OF_MONTH, 1);
		
		long mayDay = mayDayHoliday.getTimeInMillis();
		dayOfWeek = mayDayHoliday.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.TUESDAY:
			mayDay += 6 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.WEDNESDAY:
			mayDay += 5 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.THURSDAY:
			mayDay += 4 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.FRIDAY:
			mayDay += 3 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.SATURDAY:
			mayDay += 2 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.SUNDAY:
			mayDay += 24 * 60 * 60 * 1000;
			break;
		}
		mayDayHoliday.setTimeInMillis(mayDay);
		bankHolidays.put("MayDay", TransxchangeDataAspect.formatDate(mayDayHoliday.get(Calendar.YEAR), 
			mayDayHoliday.get(Calendar.MONTH) + 1, // Java starts counting months at 0 
			mayDayHoliday.get(Calendar.DAY_OF_MONTH)));
		
		// SpringBank: last Monday in May
		Calendar springBankHoliday = Calendar.getInstance();
		springBankHoliday.set(Calendar.YEAR, year);
		springBankHoliday.set(Calendar.MONTH, Calendar.MAY);
		springBankHoliday.set(Calendar.DAY_OF_MONTH, 31);

		long springBank = springBankHoliday.getTimeInMillis();
		dayOfWeek = springBankHoliday.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.SUNDAY:
			springBank -= 6 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.SATURDAY:
			springBank -= 5 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.FRIDAY:
			springBank -= 4 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.THURSDAY:
			springBank -= 3 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.WEDNESDAY:
			springBank -= 2 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.TUESDAY:
			springBank -= 24 * 60 * 60 * 1000;
			break;
		}
		springBankHoliday.setTimeInMillis(springBank);
		bankHolidays.put("SpringBank", TransxchangeDataAspect.formatDate(springBankHoliday.get(Calendar.YEAR), 
			springBankHoliday.get(Calendar.MONTH) + 1, // Java starts counting months at 0
			springBankHoliday.get(Calendar.DAY_OF_MONTH)));
		
		// LateSummerHolidayNotScotland : last Monday in August
		Calendar lateSummerHoliday = Calendar.getInstance();
		lateSummerHoliday.set(Calendar.YEAR, year);
		lateSummerHoliday.set(Calendar.MONTH, Calendar.AUGUST);
		lateSummerHoliday.set(Calendar.DAY_OF_MONTH, 31);

		long summerHoliday = lateSummerHoliday.getTimeInMillis();
		dayOfWeek = lateSummerHoliday.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.SUNDAY:
			summerHoliday -= 6 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.SATURDAY:
			summerHoliday -= 5 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.FRIDAY:
			summerHoliday -= 4 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.THURSDAY:
			summerHoliday -= 3 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.WEDNESDAY:
			summerHoliday -= 2 * 24 * 60 * 60 * 1000;
			break;
			case Calendar.TUESDAY:
			summerHoliday -= 24 * 60 * 60 * 1000;
			break;
		}
		lateSummerHoliday.setTimeInMillis(summerHoliday);
		bankHolidays.put("LateSummerBankHolidayNotScotland", TransxchangeDataAspect.formatDate(lateSummerHoliday.get(Calendar.YEAR), 
			lateSummerHoliday.get(Calendar.MONTH) + 1, // Java starts counting months at 0
			lateSummerHoliday.get(Calendar.DAY_OF_MONTH)));
		
		// ChristmasDay - consider replacement holiday for Christmas Day if it falls on a Saturday or Sunday
		Calendar christmasDay = Calendar.getInstance();
		christmasDay.set(year, 11, 25); // Java starts counting months at 0
		dayOfWeek = christmasDay.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.SATURDAY:
			theDay = "1227";
			break;
			case Calendar.SUNDAY:
			theDay = "1227";
			break;
			default:
			theDay = "1225";
			break;
		}
		bankHolidays.put("ChristmasDay", year + theDay);
		
		// BoxingDay
		// ChristmasDay - consider replacement holiday for Boxing Day if it falls on a Saturday or Sunday
		Calendar boxingDay = Calendar.getInstance();
		boxingDay.set(year, 11, 26); // Java starts counting months at 0
		dayOfWeek = boxingDay.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.SATURDAY:
			theDay = "1228";
			break;
			case Calendar.SUNDAY:
			theDay = "1228";
			break;
			default:
			theDay = "1226";
			break;
		}
		bankHolidays.put("BoxingDay", year + theDay);
//		bankHolidays.put("BoxingDay  ", year + "1226");

/*		
		Dim hols As BankHols
	    Dim Yr as string
	    '
	    Yr = "2009"
	    ' find the bank hols for the year
	    hols = CalcBankHol(Yr)
	    '
	    ' Returns 8 bank holidays.
	    //
	    //This code is copyrighted and has    // limited warranties.Please see http://
	    //     www.Planet-Source-Code.com/vb/scripts/Sh
	    //     owCode.asp?txtCodeId=71665&lngWId=-10    //for details.    //**************************************
	    //     
	    
	    Public Type BankHols
	    NewYear As Variant
	    GoodFriday As Variant
	    EasterMon As Variant
	    MayDay As Variant
	    SpringBank As Variant
	    AugustBankHol As Variant
	    ChristmasDay As Variant
	    BoxingDay As Variant
	    End Type
	    Function CalcBankHol(Yr As String) As BankHols
	    'Calculate England and Wales Bank Holiday Dates 1978 - 2099
	    Dim strDayName As String
	    Dim strEastSun As String
	    Dim i As Integer
	    Dim strDate
	    ' No 1: New Year's Day
	    ' 1st January or next weekday if it falls on a Saturday or Sunday
	    '
	    strDayName = Format("1/1/" & Yr, "dddd")
	    If strDayName = "Saturday" Then
	    CalcBankHol.NewYear = DateValue("3" & "/" & "1" & "/" & Yr)
	    ElseIf strDayName = "Sunday" Then
	    CalcBankHol.NewYear = DateValue("2" & "/" & "1" & "/" & Yr)
	    Else
	    CalcBankHol.NewYear = DateValue("1" & "/" & "1" & "/" & Yr)
	    End If
	    '
	    ' Find Easter Sunday
	    strEastSun = EasterSundayIs(Yr)
	    '
	    ' No 2: Good Friday
	    ' 2 days before Easter Sunday
	    '
	    CalcBankHol.GoodFriday = DateValue(strEastSun) - 2
	    ' No 3: Easter Monday
	    ' 1 day after Easter Sunday
	    '
	    CalcBankHol.EasterMon = DateValue(strEastSun) + 1
	    '
	    ' No 4: May Day
	    ' 1st Monday in May
	    '
	    For i = 1 To 8 ' must come within 8 days
	    strDate = Trim(str(i)) & "/" & "5" & "/" & Yr
	    strDayName = WeekdayName(Weekday(strDate), False, 1)
	    If strDayName = "Monday" Then Exit For
	    Next i
	    CalcBankHol.MayDay = DateValue(strDate)
	    '
	    ' No 5: Spring Bank Holiday
	    ' Last Monday in May
	    '
	    For i = 31 To 31 - 8 Step -1 ' must come within 8 days
	    strDate = Trim(str(i)) & "/" & "5" & "/" & Yr
	    strDayName = WeekdayName(Weekday(strDate), False, 1)
	    If strDayName = "Monday" Then Exit For
	    Next i
	    CalcBankHol.SpringBank = DateValue(strDate)
	    '
	    ' No 6: Late Summer Bank Holiday
	    ' Last Monday in August
	    '
	    For i = 31 To 31 - 8 Step -1 ' must come within 8 days
	    strDate = Trim(str(i)) & "/" & "8" & "/" & Yr
	    strDayName = WeekdayName(Weekday(strDate), False, 1)
	    If strDayName = "Monday" Then Exit For
	    Next i
	    CalcBankHol.AugustBankHol = DateValue(strDate)
	    '
	    ' No 7: Christmas Day
	    ' 25 December, or next Monday in lieu if falls on Saturday or Sunday
	    '
	    strDayName = WeekdayName(Weekday("25/12/" & Yr), False, 1)
	    Select Case strDayName
	    Case "Saturday"
	    CalcBankHol.ChristmasDay = DateValue("27/12/" & Yr)
	    Case "Sunday"
	    CalcBankHol.ChristmasDay = DateValue("26/12/" & Yr)
	    Case Else
	    CalcBankHol.ChristmasDay = DateValue("25/12/" & Yr)
	    End Select
	    '
	    ' No 8: Boxing Day
	    ' 26 December, or next Monday if this falls on a Saturday or
	    ' Tuesday if it falls on a Sunday
	    '
	    strDayName = WeekdayName(Weekday("26/12/" & Yr), False, 1)
	    Select Case strDayName
	    Case "Saturday"
	    CalcBankHol.BoxingDay = DateValue("28/12/" & Yr)
	    Case "Sunday"
	    CalcBankHol.BoxingDay = DateValue("27/12/" & Yr)
	    Case Else
	    CalcBankHol.BoxingDay = DateValue("26/12/" & Yr)
	    End Select
	    End Function
	    Public Function EasterSundayIs(inYear As String)
	    '
	    'Local Variables
	    '
	    Dim g%, c%, x%, z%, d%, e%, n%, mon%
	    Dim Y%
	    '
	    Y = Val(inYear)
	    '
	    'Do the calculations of the golden number (g)
	    '
	    g = (Y Mod 19) + 1
	    c = Int(Y / 100) + 1
	    x = Int(3 * c / 4) - 12
	    z = Int((8 * c + 5) / 25) - 5
	    d = Int(5 * Y / 4) - x - 10
	    e = (11 * g + 20 + z - x) Mod 30
	    If (e = 25) And (g > 11) Or e = 24 Then
	    e = e + 1
	    End If
	    n = 44 - e
	    If n < 21 Then n = n + 30
	    n = n + 7 - (d + n) Mod 7
	    If n > 31 Then
	    mon = 4
	    n = n - 31
	    Else
	    mon = 3
	    End If
	    EasterSundayIs = DateSerial(Y, mon, n)
*/
		return bankHolidays;
	}

	public void calendarDatesRolloutOOLDates(String serviceId) {

		int i;
		
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyyMMdd", Locale.US); // US - determined by location of Google Labs, not transit operator
		sdfIn.setCalendar(Calendar.getInstance());
		Date calendarDatesOperationDay; 
		Date calendarDateOperationDayEnd;
		GregorianCalendar gcOperationDay = new GregorianCalendar();
		String exceptionType;
	
		// for all SpecialOperationDays of VehicleJourey
		for (i = 0; i < listCalendar_OOL_start_date.size(); i++) {

			try {
				calendarDatesOperationDay = sdfIn.parse(((String)((ValueList)listCalendar_OOL_start_date.get(i)).getValue(0)));
				calendarDateOperationDayEnd = sdfIn.parse(((String)((ValueList)listCalendar_OOL_end_date.get(i)).getValue(0)));
				gcOperationDay.setTime(calendarDatesOperationDay);
				
				exceptionType = (String)((ValueList)listCalendar_OOL_exception_type.get(i)).getValue(0);
			
				while (calendarDatesOperationDay.compareTo(calendarDateOperationDayEnd) <= 0) {
					newCalendarDates__service_id = new ValueList(_key_calendar_no_dates_start[0]);
					listCalendarDates__service_id.add(newCalendarDates__service_id);
					newCalendarDates__service_id.addValue(serviceId);
					newCalendarDates__date = new ValueList(_key_calendar_no_dates_start[2]);
					listCalendarDates__date.add(newCalendarDates__date);
					newCalendarDates__date.addValue(TransxchangeDataAspect.formatDate(gcOperationDay.get(Calendar.YEAR), gcOperationDay.get(Calendar.MONTH) + 1, gcOperationDay.get(Calendar.DAY_OF_MONTH)));
					newCalendarDates__exception_type = new ValueList(_key_calendar_no_dates_start[2]);
					listCalendarDates__exception_type.add(newCalendarDates__exception_type);
//					newCalendarDates__exception_type.addValue(_key_calendar_no_dates_start[4]);
					newCalendarDates__exception_type.addValue(exceptionType);
					gcOperationDay.add(Calendar.DAY_OF_YEAR, 1);
					calendarDatesOperationDay = gcOperationDay.getTime();
				}
			} catch (Exception e) {
				handler.setParseError(e.getMessage()); 
			}
		}
	}
	
	public TransxchangeCalendarDates(TransxchangeHandlerEngine owner) {
		super(owner);
		listCalendarDates__service_id  = new ArrayList();
		listCalendarDates__date  = new ArrayList();
		listCalendarDates__exception_type  = new ArrayList();
		
		/*
		 * v1.6.3: Dynamically initialize bank holiday maps
		 */
		bankHolidays = new ArrayList();
		
	}
	
	/*
	 * v1.5: create all bank holidays
	 */
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

	/*
	 * v1.5: create a particular bank holiday
	 */
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