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
package ru.it.lecm.im.client.xmpp.xmpp.message;

import java.util.ArrayList;
import java.util.List;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.citeria.ElementCriteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.Message;

public class MessagePlugin implements Plugin {

	private ChatManager<?> chatManager;

	private List<MessageListener> messageListeners = new ArrayList<MessageListener>();

	private final Session session;

	public MessagePlugin(Session session) {
		this.session = session;
	}

	public void addMessageListener(MessageListener listener) {
		this.messageListeners.add(listener);
	}

	public Criteria getCriteria() {
		return ElementCriteria.name("message");
	}

	public PluginState getStatus() {
		return null;
	}

	public boolean process(Packet element) {
		Message message = new Message(element);

		if (chatManager != null)
			chatManager.process(message);

		for (int i = 0; i < this.messageListeners.size(); i++) {
			this.messageListeners.get(i).onMessageReceived(message);
		}
		return true;
	}

	public void removeMessageListener(MessageListener listener) {
		this.messageListeners.remove(listener);
	}

	public void reset() {
	}

	public void sendChatMessage(JID to, String body, String thread, Message.ChatState chatState) {
		Message msg = new Message(Message.Type.chat, to, null, body, thread);
		msg.setChatState(chatState);
		session.send(msg);
	}

	public void sendChatMessage(JID to, String body, String thread, Message.ChatState chatState, String nick) {
		Message msg = new Message(Message.Type.chat, to, null, body, thread);
		msg.setChatState(chatState);
		msg.setExtNick(nick);
		session.send(msg);
	}

	public void sendChatMessage(Message msg) {
		session.send(msg);
	}

	public void setChatManager(ChatManager<?> chatManager2) {
		this.chatManager = chatManager2;
	}

}
