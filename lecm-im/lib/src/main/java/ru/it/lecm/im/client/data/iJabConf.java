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

public interface iJabConf
{
	public enum ClientType {xmpp,ijabcometd};
	String getTheme();
	String avatarUrl();
	boolean isRosterManageEnabled();
	boolean disableToolBox();
	boolean disableOptionsSetting();
	boolean isTitleNotifyDisabled();
	boolean isLoginDialogEnabled();
	boolean isHidePoweredBy();
	boolean isHideOnlineGroup();
	boolean isEnableTalkToStranger();
	boolean isBarExpandDefault();
	//for talkto spam
	boolean isEnableTalkToSpam();
	JavaScriptObject talkToSpamFunction();
	int talkToSpamRepeat();
	//
	XmppConf getXmppConf();
	JsArray<LinkItemImpl> getTools();
	JsArray<LinkItemImpl> getShortcuts();
}
