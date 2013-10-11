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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.site.RobotPath;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * Panel for displaying site information
 * 
 * @author lbertelo
 */
public class SiteDetailPanel {

	private Site site;
	private OverviewPanel overviewPanel;
	private JPanel panel;
	private JTabbedPane tabbedPane;

	private JEditorPane domain;
	private JLabel minOuterDeep;
	private JLabel crawledPagesNumber;
	private JLabel totalScore;
	private JLabel averageScore;
	private JCheckBox robotFileExist;
	private JLabel robotCrawlDelay;
	private JTextArea robotCrawlDisallowPath;
	private JScrollPane scrollPane;

	private JList<Domain> incomingDomains;
	private JList<Domain> outgoingDomains;

	private JPanel analysisPanel;
	private JPanel analysisButtonPanel;

	public SiteDetailPanel(OverviewPanel overviewPanel) {
		this.overviewPanel = overviewPanel;
		buildContent();
	}

	public JPanel getPanel() {
		return panel;
	}

	private void buildContent() {
		// Main Panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridLayout(0, 1));

		domain = new JEditorPane();
		domain.setEditable(false);
		domain.setContentType("text/html");
		domain.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.siteDetail.domain")));
		tempPanel.add(domain);

		JPanel subTempPanel = new JPanel();
		subTempPanel.setLayout(new GridLayout());

		minOuterDeep = new JLabel("", JLabel.CENTER);
		minOuterDeep
				.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.siteDetail.minOuterDeep")));
		subTempPanel.add(minOuterDeep);

		crawledPagesNumber = new JLabel("", JLabel.CENTER);
		crawledPagesNumber.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.siteDetail.crawledPagesNumber")));
		subTempPanel.add(crawledPagesNumber);
		tempPanel.add(subTempPanel);

		subTempPanel = new JPanel();
		subTempPanel.setLayout(new GridLayout());

		averageScore = new JLabel("", JLabel.CENTER);
		averageScore
				.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.siteDetail.averageScore")));
		subTempPanel.add(averageScore);

		totalScore = new JLabel("", JLabel.CENTER);
		totalScore.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.siteDetail.totalScore")));
		subTempPanel.add(totalScore);
		tempPanel.add(subTempPanel);

		subTempPanel = new JPanel();
		subTempPanel.setLayout(new GridLayout());

		robotFileExist = new JCheckBox();
		robotFileExist.setEnabled(false);
		JPanel robotFileExistPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		robotFileExistPanel.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.siteDetail.robotsFileExist")));
		robotFileExistPanel.add(robotFileExist);
		subTempPanel.add(robotFileExistPanel);

		robotCrawlDelay = new JLabel("", JLabel.CENTER);
		robotCrawlDelay.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.siteDetail.robotCrawlDelay")));
		subTempPanel.add(robotCrawlDelay);
		tempPanel.add(subTempPanel);

		mainPanel.add(tempPanel, BorderLayout.NORTH);

		robotCrawlDisallowPath = new JTextArea("");
		robotCrawlDisallowPath.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.siteDetail.robotCrawlDisallowPath")));
		robotCrawlDisallowPath.setLineWrap(true);
		scrollPane = new JScrollPane(robotCrawlDisallowPath, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel commandPanel = new JPanel(new BorderLayout());
		tempPanel = new JPanel(new FlowLayout());
		JButton blacklistButton = new JButton(I18nUtil.getMessage("overview.siteDetail.blacklist"));
		blacklistButton.addActionListener(new BlacklistSiteAction());
		tempPanel.add(blacklistButton);
		JButton recrawlSiteButton = new JButton(I18nUtil.getMessage("overview.detail.recrawl"));
		recrawlSiteButton.addActionListener(new RecrawlSiteAction());
		tempPanel.add(recrawlSiteButton);
		commandPanel.add(tempPanel, BorderLayout.CENTER);
		mainPanel.add(commandPanel, BorderLayout.PAGE_END);

		// Link Panel
		JPanel linkPanel = new JPanel();
		linkPanel.setLayout(new GridLayout(0, 2));
		incomingDomains = new JList<Domain>();
		incomingDomains.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.detail.incomingDomains")));
		incomingDomains.addMouseListener(new JListMouseAction(incomingDomains));
		JScrollPane scrollPaneLinkTemp = new JScrollPane(incomingDomains, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		linkPanel.add(scrollPaneLinkTemp);
		outgoingDomains = new JList<Domain>();
		outgoingDomains.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.detail.outgoingDomains")));
		outgoingDomains.addMouseListener(new JListMouseAction(outgoingDomains));
		scrollPaneLinkTemp = new JScrollPane(outgoingDomains, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		linkPanel.add(scrollPaneLinkTemp);

		// Analysis Panel
		analysisPanel = new JPanel();
		analysisPanel.setLayout(new BorderLayout());
		JButton analysisButton = new JButton(I18nUtil.getMessage("overview.detail.launchAnalysis"));
		analysisButton.addActionListener(new ContentAnalysisAction());
		analysisButtonPanel = new JPanel(new FlowLayout());
		analysisButtonPanel.add(analysisButton);
		analysisPanel.add(analysisButtonPanel, BorderLayout.CENTER);

		// Panel
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(I18nUtil.getMessage("overview.detail.tab.main"), mainPanel);
		tabbedPane.addTab(I18nUtil.getMessage("overview.detail.tab.links"), linkPanel);
		tabbedPane.addTab(I18nUtil.getMessage("overview.detail.tab.analysis"), analysisPanel);
		panel.add(tabbedPane, BorderLayout.CENTER);
	}

	public void showSiteInfos(Site site) {
		this.site = site;
		domain.setText("<a href=\"" + site.getDomain().getUrl() + "\">" + site.getDomain().toString() + "</a>");

		if (site.getRobotFileExist() == Boolean.TRUE) {
			robotFileExist.getParent().setVisible(true);
			robotCrawlDelay.setVisible(true);
			robotCrawlDisallowPath.setVisible(true);
			scrollPane.setVisible(true);

			robotFileExist.setSelected(ConversionUtils.toBoolean(site.getRobotFileExist()));
			robotCrawlDelay.setText(ConversionUtils.toString(site.getRobotCrawlDelay()));

			StringBuffer robotPathStr = new StringBuffer();
			if (site.getRobotPathList() != null) {
				for (RobotPath robotPath : site.getRobotPathList()) {
					robotPathStr.append(robotPath.toString());
					robotPathStr.append('\n');
				}
			}
			robotCrawlDisallowPath.setText(robotPathStr.toString());
		} else {
			robotFileExist.getParent().setVisible(false);
			robotCrawlDelay.setVisible(false);
			robotCrawlDisallowPath.setVisible(false);
			scrollPane.setVisible(false);
		}

		minOuterDeep.setText(ConversionUtils.toString(site.getMinOuterDeep()));
		crawledPagesNumber.setText(ConversionUtils.toString(site.getCrawledPagesNumber()));

		if (site.getCrawledPagesNumber() == 0) {
			totalScore.setVisible(false);
			averageScore.setVisible(false);
		} else {
			totalScore.setVisible(true);
			averageScore.setVisible(true);
			if (site.getTotalScore() != null) {
				totalScore.setText(String.valueOf(Math.round(site.getTotalScore())));
				averageScore.setText(String.valueOf(Math.round(site.getTotalScore() / site.getCrawledPagesNumber())));
			} else {
				totalScore.setText(I18nUtil.getMessage("general.undefined"));
				averageScore.setText(I18nUtil.getMessage("general.undefined"));
			}
		}

		incomingDomains.setListData((Domain[]) site.getIncomingDomains().toArray(new Domain[0]));
		outgoingDomains.setListData((Domain[]) site.getOutgoingDomains().toArray(new Domain[0]));

		analysisPanel.removeAll();
		analysisPanel.add(analysisButtonPanel, BorderLayout.CENTER);

		tabbedPane.setSelectedIndex(0);
	}

	// Listener Classes

	private class JListMouseAction extends MouseAdapter {
		private JList<Domain> domainsList;

		public JListMouseAction(JList<Domain> domainsList) {
			this.domainsList = domainsList;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 2 && domainsList.getSelectedValue() instanceof Domain) {
				// show site
				Domain domain = domainsList.getSelectedValue();
				overviewPanel.selectAndShowSite(domain);
			}
		}
	}

	private class BlacklistSiteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null,
					I18nUtil.getMessage("overview.siteDetail.blacklistDialog"),
					I18nUtil.getMessage("overview.siteDetail.blacklist"), JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				overviewPanel.getModel().notify(IPropertyName.ADD_BLACKLIST_DOMAIN, domain);
			}
		}
	}

	private class RecrawlSiteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null, I18nUtil.getMessage("overview.siteDetail.recrawlDialog"),
					I18nUtil.getMessage("overview.siteDetail.recrawl"), JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				SiteDAO siteDao = new SiteDAO();
				siteDao.updateCrawlNow(site.getDomain(), true);
			}
		}
	}

	private class ContentAnalysisAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ContentAnalysisPane analysisPane = new ContentAnalysisPane(site);
			JScrollPane analysisScrollPane = new JScrollPane(analysisPane.getTable(),
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			analysisPanel.removeAll();
			analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);
		}
	}
}
