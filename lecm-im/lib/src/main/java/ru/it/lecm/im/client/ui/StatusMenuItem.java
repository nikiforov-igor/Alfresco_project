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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StatusMenuItem extends Composite {

	private static StatusMenuItemUiBinder uiBinder = GWT
			.create(StatusMenuItemUiBinder.class);

	interface StatusMenuItemUiBinder extends UiBinder<Widget, StatusMenuItem> {
	}
	
	@UiField Element itemElement;
	@UiField Element imgElement;
	@UiField Element textElement;
	@UiField LiHTMLPanel itemContent;
	
	public StatusMenuItem(final String img,final String text,final StatusMenuItemListener listener) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		itemContent.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				setSelect(true);
				listener.onSelect();
			}
		});
		
		itemContent.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				itemContent.addStyleName("ijab-status-menuitem-hover");
			}
		});
		
		itemContent.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				itemContent.removeStyleName("ijab-status-menuitem-hover");
			}
		});
		
		
		setImg(img);
		setText(text);
	}
	
	public void setSelect(boolean b)
	{
		if(b)
			textElement.addClassName("ijab-status-menuitem-select");
		else
			textElement.removeClassName("ijab-status-menuitem-select");
	}
	
	public void setImg(final String img)
	{
		imgElement.setAttribute("src", img);
	}
	
	public void setText(final String text)
	{
		textElement.setInnerText(text);
		itemElement.setAttribute("title", text);
	}

}
