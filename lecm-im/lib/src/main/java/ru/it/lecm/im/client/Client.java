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
import com.google.gwt.core.client.JsArrayString;
import ru.it.lecm.im.client.xmpp.JID;

import java.util.ArrayList;
import java.util.List;

public abstract class Client 
{
	protected final List<ClientListener> listeners = new ArrayList<ClientListener>();

    protected final List<VisibilityListener> visibilityListeners = new ArrayList<VisibilityListener>();

    private boolean isVisible;
    public boolean getIsVisible()
    {
        return isVisible;
    }

    public boolean toggleIsVisible()
    {
        if (this.isVisible)
        {
            this.fireOnHide();
        }
        else
        {
            this.fireOnShow();
        }

        this.isVisible = !this.isVisible;
        return this.isVisible;
    }

    private void fireOnShow() {
        for(VisibilityListener l:visibilityListeners)
        {
            l.onShow();
        }
    }

    private void fireOnHide() {
        for(VisibilityListener l:visibilityListeners)
        {
            l.onHide();
        }
    }

    public void addVisibilityListener(VisibilityListener handler)
    {
        visibilityListeners.add(handler);
    }

    abstract void  run();
	public abstract boolean suspend();
	public abstract void resume(); 
	public abstract void login(final String id,final String password);
	public abstract void loginWithStatus(final String id,final String password,final String status);
	public abstract String getNickname();
	public abstract String getStatusText();
	public abstract void setStatusText(final String statusText);
	public abstract String getStatus();
	public abstract void setStatus(final String status);
	public abstract void addRoster(JsArrayString users,final String group);
	public abstract void removeRoster(JsArrayString users);
	public abstract void joinMUC(final String room,final String nick);
	public abstract void leaveMUC(final String room);
	public abstract void addUsersToBlacklist(JsArrayString users);
	public abstract void removeUsersFromBlacklist(JsArrayString users);
	
	public abstract void autoLogin();
	abstract void logout();
	abstract void talkTo(final String jid);
	
	public abstract boolean isConnected();
	public abstract List<String> getGroups();
	protected boolean isLogined = false;
	public void onAvatarClicked(int clientX,int clientY,final String bareJid)
	{
		fireOnAvatarClicked(clientX,clientY,JID.fromString(bareJid).getNode(),bareJid);
	}
	
	public void onAvatarMouseOver(int clientX,int clientY,final String bareJid)
	{
		fireOnAvatarMouseOver(clientX,clientY,JID.fromString(bareJid).getNode(),bareJid);
	}
	
	public void onAvatarMouseOut(int clientX,int clientY,final String bareJid)
	{
		fireOnAvatarMouseOut(clientX,clientY, JID.fromString(bareJid).getNode(),bareJid);
	}
	
	public void onStatusTextUpdate(final String text)
	{
		fireOnStatusTextUpdated(text);
	}
	
	public void onMessageReceive(final String jid,final String message)
	{
		fireOnMessageReceive(jid,message);
	}
	
	public void addNativeListener(JavaScriptObject jso)
	{
		addClientListener(new NativeClientListener(jso));
	}
	
	public void addClientListener(ClientListener handler)
	{
		listeners.add(handler);
	}
	
	public void removeClientListener(ClientListener handler)
	{
		listeners.remove(handler);
	}
	
	public boolean isLogined()
	{
		return isLogined;
	}
	
	protected void fireOnLogout()
	{
		isLogined = false;
		for(ClientListener l:listeners)
		{
			l.onLogout();
		}
	}
	
	protected void fireOnAvatarClicked(int clientX,int clientY,final String username,final String bareJid)
	{
		for(ClientListener l:listeners)
		{
			l.onAvatarClicked(clientX,clientY,username,bareJid);
		}
	}
	
	protected void fireOnAvatarMouseOver(int clientX,int clientY,final String username,final String bareJid)
	{
		for(ClientListener l:listeners)
		{
			l.onAvatarMouseOver(clientX,clientY,username,bareJid);
		}
	}
	
	protected void fireOnAvatarMouseOut(int clientX,int clientY,final String username,final String bareJid)
	{
		for(ClientListener l:listeners)
		{
			l.onAvatarMouseOut(clientX,clientY,username,bareJid);
		}
	}
	
	protected void fireOnStatusTextUpdated(final String text)
	{
		for(ClientListener l:listeners)
		{
			l.onStatusTextUpdated(text);
		}
	}
	
	protected void fireOnError(final String error)
	{
		isLogined = false;
		for(ClientListener l:listeners)
		{
			l.onError(error);
		}
	}
	
	protected void fireOnBeforeLogin()
	{
		for(ClientListener l:listeners)
		{
			l.onBeforeLogin();
		}
	}
	
	protected void fireOnEndLogin()
	{
		isLogined = true;
		for(ClientListener l:listeners)
		{
			l.onEndLogin();
		}
	}
	
	protected void fireOnSuspend()
	{
		isLogined = false;
		for(ClientListener l:listeners)
		{
			l.onSuspend();
		}
	}
	
	protected void fireOnResume()
	{
		for(ClientListener l:listeners)
		{
			l.onResume();
		}
	}
	
	protected void fireOnMessageReceive(final String jid,final String message)
	{
		for(ClientListener l:listeners)
		{
			l.onMessageReceive(jid, message);
		}
	}
}
