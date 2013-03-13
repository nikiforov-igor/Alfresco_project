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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.ui.listeners.BarButtonListener;
import ru.it.lecm.im.client.ui.listeners.ButtonPopupWindowListener;
import ru.it.lecm.im.client.ui.SimpleFocusWidget;

import java.util.ArrayList;

public class BarButton extends Composite {

	private static BarButtonUiBinder uiBinder = GWT
			.create(BarButtonUiBinder.class);

	interface BarButtonUiBinder extends UiBinder<Widget, BarButton> {
	}
	
	@UiField FlowPanel button;
	@UiField ButtonPopupWindow window;

    private ArrayList<BarButtonListener> listeners = new ArrayList<BarButtonListener>();

    public BarButton()
	{
		initWidget(uiBinder.createAndBindUi(this));
		initButton();
	}
	
	public void setTipEnabled(boolean tipEnable)
	{
    }
	
	public void setCloseEnabled(boolean closeEnabled)
	{
		setButtonWindowCloseEnabled(closeEnabled);
	}
	
	public void setTip(final String tip)
	{

	}
	
	public void setCountEnabled(boolean countEnabled)
	{
	}
	
	public void setCount(int count)
	{
	}
	
	public void setButtonTextEnabled(boolean textEnabled)
	{
	}
	
	public void setButtonText(final String text)
	{
		window.setCaption(text);
	}
	
	public void setIconStyle(final String style)
	{
	}

    public void setButtonWindowCaption(final String caption)
	{
		window.setCaption(caption);
	}
	
	public void setButtonWindow(Widget widget)
	{
		window.setWidget(widget);
	}

    public void setButtonWindowMaxEnabled(boolean b)
	{
		window.setMaximizable(b);
	}

    public void setButtonWindowCloseEnabled(boolean b)
	{
		window.setClosable(b);
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
		//buttonStateElement.removeClassName("ui-state-active");
		fireOnWindowClose();
	}
	
	public void openWindow()
	{
        Log.log("BarButton.openWindow()");
        button.removeStyleName("ijab-window-minimize");
		button.addStyleName("ijab-window-normal");
		button.addStyleName("ijab-window-active");
		//buttonStateElement.addClassName("ui-state-active");
		fireOnWindowOpen();
	}
	
	public void addWidgetListener(BarButtonListener l)
	{
		listeners.add(l);
	}

    public void setHighlight(boolean b)
	{
	}
	
	// local function
	private void initButton()
	{
		window.addListener(new ButtonPopupWindowListener()
		{

			public void onClose() 
			{				
				fireOnClose();
			}
        });

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
}
