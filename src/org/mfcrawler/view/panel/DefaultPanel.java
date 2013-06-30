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
import java.awt.Font;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.view.ApplicationView;

/**
 * Abstract class for panel
 * 
 * @author lbertelo
 */
public abstract class DefaultPanel implements PropertyChangeListener {

	private JPanel panel;
	private String title;
	private String description;
	private ApplicationView view;
	private ApplicationModel model;

	public DefaultPanel(ApplicationView view, ApplicationModel model, String title, String description) {
		this.view = view;
		this.model = model;
		this.title = title;
		this.description = description;

		panel = new JPanel(new BorderLayout());
		panel.add(buildHeader(), BorderLayout.PAGE_START);
		panel.add(buildContent(), BorderLayout.CENTER);
	}

	protected ApplicationView getView() {
		return view;
	}

	public ApplicationModel getModel() {
		return model;
	}

	public void addToTabbedPane(JTabbedPane tabbedPane) {
		tabbedPane.addTab(title, panel);
	}

	protected final JPanel buildHeader() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JLabel labelTitle = new JLabel(title);
		Font fontBold = ((FontUIResource) UIManager.get("Label.font")).deriveFont(Font.BOLD);
		labelTitle.setFont(fontBold);
		panel.add(labelTitle);

		JLabel labelDescription = new JLabel(" : " + description);
		panel.add(labelDescription);

		return panel;
	}

	protected abstract JComponent buildContent();

}
