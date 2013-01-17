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

import ru.it.lecm.im.client.*;
import ru.it.lecm.im.client.listeners.XmppProfileListener;
import ru.it.lecm.im.client.ui.listeners.ChatPanelButtonListener;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Message.ChatState;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.xmpp.message.Chat;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;

public class ChatPanelButton extends PanelButton
{
	private BarChatWidget chatWidget = new BarChatWidget(this);
	private List<ChatPanelButtonListener> listeners = new ArrayList<ChatPanelButtonListener>();
	private Chat<XmppChat> chatItem = null;
	public ChatPanelButton(ChatPanelBar chatPanel)
	{
		super(chatPanel);
		setButtonWindow(chatWidget);
		this.addButtonStyle("ijab-chat-button");
		setIconStyle("ijab-icon-chat");
	}
	
	public void setChatItem(final Chat<XmppChat> chatItem)
	{
		this.chatItem = chatItem;
		String bareJid = chatItem.getJid().toStringBare();
		chatWidget.setUserAvatar(XmppProfileManager.getAvatarUrl(bareJid));
		String name = XmppProfileManager.getName(bareJid);
		setButtonText(name);
		chatItem.setUserNickname(name);
		
		chatWidget.setUserStatus(XmppProfileManager.getStatusText(bareJid));
		XmppProfileManager.regsiterLister(chatItem.getJid().toStringBare(), new XmppProfileListener()
		{
			public void onNameChange(String name) 
			{
				setButtonText(name);
				chatItem.setUserNickname(name);
			}

			public void onPresenceChange(Presence item) 
			{
				String status = item.getStatus();
				status = status==null?"":status;
				chatWidget.setUserStatus(status);
			}
		});
	}
	
	public Chat<XmppChat> getChatItem()
	{
		return this.chatItem;
	}
	
	public BarChatWidget getChatWidget()
	{
		return chatWidget;
	}
	
	public void processMessage(String nick,Message message,boolean firstMessage)
	{
        Log.consoleLog("ChatPanelButton.processMessage(nick="+nick+", firstMessage="+firstMessage+")");
		String body = message.getBody();
		if(!isActive()&&(body != null&&body.length()!=0))
		{
			if(!chatPanel.isButtonHide(this)&&!chatPanel.haveAcitveButton()&&iJab.client.getIsVisible())
			{
				openWindow();
			}
			else
			{
				oldMessageCount++;
				setCountEnabled(true);
				setCount(oldMessageCount);
				if(chatPanel.isButtonHide(this))
				{
					chatPanel.updateMessageCount();
				}
				setHighlight(true);
			}
		}
		chatWidget.processMessage(nick,message, firstMessage);
	}
	
	public void processSyncSend(Message message,boolean firstMessage)
	{
		chatWidget.processSyncSend(message, firstMessage);
	}
	
	public void onMessageSend(final String message)
	{
		fireOnMessageSend(message);
	}
	
	public void onAvatarClicked(int clientX,int clientY)
	{
		iJab.client.onAvatarClicked(clientX, clientY, chatItem.getJid().toStringBare());
		fireOnAvatarClicked();
	}
	
	public void onAvatarMouseOver(int clientX,int clientY)
	{
		iJab.client.onAvatarMouseOver(clientX, clientY, chatItem.getJid().toStringBare());
		//TODO: fire..
	}
	
	public void addListener(ChatPanelButtonListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(ChatPanelButtonListener l)
	{
		listeners.remove(l);
	}
	
	private void fireOnMessageSend(final String message)
	{
		for(ChatPanelButtonListener l:listeners)
		{
			l.onMessageSend(message);
		}
	}
	
	private void fireOnAvatarClicked()
	{
		for(ChatPanelButtonListener l:listeners)
		{
			l.onAavatrClicked();
		}
	}
	
	public void openWindow()
	{
		super.openWindow();
		oldMessageCount = 0;
		setCountEnabled(false);
		setHighlight(false);
		Timer delay = new Timer()
		{
			@Override
			public void run() {
				chatWidget.scrollHistoryToBottom();
			}
		};
		delay.schedule(200);
		chatWidget.focusToInput();
	}
	
	
	public void setChatWidgetTip(final String tip)
	{
		chatWidget.setNotice(tip);
	}
	
	public void clearHistory()
	{
		chatWidget.cleanHistory();
	}
	
	public void doSuspend()
	{
		chatWidget.doSuspend("ijab_chats_"+chatItem.getJid().getNode());
	}
	
	public void doResume()
	{
		chatWidget.doResume("ijab_chats_"+chatItem.getJid().getNode());
	}
	
	protected void onButtonClose()
	{
		super.onButtonClose();
		chatWidget.onButtonClose();
	}
	
	protected void onWindowClose()
	{
		super.onWindowClose();
		chatWidget.onWindowClose();
	}
	
	
	
	public boolean isContactAvailable()
	{
		return Session.instance().getPresencePlugin().isAvailableByBareJid(chatItem.getJid().toStringBare());
	}
	
	public void setChatState(ChatState state)
	{
		//TODO:set the chat state(composing...) at chatbutton
	}
}
