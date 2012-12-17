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
 * Mar 4, 2010
 */
package ru.it.lecm.im.client.xmpp;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class OpenfireBoshRequestClient extends RequestClient
{
	private final OpenfireBoshRequestBuilder request = new OpenfireBoshRequestBuilder();

	/**
	 * @param url
	 */
	public OpenfireBoshRequestClient(String url) 
	{
		super(url);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.RequestClient#sendRequest(java.lang.String, ru.it.lecm.im.client.xmpp.RequestClientCallback)
	 */
	@Override
	public int sendRequest(String body, final RequestClientCallback callback) 
	{
		ID++;
		StringBuffer uri = new StringBuffer(url);
		uri.append(url.contains("?") ? "&" : "?");
		uri.append(URL.encodeComponent(body));
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
