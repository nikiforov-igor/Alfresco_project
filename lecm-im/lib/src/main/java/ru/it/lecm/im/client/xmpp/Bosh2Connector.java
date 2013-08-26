/*
 * tigase-xmpp4gwt
 * Copyright (C) 2007 "Bartosz Małkowski" <bmalkow@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package ru.it.lecm.im.client.xmpp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.packet.PacketGwtImpl;
import ru.it.lecm.im.client.xmpp.packet.PacketImp;
import ru.it.lecm.im.client.xmpp.packet.PacketRenderer;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

import java.util.*;

public class Bosh2Connector implements Connector {

	enum State {
		connected, resume,connecting, disconnected
	}

	protected static final int MAX_ERRORS = 3;

	public static ErrorCondition getCondition(String name, int httpResult) {
		ErrorCondition result = null;
		if (name != null) {
			try {
				result = ErrorCondition.valueOf(name.replaceAll("-", "_"));
			} catch (Exception e) {
				result = null;
			}
		}
		if (result == null && httpResult != 200) {
			switch (httpResult) {
			case 400:
				result = ErrorCondition.bad_request;
				break;
			case 403:
				result = ErrorCondition.forbidden;
				break;
			case 404:
				result = ErrorCondition.item_not_found;
				break;
			case 405:
				result = ErrorCondition.not_allowed;
				break;
			default:
				result = ErrorCondition.undefined_condition;
				break;
			}
		}
		return result == null ? ErrorCondition.undefined_condition : result;
	}

	private Map<Request, String> activeRequests = new HashMap<Request, String>();

	private RequestBuilder builder;

	private boolean crossDomain = false;
	private RequestClient requestClient;
	private RequestClientCallback requestClientCallback;
	private Map<String, String> activeScriptRequests = new HashMap<String, String>();

	private int defaultTimeout = 30;

	private int errorCounter = 0;

	private String domain;
	private String host = null;
	int port = 5222;



	private List<ConnectorListener> listeners = new ArrayList<ConnectorListener>();


	private PacketRenderer renderer = new PacketRenderer()
	{
		public String render(Packet packet)
		{
			return packet.getAsString();
		}
	};

	private long rid;

	private String sid;

	private RequestCallback standardHandler;

	private State state = State.disconnected;

	private User user;

	public Bosh2Connector(final User user)
	{
		this.setUser(user);
		standardHandler = new RequestCallback()
		{

			public void onError(Request request, Throwable exception)
			{
				if(state == State.resume)
				{
					state = State.disconnected;
					fireOnResumeFailed();
					return;
				}
				final String lastSendedBody = activeRequests.remove(request);
				if (exception instanceof RequestTimeoutException)
				{
                    Log.log("Request too old. Trying again.");
					GWT.log("Request too old. Trying again.", null);
					DeferredCommand.addCommand(new Command() {
						public void execute()
						{
							if (lastSendedBody != null && state == State.connected && sid != null && sid.length() > 0)
								send(lastSendedBody, standardHandler);
							else
								continuousConnection(null);
						}
					});
				}
				else if (exception.getMessage().startsWith("Unable to read XmlHttpRequest.status;"))
				{
                    Log.log("Lost request. Ignored. Resend.");
					GWT.log("Lost request. Ignored. Resend.", null);
					if (lastSendedBody != null) {
						DeferredCommand.addCommand(new Command()
						{
							public void execute()
							{
								if (state == State.connected && sid != null && sid.length() > 0)
								{
									send(lastSendedBody, standardHandler);
								}
							}
						});
					}
				}
				else
				{
					state = State.disconnected;
                    Log.log("Connection error: " + exception.toString());
                    GWT.log("Connection error", exception);
					fireEventError(BoshErrorCondition.remote_connection_failed, null, "Response error: " + exception.getMessage());
				}
			}

			public void onResponseReceived(Request request, Response response)
			{
				if (state == State.disconnected)
					return;

				final String httpResponse = response.getText();
				final int httpStatusCode = response.getStatusCode();
				final String lastSendedBody = activeRequests.remove(request);

				Log.log(" IN (" + httpStatusCode + "): " + httpResponse);
				fireOnBodyReceive(response, httpResponse);

				final Packet body = parse(response.getText().replaceAll("&semi;", ";"));

				final String type = body == null ? null : body.getAtribute("type");
				final String receivedSid = body == null ? null : body.getAtribute("sid");
				String tmp = body == null ? null : body.getAtribute("rid");
				//final Long rid = tmp == null ? null : Long.valueOf(tmp);
				final String ack = body == null ? null : body.getAtribute("ack");
				tmp = body == null ? null : body.getAtribute("condition");
				if(tmp!=null)
					tmp = tmp.replace("-", "_");
				final BoshErrorCondition boshCondition = tmp == null ? null : BoshErrorCondition.valueOf(tmp);

				final String wait = body == null ? null : body.getAtribute("wait");
				final String inactivity = body == null ? null : body.getAtribute("inactivity");
				if (wait != null && inactivity != null)
				{
					try
					{
						int w = Integer.parseInt(wait);
						int i = Integer.parseInt(inactivity);
						int t = (w + i / 2) * 1000;
						builder.setTimeoutMillis(t);
						GWT.log("New timeout: " + t + "ms", null);
					}
					catch (Exception e)
					{
						GWT.log("Error in wait and inactivity attributes", e);
					}
				}

				if (httpStatusCode != 200 || body == null || type != null && ("terminate".equals(type) || "error".equals(type)))
				{
					if(state == State.resume)
					{
						state = State.disconnected;
						fireOnResumeFailed();
						return;
					}

                    Log.log("ERROR (" + httpStatusCode + "): " + httpResponse);
                    GWT.log("ERROR (" + httpStatusCode + "): " + httpResponse, null);
					ErrorCondition condition = body == null ? ErrorCondition.bad_request : ErrorCondition.undefined_condition;
					String msg = null;
					Packet error = body == null ? null : body.getFirstChild("error");
					if (error != null)
					{
						for (Packet c : error.getChildren())
						{
							String xmlns = c.getAtribute("xmlns");
							if ("text".equals(c.getName()))
							{
								msg = c.getCData();
								break;
							}
							else if (xmlns != null && "urn:ietf:params:xml:ns:xmpp-stanzas".equals(xmlns))
							{
								condition = getCondition(c.getName(), httpStatusCode);
							}
						}
					}

					if (condition == ErrorCondition.item_not_found)
					{
						state = State.disconnected;
						fireEventError(boshCondition, condition, msg);
					}
					else if (errorCounter < MAX_ERRORS)
					{
						errorCounter++;
						send(lastSendedBody, standardHandler);
					}
					else if (type != null && "terminate".equals(type))
					{
                        Log.log("Disconnected by server");
                        GWT.log("Disconnected by server", null);
						state = State.disconnected;
						fireDisconnectByServer(boshCondition, condition, msg);
					}
					else
					{
						state = State.disconnected;
						if (msg == null)
						{
							msg = "[" + httpStatusCode + "] " + condition.name().replace('_', '-');
						}
						fireEventError(boshCondition, condition, msg);
					}
				}
				else
				{
					errorCounter = 0;
					if (receivedSid != null && sid != null && !receivedSid.equals(sid))
					{
						if(state == State.resume)
							fireOnResumeFailed();
						state = State.disconnected;
						fireEventError(BoshErrorCondition.policy_violation, ErrorCondition.unexpected_request,
								"Unexpected session initialisation.");
					}
					else if (receivedSid != null && sid == null)
					{
						sid = receivedSid;
						//Cookies.setCookie(user.getResource()+"sid", sid, null, null, "/", false);
						state = State.connected;
					}

					if(state == State.resume)
					{
						fireOnResumeSuccessed();
						state = State.connected;
					}

					final List<? extends Packet> children = body.getChildren();
					if (children.size() > 0)
					{
						fireEventReceiveStanzas(children);
					}
					continuousConnection(ack);
				}
                Log.log("............sid value is:"+sid);
			}
		};

		//added by zhongfanglin@antapp.com
		requestClientCallback = new RequestClientCallback()
		{

			public void onFailure(Throwable caught)
			{
				if(state == State.resume)
				{
					fireOnResumeFailed();
					state = State.disconnected;
					return;
				}
				state = State.disconnected;
				Log.log("Connection error");
				fireEventError(BoshErrorCondition.remote_connection_failed, null, "Response error: request timeout or 404!");
			}

			public void onSuccess(int callbackID,String responseText)
			{
				if (state == State.disconnected)
					return;

				final String httpResponse = responseText;
				final String lastSendedBody = activeScriptRequests.remove(callbackID+"");

                Log.log(" IN:" + httpResponse);
				fireOnBodyReceive(null, httpResponse);

				final Packet body = parse(responseText.replaceAll("&semi;", ";"));

				final String type = body == null ? null : body.getAtribute("type");
				final String receivedSid = body == null ? null : body.getAtribute("sid");
				String tmp = body == null ? null : body.getAtribute("rid");
				//final Long rid = tmp == null ? null : Long.valueOf(tmp);
				final String ack = body == null ? null : body.getAtribute("ack");
				tmp = body == null ? null : body.getAtribute("condition");
				if(tmp!=null)
					tmp = tmp.replace("-", "_");
				final BoshErrorCondition boshCondition = tmp == null ? null : BoshErrorCondition.valueOf(tmp);

				final String wait = body == null ? null : body.getAtribute("wait");
				final String inactivity = body == null ? null : body.getAtribute("inactivity");
				if (wait != null && inactivity != null) {
					try {
						int w = Integer.parseInt(wait);
						int i = Integer.parseInt(inactivity);
						int t = (w + i / 2) * 1000;
						requestClient.setTimeoutMillis(t);
						GWT.log("New timeout: " + t + "ms", null);
					} catch (Exception e) {
						GWT.log("Error in wait and inactivity attributes", e);
					}
				}

				if ( body == null || type != null && ("terminate".equals(type) || "error".equals(type)))
				{
					if(state == State.resume)
					{
						state = State.disconnected;
						fireOnResumeFailed();
						return;
					}
					Log.log("ERROR : " + httpResponse);
					ErrorCondition condition = body == null ? ErrorCondition.bad_request : ErrorCondition.undefined_condition;
					String msg = null;
					Packet error = body == null ? null : body.getFirstChild("error");
					if (error != null) {
						for (Packet c : error.getChildren()) {
							String xmlns = c.getAtribute("xmlns");
							if ("text".equals(c.getName())) {
								msg = c.getCData();
								break;
							} else if (xmlns != null && "urn:ietf:params:xml:ns:xmpp-stanzas".equals(xmlns)) {
								condition = getCondition(c.getName(), -1);
							}
						}
					}

					if (condition == ErrorCondition.item_not_found) {
						state = State.disconnected;
						fireEventError(boshCondition, condition, msg);
					} else if (errorCounter < MAX_ERRORS&&lastSendedBody !=null&&lastSendedBody.length()!=0) {
						errorCounter++;
						send(lastSendedBody, requestClientCallback);
					} else if (type != null && "terminate".equals(type)) {
						GWT.log("Disconnected by server", null);
						state = State.disconnected;
						fireDisconnectByServer(boshCondition, condition, msg);
					} else {
						state = State.disconnected;
						if (msg == null) {
							msg = condition.name().replace('_', '-');
						}
						fireEventError(boshCondition, condition, msg);
					}
				}
				else
				{
					errorCounter = 0;
					if (receivedSid != null && sid != null && !receivedSid.equals(sid))
					{
						if(state == State.resume)
						{
							state = State.disconnected;
							fireOnResumeFailed();
							return;
						}
						state = State.disconnected;
						fireEventError(BoshErrorCondition.policy_violation, ErrorCondition.unexpected_request,
								"Unexpected session initialisation.");
					}
					else if (receivedSid != null && sid == null)
					{
						sid = receivedSid;
						//Cookies.setCookie(user.getResource()+"sid", sid,null,null,"/",false);
						state = State.connected;
					}

					if(state == State.resume)
					{
						state = State.connected;
						fireOnResumeSuccessed();
					}

					List<? extends Packet> children = body.getChildren();
					if (children.size() > 0) {
						fireEventReceiveStanzas(children);
					}
					continuousConnection(ack);
				}
			}


		};
		//end added
	}

	public void addListener(ConnectorListener listener) {
		this.listeners.add(listener);
	}

	public void connect()
	{
        Log.log("Bosh2Connector.connect");
        makeNewRequestBuilder(defaultTimeout + 7);
		this.rid = (long) (Math.random() * 10000000);

		Packet e = new PacketImp("body");
		e.setAttribute("content", "text/xml; charset=utf-8");
		e.setAttribute("hold", "1");
		e.setAttribute("requests", "2");
		e.setAttribute("rid", getNextRid());
		e.setAttribute("to", domain);
		e.setAttribute("ver", "1.6");
		e.setAttribute("cache", "on");
		e.setAttribute("wait", String.valueOf(defaultTimeout));
		e.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e.setAttribute("xmlns:xmpp","urn:xmpp:xbosh");
		e.setAttribute("secure","false");
		e.setAttribute("xmpp:version","1.0");
		if(host!=null&&!(host.length()==0))
		{
			final String value ="xmpp:"+host+":"+String.valueOf(port);
			e.setAttribute("route", value);
		}

		state = State.connecting;
		if(crossDomain)
			send(renderer.render(e), requestClientCallback);
		else
			send(renderer.render(e), standardHandler);

	}

	private int getActivesRequestCount()
	{
		if(crossDomain)
			return this.activeScriptRequests.size();
		else
			return this.activeRequests.size();
	}

	public void continuousConnection(String ack)
	{
		if (state != State.connected || getActivesRequestCount() > 0)
			return;
		Packet e = new PacketImp("body");
		e.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e.setAttribute("rid", getNextRid());
		if (sid != null)
			e.setAttribute("sid", sid);

		if (ack != null) {
			e.setAttribute("ack", ack);
		}

		if(crossDomain)
			send(renderer.render(e), requestClientCallback);
		else
			send(renderer.render(e), standardHandler);
	}

	public void disconnect(Packet packetToSend)
	{
		PacketImp e = new PacketImp("body");
		e.setAttribute("rid", getNextRid());
		if (sid != null)
			e.setAttribute("sid", sid);
		e.setAttribute("to", domain);
		e.setAttribute("type", "terminate");
		e.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e.setAttribute("xmlns:xmpp", "urn:xmpp:xbosh");
		e.setAttribute("secure","false");
		e.setAttribute("xmpp:version","1.0");
		if(host!=null&&!(host.length()==0))
		{
			final String value ="xmpp:"+host+":"+String.valueOf(port);
			e.setAttribute("route", value);
		}
		if (packetToSend != null) {
			e.addChild(packetToSend);
		}
		if (state == State.connected)
		{
			if(crossDomain)
				send(renderer.render(e), requestClientCallback);
			else
				send(renderer.render(e), standardHandler);
		}
		state = State.disconnected;
		reset();
	}

	private void fireDisconnectByServer(BoshErrorCondition boshCondition, ErrorCondition xmppCondition, String msg)
	{
        for (ConnectorListener l : this.listeners) {
            l.onBoshTerminate(this, boshCondition);
        }
	}

	private void fireEventError(BoshErrorCondition boshErrorCondition, ErrorCondition xmppErrorCondition, String message)
	{
        for (ConnectorListener l : this.listeners) {
            l.onBoshError(xmppErrorCondition, boshErrorCondition, message);
        }
	}

	private void fireEventReceiveStanzas(List<? extends Packet> nodes)
	{
        for (ConnectorListener l : this.listeners) {
            l.onStanzaReceived(nodes);
        }
	}

	private void fireOnBodyReceive(Response response, String body)
	{
        for (ConnectorListener l : this.listeners) {
            l.onBodyReceive(response, body);
        }
	}

	private void fireOnBodySend(String body)
	{
        for (ConnectorListener l : this.listeners) {
            l.onBodySend(body);
        }
	}

	private void fireOnResumeSuccessed()
	{
        Log.log("Bosh2Connector.fireOnResumeSuccessed");
		for(ConnectorListener l:listeners)
		{
			l.onResumeSuccessed();
		}
	}

	private void fireOnResumeFailed()
	{
        Log.log("Bosh2Connector.fireOnResumeFailed");
        for(ConnectorListener l:listeners)
		{
			l.onResumeFailed();
		}
	}

	private String getNextRid()
	{
		this.rid++;
		String tmp = String.valueOf(this.rid);
		final Date expire = new Date(39 * 1000 + (new Date()).getTime());
		//Cookies.setCookie(user.getResource()+"rid", tmp, expire,null,"/",false);
		return tmp;
	}

	public boolean isCacheAvailable()
	{
		return false;//Cookies.getCookie(user.getResource()+"sid") != null && Cookies.getCookie(user.getResource()+"rid") != null;
	}

	public boolean isConnected()
	{
		return state == State.connected;
	}

	public boolean isDisconnected()
	{
		return state == State.disconnected;
	}

	private void makeNewRequestBuilder(int timeOut)
	{
		if(crossDomain)
			requestClient.setTimeoutMillis(timeOut*2000);
		else
			builder.setTimeoutMillis(timeOut * 2000);
		GWT.log("timeout==" + (timeOut * 2000), null);
	}

	private Packet parse(final String s)
	{
		if (s == null || s.length() == 0)
		{
			return null;
		}
		else
		{
			try
			{
				Element element = XMLParser.parse(s).getDocumentElement();
				return new PacketGwtImpl(element);
			} catch (Exception e)
			{
				GWT.log("Parsing error (\"" + s + "\")", e);
				return null;
			}
		}
	}

	public void removeListener(ConnectorListener listener)
	{
		this.listeners.remove(listener);
	}

	public void reset()
	{
		Log.log("Bosh2Connector.reset");
        state = State.disconnected;
		//Cookies.removeCookie(user.getResource()+"sid");
		//Cookies.removeCookie(user.getResource()+"rid");
		this.errorCounter = 0;
		this.activeRequests.clear();
		this.activeScriptRequests.clear();
		this.sid = null;
		this.rid = (long) (Math.random() * 10000000);
	}

	public void restartStream(IQ iq)
	{
		PacketImp e = new PacketImp("body");
		if (sid != null)
			e.setAttribute("sid", sid);
		e.setAttribute("rid", getNextRid());
		e.setAttribute("to", domain);
		e.setAttribute("xmpp:restart", "true");
		e.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e.setAttribute("xmlns:xmpp", "urn:xmpp:xbosh");
		if(iq!=null)
			e.addChild(iq);
		if(crossDomain)
			send(renderer.render(e), requestClientCallback);
		else
			send(renderer.render(e), standardHandler);
	}

	public void send(Packet stanza)
	{
		PacketImp e = new PacketImp("body");
		e.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e.setAttribute("rid", getNextRid());
		if (sid != null)
			e.setAttribute("sid", sid);

		e.addChild(stanza);

		if(crossDomain)
			send(renderer.render(e), requestClientCallback);
		else
			send(renderer.render(e), standardHandler);
	}

	//adde by zhongfanglin@antapp.com
	private void send(String body,RequestClientCallback callback)
	{
		Log.log("OUT (" + this.sid + "): " + body);
		try
		{
			// ++activeConnections;
			int id = requestClient.sendRequest(body, callback);
			this.activeScriptRequests.put(id+"", body);
			fireOnBodySend(body);
		} catch (Exception e) {
			GWT.log(e.getMessage(), e);
		}
	}
	//end added

	private void send(String body, RequestCallback callback)
	{
		// activeConnections + "): " + body);
		Log.log("OUT (" + this.sid + "): " + body);
		try
		{
			// ++activeConnections;
			Request request = builder.sendRequest(body, callback);
			this.activeRequests.put(request, body);
			fireOnBodySend(body);
		}
		catch (Exception e) {
			GWT.log(e.getMessage(), e);
		}
	}

	public void sendStanza(String stanza)
	{
		String r = "<body xmlns='http://jabber.org/protocol/httpbind' rid='" + getNextRid() + "'";
		if (sid != null)
			r += " sid='" + sid + "'";
		r += ">";
		r += stanza;
		r += "</body>";
		if(crossDomain)
			send(r, requestClientCallback);
		else
			send(r, standardHandler);
	}

	public void setDomain(String domainname)
	{
		this.domain = domainname;
	}

	private boolean isCrossDomain(final String boshUrl)
	{
        return false;
//		if(boshUrl.startsWith("/"))
//			return false;
//		String baseUrl = GWT.getHostPageBaseURL();
//
//		if(getUrlProtocol(boshUrl).equals(getUrlProtocol(baseUrl))&&getUrlHost(boshUrl).equals(getUrlHost(baseUrl)))
//			return false;
//		return true;
	}

	private String getUrlHost(String url)
	{
		if(url.contains("http://"))
		{
			String tmp = url.substring(7);
			if(tmp.contains("/"))
				return tmp.substring(0,tmp.indexOf("/"));
			else
				return tmp;
		}
		else if(url.contains("https://"))
		{
			String tmp = url.substring(8);
			if(tmp.contains("/"))
				return tmp.substring(0,tmp.indexOf("/"));
			else
				return tmp;
		}
		else
		{
			if(url.contains("/"))
				return url.substring(0,url.indexOf("/"));
			else
				return url;
		}
	}

	private String getUrlProtocol(final String url)
	{
		int i = url.indexOf(":");
		if(i!=-1)
		{
			if(url.charAt(i+1)!='/')
			{
				return "";
			}
			return url.substring(0, i);
		}
		return "";
	}


	public void setHttpBase(final String boshUrl)
	{
		if(isCrossDomain(boshUrl))
		{
			setCrossDomainHttpBase(boshUrl);
			return;
		}
		builder = new RequestBuilder(RequestBuilder.POST, boshUrl);
		//builder.setHeader("Connection", "close");
	}

	public void setCrossDomainHttpBase(final String boshUrl)
	{
		crossDomain = true;
		requestClient = new JsonpRequestClient(boshUrl);
		/*
		if(Session.instance().getServerType().equals(ServerType.openfire))
			requestClient = new OpenfireBoshRequestClient(boshUrl);
		else
			requestClient = new JsonpRequestClient(boshUrl);
		*/
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public boolean resume()
	{
        Log.log("Bosh2Connector.resume");
		makeNewRequestBuilder(defaultTimeout + 7);
//		this.sid = Cookies.getCookie(user.getResource()+"sid");
//		try
//		{
//			this.rid = Long.parseLong(Cookies.getCookie(user.getResource()+"rid"));
//		}
//		catch (Exception e)
//		{
			this.rid = 0;
		//}

		if(this.sid == null||this.rid == 0)
			return false;

		this.state = State.resume;

		Packet e0 = new PacketImp("body");
		e0.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e0.setAttribute("sid", sid);
		e0.setAttribute("rid", ""+this.rid);

		if(crossDomain)
		{
			send(renderer.render(e0),requestClientCallback);
		}
		else
		{
			send(renderer.render(e0), standardHandler);
		}

		return true;
	}

	public boolean suspend()
	{
		Packet e0 = new PacketImp("body");
		e0.setAttribute("pause", "120");
		e0.setAttribute("xmlns", "http://jabber.org/protocol/httpbind");
		e0.setAttribute("sid", sid);
		e0.setAttribute("rid", this.getNextRid());

		if(crossDomain)
		{
			send(renderer.render(e0),requestClientCallback);
		}
		else
		{
			send(renderer.render(e0), standardHandler);
		}

		return true;
	}

	public boolean isCrossDomain()
	{
		return this.crossDomain;
	}

}
