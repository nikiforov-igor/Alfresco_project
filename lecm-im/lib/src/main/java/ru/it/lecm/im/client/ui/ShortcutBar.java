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

public class ShortcutBar extends Composite {

	private static ShortcutBarUiBinder uiBinder = GWT
			.create(ShortcutBarUiBinder.class);

	interface ShortcutBarUiBinder extends UiBinder<Widget, ShortcutBar> {
	}
	
	@UiField FlowPanel bar;

	public ShortcutBar() 
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addShortcutItem(final String url,final String target,final String tipStr,final String icon)
	{
		bar.add(new ShortcutItem(url,target,tipStr,icon));
	}
	
	public void addShortcutItem(final String url,final String tipStr,final String icon)
	{
		bar.add(new ShortcutItem(url,tipStr,icon));
	}
	
	public void addWidget(Widget widget)
	{
		bar.add(widget);
	}

}
