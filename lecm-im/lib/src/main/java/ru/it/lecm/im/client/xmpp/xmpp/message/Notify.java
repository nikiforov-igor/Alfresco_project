package ru.it.lecm.im.client.xmpp.xmpp.message;

import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.AbstractStanza;

public class Notify extends AbstractStanza
{
	public Notify(Packet packet)
	{
		super(packet);
	}
	
	public Notify(String str)
	{
		super(str);
	}
	
	public String getTitle()
	{
		Packet title = getFirstChild("title");
		return title == null?null:title.getCData(); 
	}
	
	public String getPublisher()
	{
		Packet from = getFirstChild("from");
		return from == null?null:from.getCData();
	}
	
	public String getContent()
	{
		Packet content = getFirstChild("content");
		return content == null?null:content.getCData();
	}
	
	public String getUrl()
	{
		Packet url = getFirstChild("url");
		return url == null?null:url.getCData();
	}
	
	public String getNotifyType()
	{
		return getAtribute("type");
	}
	
	public String getTimeout()
	{
		return getAtribute("timeout");
	}
}
