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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.ui.listeners.OptionWidgetListener;
import ru.it.lecm.im.client.utils.i18n;

@SuppressWarnings("deprecation")
public class OptionWidget extends Composite 
{

	private static ConfigWidgetUIUiBinder uiBinder = GWT
			.create(ConfigWidgetUIUiBinder.class);

	interface ConfigWidgetUIUiBinder extends UiBinder<Widget, OptionWidget> 
	{
	}
	
	@UiField CheckBox disableOnlineSound;
	@UiField CheckBox disableMessageSound;
	@UiField CheckBox clearChatClose;
	@UiField Element poweredby;
	
	OptionWidgetListener listener = null;
	
	public OptionWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		disableOnlineSound.setText(i18n.msg("Disable online sound notifications"));
		disableMessageSound.setText(i18n.msg("Disable message sound notifications"));
		clearChatClose.setText(i18n.msg("Clean chat after close chat window"));
		
		if(iJab.conf.isHidePoweredBy())
			poweredby.getStyle().setDisplay(Display.NONE);
		
		disableOnlineSound.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(listener!=null)
					listener.onOnlineSoundOptionChange(disableOnlineSound.isChecked());
			}
			
		});
		
		disableMessageSound.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(listener!=null)
					listener.onMessageSoundOptionChange(disableMessageSound.isChecked());
			}
			
		});
		
		clearChatClose.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(listener!=null)
					listener.onChatOptionChange(clearChatClose.isChecked());
			}
		});
	}
	
	public void setListener(OptionWidgetListener listener)
	{
		this.listener = listener;
	}
	
	public void setOptions(boolean disableOnlineSound,boolean disableMessageSound,boolean clearChatClose)
	{
		this.disableOnlineSound.setChecked(disableOnlineSound);
		this.disableMessageSound.setChecked(disableMessageSound);
		this.clearChatClose.setChecked(clearChatClose);
	}
	
	public void reset()
	{
		disableOnlineSound.setChecked(true);
		disableMessageSound.setChecked(false);
		clearChatClose.setChecked(false);
	}
}
