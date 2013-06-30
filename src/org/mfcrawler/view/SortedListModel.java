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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * ListModel for JList
 * 
 * @author lbertelo
 * @param <E> the type of the elements of this model
 */
public class SortedListModel<E extends Comparable<E>> extends AbstractListModel<E> {

	private static final long serialVersionUID = 1L;

	private List<E> dataList;

	public SortedListModel() {
		dataList = new ArrayList<E>();
	}

	@Override
	public int getSize() {
		return dataList.size();
	}

	@Override
	public E getElementAt(int index) {
		if (index >= 0 && index < dataList.size()) {
			return dataList.get(index);
		} else {
			return null;
		}
	}

	public void addElement(E element) {
		dataList.add(element);
		Collections.sort(dataList);
		fireContentsChanged(this, 0, dataList.size() - 1);
		fireIntervalAdded(this, dataList.size() - 1, dataList.size());
	}

	public void removeElement(E element) {
		int index = dataList.indexOf(element);
		dataList.remove(element);
		fireIntervalRemoved(this, index, index);
	}

	public List<E> getElements() {
		return dataList;
	}
}
