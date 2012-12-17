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
 * Apr 1, 2010
 */
package ru.it.lecm.im.client.ui;

import java.util.HashMap;
import java.util.Map;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import ru.it.lecm.im.client.xmpp.JID;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCUserListWidget extends Composite 
{
	private final FlowPanel widget;
	private Map<JID,MUCUserItem> users = new HashMap<JID,MUCUserItem>();
	public MUCUserListWidget()
	{
		widget = new FlowPanel();
		initWidget(widget);
	}
	
	public void updatePresence(Presence presence)
	{
		JID jid = presence.getFrom();
		MUCUserItem item = users.get(jid);
		if(item == null)
		{
			item = new MUCUserItem(presence);
			users.put(jid, item);
			widget.add(item);
			item.setItemOdd(widget.getWidgetIndex(item)%2 == 0);
		}
		else
		{
			item.updatePresence(presence);
		}
	}
	
	public void removeItem(Presence presence)
	{
		JID jid = presence.getFrom();
		MUCUserItem item = users.get(jid);
		if(item!=null)
		{
			widget.remove(item);
			ensureOdd();
		}
		users.remove(jid);
	}
	
	private void ensureOdd()
	{
		for(int index=0;index<widget.getWidgetCount();index++)
		{
			MUCUserItem item = (MUCUserItem)widget.getWidget(index);
			item.setItemOdd(index%2 == 0);
		}
	}
}
