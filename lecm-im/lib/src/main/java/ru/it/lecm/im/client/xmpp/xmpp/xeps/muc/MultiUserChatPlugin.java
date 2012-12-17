/*
 * tigase-xmpp4gwt
 * Copyright (C) 2007 "Bartosz Małkowski" <bmalkow@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package ru.it.lecm.im.client.xmpp.xmpp.xeps.muc;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.citeria.ElementCriteria;
import ru.it.lecm.im.client.xmpp.citeria.Or;
import ru.it.lecm.im.client.xmpp.events.Event;
import ru.it.lecm.im.client.xmpp.events.Events;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;

public class MultiUserChatPlugin implements Plugin {

	public final Criteria CRIT = new Or(ElementCriteria.name("presence"), ElementCriteria.name("message", new String[] { "type" },
			new String[] { "groupchat" }));

	private final GroupChatManager groupChatManager;

	private final Session session;

	public MultiUserChatPlugin(Session session) {
		groupChatManager = new GroupChatManager(session.getEventsManager());
		this.session = session;
	}

	public GroupChat createGroupChat(final JID roomJid, final String nickname, String password) {
		GroupChat gc = new GroupChat(roomJid.getBareJID(), groupChatManager, this);
		gc.setNickname(nickname);
		gc.setPassword(password);
		groupChatManager.add(gc);
		session.getEventsManager().fireEvent(Events.groupChatCreated, new GroupChatEvent((Presence) null, gc));
		return gc;
	}
	
	public GroupChatManager getGroupChatManager()
	{
		return groupChatManager;
	}

	void fireEvent(Enum<?> eventType, Event event) {
		session.getEventsManager().fireEvent(eventType, event);
	}

	public Criteria getCriteria() {
		return CRIT;
	}

	public PluginState getStatus() {
		return null;
	}

	public boolean process(Packet stanza) {
		if ("message".equals(stanza.getName())) {
			Message message = new Message(stanza);
			boolean processed = groupChatManager.process(message);
			return processed;
		} else if ("presence".equals(stanza.getName())) {
			Presence presence = new Presence(stanza);
			boolean processed = groupChatManager.process(presence);
			return processed;
		}
		return false;
	}

	public void reset() {
	}

	public void send(Message message) {
		session.send(message);
	}

	public void send(Presence presence) {
		session.send(presence);
	}

}
