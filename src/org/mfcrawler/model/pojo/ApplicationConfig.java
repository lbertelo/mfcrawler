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
 * Describes the configuration of the application
 * 
 * @author lbertelo
 */
public class ApplicationConfig implements IAppConfigParams {

	/**
	 * Name of the last opened project
	 */
	private String lastOpenedProject;

	/**
	 * User agent of the crawler
	 */
	private String userAgent;

	/**
	 * Cache size of Dbms (in ko)
	 */
	private Integer cacheSizeOfDbms;

	/**
	 * Http connection timeout for a page request (in ms)
	 */
	private Integer pageRequestTimeout;

	/**
	 * Http connection timeout for a robots.txt request (in ms)
	 */
	private Integer robotsRequestTimeout;

	/**
	 * Forbidden filename extensions (separated by '|')
	 */
	private String forbiddenFileExtensions;

	/**
	 * Indicates if the crawler must use a proxy
	 */
	private Boolean proxyUse;

	/**
	 * The host of the proxy (IP or name)
	 */
	private String proxyHost;

	/**
	 * The port used by the proxy
	 */
	private Integer proxyPort;

	/**
	 * Default constructor which initialize attributes with their default values
	 */
	public ApplicationConfig() {
		lastOpenedProject = LAST_OPENED_PROJECT_DEFAULT;
		userAgent = USER_AGENT_DEFAULT;
		cacheSizeOfDbms = CACHE_SIZE_OF_DBMS_DEFAULT;
		pageRequestTimeout = PAGE_REQUEST_TIMEOUT_DEFAULT;
		robotsRequestTimeout = ROBOTS_REQUEST_TIMEOUT_DEFAULT;
		forbiddenFileExtensions = FORBIDDEN_FILE_EXTENSIONS;

		proxyUse = PROXY_USE_DEFAULT;
		proxyHost = PROXY_HOST_DEFAULT;
		proxyPort = PROXY_PORT_DEFAULT;
	}

	/**
	 * Getter of the last opened project
	 * @return the last opened project
	 */
	public String getLastOpenedProject() {
		return lastOpenedProject;
	}

	/**
	 * Setter of the last opened project
	 * @param lastOpenedProject the last opened project
	 */
	public void setLastOpenedProject(String lastOpenedProject) {
		this.lastOpenedProject = lastOpenedProject;
	}

	/**
	 * Getter of the user agent
	 * @return the user agent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Setter of the user agent
	 * @param userAgent the user agent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * Getter of the cache size of Dbms
	 * @return the cache size of Dbms
	 */
	public Integer getCacheSizeOfDbms() {
		return cacheSizeOfDbms;
	}

	/**
	 * Setter of the cache size of Dbms
	 * @param cacheSizeOfDbms the cache size of Dbms
	 */
	public void setCacheSizeOfDbms(Integer cacheSizeOfDbms) {
		this.cacheSizeOfDbms = cacheSizeOfDbms;
	}

	/**
	 * Getter of the page request timeout
	 * @return the page request timeout
	 */
	public Integer getPageRequestTimeout() {
		return pageRequestTimeout;
	}

	/**
	 * Setter of the page request timeout
	 * @param pageRequestTimeout the page request timeout
	 */
	public void setPageRequestTimeout(Integer pageRequestTimeout) {
		this.pageRequestTimeout = pageRequestTimeout;
	}

	/**
	 * Getter of the robots.txt request timeout
	 * @return the robots.txt request timeout
	 */
	public Integer getRobotsRequestTimeout() {
		return robotsRequestTimeout;
	}

	/**
	 * Setter of the robots.txt request timeout
	 * @param robotsRequestTimeout the robots.txt request timeout
	 */
	public void setRobotsRequestTimeout(Integer robotsRequestTimeout) {
		this.robotsRequestTimeout = robotsRequestTimeout;
	}

	/**
	 * Getter of the forbidden file extensions
	 * @return the forbidden file extensions
	 */
	public String getForbiddenFileExtensions() {
		return forbiddenFileExtensions;
	}

	/**
	 * Setter of the forbidden file extensions
	 * @param forbiddenFileExtensions the forbidden file extensions
	 */
	public void setForbiddenFileExtensions(String forbiddenFileExtensions) {
		this.forbiddenFileExtensions = forbiddenFileExtensions;
	}

	/**
	 * Getter of the proxy use
	 * @return the proxy use
	 */
	public Boolean getProxyUse() {
		return proxyUse;
	}

	/**
	 * Setter of the proxy use
	 * @param proxyUse the proxy use
	 */
	public void setProxyUse(Boolean proxyUse) {
		this.proxyUse = proxyUse;
	}

	/**
	 * Getter of the proxy host
	 * @return the proxy host
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * Setter of the proxy host
	 * @param proxyHost the proxy host
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * Getter of the proxy port
	 * @return the proxy port
	 */
	public Integer getProxyPort() {
		return proxyPort;
	}

	/**
	 * Setter of the proxy port
	 * @param proxyPort the proxy port
	 */
	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * Refresh the proxy settings (with the system)
	 */
	public void refreshProxy() {
		if (proxyUse) {
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort.toString());
		} else {
			System.getProperties().remove("http.proxyHost");
			System.getProperties().remove("http.proxyPort");
		}
	}
}
