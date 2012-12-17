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
 * Mar 31, 2010
 */
package ru.it.lecm.im.client.ui;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatManager;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomItem;

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
import com.google.gwt.user.client.ui.Widget;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCRoomItemUI extends Composite {

	private static MUCRoomItemUIUiBinder uiBinder = GWT
			.create(MUCRoomItemUIUiBinder.class);

	interface MUCRoomItemUIUiBinder extends UiBinder<Widget, MUCRoomItemUI> {
	}
	
	@UiField FocusHTMLPanel item;
	@UiField Element name;
	final private MucRoomItem roomItem;
	public MUCRoomItemUI(final MucRoomItem roomItem,final ChatPanelBar chatpanel,final MUCRoomWidget roomWidget)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.roomItem = roomItem;
		name.setInnerText(roomItem.getName());
		item.setTitle(roomItem.getName()+"("+roomItem.getJid()+")");
		item.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				GroupChatManager manager = Session.instance().getMucPlugin().getGroupChatManager();
				GroupChat gc = manager.get(JID.fromString(roomItem.getJid()));
				if(gc!=null)
				{
					
				}
				else
				{
					MUCJoinDialog dialog = new MUCJoinDialog(roomItem,chatpanel,roomWidget);
					dialog.center();
				}
			}
		});
		
		item.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) {
				item.addStyleName("ijab-muc-item-hover");
			}
		});
		item.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				item.removeStyleName("ijab-muc-item-hover");
			}
		});
	}
	
	public MucRoomItem getRoomItem()
	{
		return this.roomItem;
	}
	
	public void setOddStyle()
	{
		item.addStyleName("ijab-muc-item-odd");
	}

}
