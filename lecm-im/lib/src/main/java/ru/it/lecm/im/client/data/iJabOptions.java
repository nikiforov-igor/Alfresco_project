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
package ru.it.lecm.im.client.data;

import ru.it.lecm.im.client.XmppClient;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.listeners.ClientListener;
import ru.it.lecm.im.client.ui.OptionWidget;
import ru.it.lecm.im.client.ui.UserIndicator;
import ru.it.lecm.im.client.ui.listeners.IndicatorListener;
import ru.it.lecm.im.client.ui.listeners.OptionWidgetListener;
import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.packet.PacketImp;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Show;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Type;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.privateStorage.PrivateStoragePlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.privateStorage.PrivateStorageRequestCallback;

public class iJabOptions
{
	//options data section
	private String statusText = i18n.msg("AlfrescoIM");
	private static String OPTION_STATUS =  "statusText";
	private boolean disableOnlineSoundNotification = true;
	private static String OPTION_ONLINE_SOUND = "OnlineSoundDisable";
	private boolean disableMessageSoundNotification = false;
	private static String OPTION_MESSAGE_SOUND = "MessageSoundDisable";
	private boolean autoClearHistory = false;
	private static String OPTION_CHAT = "chatAutoClear";
	private static String OPTION_XMPP_STATUS= "xmppStatus";
	private XmppStatus.Status status = XmppStatus.Status.STATUS_ONLINE;
	
	static String IJABOPTIONS_XMLNS = "ijab:options";
	static String IJABOPTIONS_ELEMENT = "options";
	final private XmppClient client;
	
	private static iJabOptions instance;
	private UserIndicator indicator = (UserIndicator) iJab.ui.getIndictorWidget();
	static public iJabOptions instance()
	{
		return instance;
	}
	
	public iJabOptions(final XmppClient client)
	{
		instance = this;
		this.client = client;
		client.addClientListener(new ClientListener()
		{
			public void onBeforeLogin() {
			}

			public void onEndLogin() {
				init(false);
				XmppProfileManager.regsiterLister(client.getSession().getUser().getStringBareJid(), indicator.getProfileListener());
			}

			public void onError(String error) {
			}

			public void onLogout() {
				XmppProfileManager.UnregsiterLister(client.getSession().getUser().getStringBareJid(), indicator.getProfileListener());
			}

			public void onResume() {
				init(true);
				XmppProfileManager.regsiterLister(client.getSession().getUser().getStringBareJid(), indicator.getProfileListener());
			}

			public void onSuspend() {
			}

            public void onStatusTextUpdated(String text) {
			}
        });
		connectUI();
	}
	
	private void connectUI()
	{
		OptionWidget widget = (OptionWidget)iJab.ui.getOptionWidget();
		widget.setListener(new OptionWidgetListener()
		{

			public void onChatOptionChange(boolean b) 
			{
				setAutoClearHistory(b);
			}

			public void onMessageSoundOptionChange(boolean b) {
				setDisableMessageSoundNotification(b);
			}

			public void onOnlineSoundOptionChange(boolean b) 
			{
				setDisableOnlineSoundNotification(b);
			}
		});
		
		
		indicator.setListener(new IndicatorListener()
		{
			public void onStatusTextChange(String statusText) {
				setStatusText(statusText);
			}

			public void onXmppStatusChange(XmppStatus.Status status) {
				setXmppStatus(status);
			}
		});
	}
	
	public void init(final boolean resume)
	{
		PrivateStoragePlugin plugin = client.getSession().getPrivateStoragePlugin();
		plugin.getPrivateData("options", IJABOPTIONS_XMLNS, new PrivateStorageRequestCallback()
		{
			public void onReceiveData(Packet element) 
			{
				Packet statusPacket = element.getFirstChild(OPTION_STATUS);
				if(statusPacket != null)
				{
					statusText = statusPacket.getCData();
					client.getSession().getPresencePlugin().sendStatusText(statusText);
				}
				
				Packet disableOnlineSoundNotificationPacket = element.getFirstChild(OPTION_ONLINE_SOUND);
				if(disableOnlineSoundNotificationPacket != null)
				{
					String value = disableOnlineSoundNotificationPacket.getCData();
					if(value.equalsIgnoreCase("false"))
						disableOnlineSoundNotification = false;
					else
						disableOnlineSoundNotification = true;
				}
				
				Packet disableMessageSoundNotificationPacket = element.getFirstChild(OPTION_MESSAGE_SOUND);
				if(disableMessageSoundNotificationPacket != null)
				{
					String value = disableMessageSoundNotificationPacket.getCData();
					if(value.equalsIgnoreCase("false"))
						disableMessageSoundNotification = false;
					else
						disableMessageSoundNotification = true;
				}
				
				Packet autoClearHistoryPacket = element.getFirstChild(OPTION_CHAT);
				if(autoClearHistoryPacket!=null)
				{
					String value = autoClearHistoryPacket.getCData();
					if(value.equalsIgnoreCase("false"))
						autoClearHistory = false;
					else
						autoClearHistory = true;
				}
				
				Packet showPacket = element.getFirstChild(OPTION_XMPP_STATUS);
				if(showPacket!=null)
				{
					String value = showPacket.getCData();
					status = XmppStatus.Status.valueOf(value);
				}
				
				OptionWidget widget = (OptionWidget)iJab.ui.getOptionWidget();
				widget.setOptions(disableOnlineSoundNotification,disableMessageSoundNotification, autoClearHistory);
				UserIndicator indicator = (UserIndicator)iJab.ui.getIndictorWidget();
				indicator.setOption(XmppProfileManager.getName(Session.instance().getUser().getStringBareJid()),statusText, status);
				// SoundManager.setOnlineEnabled(!disableOnlineSoundNotification);
				// SoundManager.setMessageEnabled(!disableMessageSoundNotification);
				if(!resume)
					sendStorePresence();
			}
			public void onError(IQ iq, ErrorType errorType,
					ErrorCondition errorCondition, String text) 
			{
			}
			
		});
	}
	
