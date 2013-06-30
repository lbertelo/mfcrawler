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

package org.mfcrawler.model.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONValue;
import org.mfcrawler.model.pojo.ApplicationConfig;

/**
 * Allows to load the application configuration
 * 
 * @author lbertelo
 */
public class LoadApplicationConfig {

	/**
	 * Filename of the application configuration
	 */
	private static final String APPLICATION_CONFIG_FILENAME = "applicationConfig.json";

	/**
	 * Last opened project key in json configuration file
	 */
	private static final String AC_LAST_PROJECT = "lastOpenedProject";

	/**
	 * User agent key in json configuration file
	 */
	private static final String AC_USER_AGENT = "userAgent";

	/**
	 * Page request timeout key in json configuration file
	 */
	private static final String AC_PAGE_TIMEOUT = "pageRequestTimeout";

	/**
	 * Robots.txt request timeout key in json configuration file
	 */
	private static final String AC_ROBOTS_TIMEOUT = "robotsRequestTimeout";

	/**
	 * Forbidden file extensions key in json configuration file
	 */
	private static final String AC_FORBIDDEN_EXTENSIONS = "forbiddenFileExtensions";

	/**
	 * Proxy use key in json configuration file
	 */
	private static final String AC_PROXY_USE = "proxyUse";

	/**
	 * Proxy host key in json configuration file
	 */
	private static final String AC_PROXY_HOST = "proxyHost";

	/**
	 * Proxy port key in json configuration file
	 */
	private static final String AC_PROXY_PORT = "proxyPort";

	/**
	 * Load the application configuration
	 * @return the application configuration
	 */
	@SuppressWarnings("unchecked")
	public static ApplicationConfig loadApplicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();

		File applicationConfigFile = new File(APPLICATION_CONFIG_FILENAME);
		if (applicationConfigFile.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(applicationConfigFile));
				Map<String, Object> jsonMap = (Map<String, Object>) JSONValue.parse(bufferedReader);

				applicationConfig.setLastOpenedProject((String) jsonMap.get(AC_LAST_PROJECT));
				applicationConfig.setUserAgent((String) jsonMap.get(AC_USER_AGENT));
				applicationConfig.setPageRequestTimeout(((Long) jsonMap.get(AC_PAGE_TIMEOUT)).intValue());
				applicationConfig.setRobotsRequestTimeout(((Long) jsonMap.get(AC_ROBOTS_TIMEOUT)).intValue());
				applicationConfig.setForbiddenFileExtensions((String) jsonMap.get(AC_FORBIDDEN_EXTENSIONS));
				applicationConfig.setProxyUse((Boolean) jsonMap.get(AC_PROXY_USE));
				applicationConfig.setProxyHost((String) jsonMap.get(AC_PROXY_HOST));
				applicationConfig.setProxyPort(((Number) jsonMap.get(AC_PROXY_PORT)).intValue());
			} catch (Exception e) {
				Logger.getLogger(LoadApplicationConfig.class.getName()).log(Level.SEVERE, "Error to read config", e);
			}
		} else {
			saveApplicationConfig(applicationConfig);
		}
		return applicationConfig;
	}

	/**
	 * Save the application configuration
	 * @param applicationConfig the application configuration
	 */
	public static void saveApplicationConfig(ApplicationConfig applicationConfig) {
		try {
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put(AC_LAST_PROJECT, applicationConfig.getLastOpenedProject());
			jsonMap.put(AC_USER_AGENT, applicationConfig.getUserAgent());
			jsonMap.put(AC_PAGE_TIMEOUT, applicationConfig.getPageRequestTimeout());
			jsonMap.put(AC_ROBOTS_TIMEOUT, applicationConfig.getRobotsRequestTimeout());
			jsonMap.put(AC_FORBIDDEN_EXTENSIONS, applicationConfig.getForbiddenFileExtensions());
			jsonMap.put(AC_PROXY_USE, applicationConfig.getProxyUse());
			jsonMap.put(AC_PROXY_HOST, applicationConfig.getProxyHost());
			jsonMap.put(AC_PROXY_PORT, applicationConfig.getProxyPort());

			File applicationConfigFile = new File(APPLICATION_CONFIG_FILENAME);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(applicationConfigFile));
			bufferedWriter.write(JSONValue.toJSONString(jsonMap));
			bufferedWriter.close();
		} catch (Exception e) {
			Logger.getLogger(LoadApplicationConfig.class.getName()).log(Level.SEVERE, "Error to write config", e);
		}
	}

}
