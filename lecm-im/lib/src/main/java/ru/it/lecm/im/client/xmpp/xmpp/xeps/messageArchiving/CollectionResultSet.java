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
 * Mar 25, 2010
 */
package ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class CollectionResultSet 
{
	private List<CollectionItem> collections = new ArrayList<CollectionItem>();
	
	private int first;
	private int firstIndex;
	private int last;
	private int count;
	
	public static CollectionResultSet createResultSetFromIQ(IQ iq)
	{
		if(!iq.getType().equals(IQ.Type.result))
			return null;
		Packet list = iq.getChild("list", "urn:xmpp:archive");
		List<CollectionItem> chats = new ArrayList<CollectionItem>();
		List<? extends Packet> childs = list.getChildren();
		for(Packet child:childs)
		{
			String name = child.getName();
			if(!name.equals("chat"))
				continue;
			JID with = JID.fromString(child.getAtribute("with"));
            String dateSource = child.getAtribute("start");
            Date start = MessageArchivingPlugin.parseDate(dateSource);
			CollectionItem item = new CollectionItem(with,start, dateSource);
			chats.add(item);
		}
		Packet setPacket = list.getFirstChild("set");
		
		int first = 0;
		int firstIndex = 0;
		int last = 0;
		/*
		Packet firstPacket = setPacket.getFirstChild("first");
		if(firstPacket != null)
		{
			first = Integer.parseInt(firstPacket.getCData());
			firstIndex = Integer.parseInt(firstPacket.getAtribute("index"));
		}
		
		Packet lastPacket = setPacket.getFirstChild("last");
		if(lastPacket!=null)
			last = Integer.parseInt(lastPacket.getCData());
		*/
		Packet countPacket = setPacket.getFirstChild("count");
		int count=0;
		if(countPacket!=null)
			count = Integer.parseInt(countPacket.getCData());
		return new CollectionResultSet(first,firstIndex,last,count,chats);
	}
	
	public CollectionResultSet(int first,int firstIndex,int last,int count,List<CollectionItem> collections)
	{
		this.first = first;
		this.firstIndex = firstIndex;
		this.last = last;
		this.count = count;
		this.collections = collections;
	}
	
	public int getFirst()
	{
		return first;
	}
	
	public int getFirstIndex()
	{
		return firstIndex;
	}
	
	public int getLast()
	{
		return last;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public List<CollectionItem> getCollections()
	{
		return collections;
	}
}
