package ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch;

import java.util.ArrayList;
import java.util.List;

import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

public abstract class JabberSearchResponseHandler implements ResponseHandler {

	private String getChildCData(Packet packet, String name) {
		Packet v = packet.getFirstChild(name);
		if (v != null) {
			return v.getCData();
		} else
			return null;
	}

	public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) {
	}

	public final void onResult(final IQ iq) 
	{
		Packet query = iq.getFirstChild("query");
		IQ queryIQ = new IQ(query);
		Packet x = queryIQ.getChild("x", "jabber:x:data");
		if(x.getAtribute("type").equals("form"))
		{
			List<? extends Packet> fileds = x.getChildren();
			List<Field> form = new ArrayList<Field>();
			for(Packet packet:fileds)
			{
				if(!packet.getName().equals("field"))
					continue;
				String type = packet.getAtribute("type");
				String label = packet.getAtribute("label");
				String var = packet.getAtribute("var");
				form.add(new Field(type,label,var,null));
			}
			onForm(iq,form);
		}
		else if(x.getAtribute("type").equals("result"))
		{
			List<? extends Packet> items = x.getChildren();
			List<Field> titles = new ArrayList<Field>();
			List<List<Field>> values = new ArrayList<List<Field>>();
			for(Packet item:items)
			{
				if(item.getName().equals("reported"))
				{
					List<? extends Packet> fields = item.getChildren();
					for(Packet field:fields)
					{
						if(!field.getName().equals("field"))
							continue;
						String var = field.getAtribute("var");
						String label = field.getAtribute("label");
						titles.add(new Field(null,label,var,null));
					}
				}
				else if(item.getName().equals("item"))
				{
					List<Field> itemValue = new ArrayList<Field>();
					List<? extends Packet> fields = item.getChildren();
					for(Packet field:fields)
					{
						if(!field.getName().equals("field"))
							continue;
						String var = field.getAtribute("var");
						String value = getChildCData(field,"value");
						itemValue.add(new Field(null,null,var,value));
					}
					values.add(itemValue);
				}
			}
			onResult(iq,titles,values);
		}

	}

	public abstract void onForm(final IQ iq,final List<Field> fields);
	public abstract void onResult(final IQ iq,final List<Field> titles,final List<List<Field>> values);

}
