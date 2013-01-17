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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.ui.emoticons.Emoticon;
import ru.it.lecm.im.client.ui.emoticons.Emoticons;
import ru.it.lecm.im.client.ui.listeners.EmoticonPanelItemListener;
import ru.it.lecm.im.client.ui.listeners.EmoticonsPanelListener;

public class EmoticonsPanel extends Composite {

	private static EmoticonsPanelUiBinder uiBinder = GWT
			.create(EmoticonsPanelUiBinder.class);

	interface EmoticonsPanelUiBinder extends UiBinder<Widget, EmoticonsPanel> {
	}
	
	@UiField FlowPanel panel;
	@UiField ListPanel content;
	EmoticonsPanelListener l;
	public EmoticonsPanel() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		for(Emoticon emot: Emoticons.instance().emoticons)
		{
			content.add(new EmoticonPanelItem(emot, new EmoticonPanelItemListener()
			{
				public void emotSelect(String text) 
				{
					l.emotSelect(text);
				}
			}));
		}
	}
	
	public void setListener(EmoticonsPanelListener l)
	{
		this.l = l;
	}
	
	public boolean isVisible()
	{
		return panel.getStyleName().contains("ijab-emot-show");
	}
	
	public void setVisible(boolean visible)
	{
		if(visible)
			panel.addStyleName("ijab-emot-show");
		else
			panel.removeStyleName("ijab-emot-show");
	}
}
