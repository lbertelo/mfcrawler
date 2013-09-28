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

package org.mfcrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.dialog.LoadingDialog;
import org.mfcrawler.view.panel.AnalyzePanel;
import org.mfcrawler.view.panel.ExportPanel;
import org.mfcrawler.view.panel.FiltersPanel;
import org.mfcrawler.view.panel.MonitoringPanel;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * View of the application
 * 
 * @author lbertelo
 */
public final class ApplicationView implements PropertyChangeListener {

	private static final Integer WINDOW_WIDTH = 750;
	private static final Integer WINDOW_HEIGHT = 600;
	private static final Integer SMALL_FONT = 12;
	private static final Integer LARGE_FONT = 13;

	private JFrame window;
	private ApplicationModel model;
	private boolean loading;
	private boolean closing;

	private LoadingDialog loadingDialog;

	public ApplicationView(ApplicationModel model) {
		this.model = model;
		window = new JFrame(I18nUtil.getMessage("window.title"));
		setViewParams();

		model.addListener(IPropertyName.ERROR, this);
		model.addListener(IPropertyName.WARNING, this);
		model.addListener(IPropertyName.INFO, this);
		model.addListener(IPropertyName.LOADING, this);
		model.addListener(IPropertyName.LOADED, this);
		model.addListener(IPropertyName.PROJECT_LOADED, this);

		loadingDialog = new LoadingDialog(this, model);

		loading = false;
		closing = false;

		buildContent();
	}

	public ApplicationModel getModel() {
		return model;
	}

	public void display() {
		window.setVisible(true);
		window.toFront();
	}

	public JFrame getFrame() {
		return window;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();

		if (propertyName.equals(IPropertyName.ERROR)) {
			String textValue = (String) event.getNewValue();
			JOptionPane.showMessageDialog(getFrame(), textValue, I18nUtil.getMessage("dialog.error"),
					JOptionPane.ERROR_MESSAGE);

		} else if (propertyName.equals(IPropertyName.WARNING)) {
			String textValue = (String) event.getNewValue();
			JOptionPane.showMessageDialog(getFrame(), textValue, I18nUtil.getMessage("dialog.warning"),
					JOptionPane.WARNING_MESSAGE);

		} else if (propertyName.equals(IPropertyName.INFO)) {
			String textValue = (String) event.getNewValue();
			JOptionPane.showMessageDialog(getFrame(), textValue, I18nUtil.getMessage("dialog.information"),
					JOptionPane.INFORMATION_MESSAGE);

		} else if (propertyName.equals(IPropertyName.LOADING)) {
			String textValue = (String) event.getNewValue();
			loading = true;
			loadingDialog.display(textValue);

		} else if (propertyName.equals(IPropertyName.LOADED)) {
			loading = false;
			loadingDialog.hide();
			if (closing) {
				window.dispose();
			}

		} else if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			CrawlProject crawlProject = (CrawlProject) event.getNewValue();
			window.setTitle(I18nUtil.getMessage("window.title") + " : " + crawlProject.getName());

		}
	}

	public void notifyDialog(String propertyName, String textValue) {
		if (propertyName.equals(IPropertyName.ERROR) || propertyName.equals(IPropertyName.WARNING)
				|| propertyName.equals(IPropertyName.INFO) || propertyName.equals(IPropertyName.LOADING)
				|| propertyName.equals(IPropertyName.LOADED)) {
			getModel().notify(propertyName, textValue);
		}
	}

	private void setViewParams() {
		window.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		window.getContentPane().setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new MyWindowListener());
		window.addComponentListener(new MyComponentListener());

		UIManager.put("TabbedPane.font", new FontUIResource("Dialog", Font.BOLD, LARGE_FONT));
		UIManager.put("Button.font", new FontUIResource("Dialog", Font.BOLD, LARGE_FONT));
		UIManager.put("TitledBorder.font", new FontUIResource("Dialog", Font.ITALIC, SMALL_FONT));
		UIManager.put("Label.font", new FontUIResource("Dialog", Font.PLAIN, SMALL_FONT));
		UIManager.put("CheckBox.font", new FontUIResource("Dialog", Font.PLAIN, SMALL_FONT));
	}

	private void buildContent() {
		MenuBarView menuBarView = new MenuBarView(this);
		menuBarView.setToJFrame(window);

		JTabbedPane tabbedPane = new JTabbedPane();

		MonitoringPanel monitoring = new MonitoringPanel(this, getModel());
		monitoring.addToTabbedPane(tabbedPane);

		FiltersPanel filters = new FiltersPanel(this, getModel());
		filters.addToTabbedPane(tabbedPane);

		OverviewPanel overview = new OverviewPanel(this, getModel());
		overview.addToTabbedPane(tabbedPane);

		AnalyzePanel analyse = new AnalyzePanel(this, getModel());
		analyse.addToTabbedPane(tabbedPane);

		ExportPanel export = new ExportPanel(this, getModel());
		export.addToTabbedPane(tabbedPane);

		window.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}

	// Listener Class

	private class MyWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			if (!loading) {
				if (getModel().getSupervisor().isStopped()) {
					getModel().closeModel();
					closing = true;
				} else {
					notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("window.closingWarning"));
				}
			}
		}
	}

	private class MyComponentListener extends ComponentAdapter {
		@Override
		public void componentMoved(ComponentEvent e) {
			loadingDialog.replace();
		}
	}

}
