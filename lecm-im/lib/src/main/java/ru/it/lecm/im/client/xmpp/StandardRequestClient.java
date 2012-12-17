package ru.it.lecm.im.client.xmpp;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class StandardRequestClient extends RequestClient
{
	final private RequestBuilder request;
	public StandardRequestClient(String url) {
		super(url);
		request = new RequestBuilder(RequestBuilder.POST,url); 
		request.setHeader("Connection", "close");
	}

	@Override
	public int sendRequest(String body, final RequestClientCallback callback)
	{
		ID++;
		final int id  = ID;
		request.setTimeoutMillis(timeoutMillis);
		try 
		{
			request.sendRequest(body, new RequestCallback()
			{

				public void onError(Request request, Throwable exception) 
				{
					callback.onFailure(exception);
					
				}

				public void onResponseReceived(Request request, Response response) 
				{
					callback.onSuccess(id, response.getText());					
				}
				
			});
		} 
		catch (RequestException e) 
		{
			e.printStackTrace();
		}
		return ID;
	}
}
