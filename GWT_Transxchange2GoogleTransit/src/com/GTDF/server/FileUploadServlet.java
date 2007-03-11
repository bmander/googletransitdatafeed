/*
 * The Google Transit Data Feed project
 * 
 * GWT_TransXChange2GoogleTransit
 * 
 * File:    FileUploadServlet.java
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

public class FileUploadServlet extends HttpServlet{ 
           protected void doPost(HttpServletRequest request, 
               HttpServletResponse response) 
throws ServletException, IOException { 

   String rootDirectory = getServletConfig().getInitParameter("TRANSFORM_HOME");
   boolean writeToFile = true; 
   String returnOKMessage = "OK"; 


   boolean isMultipart = ServletFileUpload.isMultipartContent(request); 

   PrintWriter out = response.getWriter(); 
   		   
   // Create a factory for disk-based file items 
   if (isMultipart) { 

	   		// We are uploading a file (deletes are performed by on multipart requests) 
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
                           } else { 
                                   if (writeToFile) { 
                                           String fileName = item.getName(); 
                                           if (fileName != null && !fileName.equals("")) { 
                                                   fileName = FilenameUtils.getName(fileName); 
                                                   File uploadedFile = new File(rootDirectory + '/' + fileName); 
                                                   try { 
                                                           item.write(uploadedFile); 
                                                           out.print(returnOKMessage); 
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
                   File deleteFile = new File(rootDirectory+fileName); 
                   if(deleteFile.delete()){ 
                           out.print(returnOKMessage); 
                   } 
           } 
   } 
   
   } 

}

