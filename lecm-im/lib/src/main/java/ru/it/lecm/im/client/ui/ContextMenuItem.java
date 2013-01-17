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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import ru.it.lecm.im.client.ui.listeners.ContextMenuItemListener;

public class ContextMenuItem extends MenuItem
{
	final private ContextMenu menu;
	public ContextMenuItem(final ContextMenu menu,String text, boolean asHTML,final ContextMenuItemListener listener)
	{
		super(text, asHTML,new Command()
		{

			public void execute() 
			{
				menu.hide();
				listener.onSelected(menu.getData());
			}
			
		});
		this.menu = menu;
		this.setStylePrimaryName("ijab-contextmenu-item");
	}
	
	public ContextMenuItem(final ContextMenu menu,String text,MenuBar subMenu)
	{
		super(text,true,subMenu);
		this.menu = menu;
		this.setStylePrimaryName("ijab-contextmenu-item");
	}
	
	public ContextMenu getMenu()
	{
		return menu;
	}
	
	public void clearSelection()
	{
		setSelectionStyle(false);
	}
}
