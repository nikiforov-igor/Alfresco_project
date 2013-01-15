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
 * Feb 2, 2010
 */
package ru.it.lecm.im.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import ru.it.lecm.im.client.data.XmppConf;
import ru.it.lecm.im.client.data.iJabConfImpl;
import ru.it.lecm.im.client.data.iJabOptions;
import ru.it.lecm.im.client.ui.ChatPanelBar;
import ru.it.lecm.im.client.ui.ContactView;
import ru.it.lecm.im.client.ui.ContactViewListener;
import ru.it.lecm.im.client.ui.MUCPanelButton;
import ru.it.lecm.im.client.utils.*;
import ru.it.lecm.im.client.utils.TextUtils;
import ru.it.lecm.im.client.xmpp.Connector.BoshErrorCondition;
import ru.it.lecm.im.client.xmpp.*;
import ru.it.lecm.im.client.xmpp.events.Events;
import ru.it.lecm.im.client.xmpp.events.Listener;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Show;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Type;
import ru.it.lecm.im.client.xmpp.util.StringUtil;
import ru.it.lecm.im.client.xmpp.xmpp.message.ChatManager;
import ru.it.lecm.im.client.xmpp.xmpp.presence.PresenceEvent;
import ru.it.lecm.im.client.xmpp.xmpp.presence.PresenceListener;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem.Action;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem.Kind;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyList;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin.RetrieveListHandler;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin.RetrieveListsNamesHandler;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterListener;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MultiUserChatPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class XmppClient
{
	private final String RESOURCE_PREFIX = "AlfIM";
	private final Session session = Session.instance();
	private final XmppConf conf = iJabConfImpl.getConf().getXmppConf();
	private int priority = 5;
	private static String SELF_NICK = "SELFNICK";
	public static String BLACKLIST = "IJAB_BLACKLIST"; 
	
	private int onlineContactCount = 0;
	private int totalContactCount = 0;
	final private XmppChatManager xmppChatManager;


	private int reconnect_count = conf.getMaxReconnet();
	private Show weekShow = null;
	private int talkToCount = 0;
	List<String> talkToList = new ArrayList<String>();
    protected final List<ClientListener> listeners = new ArrayList<ClientListener>();
    protected final List<MessageReceiveListener> messageReceivelisteners = new ArrayList<MessageReceiveListener>();
    protected final List<VisibilityListener> visibilityListeners = new ArrayList<VisibilityListener>();
    private boolean isVisible = false;
    protected boolean isLogined = false;

    public XmppClient()
	{
		session.setGetRosterDelay(conf.isGetRosterDelay());
		session.setNoneRoster(iJabConfImpl.getConf().getXmppConf().isNoneRoster());

        Window.addCloseHandler(new CloseHandler<Window>()
		{

			public void onClose(CloseEvent<Window> event) 
			{
				suspend();
			}
		});

        setupSessionListener();

        session.setServerType(conf.getServerType());
		session.getConnector().setHost(conf.getHost());
		session.getConnector().setHttpBase(conf.getHttpBind());
		session.getConnector().setPort(conf.getPort());
		
		//connect ui and client events
		connectRosterEvents();
		
		ChatManager<XmppChat> chatManager = new ChatManager<XmppChat>(session.getChatPlugin());
		xmppChatManager = new XmppChatManager(chatManager,(ChatPanelBar)iJab.ui.getChatManagerWidget());
		session.getChatPlugin().setChatManager(chatManager);
		
		new iJabOptions(this);
		addClientListener(iJab.ui);
		addClientListener(xmppChatManager);
	}

    private void setupSessionListener() {
        this.session.addListener(new SessionListener()
        {
            public void onBeforeLogin()
            {
                fireOnBeforeLogin();
            }

            public void onEndLogin()
            {
                fireOnEndLogin();
                IdleTool.instance().start(new IdleListener()
                {

                    public void onIdle()
                    {
                        Idle();
                    }

                    public void onWeek()
                    {
                        Week();
                    }
                });
                activeBlackList();
            }

            public void onError(BoshErrorCondition boshErrorCondition,
                    String message)
            {
                fireOnError(message);
                IdleTool.instance().stop();
                if(conf.isAutoLogin()&&reconnect_count>0)
                {
                    Timer t = new Timer()
                    {
                        @Override
                        public void run()
                        {
                            loginByCookieField();
                            reconnect_count--;
                        }
                    };
                    t.schedule(1000);
                }
                else
                {
                    Timer t = new Timer()
                    {
                        @Override
                        public void run()
                        {
                            loginAnonymous();
                        }
                    };
                    t.schedule(1000);
                }
            }

            public void onLoginOut()
            {
                fireOnLogout();
                IdleTool.instance().stop();
            }

            public void onResumeFailed()
            {
                IdleTool.instance().stop();
                autoLogin();
            }

            public void onResumeSuccessed()
            {
                isLogined = true;
                fireOnResume();
                IdleTool.instance().start(new IdleListener()
                {

                    public void onIdle()
                    {
                        Idle();
                    }

                    public void onWeek()
                    {
                        Week();
                    }
                });
            }

            public void onSelfVCard()
            {
                //should cache the self nickname
                XmppProfileManager.commitNewName(session.getUser().getStringBareJid(), session.getSelfVCard().getNickname());
                saveNickToCache(session.getSelfVCard().getNickname());
            }
        });
    }

    public Session getSession()
	{
		return session;
	}
	
	public void autoLogin()
	{
		if(conf.isAutoLogin())
		{
			Timer t = new Timer()
			{
				@Override
				public void run() 
				{
					loginByCookieField();
				}
			};
			t.schedule(500);
		}
		else
		{
			Timer t = new Timer()
			{
				@Override
				public void run() 
				{
					loginAnonymous();
				}
			};
			t.schedule(500);
		}
	}
	
	public void login(String id, String password) 
	{
		if(session.isActive())
			return;
		else
			connect(id,password);
	}
	
	public void resume() 
	{
        Log.consoleLog("XmppClient.resume()");
        reset();
		XmppProfileManager.reset();
		BrowserHelper.init();
		if(BrowserHelper.isOpera)
		{
			autoLogin();
			return;
		}
		session.resume();
	}

	public void run() 
	{
        Log.consoleLog("XmppClient.run()");
		resume();
	}

	public boolean suspend() 
	{
        Log.consoleLog("XmppClient.suspend()");
		session.suspend();
		fireOnSuspend();
		return true;
	}

	public void logout() 
	{		
		reset();
		session.logout();
		Storage storage = Storage.createStorage(SELF_NICK, "");
		storage.set(SELF_NICK, "");
		storage.remove(SELF_NICK);
		XmppProfileManager.reset();
	}
	
	public void removeFromBlacklist(final JsArrayString users)
	{
		final PrivacyListsPlugin privacyListsPlugin = session.getPrivacyListPlugin();
		if(!privacyListsPlugin.isListNamesRetrieved())
		{
			privacyListsPlugin.retrieveListsNames(new RetrieveListsNamesHandler()
			{
				public void onRetrieve(String activeListName,
						String defaultListName, Set<String> listNames) 
				{
					removeFromBlacklist(users);
				}
			});
		}
		else
		{
			if(privacyListsPlugin.isListExists(BLACKLIST))
			{
				privacyListsPlugin.retrieveList(BLACKLIST, new RetrieveListHandler()
				{
					public void onRtrieveList(String listName, PrivacyList list) 
					{
						List<String> removes = new ArrayList<String>();
						for(int index=0;index<users.length();index++)
							removes.add(users.get(index));
						for(final PrivacyItem item:list.getItems())
						{
							if(item.getAction().equals(Action.deny)&&removes.contains(item.getValue()))
							{	
								item.setAllKinds();
								item.setAction(Action.allow);
							}
						}
						list.commit();
					}
				});
			}
		}
	}
	
	public void addToBlackList(final String bareJid,final boolean alert)
	{
		final PrivacyListsPlugin privacyListsPlugin = session.getPrivacyListPlugin();
		if(!privacyListsPlugin.isListNamesRetrieved())
		{
			privacyListsPlugin.retrieveListsNames(new RetrieveListsNamesHandler()
			{
				public void onRetrieve(String activeListName,
						String defaultListName, Set<String> listNames) 
				{
					addToBlackList(bareJid,alert);
				}
			});
		}
		else
		{
			if(privacyListsPlugin.isListExists(BLACKLIST))
			{
				privacyListsPlugin.retrieveList(BLACKLIST, new RetrieveListHandler()
				{
					public void onRtrieveList(String listName, PrivacyList list) 
					{
						if(list == null)
							list = privacyListsPlugin.createList(BLACKLIST);
						for(final PrivacyItem item:list.getItems())
						{
							if(item.getValue().equals(bareJid))
							{
								if(item.getAction().equals(Action.allow))
								{
									item.setAction(Action.deny);
									list.commit();
									activeBlackList();
									return;
								}
								else
								{
									if(alert)
										Window.alert(i18n.msg("Contact is already in the blacklist!"));
									return;
								}
							}
						}
						PrivacyItem privacyItem = list.addRuleBlockJid(JID.fromString(bareJid));
						Kind kinds[] = {Kind.iq,Kind.message,Kind.presence_out};
						privacyItem.addKinds(kinds);
						list.commit();
						activeBlackList();
					}
				});
			}
			else
			{
				PrivacyList list =privacyListsPlugin.createList(BLACKLIST);
				PrivacyItem privacyItem = list.addRuleBlockJid(JID.fromString(bareJid));
				Kind kinds[] = {Kind.iq,Kind.message,Kind.presence_out};
				privacyItem.addKinds(kinds);
				list.commit();
				activeBlackList();
			}
		}
	}
	
	//local api
	//for privacyLists
	private void activeBlackList()
	{
		Timer  timer = new Timer()
		{
			@Override
			public void run() 
			{
				session.getPrivacyListPlugin().setActive(BLACKLIST);
				session.getPrivacyListPlugin().setDefault(BLACKLIST);
			}
		};
		timer.schedule(1000);
		
	}
	
	private void loginByCookieField()
	{
		String userName = Cookies.getCookie(conf.getUserCookieField());
		userName = userName==null?"":userName;
		String pwd = Cookies.getCookie(conf.getPasswordCookieField());
		pwd = pwd==null?"":pwd;
		if(userName.length() == 0||pwd.length() == 0)
			return;
		userName = userName.replaceAll("\"", "");
		pwd = pwd.replaceAll("\"", "");
		connect(userName,pwd);
		
	}
	
	private void loginAnonymous()
	{
		if(conf.getAnonymousPrefix().length() == 0)
			return;
		String userName = conf.getAnonymousPrefix() + TextUtils.genUniqueId();
		connect(userName,userName);
		//connect("mirco","123456");
		//connect("imdev","imdev631");
	}
	
	private void connect(final String id,final String password)
	{
        Log.consoleLog("XmppClient.connect()");
        reset();
		session.reset();
		User user = session.getUser();
		user.setUsername(id);
		user.setDomainname(conf.getDomain());
		user.setPassword(password);
		user.setResource(RESOURCE_PREFIX+TextUtils.genUniqueId());
		user.setPriority(priority);
		
		session.login();
	}
	
	private void connectRosterEvents()
	{
        Log.consoleLog("XmppClient.connectRosterEvents()");
		final ContactView contactView = iJab.ui.getContactView();
		RosterListener listener = new RosterListener()
		{
			public void beforeAddItem(JID jid, String name,
					List<String> groupsNames) 
			{				
			}

			public void onAddItem(RosterItem item) 
			{
                Log.consoleLog("XmppClient.RosterListener.onAddItem()");
                if(!item.getJid().toString().contains("@"))
					return;
				XmppProfileManager.commitNewName(item.getJid(), item.getName());
				contactView.addRosterItem(item);
				totalContactCount++;
				iJab.ui.updateTotalCount(totalContactCount);
			}

			public void onEndRosterUpdating() 
			{
                Log.consoleLog("XmppClient.RosterListener.onEndRosterUpdating()");
                MultiWordSuggestOracle oracle = (MultiWordSuggestOracle)iJab.ui.getSearchWidget().getSuggestOracle();
				oracle.addAll(XmppProfileManager.names.keySet());
				oracle.addAll(XmppProfileManager.names.values());
				readNickFromCache();
			}

			public void onRemoveItem(RosterItem item) 
			{
				contactView.removeRosterItem(item);
				totalContactCount--;
				iJab.ui.updateTotalCount(totalContactCount);
			}

			public void onStartRosterUpdating() 
			{
                Log.consoleLog("XmppClient.RosterListener.onStartRosterUpdating()");
				//onlineContactCount = 0;
				totalContactCount = 0;
				if(!iJab.conf.getXmppConf().isGetRosterDelay())
				{
					contactView.clear();
					iJab.ui.updateOnlineCount(0);
				}
				iJab.ui.updateTotalCount(0);
			}

			public void onUpdateItem(RosterItem item) 
			{
                if(!item.getJid().toString().contains("@"))
					return;
				XmppProfileManager.commitNewName(item.getJid(), item.getName());
				contactView.updateRosterItem(item);
			}		
		};
		session.getRosterPlugin().addRosterListener(listener);
		
		PresenceListener presenceListener = new PresenceListener()
		{

			public void beforeSendInitialPresence(Presence presence) {				
			}

			public void onBigPresenceChanged() {				
			}

			public void onContactAvailable(Presence presenceItem) 
			{
                Log.consoleLog("XmppClient.PresenceListener.onContactAvailable()");
                if(!presenceItem.getFrom().toString().contains("@"))
					return;
				final String bareJid = presenceItem.getFrom().toStringBare();
				XmppProfileManager.commitNewName(bareJid, presenceItem.getExtNick());
				
				SoundManager.playOnline();
				contactView.addOnlineGroupItem(presenceItem, session.getRosterPlugin().getRosterItem(presenceItem.getFrom()));
				onlineContactCount++;
				iJab.ui.updateOnlineCount(onlineContactCount);
			}

			public void onContactUnavailable(Presence presenceItem) {
                Log.consoleLog("XmppClient.PresenceListener.onContactUnavailable()");
                if(!presenceItem.getFrom().toString().contains("@"))
					return;
				SoundManager.playOffline();
				contactView.removeOnlineGroupItem(presenceItem, session.getRosterPlugin().getRosterItem(presenceItem.getFrom()));
				onlineContactCount--;
				iJab.ui.updateOnlineCount(onlineContactCount);
			}

			public void onPresenceChange(Presence presenceItem) 
			{
                Log.consoleLog("XmppClient.PresenceListener.onPresenceChange()");
                if(!presenceItem.getFrom().toString().contains("@"))
					return;
				final String bareJid = presenceItem.getFrom().toStringBare();
				XmppProfileManager.commitNewName(bareJid, presenceItem.getExtNick());
				if(presenceItem.getType() == Type.unavailable&&session.getPresencePlugin().isAvailableByBareJid(bareJid))
					return;
				XmppProfileManager.commitNewPresence(bareJid, presenceItem);
			}
		};
		session.getPresencePlugin().addPresenceListener(presenceListener);
		
		ContactViewListener l = new ContactViewListener()
		{

			public void onAvatarOver(RosterItem item) 
			{
			}

			public void onItemClick(RosterItem item) 
			{
                Log.consoleLog("XmppClient.ContactViewListener.onItemClick()");
                xmppChatManager.openChat(item.getJid());
			}

			public void onAvatarOut(RosterItem item) {
				
			}
		};
		contactView.addListener(l);
		
		session.getEventsManager().addListener(Events.subscribe, new Listener<PresenceEvent>()
		{
			public void handleEvent(PresenceEvent event) 
			{
				Presence presence = event.getPresence();
				JID jid = presence.getFrom();
				// is a transport subscribe?
				if(!jid.getDomain().equals(session.getUser().getDomainname())||!jid.toString().contains("@"))
				{
					session.getPresencePlugin().subscribed(jid);
				}
				else
				{
					String nick = presence.getName();
					nick = nick==null?"":nick;
					nick = nick.length()==0?StringUtil.jid2name(jid.toStringBare()):nick;
					nick = nick+"("+jid.toStringBare()+")";
					if(Window.confirm(nick+" "+ i18n.msg("request that you add as a friend. Do you accept?")))
					{
						session.getPresencePlugin().subscribed(jid);
						session.getPresencePlugin().subscribe(jid);
					}
					else
						session.getPresencePlugin().unsubscribed(jid);
				}
			}
		});
	}
	
	private void reset()
	{
		onlineContactCount = 0;
		totalContactCount = 0;
	}

	void talkTo(String j)
	{
		j = j==null?"":j;
		if(j.length()==0)
			return;
		if(!j.contains("@"))
			j = j+"@"+session.getDomainName();
		final String jid = j;
		if(!talkToList.contains(jid)&&iJab.conf.isEnableTalkToSpam()&&!session.getRosterPlugin().isContactExists(JID.fromString(jid)))
		{
			if(talkToCount>=iJab.conf.talkToSpamRepeat())
			{
				SpamHelper.callSpamFunction(iJab.conf.talkToSpamFunction(), new SpamCallBack()
				{
					public void onResult(boolean result) 
					{
						if(result)
						{
							talkToCount = 0;
							talkToList.add(jid);
							talkToDirect(jid);
						}
					}
					
				});
			}
			else
			{
				talkToCount++;
				talkToDirect(jid);
			}
		}
		else
		{
			talkToDirect(jid);
		}
	}
	
	void talkToDirect(String jid)
	{
		if(!iJabConfImpl.getConf().isEnableTalkToStranger()&&!session.getRosterPlugin().isContactExists(JID.fromString(jid)))
			return;
		try
		{
			xmppChatManager.openChat(jid);
		}
		catch(Exception e)
		{
		}
	}
	
	private void Idle()
	{
        Log.consoleLog("XmppClient.Idle()");
        if(session.isDisconnected())
			return;
		weekShow = session.getPresencePlugin().getCurrentShow();
		session.getPresencePlugin().sendStatus(Show.away);
	}
	
	private void Week()
	{
        Log.consoleLog("XmppClient.Week()");
		if(session.isDisconnected())
			return;
		session.getPresencePlugin().sendStatus(weekShow);
	}

	public boolean isConnected()
	{
		return !session.isDisconnected();
	}
	
	private void saveNickToCache(String nick)
	{
		Storage storage = Storage.createStorage(SELF_NICK, "");
		storage.set(SELF_NICK, nick);
	}
	
	private void readNickFromCache()
	{
		Storage storage = Storage.createStorage(SELF_NICK, "");
		String nick = storage.get(SELF_NICK);
		if(nick == null||nick.length()==0)
			return;
		XmppProfileManager.commitNewName(session.getUser().getStringBareJid(), nick);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#getGroups()
	 */
	public List<String> getGroups()
	{
		return iJab.ui.getContactView().getXmppGroups();
	}
	
	public void viewMessageArchive(final JID jid)
	{
		xmppChatManager.openChat(jid).getChatWidget().viewHistory();
	}
	
	public XmppChatManager getChatManager()
	{
		return xmppChatManager;
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#loginWithStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void loginWithStatus(String id, String password, String status)
	{
		session.setInitPresence(XmppStatus.makePresence(status));
		login(id,password);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#getNickname()
	 */
	public String getNickname()
	{
		return XmppProfileManager.getName(session.getUser().getStringBareJid());
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#getStatus()
	 */
	public String getStatusText()
	{
		return iJab.ui.getStatusText();
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#setStatus(java.lang.String)
	 */
	public void setStatusText(String statusText)
	{
		session.getPresencePlugin().sendStatusText(statusText);
		iJab.ui.setStatusText(statusText);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#getStatus()
	 */
	public String getStatus()
	{
		return XmppStatus.makeStatus(session.getPresencePlugin().getCurrentPresence()).name();
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#setStatus(java.lang.String)
	 */
	public void setStatus(String status)
	{
		Presence presence = XmppStatus.makePresence(status);
		session.getPresencePlugin().sendPresence(presence);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#addRoster(com.google.gwt.core.client.JsArrayString, java.lang.String)
	 */
	public void addRoster(JsArrayString users, String group)
	{
		for(int index=0;index<users.length();index++)
		{
			String j = users.get(index);
			if(!j.contains("@"))
				j = j+"@"+session.getDomainName();
			RosterItem ri = new RosterItem();
			ri.setJid(j);
			if(group!=null&&group.length()>0)
			{
				String[] groups = {group};
				ri.setGroups(groups);
			}
			Session.instance().getRosterPlugin().addItem(ri, null);
			Session.instance().getPresencePlugin().subscribe(JID.fromString(j));
		}
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#removeRoster(com.google.gwt.core.client.JsArrayString)
	 */
	public void removeRoster(JsArrayString users)
	{
		for(int index=0;index<users.length();index++)
		{
			String j = users.get(index);
			if(!j.contains("@"))
				j = j+"@"+session.getDomainName();
			Session.instance().getRosterPlugin().removeRosterItem(JID.fromString(j));
		}
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#joinMUC(java.lang.String, java.lang.String)
	 */
	public void joinMUC(String room, String nick)
	{
		MultiUserChatPlugin plugin = Session.instance().getMucPlugin();
		String roomJid = room+"@"+conf.getMUCServernode();
		GroupChat gc = plugin.createGroupChat(JID.fromString(roomJid), nick,"");
		gc.setRoomName(room);
		//gc.setUserData(MUCJoinDialog.this);
		final List<GroupChatEvent> presenceCaches = new ArrayList<GroupChatEvent>();
		gc.addListener(new GroupChatListener()
		{
			public void onGCJoinDeny(GroupChatEvent gcEvent) {
			}

			public void onGCJoined(final GroupChatEvent gcEvent) 
			{
				MUCPanelButton mucButton = iJab.ui.getChatPanelBar().createMUCButton();
				mucButton.setGroupChat(gcEvent.getGroupChat());
				mucButton.openWindow();
				for(GroupChatEvent event:presenceCaches)
					mucButton.onUserPresenceChange(event);
			}

			public void onGCLeaved(GroupChatEvent gcEvent) {
			}

			public void onMessage(Message message) {
			}

			public void onUserLeaved(GroupChatEvent gcEvent) {
			}

			public void onUserPresenceChange(GroupChatEvent gcEvent) {
				presenceCaches.add(gcEvent);
			}
		});
		gc.join();
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#leaveMUC(java.lang.String)
	 */
	public void leaveMUC(String room)
	{
		MultiUserChatPlugin plugin = Session.instance().getMucPlugin();
		String roomJid = room+"@"+conf.getMUCServernode();
		Presence presence = new Presence(Type.unavailable);
		presence.setTo(JID.fromString(roomJid));
		plugin.send(presence);
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#addUsersToBlacklist(com.google.gwt.core.client.JsArrayString)
	 */
	public void addUsersToBlacklist(JsArrayString users)
	{
		for(int index=0;index<users.length();index++)
		{
			String user = users.get(index);
			addToBlackList(user,false);
		}
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.Client#removeUsersFromBlacklist(com.google.gwt.core.client.JsArrayString)
	 */
	public void removeUsersFromBlacklist(JsArrayString users) {
		this.removeFromBlacklist(users);
	}

    public boolean getIsVisible()
    {
        return isVisible;
    }

    public void toggleIsVisible()
    {
        if (this.isVisible)
        {
            this.fireOnHide();
        }
        else
        {
            this.fireOnShow();
        }

        this.isVisible = !this.isVisible;

    }

    private void fireOnShow() {
        for(VisibilityListener l:visibilityListeners)
        {
            l.onShow();
        }
    }

    private void fireOnHide() {
        for(VisibilityListener l:visibilityListeners)
        {
            l.onHide();
        }
    }

    public void addVisibilityListener(VisibilityListener handler)
    {
        visibilityListeners.add(handler);
    }

    public void onAvatarClicked(int clientX,int clientY,final String bareJid)
    {
        fireOnAvatarClicked(clientX,clientY,JID.fromString(bareJid).getNode(),bareJid);
    }

    public void onAvatarMouseOver(int clientX,int clientY,final String bareJid)
    {
        fireOnAvatarMouseOver(clientX,clientY,JID.fromString(bareJid).getNode(),bareJid);
    }

    public void onAvatarMouseOut(int clientX,int clientY,final String bareJid)
    {
        fireOnAvatarMouseOut(clientX,clientY, JID.fromString(bareJid).getNode(),bareJid);
    }

    public void onStatusTextUpdate(final String text)
    {
        fireOnStatusTextUpdated(text);
    }

    public void onMessageReceive(final String jid,final String message)
    {
        fireOnMessageReceive(jid,message);
    }

    public void addNativeListener(JavaScriptObject jso)
    {
        addClientListener(new NativeClientListener(jso));
    }

    public void addClientListener(ClientListener handler)
    {
        listeners.add(handler);
    }

    public void addNativeMessageReceiveListener(JavaScriptObject jso)
    {
        this.addNativeMessageReceiveListener(new NativeClientListener(jso));
    }

    private void addNativeMessageReceiveListener(MessageReceiveListener messageReceiveListener) {
        this.messageReceivelisteners.add(messageReceiveListener);
    }

    public boolean isLogined()
    {
        return isLogined;
    }

    protected void fireOnLogout()
    {
        isLogined = false;
        for(ClientListener l:listeners)
        {
            l.onLogout();
        }
    }

    protected void fireOnAvatarClicked(int clientX,int clientY,final String username,final String bareJid)
    {
        for(ClientListener l:listeners)
        {
            l.onAvatarClicked(clientX,clientY,username,bareJid);
        }
    }

    protected void fireOnAvatarMouseOver(int clientX,int clientY,final String username,final String bareJid)
    {
        for(ClientListener l:listeners)
        {
            l.onAvatarMouseOver(clientX,clientY,username,bareJid);
        }
    }

    protected void fireOnAvatarMouseOut(int clientX,int clientY,final String username,final String bareJid)
    {
        for(ClientListener l:listeners)
        {
            l.onAvatarMouseOut(clientX,clientY,username,bareJid);
        }
    }

    protected void fireOnStatusTextUpdated(final String text)
    {
        for(ClientListener l:listeners)
        {
            l.onStatusTextUpdated(text);
        }
    }

    protected void fireOnError(final String error)
    {
        isLogined = false;
        for(ClientListener l:listeners)
        {
            l.onError(error);
        }
    }

    protected void fireOnBeforeLogin()
    {
        for(ClientListener l:listeners)
        {
            l.onBeforeLogin();
        }
    }

    protected void fireOnEndLogin()
    {
        isLogined = true;
        for(ClientListener l:listeners)
        {
            l.onEndLogin();
        }
    }

    protected void fireOnSuspend()
    {
        isLogined = false;
        for(ClientListener l:listeners)
        {
            l.onSuspend();
        }
    }

    protected void fireOnResume()
    {
        for(ClientListener l:listeners)
        {
            l.onResume();
        }
    }

    protected void fireOnMessageReceive(final String jid,final String message)
    {
        for(MessageReceiveListener l:messageReceivelisteners)
        {
            l.onMessageReceive(jid, message);
        }
    }
}
