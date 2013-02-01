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
 * Mar 12, 2010
 */
package ru.it.lecm.im.client.ui;


import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyItem.Action;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyList;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin.RetrieveListHandler;
import ru.it.lecm.im.client.xmpp.xmpp.privacy.PrivacyListsPlugin.RetrieveListsNamesHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.XmppClient;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class BlackListWnd extends DialogBox
{
	private PrivacyListsPlugin privacyListsPlugin;
	private PrivacyList blackList = null;
	private List<PrivacyItem> removeList = new ArrayList<PrivacyItem>();
	
	private final FlowPanel wnd; 
	private FlowPanel prompt;
	public BlackListWnd()
	{
		setText(i18n.msg("Управление чёрным списком"));
		setGlassEnabled(false);
		setAnimationEnabled(true);
		setModal(false);
		
		prompt = new FlowPanel();
		prompt.setStyleName("ijab-blacklistwnd-prompt");
		HTML promptText = new HTML(i18n.msg("Загружается чёрный список, пожалуйста ожидайте..."));
		prompt.add(promptText);
		
		
		HTML title = new HTML(i18n.msg("Remove your friend from black list."));
		title.setStyleName("ijab-blacklistwnd-title");
		
		wnd = new FlowPanel();
		wnd.setStyleName("ijab-blacklistwnd");
		
		wnd.add(prompt);
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vPanel.add(title);
		vPanel.add(wnd);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    hPanel.setSpacing(5);
	    
	    Button cancelButton = new Button(i18n.msg("Отмена"));
	    cancelButton.addClickHandler(new ClickHandler()
	    {
			public void onClick(ClickEvent event) 
			{
				hide();
			}
	    });

		Button confirmButton = new Button(i18n.msg("Ok"));
		confirmButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(blackList!=null && !removeList.isEmpty())
				{
					for(PrivacyItem item:removeList)
					{
						item.setAllKinds();
						item.setAction(Action.allow);
					}
				}
				blackList.commit();
				removeList.clear();
				hide();
			}
			
		});
		hPanel.add(cancelButton);
		hPanel.add(confirmButton);
		vPanel.add(hPanel);
		vPanel.setCellHorizontalAlignment(hPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		if(iJab.client != null)
		{
			privacyListsPlugin = Session.instance().getPrivacyListPlugin();
			loadBlackList();
		}
		setWidget(vPanel);
	}
	
	
	private void loadBlackList()
	{
		if(!privacyListsPlugin.isListNamesRetrieved())
		{
			privacyListsPlugin.retrieveListsNames(new RetrieveListsNamesHandler()
			{
				public void onRetrieve(String activeListName,
						String defaultListName, Set<String> listNames) 
				{
					loadBlackList();
				}
			});
		}
		else
		{
			if(privacyListsPlugin.isListExists(XmppClient.BLACKLIST))
			{
				privacyListsPlugin.retrieveList(XmppClient.BLACKLIST, new RetrieveListHandler()
				{
					public void onRtrieveList(String listName, PrivacyList list) 
					{
						blackList = list;
						wnd.remove(prompt);
						for(final PrivacyItem item:list.getItems())
						{
							if(!item.getAction().equals(Action.deny))
								continue;
							final BlackListWndItem wndItem = new BlackListWndItem(item);
							wndItem.setListener(new BlackListWndItem.RemoveListener()
							{
								public void onRemove() 
								{
									removeList.add(item);
									wnd.remove(wndItem);
								}
							});
							wnd.add(wndItem);
						}
						if(wnd.getWidgetCount()==0)
						{
							prompt.clear();
							HTML promptText = new HTML(i18n.msg("No contact in blacklist!"));
							prompt.add(promptText);
							wnd.add(prompt);
						}
					}
				});
			}
			else
			{
				//no black list now
				prompt.clear();
				HTML promptText = new HTML(i18n.msg("No contact in blacklist!"));
				prompt.add(promptText);
				blackList = null;
			}
		}
	}
}
