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

import java.lang.Class;
import java.sql.*;
import java.util.Calendar;

import com.GTDF.client.WikiUserService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WikiUserImpl extends RemoteServiceServlet implements WikiUserService {
	
	static final long serialVersionUID = 0;

	public String wikiUserVerify(String username) {	

		String wikiDb = getServletConfig().getInitParameter("WIKIDB");
		String wikiDbUser = getServletConfig().getInitParameter("WIKIDB_USER");
		String wikiDbPassword = getServletConfig().getInitParameter("WIKIDB_PASSWORD");
		String wikiNoAuth = getServletConfig().getInitParameter("NOAUTH");

		return wikiUserVerifyDb(username, wikiDb, wikiDbUser, wikiDbPassword, wikiNoAuth);
	}
	
	public String wikiUserVerifyDb(String username, String wikiDb, String wikiDbUser, String wikiDbPassword, String wikiNoAuth) {	
		Connection con = null;
		boolean found = false;
		boolean userFound = false;
		String today, yesterday, tomorrow;

		// Do not allow WikiSysop through. User name too easy to guess
		if (username.equals("WikiSysop"))
			return ("User WikiSysop not authorized to perform non-wiki operations");
		
		// v1.5: If no authorization, return LOGGED
		if (wikiNoAuth.equals("YES"))
		   return "LOGGED";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(wikiDb, wikiDbUser, wikiDbPassword);
			if(con.isClosed())
				return "Server Error: Database connection not open";
		    
			// Query database
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT user_name, user_touched FROM user");
			String user = "";
			String touched = "";

			// Get today's date and convert to mediawiki format
			Calendar cal = Calendar.getInstance();
			today = this.cal2WikiDate(cal);
			cal.add(Calendar.DATE, -1);
			yesterday = this.cal2WikiDate(cal);
			cal.add(Calendar.DATE, 2);
			tomorrow = this.cal2WikiDate(cal);
			
			// Run through wiki users
			while (rs.next() && !found) {
				user = rs.getString(1);
				touched = rs.getString(2).substring(0, 8); // get date of last user login
				if (user.equals(username))
					userFound = true;
				if (user.equals(username) && (touched.equals(today) || touched.equals(yesterday) || touched.equals(tomorrow))) // logged in between yesterday and tomorrow (let's be a little generous)?
					found = true;
				
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		
		if (!userFound)
			return "User " + username + " not found";
		if (!found)
			return "User " + username + " not logged in"; // today " + today;
		
		return "LOGGED";
	}
	
	private String cal2WikiDate(Calendar calendar) {
		String addS;

		Integer yyy = new Integer(calendar.get(Calendar.YEAR));
		String date = yyy.toString();
		Integer mmm = new Integer(calendar.get(Calendar.MONTH) + 1);
		addS = mmm.toString();
		if (addS.length() < 2)
			addS = '0' + addS;
		date += addS;
		Integer ddd = new Integer(calendar.get(Calendar.DAY_OF_MONTH));
		addS = ddd.toString();
		if (addS.length() < 2)
			addS = '0' + addS;
		date += addS;

		return date;
	}
}
