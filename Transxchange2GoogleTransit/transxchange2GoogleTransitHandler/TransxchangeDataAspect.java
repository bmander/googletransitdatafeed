/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * File:    TransxchangeDataAspect.java 
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;



/*
 * Abstract superclass to cover transxchange data aspects (subclasses: TransxchangeAgency, TransxchangeStops etc.
 */ 

public abstract class TransxchangeDataAspect {

	String key = ""; // general key
	String keyNested = ""; // nested key
	String niceString = ""; // parsed string
	boolean activeStartElement = false; // control flag to skip characters outside start/endElement()
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		niceString = "";
	}
	
	public void endElement (String uri, String name, String qName) {
	}

	public void clearKeys (String qName) {
	}	

	public void characters (char ch[], int start, int length) {
		if (key.length() > 0) {
			for (int i = start; i < start + length; i++)
					niceString = niceString + ch[i];
		}		
	}
	
	public void endDocument() {
	}

	public void completeData() {
	}	

	public void dumpValues() {
	}

	
	/*
	 * Read time in transxchange specific format
	 */
/* Java 1.5	static void readTransxchangeTime(Integer[] timehhmmss, String inString) {
*/static void readTransxchangeTime(int[] timehhmmss, String inString) {
		StringTokenizer st = new StringTokenizer(inString, ":");
		int i = 0;
		while (st.hasMoreTokens() && i < 3) {
			timehhmmss[i] = Integer.parseInt(st.nextToken());
			i ++;
		}
	}

	/*
	 * Read frequency in transxchange specific format
	 * 
	 * CAUTION: Only supports minutes at this point
	 * 
	 */
	static int readTransxchangeFrequency(String inString) {
		inString = inString.substring(2, inString.length());
		StringTokenizer st = new StringTokenizer(inString, "M");
		int freq = 0;
		int i = 0;
		while (st.hasMoreTokens() && i < 1) {
			freq = Integer.parseInt(st.nextToken());
		}
		return freq;
	}

	/*
	 * Read date in transxchange specific format
	 */
	static String readTransxchangeDate(String inString) {
		StringTokenizer st = new StringTokenizer(inString, "-");
		String ret = "";
		int i = 0;
		while (st.hasMoreTokens() && i < 2) {
			ret = ret + st.nextToken();
		}
		return ret;
	}
	
	/*
	 * CSV-"proof" field
	 */
	static void csvProofList(List values) {
		int i, j;
		String s;
		ValueList iterator;
		
		for (i = 0; i < values.size(); i++) {
		    iterator = (ValueList)values.get(i);
		    for (j = 0; j < iterator.size(); j++) {
		    	s = (String)iterator.getValue(j);
		    	if (s.lastIndexOf(",") != -1 || s.lastIndexOf("\"") != -1)
		    		s = "\"" + s + "\"";
		    	iterator.setValue(j, s);
		    }
		}
	}

	/*
	 * Return date in Google Transit Data Feed format
	 * introduced to support Java 1.4.2
	 */
	static String formatDate(int year, int month, int day_of_month) {
		String result = "";
		String digis = "";
		Integer iYear;
		Integer iMonth;
		Integer iDay_of_month;
		
		iYear = new Integer(year);
		result = iYear.toString();

		iMonth = new Integer(month);
		digis = iMonth.toString();
		if (digis.length() == 1)
			digis = "0" + digis;
		result = result + digis;

		iDay_of_month = new Integer(day_of_month);
		digis = iDay_of_month.toString();
		if (digis.length() == 1)
			digis = "0" + digis;
		result = result + digis;
		
		return result;
	}

	/*
	 * Return time in Google Transit Data Feed format
	 * introduced to support Java 1.4.2
	 */
	static String formatTime(int hour, int mins) {
		String result = "";
		String digis = "";
		Integer iHour;
		Integer iMins;

		iHour = new Integer(hour);
		digis = iHour.toString();
		if (digis.length() == 1)
			digis = "0" + digis;
		result = result + digis;
		
		result = result + ":";
		
		iMins = new Integer(mins);
		digis = iMins.toString();
		if (digis.length() == 1)
			digis = "0" + digis;
		result = result + digis;
		
		result = result + ":00";
		
		return result;
	}

}
