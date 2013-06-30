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

package org.mfcrawler.model.util.extraction;

import java.util.ArrayList;
import java.util.List;
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
	 * @param site the site
	 * @param robotFileContent the content of the robots.txt
	 */
	public static void extraction(Site site, String robotFileContent) {
		if (robotFileContent != null) {

			final String myUserAgent = ApplicationModel.getConfig().getUserAgent();
			String currentUserAgent = null;
			boolean myUserAgentSelected = false;
			List<RobotPath> robotPathList = new ArrayList<RobotPath>();
			
			Integer robotCrawlDelay = null;

			Pattern pattern = Pattern.compile(LINE_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(robotFileContent);

			while (matcher.find()) {
				int groupCount = matcher.groupCount();
				if (groupCount == 2) {
					String lineName = matcher.group(1);
					String value = matcher.group(2);

					if (lineName.equalsIgnoreCase(USER_AGENT_NAME)) {
						if (value.equalsIgnoreCase(myUserAgent)) {
							myUserAgentSelected = true;
							currentUserAgent = value;
						} else if (!myUserAgentSelected && value.equalsIgnoreCase(USER_AGENT_ALL)) {
							currentUserAgent = value;
						} else {
							currentUserAgent = null;
						}
						
						if (currentUserAgent != null) {
							robotPathList = new ArrayList<RobotPath>();
						}

					} else if (lineName.equalsIgnoreCase(CRAWL_DELAY_NAME)) {
						if (currentUserAgent != null) {
							robotCrawlDelay = ConversionUtils.toInteger(value);
						}

					} else if (lineName.equalsIgnoreCase(DISALLOW_NAME) || lineName.equalsIgnoreCase(ALLOW_NAME)) {
						if (currentUserAgent != null && !value.isEmpty()) {
							boolean allow = lineName.equalsIgnoreCase(ALLOW_NAME);
							robotPathList.add(new RobotPath(allow, value));
						}

					}
				}
			}

			site.setRobotCrawlDelay(robotCrawlDelay);
			site.setRobotPathList(robotPathList);
		}
	}

}
