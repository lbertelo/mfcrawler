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
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Dialog for loading
 * 
 * @author lbertelo
 */
public class LoadingDialog extends DefaultDialog {

	private static final String IMG_PATH = "/org/mfcrawler/resources/loader.gif";

	private JDialog dialog;
	private JLabel imgLoading;
	private JLabel textLoading;

	public LoadingDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);
	}

	public void display(String textValue) {
		getView().getFrame().setEnabled(false);
		textLoading.setText(textValue);
		display();
	}

	public void hide() {
		super.hide();
		getView().getFrame().setEnabled(true);
	}

	public void replace() {
		dialog.setLocationRelativeTo(getView().getFrame());
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.loading"), false);
		dialog.setMinimumSize(new Dimension(250, 100));
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addComponentListener(new MyComponentListener());
		dialog.getContentPane().setLayout(new GridLayout(2, 1));

		imgLoading = new JLabel(new ImageIcon(getClass().getResource(IMG_PATH)));
		dialog.getContentPane().add(imgLoading);

		textLoading = new JLabel("...");
		textLoading.setVerticalAlignment(SwingConstants.CENTER);
		textLoading.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.getContentPane().add(textLoading);

		return dialog;
	}

	// Listener Class

	private class MyComponentListener extends ComponentAdapter {
		@Override
		public void componentMoved(ComponentEvent e) {
			replace();
		}
	}

}
