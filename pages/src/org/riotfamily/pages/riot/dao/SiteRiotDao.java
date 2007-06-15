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
package org.riotfamily.pages.riot.dao;

import java.util.Collection;

import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.support.RiotDaoAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SiteRiotDao extends RiotDaoAdapter implements InitializingBean {

	private PageDao pageDao;

	public SiteRiotDao() {
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(pageDao, "A PageDao must be set.");
	}

	public Class getEntityClass() {
		return Site.class;
	}

	public Collection list(Object parent, ListParams params) throws DataAccessException {
		return pageDao.listSites();
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		pageDao.saveSite((Site) entity);
	}

	public void update(Object entity) throws DataAccessException {
		pageDao.updateSite((Site) entity);
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		pageDao.deleteSite((Site) entity);
	}

	public String getObjectId(Object entity) {
		return ((Site) entity).getId().toString();
	}

	public Object load(String id) throws DataAccessException {
		return pageDao.loadSite(new Long(id));
	}

}
