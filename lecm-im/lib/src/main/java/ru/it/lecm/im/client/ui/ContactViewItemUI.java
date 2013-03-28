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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContactViewItemUI extends Composite {

	private static ContactViewItemUIUiBinder uiBinder = GWT
			.create(ContactViewItemUIUiBinder.class);

	interface ContactViewItemUIUiBinder extends
			UiBinder<Widget, ContactViewItemUI> {
	}

	@UiField Element nameElement;
	@UiField Element nameTextElement;
	@UiField Element msgCounterTextElement;
	@UiField Element statusIconElement;
	@UiField Element statusTextElement;
	@UiField FocusHTMLPanel mainWidget;

    public void clearActive()
    {
        mainWidget.removeStyleName("active-contact-view-item");
    }

    public void setActive()
    {
        mainWidget.addStyleName("active-contact-view-item");
    }

    public ContactViewItemUI(final String id)
	{
		initWidget(uiBinder.createAndBindUi(this));
		mainWidget.getElement().setId(id);
		mainWidget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				onItemClicked();
			}
		});
	}
	
	public String getWidgetID()
	{
		return mainWidget.getElement().getId();
	}
	
	public void setName(final String name)
	{
		nameTextElement.setInnerText(name);
	}
	
	public void setTitle(final String title)
	{
		mainWidget.setTitle(title);
		nameElement.setTitle(title);
	}
	
	public void setItemOdd(boolean b)
	{
		if(b)
			mainWidget.addStyleName("ijab-contactview-item-odd");
		else
			mainWidget.removeStyleName("ijab-contactview-item-odd");
	}
	
	public void setStatusText(final String status)
	{
		if(status == null||status.length()==0)
			nameElement.addClassName("names_nostatus");
		else
			nameElement.removeClassName("names_nostatus");
		statusTextElement.setInnerText(status);
	}

    public void setStatusIcon(final String url)
	{
		statusIconElement.setAttribute("src", url);
	}
	
	public void setOffline(boolean b)
	{
	}

    protected abstract void onItemClicked();

    public void setHighlight()
	{
		mainWidget.addStyleName("ijab-contactview-item-highlight");
	}
	
	public void removeHighlight()
	{
		mainWidget.removeStyleName("ijab-contactview-item-highlight");
	}
}