	private void sendStorePresence()
	{
		Presence presence = new Presence(Type.available);
		if(status == XmppStatus.Status.STATUS_INVISIBLE)
		{
			//presence.setType(Type.invisible);
            presence.setShow(Show.notSpecified);
		}
		else
		{
			Show show = Show.notSpecified;
			switch(status)
			{
			case STATUS_DND:
				show = Show.dnd;
				break;
			case STATUS_XA:
				show = Show.xa;
				break;
			case STATUS_CHAT:
				show = Show.chat;
				break;
			case STATUS_AWAY:
				show= Show.away;
				break;
			case STATUS_ONLINE:
				show= Show.notSpecified;
			default:
				show= Show.notSpecified;
				break;
			}
			presence.setShow(show);
		}
		presence.setStatus(statusText);
		client.getSession().send(presence);
	}
	
	private void store()
	{
		Packet data = new PacketImp(IJABOPTIONS_ELEMENT);
		data.setAttribute("xmlns", IJABOPTIONS_XMLNS);
		
		Packet statusTextPacket = data.addChild(OPTION_STATUS, null);
		statusTextPacket.setCData(statusText);
		
		String disableOnlineSoundNotificationString = disableOnlineSoundNotification?"true":"false"; 
		Packet disableOnlineSoundNotificationPacket = data.addChild(OPTION_ONLINE_SOUND, null);
		disableOnlineSoundNotificationPacket.setCData(disableOnlineSoundNotificationString);
		
		String disableMessageSoundNotificationString = disableMessageSoundNotification?"true":"false"; 
		Packet disableMessageSoundNotificationPacket = data.addChild(OPTION_MESSAGE_SOUND, null);
		disableMessageSoundNotificationPacket.setCData(disableMessageSoundNotificationString);
		
		String autoClearHistoryString = autoClearHistory?"true":"false"; 
		Packet autoClearHistoryPacket = data.addChild(OPTION_CHAT, null);
		autoClearHistoryPacket.setCData(autoClearHistoryString);
		
		Packet showPacket = data.addChild(OPTION_XMPP_STATUS, null);
		showPacket.setCData(status.toString());
		
		PrivateStoragePlugin plugin = client.getSession().getPrivateStoragePlugin();
		plugin.store(data);
	}
	
	public void setStatusText(String statusText) 
	{
		iJab.client.onStatusTextUpdate(statusText);
		this.statusText = statusText;
		sendStorePresence();
		store();
	}
	
	public String getStatusText() 
	{
		return statusText;
	}
	
	public void setXmppStatus(XmppStatus.Status status)
	{
		this.status = status;
		sendStorePresence();
		store();
	}

	public void setDisableOnlineSoundNotification(boolean disableOnlineSoundNotification) 
	{
		this.disableOnlineSoundNotification = disableOnlineSoundNotification;
		// SoundManager.setOnlineEnabled(!disableOnlineSoundNotification);
		store();
	}
	
	public void setDisableMessageSoundNotification(boolean disableMessageSoundNotification) 
	{
		this.disableMessageSoundNotification = disableMessageSoundNotification;
		// SoundManager.setMessageEnabled(!disableMessageSoundNotification);
		store();
	}

	public boolean isDisableOnlineSoundNotification() 
	{
		return disableOnlineSoundNotification;
	}
	
	public boolean isDisableMessageSoundNotification() 
	{
		return disableMessageSoundNotification;
	}

	public void setAutoClearHistory(boolean autoClearHistory) 
	{
		this.autoClearHistory = autoClearHistory;
		store();
	}

	public boolean isAutoClearHistory() 
	{
		return autoClearHistory;
	}
}
