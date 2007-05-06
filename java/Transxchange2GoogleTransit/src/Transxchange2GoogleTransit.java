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

import transxchange2GoogleTransitHandler.*;

/*
 * Transxchange2GoogleTransit 
 * 	$ transxchange2GoogleTransit <transxchange input filename> <url> <timezone> <default route type> [<output-directory>]
 * 
 * <default route type>: 0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry, 5 - Cable car, 6 - Gondola, 7 - Funicular
 */
public class Transxchange2GoogleTransit {

	public static void main(String[] args) {

		System.out.println();
        System.out.println("transxchange2GoogleTransit 1.5");
        System.out.println("Please refer to LICENSE file for licensing information");
        if (args.length < 4 || args.length > 5) {
        	System.out.println();
        	System.out.println("Usage: $ transxchange2GoogleTransit <transxchange input filename> -");
        	System.out.println("         <url> <timezone> <default route type> [<output-directory>]");
        	System.out.println();
        	System.out.println("         <timezone>: Please refer to ");
        	System.out.println("             http://en.wikipedia.org/wiki/List_of_tz_zones");
        	System.out.println("         <default route type>:");
        	System.out.println("             0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry, 5 - Cable car, 6 - Gondola, 7 - Funicular");
        	System.exit(1);
        }
        
        // Parse transxchange input file
        TransxchangeHandler handler = new TransxchangeHandler();
        try {
        	handler.parse(args[0], args[1], args[2], args[3]);
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit parse error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
     
        // Create Google Transit output files
		String outdir = "";
        if (args.length == 5)
        	outdir = args[4];
        try {
        	handler.writeOutput(outdir, "");
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
       
    	System.exit(0);
    }
}
