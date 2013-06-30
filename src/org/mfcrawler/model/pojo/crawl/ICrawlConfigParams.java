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

package org.mfcrawler.model.pojo.crawl;

/**
 * Constants for Crawl Config
 * 
 * @author lbertelo
 */
public interface ICrawlConfigParams {

	/**
	 * Spinner step for crawl config
	 */
	static final int SPINNER_STEP = 1;

	/**
	 * Default value for thread number
	 */
	static final int THREAD_NUMBER_DEFAULT = 5;
	
	/**
	 * Minimum value for thread number
	 */
	static final int THREAD_NUMBER_MIN = 1;
	
	/**
	 * Maximum value for thread number
	 */
	static final int THREAD_NUMBER_MAX = 50;

	/**
	 * Default value for inner deep
	 */
	static final int INNER_DEEP_DEFAULT = 5;
	
	/**
	 * Minimum value for inner deep
	 */
	static final int INNER_DEEP_MIN = -1;
	
	/**
	 * Maximum value for inner deep
	 */
	static final int INNER_DEEP_MAX = 1000;
	
	/**
	 * Default value for outer deep
	 */
	static final int OUTER_DEEP_DEFAULT = 3;
	
	/**
	 * Minimum value for outer deep
	 */
	static final int OUTER_DEEP_MIN = -1;
	
	/**
	 * Maximum value for outer deep
	 */
	static final int OUTER_DEEP_MAX = 10000;

	/**
	 * Default value for remaining crawl number
	 */
	static final int REMAINING_CRAWL_NUMBER_DEFAULT = 100;
	
	/**
	 * Minimum value for remaining crawl number
	 */
	static final int REMAINING_CRAWL_NUMBER_MIN = -1;
	
	/**
	 * Maximum value for remaining crawl number
	 */
	static final int REMAINING_CRAWL_NUMBER_MAX = 1000000;

	/**
	 * Default value for minimum score
	 */
	static final int MINIMUM_SCORE_DEFAULT = 0;
	
	/**
	 * Minimum value for minimum score
	 */
	static final int MINIMUM_SCORE_MIN = -100;
	
	/**
	 * Maximum value for minimum score
	 */
	static final int MINIMUM_SCORE_MAX = 100;

	/**
	 * Default value for crawl delay
	 */
	static final int CRAWL_DELAY_DEFAULT = 500;
	
	/**
	 * Minimum value for crawl delay
	 */
	static final int CRAWL_DELAY_MIN = 0;
	
	/**
	 * Maximum value for crawl delay
	 */
	static final int CRAWL_DELAY_MAX = 60000;
	
	/**
	 * Default value for force crawl
	 */
	static final boolean FORCE_CRAWL_DEFAULT = false;
	
	/**
	 * Default value for force crawl delay
	 */
	static final int FORCE_CRAWL_DELAY_DEFAULT = 5000;
	
	/**
	 * Minimum value for force crawl delay 
	 */
	static final int FORCE_CRAWL_DELAY_MIN = 0;
	
	/**
	 * Maximum value for force crawl delay
	 */
	static final int FORCE_CRAWL_DELAY_MAX = 60000;

}
