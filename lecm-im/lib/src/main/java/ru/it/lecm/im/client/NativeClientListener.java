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

import com.google.gwt.core.client.JavaScriptObject;

public class NativeClientListener implements ClientListener, MessageReceiveListener
{

	final JavaScriptObject delegate;
	public NativeClientListener(final JavaScriptObject delegate)
	{
		this.delegate = delegate;
	}
	
	private native void handleBeforeLogin()
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onBeforeLogin();
		}
		catch(e)
		{
		}
	}-*/;
	
	private native void handleEndLogin()
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onEndLogin();
		}
		catch(e)
		{
		}
	}-*/;
	
	private native void handleError(String message)
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onError(message);
		}
		catch(e)
		{
		}
	}-*/;
	
	private native void handleLogout()
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onLogout();
		}
		catch(e)
		{
		}
	}-*/;
	
	public void onBeforeLogin() 
	{
		handleBeforeLogin();
	}

	public void onEndLogin() 
	{
		handleEndLogin();
	}

	public void onError(String error) 
	{
		handleError(error);
	}

	public void onLogout() {
		handleLogout();
	}

	public native void  onResume() 
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onResume();
		}
		catch(e)
		{
		}
	}-*/;

	public native void onSuspend()
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onSuspend();
		}
		catch(e)
		{
		}
	}-*/;

	public native void onAvatarClicked(int clientX, int clientY, String username,
			String bareJid)
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onAvatarClicked(clientX,clientY,username,bareJid);
		}
		catch(e)
		{
		}
	}-*/;

	public native void onAvatarMouseOver(int clientX, int clientY, String username,
			String bareJid) 
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onAvatarMouseOver(clientX,clientY,username,bareJid);
		}
		catch(e)
		{
		}
	}-*/;
	
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onAvatarMouseOut(int, int, java.lang.String, java.lang.String)
	 */
	public native void onAvatarMouseOut(int clientX, int clientY, String usrname,
			String bareJid) 
	/*-{
	var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
	try
	{
		delegate.onAvatarMouseOut(clientX,clientY,username,bareJid);
	}
	catch(e)
	{
	}
}-*/;

	public native void onStatusTextUpdated(String text) 
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onStatusTextUpdated(text);
		}
		catch(e)
		{
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onMessageReceive(java.lang.String, java.lang.String)
	 */
	public native void onMessageReceive(String jid, String message) 
	/*-{
		var delegate = this.@ru.it.lecm.im.client.NativeClientListener::delegate;
		try
		{
			delegate.onMessageReceive(jid,message);
		}
		catch(e)
		{
		}
	}-*/;

	

}
