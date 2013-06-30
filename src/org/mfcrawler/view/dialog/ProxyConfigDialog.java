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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.pojo.ApplicationConfig;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Dialog for proxy configuration
 * 
 * @author lbertelo
 */
public class ProxyConfigDialog extends DefaultDialog {

	private static final int DIALOG_WIDTH = 250;
	private static final int DIALOG_HEIGHT = 200;
	private static final int HOST_PROXY_COLUMNS = 13;
	private static final int PORT_PROXY_COLUMNS = 3;

	private JDialog dialog;
	private JCheckBox enableProxyCheckBox;
	private JTextField hostProxyTextField;
	private JTextField portProxyTextField;

	public ProxyConfigDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);
	}

	@Override
	public void display() {
		ApplicationConfig config = ApplicationModel.getConfig();

		enableProxyCheckBox.setSelected(config.getProxyUse());
		hostProxyTextField.setText(config.getProxyHost());
		hostProxyTextField.setEnabled(config.getProxyUse());
		portProxyTextField.setText(config.getProxyPort().toString());
		portProxyTextField.setEnabled(config.getProxyUse());

		super.display();
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.proxyConfig"), true);
		dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		enableProxyCheckBox = new JCheckBox(I18nUtil.getMessage("dialog.proxyConfig.enableProxy"));
		enableProxyCheckBox.addActionListener(new EnableAction());
		tempPanel.add(enableProxyCheckBox);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel hostLabel = new JLabel(I18nUtil.getMessage("dialog.proxyConfig.host"));
		tempPanel.add(hostLabel);
		hostProxyTextField = new JTextField(HOST_PROXY_COLUMNS);
		tempPanel.add(hostProxyTextField);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel portLabel = new JLabel(I18nUtil.getMessage("dialog.proxyConfig.port"));
		tempPanel.add(portLabel);
		portProxyTextField = new JTextField(PORT_PROXY_COLUMNS);
		tempPanel.add(portProxyTextField);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton applyButton = new JButton(I18nUtil.getMessage("general.ok"));
		applyButton.addActionListener(new ApplyAction());
		tempPanel.add(applyButton);
		JButton cancelButton = new JButton(I18nUtil.getMessage("general.cancel"));
		cancelButton.addActionListener(new CancelAction());
		tempPanel.add(cancelButton);
		dialog.getContentPane().add(tempPanel);

		return dialog;
	}

	// Listener Classes

	private class ApplyAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ApplicationConfig config = ApplicationModel.getConfig();

			config.setProxyUse(enableProxyCheckBox.isSelected());
			config.setProxyHost(hostProxyTextField.getText());
			Integer proxyPort;
			try {
				proxyPort = Integer.valueOf(portProxyTextField.getText());
			} catch (NumberFormatException ex) {
				proxyPort = 3128;
			}
			config.setProxyPort(proxyPort);

			config.refreshProxy();
			hide();
		}
	}

	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			hide();
		}
	}

	private class EnableAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean proxyUse = enableProxyCheckBox.isSelected();
			hostProxyTextField.setEnabled(proxyUse);
			portProxyTextField.setEnabled(proxyUse);
		}
	}

}
