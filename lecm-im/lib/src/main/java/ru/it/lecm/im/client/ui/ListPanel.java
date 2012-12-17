/*
 * anzsoft.com
 * Copyright (C) 2005-2010 anzsoft.com <admin@anzsoft.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * Last modified by Fanglin Zhong<zhongfanglin@gmail.com>
 * Feb 2, 2010
 */
package ru.it.lecm.im.client.ui;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListPanel extends ComplexPanel implements InsertPanel
{
	public ListPanel()
	{
		setElement(DOM.createElement("ul"));
	}
	/**
	 * Adds a new child widget to the panel.
	 * 
	 * @param w the widget to be added
	 */
	@Override
	public void add(Widget w) {
		add(w, getElement());
	}

	@Override
	public void clear() 
	{
		Node child = getElement().getFirstChild();
		while (child != null) {
			getElement().removeChild(child);
			child = getElement().getFirstChild();
		}
	}

	/**
	 * Inserts a widget before the specified index.
	 * 
	 * @param w the widget to be inserted
	 * @param beforeIndex the index before which it will be inserted
	 * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
	 *           range
	 */
	public void insert(Widget w, int beforeIndex) {
		insert(w, getElement(), beforeIndex, true);
	}
}
