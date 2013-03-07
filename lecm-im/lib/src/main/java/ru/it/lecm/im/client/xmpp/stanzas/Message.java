/*
 * tigase-xmpp4gwt
 * Copyright (C) 2007-2008 "Bartosz Małkowski" <bmalkow@tigase.org>
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
package ru.it.lecm.im.client.xmpp.stanzas;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.packet.PacketImp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bmalkow
 * 
 */
public class Message extends AbstractStanza {

	public static enum ChatState {
		active, composing, gone, inactive, paused
	}
	
	public static enum MsgEvent { OfflineEvent, DeliveredEvent, DisplayedEvent,
		ComposingEvent, CancelEvent }

	public static enum Type {
		chat, error, groupchat, headlines, normal,notify,syncsend,syncrecv
	}
	private String eventId = null;
	private List<MsgEvent> eventList = new ArrayList<MsgEvent>();
	public Message(Packet packet) {
		super(packet);
	}

	public Message(Type type, JID to, String subject, String body, String thread) {
		super(new PacketImp("message"));
		setType(type);
		setTo(to);
		setSubject(subject);
		setBody(body);
		setThread(thread);
	}

	public String getBody() {
		Packet body = getFirstChild("body");
		return body == null ? null : body.getCData();
	}

	public ChatState getChatState() {
		Packet chatstate = getChildByXMLNS("http://jabber.org/protocol/chatstates");
		if (chatstate != null) {
			return ChatState.valueOf(chatstate.getName());
		}
		return null;
	}
	
	public boolean containsEvents()
	{
		Packet xEvent = getChildByXMLNS("jabber:x:event");
		if(xEvent!=null)
			return true;
		else
			return false;
	}
	
	public boolean containsEvent(MsgEvent e)
	{
		Packet xEvent = getChildByXMLNS("jabber:x:event");
		if(xEvent!=null)
		{
			if(eventList.isEmpty())
				parseEvent();
			return eventList.contains(e);
		}
		else
			return false;
	}
	
	public void addEvent(MsgEvent e)
	{
		if(eventList.contains(e))
			return;
		
		Packet xEvent = getChildByXMLNS("jabber:x:event");
		if(xEvent == null)
			xEvent = addChild("x", "jabber:x:event");
		switch(e)
		{
		case OfflineEvent:
			xEvent.addChild("offline", "jabber:x:event");
			break;
		case DeliveredEvent:
			xEvent.addChild("delivered", "jabber:x:event");
			break;
		case DisplayedEvent:
			xEvent.addChild("displayed", "jabber:x:event");
			break;
		case ComposingEvent:
			xEvent.addChild("composing", "jabber:x:event");
			break;
		case CancelEvent:
			//add nothin
			break;
		}
		eventList.add(e);
	}
	
	private void parseEvent()
	{
		Packet xEvent = getChildByXMLNS("jabber:x:event");
		if(xEvent!=null)
		{
			List<? extends Packet> childs = xEvent.getChildren();
            for (Packet event : childs) {
                final String eventName = event.getName();
                if (eventName.equals("id"))
                    eventId = event.getCData();
                else if (eventName.equals("displayed"))
                    eventList.add(MsgEvent.DisplayedEvent);
                else if (eventName.equals("composing"))
                    eventList.add(MsgEvent.ComposingEvent);
                else if (eventName.equals("delivered"))
                    eventList.add(MsgEvent.DeliveredEvent);
            }
			if(eventList.isEmpty())
				eventList.add(MsgEvent.CancelEvent);
		}
	}

	public String getExtNick() {
		Packet thread = getFirstChild("nick");
		return thread == null ? null : thread.getCData();
	}

	public String getSubject() {
		Packet subject = getFirstChild("subject");
		return subject == null ? null : subject.getCData();
	}

	public String getThread() {
		Packet thread = getFirstChild("thread");
		return thread == null ? null : thread.getCData();
	}

	public Type getType() {
		final String type = getAtribute("type");
		try {
			return type != null ? Type.valueOf(type) : Type.normal;
		} catch (final IllegalArgumentException e) {
			return Type.normal;
		}
	}

	public void setBody(String value) {
		setChildrenValue("body", value);
	}

	public void setChatState(ChatState chatState) {
		Packet chatstate = getChildByXMLNS("http://jabber.org/protocol/chatstates");
		if (chatstate != null) {
			removeChild(chatstate);
		}
		if (chatState != null)
			addChild(chatState.name(), "http://jabber.org/protocol/chatstates");
	}

	public void setExtNick(String value) {
		Packet child = getFirstChild("nick");
		if (child == null && value != null) {
			child = addChild("nick", "http://jabber.org/protocol/nick");
		} else if (child != null && value == null) {
			removeChild(child);
		}
		if (child != null && value != null) {
			child.setCData(value);
		}
	}

	public void setSubject(String value) {
		setChildrenValue("subject", value);
	}

	public void setThread(String value) {
		setChildrenValue("thread", value);
	}

	public void setType(Type type) {
		setAttribute("type", type == null ? null : type.name());
	}
	
	public JID getSyncWho()
	{
		String ret = getAtribute("syncwho");
		if(ret == null||ret.length() == 0)
			return null;
		else
			return JID.fromString(ret);
	}
}
