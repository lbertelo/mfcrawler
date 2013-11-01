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

package org.mfcrawler.model.pojo;

/**
 * Constants for Application Config
 * 
 * @author lbertelo
 */
public interface IAppConfigParams {

	/**
	 * Default value of last opened project
	 */
	static final String LAST_OPENED_PROJECT_DEFAULT = "default";

	/**
	 * Default value of user agent
	 */
	static final String USER_AGENT_DEFAULT = "MfCrawler";

	/**
	 * Default value of the cache size of dbms
	 */
	static final Integer CACHE_SIZE_OF_DBMS_DEFAULT = 128_000;

	/**
	 * Default value of page request timeout (in ms)
	 */
	static final Integer PAGE_REQUEST_TIMEOUT_DEFAULT = 8_000;

	/**
	 * Default value of robots.txt request timeout (in ms)
	 */
	static final Integer ROBOTS_REQUEST_TIMEOUT_DEFAULT = 4_000;

	/**
	 * Minimum value for request timeout
	 */
	static final int REQUEST_TIMEOUT_MIN = 0;

	/**
	 * Maximum value for request timeout
	 */
	static final int REQUEST_TIMEOUT_MAX = 20000;

	/**
	 * Step of the spinner for request timeout
	 */
	static final int REQUEST_TIMEOUT_SPINNER_STEP = 200;
	
	/**
	 * Minimum value for cache size of dbms
	 */
	static final int CACHE_SIZE_OF_DBMS_MIN = 32_000;

	/**
	 * Maximum value for cache size of dbms
	 */
	static final int CACHE_SIZE_OF_DBMS_MAX = 4_000_0000;

	/**
	 * Step of the spinner for cache size of dbms
	 */
	static final int CACHE_SIZE_OF_DBMS_STEP = 1_000;

	/**
	 * Default value of forbidden file extensions
	 */
	static final String FORBIDDEN_FILE_EXTENSIONS = ".apk|.avi|.exe|.flv|.gif|.gz|.jpeg|.jpg|.mkv|.mp3|.mp4|.png|.pdf|.rar|.swf|.zip";

	/**
	 * Default value of the use of a proxy
	 */
	static final Boolean PROXY_USE_DEFAULT = false;

	/**
	 * Default value of proxy host
	 */
	static final String PROXY_HOST_DEFAULT = "proxy.domain.fr";

	/**
	 * Default value of proxy port
	 */
	static final Integer PROXY_PORT_DEFAULT = 3128;
}
