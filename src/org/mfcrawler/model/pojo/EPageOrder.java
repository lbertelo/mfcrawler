/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013-2014  lbertelo

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

package org.mfcrawler.model.pojo;

import org.mfcrawler.model.util.I18nUtil;

/**
 * Enumeration for the sort of displaying pages (and sites)
 * 
 * @author lbertelo
 */
public enum EPageOrder {

	NAME("model.ePageOrder.name"),
	TOTAL_SCORE("model.ePageOrder.totalScore"),
	AVG_SCORE("model.ePageOrder.avgScore"),
	DEEP("model.ePageOrder.deep"),
	CRAWLTIME("model.ePageOrder.crawlTime");

	/**
	 * The internationalized name of the sort
	 */
	private String display;

	/**
	 * Private constructor of the enumeration
	 * @param message the i18n message key
	 */
	private EPageOrder(String message) {
		display = I18nUtil.getMessage(message);
	}

	@Override
	public String toString() {
		return display;
	}
}
