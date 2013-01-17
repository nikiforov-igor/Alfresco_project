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
package ru.it.lecm.im.client.ui.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.ui.listeners.BarButtonListener;
import ru.it.lecm.im.client.ui.listeners.ButtonPopupWindowListener;
import ru.it.lecm.im.client.ui.FocusHTMLPanel;
import ru.it.lecm.im.client.ui.SimpleFocusWidget;
import ru.it.lecm.im.client.utils.i18n;

import java.util.ArrayList;

public class BarButton extends Composite {

	private static BarButtonUiBinder uiBinder = GWT
			.create(BarButtonUiBinder.class);

	interface BarButtonUiBinder extends UiBinder<Widget, BarButton> {
	}
	
	@UiField FlowPanel button;
	@UiField
    FocusHTMLPanel buttonFocus;
	@UiField Element buttonStateElement;
	@UiField Element tipDIVElement;
	@UiField Element tipElement;
	@UiField Element closeElement;
	@UiField Element closePicElement;
	@UiField Element countElement;
	@UiField Element iconElement;
	@UiField Element textElement;
	@UiField
    ButtonPopupWindow window;
	
	//private SimpleFocusWidget button;
	private SimpleFocusWidget closeButton;
	private ArrayList<BarButtonListener> listeners = new ArrayList<BarButtonListener>();
	
	private boolean tipEnabled = true;

	public BarButton() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		closeElement.setAttribute("title", i18n.msg("Close"));
		closeButton = new SimpleFocusWidget(closeElement);
		initButton();
	}
	
	public void setTipEnabled(boolean tipEnable)
	{
		this.tipEnabled = tipEnable;
	}
	
	public void setCloseEnabled(boolean closeEnabled)
	{
		closeButton.setVisible(closeEnabled);
		setButtonWindowCloseEnabled(closeEnabled);
	}
	
	public void setTip(final String tip)
	{
		tipElement.setInnerText(tip);
	}
	
	public void setCountEnabled(boolean countEnabled)
	{
		if(countEnabled)
			countElement.getStyle().setDisplay(Display.BLOCK);
		else
			countElement.getStyle().setDisplay(Display.NONE);
	}
	
	public void setCount(int count)
	{
		countElement.setInnerText(""+count);
	}
	
	public void setButtonTextEnabled(boolean textEnabled)
	{
		if(textEnabled)
			textElement.getStyle().setDisplay(Display.INLINE);
		else
			textElement.getStyle().setDisplay(Display.NONE);
	}
	
	public void setButtonText(final String text)
	{
		textElement.setInnerText(text);
		window.setCaption(text);
	}
	
	public void setButtonWidth(int width)
	{
		buttonStateElement.getStyle().setWidth(width, Unit.PX);
	}
	
	public void setButtonWidthEm(int width)
	{
		buttonStateElement.getStyle().setWidth(width, Unit.EM);
	}
	
	public void setIconStyle(final String style)
	{
		iconElement.addClassName(style);
	}
	
	public void removeIconStyle(final String style)
	{
		iconElement.removeClassName(style);
	}
	
	public void setButtonWindowCaption(final String caption)
	{
		window.setCaption(caption);
	}
	
	public void setButtonWindow(Widget widget)
	{
		window.setWidget(widget);
	}
	
	public void setButtonWindow(Widget widget,final String caption)
	{
		window.setWidget(widget);
		window.setCaption(caption);
	}
	
	public void setButtonWindowMaxEnabled(boolean b)
	{
		window.setMaximizable(b);
	}
	
	public void setButtonWindowMinEnabled(boolean b)
	{
		window.setMinimizable(b);
	}
	
	public void setButtonWindowCloseEnabled(boolean b)
	{
		window.setClosable(b);
	}
	
	public Widget getButtonWidget()
	{
		return window.getWidget();
	}
	
	public void addButtonStyle(final String style)
	{
		if(style.length() == 0 || style == null)
			return;
		button.addStyleName(style);
	}
	
	
	public void closeWindow()
	{
		button.removeStyleName("ijab-window-normal");
		button.removeStyleName("ijab-window-active");
		button.addStyleName("ijab-window-minimize");
		buttonStateElement.removeClassName("ui-state-active");
		fireOnWindowClose();
	}
	
	public void openWindow()
	{
		button.removeStyleName("ijab-window-minimize");
		button.addStyleName("ijab-window-normal");
		button.addStyleName("ijab-window-active");
		buttonStateElement.addClassName("ui-state-active");
		fireOnWindowOpen();
	}
	
	public void addWidgetListener(BarButtonListener l)
	{
		listeners.add(l);
	}
	
	public void removeWidgetListener(BarButtonListener l)
	{
		listeners.remove(l);
	}
	
	public void setHighlight(boolean b)
	{
		if(b)
			buttonStateElement.addClassName("ui-state-highlight");
		else
			buttonStateElement.removeClassName("ui-state-highlight");
	}
	
	// local function
	private void initButton()
	{
		buttonFocus.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{				
				mouseClick();
			}
		});
		
		buttonFocus.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				mouseOut();
			}
		});
		
		buttonFocus.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				mouseOver();
			}
		});
		
		window.addListener(new ButtonPopupWindowListener()
		{

			public void onClose() 
			{				
				fireOnClose();
			}

			public void onMax() 
			{				
				fireOnMax();
			}

			public void onMin() 
			{
				closeWindow();
			}
		});
		
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				fireOnClose();				
			}
		});
		closeButton.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) 
			{
				closePicElement.addClassName("ijab-actions-prompt");
			}
		});
		closeButton.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) 
			{
				closePicElement.removeClassName("ijab-actions-prompt");
			}
		});
	}
	
	private void mouseClick()
	{
        ToggleWindow();
    }

    public void ToggleWindow() {
        if(!button.getStyleName().contains("ijab-window-minimize"))
            closeWindow();
        else
            openWindow();
    }

    private void mouseOver()
	{
		buttonStateElement.addClassName("ui-state-hover");
		if(tipEnabled)
			tipDIVElement.getStyle().setDisplay(Display.BLOCK);
	}
	
	private void mouseOut()
	{
		buttonStateElement.removeClassName("ui-state-hover");
		if(tipEnabled)
			tipDIVElement.getStyle().setDisplay(Display.NONE);
	}
	
	private void fireOnMax()
	{
		for(BarButtonListener l:listeners)
		{
			l.onMax();
		}
	}
	
	private void fireOnClose()
	{
		onButtonClose();
		for(BarButtonListener l:listeners)
		{
			l.onClose();
		}
	}
	
	private void fireOnWindowOpen()
	{
		for(BarButtonListener l:listeners)
		{
			l.onWindowOpen();
		}
	}
	
	private void fireOnWindowClose()
	{
		onWindowClose();
		for(BarButtonListener l:listeners)
		{
			l.onWindowClose();
		}
	}
	
	public boolean isActive()
	{
		return button.getStyleName().contains("ijab-window-active");
	}
	
	protected void onWindowClose()
	{
		
	}
	
	protected void onButtonClose()
	{
		
	}
	
	public FocusHTMLPanel getButton()
	{
		return this.buttonFocus;
	}
}
