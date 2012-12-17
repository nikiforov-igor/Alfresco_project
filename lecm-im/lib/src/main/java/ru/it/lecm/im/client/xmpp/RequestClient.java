package ru.it.lecm.im.client.xmpp;

public abstract class RequestClient 
{
	protected final String url;
	protected int timeoutMillis = 30;
	protected static int ID = 0;
	public RequestClient(String url)
	{
		this.url = url;
	}
	
	public abstract int sendRequest(String body,RequestClientCallback callback);
	
	public void setTimeoutMillis(int timeoutMillis)
	{
		if (timeoutMillis < 0) 
		{
		      throw new IllegalArgumentException("Timeouts cannot be negative");
		 }

		this.timeoutMillis = timeoutMillis;
	}
	
}
