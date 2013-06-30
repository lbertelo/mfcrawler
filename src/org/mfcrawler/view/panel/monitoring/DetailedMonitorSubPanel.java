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

package org.mfcrawler.view.panel.monitoring;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.DefaultSubPanel;
import org.mfcrawler.view.panel.MonitoringPanel;


/**
 * SubPanel for crawling details
 * 
 * @author lbertelo
 */
public class DetailedMonitorSubPanel extends DefaultSubPanel {

	private DefaultListModel<String> listModel;

	public DetailedMonitorSubPanel(MonitoringPanel monitoringPanel) {
		super(monitoringPanel, I18nUtil.getMessage("monitoring.detailedMonitoring"));
	}

	public void updateThreadInfo(Map<Integer, String> map) {
		for (Integer key : map.keySet()) {
			if (key >= listModel.getSize()) {
				listModel.add(key, map.get(key));
			} else if (map.get(key) != null) {
				listModel.set(key, map.get(key));
			} else if (key < listModel.size()) {
				listModel.remove(key);
			}
		}
	}
	
	@Override
	protected JPanel buildContent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JList<String> jlist = new JList<String>();
		listModel = new DefaultListModel<String>();
		jlist.setModel(listModel);
		JScrollPane scrollPane = new JScrollPane(jlist);

		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

}
