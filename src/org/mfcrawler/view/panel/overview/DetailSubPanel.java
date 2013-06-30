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

package org.mfcrawler.view.panel.overview;

import java.awt.CardLayout;

import javax.swing.JPanel;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.DefaultPanel;
import org.mfcrawler.view.panel.DefaultSubPanel;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * SubPanel for overview details
 * 
 * @author lbertelo
 */
public class DetailSubPanel extends DefaultSubPanel {

	private static final String SITE_PANEL = "site";
	private static final String PAGE_PANEL = "page";
	private static final String REFRESH_PANEL = "refresh";

	private CardLayout cardLayout;
	private SiteDetailPanel siteDetailPanel;
	private PageDetailPanel pageDetailPanel;
	private RefreshDetailPanel refreshDetailPanel;

	public DetailSubPanel(DefaultPanel parentPanel) {
		super(parentPanel, I18nUtil.getMessage("overview.detail"));
	}

	@Override
	protected JPanel buildContent() {
		siteDetailPanel = new SiteDetailPanel((OverviewPanel) getParentPanel());
		pageDetailPanel = new PageDetailPanel((OverviewPanel) getParentPanel());
		refreshDetailPanel = new RefreshDetailPanel(this);

		cardLayout = new CardLayout();
		JPanel panel = new JPanel(cardLayout);
		panel.add(siteDetailPanel.getPanel(), SITE_PANEL);
		panel.add(pageDetailPanel.getPanel(), PAGE_PANEL);
		panel.add(refreshDetailPanel.getPanel(), REFRESH_PANEL);
		cardLayout.show(panel, REFRESH_PANEL);

		return panel;
	}

	public OverviewParams getOverviewParams() {
		return refreshDetailPanel.getParams();
	}
	
	public void showRefresh() {
		cardLayout.show(getPanel(), REFRESH_PANEL);
	}

	public void showSiteInfos(Domain domain) {
		SiteDAO siteDao = new SiteDAO();
		Site site = siteDao.getSiteWithAllInformation(domain);
		if (site != null) {
			siteDetailPanel.showSiteInfos(site);
			cardLayout.show(getPanel(), SITE_PANEL);
		}		
	}

	public void showPageInfos(Link link) {
		PageDAO pageDao = new PageDAO();
		Page page = pageDao.getPageWithAllInformation(link);
		if (page != null) {
			pageDetailPanel.showPageInfos(page);
			cardLayout.show(getPanel(), PAGE_PANEL);
		}
	}

	public void updateResultsNumber(Integer sitesNumber, Integer pagesNumber) {
		refreshDetailPanel.updateResultsNumber(sitesNumber, pagesNumber);
	}

}
