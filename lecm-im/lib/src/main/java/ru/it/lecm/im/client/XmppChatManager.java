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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import ru.it.lecm.im.client.bubling.MessageCountUpdater;
import ru.it.lecm.im.client.ui.ChatPanelBar;
import ru.it.lecm.im.client.ui.ChatPanelButton;
import ru.it.lecm.im.client.ui.ContactView;
import ru.it.lecm.im.client.utils.WindowPrompt;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Storage;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.xmpp.message.Chat;
import ru.it.lecm.im.client.xmpp.xmpp.message.ChatListener;
import ru.it.lecm.im.client.xmpp.xmpp.message.ChatManager;
import ru.it.lecm.im.client.xmpp.xmpp.message.Notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XmppChatManager implements ChatListener<XmppChat>,ClientListener
{
	private final static String STORAGE_KEY = "XmppChatManager";
	private final static String CHATS_KEY = "all_alive_chat";
	private final static String OPENCHAT_KEY = "current_open_chat";
	
	private final ChatManager<XmppChat> manager;
	private final ChatPanelBar chatPanel;
	private Map<String, Chat<XmppChat>> chats = new HashMap<String,Chat<XmppChat>>();
	private boolean resumeing = false;
	public XmppChatManager(ChatManager<XmppChat> manager,ChatPanelBar chatPanel)
	{
		this.manager = manager;
		this.chatPanel = chatPanel;
		this.manager.addListener(this);
	}
	
	public void onMessageReceived(Chat<XmppChat> chat, Message message,
			boolean firstMessage) 
	{
        Log.consoleLog("XmppChatManager.onMessageReceived()");
		XmppChat xmppChat = chat.getUserData();
		xmppChat.process(message, firstMessage);
		if(message.getBody()!=null&&message.getBody().length() > 0)
		{
            RefreshNewMessagesCount();
            Integer messages = xmppChat.getButton().getOldMessageCount();
            ContactView contactView = iJab.ui.getContactView();
            //contactView.
			String bareJid = chat.getJid().toStringBare();
			iJab.client.onMessageReceive(bareJid, message.getBody());
			String prompt = XmppProfileManager.getName(bareJid)+" Say:"+message.getBody();
			WindowPrompt.prompt(prompt);

            chatPanel.ensureButtonInBar(xmppChat.getButton());

		}
	}

    public void RefreshNewMessagesCount()
    {
        Collection<Chat<XmppChat>> chatCollection = chats.values();
        int oldMessagesCount = 0;
        for (Chat<XmppChat> chat : chatCollection ) {
            XmppChat xmppChat = chat.getUserData();
            oldMessagesCount = oldMessagesCount + xmppChat.getButton().getOldMessageCount();
        }

        Log.consoleLog("oldMessagesCount: " + oldMessagesCount);
        Log.consoleLog("MessageCountUpdater.Update()");
        MessageCountUpdater.Update(oldMessagesCount);
        Log.consoleLog("MessageCountUpdater.Update() - Complete!");



    }

	public void onNotifyReceive(Notify notify) 
	{
	}

	public void onStartNewChat(Chat<XmppChat> chat) 
	{
        Log.consoleLog("XmppChatManager.onStartNewChat()");
		if(chat.getUserData() == null)
		{
			String nick = Session.instance().getRosterPlugin().getNameByJid(chat.getJid());
			if(nick == null||nick.length() == 0)
				nick = chat.getJid().getNode();
			chat.setUserNickname(nick);
			ChatPanelButton button = chatPanel.createChatButton();
			new XmppChat(chat,button);
			chats.put(chat.getJid().toStringBare(), chat);
			//button.openWindow();
		}
	}

	public void onSyncRecv(Chat<XmppChat> chat, Message message,
			boolean firstMessage) 
	{
		XmppChat xmppChat = chat.getUserData();
		xmppChat.process(message, firstMessage);
		if(message.getBody()!=null&&message.getBody().length() > 0)
			chatPanel.ensureButtonInBar(xmppChat.getButton());
	}

	public void onSyncSend(Chat<XmppChat> chat, Message message,
			boolean firstMessage) 
	{
		XmppChat xmppChat = chat.getUserData();
		xmppChat.processSyncSend(message, firstMessage);
		if(message.getBody()!=null&&message.getBody().length() > 0)
			chatPanel.ensureButtonInBar(xmppChat.getButton());
	}
	
	public ChatPanelButton openChat(JID jid)
	{
        Log.consoleLog("XmppChatManager.openChat()");
		Chat<XmppChat> chat = chats.get(jid.toStringBare());
		if(chat != null&&chat.getUserData() != null)
		{
			XmppChat xmppChat = chat.getUserData();
			if(!resumeing)
				xmppChat.openChat();
			chatPanel.ensureButtonInBar(xmppChat.getButton());
            RefreshNewMessagesCount();
			return xmppChat.getButton();
		}
		else
		{
			XmppChat xmppChat = manager.startChat(jid).getUserData();
			if(!resumeing)
				xmppChat.openChat();
            RefreshNewMessagesCount();
			return xmppChat.getButton();
		}
	}
	
	public ChatPanelButton openChat(String jid)
	{
        Log.consoleLog("XmppChatManager.openChat()");
		return openChat(JID.fromString(jid));
	}
	
	private void clearCacheData()
	{
		final String prefix = Session.instance().getUser().getStorageID();
		Storage storage = Storage.createStorage(STORAGE_KEY,prefix);
		storage.set(STORAGE_KEY, "");
		storage.remove(STORAGE_KEY);
		storage.set(prefix+STORAGE_KEY,"");
		storage.remove(prefix+STORAGE_KEY);
	}

	public void onResume() 
	{
        Log.consoleLog("XmppChatManager.onResume()");
		final String prefix = Session.instance().getUser().getStorageID();
		Storage storage = Storage.createStorage(STORAGE_KEY, prefix);
		//first ,get all button in bar 
		final String data = storage.get(CHATS_KEY);
		JSONArray array = JSONParser.parse(data).isArray();
		chatPanel.setOnResume(true);
		resumeing = true;
		if(array!=null)
		{
			for(int index=array.size()-1;index>=0;index--)
			{
				String bareJid = array.get(index).isString().stringValue();
				if(bareJid != null&&bareJid.length()>0)
				{
					ChatPanelButton button = openChat(bareJid);
					if(button!=null)
						button.doResume();
				}
			}
		}
		resumeing = false;
		chatPanel.setOnResume(false);
		
		final String activeButtonJid = storage.get(OPENCHAT_KEY);
		if(activeButtonJid!=null&&activeButtonJid.length()>0)
			openChat(activeButtonJid);
		storage.set(OPENCHAT_KEY, "");
		storage.remove(OPENCHAT_KEY);
		storage.set(STORAGE_KEY, "");
		storage.remove(STORAGE_KEY);
		storage.set(prefix+STORAGE_KEY,"");
		storage.remove(prefix+STORAGE_KEY);
	}

	public void onSuspend() 
	{
        Log.consoleLog("XmppChatManager.onSuspend()");
		//first get all button in the bar
		final String prefix = Session.instance().getUser().getStorageID();
		Storage storage = Storage.createStorage(STORAGE_KEY,prefix);
		ArrayList<ChatPanelButton> buttons = chatPanel.getChatButtonsInBar();
		JSONArray array = new JSONArray();
		for(int index=0;index<buttons.size();index++)
		{
			ChatPanelButton button = buttons.get(index);
			array.set(index, new JSONString(button.getChatItem().getJid().toStringBare()));
			button.doSuspend();
		}
		storage.set(CHATS_KEY, array.toString());
		
		ChatPanelButton activeButton = chatPanel.getActiveChatButton();
		if(activeButton!=null)
		{
			String openJid = activeButton.getChatItem().getJid().toStringBare();
			storage.set(OPENCHAT_KEY, openJid);
		}
		else
		{
			storage.set(OPENCHAT_KEY, "");
			storage.remove(OPENCHAT_KEY);
		}
	}

	public void onBeforeLogin() {
		
	}

	public void onEndLogin() {
	}

	public void onError(String error) {
		clearCacheData();
	}

	public void onLogout() {
		clearCacheData();
	}

	public void onAvatarClicked(int clientX, int clientY, String username,
			String bareJid) {
	}

	public void onAvatarMouseOver(int clientX, int clientY, String username,
			String bareJid) {
	}

	public void onStatusTextUpdated(String text) {
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onAvatarMouseOut(int, int, java.lang.String, java.lang.String)
	 */
	public void onAvatarMouseOut(int clientX, int clientY, String usrname,
			String bareJid) {
		
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onMessageReceive(java.lang.String, java.lang.String)
	 */
	public void onMessageReceive(String jid, String message) {
	}
	

}
