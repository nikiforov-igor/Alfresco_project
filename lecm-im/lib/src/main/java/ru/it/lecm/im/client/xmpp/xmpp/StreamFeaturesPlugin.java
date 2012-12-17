package ru.it.lecm.im.client.xmpp.xmpp;

import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.citeria.ElementCriteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;

public class StreamFeaturesPlugin implements Plugin
{

	private final Criteria CRIT = ElementCriteria.name("stream:features");

	private PluginState state = PluginState.NONE;

	public Criteria getCriteria() 
	{
		return CRIT;
	}

	public PluginState getStatus() 
	{
		return null;
	}

	public boolean process(Packet stanza) 
	{
		setState(PluginState.SUCCESS);
		return false;
	}

	public void reset() 
	{
		setState(PluginState.NONE);
	}

	public void setState(PluginState state) 
	{
		this.state = state;
	}

	public PluginState getState() 
	{
		return state;
	}

}
