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
 * Apr 2, 2010
 */
package ru.it.lecm.im.client.xmpp.gateway;

import java.util.Map;

import ru.it.lecm.im.client.xmpp.*;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class GatewayPlugin implements Plugin
{
	private final Session session;
	public GatewayPlugin(final Session session)
	{
		this.session = session;
	}
	
	public void getInfo(String gateway,ResponseHandler responseHandler)
	{
		final IQ iq = new IQ(IQ.Type.get);
		iq.setTo(JID.fromString(gateway));
		iq.addChild("query", "jabber:iq:register");
		this.session.addResponseHandler(iq, responseHandler);
	}
	
	public void register(String gateway,Map<String,String> fields,ResponseHandler responseHandler)
	{
		final IQ iq = new IQ(IQ.Type.set);
		iq.setTo(JID.fromString(gateway));
		Packet query = iq.addChild("query", "jabber:iq:register");
		for(String key:fields.keySet())
		{
			Packet field = query.addChild(key, null);
			field.setCData(fields.get(key));
		}
		this.session.addResponseHandler(iq, responseHandler);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.Plugin#getCriteria()
	 */
	public Criteria getCriteria() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.Plugin#getStatus()
	 */
	public PluginState getStatus() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.Plugin#process(ru.it.lecm.im.client.xmpp.packet.Packet)
	 */
	public boolean process(Packet stanza) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.Plugin#reset()
	 */
	public void reset() {
	}

}
