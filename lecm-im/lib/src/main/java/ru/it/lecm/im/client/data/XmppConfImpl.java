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

import ru.it.lecm.im.client.xmpp.Session.ServerType;

import com.google.gwt.core.client.JavaScriptObject;

public class XmppConfImpl extends JavaScriptObject implements XmppConf
{
	protected XmppConfImpl()
	{
		
	}
	
	public final native String getAnonymousPrefix() 
	/*-{
		try
		{
			return this.anonymous_prefix;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	public final native String getDomain() 
	/*-{
		try
		{
			return this.domain;
		}
		catch(e)
		{
			alert("iJab configuration of domain can't be empty!");
			return "";
		}
	}-*/;

	public final native String getHttpBind() 
	/*-{
		try
		{
			return this.http_bind;
		}
		catch(e)
		{
			alert("iJab configuration of http_bind can't be empty!");
			return "";
		}
	}-*/;

	public final native int getMaxReconnet() 
	/*-{
		try
		{
			return this.max_reconnect;
		}
		catch(e)
		{
			return 0;
		}
	}-*/;

	public final native String getPasswordCookieField() 
	/*-{
		try
		{
			return this.token_cookie_field;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	public final native int getPort() 
	/*-{
		try
		{
			return this.port;
		}
		catch(e)
		{
			return 5222;
		}
	}-*/;

	public final ServerType getServerType() 
	{
		try
		{
			String strType = getServerTypeJSO();
			strType = strType.toLowerCase();
			return ServerType.valueOf(strType);
		}
		catch(Exception e)
		{
			return ServerType.ejabberd;
		}
	}
	
	private final native String getServerTypeJSO()
	/*-{
		try
		{
			return this.server_type;
		}
		catch(e)
		{
			return "ejabberd";
		}
	}-*/;
	
	public final native String getUserCookieField() 
	/*-{
		try
		{
			return this.username_cookie_field;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	public final native boolean isAutoLogin() 
	/*-{
		try
		{
			return this.auto_login;
		}
		catch(e)
		{
			return true;
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.XmppConf#getHost()
	 */
	public final native String getHost() 
	/*-{
		try
		{
			return this.host;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

    /* (non-Javadoc)
     * @see anzsoft.iJab.client.data.XmppConf#getMUCServernode()
     */
	public final native String getMUCServernode() 
	/*-{
		try
		{
			return this.muc_servernode;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.XmppConf#getVCardSearchServernode()
	 */
	public final native String getVCardSearchServernode() 
	/*-{
		try
		{
			return this.vcard_search_servernode;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.XmppConf#isGetRosterDelay()
	 */
	public final native boolean isGetRosterDelay() 
	/*-{
		try
		{
			return this.get_roster_delay;
		}
		catch(e)
		{
			return false;
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.XmppConf#isNoneRoster()
	 */
	public final native boolean isNoneRoster() 
	/*-{
		try
		{
			return this.none_roster;
		}
		catch(e)
		{
			return false;
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.XmppConf#isMUCEnabled()
	 */
	public final native boolean isMUCEnabled() 
	/*-{
		try
		{
			return this.enable_muc;
		}
		catch(e)
		{
			return true;
		}
	}-*/;
}
