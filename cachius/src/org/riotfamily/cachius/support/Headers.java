/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.support;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

/**
 * Backport of the new (6.5) cache to the 6.4 branch.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.4
 */
public class Headers implements Serializable {

	private SimpleDateFormat format = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	private ArrayList headers = new ArrayList();
	
	public Headers() {
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public void add(String name, String value) {
		getHeader(name).addValue(value);
	}
	
	public void addInt(String name, int value) {
		add(name, String.valueOf(value));
	}

	public void addDate(String name, long date) {
		add(name, format.format(new Date(date)));
	}
	
	public void set(String name, String value) {
		getHeader(name).setValue(value);
	}
	
	public void setInt(String name, int value) {
		set(name, String.valueOf(value));
	}

	public void setDate(String name, long date) {
		set(name, format.format(new Date(date)));
	}

	public void addToResponse(HttpServletResponse response) {
		Iterator it = headers.iterator();
		while (it.hasNext()) {
			Header header = (Header) it.next();
			header.addToResponse(response);
		}
	}
	
	private Header getHeader(String name) {
		Iterator it = headers.iterator();
		while (it.hasNext()) {
			Header header = (Header) it.next();
			if (header.getName().equals(name)) {
				return header;
			}
		}
		Header header = new Header(name);
		headers.add(header);
		return header;
	}
	
	private static class Header implements Serializable {
		
		private String name;
		
		private ArrayList values = new ArrayList();

		public Header(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setValue(String value) {
			values.clear();
			addValue(value);
		}
		
		public void addValue(String value) {
			values.add(value);
		}
		
		public void addToResponse(HttpServletResponse response) {
			Iterator it = values.iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				response.addHeader(name, value);	
			}
		}
		
	}
}
