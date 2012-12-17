package ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch;

import java.util.List;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;

public class JabberSearchPlugin implements Plugin {

	private final Session session;

	public JabberSearchPlugin(Session session) {
		this.session = session;
	}

	public Criteria getCriteria() {
		return null;
	}

	public PluginState getStatus() {
		return null;
	}

	public boolean process(Packet element) {
		return false;
	}

	public void reset() {
	}

	public void search(final String serverNode,List<Field> queryValue,final JabberSearchResponseHandler handler) {
		final IQ iq = new IQ(IQ.Type.set);
		iq.setAttribute("id", Session.nextId());
		if(serverNode.length()>0)
			iq.setTo(JID.fromString(serverNode));

		Packet query = iq.addChild("query", "jabber:iq:search");
		Packet x = query.addChild("x", "jabber:x:data");
		x.setAttribute("type", "submit");
		for(Field field:queryValue)
		{
			Packet fieldPacket = x.addChild("field",null);
			fieldPacket.setAttribute("var", field.var);
			fieldPacket.setAttribute("type", field.type);
			fieldPacket.addChild("value", null).setCData(field.value);
		}

		this.session.addResponseHandler(iq, handler);
	}
	
	public void queryFields(final String serverNode,final JabberSearchResponseHandler handler)
	{
		final IQ iq = new IQ(IQ.Type.get);
		iq.setAttribute("id", Session.nextId());
		if(serverNode.length()>0)
			iq.setTo(JID.fromString(serverNode));
		iq.addChild("query", "jabber:iq:search");
		this.session.addResponseHandler(iq, handler);
	}

}
