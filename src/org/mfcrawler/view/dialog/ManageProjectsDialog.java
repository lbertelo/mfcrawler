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

package org.mfcrawler.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.export.config.LoadCrawlProjectConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.SortedListModel;

/**
 * Dialog for managing projects
 * 
 * @author lbertelo
 */
public class ManageProjectsDialog extends DefaultDialog {

	private static final int DIALOG_WIDTH = 400;
	private static final int DIALOG_HEIGHT = 300;

	private JDialog dialog;
	private SortedListModel<String> projectListModel;
	private JList<String> projectJList;
	private Map<String, CrawlProject> projectMap;
	private JButton loadButton;
	private JButton deleteButton;

	public ManageProjectsDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);
	}

	@Override
	public void display() {
		projectListModel = new SortedListModel<String>();
		projectMap = new HashMap<String, CrawlProject>();

		for (CrawlProject project : LoadCrawlProjectConfig.getCrawlProjectList()) {
			projectMap.put(project.toString(), project);
			projectListModel.addElement(project.toString());
		}
		projectJList.setModel(projectListModel);

		super.display();
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.manageProject"), true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialog.setLayout(new BorderLayout());

		JPanel tempPanel;
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 10));
		projectJList = new JList<String>();
		projectJList.addListSelectionListener(new SelectProjectAction());
		JScrollPane paneJList = new JScrollPane(projectJList);
		contentPanel.add(paneJList, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		JPanel buttonPanelStart = new JPanel();
		buttonPanelStart.setLayout(new BoxLayout(buttonPanelStart, BoxLayout.PAGE_AXIS));
		buttonPanel.add(buttonPanelStart, BorderLayout.PAGE_START);
		tempPanel = new JPanel(new FlowLayout());
		loadButton = new JButton(I18nUtil.getMessage("general.load"));
		loadButton.setEnabled(false);
		loadButton.addActionListener(new LoadAction());
		tempPanel.add(loadButton);
		buttonPanelStart.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		deleteButton = new JButton(I18nUtil.getMessage("general.delete"));
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new DeleteAction());
		tempPanel.add(deleteButton);
		buttonPanelStart.add(tempPanel);

		JPanel buttonPanelEnd = new JPanel();
		buttonPanelEnd.setLayout(new BoxLayout(buttonPanelEnd, BoxLayout.PAGE_AXIS));
		buttonPanel.add(buttonPanelEnd, BorderLayout.PAGE_END);
		tempPanel = new JPanel(new FlowLayout());
		JButton closeButton = new JButton(I18nUtil.getMessage("general.close"));
		closeButton.addActionListener(new CloseAction());
		tempPanel.add(closeButton);
		buttonPanelEnd.add(tempPanel);

		dialog.add(contentPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.EAST);

		return dialog;
	}

	// Listener Classes

	private class SelectProjectAction implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			List<String> selectedValues = projectJList.getSelectedValuesList();
			deleteButton.setEnabled(!selectedValues.isEmpty());
			loadButton.setEnabled(selectedValues.size() == 1);
		}
	}

	private class LoadAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> selectedValues = projectJList.getSelectedValuesList();
			if (selectedValues.size() == 1) {
				CrawlProject crawlProject = projectMap.get(selectedValues.get(0));

				if (getModel().getCurrentCrawlProject().equals(crawlProject)) {
					getView().notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.loadCurrentProject"));
				} else {
					getModel().loadCrawlProject(crawlProject.getName());
				}
			}
			hide();
		}
	}

	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> selectedValues = projectJList.getSelectedValuesList();
			for (String value : selectedValues) {
				CrawlProject crawlProject = projectMap.get(value);

				if (getModel().getCurrentCrawlProject().equals(crawlProject)) {
					getView().notifyDialog(IPropertyName.ERROR, I18nUtil.getMessage("error.deleteCurrentProject"));
				} else {
					StringBuilder confirmMsg = new StringBuilder(I18nUtil.getMessage("general.theProject"))
							.append(" \"").append(crawlProject.getName()).append("\" ")
							.append(I18nUtil.getMessage("confirm.deleteProject"));
					int option = JOptionPane.showConfirmDialog(null, confirmMsg,
							I18nUtil.getMessage("dialog.deleteProject"), JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION) {
						getModel().deleteCrawlProject(crawlProject.getName());
						projectListModel.removeElement(value);
					}
				}
			}
		}
	}

	private class CloseAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			hide();
		}
	}

}
