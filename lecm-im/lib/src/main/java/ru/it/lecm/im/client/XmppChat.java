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
package ru.it.lecm.im.client;

import ru.it.lecm.im.client.ui.ChatPanelButton;
import ru.it.lecm.im.client.ui.listeners.ChatPanelButtonListener;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.xmpp.message.Chat;

public class XmppChat 
{
	final private Chat<XmppChat> chatItem;
	final private ChatPanelButton button;
	
	public XmppChat(final Chat<XmppChat> chatItem, ChatPanelButton button)
	{
		this.chatItem = chatItem;
		this.button = button;
		this.button.setChatItem(this.chatItem);
		chatItem.setUserData(this);
		button.addListener(new ChatPanelButtonListener()
		{
			public void onAvatarClicked()
			{
			}

			public void onMessageSend(String message) 
			{
                Log.log("XmppChat.ChatPanelButtonListener.onMessageSend()");
                chatItem.send(message);
			}
		});
	}
	
	public void openChat()
	{
        Log.log("XmppChat.openChat()");
        iJab.ui.getContactView().setActive(this.chatItem.getJid().getBareJID().toStringBare());
		offlineTip();
		button.openWindow();
	}
	
	public void process(Message message,boolean firstMessage)
	{
        Log.log("XmppChat.process()");
		if(firstMessage)
			offlineTip();
		
		button.processMessage(chatItem.getUserNickname(),message, firstMessage);
	}
	
	public void processSyncSend(Message message,boolean firstMessage)
	{
        Log.log("XmppChat.processSyncSend()");
		if(firstMessage)
			offlineTip();
		button.processSyncSend(message, firstMessage);
	}
	
	public ChatPanelButton getButton()
	{
        Log.log("XmppChat.getButton()");
		return button;
	}
	
	private void offlineTip()
	{
		if(!Session.instance().getPresencePlugin().isAvailableByBareJid(chatItem.getJid().toStringBare()))
		{
			button.setChatWidgetTip(chatItem.getUserNickname()+ i18n.msg(" не в сети. ")+ chatItem.getUserNickname()+i18n.msg(" получит ваши сообщения после следующего входа."));
		}
	}
}
