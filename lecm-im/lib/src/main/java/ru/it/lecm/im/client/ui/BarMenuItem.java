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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BarMenuItem extends Composite {

	private static BarMenuItemUiBinder uiBinder = GWT
			.create(BarMenuItemUiBinder.class);

	interface BarMenuItemUiBinder extends UiBinder<Widget, BarMenuItem> {
	}
	
	@UiField Element itemElement;
	@UiField Element imgElement;
	@UiField Element textElement;
	@UiField LiHTMLPanel itemContent;
	

	private final SimpleFocusWidget itemWidget;
	
	private String target;
	private String href;
	public BarMenuItem(final String href,final String target,final String img,final String text)
	{
		initWidget(uiBinder.createAndBindUi(this));
		itemWidget = new SimpleFocusWidget(itemElement);
		itemWidget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				doClick();
			}
		});
		
		setHref(href);
		setTarget(target);
		setImg(img);
		setText(text);
	}
	
	private void doClick()
	{
		if(target.equals("embed"))
		{
			//TODO open the url in embed dialog
		}
		else
		{
			if(target==null||target.length() == 0)
				target = "_blank";
			Window.open(href, target, "");
		}
	}
	
	public void setTarget(final String target)
	{
		this.target = target;
	}
	
	public void setHref(final String href)
	{
		this.href = href;
		//itemWidget.getElement().setAttribute("href", href);
	}
	
	public void setImg(final String img)
	{
		imgElement.setAttribute("src", img);
	}
	
	public void setText(final String text)
	{
		textElement.setInnerText(text);
	}
	
	
	

}
