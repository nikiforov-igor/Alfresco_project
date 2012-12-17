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
 * Mar 31, 2010
 */
package ru.it.lecm.im.client.xmpp.xmpp.xeps.muc;

import ru.it.lecm.im.client.xmpp.stanzas.Message;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public interface GroupChatListener 
{
	void onGCJoinDeny(GroupChatEvent gcEvent);
	void onGCLeaved(GroupChatEvent gcEvent);
	void onGCJoined(GroupChatEvent gcEvent);
	void onUserLeaved(GroupChatEvent gcEvent);
	void onUserPresenceChange(GroupChatEvent gcEvent);
	void onMessage(Message message);
}
