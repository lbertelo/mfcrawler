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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.pojo.site.link.LinkPath;
import org.mfcrawler.model.pojo.site.link.OutgoingLink;
import org.mfcrawler.model.process.content.KeywordManager;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * Panel for displaying page information
 * 
 * @author lbertelo
 */
public class PageDetailPanel {

	private Page page;
	private OverviewPanel overviewPanel;
	private JPanel panel;
	private JTabbedPane tabbedPane;

	private JEditorPane path;
	private JLabel title;
	private JCheckBox allowCrawl;
	private JLabel score;
	private JLabel innerDeep;
	private JLabel outerDeep;
	private JLabel crawlTime;
	private JPanel changingPanel;
	private JLabel crawlError;
	private JScrollPane scrollPaneResume;
	private JTextArea resumeContentText;

	private String localMessage;
	private JTree incomingLinksTree;
	private DefaultTreeModel incomingLinksModel;
	private JTree outgoingLinksTree;
	private DefaultTreeModel outgoingLinksModel;

	private JPanel analysisPanel;
	private JPanel analysisButtonPanel;

	private JEditorPane content;

	public PageDetailPanel(OverviewPanel overviewPanel) {
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

		path = new JEditorPane();
		path.setEditable(false);
		path.setContentType("text/html");
		path.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.path")));
		tempPanel.add(path);

		title = new JLabel();
		title.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.title")));
		tempPanel.add(title);

		JPanel subTempPanel = new JPanel();
		subTempPanel.setLayout(new GridLayout());

		allowCrawl = new JCheckBox();
		allowCrawl.setEnabled(false);
		JPanel allowCrawlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		allowCrawlPanel.add(allowCrawl);
		allowCrawlPanel.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.pageDetail.allowCrawl")));
		subTempPanel.add(allowCrawlPanel);

		score = new JLabel("", JLabel.CENTER);
		score.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.score")));
		subTempPanel.add(score);
		tempPanel.add(subTempPanel);

		subTempPanel = new JPanel();
		subTempPanel.setLayout(new GridLayout());

		innerDeep = new JLabel("", JLabel.CENTER);
		innerDeep.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.innerDeep")));
		subTempPanel.add(innerDeep);

		outerDeep = new JLabel("", JLabel.CENTER);
		outerDeep.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.outerDeep")));
		subTempPanel.add(outerDeep);

		tempPanel.add(subTempPanel);

		crawlTime = new JLabel("", JLabel.CENTER);
		crawlTime.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.crawlTime")));
		tempPanel.add(crawlTime);
		mainPanel.add(tempPanel, BorderLayout.NORTH);

		changingPanel = new JPanel(new BorderLayout());
		mainPanel.add(changingPanel, BorderLayout.CENTER);

		crawlError = new JLabel("", JLabel.CENTER);
		crawlError.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.crawlError")));

		resumeContentText = new JTextArea("");
		resumeContentText
				.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("overview.pageDetail.resume")));
		resumeContentText.setLineWrap(true);
		resumeContentText.setEditable(false);
		scrollPaneResume = new JScrollPane(resumeContentText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel commandPanel = new JPanel(new BorderLayout());
		tempPanel = new JPanel(new FlowLayout());
		JButton recrawlButton = new JButton(I18nUtil.getMessage("overview.detail.recrawl"));
		recrawlButton.addActionListener(new RecrawlPageAction());
		tempPanel.add(recrawlButton);
		commandPanel.add(tempPanel);
		mainPanel.add(commandPanel, BorderLayout.PAGE_END);

		// Link Panel
		localMessage = I18nUtil.getMessage("overview.pageDetail.local");
		JPanel linkPanel = new JPanel();
		linkPanel.setLayout(new GridLayout(0, 2));
		TreeNode incomingLinksRoot = new DefaultMutableTreeNode(I18nUtil.getMessage("overview.pageDetail.links"));
		incomingLinksModel = new DefaultTreeModel(incomingLinksRoot);
		incomingLinksTree = new JTree(incomingLinksModel);
		incomingLinksTree.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.detail.incomingDomains")));
		incomingLinksTree.addMouseListener(new JTreeLinksAction(incomingLinksTree));
		JScrollPane scrollPaneLinkTemp = new JScrollPane(incomingLinksTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		linkPanel.add(scrollPaneLinkTemp);

		TreeNode outgoingLinksRoot = new DefaultMutableTreeNode(I18nUtil.getMessage("overview.pageDetail.links"));
		outgoingLinksModel = new DefaultTreeModel(outgoingLinksRoot);
		outgoingLinksTree = new JTree(outgoingLinksModel);
		outgoingLinksTree.setBorder(BorderFactory.createTitledBorder(I18nUtil
				.getMessage("overview.detail.outgoingDomains")));
		outgoingLinksTree.addMouseListener(new JTreeLinksAction(outgoingLinksTree));
		scrollPaneLinkTemp = new JScrollPane(outgoingLinksTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		linkPanel.add(scrollPaneLinkTemp);

		// Content Panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		content = new JEditorPane();
		content.setEditable(false);
		content.setContentType("text/html");
		JScrollPane scrollPaneContent = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel.add(scrollPaneContent, BorderLayout.CENTER);

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
		tabbedPane.addTab(I18nUtil.getMessage("overview.detail.tab.content"), contentPanel);
		tabbedPane.addTab(I18nUtil.getMessage("overview.detail.tab.analysis"), analysisPanel);
		panel.add(tabbedPane, BorderLayout.CENTER);
	}

	public void showPageInfos(Page page) {
		this.page = page;

		path.setText("<a href=\"" + page.getLink().getUrl() + "\">" + page.getLink().toString() + "</a>");
		title.setText(ConversionUtils.toString(page.getTitle()));
		allowCrawl.setSelected(ConversionUtils.toBoolean(page.getAllowCrawl()));

		score.setText(ConversionUtils.toString(Math.round(page.getScore())));
		innerDeep.setText(ConversionUtils.toString(page.getInnerDeep()));
		outerDeep.setText(ConversionUtils.toString(page.getOuterDeep()));
		crawlTime.setText(ConversionUtils.toString(page.getCrawlTime()));

		changingPanel.removeAll();
		if (page.getCrawlError() != null) {
			crawlError.setText(ConversionUtils.toString(page.getCrawlError()));
			changingPanel.add(crawlError, BorderLayout.CENTER);

			content.setText("");
		} else {
			String resumeContent;
			if (page.getContent() != null && page.getContent().length() > 2000) {
				resumeContent = page.getContent().substring(0, 2000) + "...";
			} else {
				resumeContent = page.getContent();
			}
			resumeContentText.setText(ConversionUtils.toString(resumeContent));
			changingPanel.add(scrollPaneResume, BorderLayout.CENTER);

			content.setText(formatContent(page.getContent()));
		}
		changingPanel.repaint();

		incomingLinksModel = new DefaultTreeModel(constructTree(page.getIncomingInternLinks(),
				page.getIncomingExternLinks()));
		incomingLinksTree.setModel(incomingLinksModel);

		outgoingLinksModel = new DefaultTreeModel(constructTree(page.getOutgoingInternLinks(),
				page.getOutgoingExternLinks()));
		outgoingLinksTree.setModel(outgoingLinksModel);

		analysisPanel.removeAll();
		analysisPanel.add(analysisButtonPanel, BorderLayout.CENTER);

		tabbedPane.setSelectedIndex(0);
	}

	private TreeNode constructTree(List<Link> internLinkList, List<Link> externLinkList) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18nUtil.getMessage("overview.pageDetail.links"));
		Map<Domain, DefaultMutableTreeNode> linkMap = new HashMap<Domain, DefaultMutableTreeNode>();
		DefaultMutableTreeNode domainNode;

		if (!internLinkList.isEmpty()) {
			domainNode = new DefaultMutableTreeNode(localMessage);
			root.add(domainNode);
			for (Link link : internLinkList) {
				domainNode.add(buildPathNode(link));
			}
		}

		for (Link link : externLinkList) {
			if (linkMap.containsKey(link.getDomain())) {
				domainNode = linkMap.get(link.getDomain());
			} else {
				domainNode = new DefaultMutableTreeNode(link.getDomain());
				linkMap.put(link.getDomain(), domainNode);
				root.add(domainNode);
			}
			domainNode.add(buildPathNode(link));
		}

		return root;
	}

	private DefaultMutableTreeNode buildPathNode(Link link) {
		if (link instanceof OutgoingLink && !((OutgoingLink) link).getPageCrawled()) {
			return new DefaultMutableTreeNode(I18nUtil.getMessage("overview.pageDetail.linkNonCrawled")
					+ link.getLinkPath());
		} else {
			return new DefaultMutableTreeNode(link.getLinkPath());
		}
	}

	private String formatContent(String allContent) {
		String formatedContent = allContent;
		if (formatedContent == null) {
			return "";
		}

		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();
		for (String word : keywordMap.keySet()) {
			Pattern pattern = Pattern.compile(KeywordManager.makeRegexPattern(word), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(formatedContent);

			StringBuffer newContent = new StringBuffer();
			while (matcher.find()) {
				StringBuilder replacement = new StringBuilder();
				if (keywordMap.get(word) < 0) {
					replacement.append("<i style=\"background-color: yellow; color: red;\">");
					replacement.append(matcher.group());
					replacement.append("</i>");
				} else {
					replacement.append("<b style=\"background-color: yellow; color: green;\">");
					replacement.append(matcher.group());
					replacement.append("</b>");
				}
				matcher.appendReplacement(newContent, replacement.toString());
			}
			matcher.appendTail(newContent);
			formatedContent = newContent.toString();
		}

		String start = "<html><head> <style type=\"text/css\"> body { font-size : 11pt; } </style> </head><body>";
		String end = "</body></html>";

		return start + formatedContent + end;
	}

	// Listener Classes

	private class JTreeLinksAction extends MouseAdapter {
		private JTree linksTree;

		public JTreeLinksAction(JTree linksTree) {
			this.linksTree = linksTree;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 2 && !linksTree.isSelectionEmpty()) {
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) linksTree.getSelectionPath()
						.getLastPathComponent();

				if (currentNode.getLevel() == 2) {
					Object userObject = currentNode.getUserObject();
					if (userObject instanceof LinkPath) {
						LinkPath path = (LinkPath) userObject;

						Object parentUserObject = ((DefaultMutableTreeNode) currentNode.getParent()).getUserObject();
						Domain domain;
						if (parentUserObject instanceof Domain) {
							domain = (Domain) parentUserObject;
						} else {
							domain = page.getLink().getDomain();
						}

						overviewPanel.selectAndShowPage(new Link(domain, path));
					}
				}
			}
		}
	}

	private class RecrawlPageAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null, I18nUtil.getMessage("overview.pageDetail.recrawlDialog"),
					I18nUtil.getMessage("overview.pageDetail.recrawl"), JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				PageDAO pageDao = new PageDAO();
				pageDao.updateCrawlNow(page.getLink(), true);
			}
		}
	}

	private class ContentAnalysisAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ContentAnalysisPane analysisPane = new ContentAnalysisPane(page);
			JScrollPane analysisScrollPane = new JScrollPane(analysisPane.getTable(),
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			analysisPanel.removeAll();
			analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);
		}
	}

}
