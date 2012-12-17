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
package ru.it.lecm.im.client.xmpp.xmpp.roster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.it.lecm.im.client.xmpp.*;
import ru.it.lecm.im.client.xmpp.Plugin;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Storage;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.PluginState;
import ru.it.lecm.im.client.xmpp.citeria.Criteria;
import ru.it.lecm.im.client.xmpp.citeria.ElementCriteria;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.util.StringUtil;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem.Subscription;

public class RosterPlugin implements Plugin {

	private Map<String, RosterItem> rosterItemsByBareJid = new HashMap<String, RosterItem>();
	private List<String> transports = new ArrayList<String>();
	private List<RosterListener> rosterListeners = new ArrayList<RosterListener>();

	private boolean rosterReceived;

	private Session session;

	private PluginState stage = PluginState.NONE;
	
	private static final String STORAGEKEY = "iJabRoster";
	
	
	private static final String INIT_ID = "get_roster_0";

	public RosterPlugin(Session session) {
		this.session = session;
	}

	public void addItem(JID jid, String name, List<String> groupsNames, ResponseHandler handler) {
		if (!fireBeforeAddItemfinal(jid, name, groupsNames))
			return;
		IQ iq = new IQ(IQ.Type.set);
		String id = String.valueOf(Session.nextId());
		iq.setAttribute("id", id);

		Packet query = iq.addChild("query", "jabber:iq:roster");

		Packet item = query.addChild("item", null);
		item.setAttribute("jid", jid.toString());
		item.setAttribute("name", name);

		if (groupsNames != null) {
			for (int i = 0; i < groupsNames.size(); i++) {
				Packet group = item.addChild("group", null);
				group.setCData(groupsNames.get(i).toString());
			}
		}

		session.addResponseHandler(iq, handler);
	}

	public void addItem(JID jid, String text, String[] itemGroups, ResponseHandler itemEditorDialog) {
		List<String> groups = new ArrayList<String>();
		if (itemGroups != null) {
			for (int i = 0; i < itemGroups.length; i++) {
				groups.add(itemGroups[i]);
			}
		}
		addItem(jid, text, groups, itemEditorDialog);
	}

	public void addItem(RosterItem ri, ResponseHandler itemEditorDialog) {
		addItem(JID.fromString(ri.getJid()), ri.getName(), ri.getGroups(), itemEditorDialog);
	}

	public void addRosterListener(RosterListener listener) {
		this.rosterListeners.add(listener);
	}

	private void fireAddRosterItem(final RosterItem rosterItem) {
		for (int i = 0; i < rosterListeners.size(); i++) {
			rosterListeners.get(i).onAddItem(rosterItem);
		}
	}
	
