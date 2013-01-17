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
package ru.it.lecm.im.client.listeners;

public interface ClientListener 
{
	void onSuspend();
	void onResume();
	void onLogout();
	void onError(final String error);
	void onBeforeLogin();
	void onEndLogin();
	void onAvatarClicked(int clientX,int clientY,final String username,final String bareJid);
	void onAvatarMouseOver(int clientX,int clientY,final String username,final String bareJid);
	void onAvatarMouseOut(int clientX,int clientY,final String usrname,final String bareJid);
	void onStatusTextUpdated(final String text);
}


