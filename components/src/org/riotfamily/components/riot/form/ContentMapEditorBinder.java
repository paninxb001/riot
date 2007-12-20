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
package org.riotfamily.components.riot.form;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.service.ContentFactory;
import org.riotfamily.forms.AbstractEditorBinder;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentMapEditorBinder extends AbstractEditorBinder {

	private ContentFactory contentFactory;
	
	private ContentMap contentMap;
	
	public ContentMapEditorBinder(ContentFactory contentFactory) {
		this.contentFactory = contentFactory;
		this.contentMap = new ContentMap();
	}
	
	public boolean isEditingExistingBean() {
		return contentMap.getId() != null;
	}

	public void setBackingObject(Object backingObject) {
		if (backingObject == null) {
			contentMap.clear();
		}
		else {
			Assert.isInstanceOf(ContentMap.class, backingObject);
			contentMap = (ContentMap) backingObject;
		}
	}
	
	public Object getBackingObject() {
		return contentMap;
	}

	public Class getBeanClass() {
		return ContentMap.class;
	}
	
	public Class getPropertyType(String path) {
		return Object.class;
	}

	public Object getPropertyValue(String property) {
		if (getEditor(property) instanceof ContentEditor) {
			return contentMap.get(property);
		}
		return contentMap.getUwrapped(property);
	}

	public void setPropertyValue(String property, Object value) {
		if (value == null) {
			contentMap.remove(property);
		}
		else if (value instanceof Content) {
			contentMap.put(property, (Content) value);
		}
		else {
			Content content = contentMap.get(property);
			if (content == null) {
				content = contentFactory.createContent(value);
				contentMap.put(property, content);
			}
			else {
				content.setValue(value);
			}
		}
	}

}
