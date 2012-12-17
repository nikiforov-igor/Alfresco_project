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
 * Mar 30, 2010
 */
package ru.it.lecm.im.client.ui;

import java.util.List;

import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomItem;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomListener;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomPlugin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCRoomWidget extends Composite {

	private static MUCRoomWidgetUiBinder uiBinder = GWT
			.create(MUCRoomWidgetUiBinder.class);

	interface MUCRoomWidgetUiBinder extends UiBinder<Widget, MUCRoomWidget> {
	}

	@UiField Element createButtonText;
	@UiField Anchor createButton;
	@UiField Element refreshButtonText;
	@UiField Anchor refreshButton;
	@UiField FlowPanel mainWidget;
	@UiField FocusFlowPanel listWidget;
	@UiField FlowPanel toolBar;
	@UiField HTMLPanel info;
	@UiField Element disText;
	
	private boolean connected;
	private boolean roomLoaded = false;
	private MucRoomListener roomListener = null;
	final private ChatPanelBar chatpanel;
	public MUCRoomWidget(ChatPanelBar chatpanelBar) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.chatpanel = chatpanelBar;
		createButton.setTitle(i18n.msg("Create Room"));
		createButtonText.setInnerText(i18n.msg("Create Room"));
		refreshButton.setTitle(i18n.msg("Refresh"));
		refreshButtonText.setInnerText(i18n.msg("Refresh"));
		disText.setInnerText(i18n.msg("Disconnected"));
		setConnected(false);
		createButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(Session.instance().isDisconnected())
					return;
				MUCJoinDialog joinDialog = new MUCJoinDialog(null,chatpanel,MUCRoomWidget.this);
				joinDialog.center();
			}
		});
		refreshButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				if(Session.instance().isDisconnected())
					return;
				reloadList();
			}
		});
	}
	
	public void setConnected(boolean b)
	{
		connected = b;
		if(b == false)
		{
			//listWidget.setVisible(false);
			//toolBar.setVisible(false);
			disText.setInnerText(i18n.msg("Disconnected"));
			info.setVisible(true);
			listWidget.clear();
			roomLoaded = false;
		}
		else
		{
			disText.setInnerText(i18n.msg("Loading room list..."));
		}
	}
	
	public void reloadList()
	{
		roomLoaded = false;
		loadRoomList();
	}
	
	public void loadRoomList()
	{
		if(!connected||roomLoaded)
			return;
		roomLoaded = true;
		MucRoomPlugin plugin = Session.instance().getMucRoomPlugin();
		disText.setInnerText(i18n.msg("Loading room list..."));
		if(roomListener == null)
		{
			roomListener = new MucRoomListener()
			{
				public void onRoomListUpdate(List<MucRoomItem> rooms) 
				{
					listWidget.clear();
					if(rooms.size()==0)
					{
						disText.setInnerText(i18n.msg("No room exists, you can create a new!"));
					}
					else
					{
						//listWidget.setVisible(true);
						//toolBar.setVisible(true);
						info.setVisible(false);
					}
					for(int index=0;index<rooms.size();index++)
					{
						MucRoomItem roomItem = rooms.get(index);
						MUCRoomItemUI uiItem = new MUCRoomItemUI(roomItem,chatpanel,MUCRoomWidget.this);
						if(index%2==1)
							uiItem.setOddStyle();
						listWidget.add(uiItem);
					}
				}
			};
		}
		plugin.addListener(roomListener);
		plugin.getMucRoomList(iJab.conf.getXmppConf().getMUCServernode());
	}

}
