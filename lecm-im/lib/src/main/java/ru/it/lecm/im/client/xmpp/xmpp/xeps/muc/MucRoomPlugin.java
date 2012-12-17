package ru.it.lecm.im.client.xmpp.xmpp.xeps.muc;

import java.util.ArrayList;
import java.util.List;

import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.citeria.ElementCriteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;

public class MucRoomPlugin implements Plugin
{
	private final Session session;
	
	private List<MucRoomListener> listeners = new ArrayList<MucRoomListener>();
	public MucRoomPlugin(Session session)
	{
		this.session = session;
	}

	public Criteria getCriteria() 
	{
		return ElementCriteria.name("iq").add(
				ElementCriteria.name("query", new String[] { "xmlns" }, new String[] { "http://jabber.org/protocol/disco#items" }));
	}

	public PluginState getStatus() 
	{
		return null;
	}
	
	public boolean process(Packet iq) 
	{
		List<MucRoomItem> rooms = new ArrayList<MucRoomItem>();
		if(iq.getAtribute("type").equals("result"))
		{
			Packet query = iq.getFirstChild("query");
			for (Packet item : query.getChildren()) 
			{
				MucRoomItem room = new MucRoomItem(item.getAtribute("jid"),item.getAtribute("name"));
				rooms.add(room);
			}
		}
		fireOnRoomListUpdate(rooms);
		return true;
	}

	public void reset() 
	{
		
	}
	
	public void getMucRoomList(final String mucServerNode)
	{
		IQ iq = new IQ(IQ.Type.get);
		iq.setAttribute("id", "" + Session.nextId());
		iq.setAttribute("to", mucServerNode);
		iq.addChild("query", "http://jabber.org/protocol/disco#items");
		session.send(iq);
	}
	
	public void addListener(MucRoomListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeListener(MucRoomListener listener)
	{
		listeners.remove(listener);
	}
	
	private void fireOnRoomListUpdate(List<MucRoomItem> rooms)
	{
		for(MucRoomListener l:listeners)
		{
			l.onRoomListUpdate(rooms);
		}
	}
}
