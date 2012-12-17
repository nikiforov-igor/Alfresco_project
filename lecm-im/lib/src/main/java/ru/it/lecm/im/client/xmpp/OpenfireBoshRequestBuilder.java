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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */

public class OpenfireBoshRequestBuilder {
	  private int timeout = 10000;
	  private String callbackParam = "callback";
	  private String failureCallbackParam = null;

	  /**
	   * @return the name of the callback url parameter to send to the server. The default value is
	   *     "callback".
	   */
	  public String getCallbackParam() {
	    return callbackParam;
	  }

	  /**
	   * @return the name of the failure callback url parameter to send to the server. The default is
	   *     null.
	   */
	  public String getFailureCallbackParam() {
	    return failureCallbackParam;
	  }

	  /**
	   * @return the expected timeout (ms) for this request.
	   */
	  public int getTimeout() {
	    return timeout;
	  }

	  public OpenfireBoshRequest<Boolean> requestBoolean(String url, AsyncCallback<Boolean> callback) {
	    return send(url, callback, false);
	  }

	  public OpenfireBoshRequest<Double> requestDouble(String url, AsyncCallback<Double> callback) {
	    return send(url, callback, false);
	  }

	  public OpenfireBoshRequest<Integer> requestInteger(String url, AsyncCallback<Integer> callback) {
	    return send(url, callback, true);
	  }

	  /**
	   * Sends a JSONP request and expects a JavaScript object as a result. The caller can either use
	   * {@link com.google.gwt.json.client.JSONObject} to parse it, or use a JavaScript overlay class.
	   */
	  public <T extends JavaScriptObject> OpenfireBoshRequest<T> requestObject(String url,
	      AsyncCallback<T> callback) {
	    return send(url, callback, false);
	  }

	  public OpenfireBoshRequest<String> requestString(String url, AsyncCallback<String> callback) {
	    return send(url, callback, false);
	  }

	  /**
	   * Sends a JSONP request and does not expect any results.
	   */
	  public void send(String url) {
	    send(url, null, false);
	  }

	  /**
	   * Sends a JSONP request, does not expect any result, but still allows to be notified when the
	   * request has been executed on the server.
	   */
	  public OpenfireBoshRequest<Void> send(String url, AsyncCallback<Void> callback) {
	    return send(url, callback, false);
	  }

	  /**
	   * @param callbackParam The name of the callback url parameter to send to the server. The default
	   *     value is "callback".
	   */
	  public void setCallbackParam(String callbackParam) {
	    this.callbackParam = callbackParam;
	  }

	  /**
	   * @param failureCallbackParam The name of the failure callback url parameter to send to the
	   *     server. The default is null.
	   */
	  public void setFailureCallbackParam(String failureCallbackParam) {
	    this.failureCallbackParam = failureCallbackParam;
	  }

	  /**
	   * @param timeout The expected timeout (ms) for this request. The default is 10s.
	   */
	  public void setTimeout(int timeout) {
	    this.timeout = timeout;
	  }

	  private <T> OpenfireBoshRequest<T> send(String url, AsyncCallback<T> callback, boolean expectInteger) {
		  OpenfireBoshRequest<T> request = new OpenfireBoshRequest<T>(callback, timeout, expectInteger, callbackParam,
	        failureCallbackParam);
	    request.send(url);
	    return request;
	  }
	}
