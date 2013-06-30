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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.mfcrawler.view.ApplicationView;

/**
 * Abstract class for subPanel
 * 
 * @author lbertelo
 */
public abstract class DefaultSubPanel {

	private JPanel panel;
	private DefaultPanel parentPanel;

	public DefaultSubPanel(DefaultPanel parentPanel, String title) {
		this.parentPanel = parentPanel;

		panel = buildContent();
		panel.setBorder(BorderFactory.createTitledBorder(title));
	}

	public DefaultPanel getParentPanel() {
		return parentPanel;
	}

	public ApplicationView getView() {
		return parentPanel.getView();
	}

	public JPanel getPanel() {
		return panel;
	}

	protected abstract JPanel buildContent();
}
