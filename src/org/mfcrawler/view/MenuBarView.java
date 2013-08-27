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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.dialog.GeneralConfigDialog;
import org.mfcrawler.view.dialog.ManageProjectsDialog;
import org.mfcrawler.view.dialog.NewProjectDialog;
import org.mfcrawler.view.dialog.ProxyConfigDialog;

/**
 * Menu bar of the view
 * 
 * @author lbertelo
 */
public class MenuBarView {

	private ApplicationView view;
	private JMenuBar menuBar;

	public MenuBarView(ApplicationView view) {
		this.view = view;
		buildMenuBar();
	}

	public void setToJFrame(JFrame window) {
		window.setJMenuBar(menuBar);
	}

	private void buildMenuBar() {
		menuBar = new JMenuBar();
		Dimension menuItemSize = new Dimension(130, 20);

		JMenu projectMenu = new JMenu(I18nUtil.getMessage("menubar.project"));
		menuBar.add(projectMenu);
		JMenuItem clearProject = new JMenuItem(I18nUtil.getMessage("menubar.project.clear"));
		clearProject.setPreferredSize(menuItemSize);
		clearProject.addActionListener(new ClearProjectAction());
		projectMenu.add(clearProject);
		JMenuItem newProject = new JMenuItem(I18nUtil.getMessage("menubar.project.new"));
		newProject.addActionListener(new NewProjectAction());
		projectMenu.add(newProject);
		JMenuItem manageProject = new JMenuItem(I18nUtil.getMessage("menubar.project.manage"));
		manageProject.addActionListener(new ManageProjectsAction());
		projectMenu.add(manageProject);
		// FIXME Ajouter import et export des filtres
		projectMenu.add(new JSeparator());
		JMenuItem exit = new JMenuItem(I18nUtil.getMessage("menubar.exit"));
		exit.addActionListener(new ExitAction());
		projectMenu.add(exit);

		JMenu configMenu = new JMenu(I18nUtil.getMessage("menubar.configuration"));
		menuBar.add(configMenu);
		JMenuItem proxyConfig = new JMenuItem(I18nUtil.getMessage("menubar.configuration.proxy"));
		proxyConfig.setPreferredSize(menuItemSize);
		proxyConfig.addActionListener(new ProxyConfigAction());
		configMenu.add(proxyConfig);
		JMenuItem generalConfig = new JMenuItem(I18nUtil.getMessage("menubar.configuration.general"));
		generalConfig.setPreferredSize(menuItemSize);
		generalConfig.addActionListener(new GeneralConfigAction());
		configMenu.add(generalConfig);

		JMenu helpMenu = new JMenu(I18nUtil.getMessage("menubar.help"));
		menuBar.add(helpMenu);
		JMenuItem aboutHelp = new JMenuItem(I18nUtil.getMessage("menubar.help.about"));
		aboutHelp.setPreferredSize(menuItemSize);
		aboutHelp.addActionListener(new AboutHelpAction());
		helpMenu.add(aboutHelp);
	}

	// Project

	private class NewProjectAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!view.getModel().getSupervisor().isStopped()) {
				view.notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			} else {
				NewProjectDialog newProjectDialog = new NewProjectDialog(view, view.getModel());
				newProjectDialog.display();
			}
		}
	}

	private class ManageProjectsAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!view.getModel().getSupervisor().isStopped()) {
				view.notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			} else {
				ManageProjectsDialog manageProjectsDialog = new ManageProjectsDialog(view, view.getModel());
				manageProjectsDialog.display();
			}
		}
	}

	private class ClearProjectAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			int option = JOptionPane.showConfirmDialog(null, I18nUtil.getMessage("confirm.clearProject"),
					I18nUtil.getMessage("dialog.clearProject"), JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				view.getModel().clearProject();
			}
		}
	}

	private class ExitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			view.getFrame().dispose();
		}
	}

	// Config

	private class ProxyConfigAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!view.getModel().getSupervisor().isStopped()) {
				view.notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			} else {
				ProxyConfigDialog proxyConfigDialog = new ProxyConfigDialog(view, view.getModel());
				proxyConfigDialog.display();
			}
		}
	}

	private class GeneralConfigAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!view.getModel().getSupervisor().isStopped()) {
				view.notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			} else {
				GeneralConfigDialog generalConfigDialog = new GeneralConfigDialog(view, view.getModel());
				generalConfigDialog.display();
			}
		}
	}

	// Help

	private class AboutHelpAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			view.notifyDialog(IPropertyName.INFO, I18nUtil.getMessage("window.about"));
		}
	}

}
