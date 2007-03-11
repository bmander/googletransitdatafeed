/*
 * The Google Transit Data Feed project
 * 
 * GWT_TransXChange2GoogleTransit
 * 
 * File:    Transxchange2GoogleTransitServiceImpl.java
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
 */package com.GTDF.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.GTDF.client.Transxchange2GoogleTransitService;

import java.util.StringTokenizer;

import org.apache.commons.io.FilenameUtils;

import transxchange2GoogleTransitHandler.TransxchangeHandler;

public class Transxchange2GoogleTransitServiceImpl extends RemoteServiceServlet implements
Transxchange2GoogleTransitService {

	public String transxchange2GoogleTransit_transform(String s) {
		
		String rootDirectory = getServletConfig().getInitParameter("TRANSFORM_HOME");

		int maxargs = 5;
		String[] args = new String[maxargs];
		int argv = 0;
		
		/*
		 * Parse input string to extract arguments (similar command line arguments)
		 */ 
		StringTokenizer st = new StringTokenizer(s, " ");
		while (st.hasMoreTokens() && argv < maxargs) {
			args[argv] = st.nextToken();
			argv++;
		}
		s = "Success";
		
        /*
         * Parse transxchange input file
         */ 
  	    String fileName = FilenameUtils.getName(args[0]); 
		
		TransxchangeHandler handler = new TransxchangeHandler();
        try {
        	handler.parse(rootDirectory + '/' + fileName, args[1], args[2], args[3]);
        } catch (Exception e) {
        	return e.getMessage();
        }
     
        /*
         * Create Google Transit output files
         */
		String outdir = "";
        if (argv == 5)
        	outdir = args[4];
        try {
        	handler.writeOutput(rootDirectory + '/' + outdir);
        } catch (Exception e) {
        	return e.getMessage();
        }
		
		return s;
	}
}
