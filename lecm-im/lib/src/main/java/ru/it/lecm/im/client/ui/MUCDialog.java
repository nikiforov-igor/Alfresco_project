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

import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCDialog extends DialogBox implements GroupChatListener
{
	private final GroupChat gc;
	private MUCChatWidget chatWidget;
	private MUCUserListWidget listWidget = new MUCUserListWidget();
	public MUCDialog(GroupChat gc)
	{
		addStyleName("ijab-muc-dialog");
		this.gc = gc;
		setGlassEnabled(false);
		setAnimationEnabled(true);
		setModal(false);
		setText(gc.getRoomJid().getNode());
		setWidget(initUI());
		connectEvent();
	}
	
	private Widget initUI()
	{
		HorizontalSplitPanel hSplit = new HorizontalSplitPanel();
		hSplit.setSize("400px", "320px");
	    hSplit.setSplitPosition("70%");
	    chatWidget =new MUCChatWidget();
	    chatWidget.setGroupChat(gc);
	    hSplit.setRightWidget(listWidget);
	    hSplit.setLeftWidget(chatWidget);
	    return hSplit;
	}
	
	private void connectEvent()
	{
		this.gc.addListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCJoinDeny(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCJoinDeny(GroupChatEvent gcEvent) 
	{
		this.gc.removeListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCJoined(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCJoined(GroupChatEvent gcEvent) 
	{
		center();
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCLeaved(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCLeaved(GroupChatEvent gcEvent) 
	{
		gc.removeListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onMessage(ru.it.lecm.im.client.xmpp.stanzas.Message)
	 */
	public void onMessage(Message message) {
		chatWidget.processMessage(message);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onUserLeaved(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onUserLeaved(GroupChatEvent gcEvent) 
	{
		listWidget.removeItem(gcEvent.getPresence());
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onUserPresenceChange(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onUserPresenceChange(GroupChatEvent gcEvent) 
	{
		listWidget.updatePresence(gcEvent.getPresence());
	}
}
