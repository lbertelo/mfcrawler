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

package org.mfcrawler.model.process.extraction;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.pojo.site.RobotPath;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.util.ConversionUtils;

/**
 * Utility which extracts robots.txt information
 * 
 * @author lbertelo
 */
public class RobotExtractionUtil {

	/**
	 * Inner class which contains user agent information
	 */
	private static class UserAgentInfo {
		/**
		 * User agent of the crawler (get from application config)
		 */
		public final String myUserAgent = ApplicationModel.getConfig().getUserAgent();;
		/**
		 * Indicates if my User Agent is or was my current user agent
		 */
		public boolean myUserAgentSelected = false;
		/**
		 * My current user agent while reading content of robots.txt
		 */
		public String currentUserAgent = null;
	}

	/**
	 * Regex of a line in robots.txt
	 */
	private static final String LINE_REGEX = "\\s*([^\\s:#]+)\\s*:\\s*(\\S+)\\s*";

	/**
	 * Disallow key in robots.txt
	 */
	private static final String DISALLOW_NAME = "Disallow";

	/**
	 * Allow key in robots.txt
	 */
	private static final String ALLOW_NAME = "Allow";

	/**
	 * User agent key in robots.txt
	 */
	private static final String USER_AGENT_NAME = "User-agent";

	/**
	 * Crawl delay key in robots.txt
	 */
	private static final String CRAWL_DELAY_NAME = "Crawl-delay";

	/**
	 * Value for representing all user agent
	 */
	private static final String USER_AGENT_ALL = "*";

	/**
	 * Extracts robots.txt information from the content of the file
	 * @param site the site modified
	 * @param robotFileContent the content of the robots.txt
	 */
	public static void extraction(Site site, String robotFileContent) {
		if (robotFileContent != null) {
			final UserAgentInfo userAgentInfo = new UserAgentInfo();

			Pattern pattern = Pattern.compile(LINE_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(robotFileContent);

			while (matcher.find()) {
				int groupCount = matcher.groupCount();
				if (groupCount == 2) {
					String lineName = matcher.group(1);
					String lineValue = matcher.group(2);

					handleLine(site, lineName, lineValue, userAgentInfo);
				}
			}
		}
	}

	/**
	 * Handles a line in robots.txt and extracts information
	 * @param site the site modified
	 * @param lineName the name of the line
	 * @param lineValue the value of the line
	 * @param userAgentInfo user agent information
	 */
	private static void handleLine(Site site, String lineName, String lineValue, UserAgentInfo userAgentInfo) {
		if (lineName.equalsIgnoreCase(USER_AGENT_NAME)) {
			if (lineValue.equalsIgnoreCase(userAgentInfo.myUserAgent)) {
				userAgentInfo.myUserAgentSelected = true;
				userAgentInfo.currentUserAgent = lineValue;
			} else if (!userAgentInfo.myUserAgentSelected && lineValue.equalsIgnoreCase(USER_AGENT_ALL)) {
				userAgentInfo.currentUserAgent = lineValue;
			} else {
				userAgentInfo.currentUserAgent = null;
			}

			if (userAgentInfo.currentUserAgent != null) {
				site.setRobotPathList(new ArrayList<RobotPath>());
			}

		} else if (lineName.equalsIgnoreCase(CRAWL_DELAY_NAME)) {
			if (userAgentInfo.currentUserAgent != null) {
				site.setRobotCrawlDelay(ConversionUtils.toInteger(lineValue));
			}

		} else if (lineName.equalsIgnoreCase(DISALLOW_NAME) || lineName.equalsIgnoreCase(ALLOW_NAME)) {
			if (userAgentInfo.currentUserAgent != null && !lineValue.isEmpty()) {
				boolean allow = lineName.equalsIgnoreCase(ALLOW_NAME);
				site.getRobotPathList().add(new RobotPath(allow, lineValue));
			}

		}
	}

}
