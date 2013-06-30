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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.pojo.site.link.LinkPath;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.DefaultPanel;
import org.mfcrawler.view.panel.DefaultSubPanel;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * SubPanel for selecting sites and page
 * 
 * @author lbertelo
 */
public class IndexSubPanel extends DefaultSubPanel {

	private OverviewParams overviewParams;

	private JTree domainsTree;
	private DefaultTreeModel domainsTreeModel;
	private DefaultMutableTreeNode rootNode;
	private JButton quickRefreshButton;
	private JButton refreshButton;

	public IndexSubPanel(DefaultPanel parentPanel) {
		super(parentPanel, I18nUtil.getMessage("overview.index"));
		overviewParams = new OverviewParams();
	}

	@Override
	protected JPanel buildContent() {
		JPanel panel = new JPanel(new BorderLayout());

		// JTree Construction
		rootNode = new DefaultMutableTreeNode(I18nUtil.getMessage("overview.index.root"));
		domainsTree = new JTree(rootNode);
		domainsTree.addTreeWillExpandListener(new ExpandEvent());
		domainsTree.addTreeSelectionListener(new SelectedValueAction());
		JScrollPane scrollPane = new JScrollPane(domainsTree);
		panel.add(scrollPane, BorderLayout.CENTER);

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		quickRefreshButton = new JButton(I18nUtil.getMessage("general.refresh"));
		quickRefreshButton.addActionListener(new QuickRefreshAction());
		tempPanel.add(quickRefreshButton);
		refreshButton = new JButton(I18nUtil.getMessage("overview.index.refreshMenu"));
		refreshButton.addActionListener(new RefreshAction());
		tempPanel.add(refreshButton);

		panel.add(tempPanel, BorderLayout.NORTH);

		return panel;
	}

	public void setOverviewParams(OverviewParams params) {
		overviewParams = params;
	}

	public void updateSiteTree(List<Domain> domainList) {
		domainsTreeModel = new DefaultTreeModel(constructTree(domainList));
		domainsTree.setModel(domainsTreeModel);
	}

	private TreeNode constructTree(List<Domain> domainList) {
		rootNode = new DefaultMutableTreeNode(I18nUtil.getMessage("overview.index.root"));

		for (Domain domain : domainList) {
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(I18nUtil.getMessage("dialog.loading"));
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(domain);
			node.add(subNode);
			rootNode.add(node);
		}

		return rootNode;
	}

	private void refreshSiteNode(DefaultMutableTreeNode node) {
		if (node.getLevel() == 1) {
			node.removeAllChildren();
			Domain domain = ((Domain) node.getUserObject());

			PageDAO pageDao = new PageDAO();
			List<LinkPath> pathList = pageDao.getPathListToDisplay(domain, overviewParams);

			for (LinkPath path : pathList) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode();
				subNode.setUserObject(path);
				node.add(subNode);
			}

			domainsTreeModel.nodeStructureChanged(node);
		}
	}

	public boolean selectSiteNode(Domain domain) {
		TreePath selectionTreePath = null;
		int i = 1;
		int siteNodeCount = rootNode.getChildCount();

		while (i < siteNodeCount && selectionTreePath == null) {
			DefaultMutableTreeNode siteNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			if (siteNode.getUserObject().equals(domain)) {
				selectionTreePath = new TreePath(siteNode.getPath());
			}
			i++;
		}

		domainsTree.setSelectionPath(selectionTreePath);
		return (selectionTreePath != null);
	}

	public boolean selectPageNode(Link link) {
		TreePath selectionTreePath = null;
		int i = 0;
		int siteNodeCount = rootNode.getChildCount();

		// search site
		while (i < siteNodeCount && selectionTreePath == null) {
			DefaultMutableTreeNode siteNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			if (siteNode.getUserObject().equals(link.getDomain())) {
				int j = 0;
				refreshSiteNode(siteNode);
				int pageNodeCount = siteNode.getChildCount();

				// search node
				while (j < pageNodeCount && selectionTreePath == null) {
					DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode) siteNode.getChildAt(j);
					if (pathNode.getUserObject().equals(link.getLinkPath())) {
						selectionTreePath = new TreePath(pathNode.getPath());
					}
					j++;
				}
			}
			i++;
		}

		domainsTree.setSelectionPath(selectionTreePath);
		return (selectionTreePath != null);
	}

	// Listener Classes

	private class SelectedValueAction implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (!domainsTree.isSelectionEmpty()) {
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) domainsTree.getSelectionPath()
						.getLastPathComponent();

				if (currentNode.getLevel() == 0) {
					// root
					((OverviewPanel) getParentPanel()).showRefresh();
				} else if (currentNode.getLevel() == 1) {
					// site
					Domain domain = (Domain) currentNode.getUserObject();
					((OverviewPanel) getParentPanel()).showSiteInfos(domain);
					refreshSiteNode(currentNode);
				} else if (currentNode.getLevel() == 2) {
					// page
					LinkPath path = (LinkPath) currentNode.getUserObject();
					Domain domain = (Domain) ((DefaultMutableTreeNode) currentNode.getParent()).getUserObject();
					Link link = new Link(domain, path);
					((OverviewPanel) getParentPanel()).showPageInfos(link);
				}
			}
		}
	}

	private class RefreshAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			((OverviewPanel) getParentPanel()).showRefresh();
		}
	}

	private class QuickRefreshAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			((OverviewPanel) getParentPanel()).refresh();
		}
	}

	private class ExpandEvent implements TreeWillExpandListener {
		@Override
		public void treeWillCollapse(TreeExpansionEvent event) {
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
			refreshSiteNode(currentNode);
		}
	}

}
