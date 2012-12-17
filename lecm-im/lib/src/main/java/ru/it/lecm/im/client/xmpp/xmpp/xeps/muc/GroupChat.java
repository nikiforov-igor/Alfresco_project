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
package ru.it.lecm.im.client.xmpp.xmpp.xeps.muc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.events.Events;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.stanzas.Presence.Type;
import ru.it.lecm.im.client.xmpp.JID;

public class GroupChat {

	public static enum Stage {
		connected, connecting, error, none
	}

	private final Map<JID, Affiliation> affiiations = new HashMap<JID, Affiliation>();

	private final GroupChatManager groupChatManager;

	private final MultiUserChatPlugin multiUserChatPlugin;

	private final Map<JID, Presence> presences = new HashMap<JID, Presence>();

	private final Map<JID, Role> roles = new HashMap<JID, Role>();

	private final JID roomJid;

	private Stage stage;

	private Object userData;

	private String password;
	
	private String roomName;
	
	private List<GroupChatListener> listeners = new ArrayList<GroupChatListener>();

	GroupChat(JID groupJid, GroupChatManager chatManager, MultiUserChatPlugin multiUserChatPlugin) {
		this.roomJid = JID.fromString(groupJid.toStringBare());
		this.groupChatManager = chatManager;
		this.multiUserChatPlugin = multiUserChatPlugin;
	}

	public Affiliation getAffiliation(JID jid) {
		Affiliation a = this.affiiations.get(jid);
		return a == null ? Affiliation.none : a;
	}
	
	public String getRoomName()
	{
		return roomName;
	}
	
	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}

	public String getNickname() {
		return this.roomJid.getResource();
	}

	public Presence getPresence(JID jid) {
		return this.presences.get(jid);
	}

	public Role getRole(JID jid) {
		Role r = this.roles.get(jid);
		return r == null ? Role.none : r;
	}

	public JID getRoomJid() {
		return roomJid;
	}

	@SuppressWarnings("unchecked")
	public <T> T getUserData() {
		return (T) userData;
	}

	public void join() {
		stage = Stage.connecting;
		Presence presence = new Presence(Type.available);
		Packet x = presence.addChild("x", "http://jabber.org/protocol/muc");
		presence.setTo(this.roomJid);
		if (password != null) {
			x.addChild("password", null).setCData(password);
		}
		multiUserChatPlugin.send(presence);
	}

	public void leave() {
		Presence presence = new Presence(Type.unavailable);
		presence.setTo(this.roomJid);
		multiUserChatPlugin.send(presence);
	}
	
	void process(Message message)
	{
		fireOnMessage(message);
	}

	void process(Presence presence) {
		final GroupChatEvent event = new GroupChatEvent(presence, this);
		Packet $x = presence.getChild("x", "http://jabber.org/protocol/muc#user");
		final MucXPacket x = $x == null ? null : new MucXPacket($x);
		final Set<String> statuses = x == null ? null : x.getStatusCodes();
		if (statuses != null) {
			event.setKicked(statuses.contains("307"));
		}

		if (presence.getType() == Type.error) {
			if (stage == Stage.connecting) 
			{
				fireOnGCJoinDeny(event);
				groupChatManager.remove(this);
				multiUserChatPlugin.fireEvent(Events.groupChatJoinDeny, event);
			}
		} else if (presence.getType() == Type.unavailable) 
		{
			presences.remove(presence.getFrom());
			affiiations.remove(presence.getFrom());
			roles.remove(presence.getFrom());
			if (presence.getFrom().equals(roomJid)) 
			{
				fireOnGCLeaved(event);
				groupChatManager.remove(this);
				multiUserChatPlugin.fireEvent(Events.groupChatLeaved, event);
			}
			else
			{
				fireOnUserLeaved(event);
			}
		} else 
		{
			presences.put(presence.getFrom(), presence);
			fireOnUserPresenceChange(event);
			if (x != null) {
				Role r = x.getRole();
				Affiliation a = x.getAffiliation();
				if (r != null)
					roles.put(presence.getFrom(), r);
				if (a != null)
					affiiations.put(presence.getFrom(), a);

				//if (stage == Stage.connecting && statuses.contains("100")) {
				if (stage == Stage.connecting) {
					stage = Stage.connected;
					this.fireOnGCJoined(event);
					multiUserChatPlugin.fireEvent(Events.groupChatJoined, new GroupChatEvent(presence, this));
				}
			}
		}
	}

	public void send(String body) {
		Message message = new Message(ru.it.lecm.im.client.xmpp.stanzas.Message.Type.groupchat, roomJid.getBareJID(), null, body, null);
		multiUserChatPlugin.send(message);
	}

	public void setNickname(String nickname) {
		this.roomJid.setResource(nickname);
	}

	public <T> void setUserData(T userData) {
		this.userData = userData;
	}

	public void setPassword(String password) {
		this.password = password == null || password.length() == 0 ? null : password;
	}
	
	public void addListener(GroupChatListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(GroupChatListener l)
	{
		listeners.remove(l);
	}
	
	void fireOnGCJoinDeny(GroupChatEvent gcEvent)
	{
		try
		{
			for(GroupChatListener l:listeners)
				l.onGCJoinDeny(gcEvent);
		}
		catch(Exception e)
		{
		}
	}
	
	void fireOnGCLeaved(GroupChatEvent gcEvent)
	{
		try
		{
			for(GroupChatListener l:listeners)
				l.onGCLeaved(gcEvent);
		}
		catch(Exception e)
		{
		}
	}
	
	void fireOnGCJoined(GroupChatEvent gcEvent)
	{
		try
		{
			for(GroupChatListener l:listeners)
				l.onGCJoined(gcEvent);
		}
		catch(Exception e)
		{
		}
	}
	
	void fireOnUserLeaved(GroupChatEvent gcEvent)
	{
		for(GroupChatListener l:listeners)
			l.onUserLeaved(gcEvent);
	}
	
	void fireOnUserPresenceChange(GroupChatEvent gcEvent)
	{
		try
		{
			for(GroupChatListener l:listeners)
				l.onUserPresenceChange(gcEvent);
		}
		catch(Exception e)
		{
		}
	}
	
	void fireOnMessage(Message message)
	{
		try
		{
			for(GroupChatListener l:listeners)
				l.onMessage(message);
		}
		catch(Exception e)
		{
		}
		
	}
}
