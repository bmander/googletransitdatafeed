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

package com.GTDF.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.GTDF.client.Transxchange2GoogleTransitService;

import java.util.StringTokenizer;

import org.apache.commons.io.FilenameUtils;

import transxchange2GoogleTransitHandler.TransxchangeHandler;

public class Transxchange2GoogleTransitServiceImpl extends RemoteServiceServlet implements Transxchange2GoogleTransitService {

	static final long serialVersionUID = 0;
	
	public String transxchange2GoogleTransit_transform(String inArgs) {
		
		String rootDirectory = getServletConfig().getInitParameter("TRANSFORM_HOME");
		String workDirectory = getServletConfig().getInitParameter("TRANSFORM_DIR");
		int maxargs = 5;
		String[] args = new String[maxargs];
		int argv = 0;
		String result = "";
		TransxchangeHandler handler = null;

        /*
		 * Parse input string to extract arguments (similar command line arguments)
		 */ 
		StringTokenizer st = new StringTokenizer(inArgs, " ");
		while (st.hasMoreTokens() && argv < maxargs) {
			args[argv] = st.nextToken();
			argv++;
		}
		if (argv < maxargs) // Don't let in if too few arguments
			return "Not enough arguments. Required: <url> <timezone> <default route type> <output-directory>";
	
		String outdir = "";
        if (argv == 5)
        	outdir = args[4];

		/*
         * Parse transxchange input file
         */ 
  	    String fileName = FilenameUtils.getName(args[0]); 
		
        try {
    		handler = new TransxchangeHandler();
        	handler.parse(rootDirectory + workDirectory + '/' + outdir + '/' + fileName, args[1], args[2], args[3], rootDirectory, workDirectory + '/' + outdir);
        } catch (Exception e) {
        	return e.getMessage();
        }
     
        /*
         * Create Google Transit output files
         */
        try {
        	result = handler.writeOutput(rootDirectory, workDirectory + '/' + outdir);
        } catch (Exception e) {
        	return e.getMessage();
        }
		
        /*
         * Success - create return message
         */
		String hostedOn = getServletConfig().getInitParameter("HOSTED_ON");
		return "Created Google Transit Data Feed Spec archive at: " + hostedOn + result;
	}
}
