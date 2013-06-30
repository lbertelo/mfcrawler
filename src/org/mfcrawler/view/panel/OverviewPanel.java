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

package org.mfcrawler.view.panel;

import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.panel.overview.DetailSubPanel;
import org.mfcrawler.view.panel.overview.IndexSubPanel;

/**
 * Panel for displaying site and page information
 * 
 * @author lbertelo
 */
public class OverviewPanel extends DefaultPanel {

	private JSplitPane splitPane;
	private IndexSubPanel indexSubPanel;
	private DetailSubPanel detailSubPanel;

	public OverviewPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("overview.title"), I18nUtil.getMessage("overview.description"));
		getModel().addListener(IPropertyName.PROJECT_LOADED, this);
	}

	@Override
	protected JComponent buildContent() {
		indexSubPanel = new IndexSubPanel(this);
		detailSubPanel = new DetailSubPanel(this);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, indexSubPanel.getPanel(), detailSubPanel.getPanel());
		splitPane.setDividerLocation(250);
		splitPane.setDividerSize(8);
		splitPane.setOneTouchExpandable(true);

		return splitPane;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			refresh();
		}
	}

	public void showSiteInfos(Domain domain) {
		detailSubPanel.showSiteInfos(domain);
	}

	public void selectAndShowSite(Domain domain) {
		// If the node is not selected, we still display it
		if (!indexSubPanel.selectSiteNode(domain)) {
			showSiteInfos(domain);
		}
	}

	public void showPageInfos(Link link) {
		detailSubPanel.showPageInfos(link);
	}

	public void selectAndShowPage(Link link) {
		// If the node is not selected, we still display it
		if (!indexSubPanel.selectPageNode(link)) {
			showPageInfos(link);
		}
	}

	public void showRefresh() {
		detailSubPanel.showRefresh();
	}

	public void refresh() {
		OverviewParams params = detailSubPanel.getOverviewParams();

		SiteDAO siteDao = new SiteDAO();
		PageDAO pageDao = new PageDAO();
		List<Domain> domainList = siteDao.getDomainListToDisplay(params);
		Integer pagesNumber = pageDao.getPathNumberToDisplay(params);

		indexSubPanel.setOverviewParams(params);
		indexSubPanel.updateSiteTree(domainList);
		detailSubPanel.updateResultsNumber(domainList.size(), pagesNumber);
	}

}
