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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.XmppClient;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.ui.listeners.ContactViewListener;
import ru.it.lecm.im.client.ui.listeners.ContextMenuItemListener;
import ru.it.lecm.im.client.ui.listeners.SearchBoxListener;
import ru.it.lecm.im.client.utils.PopupPrompt;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;

import java.util.*;

public class ContactView extends Composite {

	private static ContactViewUiBinder uiBinder = GWT.create(ContactViewUiBinder.class);
	
	@UiField FlowPanel widget;
	
	private Map<String,ContactViewGroup> groups = new HashMap<String,ContactViewGroup>();
	private Map<String,ContactViewGroup> groupsCache = new HashMap<String,ContactViewGroup>();

	
	private ContactViewGroup onlineGroup;
	private ContactViewGroup noGroup = null;
	
	private List<ContactViewListener> listeners = new ArrayList<ContactViewListener>();
	private final SearchBoxListener searchListener;
	private ContextMenu contextMenu;
	private String onlineGroupName = i18n.msg("В сети");
	
	private ContextMenuItem addToBlackListItem = null;
	private ContextMenuItem manageBlackListItem = null;
	private MenuBar groupSubMenu = null;

    public void clearActive()
    {
        onlineGroup.clearActive();
    }

    public void setActive(String bareJid)
    {
        clearActive();
        onlineGroup.setActitve(bareJid);
    }

    public void onEndRosterUpdating() {
        for(String key : groupsCache.keySet())
        {
            if (!groups.containsKey(key))
            {
                ContactViewGroup contactViewGroup = groupsCache.get(key);
                groups.put(key, contactViewGroup);
                widget.add(contactViewGroup);
                widget.add(contactViewGroup.getGroupBody());
            }
        }
        groupsCache.clear();
    }

    interface ContactViewUiBinder extends UiBinder<Widget, ContactView>
	{
	}

	public ContactView()
	{
		initWidget(uiBinder.createAndBindUi(this));
		if(iJab.conf.isHideOnlineGroup())
			onlineGroupName = "OnlineHideen";
		onlineGroup = ensureGroup(onlineGroupName,"ijab-contactview-online-grouphead");
		if(iJab.conf.isHideOnlineGroup())
			onlineGroup.setGroupVisible(false);
		searchListener = new SearchBoxListener()
		{
			public void onSearch(List<String> results) {
				filterGroupByJids(results);
			}
			
			public void onFinished() {
				clearFilter();
			}
			
			public void onCancel() {
				clearFilter();
			}
		};
		
		//build conext menu
		if(iJab.conf.isRosterManageEnabled())
		{
			contextMenu = new ContextMenu();
			contextMenu.addItem(new ContextMenuItem(contextMenu,i18n.msg("Отправить сообщение"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					ContactViewItem item = (ContactViewItem)data;
					fireOnItemClick(item.getRosterItem());
				}
			}));
			contextMenu.addItem(new ContextMenuItem(contextMenu,i18n.msg("Показать историю переписки"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					ContactViewItem item = (ContactViewItem)data;
					if(iJab.client != null)
					{
						XmppClient client = iJab.client;
						client.viewMessageArchive(JID.fromString(item.getRosterItem().getJid()));
					}
				}
			}));
			
			contextMenu.addSeparator();
			
			groupSubMenu = new MenuBar(true);
			groupSubMenu.setFocusOnHoverEnabled(true);
			groupSubMenu.setStylePrimaryName("ijab-contextmenu-body");
			groupSubMenu.setAutoOpen(true);
			contextMenu.addItem(i18n.msg("Группа"),groupSubMenu);
			
			/*
			contextMenu.addItem(new ContextMenuItem(contextMenu,i18n.msg("Rerequest Authorization"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					ContactViewItem item = (ContactViewItem)data;
					Window.alert("click on:"+item.getJid());
				}
			}));
			*/
			
