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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ChatPanelBarUI extends Composite 
{

	private static ChatPanelUiBinder uiBinder = GWT
			.create(ChatPanelUiBinder.class);

	interface ChatPanelUiBinder extends UiBinder<Widget, ChatPanelBarUI> {
	}

	
	@UiField Element nextElement;
	@UiField Element nextMsgCountElement;
	@UiField Element nextClickElement;
	@UiField Element nextChatCountElement;
	
	@UiField FlowPanel chatsContent;
	
	@UiField Element prevElement;
	@UiField Element prevMsgCountElement;
	@UiField Element prevClickElement;
	@UiField Element prevChatCountElement;
	
	private SimpleFocusWidget nextClickButton;
	private SimpleFocusWidget prevClickButton;
	
	public ChatPanelBarUI() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		nextClickButton = new SimpleFocusWidget(nextClickElement);
		nextClickButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{	
				if(nextElement.getClassName().contains("ui-state-disabled"))
					return;
				nextClicked();
			}
		});
		
		prevClickButton = new SimpleFocusWidget(prevClickElement);
		prevClickButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(prevElement.getClassName().contains("ui-state-disabled"))
					return;
				prevClicked();
			}
			
		});
	}
	
	protected void setNextMsgCount(int count)
	{
		if(count>0)
		{
			nextMsgCountElement.getStyle().setDisplay(Display.BLOCK);
			nextMsgCountElement.setInnerText(""+count);
		}
		else
		{
			nextMsgCountElement.getStyle().setDisplay(Display.NONE);
			nextMsgCountElement.setInnerText(""+count);
		}
	}
	
	protected void setPrevMsgCount(int count)
	{
		if(count>0)
		{
			prevMsgCountElement.getStyle().setDisplay(Display.BLOCK);
			prevMsgCountElement.setInnerText(""+count);
		}
		else
		{
			prevMsgCountElement.getStyle().setDisplay(Display.NONE);
			prevMsgCountElement.setInnerText(""+count);
		}
	}
	
	protected void setNextChatCount(int count)
	{
		if(count>0)
			nextElement.removeClassName("ui-state-disabled");
		else
			nextElement.addClassName("ui-state-disabled");
		nextChatCountElement.setInnerText(""+count);
	}
	
	protected void setPrevChatCount(int count)
	{
		if(count>0)
			prevElement.removeClassName("ui-state-disabled");
		else
			prevElement.addClassName("ui-state-disabled");
		prevChatCountElement.setInnerText(""+count);
	}
	
	protected void setScrollVisible(boolean b)
	{
		if(b)
		{
			nextElement.getStyle().setDisplay(Display.BLOCK);
			prevElement.getStyle().setDisplay(Display.BLOCK);
		}
		else
		{
			nextElement.getStyle().setDisplay(Display.NONE);
			prevElement.getStyle().setDisplay(Display.NONE);
		}
	}
	
	protected int getWidgetCount()
	{
		return chatsContent.getWidgetCount();
	}
	
	protected Widget getWidget(int index)
	{
		return chatsContent.getWidget(index);
	}
	
	protected void addWidget(Widget widget)
	{
		chatsContent.add(widget);
	}
	
	protected void insertWidget(Widget widget,int beforeIndex)
	{
		chatsContent.insert(widget, beforeIndex);
	}
	
	protected boolean containsWidget(Widget widget)
	{
		return (chatsContent.getWidgetIndex(widget)!=-1);
	}
	
	protected void removeWidget(Widget widget)
	{
		chatsContent.remove(widget);
	}
	
	protected void clear()
	{
		chatsContent.clear();
	}
	
	protected abstract void nextClicked();
	protected abstract void prevClicked();

}
