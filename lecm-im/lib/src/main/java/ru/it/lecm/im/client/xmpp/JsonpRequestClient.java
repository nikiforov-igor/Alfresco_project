package ru.it.lecm.im.client.xmpp;

import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JsonpRequestClient extends RequestClient
{
	private final JsonpRequestBuilder request = new JsonpRequestBuilder();
	public JsonpRequestClient(String url) 
	{
		super(url);
	}

	@Override
	public int sendRequest(String body, final RequestClientCallback callback) 
	{
		ID++;
		StringBuffer uri = new StringBuffer(url);
		uri.append(url.contains("?") ? "&" : "?");
		uri.append("xml=").append(URL.encodeComponent(body));
		request.setTimeout(timeoutMillis);
		final int id = ID;
		request.requestString(uri.toString(), new AsyncCallback<String>()
		{

			public void onFailure(Throwable caught) 
			{
				callback.onFailure(caught);
			}

			public void onSuccess(String result) 
			{
				callback.onSuccess(id,result);
			}
			
		});
		return ID;
	}

}
