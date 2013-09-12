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
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.export.config.LoadTextFile;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.SortedListModel;

/**
 * Dialog for managing starting pages
 * 
 * @author lbertelo
 */
public class StartingPagesDialog extends DefaultDialog {

	private static final int DIALOG_WIDTH = 400;
	private static final int DIALOG_HEIGHT = 300;

	private JDialog dialog;
	private JFileChooser fileChooser;
	private JTextField pageTextField;
	private SortedListModel<Link> linkListModel;
	private JList<Link> linkJList;
	private Map<Link, Page> pageMap;
	private JButton addButton;
	private JButton deleteButton;
	private JButton loadFileButton;
	private JButton cancelButton;
	private JButton validButton;

	public StartingPagesDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);

		pageMap = new HashMap<Link, Page>();
		fileChooser = new JFileChooser();
	}

	@Override
	public void display() {
		linkListModel = new SortedListModel<Link>();
		PageDAO pageDao = new PageDAO();
		List<Page> startingPageList = pageDao.getStartingPageList();

		for (Page startingPage : startingPageList) {
			pageMap.put(startingPage.getLink(), startingPage);
			linkListModel.addElement(startingPage.getLink());
		}
		linkJList.setModel(linkListModel);

		super.display();
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.startingPages"), true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialog.setLayout(new BorderLayout());

		JPanel tempPanel;
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 10));
		pageTextField = new JTextField();
		contentPanel.add(pageTextField, BorderLayout.PAGE_START);
		pageTextField.addActionListener(new AddAction());
		linkJList = new JList<Link>();
		linkJList.addListSelectionListener(new SelectPageAction());
		JScrollPane paneJList = new JScrollPane(linkJList);
		contentPanel.add(paneJList, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		JPanel buttonPanelStart = new JPanel();
		buttonPanelStart.setLayout(new BoxLayout(buttonPanelStart, BoxLayout.PAGE_AXIS));
		buttonPanel.add(buttonPanelStart, BorderLayout.PAGE_START);
		tempPanel = new JPanel(new FlowLayout());
		addButton = new JButton(I18nUtil.getMessage("general.add"));
		addButton.addActionListener(new AddAction());
		tempPanel.add(addButton);
		buttonPanelStart.add(tempPanel);
		tempPanel = new JPanel(new FlowLayout());
		deleteButton = new JButton(I18nUtil.getMessage("general.delete"));
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new DeleteAction());
		tempPanel.add(deleteButton);
		buttonPanelStart.add(tempPanel);
		tempPanel = new JPanel(new FlowLayout());
		loadFileButton = new JButton(I18nUtil.getMessage("dialog.startingPages.loadFromFile"));
		loadFileButton.addActionListener(new LoadFileAction());
		tempPanel.add(loadFileButton);
		buttonPanelStart.add(tempPanel);

		JPanel buttonPanelEnd = new JPanel();
		buttonPanelEnd.setLayout(new BoxLayout(buttonPanelEnd, BoxLayout.PAGE_AXIS));
		buttonPanel.add(buttonPanelEnd, BorderLayout.PAGE_END);
		tempPanel = new JPanel(new FlowLayout());
		cancelButton = new JButton(I18nUtil.getMessage("general.cancel"));
		cancelButton.addActionListener(new CancelAction());
		tempPanel.add(cancelButton);
		buttonPanelEnd.add(tempPanel);
		tempPanel = new JPanel(new FlowLayout());
		validButton = new JButton(I18nUtil.getMessage("general.ok"));
		validButton.addActionListener(new ValidAction());
		tempPanel.add(validButton);
		buttonPanelEnd.add(tempPanel);

		dialog.add(contentPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.EAST);

		return dialog;
	}

	// Listener Classes

	private class SelectPageAction implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			List<Link> selectedValues = linkJList.getSelectedValuesList();
			Iterator<Link> selectedValuesIterator = selectedValues.iterator();
			boolean deletable = true;
			while (deletable && selectedValuesIterator.hasNext()) {
				Link link = selectedValuesIterator.next();
				Page page = pageMap.get(link);
				if (page.getCrawlTime() != null) {
					deletable = false;
				}
			}
			deleteButton.setEnabled(deletable);
		}
	}

	private class AddAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String valueTextField = pageTextField.getText().trim();
			Link link = Link.parseUrl(valueTextField);
			if (link != null && pageMap.get(link) == null) {
				linkListModel.addElement(link);
				pageMap.put(link, new Page(link));
				pageTextField.setText("");
			}
		}
	}

	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			List<Link> selectedValues = linkJList.getSelectedValuesList();
			for (Link link : selectedValues) {
				linkListModel.removeElement(link);
				pageMap.remove(link);
			}
		}
	}

	private class LoadFileAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showOpenDialog(dialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File linkFile = fileChooser.getSelectedFile();
				List<Link> linkList = LoadTextFile.loadLinkList(linkFile);
				for (Link link : linkList) {
					if (pageMap.get(link) == null) {
						linkListModel.addElement(link);
						pageMap.put(link, new Page(link));
					}
				}
			}
		}
	}

	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			hide();
		}
	}

	private class ValidAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			List<Link> elements = linkListModel.getElements();
			PageDAO pageDao = new PageDAO();
			for (Link link : elements) {
				Page page = pageMap.get(link);
				if (page.getCrawlTime() == null) {
					page.setInnerDeep(0);
					page.setOuterDeep(0);
					page.setCrawlNow(true);
					page.setScore(0.0);
					pageDao.updateFoundPage(page);
				}
			}
			hide();
		}
	}

}
