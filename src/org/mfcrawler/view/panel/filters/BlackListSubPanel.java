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

package org.mfcrawler.view.panel.filters;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.SortedListModel;
import org.mfcrawler.view.panel.DefaultPanel;
import org.mfcrawler.view.panel.DefaultSubPanel;


/**
 * SubPanel for blacklist domains
 * 
 * @author lbertelo
 */
public class BlackListSubPanel extends DefaultSubPanel implements IFiltersParams {

	private JPanel panel;
	private JTextField domainTextField;
	private JButton addButton;
	private JButton delButton;
	private JList<Domain> domainJList;
	private SortedListModel<Domain> domainListModel;
	private Set<Domain> blacklistDomains;

	public BlackListSubPanel(DefaultPanel parentPanel) {
		super(parentPanel, I18nUtil.getMessage("filters.blacklist"));
		blacklistDomains = new HashSet<Domain>();
	}

	public void setBlacklistDomains(Set<Domain> blacklistDomains) {
		this.blacklistDomains = new HashSet<Domain>(blacklistDomains);

		domainListModel = new SortedListModel<Domain>();
		for (Domain domain : blacklistDomains) {
			domainListModel.addElement(domain);
		}
		domainJList.setModel(domainListModel);
	}

	public void addBlacklistDomain(String domainStr) {
		Link link = Link.parseUrl(domainStr);
		if (link != null) {
			blacklistDomains.add(link.getDomain());
			domainListModel.addElement(link.getDomain());
		}
	}

	public Set<Domain> getBlacklistDomains() {
		return new HashSet<Domain>(blacklistDomains);
	}

	@Override
	protected JPanel buildContent() {
		JPanel tempPanel;
		panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));

		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		JPanel subTempPanel = new JPanel();
		JLabel siteBlacklistLabel = new JLabel(I18nUtil.getMessage("filters.blacklist.site"));
		subTempPanel.add(siteBlacklistLabel);
		domainTextField = new JTextField("", DEFAULT_SITE_COLUMNS);
		domainTextField.addActionListener(new AddAction());
		subTempPanel.add(domainTextField);
		tempPanel.add(subTempPanel);

		addButton = new JButton(I18nUtil.getMessage("general.add"));
		addButton.addActionListener(new AddAction());
		tempPanel.add(addButton);

		delButton = new JButton(I18nUtil.getMessage("general.delete"));
		delButton.addActionListener(new DeleteAction());
		delButton.setEnabled(false);
		tempPanel.add(delButton);

		panel.add(tempPanel, BorderLayout.PAGE_START);

		tempPanel = new JPanel();
		tempPanel.setLayout(new BorderLayout());
		domainJList = new JList<Domain>();
		domainJList.addListSelectionListener(new SelectedValueAction());
		JScrollPane tmp_scroll = new JScrollPane(domainJList);
		tempPanel.add(tmp_scroll);
		panel.add(tempPanel, BorderLayout.CENTER);

		return panel;
	}

	// Listener Classes

	private class SelectedValueAction implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (domainJList.getSelectedValue() instanceof Domain) {
				domainTextField.setText(domainJList.getSelectedValue().toString());
				delButton.setEnabled(true);
			} else {
				domainTextField.setText("");
				delButton.setEnabled(false);
			}
		}
	}

	private class AddAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String valueTextField = domainTextField.getText();
			Link link = Link.parseUrl(valueTextField);
			if (link != null && !blacklistDomains.contains(link.getDomain())) {
				blacklistDomains.add(link.getDomain());
				domainListModel.addElement(link.getDomain());
			}
		}
	}

	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (domainJList.getSelectedValue() instanceof Domain) {
				blacklistDomains.remove(domainJList.getSelectedValue());
				domainListModel.removeElement(domainJList.getSelectedValue());
				domainTextField.setText("");
			}
		}
	}

}
