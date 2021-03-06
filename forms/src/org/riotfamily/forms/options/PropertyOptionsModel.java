/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PropertyOptionsModel implements OptionsModel {

	private String propertyName;

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Collection<?> getOptionValues(Element element) {
		Assert.notNull(propertyName, "A propertyName must be set.");
		Form form = element.getForm();
		if (form.isNew()) {
			return null;	
		}
		Object value = PropertyUtils.getProperty(form.getBackingObject(), 
				propertyName);
		
		if (value instanceof Collection) {
			return (Collection<?>) value;
		}
		return CollectionUtils.arrayToList(value);
	}
}
