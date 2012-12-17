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
package ru.it.lecm.im.client.utils;

import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Show;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Type;

import com.google.gwt.core.client.GWT;

public class XmppStatus 
{
	public enum Status{STATUS_OFFLINE,STATUS_INVISIBLE,STATUS_DND,STATUS_XA,STATUS_AWAY,STATUS_ONLINE,STATUS_CHAT};
	
	public static String available()
	{
		return GWT.getModuleBaseURL()+"images/status/available.png";
	}
	
	public static String away()
	{
		return GWT.getModuleBaseURL()+"images/status/away.png";
	}
	
	public static String blocked()
	{
		return GWT.getModuleBaseURL()+"images/status/blocked.png";
	}
	
	public static String idle()
	{
		return GWT.getModuleBaseURL()+"images/status/idle.png";
	}
	
	public static String invisible()
	{
		return GWT.getModuleBaseURL()+"images/status/invisible.png";
	}
	
	public static String unavailable()
	{
		return GWT.getModuleBaseURL()+"images/status/offline.png";
	}
	
	public static String typing()
	{
		return GWT.getModuleBaseURL()+"images/status/typing.png";
	}
	
	public static String xa()
	{
		return GWT.getModuleBaseURL()+"images/status/xa.png";
	}
	
	public static String busy()
	{
		return GWT.getModuleBaseURL()+"images/status/busy.png";
	}
	
	public static String chat()
	{
		return GWT.getModuleBaseURL()+"images/status/chat.png";
	}
	
	public static Status statusFromString(final String statusStr)
	{
		try
		{
			return Status.valueOf(statusStr);
		}
		catch(Exception e)
		{
			return Status.STATUS_ONLINE;
		}
	}
	
	public static Presence makePresence(final String statusStr)
	{
		return makePresence(statusFromString(statusStr));
	}
	
	public static Presence makePresence(final Status status)
	{
		Presence presence = new Presence(Type.available);
		if(status == XmppStatus.Status.STATUS_INVISIBLE)
		{
			presence.setType(Type.invisible);
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
		return presence;
	}
	
	public static Status makeStatus(final Presence presence)
	{
		if(presence == null)
			return Status.STATUS_OFFLINE;
		
		if(presence.getType().equals(Type.unavailable))
			return Status.STATUS_OFFLINE;
		else
		{
			switch(presence.getShow())
			{
			case away:
				return Status.STATUS_AWAY;
			case chat:
				return Status.STATUS_CHAT;
			case dnd:
				return Status.STATUS_DND;
			case xa:
				return Status.STATUS_XA;
			default:
				return Status.STATUS_ONLINE;
			}
		}
	}
	
	public static String statusIconFromPresence(Presence presence)
	{
		return statusIconFromStatus(makeStatus(presence));
	}
	
	public static String statusIconFromStatus(Status status)
	{
		String statusIconString = unavailable();
		switch(status)
		{
		case STATUS_OFFLINE:
			statusIconString = unavailable();
			break;
		case STATUS_DND:
			statusIconString = busy();
			break;
		case STATUS_AWAY:
			statusIconString = away();
			break;
		case STATUS_CHAT:
			statusIconString = chat();
			break;
		case STATUS_XA:
			statusIconString = xa();
			break;
		case STATUS_INVISIBLE:
			statusIconString = invisible();
			break;
		default:
			statusIconString = available();
			
		}
		return statusIconString;
		
	}
	
}
