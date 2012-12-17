package ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving;

import ru.it.lecm.im.client.xmpp.packet.Packet;

public interface MessageArchivingListener {

	void onReceiveSetChat(final Packet iq, ResultSet rs);
}
