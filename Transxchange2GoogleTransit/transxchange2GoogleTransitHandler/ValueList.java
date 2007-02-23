/*
 * The Google Transit Data Feed project
 * 
 * TransXChange2GoogleTransit
 *
 * File:    ValueList.java
 * Version:	1.1
 * Date: 	22-Feb-2007
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

package transxchange2GoogleTransitHandler;

import java.util.*;

public class ValueList {
	  private String keyName;
	  private List values;

	  public void addValue(String addValue) {
		  values.add(addValue);
	  }

	  public Object getValue(int i) {
		  if (i < 0 || i >= values.size())
			  return null;
		  return values.get(i);
	  }

	  public void setValue(int i, Object value) {
		    if (i < 0 || i >= values.size())
		      return;
		    values.set(i, value);
		  }

	  public void dumpValues() {
		  Iterator i = values.iterator();
		  while (i.hasNext()) {
			  System.out.println(keyName + " " + i.next());
		  }
	  }

	  public String getKeyName() {
		  return keyName;
	  }

	  public List getValues() {
		  return values;
	  }
	  
	  public int size() {
		  return values.size();
	  }

	  public ValueList(String key) {
		  keyName = key;
		  values = new ArrayList();
	  }
}
