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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility which does conversions
 * 
 * @author lbertelo
 */
public class ConversionUtils {

	/**
	 * Private constructor
	 */
	private ConversionUtils() {
	}

	/**
	 * Converts an object to a String (if the object is null, a default string
	 * is returned)
	 * @param object the object
	 * @return the string
	 */
	public static String toString(Object object) {
		if (object == null) {
			return I18nUtil.getMessage("general.undefined");
		} else {
			if (object instanceof Date) {
				return DateFormat.getDateTimeInstance().format((Date) object);
			} else {
				return object.toString();
			}
		}
	}

	/**
	 * Converts an object to a boolean (return false if the conversion fail)
	 * @param object the object
	 * @return the boolean
	 */
	public static boolean toBoolean(Object object) {
		if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue();
		} else if (object instanceof String) {
			return Boolean.parseBoolean((String) object);
		} else {
			return false;
		}
	}

	/**
	 * Converts an object to a Integer (return 0 if the conversion fail)
	 * @param object the object
	 * @return the integer
	 */
	public static Integer toInteger(Object object) {
		Integer value;
		try {
			value = Integer.valueOf(object.toString());
		} catch (NumberFormatException e) {
			value = 0;
		}
		return value;
	}

	/**
	 * Converts and object to a Double (return 0.0 if the conversion fail)
	 * @param object the object
	 * @return the double
	 */
	public static Double toDouble(Object object) {
		Double value;
		try {
			value = Double.valueOf(object.toString());
		} catch (NumberFormatException e) {
			value = 0.0;
		}
		return value;
	}

	/**
	 * Format a date to a string
	 * @param date the date
	 * @param format the date format
	 * @return the date formatted to a string
	 */
	public static String toFormattedDate(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

}
