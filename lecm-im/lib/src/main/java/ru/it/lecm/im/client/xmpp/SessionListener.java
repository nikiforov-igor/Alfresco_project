package ru.it.lecm.im.client.xmpp;

import ru.it.lecm.im.client.xmpp.Connector.BoshErrorCondition;

public interface SessionListener 
{
	void onBeforeLogin();
	void onEndLogin();
	void onLoginOut();
	void onError(BoshErrorCondition boshErrorCondition,String message);
	void onResumeSuccessed();
	void onResumeFailed();
	void onSelfVCard();
}
