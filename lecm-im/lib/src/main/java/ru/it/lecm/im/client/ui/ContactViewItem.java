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

import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.listeners.XmppProfileListener;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.utils.XmppStatus.Status;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItemListener;

public class ContactViewItem extends ContactViewItemUI
{
	private final RosterItem item;
	private final ContactView view;
	private final ContactViewGroup group;
	private XmppStatus.Status status = XmppStatus.Status.STATUS_OFFLINE;
	private final XmppProfileListener profileListener;
	public ContactViewItem(ContactView view,ContactViewGroup group,final RosterItem item,boolean onlineGroup) 
	{
		super(onlineGroup?buildOnlineWidgetItemIDFromJid(item.getJid()):buildWidgetIDFromJid(item.getJid()));
		setStatusIcon(XmppStatus.statusIconFromStatus(status));
		setOffline(true);
		this.view = view;
		this.item = item;
		this.group = group;
		String name = XmppProfileManager.getName(item.getJid());
		setName(name);	
		setStatusText(XmppProfileManager.getStatusText(item.getJid()));
		setTitle(name+"<"+item.getJid()+">");
		profileListener =  new XmppProfileListener()
		{
			public void onNameChange(String name) 
			{
				setName(name);	
				setTitle(name+"<"+item.getJid()+">");
			}

			public void onPresenceChange(Presence item) 
			{
				XmppStatus.Status newStatus = XmppStatus.makeStatus(item);
				setXmppStatus(newStatus);
				setStatusText(item.getStatus());
			}
		};
		XmppProfileManager.regsiterLister(item.getJid(),profileListener);
		setAvatar(XmppProfileManager.getAvatarUrl(item.getJid()));

        item.addListener(new RosterItemListener() {
            @Override
            public void onNewMessage(int oldMessages) {
                Log.log(XmppProfileManager.getName("RosterItemListener.onNewMessage(): " + item.getJid()) + " " + oldMessages);
                if (oldMessages > 0)
                {
                    nameElement.addClassName("has-unread-messages");
                    msgCounterTextElement.setInnerText("("+oldMessages+")");
                }
                else
                {
                    nameElement.removeClassName("has-unread-messages");
                    msgCounterTextElement.setInnerText("");
                }

            }
        });
	}
	
	public void destory()
	{
		XmppProfileManager.UnregsiterLister(item.getJid(), profileListener);
	}
	
	public XmppStatus.Status getXmppStatus()
	{
		return status;
	}
	
	public String getGroupName()
	{
		return group.getGroupName();
	}
	
	public void setXmppStatus(XmppStatus.Status newStatus)
	{
		if(newStatus == status)
			return;
		setStatusIcon(XmppStatus.statusIconFromStatus(newStatus));
		if(newStatus.ordinal()>Status.STATUS_OFFLINE.ordinal())
			setOffline(false);
		else
			setOffline(true);
		group.itemStatusUpdate(this, newStatus, status);
		status = newStatus;
	}
	
	public String getJid()
	{
		return item.getJid();
	}
	
	public RosterItem getRosterItem()
	{
		return item;
	}

	@Override
	protected void onItemClicked() 
	{
		view.fireOnItemClick(item);
	}
	
	public static String buildWidgetIDFromJid(final String jid)
	{
		return "ijabuser_"+jid;
	}
	
	public static String buildOnlineWidgetItemIDFromJid(final String jid)
	{
		return "ijabonlineuser_"+jid;
	}

	@Override
	protected void onAvatarClicked(int clientX, int clientY) 
	{
		iJab.client.onAvatarClicked(clientX,clientY,item.getJid());
	}

	@Override
	protected void onAvatarOver(int clientX, int clientY) 
	{
		iJab.client.onAvatarMouseOver(clientX, clientY, item.getJid());
		view.fireOnAvatarOver(item);
	}

	@Override
	protected void onConextMenu(int x,int y) 
	{
		view.onItemContextMenu(this,x,y);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ui.ContactViewItemUI#onAvatarOut(int, int)
	 */
	@Override
	protected void onAvatarOut(int clientX, int clientY) 
	{
		iJab.client.onAvatarMouseOut(clientX, clientY, item.getJid());
		view.fireOnAvatarOut(item);
	}

}
