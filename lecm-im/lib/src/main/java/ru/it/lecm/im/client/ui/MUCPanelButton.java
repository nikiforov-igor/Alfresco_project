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
 * Apr 1, 2010
 */
package ru.it.lecm.im.client.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCPanelButton extends PanelButton implements GroupChatListener
{
	private GroupChat gc = null;
	private MUCChatWidget chatWidget;
	private MUCUserListWidget listWidget = new MUCUserListWidget();
	/**
	 * @param chatPanel
	 */
	public MUCPanelButton(ChatPanelBar chatPanel) 
	{
		super(chatPanel);
		setButtonWindow(createMainWidget());
		this.addButtonStyle("ijab-muc-button");
		setIconStyle("ijab-icon-muc");
	}
	
	public void setGroupChat(GroupChat gc)
	{
		this.gc = gc;
		this.gc.addListener(this);
		chatWidget.setGroupChat(gc);
		gc.setUserData(this);
		setButtonText(gc.getRoomName());
	}
	
	private Widget createMainWidget()
	{
		HorizontalSplitPanel hSplit = new HorizontalSplitPanel();
		hSplit.setSize("400px", "280px");
	    hSplit.setSplitPosition("70%");
	    chatWidget =new MUCChatWidget();
	    hSplit.setRightWidget(listWidget);
	    hSplit.setLeftWidget(chatWidget);
	    return hSplit;
	}
	
	public void closeWindow()
	{
		super.closeWindow();
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
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCLeaved(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCLeaved(GroupChatEvent gcEvent) 
	{
		//gc.removeListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onMessage(ru.it.lecm.im.client.xmpp.stanzas.Message)
	 */
	public void onMessage(Message message) 
	{
		String body = message.getBody();
		if(!isActive()&&(body != null&&body.length()!=0))
		{
			if(!chatPanel.isButtonHide(this)&&!chatPanel.haveAcitveButton())
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
	
	protected void onButtonClose()
	{
		gc.leave();
	}
}
