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
package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheableRequestProcessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.4
 */
public class CacheableControllerProcessor implements CacheableRequestProcessor {

	private CacheableController controller;
	
	private CacheKeyProvider cacheKeyProvider;
	
	private ViewResolverHelper viewResolverHelper;
	
	
	public CacheableControllerProcessor(CacheableController controller, 
			CacheKeyProvider cacheKeyProvider,
			ViewResolverHelper viewResolverHelper) {

		this.controller = controller;
		this.cacheKeyProvider = cacheKeyProvider;
		this.viewResolverHelper = viewResolverHelper;
	}

	public String getCacheKey(HttpServletRequest request) {
		return cacheKeyProvider.getCacheKey(controller, request);
	}

	public long getLastModified(HttpServletRequest request) throws Exception {
		return controller.getLastModified(request);
	}

	public long getTimeToLive() {
		return controller.getTimeToLive();
	}

	public boolean responseShouldBeZipped(HttpServletRequest request) {
		if (controller instanceof Compressible) {
			return ((Compressible) controller).gzipResponse(request);
		}
		return false;
	}
	
	public void processRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		ModelAndView mv = controller.handleRequest(request, response);
		if (mv != null) {
	    	View view = viewResolverHelper.resolveView(request, mv);
	    	view.render(mv.getModel(), request, response);
	    }
	}
	
}