			contextMenu.addItem(new ContextMenuItem(contextMenu,i18n.msg("Удалить контакт"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					ContactViewItem item = (ContactViewItem)data;
					if(Window.confirm(i18n.msg("Вы уверены, что хотите удалить контакт ")+XmppProfileManager.getName(item.getRosterItem().getJid())))
					{
						Session.instance().getRosterPlugin().removeRosterItem(JID.fromString(item.getRosterItem().getJid()));
						for(ContactViewGroup group:groups.values())
						{
							group.removeRosterItem(item.getJid());
						}
						removeEmptyGroups();
					}
				}
			}));
			contextMenu.addSeparator();
			addToBlackListItem = new ContextMenuItem(contextMenu,i18n.msg("Занести в черный список"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					ContactViewItem item = (ContactViewItem)data;
					if(iJab.client != null)
					{
						iJab.client.addToBlackList(item.getJid(), true);
						PopupPrompt.prompt(XmppProfileManager.getName(item.getJid()) + " " + i18n.msg("был занесён в чёрный список!"));
					}
				}
			});
			contextMenu.addItem(addToBlackListItem);
			manageBlackListItem = new ContextMenuItem(contextMenu,i18n.msg("Управление черным списком"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					BlackListWnd wnd = new BlackListWnd();
					wnd.center();
					wnd.show();
				}
			});
			contextMenu.addItem(manageBlackListItem);
		}
	}
	
	public SearchBoxListener getSearchListener()
	{
		return searchListener;
	}
	
	private void filterGroupByJids(List<String> jids)
	{
		List<String> visibleGroups = new ArrayList<String>();
		for(String jid:jids)
		{
			String groupName = getGroupNameByJid(jid);
			groupName = groupName==null?"":groupName;
			if(groupName.length()>0&&!visibleGroups.contains(groupName))
            {
                visibleGroups.add(groupName);
            }

			if(!iJab.conf.isHideOnlineGroup())
			{
				if(onlineGroup.contains(jid)&&!visibleGroups.contains(onlineGroupName))
					visibleGroups.add("Online");
			}
				
		}
		
		for(String group:groups.keySet())
		{
			if(visibleGroups.contains(group))
			{
				groups.get(group).setGroupVisible(true);
				groups.get(group).filter(jids);
			}
			else
				groups.get(group).setGroupVisible(false);
		}
	}
	
	private void clearFilter()
	{
		for(ContactViewGroup group:groups.values())
		{
			if(group==onlineGroup&&iJab.conf.isHideOnlineGroup())
				continue;
			group.clearFilter();
		}
	}
	
	private ContactViewGroup ensureGroup(final String groupName,final String groupStyle)
	{
		if(groupsCache.containsKey(groupName))
			return groupsCache.get(groupName);
		
		FlowPanel groupBody = new FlowPanel();
		ContactViewGroup group;
		if(groupStyle == null)
			group = new ContactViewGroup(this,groupName,groupBody);
		else
			group = new ContactViewGroup(this,groupName,groupBody,groupStyle);
		groupsCache.put(groupName, group);
		return group;
	}
	
	private ContactViewGroup ensureGroup(final String groupName)
	{
		return ensureGroup(groupName,null);
	}
	
	void removeEmptyGroups()
	{
		List<ContactViewGroup> removes = new ArrayList<ContactViewGroup>();
		for(ContactViewGroup group:this.groups.values())
		{
			if(group.getChildCount()==0)
				removes.add(group);
		}
		for(ContactViewGroup group:removes)
			removeGroup(group);
	}
	
	private void removeGroup(ContactViewGroup groupItem)
	{
		widget.remove(groupItem.getGroupBody());
		widget.remove(groupItem);
		groups.remove(groupItem.getGroupName());
	}
	
	private String getGroupNameByJid(final String jid)
	{
		String widgetId = ContactViewItem.buildWidgetIDFromJid(jid);
		Element element = DOM.getElementById(widgetId);
		if(element == null)
			return null;
		String groupId = null;
		try
		{
			Element groupBodyEl = element.getParentElement();
			groupId = groupBodyEl.getId();
			groupId = groupId.substring(23, groupId.indexOf("_div"));
		}
		catch(Exception e)
		{
            Log.log("ContactView exception: "+ e.toString());
        }
		return groupId;
	}

	
	public void clear()
	{
		widget.clear();
		groups.clear();
		onlineGroup = ensureGroup(onlineGroupName,"ijab-contactview-online-grouphead");
		if(iJab.conf.isHideOnlineGroup())
			onlineGroup.setGroupVisible(false);
		noGroup = null;
	}
	
	public void updateRosterItem(RosterItem item)
	{
		//first update the online item if it's online
		if(onlineGroup.contains(item))
			onlineGroup.updateRosterItem(item);
		
		String oldGroup = getGroupNameByJid(item.getJid());
		String newGroup = i18n.msg("Friends");
		if(item.getGroups().length > 0&&item.getGroups()[0] != null)
		{
			newGroup = item.getGroups()[0];
		}
		
		if(!newGroup.equals(oldGroup))
		{
			ContactViewGroup oldGroupItem = groups.get(oldGroup);
			if(oldGroupItem!=null)
			{
				oldGroupItem.removeRosterItem(item);
				if(oldGroupItem.getChildCount()==0)
				{
					widget.remove(oldGroupItem);
					groups.remove(oldGroup);
				}
			}
				
			ContactViewGroup newGroupItem = ensureGroup(newGroup);
			newGroupItem.addRosterItem(item);
		}
		else
		{
			ContactViewGroup oldGroupItem = groups.get(oldGroup);
			oldGroupItem.updateRosterItem(item);
		}
	}
	
	public void addRosterItem(RosterItem item)
	{
		if(item.getGroups().length == 0||item.getGroups()[0] == null)//||item.getGroups()[0].length()==0)
		{
			if(noGroup == null)
				noGroup = ensureGroup(i18n.msg("Собеседники"));
			noGroup.addRosterItem(item);
		}
		else
		{
			ContactViewGroup group = ensureGroup(item.getGroups()[0]);
			group.addRosterItem(item);
		}
	}
	
	public void removeRosterItem(RosterItem rosterItem)
	{
		if(rosterItem == null)
			return;
		String bareJid = rosterItem.getJid();
		String groupId = getGroupNameByJid(bareJid);
		ContactViewGroup group = groups.get(groupId);
		if(group!=null)
			group.removeRosterItem(rosterItem);
		onlineGroup.removeRosterItem(rosterItem);
	}
	
	public void addOnlineGroupItem(final Presence presenceItem,final RosterItem rosterItem)
	{
		//first add online item at online group
		RosterItem addRosterItem = rosterItem;
		if(rosterItem == null)
		{
			addRosterItem = new RosterItem();
			addRosterItem.setJid(presenceItem.getFrom().toStringBare());
		}
		onlineGroup.addRosterItem(addRosterItem);
	}
	
	public void removeOnlineGroupItem(final Presence presenceItem,final RosterItem rosterItem)
	{
		//first remove the contact from online group
		RosterItem addRosterItem = rosterItem;
		if(rosterItem == null)
		{
			addRosterItem = new RosterItem();
			addRosterItem.setJid(presenceItem.getFrom().toStringBare());
		}
		onlineGroup.removeRosterItem(addRosterItem);
	}

    public void addListener(ContactViewListener l)
	{
		listeners.add(l);
	}
	
	public void removeListener(ContactViewListener l)
	{
		listeners.remove(l);
	}
	
	public void fireOnItemClick(RosterItem item)
	{
		for(ContactViewListener l:listeners)
		{
			l.onItemClick(item);
		}
	}

    private String getItemGroupName(ContactViewItem item)
	{
		String curGroupName = "";
		if(item.getRosterItem().getGroups().length>0)
			curGroupName = item.getRosterItem().getGroups()[0];
		curGroupName = curGroupName==null?"":curGroupName;
		curGroupName = curGroupName.length()==0?item.getGroupName():curGroupName;
		return curGroupName;
	}
	
	public void onItemContextMenu(final ContactViewItem item,int x,int y)
	{
		if(iJab.conf.isRosterManageEnabled())
		{
			contextMenu.setData(item);
			//build the sub groups menu
			final String finalGroupName = getItemGroupName(item);
			groupSubMenu.setVisible(true);
			Iterator<String> it = this.groups.keySet().iterator();
			groupSubMenu.clearItems();
			int count=0;
			while(it.hasNext())
			{
				final String groupName = it.next();
				if(isSpeicalGroup(groupName))
					continue;
				ContextMenuItem menuItem = new ContextMenuItem(contextMenu,groupName,true,new ContextMenuItemListener()
				{
					public void onSelected(Object data) 
					{
						if(finalGroupName.equals(groupName))
							return;
						changeGroup(item,finalGroupName,groupName);
						
					}
				});
				groupSubMenu.addItem(menuItem);
				if(groupName.equals(finalGroupName))
					menuItem.setHTML("<font color='#E6007C'><b>"+groupName+"</b></font>");
				count++;
			}
			if(count>0)
				groupSubMenu.addSeparator();
			ContextMenuItem newGroupItem = new ContextMenuItem(contextMenu,i18n.msg("Добавить группу"),true,new ContextMenuItemListener()
			{
				public void onSelected(Object data) 
				{
					String newGroupName = Window.prompt(i18n.msg("Введити имя новой группы."), "");
					if(newGroupName==null||newGroupName.length()==0)
						return;
					if(groups.containsKey(newGroupName))
						Window.alert(i18n.msg("Группа уже существует!"));
					changeGroup(item,finalGroupName,newGroupName);
				}
			});
			groupSubMenu.addItem(newGroupItem);
			contextMenu.showRelativeTo(item);
		}
	}
	
	private void changeGroup(ContactViewItem item,String oldGroupName,String newGroupName)
	{
		RosterItem ri = item.getRosterItem();
		String[] groups = {newGroupName};
		ri.setGroups(groups);
		Session.instance().getRosterPlugin().addItem(ri, new ResponseHandler()
		{

			public void onError(IQ iq, ErrorType errorType,
					ErrorCondition errorCondition, String text) 
			{
			}
			public void onResult(IQ iq) {
			}
		});
		ContactViewGroup oldGroup = this.groups.get(oldGroupName);
		if(oldGroup!=null&&!oldGroupName.equals(onlineGroupName))
		{
			oldGroup.removeRosterItem(ri);
			if(oldGroup.getChildCount()==0)
				removeGroup(oldGroup);
		}
		ContactViewGroup newGroup = this.groups.get(newGroupName);
		if(newGroup == null)
			newGroup = ensureGroup(newGroupName);
		newGroup.addRosterItem(ri);
	}
	
	private boolean isSpeicalGroup(String groupName)
	{
		//return groupName.equals(onlineGroupName)||groupName.equals(i18n.msg("Friends"));
		return groupName.equals(onlineGroupName);
	}
	
	public List<String> getXmppGroups()
	{
		List<String> ret = new ArrayList<String>();
		for(String groupName:this.groups.keySet())
		{
			if(!groupName.equals(onlineGroupName)&&!groupName.equals(i18n.msg("Собеседники")))
				ret.add(groupName);
		}
		return ret;
	}

    public void RefreshGroupCounters(){
        for ( ContactViewGroup group : this.groups.values() )
        {
           group.updateGroupCount();
        }
    }
}
