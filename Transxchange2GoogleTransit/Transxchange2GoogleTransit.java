
/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 * 
 * File:    Transxchange2GoogleTransit.java
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

import transxchange2GoogleTransitHandler.*;

public class Transxchange2GoogleTransit {
	/**
	 * Transxchange2GoogleTransit 
	 * 	$ transxchange2GoogleTransit <transxchange input filename> <url> <timezone> <default route type> [<output-directory>]
	 * 
	 * <default route type>: 0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry (global default value)
	 */

	public static void main(String[] args) {

		String outdir = "";
		
		System.out.println();
        System.out.println("transxchange2GoogleTransit 1.2");
        System.out.println("Please refer to License directory for licensing information");
        if (args.length < 4 || args.length > 5) {
        	System.out.println();
        	System.out.println("Usage: $ transxchange2GoogleTransit <transxchange input filename> -");
        	System.out.println("         <url> <timezone> <default route type> [<output-directory>]");
        	System.out.println();
        	System.out.println("         <timezone>: Please refer to ");
        	System.out.println("             http://en.wikipedia.org/wiki/List_of_tz_zones");
        	System.out.println("         <default route type>:");
        	System.out.println("             0 - Tram, 1 - Subway, 2 - Rail, 3 - Bus, 4 - Ferry");
        	System.exit(1);
        }
        
        if (args.length == 5)
        	outdir = args[4];
 
        /*
         * Parse transxchange input file
         */ 
        TransxchangeHandler handler = new TransxchangeHandler();
        handler.setUrl(args[1]);
        handler.setTimezone(args[2]);
        handler.setDefaultRouteType(args[3]);
        try {
        	handler.parse(args[0]);
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit parse error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
     
        /*
         * Create Google Transit output files
         */
        try {
        	handler.writeOutput(outdir);
        } catch (Exception e) {
        	System.out.println("transxchange2GoogleTransit write error:");
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
       
    	System.exit(0);
    }
}
