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

import ru.it.lecm.im.client.ui.listeners.StatusMenuItemListener;
import ru.it.lecm.im.client.ui.listeners.StatusMenuListener;
import ru.it.lecm.im.client.xmpp.Session.ServerType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.utils.i18n;

public class StatusMenu extends Composite {

	private static StatusMenuUiBinder uiBinder = GWT
			.create(StatusMenuUiBinder.class);

	interface StatusMenuUiBinder extends UiBinder<Widget, StatusMenu> {
	}

	@UiField ListPanel menuContent;
	
	private final StatusMenuItem availableItem;
	private final StatusMenuItem awayItem;
	private final StatusMenuItem busyItem;
	private final StatusMenuItem chatItem;
	private final StatusMenuItem xaItem;
	private final StatusMenuItem invisibleItem;
	
	private StatusMenuItem currentItem;
	private StatusMenuListener listener;
	public StatusMenu(final ContextMenuUI menuWidget) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		availableItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_ONLINE), i18n.msg("Available"),new StatusMenuItemListener()
		{
			public void onSelect() 
			{
				menuWidget.hide();
				if(currentItem == availableItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(availableItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_ONLINE);
			}
		});
		chatItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_CHAT), i18n.msg("Free for chat"),new StatusMenuItemListener()
		{
			public void onSelect() 
			{
				menuWidget.hide();
				if(currentItem == chatItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(chatItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_CHAT);
			}
		});
		xaItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_XA), i18n.msg("Not available"),new StatusMenuItemListener()
		{
			public void onSelect() 
			{
				menuWidget.hide();
				if(currentItem == xaItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(xaItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_XA);
			}
		});
		awayItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_AWAY), i18n.msg("Away"),new StatusMenuItemListener()
		{
			public void onSelect() 
			{
				menuWidget.hide();
				if(currentItem == awayItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(awayItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_AWAY);
			}
		});
		busyItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_DND), i18n.msg("Busy"),new StatusMenuItemListener()
		{
			public void onSelect() 
			{
				menuWidget.hide();
				if(currentItem == busyItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(busyItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_DND);
			}
		});
		invisibleItem = new StatusMenuItem(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_INVISIBLE), i18n.msg("Invisible"),new StatusMenuItemListener()
		{
			public void onSelect() {
				menuWidget.hide();
				if(currentItem == invisibleItem)
					return;
				if(currentItem!=null)
					currentItem.setSelect(false);
				selectItem(invisibleItem);
				listener.onSetXmppStatus(XmppStatus.Status.STATUS_INVISIBLE);
			}
		});
		
		menuContent.add(availableItem);
		menuContent.add(chatItem);
		//menuContent.add(xaItem);
		menuContent.add(awayItem);
		menuContent.add(busyItem);
		if(iJab.conf.getXmppConf().getServerType().equals(ServerType.ejabberd))
			menuContent.add(invisibleItem);
	}
	
	public void setStatus(XmppStatus.Status status)
	{
		StatusMenuItem item;
		switch(status)
		{
		case STATUS_INVISIBLE:
			item = invisibleItem;
			break;
		case STATUS_DND:
			item = busyItem;
			break;
		case STATUS_XA:
			item = xaItem;
			break;
		case STATUS_CHAT:
			item = chatItem;
		case STATUS_AWAY:
			item = awayItem;
			break;
		case STATUS_ONLINE:
			item = availableItem;
			break;
		default:
			item = availableItem;
			break;
		}
		selectItem(item);
	}
	
	private void selectItem(StatusMenuItem item)
	{
		availableItem.setSelect(false);
		awayItem.setSelect(false);
		busyItem.setSelect(false);
		availableItem.setSelect(false);
		chatItem.setSelect(false);
		xaItem.setSelect(false);
		invisibleItem.setSelect(false);
		
		currentItem = item;
		currentItem.setSelect(true);
	}
	
	public void setListener(StatusMenuListener l)
	{
		listener = l;
	}

}
