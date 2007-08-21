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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.common.web.filter.PluginChain;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.LoginManager;
import org.riotfamily.riot.security.auth.RiotUser;

/**
 * Servlet filter that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see AccessController
 */
public final class AccessControlFilterPlugin extends FilterPlugin {
	public static final int ORDER = 0;
	
	public int getOrder() {
		return ORDER;
	}

	public void setOrder(int order) {
		throw new UnsupportedOperationException();
	}

	public void doFilter(HttpServletRequest request,
		HttpServletResponse response, PluginChain pluginChain)
		throws IOException, ServletException {
		
		try {
			RiotUser user = LoginManager.getUser(request);
			AccessController.bindUserToCurrentThread(user);
			pluginChain.doFilter(request, response);
		}
		finally {
			AccessController.resetUser();
		}
	}
}