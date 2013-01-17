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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.ui.emoticons.Emoticon;
import ru.it.lecm.im.client.ui.listeners.EmoticonPanelItemListener;
import ru.it.lecm.im.client.utils.i18n;

public class EmoticonPanelItem extends Composite {

	private static EmoticonPanelItemUiBinder uiBinder = GWT
			.create(EmoticonPanelItemUiBinder.class);

	interface EmoticonPanelItemUiBinder extends
			UiBinder<Widget, EmoticonPanelItem> {
	}

	@UiField Image image;
	
	public EmoticonPanelItem(final Emoticon emot,final EmoticonPanelItemListener l)
	{
		initWidget(uiBinder.createAndBindUi(this));
		image.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				l.emotSelect(emot.getText());
			}
		});
		image.getElement().setAttribute("alt",emot.getText());
		image.getElement().setAttribute("title", i18n.msg(emot.getName()));
		image.getElement().setAttribute("src", emot.getIconUrl());
	}

}
