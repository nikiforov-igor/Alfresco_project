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
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.utils.i18n;

public class BarMainWidget extends Composite implements HasVisibility, EventListener, HasAttachHandlers, IsWidget, IsRenderable {

    private static BarMainWidgetUiBinder uiBinder = GWT
			.create(BarMainWidgetUiBinder.class);

	interface BarMainWidgetUiBinder extends UiBinder<Widget, BarMainWidget> {
	}

	@UiField ContactView contactView;
	@UiField UserIndicator indicator;
	@UiField HTMLPanel disconnected;
	@UiField SearchBox searchBox;
	@UiField Element disText;
	@UiField FlowPanel mainWidget;

	public BarMainWidget() 
	{
		initWidget(BarMainWidget.uiBinder.createAndBindUi(this));
		disText.setInnerText(i18n.msg("Отключен"));
	}
	
	public ContactView getContactView()
	{
		return contactView;
	}
	
	public UserIndicator getIndictorWidget()
	{
		return indicator;
	}
	
	public SearchBox getSearchWidget()
	{
		return searchBox;
	}
	
	public void setDisconnected(boolean b)
	{
		if(b)
		{
			disconnected.setVisible(true);
			contactView.setVisible(false);
			indicator.setVisible(false);
			searchBox.setVisible(false);
		}
		else
		{
			disconnected.setVisible(false);
			contactView.setVisible(true);
			indicator.setVisible(true);
			searchBox.setVisible(true);
		}
	}
	
	public void removeToolBar()
	{

	}
	
	public void setToolBarListener(IToolBarListener listener)
	{

	}

    public interface IToolBarListener
    {
        void addButtonClicked();
        void manageButtonClicked();
    }
}
