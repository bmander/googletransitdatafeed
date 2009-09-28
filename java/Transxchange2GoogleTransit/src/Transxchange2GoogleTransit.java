/*
 * Copyright 2007, 2009 GoogleTransitDataFeed
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import transxchange2GoogleTransitHandler.*;

/*
 * Transxchange2GoogleTransit 
 * 	$ transxchange2GoogleTransit <transxchange input filename> <url> <timezone> <default route type> <output-directory> <stopfile>
 * 
 * <default route type>: 0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry, 5 - Cable car, 6 - Gondola, 7 - Funicular
 */
public class Transxchange2GoogleTransit {
	
	static boolean 										useAgencyShortname = false;

	public static void main(String[] args) {

		TransxchangeHandler handler = null;

		System.out.println();
        System.out.println("transxchange2GoogleTransit 1.6.4");
        System.out.println("Please refer to LICENSE file for licensing information");
        if ((args.length != 3 || args.length == 3 && !args[1].toLowerCase().equals("-c")))
        	if (args.length < 5 || args.length > 6) {
	        	System.out.println();
	        	System.out.println("Usage: $ transxchange2GoogleTransit <transxchange input filename> -c <configuration file name>");
	        	System.out.println();
	        	System.out.println("             -- OR --");
	        	System.out.println();
	        	System.out.println("Usage: $ transxchange2GoogleTransit <transxchange input filename> -");
	        	System.out.println("         <url> <timezone> <default route type> <output-directory> [<stopfile>]");
	        	System.out.println();
	        	System.out.println("         <timezone>: Please refer to ");
	        	System.out.println("             http://en.wikipedia.org/wiki/List_of_tz_zones");
	        	System.out.println("         <default route type>:");
	        	System.out.println("             0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry, 5 - Cable car, 6 - Gondola, 7 - Funicular");
	        	System.exit(1);
	        }
    
        // Parse transxchange input file and create initial Google Transit output files
        try {
        	
        	handler = new TransxchangeHandler();

        	// v1.6.4: Read configuration file
        	if (args.length == 3)
        		args = readConfigFile(args[0], args[2]);
        	if (args.length == 6)
        		handler.parse(args[0], args[1], args[2], args[3], "", args[4], args[5], useAgencyShortname);
        	else
        		handler.parse(args[0], args[1], args[2], args[3], "", args[4], "", useAgencyShortname);
		} catch (ParserConfigurationException e) {
        	System.out.println("transxchange2GoogleTransit ParserConfiguration parse error:");
        	System.out.println(e.getMessage());
        	System.exit(1);			
		}
		catch (SAXException e) {
			System.out.println("transxchange2GoogleTransit SAX parse error:");
			System.out.println(e.getMessage());
			System.out.println(e.getException());
			System.exit(1);						
		}
		catch (UnsupportedEncodingException e) { // v1.5: resource file ukstops.txt incorrect encoding
			System.out.println("transxchange2GoogleTransit Naptan stop file:");
			System.out.println(e.getMessage());
			System.exit(1);						
		}
 		catch (IOException e) {
			System.out.println("transxchange2GoogleTransit IO parse error:");
			System.out.println(e.getMessage());
			System.exit(1);						
		}

       // Create final Google Transit output files
        try {
        	handler.writeOutput("", args[4]);
        } catch (IOException e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
       
    	System.exit(0);
    }
	
	private static String[] readConfigFile(String inputFileName, String configFilename) 
		throws IOException
	
	{
		String[] result = {inputFileName, "", "", "", "", ""};
		useAgencyShortname = false;
		
		BufferedReader in = new BufferedReader(new FileReader(configFilename));
		String line;
		int tokenCount;
		String tagToken = "", configurationValue;
		while ((line = in.readLine()) != null) {
			tokenCount = 0;
			java.util.StringTokenizer st = new java.util.StringTokenizer(line, "=");
			while (st.hasMoreTokens() && tokenCount < 2) {
				if (tokenCount == 0)
					tagToken = st.nextToken().trim().toLowerCase();
				else {
					configurationValue = st.nextToken().trim();
					if (tagToken.equals("url"))
						result[1] = new String(configurationValue);
					if (tagToken.equals("timezone"))
						result[2] = new String(configurationValue);
					if (tagToken.equals("default-route-type"))
						result[3] = new String(configurationValue);
					if (tagToken.equals("output-directory"))
						result[4] = new String(configurationValue);
					if (tagToken.equals("stopfile"))
						result[5] = new String(configurationValue);
					if (tagToken.equals("useagencyshortname") && configurationValue != null && configurationValue.trim().toLowerCase().equals("true"))
						useAgencyShortname = true;
				}	
				tokenCount++;
			}
		}
		in.close();
			
		return result;
	}
}
