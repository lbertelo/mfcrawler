/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013  lbertelo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mfcrawler.model.util;

import java.util.ResourceBundle;

/**
 * Utility for Internalization
 * 
 * @author lbertelo
 */
public final class I18nUtil {

	/**
	 * Messages Bundle path
	 */
	private static final String MESSAGES_PATH = "org.mfcrawler.resources.MessagesBundle";
	
	/**
	 * Resource bundle
	 */
	private static final ResourceBundle messages = ResourceBundle.getBundle(MESSAGES_PATH);
	
	/**
	 * Private constructor
	 */
	private I18nUtil() {
	}
	
	/**
	 * Gets a string for a key from the resource bundle
	 * @param key the key
	 * @return the string for the given key
	 */
	public static String getMessage(String key) {
		return messages.getString(key);
	}
	
}
