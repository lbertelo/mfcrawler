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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Describes a link for a website page
 * 
 * @author lbertelo
 */
public class Link implements Comparable<Link> {

	/**
	 * Protocol for robots.txt
	 */
	private static final String ROBOTSTXT_PROTOCOL = "http";
	
	/**
	 * Path for robots.txt
	 */
	private static final String ROBOTSTXT_PATH = "/robots.txt";
	
	/**
	 * Protocol suffix
	 */
	private static final String PROTOCOL_SUFFIX = "://";

	/**
	 * The domain
	 */
	private Domain domain;
	
	/**
	 * The linkPath (path + protocol)
	 */
	private LinkPath linkPath;

	/**
	 * Base constructor
	 * @param protocol the protocol
	 * @param domain the domain
	 * @param path the path
	 */
	public Link(String protocol, String domain, String path) {
		this.domain = new Domain(domain);
		this.linkPath = new LinkPath(protocol, path);
	}

	/**
	 * Constructor with domain and linkPath as parameters
	 * @param domain
	 * @param linkPath
	 */
	public Link(Domain domain, LinkPath linkPath) {
		this.domain = domain;
		this.linkPath = linkPath;
	}

	/**
	 * Copy constructor
	 * @param link the link to copy
	 */
	public Link(Link link) {
		this.domain = new Domain(link.domain);
		this.linkPath = new LinkPath(link.linkPath);
	}

	/**
	 * Constructor for robots.txt
	 * @param domain
	 */
	public Link(Domain domain) {
		this.domain = domain;
		this.linkPath = new LinkPath(ROBOTSTXT_PROTOCOL, ROBOTSTXT_PATH);
	}

	/**
	 * Getter of domain
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Setter of domain
	 * @param domain the domain
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Getter of linkPath
	 * @return the linkPath
	 */
	public LinkPath getLinkPath() {
		return linkPath;
	}

	/**
	 * Setter of linkPath
	 * @param linkPath the linkPath
	 */
	public void setLinkPath(LinkPath linkPath) {
		this.linkPath = linkPath;
	}

	/**
	 * Return the url of the link
	 * @return the url of the link
	 */
	public String getUrl() {
		if (domain != null && linkPath != null) {
			StringBuilder url = new StringBuilder();
			url.append(linkPath.getProtocol()).append(PROTOCOL_SUFFIX).append(domain).append(linkPath.getPath());
			return url.toString();
		} else {
			return null;
		}
	}

	/**
	 * Return the root domain (without subdomains)
	 * @return the root domain
	 */
	public String getRootDomain() {
		return domain.getRootDomain();
	}

	/**
	 * Describes if the link concerns robots.txt
	 * @return true if is a robots.txt link, false otherwise
	 */
	public boolean isRobotsTxt() {
		return (linkPath.getProtocol().equals(ROBOTSTXT_PROTOCOL) && linkPath.getPath().equals(ROBOTSTXT_PATH));
	}

	@Override
	public String toString() {
		return getUrl();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Link) {
			Link link = (Link) object;
			return getUrl().equals(link.getUrl());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	@Override
	public int compareTo(Link link) {
		int compareRootDomain = getDomain().compareTo(link.getDomain());
		if (compareRootDomain != 0) {
			return compareRootDomain;
		} else {
			return getUrl().compareTo(link.getUrl());
		}
	}

	/**
	 * Parse a url and construct a link
	 * @param url the url to parse
	 * @return the link
	 */
	public static Link parseUrl(String url) {
		Link link = null;

		String stringUrl;
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			stringUrl = "http://" + url;
		} else {
			stringUrl = url;
		}

		try {
			URL myUrl = new URL(stringUrl);

			String protocol = myUrl.getProtocol();
			String domain = myUrl.getHost().trim();
			String path = myUrl.getPath().trim();

			if (myUrl.getQuery() != null) {
				path += "?" + myUrl.getQuery();
			}
			if (myUrl.getRef() != null) {
				path += "#" + myUrl.getRef();
			}

			if (!domain.isEmpty() && !protocol.isEmpty()) {
				if (path.isEmpty()) {
					path = "/";
				}

				link = new Link(protocol, domain, path);
			}

		} catch (Exception e) {
			Logger.getLogger(Link.class.getName()).log(Level.INFO, "Error to parse : " + stringUrl, e);
		}

		return link;
	}

}
