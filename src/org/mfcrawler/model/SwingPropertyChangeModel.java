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

package org.mfcrawler.model;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Allows to notify messages in the swing thread
 * 
 * @author lbertelo
 */
public class SwingPropertyChangeModel {

	/**
	 * The swing property change support
	 */
	private SwingPropertyChangeSupport propertyChangeSupport;

	/**
	 * Default constructor
	 */
	public SwingPropertyChangeModel() {
		propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
	}

	/**
	 * Adds a listener to a property
	 * @param propertyName the name of the property
	 * @param listener the property change listener
	 */
	public void addListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Notifies a message to a property
	 * @param propertyName the name of the property
	 * @param newValue the message to notify
	 */
	public void notify(final String propertyName, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, null, newValue);
	}

}
