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
 * Describes a outgoing link
 * 
 * @author lbertelo
 */
public class OutgoingLink extends Link {

	/**
	 * Indicates if the page pointed
	 * by the outgoing link is crawled
	 */
	private Boolean pageCrawled;

	/**
	 * Default constructor
	 * @param link
	 * @param crawled
	 */
	public OutgoingLink(Link link, Boolean crawled) {
		super(link);
		this.pageCrawled = crawled;
	}

	/**
	 * Getter of page crawled
	 * @return the boolean page crawled
	 */
	public Boolean getPageCrawled() {
		return pageCrawled;
	}

	/**
	 * Setter of page crawled
	 * @param pageCrawled the boolean page crawled
	 */
	public void setPageCrawled(Boolean pageCrawled) {
		this.pageCrawled = pageCrawled;
	}

}
