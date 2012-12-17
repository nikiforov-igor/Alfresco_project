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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class StatusSelector extends Composite {

	private static StatusSelectorUiBinder uiBinder = GWT
			.create(StatusSelectorUiBinder.class);

	interface StatusSelectorUiBinder extends UiBinder<Widget, StatusSelector> {
	}

	@UiField Image img;
	@UiField FocusFlowPanel button;
	public StatusSelector() {
		initWidget(uiBinder.createAndBindUi(this));
		button.setStyleName("ijab-status-selector");
		button.setStylePrimaryName("ijab-status-selector");
		button.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				button.addStyleDependentName("hover");
				button.addStyleName("ui-corner-all");
			}
		});
		
		button.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				button.removeStyleDependentName("hover");
				button.removeStyleName("ui-corner-all");
			}
		});
	}
	
	public void setUrl(final String url)
	{
		img.setUrl(url);
	}

	public void addClickHandler(ClickHandler clickHandler) 
	{
		button.addClickHandler(clickHandler);
	}
	

}
