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
package ru.it.lecm.im.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactViewGroup extends ContactViewGroupUI
{
	private Map<String,ContactViewItem> childs = new HashMap<String,ContactViewItem>();//widget id to widget
	private int onlineCount = 0;
	boolean onlineGroup = false;
	final private ContactView view;
	public ContactViewGroup(final ContactView view,String name, FlowPanel groupBody) 
	{
		super(name, groupBody);
		this.view = view;
	}
	
	public ContactViewGroup(final ContactView view,final String name,final FlowPanel groupBody,final String groupIconStyle)
	{
		super(name,groupBody,groupIconStyle);
		this.view = view;
		if(!groupIconStyle.equals("ijab-contactview-grouphead"))
			onlineGroup = true;
	}

    public void setActitve(String bareJid)
    {
        String itemId = getViewItemId(bareJid);
        if(childs.containsKey(itemId))
        {
            ContactViewItem ri = childs.get(itemId);
            ri.setActive();
        }
    }

    public void clearActive()
    {
        for (ContactViewItem item : childs.values())
        {
            item.clearActive();
        }
    }
	
	public String getGroupName()
	{
		return name;
	}
	
	public void filter(List<String> jids)
	{
		groupBody.setVisible(true);
		groupHeadTriangle.removeClassName("ui-icon-triangle-1-e");
		groupHeadTriangle.addClassName("ui-icon-triangle-1-s");
		//hide all item
		for(ContactViewItem item:childs.values())
		{
			item.setVisible(false);
		}
		
		for(String jid:jids)
		{
			String widgetId = getViewItemId(jid);
			ContactViewItem item = childs.get(widgetId);
			if(item!=null)
				item.setVisible(true);
		}
	}
	
	public void clearFilter()
	{
		for(ContactViewItem item:childs.values())
		{
			item.setVisible(true);
		}
		setGroupVisible(true);
	}
	
	public void addRosterItem(RosterItem item)
	{
		if(contains(item))
			return;
		
		ContactViewItem viewItem = new ContactViewItem(view,this,item,onlineGroup);
		groupBody.add(viewItem);
		XmppStatus.Status newStatus = XmppStatus.makeStatus(XmppProfileManager.getPresence(item.getJid()));
		viewItem.setXmppStatus(newStatus);
		childs.put(viewItem.getWidgetID(), viewItem);
		viewItem.setItemOdd(groupBody.getWidgetIndex(viewItem)%2!=0);
		//27.02.2013 updateGroupCount();
	}
	
	public void removeRosterItem(String bareJid)
	{
		if(childs.containsKey(getViewItemId(bareJid)))
		{
			ContactViewItem ri = childs.get(getViewItemId(bareJid));
			removeRosterItem(ri.getRosterItem());
		}
	}
	
	public void removeRosterItem(RosterItem item)
	{
		if(!contains(item))
			return;
		ContactViewItem viewItem = childs.remove(getViewItemId(item.getJid()));
		viewItem.destory();
		if(viewItem.getXmppStatus().ordinal()> XmppStatus.Status.STATUS_OFFLINE.ordinal())
		{
			onlineCount--;
		}
		int widgetPos = groupBody.getWidgetIndex(viewItem);
		boolean oddChange = !(widgetPos == groupBody.getWidgetCount()-1);
		groupBody.remove(viewItem);
		if(oddChange)
			updateItemOddStyle(widgetPos);
		updateGroupCount();
	}
	
	public void itemStatusUpdate(ContactViewItem item, XmppStatus.Status newStatus,XmppStatus.Status oldStatus)
	{
		int itemPos = groupBody.getWidgetIndex(item);
		int oddChangePos = itemPos;
        if(newStatus == XmppStatus.Status.STATUS_OFFLINE)
		{
			onlineCount--;
			updateGroupCount();
			if(groupBody.getWidgetCount()<=1)
				return;
            groupBody.add(item);
		}
		else
		{
			if(oldStatus == XmppStatus.Status.STATUS_OFFLINE)
			{
				onlineCount++;
				updateGroupCount();
			}
			if(groupBody.getWidgetCount()<=1)
				return;
            int afterIndex = -1;
			int startPos = 0;
			if(newStatus.ordinal()<oldStatus.ordinal())
			{
				startPos = itemPos+1;
			}
			for(int index=startPos;index<groupBody.getWidgetCount();index++)
			{
				ContactViewItem tmpItem = (ContactViewItem)groupBody.getWidget(index);
				if(newStatus.ordinal()>=tmpItem.getXmppStatus().ordinal())
				{
					afterIndex = index;
					break;
				}
			}
			if(afterIndex==-1)
				groupBody.add(item);
			else
			{
				if(afterIndex<itemPos)
					oddChangePos = afterIndex-1;
				groupBody.insert(item, afterIndex);
			}
			
		}
        updateItemOddStyle(oddChangePos);
    }
	
	private String getViewItemId(final String bareJid)
	{
		if(onlineGroup)
			return ContactViewItem.buildOnlineWidgetItemIDFromJid(bareJid);
		else
			return ContactViewItem.buildWidgetIDFromJid(bareJid);
	}
	
	
	public void updateGroupCount()
	{
		if(!onlineGroup)
			setGroupHeaderCount("["+onlineCount+"/"+childs.size()+"]");
		else
			setGroupHeaderCount("["+childs.size()+"]");
	}
	
	public void updateRosterItem(RosterItem item)
	{
		if(!contains(item))
			return;
		ContactViewItem viewItem = getChildViewItem(item);
		viewItem.setName(item.getName());
		viewItem.setTitle(item.getName()+"<"+item.getJid()+">");
	}
	
	private ContactViewItem getChildViewItem(RosterItem item)
	{
		return childs.get(getViewItemId(item.getJid()));
	}
	
	public boolean contains(RosterItem item)
	{
		return childs.containsKey(getViewItemId(item.getJid()));
	}
	
	public boolean contains(String bareJid)
	{
		return childs.containsKey(getViewItemId(bareJid));
	}
	
	public int getChildCount()
	{
		return childs.size();
	}
	
	private void updateItemOddStyle(int startPos)
	{
		if(startPos < 0)
		{
			startPos = 0;
		}
		boolean odd = false;
		for(int index=startPos;index<groupBody.getWidgetCount();index++)
		{
			ContactViewItem item = (ContactViewItem)groupBody.getWidget(index);
			item.setItemOdd(odd);
			odd = !odd;
		}
	}
}
