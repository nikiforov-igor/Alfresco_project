package ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving;

import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

public abstract class MessageArchiveRequestHandler implements ResponseHandler {

	private MessageArchivingPlugin plugin;

	public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) {
	}

	public final void onResult(final IQ iq) 
	{
		if(plugin == null)
			plugin = Session.instance().getMessageArchivingPlugin();
		ResultSet rs = plugin.makeResultSet(iq);
		onSuccess(iq, rs);
	}

	public abstract void onSuccess(final IQ iq, ResultSet rs);

	void setPlugin(MessageArchivingPlugin plugin) {
		this.plugin = plugin;
	}
	

}
