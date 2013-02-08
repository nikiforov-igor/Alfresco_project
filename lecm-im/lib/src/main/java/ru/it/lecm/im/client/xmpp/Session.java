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

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.xmpp.Connector.BoshErrorCondition;
import ru.it.lecm.im.client.xmpp.ResponseHandler.ErrorType;
import ru.it.lecm.im.client.xmpp.events.Event;
import ru.it.lecm.im.client.xmpp.events.Events;
import ru.it.lecm.im.client.xmpp.events.EventsManager;
import ru.it.lecm.im.client.xmpp.events.Listener;
import ru.it.lecm.im.client.xmpp.gateway.GatewayPlugin;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.packet.PacketImp;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.stanzas.IQ.Type;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;
import ru.it.lecm.im.client.xmpp.xmpp.ImSessionPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.ResourceBindPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.StreamFeaturesPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.message.MessagePlugin;
import ru.it.lecm.im.client.xmpp.xmpp.presence.PresencePlugin;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.sasl.SaslAuthPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.sasl.SaslEvent;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch.JabberSearchPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving.MessageArchivingPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MultiUserChatPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.privateStorage.PrivateStoragePlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.softwareVersion.SoftwareVersionPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.vcard.VCard;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.vcard.VCardPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.vcard.VCardResponseHandler;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.xmppPing.PingPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Session implements Responsemanager 
{

	private final static ResponseHandler EMPTY_HANDLER = new ResponseHandler() {

		public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) {
            Log.log("EMPTY_HANDLER.ResponseHandler.onError()");
		}

		public void onResult(IQ iq) {
            Log.log("EMPTY_HANDLER.ResponseHandler.onResult()");
		}
	};

	private static UIDGenerator id = new UIDGenerator();
	
	public enum ServerType{ejabberd,openfire,others};
	
	private ServerType serverType = ServerType.ejabberd;

	private static void callErrorOnResponseHandler(final IQ iq, final ResponseHandler handler) 
	{
        Log.log("Session.callErrorOnResponseHandler()");
        final Packet error = iq.getFirstChild("error");
		String text = null;
		ErrorCondition condition = null;
		ResponseHandler.ErrorType errorType = null;
		if (error != null) {
			String et = error.getAtribute("type");
			errorType = et == null ? null : ErrorType.valueOf(et.toUpperCase());
			List<? extends Packet> kids = error.getChildren();
			if (kids != null)
				for (Packet k : kids) {
					String xmlns = k.getAtribute("xmlns");
					if (xmlns != null && "text".equals(k.getName()) && "urn:ietf:params:xml:ns:xmpp-stanzas".equals(xmlns)) {
						text = k.getCData();
					} else if (xmlns != null && "urn:ietf:params:xml:ns:xmpp-stanzas".equals(xmlns)) {
						condition = ErrorCondition.valueOf(k.getName().replace('-', '_'));
					}
				}
		}
		handler.onError(iq, errorType, condition, text);
	}

	public static void main(String[] args) {
		IQ iq = new IQ(Type.error);
		Packet error = iq.addChild("error", null);
		error.setAttribute("type", "cancel");
		error.setAttribute("code", "404");
		error.addChild("item-not-found", "urn:ietf:params:xml:ns:xmpp-stanzas");
		Packet text = error.addChild("text", "urn:ietf:params:xml:ns:xmpp-stanzas");
		text.setCData("Some special application diagnostic information...");

        Log.log(iq.toString());
		callErrorOnResponseHandler(iq, null);
	}

	public static String nextId() 
	{
		return id.nextUID();
	}
	
	public void setServerType(ServerType type)
	{
		serverType = type;
	}
	
	public ServerType getServerType()
	{
		return serverType;
	}

	private ResourceBindPlugin bindPlugin;

	private ConnectorListener boshListener;

	private MessagePlugin chatPlugin;

	private Connector con;

	private final EventsManager eventsManager = new EventsManager();

	private JabberSearchPlugin jabberSearchPlugin;

	private MessageArchivingPlugin messageArchivingPlugin;

	private MultiUserChatPlugin mucPlugin;

	private ArrayList<Plugin> plugins = new ArrayList<Plugin>();

	private PresencePlugin presencePlugin;

	private PrivacyListsPlugin privacyListPlugin;

	private PrivateStoragePlugin privateStoragePlugin;

	private Map<String, ResponseHandler> responseHandlers = new HashMap<String, ResponseHandler>();

	private RosterPlugin rosterPlugin;

	private SaslAuthPlugin saslPlugin;

	private SoftwareVersionPlugin softwareVersionPlugin;
	
	private ImSessionPlugin sessionPlugin;
	
	private MucRoomPlugin mucRoomPlugin;

	private User user;

	private VCardPlugin vCardPlugin;
	
	private GatewayPlugin gatewayPlugin;
	
	private VCard vcard;
	
	private List<SessionListener> listeners = new ArrayList<SessionListener>();
	
	private boolean isBigPresence = false;
	
	private static Session instance = null;
	
	private boolean getRosterDelay = false;
	private boolean noneRoster = false;
	private Presence initPresence = null;
	public static Session instance()
	{
		if(instance == null)
			instance = new Session(new User());
		return instance;
	}

	private Session(User user) 
	{
        Log.log("new Session()");
        this.con = new Bosh2Connector(user);
		this.user = user;
		this.boshListener = new ConnectorListener() 
		{

			public void onBodyReceive(Response response, String body) 
			{
			}

			public void onBodySend(String body) 
			{
			}

			public void onBoshError(ErrorCondition errorCondition, BoshErrorCondition boshErrorCondition, String message) 
			{
                Log.log("Session.onBoshError()");
                fireOnError(boshErrorCondition,message);
				con.disconnect(null);
				reset();
			}

			public void onBoshTerminate(Connector con, BoshErrorCondition boshErrorCondition) 
			{
                Log.log("Session.onBoshError()");
				fireOnError(boshErrorCondition,null);
				con.disconnect(null);
				reset();
			}

			public void onConnect(Connector con) 
			{
                Log.log("Session.onBoshError()");
			}

			public void onStanzaReceived(List<? extends Packet> nodes) 
			{
				int presenceCount = 0;
				for(Packet node:nodes)
				{
					if(node.getName().equalsIgnoreCase("presence"))
						presenceCount++;
				}
				if(presenceCount>2)
				{
					isBigPresence = true;
				}
				for (Packet node : nodes) 
				{
					boolean handled = false;
					JID to = JID.fromString(node.getAtribute("to"));
					if (to != null && bindPlugin.getBindedJid() != null && !to.getBareJID().equals(bindPlugin.getBindedJid().getBareJID())) 
					{
						Log.log("skip");
						continue;
					}
					// Log.logln("IN: " + node);
					if (!(handled = runResponceHandler(node))) 
					{
						boolean stopProcessing = false;
						for (int j = 0; !stopProcessing && j < plugins.size(); j++) 
						{
							Plugin plugin = plugins.get(j);
							if (plugin.getCriteria() != null && plugin.getCriteria().match(node)) {
								handled = true;
								stopProcessing = plugin.process(node);
							}
						}
					}
					if (!handled) 
					{
                        Log.log("Session.onStanzaReceived() NOT HANDLED!!!");
						PacketImp errorStanza = new PacketImp(node.getName(), node.getAtribute("xmlns"));

						final String id = node.getAtribute("id");
						final String toJID = node.getAtribute("from");

						if (id != null)
							errorStanza.setAttribute("id", id);
						errorStanza.setAttribute("type", "error");
						errorStanza.setAttribute("to", toJID);

						errorStanza.addChild(node);

						Packet errorElement = new PacketImp("error");
						errorElement.setAttribute("type", "cancel");
						errorElement.setAttribute("code", "501");
						errorStanza.addChild(errorElement);

						errorElement.addChild("feature-not-implemented", "urn:ietf:params:xml:ns:xmpp-stanzas");

						send(errorStanza);
					}
				}
				if(isBigPresence)
					presencePlugin.bigPresenceUpdated();
				isBigPresence = false;

				if (bindPlugin.getStatus() != PluginState.SUCCESS) 
				{
					if (saslPlugin.isFeaturesReceived() && saslPlugin.getStatus() == PluginState.NONE) 
					{
						saslPlugin.auth();
					}
					else if (saslPlugin.getStatus() == PluginState.SUCCESS && bindPlugin.getStatus() == PluginState.NONE) 
					{
						bindPlugin.bind();
					}
				} 
				else if(sessionPlugin.getStatus() == PluginState.NONE&&serverType.equals(ServerType.ejabberd))
				{
					sessionPlugin.requestImSession();
				}
				else if(getRosterDelay||noneRoster)
				{
					if(!presencePlugin.isInitialPresenceSended())
					{
						presencePlugin.sendInitialPresence(Session.this.user.getPriority());
						JID j = new JID (Session.this.user.getUsername(),Session.this.user.getDomainname(), "ijab");
						vCardPlugin.vCardRequest(j, new VCardResponseHandler()
						{
							public void onSuccess(VCard c) 
							{
								vcard = c;
								fireOnSelfVCard();
							}
	
							public void onError(IQ iq, ErrorType errorType,
									ErrorCondition errorCondition, String text) 
							{	
							}
							
						});
						fireOnEndLogin();
					}
				}
				else if(!getRosterDelay)
				{
					if (rosterPlugin.getStatus() == PluginState.NONE ) 
					{
						Log.log("Session.ConnectorListener.onStanzaReceived rosterPlugin.getStatus() == PluginState.NONE rosterPlugin.getRoster(null) ");
                        rosterPlugin.getRoster(null);
					} 
					else if (rosterPlugin.getStatus() == PluginState.SUCCESS && !presencePlugin.isInitialPresenceSended()) 
					{
                        Log.log("Session.ConnectorListener.onStanzaReceived if (rosterPlugin.getStatus() == PluginState.SUCCESS && !presencePlugin.isInitialPresenceSended())");
						presencePlugin.sendInitialPresence(Session.this.user.getPriority());
						JID j = new JID (Session.this.user.getUsername(),Session.this.user.getDomainname(), "ijab");
						vCardPlugin.vCardRequest(j, new VCardResponseHandler()
						{
							public void onSuccess(VCard c) 
							{
								vcard = c;
								fireOnSelfVCard();
							}
	
							public void onError(IQ iq, ErrorType errorType,
									ErrorCondition errorCondition, String text) 
							{	
							}
							
						});
						fireOnEndLogin();
					}
				}
			}

			public void onResumeFailed() 
			{
				fireOnResumeFailed();
			}

			public void onResumeSuccessed() 
			{
                Log.log("Session.ConnectorListener.onResumeSuccessed()");
				if(!noneRoster&&!getRosterDelay)
				{
                    Log.log("Session.ConnectorListener.onResumeSuccessed() !noneRoster&&!getRosterDelay = true");
                    rosterPlugin.getRoster(new AsyncCallback<Object>()
					{
						public void onFailure(Throwable caught) 
						{
                            Log.log("Session.ConnectorListener.onResumeSuccessed.getRoster.onFailure()");
                            fireOnResumeFailed();
						}
						public void onSuccess(Object result) 
						{
                            Log.log("Session.ConnectorListener.onResumeSuccessed.getRoster.onSuccess()");
							presencePlugin.resume();
							fireOnResumeSuccessed();
						}
					});
				}
				else
				{
                    Log.log("Session.ConnectorListener.onResumeSuccessed() !noneRoster&&!getRosterDelay = false");
					presencePlugin.resume();
					fireOnResumeSuccessed();
				}
			}

		};
		this.con.addListener(boshListener);

		saslPlugin = new SaslAuthPlugin(con, user, eventsManager);
		eventsManager.addListener(Events.saslSuccess, new Listener<SaslEvent>() 
		{
			public void handleEvent(SaslEvent event) 
			{
				bindPlugin.reset();
				sessionPlugin.reset();
				if(serverType.equals(ServerType.ejabberd))
					con.restartStream(bindPlugin.bindInRestarStream());
				else
					con.restartStream(null);
			}
		});
		add(saslPlugin);

		mucPlugin = add(new MultiUserChatPlugin(this));
		chatPlugin = add(new MessagePlugin(this));
		bindPlugin = add(new ResourceBindPlugin(this, user));
		presencePlugin = add(new PresencePlugin(this));
		rosterPlugin = add(new RosterPlugin(this));
		vCardPlugin = add(new VCardPlugin(this));
		privateStoragePlugin = add(new PrivateStoragePlugin(this));
		messageArchivingPlugin = add(new MessageArchivingPlugin(this));
		jabberSearchPlugin = add(new JabberSearchPlugin(this));
		softwareVersionPlugin = add(new SoftwareVersionPlugin(this));
		sessionPlugin = add(new ImSessionPlugin(this));
		mucRoomPlugin = add(new MucRoomPlugin(this));
		gatewayPlugin = add(new GatewayPlugin(this));
		this.privacyListPlugin = add(new PrivacyListsPlugin(this));
		add(new PingPlugin(this));
		add(new StreamFeaturesPlugin());
	}

	public <T extends Plugin> T add(T aplugin) 
	{
		this.plugins.add(aplugin);
		return aplugin;
	}

	public void addEventListener(Enum<?> eventType, Listener<? extends Event> listener) 
	{
		this.eventsManager.addListener(eventType, listener);
	}

	
	public void addResponseHandler(IQ iq) 
	{
		if (iq.getId() == null) {
			iq.setId(nextId());
		}
		final String key = makeKeyForResponseListener(iq.getId(), iq.getTo());
		Log.log("Register handler for " + key);

		this.responseHandlers.put(key, EMPTY_HANDLER);

		send(iq);
	}

	public void addResponseHandler(IQ iq, ResponseHandler listener) 
	{
		if (iq.getId() == null) {
			iq.setId(nextId());
		}
		final String key = makeKeyForResponseListener(iq.getId(), iq.getTo());
		Log.log("Register handler for " + key);

		this.responseHandlers.put(key, listener == null ? EMPTY_HANDLER : listener);

		send(iq);
	}
	
	public void setGetRosterDelay(boolean b)
	{
		this.getRosterDelay = b;
	}
	
	public void setNoneRoster(boolean b)
	{
		this.noneRoster = b;
	}

	public SaslAuthPlugin getAuthPlugin() 
	{
		return saslPlugin;
	}

	public ResourceBindPlugin getBindPlugin() 
	{
		return this.bindPlugin;
	}

	/**
	 * @return the chatPlugin
	 */
	public MessagePlugin getChatPlugin() 
	{
		return chatPlugin;
	}
	
	public GatewayPlugin getGatewayPlugin()
	{
		return gatewayPlugin;
	}

	public Connector getConnector() 
	{
		return this.con;
	}

	public String getDomainName() 
	{
		return user.getDomainname();
	}

	public EventsManager getEventsManager() 
	{
		return eventsManager;
	}

	public JabberSearchPlugin getJabberSearchPlugin() 
	{
		return jabberSearchPlugin;
	}

	public MessageArchivingPlugin getMessageArchivingPlugin() 
	{
		return messageArchivingPlugin;
	}

	public MultiUserChatPlugin getMucPlugin() 
	{
		return mucPlugin;
	}
	
	public MucRoomPlugin getMucRoomPlugin()
	{
		return mucRoomPlugin;
	}

	/**
	 * @return the presencePlugin
	 */
	public PresencePlugin getPresencePlugin() 
	{
		return presencePlugin;
	}

	public PrivacyListsPlugin getPrivacyListPlugin() 
	{
		return privacyListPlugin;
	}

	public PrivateStoragePlugin getPrivateStoragePlugin() 
	{
		return privateStoragePlugin;
	}

	/**
	 * @return the rosterPlugin
	 */
	public RosterPlugin getRosterPlugin() 
	{
		return rosterPlugin;
	}

	public SoftwareVersionPlugin getSoftwareVersionPlugin() 
	{
		return softwareVersionPlugin;
	}

	public User getUser() 
	{
		return user;
	}
	
	public VCard getSelfVCard()
	{
		return this.vcard;
	}

	public VCardPlugin getVCardPlugin() 
	{
		return vCardPlugin;
	}

	public boolean isActive() 
	{
		return bindPlugin.getStatus() == PluginState.SUCCESS && con.isConnected();
	}
	
	public boolean isDisconnected()
	{
		return con.isDisconnected();
	}

	public void letInitializeSomePluginsVeryDirty() 
	{
		bindPlugin.setInitializedDirty();
		rosterPlugin.setInitializedDirty();
	}

	public void login() 
	{
		fireOnBeforeLogin();
		con.setDomain(user.getDomainname());
		this.con.connect();
	}

	public void logout() 
	{
		Presence presence = presencePlugin.offlinePresence();
		con.disconnect(presence);
		DeferredCommand.addCommand(new Command() {

			public void execute() {
				reset();
			}
		});
		fireOnLoginOut();
	}

	private String makeKeyForResponseListener(String id, JID jid) 
	{
		return id;
		/*
		String key = id + ":" + (jid == null ? "null" : jid.getBareJID());
		return new String(key);
		*/
	}

	public void reset() 
	{
		user.reset();
		rosterPlugin.reset();
		responseHandlers.clear();
		con.reset();
		for (int i = 0; i < this.plugins.size(); i++) 
		{
			(this.plugins.get(i)).reset();
		}
	}

	private boolean runResponceHandler(Packet packet) 
	{
		final IQ iq = new IQ(packet);
		JID from = iq.getFrom();
		String id = iq.getId();
		IQ.Type type = iq.getType();

		final String key = makeKeyForResponseListener(id, from);
		Log.log("Retrieving key: " + key);
		ResponseHandler listener = this.responseHandlers.remove(key);
		if (listener != null) 
		{
			if (type == IQ.Type.result) 
			{
				listener.onResult(iq);
				return true;
			}
			else if (type == IQ.Type.error) 
			{
				callErrorOnResponseHandler(iq, listener);
				return true;
			}
		}

		return false;
	}

	public void send(Packet iq) 
	{
		// Log.logln("OUT: " + iq);
		this.con.send(iq);
	}
	
	public void addListener(SessionListener listener) 
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(SessionListener listener) 
	{
		this.listeners.remove(listener);
	}
	
	private void fireOnBeforeLogin()
	{
		for(int i = 0;i<listeners.size();i++)
		{
			SessionListener l = listeners.get(i);
			l.onBeforeLogin();
		}
	}
	
	private void fireOnEndLogin()
	{
        Log.log("Session.fireOnEndLogin");
		for(int i = 0;i<listeners.size();i++)
		{
			SessionListener l = listeners.get(i);
			l.onEndLogin();
		}
	}
	
	private void fireOnLoginOut()
	{
        Log.log("Session.fireOnLoginOut");
		for(int i = 0;i<listeners.size();i++)
		{
			SessionListener l = listeners.get(i);
			l.onLoginOut();
		}
	}
	
	private void fireOnError(BoshErrorCondition boshErrorCondition,String message)
	{
        Log.log("Session.fireOnError");
		for(int i = 0;i<listeners.size();i++)
		{
			SessionListener l = listeners.get(i);
			l.onError(boshErrorCondition, message);
		}
	}
	
	private void fireOnResumeSuccessed()
	{
        Log.log("Session.fireOnResumeSuccessed()");
        for(SessionListener l:listeners)
			l.onResumeSuccessed();
	}
	
	private void fireOnResumeFailed()
	{
        Log.log("Session.fireOnResumeFailed()");
		for(SessionListener l:listeners)
			l.onResumeFailed();
	}
	
	private void fireOnSelfVCard()
	{
		for(SessionListener l:listeners)
			l.onSelfVCard();
	}
	
	public boolean IsBigPresence()
	{
		return this.isBigPresence;
	}
	
	public boolean suspend()
	{
        Log.log("Session.suspend");
		//rosterPlugin.suspend();
		presencePlugin.suspend();
		if(!user.suspend())
			return false;
		if(!con.suspend())
			return false;
		//TODO: implement all the plugin's function suspend
		return true;
	}
	
	public void resume()
	{
        Log.log("Session.resume()");
		//TODO: resume all the plugin state before the call the resume of con
		if(!user.resume())
		{
			fireOnResumeFailed();
			return;
		}
				
		if(!con.resume())
		{
			fireOnResumeFailed();
			return;
		}
	}
	
	public void setInitPresence(Presence presence)
	{
		this.initPresence = presence;
	}
	
	public Presence getInitPresence()
	{
		return this.initPresence;
	}

}
