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
 * Describes a website domain
 * 
 * @author lbertelo
 */
public class Domain implements Comparable<Domain> {

	/**
	 * The name of the domain
	 */
	private String name;

	/**
	 * Default constructor
	 */
	public Domain() {
		name = "";
	}

	/**
	 * Constructor with the domain name as a parameter
	 * @param domain the domain name
	 */
	public Domain(String domainName) {
		this.name = domainName;
	}
	
	/**
	 * Copy constructor
	 * @param domain the domain to copy
	 */
	public Domain(Domain domain) {
		this.name = new String(domain.name);
	}
	
	/**
	 * Getter of name
	 * @return the name of the domain
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter of name
	 * @param name the name of the domain
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the url for the domain (with "http://")
	 * @return the url
	 */
	public String getUrl() {
		if (name != null) {
			return "http://" + name;
		} else {
			return null;
		}
	}

	/**
	 * Return the root domain (without subdomains)
	 * @return the root domain
	 */
	public String getRootDomain() {
		String domainSplit[] = name.split("\\.");
		String rootDomain = name;
		if (domainSplit.length > 2) {
			rootDomain = domainSplit[domainSplit.length - 2] + "." + domainSplit[domainSplit.length - 1];
		}
		return rootDomain;
	}

	/**
	 * Describes if the domain is a filter domain (start with ".")
	 * @return true if the domain is a filter, false otherwise
	 */
	public boolean isFilterBlacklist() {
		return name.startsWith(".");
	}

	/**
	 * Check the domain for blacklisting
	 * @param blacklistDomain the blacklist domain
	 * @return true if the domain must be blacklisted, false otherwise
	 */
	public boolean checkBlacklist(Domain blacklistDomain) {
		return (this.equals(blacklistDomain) || (blacklistDomain.isFilterBlacklist() && name.endsWith(blacklistDomain
				.getName())));
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Domain) {
			Domain otherDomain = (Domain) object;
			return name.equals(otherDomain.getName());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(Domain otherDomain) {
		int compareRootDomain = getRootDomain().compareTo(otherDomain.getRootDomain());
		if (compareRootDomain != 0) {
			return compareRootDomain;
		} else {
			return name.compareTo(otherDomain.getName());
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
