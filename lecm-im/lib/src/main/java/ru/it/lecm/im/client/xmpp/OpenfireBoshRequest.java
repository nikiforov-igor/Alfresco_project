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

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
import com.google.gwt.jsonp.client.TimeoutException;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A JSONP request that is waiting for a response. The request can be canceled.
 * 
 * @param <T> the type of the response object.
 */
public class OpenfireBoshRequest<T> 
{
	private static int ID = 0;
	/**
	   * A global JS variable that holds the next index to use.
	   */
	  @SuppressWarnings("unused") // accessed from JSNI
	  private static final String CALLBACKS_COUNTER_NAME = "__ijab_bosh_counter__";

	  /**
	   * A global JS object that contains callbacks of pending requests.
	   */
	  @SuppressWarnings("unused") // accessed from JSNI
	  private static final String CALLBACKS_NAME = "__ijab_bosh__";
	  //private static final JavaScriptObject CALLBACKS = getOrCreateCallbacksObject();
	  
	  /**
	   * @return the next ID to use, incrementing the global counter.
	   */
	  private static int getAndIncrementCallbackCounter()
	  {
		  return ID++;
	  }
	 
	  private static String nextCallbackId() {
	    return "iJabBosh" + getAndIncrementCallbackCounter();
	  }

	  private final String callbackId;

	  private final int timeout;

	  private final AsyncCallback<T> callback;

	  /**
	   * Whether the result is expected to be an integer or not.
	   */
	  @SuppressWarnings("unused") // used by JSNI
	  private final boolean expectInteger;

	  /**
	   * Create a new JSONP request.
	   * 
	   * @param callback The callback instance to notify when the response comes
	   *          back
	   * @param timeout Time in ms after which a {@link TimeoutException} will be
	   *          thrown
	   * @param expectInteger Should be true if T is {@link Integer}, false
	   *          otherwise
	   * @param callbackParam Name of the url param of the callback function name
	   * @param failureCallbackParam Name of the url param containing the the
	   *          failure callback function name, or null for no failure callback
	   */
	  OpenfireBoshRequest(AsyncCallback<T> callback, int timeout, boolean expectInteger,
	      String callbackParam, String failureCallbackParam) {
	    callbackId = nextCallbackId();
	    this.callback = callback;
	    this.timeout = timeout+100;
	    this.expectInteger = expectInteger;
	  }

	  /**
	   * Cancels a pending request.
	   */
	  public void cancel() {
	  }

	  public AsyncCallback<T> getCallback() {
	    return callback;
	  }

	  public int getTimeout() {
	    return timeout;
	  }

	  @Override
	  public String toString() {
	    return "BoshRequest(id=" + callbackId + ")";
	  }

	  // @VisibleForTesting
	  String getCallbackId() {
	    return callbackId;
	  }

	  /**
	   * Sends a request using the JSONP mechanism.
	   * 
	   * @param baseUri To be sent to the server.
	   */
	  /*
	  void send(final String baseUri) {
		//unregisterCallbacks();
	    registerCallback();
	    StringBuffer uri = new StringBuffer(baseUri);
	    
	    script = Document.get().createScriptElement();
	    script.setType("text/javascript");
	    script.setId(callbackId);
	    script.setSrc(uri.toString());
	    timer = new Timer() {
	      @Override
	      public void run() {
	        onFailure(new TimeoutException("Timeout while calling " + baseUri));
	      }
	    };
	    timer.schedule(timeout);
	    getHeadElement().appendChild(script);
	  }
	  */
	  
	  @SuppressWarnings("unused") //call in jni
	private void timeFailed(String uri)
	  {
		  onFailure(new TimeoutException("Timeout while calling " + uri));
	  }
	  
	  void send(final String baseUri) 
	  {
		  sendRequestImpl(baseUri,callbackId,timeout);
	  }
	  
	  private native void sendRequestImpl(String url,String callbackID,int timeOut)
		/*-{
			var self = this;
			var callback = '_BOSH_';
			var script = document.createElement("script");
			script.setAttribute("src", url);
	   		script.setAttribute("type", "text/javascript");
	   		script.setAttribute("id",callbackID);
	   		window[callback] = function(data)
	   		{
			      self.@ru.it.lecm.im.client.xmpp.OpenfireBoshRequest::onSuccess(Ljava/lang/Object;)(data);
	   			  window[callback + "done"] = true;
	   		};
	   		
	   		setTimeout(function()
	   		{
	   			if (!window[callback + "done"]) 
	   			{
	       			self.@ru.it.lecm.im.client.xmpp.OpenfireBoshRequest::timeFailed(Ljava/lang/String;)(url);
	     		}
	     		
	     		document.body.removeChild(script);
			    delete window[callback];
			    delete window[callback + "done"];
	   		},timeOut);
	   		document.body.appendChild(script);
		}-*/;

	  @SuppressWarnings("unused") // used by JSNI
	  private void onFailure(String message) {
	    onFailure(new Exception(message));
	  }

	  private void onFailure(Throwable ex) {
	    try {
	      if (callback != null) {
	        callback.onFailure(ex);
	      }
	    } finally {
	    }
	  }

	  @SuppressWarnings("unused") // used by JSNI
	  private void onSuccess(T data) {
	    try {
	      if (callback != null) {
	        callback.onSuccess(data);
	      }
	    } finally {
	    }
	  }
}
