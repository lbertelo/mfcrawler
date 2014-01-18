/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013-2014  lbertelo

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
 * Describes the params for displaying sites and pages (for OverviewPanel and
 * DAOs)
 * 
 * @author lbertelo
 */
public class OverviewParams {

	/**
	 * Default value of the boolean selectCrawled
	 */
	private static final boolean SELECT_CRAWLED_DEFAULT = true;

	/**
	 * Default value of the boolean selectJustFound
	 */
	private static final boolean SELECT_JUST_FOUND_DEFAULT = false;

	/**
	 * Default value of the boolean selectError
	 */
	private static final boolean SELECT_ERROR_DEFAULT = false;

	/**
	 * Default value of the boolean selectRedirectPage
	 */
	private static final boolean SELECT_REDIRECT_PAGE_DEFAULT = false;

	/**
	 * Default value of the order
	 */
	private static final EPageOrder ORDER_DEFAULT = EPageOrder.NAME;

	/**
	 * Indicates if the crawled pages (and ok) should be displayed
	 */
	private boolean selectCrawled;

	/**
	 * Indicates if the just found pages (not crawled) should be displayed
	 */
	private boolean selectJustFound;

	/**
	 * Indicates if the errors should be displayed
	 */
	private boolean selectError;

	/**
	 * Indicates if the redirect pages should be displayed
	 */
	private boolean selectRedirectPage;

	/**
	 * Indicates the order of the display
	 */
	private EPageOrder order;

	/**
	 * Default constructor which initialize attributes with their default values
	 */
	public OverviewParams() {
		selectCrawled = SELECT_CRAWLED_DEFAULT;
		selectJustFound = SELECT_JUST_FOUND_DEFAULT;
		selectError = SELECT_ERROR_DEFAULT;
		selectRedirectPage = SELECT_REDIRECT_PAGE_DEFAULT;
		order = ORDER_DEFAULT;
	}

	/**
	 * Getter of selectCrawled
	 * @return the boolean selectCrawled
	 */
	public boolean isSelectCrawled() {
		return selectCrawled;
	}

	/**
	 * Setter of selectCrawled
	 * @param selectCrawled the boolean selectCrawled
	 */
	public void setSelectCrawled(boolean selectCrawled) {
		this.selectCrawled = selectCrawled;
	}

	/**
	 * Getter of selectJustFound
	 * @return the boolean selectJustFound
	 */
	public boolean isSelectJustFound() {
		return selectJustFound;
	}

	/**
	 * Setter of selectJustFound
	 * @param selectJustFound the boolean selectJustFound
	 */
	public void setSelectJustFound(boolean selectJustFound) {
		this.selectJustFound = selectJustFound;
	}

	/**
	 * Getter of selectError
	 * @return the boolean selectError
	 */
	public boolean isSelectError() {
		return selectError;
	}

	/**
	 * Setter of selectError
	 * @param selectError the boolean selectError
	 */
	public void setSelectError(boolean selectError) {
		this.selectError = selectError;
	}

	/**
	 * Getter of selectRedirectPage
	 * @return the boolean selectRedirectPage
	 */
	public boolean isSelectRedirectPage() {
		return selectRedirectPage;
	}

	/**
	 * Setter of selectRedirectPage
	 * @param selectRedirectPage the boolean selectRedirectPage
	 */
	public void setSelectRedirectPage(boolean selectRedirectPage) {
		this.selectRedirectPage = selectRedirectPage;
	}

	/**
	 * Getter of order
	 * @return the pageOrder
	 */
	public EPageOrder getOrder() {
		return order;
	}

	/**
	 * Setter of order
	 * @param order the pageOrder
	 */
	public void setOrder(EPageOrder order) {
		this.order = order;
	}

}
