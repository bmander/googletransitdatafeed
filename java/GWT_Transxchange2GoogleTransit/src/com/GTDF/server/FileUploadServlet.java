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

import java.io.*; 
import java.util.Iterator; 
import java.util.List; 

import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 


import org.apache.commons.fileupload.FileItem; 
import org.apache.commons.fileupload.FileItemFactory; 
import org.apache.commons.fileupload.FileUploadException; 
import org.apache.commons.fileupload.disk.DiskFileItemFactory; 
import org.apache.commons.fileupload.servlet.ServletFileUpload; 
import org.apache.commons.io.FilenameUtils;

import com.GTDF.server.WikiUserImpl;

public class FileUploadServlet extends HttpServlet{ 

	static final long serialVersionUID = 0;
	
protected void doPost(HttpServletRequest request, 
    HttpServletResponse response) 
throws ServletException, IOException { 


    String rootDirectory = getServletConfig().getInitParameter("TRANSFORM_HOME");
	String workDirectory = getServletConfig().getInitParameter("TRANSFORM_DIR");
	String prefixDirectory = "";
    boolean writeToFile = true; 
    String returnOKMessage = "OK"; 
    String username = "";

    boolean isMultipart = ServletFileUpload.isMultipartContent(request); 

    PrintWriter out = response.getWriter(); 

    // Create a factory for disk-based file items 
    if (isMultipart) { 

    	// We are uploading a file (deletes are performed by non multipart requests) 
           FileItemFactory factory = new DiskFileItemFactory(); 

           // Create a new file upload handler 
           ServletFileUpload upload = new ServletFileUpload(factory); 
           // Parse the request 
           try { 

         	   List items = upload.parseRequest(request); 

                   // Process the uploaded items 
                   Iterator iter = items.iterator(); 
                   while (iter.hasNext()) { 
                 	   FileItem item = (FileItem) iter.next(); 
                           if (item.isFormField()) {
                        	   
                        	   // Hidden field containing username - check authorization
                        	   if (item.getFieldName().equals("username")) {
                        		   username = item.getString();
                        		   String wikiDb = getServletConfig().getInitParameter("WIKIDB");
                        		   String wikiDbUser = getServletConfig().getInitParameter("WIKIDB_USER");
                        		   String wikiDbPassword = getServletConfig().getInitParameter("WIKIDB_PASSWORD");
                        		   WikiUserImpl wikiUser = new WikiUserImpl();
                        		   String authResult = wikiUser.wikiUserVerifyDb(username, wikiDb, wikiDbUser, wikiDbPassword);
                        		   if (authResult != "LOGGED") {
                      		      		out.print(authResult);
                        		      	return;
                        		   } else
                        		        new File(rootDirectory + workDirectory + '/' + username).mkdirs();
                        	   }

                        	   // Hidden field containing file prefix to create subdirectory
                        	   if (item.getFieldName().equals("prefix")) {
                        		   prefixDirectory = item.getString();
                        		   new File(rootDirectory + workDirectory + '/' + username + '/' + prefixDirectory).mkdirs();
                        		   prefixDirectory += '/';
                        	   }                           
                           } else { 
                                   if (writeToFile) { 
                                           String fileName = item.getName(); 
                                           if (fileName != null && !fileName.equals("")) { 
                                                   fileName = FilenameUtils.getName(fileName); 
                                                   File uploadedFile = new File(rootDirectory + workDirectory + '/' + username + '/'+ prefixDirectory + fileName); 
                                                   try { 
                                                           item.write(uploadedFile); 
                                                           String hostedOn = getServletConfig().getInitParameter("HOSTED_ON");
                                                           out.print("Infile at: " + hostedOn + workDirectory + '/' + username + '/'+ prefixDirectory + fileName); 
                                                   } catch (Exception e) { 
                                                           e.printStackTrace(); 
                                                   } 
                                           } 
                                   } else { 
                                   } 
                           } 
                   } 
           } catch (FileUploadException e) { 
                   e.printStackTrace(); 
           } 
   } else { 
	      
           //Process a request to delete a file 
           String[] paramValues = request.getParameterValues("uploadFormElement"); 
           for (int i=0;i<paramValues.length;i++){ 
                   String fileName = FilenameUtils.getName(paramValues[i]); 
                   File deleteFile = new File(rootDirectory + workDirectory + fileName); 
                   if(deleteFile.delete()){ 
                           out.print(returnOKMessage); 
                   } 
           } 
   } 
   
   } 

}

