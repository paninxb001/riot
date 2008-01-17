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
package org.riotfamily.media.model;

import org.riotfamily.media.model.data.SwfData;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotSwf extends RiotFile {
	
	public RiotSwf() {
		super();
	}

	public RiotSwf(SwfData data) {
		super(data);
	}

	public SwfData getSwfData() {
		return (SwfData) getFileData();
	}

	public RiotFile createCopy() {
		return new RiotSwf(getSwfData());
	}
	
	public boolean isValid() {
		return getSwfData().isValid();
	}
	
	public int getWidth() {
		return getSwfData().getWidth();
	}
	
	public int getHeight() {
		return getSwfData().getHeight();
	}

	public int getVersion() {
		return getSwfData().getVersion();
	}
	
}