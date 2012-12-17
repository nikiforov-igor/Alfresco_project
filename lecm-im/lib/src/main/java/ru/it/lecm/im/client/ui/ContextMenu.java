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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.MenuBar;

public class ContextMenu extends ContextMenuUI
{
	private final MenuBar menuBody;
	private Object data = null;
	List<ContextMenuItem> items = new ArrayList<ContextMenuItem>();
	public ContextMenu()
	{
		menuBody = new MenuBar(true);
		menuBody.setStylePrimaryName("ijab-contextmenu-body");
		menuBody.setAnimationEnabled(true);
		setWidget(menuBody);
		this.addCloseHandler(new CloseHandler<ContextMenuUI>()
		{
			public void onClose(CloseEvent<ContextMenuUI> event) 
			{
				if(data!=null&&data instanceof ContactViewItem)
				{
					ContactViewItem item = (ContactViewItem)data;
					item.removeHighlight();
					for(ContextMenuItem menuItem:items)
					{
						menuItem.clearSelection();
					}
				}
			}
		});
	}
	
	public void addSeparator()
	{
		menuBody.addSeparator();
	}
	
	public void addItem(ContextMenuItem item)
	{
		items.add(item);
		menuBody.addItem(item);
	}
	
	public void addItem(String text,MenuBar subMenu)
	{
		menuBody.addItem(text, subMenu);
	}
	
	public void removeItem(ContextMenuItem item)
	{
		items.remove(item);
		menuBody.removeItem(item);
	}
	
	public void setData(Object o)
	{
		if(o!=null&&o instanceof ContactViewItem)
		{
			ContactViewItem item = (ContactViewItem)o;
			item.setHighlight();
		}
		this.data = o;
	}
	
	public Object getData()
	{
		return this.data;
	}
	
	public void clear()
	{
		menuBody.clearItems();
	}
}