	private boolean fireBeforeAddItemfinal(JID jid, final String name, final List<String> groupsNames) {
		try {
			for (RosterListener listener : this.rosterListeners) {
				listener.beforeAddItem(jid, name, groupsNames);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void fireEndRosterUpdating() {
		for (int i = 0; i < rosterListeners.size(); i++) {
			rosterListeners.get(i).onEndRosterUpdating();
		}
	}

	private void fireRemoveRosterItem(final RosterItem rosterItem) {
		for (int i = 0; i < rosterListeners.size(); i++) {
			rosterListeners.get(i).onRemoveItem(rosterItem);
		}
	}

	private void fireStartRosterUpdating() {
		for (int i = 0; i < rosterListeners.size(); i++) {
			rosterListeners.get(i).onStartRosterUpdating();
		}
	}

	private void fireUpdateRosterItem(final RosterItem rosterItem) {
		for (int i = 0; i < rosterListeners.size(); i++) {
			rosterListeners.get(i).onUpdateItem(rosterItem);
		}
	}

	public List<RosterItem> getAllRosteritems() {
		return new ArrayList<RosterItem>(this.rosterItemsByBareJid.values());
	}
	
	public int getAllRosterItemCount()
	{
		return getAllRosteritems().size();
	}

	public Criteria getCriteria() 
	{
		return ElementCriteria.name("iq").add(
				ElementCriteria.name("query", new String[] { "xmlns" }, new String[] { "jabber:iq:roster" }));
	}

	public String[] getGroupsByJid(JID jid) {
		RosterItem rosterItem = this.rosterItemsByBareJid.get(jid.toStringBare());
		if (rosterItem != null) {
			return rosterItem.getGroups();
		}
		return null;
	}

	public List<String> getJidsByGroupName(String groupName) {
		ArrayList<String> result = new ArrayList<String>();
		Iterator<RosterItem> iterator = this.rosterItemsByBareJid.values().iterator();
		while (iterator.hasNext()) {
			RosterItem ri = iterator.next();
			boolean ok = false;
			if (groupName == null && (ri.getGroups() == null || ri.getGroups().length == 0)) {
				ok = true;
			} else if (groupName != null && ri.getGroups() != null) {
				for (int i = 0; i < ri.getGroups().length; i++) {
					ok = ok | groupName.equals(ri.getGroups()[i]);
				}
			}
			if (ok) {
				result.add(ri.getJid());
			}
		}
		return result;
	}

	public String getNameByJid(JID from) 
	{
		RosterItem rosterItem = this.rosterItemsByBareJid.get(from.toStringBare());
		if (rosterItem != null) {
			return rosterItem.getName();
		}
		return null;
	}

	public void getRoster(final AsyncCallback<?> callback) 
	{
		stage = PluginState.IN_PROGRESS;
		IQ iq = new IQ(IQ.Type.get);
		iq.setAttribute("id", INIT_ID);
		iq.addChild("query", "jabber:iq:roster");
		session.addResponseHandler(iq, new ResponseHandler() 
		{
			public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) 
			{
				rosterReceived = true;
				stage = PluginState.ERROR;
				if(callback!=null)
				{
					callback.onFailure(null);
				}
			}
			public void onResult(IQ iq) 
			{
				GWT.log("process roster at function getRoster call back!", null);
				stage = PluginState.SUCCESS;
				rosterReceived = true;
				processRoster(iq);
				if(callback!=null)
					callback.onSuccess(null);
			}
		});
		/*
		if(session.getServerType().equals(ServerType.ejabberd))
			session.send(iq);
		else
		{
			session.addResponseHandler(iq, new ResponseHandler() 
			{
				public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) 
				{
					rosterReceived = true;
					stage = PluginState.ERROR;
					if(callback!=null)
					{
						callback.onFailure(null);
					}
				}
				public void onResult(IQ iq) 
				{
					GWT.log("process roster at function getRoster call back!", null);
					stage = PluginState.SUCCESS;
					rosterReceived = true;
					if(useBigRoster&&iq.getAtribute("id").equalsIgnoreCase(INIT_ID))
					{
						processInitRoster(iq);
					}
					else
						processRoster(iq);
				}
			});
		}
		*/
	}

	public RosterItem getRosterItem(JID jid) 
	{
		RosterItem rosterItem = this.rosterItemsByBareJid.get(jid.toStringBare());
		return rosterItem;
	}

	public List<RosterItem> getRosterItemsByGroupName(String groupName) 
	{
		ArrayList<RosterItem> result = new ArrayList<RosterItem>();
		Iterator<RosterItem> iterator = this.rosterItemsByBareJid.values().iterator();
		while (iterator.hasNext()) {
			RosterItem ri = iterator.next();
			boolean ok = false;
			if (groupName == null && (ri.getGroups() == null || ri.getGroups().length == 0)) {
				ok = true;
			} else if (groupName != null && ri.getGroups() != null) {
				for (int i = 0; i < ri.getGroups().length; i++) {
					ok = ok | groupName.equals(ri.getGroups()[i]);
				}
			}
			if (ok) {
				result.add(ri);
			}
		}
		return result;
	}

	public PluginState getStatus()
	{
		return stage;
	}

	public boolean isContactExists(JID jid) 
	{
		return this.rosterItemsByBareJid.containsKey(jid.toStringBare());
	}

	/**
	 * @return the rosterReceived
	 */
	public boolean isRosterReceived() 
	{
		return rosterReceived;
	}
	
	private void processRoster(final IQ iq)
	{
		Packet rosterQuery = iq.getFirstChild("query");
		try 
		{
			fireStartRosterUpdating();	
			final List<? extends Packet> childs = rosterQuery.getChildren();
			final int itemsCount = childs.size();
			IncrementalCommand ic = new IncrementalCommand()
			{
				int counter = 0;
				IQ queryIQ = new IQ(iq);
				public boolean execute() 
				{
					if(itemsCount == 0)
					{
						fireEndRosterUpdating();
						return false;
					}
					else
					{
						for(int i=0;i<50;i++)
						{
							processRosterItem(queryIQ,childs.get(counter));
							counter++;
							if(counter == itemsCount||counter>1000)
							{
								fireEndRosterUpdating();
								return false;
							}
						}
						return true;
					}
				}
			};
			DeferredCommand.addCommand(ic);
		}
		catch(Exception e) 
		{
			fireEndRosterUpdating();
		}
	}
	
	public void processPushRoster(final IQ iq)
	{
		Packet rosterQuery = iq.getFirstChild("query");
		try 
		{
			final List<? extends Packet> childs = rosterQuery.getChildren();
			final int itemsCount = childs.size();
			IncrementalCommand ic = new IncrementalCommand()
			{
				int counter = 0;
				IQ queryIQ = new IQ(iq);
				public boolean execute() 
				{
					if(itemsCount == 0)
					{
						return false;
					}
					else
					{
						for(int i=0;i<50;i++)
						{
							processRosterItem(queryIQ,childs.get(counter));
							counter++;
							if(counter == itemsCount||counter>1000)
							{
								return false;
							}
						}
						return true;
					}
				}
			};
			DeferredCommand.addCommand(ic);
		}
		catch(Exception e) 
		{
		}
	}

	public boolean process(Packet iq) 
	{
		if(iq.getAtribute("id").equals("push"))
		{
			processPushRoster(new IQ(iq));
			return true;
		}
		
		if(!iq.getAtribute("type").equals("result"))
		{
			rosterReceived = true;
			stage = PluginState.ERROR;
		}
		else
		{
			stage = PluginState.SUCCESS;
			rosterReceived = true;
			processRoster(new IQ(iq));
		}
		return true;
	}
	
	public List<String> getTransports()
	{
		return transports;
	}

	public void processRosterItem(final IQ iq, Packet item) 
	{
		final String jid = item.getAtribute("jid");
		// is a transport nodes
		String name = item.getAtribute("name");
		name = (name==null||name.length()==0)?StringUtil.jid2name(jid):name;
		final String tmp = item.getAtribute("subscription");
		final String _ask = item.getAtribute("ask");
		String order = item.getAtribute("order");
		boolean ask = _ask != null && "subscribe".equals(_ask);

		if (order != null) {
			order = "000000" + order;
			order = order.substring(order.length() - 6);
		}

		List<? extends Packet> grl = item.getChildren();
		final String[] groups = new String[grl.size()];
		for (int i = 0; i < grl.size(); i++) {
			Packet gri = grl.get(i);
			if (gri != null) {
				groups[i] = gri.getCData();
			} else {
				groups[i] = "";
			}
		}

		RosterItem rosterItem = this.rosterItemsByBareJid.get(jid);
		if(!jid.contains("@")&&tmp.equals("remove"))
		{
			transports.remove(jid);
		}
		else if(!jid.contains("@"))
		{
			transports.add(jid);
		}
		if (tmp.equals("remove")) 
		{
			rosterItemsByBareJid.remove(jid);
			fireRemoveRosterItem(rosterItem);
		}
		else if (rosterItem == null) 
		{
			final Subscription subscription = tmp == null ? Subscription.none : Subscription.valueOf(tmp);
			rosterItem = new RosterItem(jid, name, subscription, ask, groups, order);
			rosterItemsByBareJid.put(jid, rosterItem);
			fireAddRosterItem(rosterItem);
		}
		else 
		{
			final Subscription subscription = tmp == null ? Subscription.none : Subscription.valueOf(tmp);

			rosterItem.setName(name);
			rosterItem.setSubscription(subscription);
			rosterItem.setGroups(groups);
			rosterItem.setAsk(ask);
			rosterItem.setOrder(order);
			fireUpdateRosterItem(rosterItem);
		}
	}

	public void removeRosterItem(JID jidToRemove) 
	{
		IQ iq = new IQ(IQ.Type.set);
		iq.setAttribute("id", String.valueOf(Session.nextId()));

		Packet query = iq.addChild("query", "jabber:iq:roster");

		Packet item = query.addChild("item", null);
		item.setAttribute("jid", jidToRemove.toString());
		item.setAttribute("subscription", "remove");

		session.addResponseHandler(iq, new ResponseHandler() 
		{
			public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text) 
			{
			}

			public void onResult(IQ iq) 
			{
			}
		});
	}

	public void removeRosterListener(RosterListener listener) 
	{
		this.rosterListeners.remove(listener);
	}

	public void reset() 
	{
		final String prefix = Session.instance().getUser().getStorageID();
		Storage storage = Storage.createStorage(STORAGEKEY, prefix);
		storage.remove("index");
		this.rosterItemsByBareJid.clear();
		this.rosterReceived = false;
		this.stage = PluginState.NONE;
	}

	public void setInitializedDirty() 
	{
		this.rosterReceived = true;
		this.stage = PluginState.SUCCESS;
	}
}
