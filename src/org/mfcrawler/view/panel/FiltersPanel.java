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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.panel.filters.BlackListSubPanel;
import org.mfcrawler.view.panel.filters.KeywordSubPanel;

/**
 * Panel for filtering information (keywords and blacklisted domains)
 * 
 * @author lbertelo
 */
public class FiltersPanel extends DefaultPanel {

	private KeywordSubPanel keywordSubPanel;
	private BlackListSubPanel blackListSubPanel;
	private JButton buttonApply;

	public FiltersPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("filters.title"), I18nUtil.getMessage("filters.description"));
		getModel().addListener(IPropertyName.PROJECT_LOADED, this);
		getModel().addListener(IPropertyName.ADD_BLACKLIST_DOMAIN, this);
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel filtersPanel = new JPanel();
		filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.PAGE_AXIS));

		keywordSubPanel = new KeywordSubPanel(this);
		filtersPanel.add(keywordSubPanel.getPanel());

		blackListSubPanel = new BlackListSubPanel(this);
		filtersPanel.add(blackListSubPanel.getPanel());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonApply = new JButton(I18nUtil.getMessage("filters.apply"));
		buttonApply.addActionListener(new ApplyFiltersAction());
		buttonApply.setEnabled(false);
		buttonPanel.add(buttonApply);

		panel.add(filtersPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.PAGE_END);

		return panel;
	}

	public void enabledButtonApply(boolean enabled) {
		buttonApply.setEnabled(enabled);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			CrawlProject crawlProject = (CrawlProject) evt.getNewValue();
			blackListSubPanel.setBlacklistDomains(crawlProject.getBlacklistDomains());
			keywordSubPanel.setKeywordMap(crawlProject.getKeywordMap());
		} else if (propertyName.equals(IPropertyName.ADD_BLACKLIST_DOMAIN)) {
			String domainStr = (String) evt.getNewValue();
			blackListSubPanel.addBlacklistDomain(domainStr);
		}
	}
	
	private class ApplyFiltersAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (getModel().getSupervisor().isStopped()) {
				getModel().applyFilters(blackListSubPanel.getBlacklistDomains(), keywordSubPanel.getKeywordMap());
				buttonApply.setEnabled(false);
			} else {
				getView().notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			}
		}
	}

}