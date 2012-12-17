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
 * Mar 1, 2010
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.utils.i18n;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class CollapseButton extends Composite {

	private static CollapseButtonUiBinder uiBinder = GWT
			.create(CollapseButtonUiBinder.class);

	interface CollapseButtonUiBinder extends UiBinder<Widget, CollapseButton> {
	}

	@UiField FocusHTMLPanel widget;
	@UiField Element widgetIcon;
	@UiField Element tipElement;
	@UiField Element tipDIVElement;
	public CollapseButton() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		//widgetIcon.setAttribute("title", i18n.msg("Collapse"));
		widget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(widgetIcon.getClassName().contains("ui-icon-circle-arrow-w"))
				{
					setExpand(true);
				}
				else
				{
					setExpand(false);
				}
			}
		});
		
		widget.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				tipDIVElement.getStyle().setDisplay(Display.BLOCK);
			}
		});
		
		widget.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				tipDIVElement.getStyle().setDisplay(Display.NONE);
			}
			
		});
		setExpand(false);
	}
	
	public void setExpand(boolean b)
	{
		if(b)
		{
			widgetIcon.addClassName("ui-icon-circle-arrow-e");
			widgetIcon.removeClassName("ui-icon-circle-arrow-w");
			setTip(i18n.msg("Collapse"));
		}
		else
		{
			widgetIcon.addClassName("ui-icon-circle-arrow-w");
			widgetIcon.removeClassName("ui-icon-circle-arrow-e");
			setTip(i18n.msg("Expand"));
		}
	}
	
	public void setTip(final String tip)
	{
		tipElement.setInnerText(tip);
	}
	
	public FocusHTMLPanel getWidget()
	{
		return widget;
	}

}
