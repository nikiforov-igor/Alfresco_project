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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.utils.i18n;

public class ButtonPopupWindow extends Composite {

	private static ButtonPopWindowUiBinder uiBinder = GWT
			.create(ButtonPopWindowUiBinder.class);

	interface ButtonPopWindowUiBinder extends UiBinder<Widget, ButtonPopupWindow> {
	}
	
	@UiField Element minElement;
	@UiField Element maxElement;
	@UiField Element closeElement;
	@UiField Element minPicElement;
	@UiField Element maxPicElement;
	@UiField Element closePicElement;
	@UiField Element captionElement;
	@UiField FlowPanel contentWrap;
	
	
	private SimpleFocusWidget minButton;
	private SimpleFocusWidget maxButton;
	private SimpleFocusWidget closeButton;
	
	private Widget contentWidget;
	private List<ButtonPopupWindowListener> listeners = new ArrayList<ButtonPopupWindowListener>();

	public ButtonPopupWindow() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		minButton = new SimpleFocusWidget(minElement);
		minButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				clickMin();
			}
		});
		minButton.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				overMin();
			}
		});
		minButton.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				outMin();
			}
		});
		
		maxButton = new SimpleFocusWidget(maxElement);
		maxButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				clickMax();
			}
		});
		maxButton.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				overMax();
			}
		});
		maxButton.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				outMax();
			}
		});
		
		closeButton = new SimpleFocusWidget(closeElement);
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				clickClose();
			}
		});
		closeButton.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				overClose();
			}
		});
		closeButton.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				outClose();
			}
		});		
		
		//for i18n
		minPicElement.setInnerText(i18n.msg("Minimize"));
		minElement.setAttribute("title", i18n.msg("Minimize"));
		
		maxPicElement.setInnerText(i18n.msg("Maximize"));
		maxElement.setAttribute("title", i18n.msg("Maximize"));
		
		closePicElement.setInnerText(i18n.msg("Close"));
		closeElement.setAttribute("title", i18n.msg("Close"));
	}
	
	private void fireOnClose()
	{
		for(ButtonPopupWindowListener l:listeners)
		{
			l.onClose();
		}
	}
	
	private void fireOnMin()
	{
		for(ButtonPopupWindowListener l:listeners)
		{
			l.onMin();
		}
	}
	
	private void fireOnMax()
	{
		for(ButtonPopupWindowListener l:listeners)
		{
			l.onMax();
		}
	}
	
	private void clickMin()
	{
		fireOnMin();
	}
	
	private void overMin()
	{
		minPicElement.addClassName("ijab-actions-hover");
	}
	
	private void outMin()
	{
		minPicElement.removeClassName("ijab-actions-hover");
	}
	
	private void clickMax()
	{
		fireOnMax();
	}
	
	private void overMax()
	{
		maxPicElement.addClassName("ijab-actions-hover");
	}
	
	private void outMax()
	{
		maxPicElement.removeClassName("ijab-actions-hover");
	}
	
	private void clickClose()
	{
		fireOnClose();
	}
	
	private void overClose()
	{
		closePicElement.addClassName("ijab-actions-prompt");
	}
	
	private void outClose()
	{
		closePicElement.removeClassName("ijab-actions-prompt");
	}
	
	public void setMaximizable(boolean maximizable)
	{
		if(maximizable)
			maxButton.setVisible(true);
		else
			maxButton.setVisible(false);
	}
	
	public void setMinimizable(boolean minimizable)
	{
		if(minimizable)
			minButton.setVisible(true);
		else
			minButton.setVisible(false);
	}
	
	public void setClosable(boolean closable) 
	{
		if(closable)
			closeButton.setVisible(true);
		else
			closeButton.setVisible(false);
	}
	
	public void addListener(ButtonPopupWindowListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(ButtonPopupWindowListener l)
	{
		listeners.remove(l);
	}
	
	public Widget getWidget()
	{
		return this.contentWidget;
	}	
	
	public void setWidget(Widget widget)
	{
		this.contentWidget = widget;
		contentWrap.add(contentWidget);
	}	
	
	public void setCaption(final String caption)
	{
		captionElement.setInnerText(caption);
	}
}
