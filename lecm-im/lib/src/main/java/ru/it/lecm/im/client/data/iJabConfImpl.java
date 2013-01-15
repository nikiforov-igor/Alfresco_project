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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class iJabConfImpl extends JavaScriptObject implements iJabConf
{
	protected iJabConfImpl()
	{
		
	}

	public final native String getTheme() 
	/*-{
		try
		{
			return this.theme;
		}
		catch(e)
		{
			return "standard";
		}
	}-*/;

	public final native XmppConfImpl getXmppConf() 
	/*-{
		try
		{
			return this.xmpp;
		}
		catch(e)
		{
			return null;
		}
	}-*/;

	public final static native iJabConfImpl getConf() 
	/*-{
		return $wnd.iJabConf;
	}-*/;

	public final native JsArray<LinkItemImpl> getShortcuts() 
	/*-{
		try
		{
			return this.shortcuts;
		}
		catch(e)
		{
			return [];
		}
	}-*/;

	public final native JsArray<LinkItemImpl> getTools() 
	/*-{
		try
		{
			return this.tools;
		}
		catch(e)
		{
			return [];
		}
	}-*/;

	public final native String avatarUrl() 
	/*-{
		try
		{
			return this.avatar_url;
		}
		catch(e)
		{
			return null;
		}
	}-*/;


	public final native boolean isRosterManageEnabled() 
	/*-{
		try
		{
			return this.enable_roster_manage;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean disableToolBox() 
	/*-{
		try
		{
			return this.disable_toolbox;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean disableOptionsSetting() 
	/*-{
		try
		{
			return this.disable_option_setting;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isTitleNotifyDisabled() 
	/*-{
		try
		{
			return this.disable_msg_browser_prompt;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isHideOnlineGroup() 
	/*-{
		try
		{
			return this.hide_online_group;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isHidePoweredBy() 
	/*-{
		try
		{
			return this.hide_poweredby;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isLoginDialogEnabled() 
	/*-{
		try
		{
			return this.enable_login_dialog;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isBarExpandDefault()
	/*-{
		try
		{
			return this.expand_bar_default;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isEnableTalkToStranger() 
	/*-{
		try
		{
			return this.enable_talkto_stranger;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native boolean isEnableTalkToSpam() 
	/*-{
		try
		{
			if(this.talkto_spam_function == null)
				return false;
			return this.enable_talkto_spam;
		}
		catch(e)
		{
			return false;
		}
	}-*/;


	public final native JavaScriptObject talkToSpamFunction() 
	/*-{
		try
		{
			return this.talkto_spam_function;
		}
		catch(e)
		{
			return null;
		}
	}-*/;


	public final native int talkToSpamRepeat() 
	/*-{
		try
		{
			return this.talkto_spam_repeat;
		}
		catch(e)
		{
			return 1;
		}
	}-*/;
}
