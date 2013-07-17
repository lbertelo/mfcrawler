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

package org.mfcrawler.model.pojo.site;

/**
 * Describes a robotPath from robots.txt
 * 
 * @author lbertelo
 */
public class RobotPath {

	/**
	 * Start of pattern for "checkPath"
	 */
	private static final String PATTERN_START = "^\\Q";

	/**
	 * Pattern for replacing "*"
	 */
	private static final String PATTERN_REPLACE = "\\*";

	/**
	 * Pattern for replacing by "\E\S+\Q"
	 */
	private static final String PATTERN_REPLACE_BY = "\\\\E\\\\S+\\\\Q";

	/**
	 * End of pattern for "checkPath"
	 */
	private static final String PATTERN_END = "\\E";

	/**
	 * Indicates if the robotPath is allow
	 */
	private boolean allow;

	/**
	 * Path of the robotPath
	 */
	private String path;

	/**
	 * Default constructor
	 * @param allow the boolean allow
	 * @param path the path
	 */
	public RobotPath(Boolean allow, String path) {
		this.allow = allow;
		this.path = path;
	}

	/**
	 * Getter of allow
	 * @return the boolean allow
	 */
	public boolean isAllow() {
		return allow;
	}

	/**
	 * Setter of allow
	 * @param allow the boolean allow
	 */
	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	/**
	 * Getter of path
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Setter of path
	 * @param path the path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Indicates if the path checks an url
	 * @param urlPath the url to check
	 * @return true if urlPath checks the robotPath, false otherwise
	 */
	public boolean checkPath(String urlPath) {
		StringBuilder pathPattern = new StringBuilder();
		pathPattern.append(PATTERN_START);
		pathPattern.append(path.replaceAll(PATTERN_REPLACE, PATTERN_REPLACE_BY));
		pathPattern.append(PATTERN_END);

		return urlPath.matches(pathPattern.toString());
	}

}
