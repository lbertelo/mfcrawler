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

package org.mfcrawler.model.pojo.site.link;

/**
 * Describes a linkPath (path + protocol)
 * 
 * @author lbertelo
 */
public class LinkPath {

	/**
	 * Protocol of a link ("http" or "https")
	 */
	private String protocol;

	/**
	 * Path of a link
	 */
	private String path;

	/**
	 * Default constructor
	 * @param protocol the protocol
	 * @param path the path
	 */
	public LinkPath(String protocol, String path) {
		this.protocol = protocol;
		this.path = path;
	}

	/**
	 * Copy constructor
	 * @param linkPath linkPath to copy
	 */
	public LinkPath(LinkPath linkPath) {
		this.protocol = new String(linkPath.protocol);
		this.path = new String(linkPath.path);
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
	 * Getter of protocol
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Setter of protocol
	 * @param protocol the protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		String prefix = "";
		if (protocol.endsWith("s")) {
			prefix = "(https)";
		}

		return prefix + path;
	}

}
