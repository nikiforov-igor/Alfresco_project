package ru.it.lecm.im.client.xmpp;

public interface RequestClientCallback 
{
	void onFailure(Throwable caught);
	void onSuccess(int id,String result);
}
