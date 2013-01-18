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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppsBar extends Composite {

	private static AppsBarUiBinder uiBinder = GWT.create(AppsBarUiBinder.class);

	interface AppsBarUiBinder extends UiBinder<Widget, AppsBar> {
	}

	@UiField FlowPanel appsBar;
	public AppsBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addWidget(Widget widget)
	{
		appsBar.add(widget);
	}
	
	public void removeWidget(Widget widget)
	{
		appsBar.remove(widget);
	}

}
