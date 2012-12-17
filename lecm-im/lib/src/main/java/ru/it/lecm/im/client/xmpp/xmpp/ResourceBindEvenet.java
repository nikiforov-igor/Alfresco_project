package ru.it.lecm.im.client.xmpp.xmpp;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.events.IQEvent;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.JID;

public class ResourceBindEvenet extends IQEvent {

	private final JID bindedJid;

	public ResourceBindEvenet(IQ iq, JID new_jid) {
		super(iq);
		this.bindedJid = new_jid;
	}

	public JID getBindedJid() {
		return bindedJid;
	}

}
