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
 * Mar 12, 2010
 */
package ru.it.lecm.im.client.ui;

import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.net.XmppProfileManager;
import ru.it.lecm.im.client.utils.i18n;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class BlackListWndItem extends Composite 
{
	public interface RemoveListener
	{
		void onRemove();
	}
	
	private static BlackListWndItemUiBinder uiBinder = GWT
			.create(BlackListWndItemUiBinder.class);

	interface BlackListWndItemUiBinder extends
			UiBinder<Widget, BlackListWndItem> {
	}

	@UiField Element nameText;
	@UiField Element jidText;
	@UiField Element removeElement;
	
	private SimpleFocusWidget removeWidget;
	private final PrivacyItem item;
	private RemoveListener listener;
	public BlackListWndItem(PrivacyItem item)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;
		nameText.setInnerText(XmppProfileManager.getName(item.getValue()));
		jidText.setInnerText(item.getValue());
		removeElement.setInnerText(i18n.msg("Remove"));
		removeElement.setAttribute("title", i18n.msg("Remove from blacklist"));
		removeWidget = new SimpleFocusWidget(removeElement);
		removeWidget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(listener!=null)
					listener.onRemove();
			}
		});
	}
	
	public void setListener(RemoveListener listener)
	{
		this.listener = listener;
	}
	
	public PrivacyItem item()
	{
		return item;
	}

}
