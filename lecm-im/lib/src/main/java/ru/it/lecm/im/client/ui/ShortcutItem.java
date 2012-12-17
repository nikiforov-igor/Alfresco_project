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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShortcutItem extends Composite {

	private static ShortcutItemUiBinder uiBinder = GWT
			.create(ShortcutItemUiBinder.class);

	interface ShortcutItemUiBinder extends UiBinder<Widget, ShortcutItem> {
	}
	
	private static final String HOVER_STYLE = "ijab_shortcutitem_hover";

	@UiField HTMLPanel item;
	@UiField Element tipDiv;
	@UiField Element iconEM;
	@UiField Element tip;
	@UiField Element tipA;
	
	SimpleFocusWidget aWidget;
	
	private String url;
	private String target = "_blank";
	String iconStyle;
	public ShortcutItem(final String url,final String target,final String tipStr,final String icon) 
	{
		this.url = url;
		this.target = target;
		initWidget(uiBinder.createAndBindUi(this));
		setTip(tipStr);
		iconEM.getStyle().setBackgroundImage("url("+icon+")");
		tipDiv.getStyle().setDisplay(Display.NONE);
		
		aWidget = new SimpleFocusWidget(tipA);
		aWidget.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event) 
			{
				handleClick(event);			
			}
		});
		
		aWidget.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				doMouseOver(event);
			}
		});
		
		aWidget.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				doMouseOut(event);
			}
		});
	}
	
	public ShortcutItem(final String url,final String tipStr,final String icon) 
	{
		this(url,"_blank",tipStr,icon);
	}
	
	void handleClick(ClickEvent e) 
	{
		Window.open(url, target, "");
		
	}
	
	void doMouseOver(MouseOverEvent event)
	{
		aWidget.addStyleName(HOVER_STYLE);
		tipDiv.getStyle().setDisplay(Display.BLOCK);
	}
	
	void doMouseOut(MouseOutEvent event) 
	{
		aWidget.removeStyleName(HOVER_STYLE);
		tipDiv.getStyle().setDisplay(Display.NONE);
	}
	
	public void setTip(final String tipStr)
	{
		tip.setInnerText(tipStr);
	}
	
	public void setIcon(final String icon)
	{
		iconEM.getStyle().setBackgroundImage("url("+icon+")");
	}
	
	public void setUrl(final String url)
	{
		this.url = url;
	}
	
	public void setTarget(final String target)
	{
		this.target = target;
	}
}
