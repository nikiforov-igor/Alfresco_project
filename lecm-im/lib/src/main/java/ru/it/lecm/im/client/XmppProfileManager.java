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

import com.google.gwt.core.client.GWT;
import ru.it.lecm.im.client.listeners.XmppProfileListener;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class XmppProfileManager 
{
	private static Map<String,ArrayList<XmppProfileListener>> listenersMap = new HashMap<String,ArrayList<XmppProfileListener>>();
	public static Map<String,String> names = new HashMap<String,String>();
	public static void reset()
	{
		names.clear();
		listenersMap.clear();
	}
	
	public static void regsiterLister(final String bareJid, XmppProfileListener l)
	{
		ArrayList<XmppProfileListener> listeners = listenersMap.get(bareJid);
		if(listeners == null)
			listeners = new ArrayList<XmppProfileListener>();
		if(listeners.contains(l))
			return;
		listeners.add(l);
		listenersMap.put(bareJid, listeners);
	}
	
	public static void UnregsiterLister(final String bareJid,XmppProfileListener l)
	{
		ArrayList<XmppProfileListener> listeners = listenersMap.get(bareJid);
		if(listeners!=null)
		{
			listeners.remove(l);
		}
	}
	
	public static void commitNewName(final String bareJid,final String newName)
	{
		final String nodeName = StringUtil.jid2name(bareJid);//JID.fromString(bareJid).getNode();
		final String oldName = names.get(bareJid);
		String commitName;
		if(oldName == null)
		{
			if(newName == null||newName.length()==0)
				commitName = nodeName;
			else
				commitName = newName;
		}
		else
		{
			//throw empty newName and node name
			if(newName==null||newName.length()==0||newName.equals(nodeName))
				return;
			else
				commitName = newName;
		}
		names.put(bareJid, commitName);
		fireOnNameChange(bareJid,commitName);
	}
	
	public static void commitNewPresence(final String bareJid,final Presence newPresence)
	{
		fireOnPresenceChange(bareJid,newPresence);
	}
	
	public static String getName(final String bareJid)
	{
		String name = names.get(bareJid);
		if(name!=null)
			return name;
		else
			return JID.fromString(bareJid).getNode();
	}
	
	public static String getStatusText(final String bareJid)
	{
		Presence presence = Session.instance().getPresencePlugin().getPresenceitemByBareJid(bareJid);
		if(presence!=null)
		{
			String status = presence.getStatus();
			return status==null?"":status;
			
		}
		else
			return new String("");		
	}
	
	public static Presence getPresence(final String bareJid)
	{
		return Session.instance().getPresencePlugin().getPresenceitemByBareJid(bareJid);
	}
	
	public static String getAvatarUrl(final String bareJid)
	{
		String url = iJab.conf.avatarUrl();
		if(url == null||!url.contains("{username}"))
			return GWT.getModuleBaseURL()+"images/default_avatar.png";
		String node = JID.fromString(bareJid).getNode();
		if(node == null)
			return GWT.getModuleBaseURL()+"images/default_avatar.png";
		return url.replace("{username}", node);
	}
	
	private static void fireOnNameChange(final String bareJid,final String name)
	{
		ArrayList<XmppProfileListener> listeners = listenersMap.get(bareJid);
		if(listeners == null)
			return;
		for(XmppProfileListener l:listeners)
		{
			l.onNameChange(name);
		}
	}
	
	private static void fireOnPresenceChange(final String bareJid,final Presence presence)
	{
		ArrayList<XmppProfileListener> listeners = listenersMap.get(bareJid);
		if(listeners == null)
			return;
		for(XmppProfileListener l:listeners)
		{
			l.onPresenceChange(presence);
		}
	}
	
	
}
