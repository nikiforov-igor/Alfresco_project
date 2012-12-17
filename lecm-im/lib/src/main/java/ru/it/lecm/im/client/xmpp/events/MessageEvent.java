package ru.it.lecm.im.client.xmpp.events;

import ru.it.lecm.im.client.xmpp.stanzas.Message;

public class MessageEvent extends Event {
	protected final Message message;

	public MessageEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
