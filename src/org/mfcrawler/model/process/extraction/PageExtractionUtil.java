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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Utility which extracts page information
 * 
 * @author lbertelo
 */
public class PageExtractionUtil {

	/**
	 * Maximum length for a domain
	 */
	private static final Integer MAX_DOMAIN_LENGTH = 255;

	/**
	 * Maximum length for a path
	 */
	private static final Integer MAX_PATH_LENGTH = 2048;

	/**
	 * Regex representing a url
	 */
	private static final String LINK_REGEX = "href\\s?=\\s?[\"|']([http://|https://|/]\\S+)[\"|']";

	/**
	 * Extracts links from a page
	 * @param page the page
	 * @param parsedContent the content of the page
	 * @param forbiddenFileExtensions the forbidden file extensions
	 */
	public static void pageLinksExtraction(Page page, Source parsedContent, String forbiddenFileExtensions[]) {
		String tmpUrl;
		List<Element> elems = parsedContent.getAllElements("a");

		for (Element elem : elems) {
			tmpUrl = elem.getAttributeValue("href");
			if (tmpUrl != null) {
				addLinkToPage(page, tmpUrl.trim(), forbiddenFileExtensions);
			}
		}
	}

	/**
	 * Extracts links from a redirect pages
	 * @param page the page
	 * @param parsedContent the content of the page
	 * @param forbiddenFileExtensions the forbidden file extensions
	 */
	public static void redirectLinksExtraction(Page page, Source parsedContent, String forbiddenFileExtensions[]) {
		String content = parsedContent.toString();
		Pattern pattern = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		String tmpUrl;

		while (matcher.find()) {
			tmpUrl = matcher.group(1).trim();
			addLinkToPage(page, tmpUrl, forbiddenFileExtensions);
		}
	}

	/**
	 * Adds url to the page checks before if the url is valid
	 * @param page the page
	 * @param linkUrl the url
	 * @param forbiddenFileExtensions the forbidden file extensions
	 */
	private static void addLinkToPage(Page page, String linkUrl, String forbiddenFileExtensions[]) {
		Link tmpLink = formatUrl(page.getLink(), linkUrl.trim());
		if (tmpLink != null && checkUrl(tmpLink, forbiddenFileExtensions)) {
			if (tmpLink.getDomain().equals(page.getLink().getDomain())) {
				if (!page.getOutgoingInternLinks().contains(tmpLink)) {
					page.getOutgoingInternLinks().add(tmpLink);
				}
			} else {
				if (!page.getOutgoingExternLinks().contains(tmpLink)) {
					page.getOutgoingExternLinks().add(tmpLink);
				}
			}
		}
	}

	/**
	 * Tries to extract a link from a url
	 * @param baseLink the link of the page
	 * @param stringUrl the url
	 * @return the link
	 */
	private static Link formatUrl(Link baseLink, String stringUrl) {
		if (stringUrl.toLowerCase().startsWith("javascript:") || stringUrl.toLowerCase().startsWith("mailto:")) {
			return null;
		}

		try {
			URL newUrl;
			if (stringUrl.startsWith("http://") || stringUrl.startsWith("https://")) {
				newUrl = new URL(stringUrl);
			} else {
				URL baseUrl = new URL(baseLink.getUrl());
				newUrl = new URL(baseUrl, stringUrl);
			}

			Link link = Link.parseUrl(newUrl.toString());
			if (link != null && link.getDomain().getName().length() < MAX_DOMAIN_LENGTH
					&& link.getLinkPath().getPath().length() < MAX_PATH_LENGTH) {
				return link;
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(PageExtractionUtil.class.getName()).log(Level.WARNING, "Malformed URL = " + stringUrl, e);
			return null;
		}
	}

	/**
	 * Checks the link with the forbidden file extensions
	 * @param link the links
	 * @param forbiddenFileExtensions the forbidden file extensions
	 * @return true if the link is clear, false otherwise
	 */
	private static boolean checkUrl(Link link, String forbiddenFileExtensions[]) {
		for (String extension : forbiddenFileExtensions) {
			if (link.getLinkPath().getPath().endsWith(extension)) {
				return false;
			}
		}
		return true;
	}

}
