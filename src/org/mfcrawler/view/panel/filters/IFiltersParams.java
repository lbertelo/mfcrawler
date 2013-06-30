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

package org.mfcrawler.view.panel.filters;

/**
 * Constants for filters (BlackListSubPanel and KeywordSubPanel)
 * 
 * @author lbertelo
 */
public interface IFiltersParams {

	static final int DEFAULT_KEYWORD_COLUMNS = 10;
	static final int KEYWORD_NUMBER_DEFAULT = 100;
	static final int KEYWORD_NUMBER_MIN = -100;
	static final int KEYWORD_NUMBER_MAX = 200;
	static final int KEYWORD_SPINNER_STEP = 1;

	static final int DEFAULT_SITE_COLUMNS = 30;
	static final int DEFAULT_WEIGHT_VALUE = 100;
}
