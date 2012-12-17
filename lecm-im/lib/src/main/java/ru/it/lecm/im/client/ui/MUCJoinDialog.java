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
 * Mar 31, 2010
 */
package ru.it.lecm.im.client.ui;

import java.util.ArrayList;
import java.util.List;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MucRoomItem;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.MultiUserChatPlugin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.net.XmppProfileManager;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.JID;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCJoinDialog extends DialogBox implements GroupChatListener
{
	private MucRoomItem roomItem = null;
	private TextBox roomName;
	private TextBox nickName;
	private PasswordTextBox password;
	private Button cancelButton;
	private Button okButton;
	final private ChatPanelBar chatpanel;
	private List<GroupChatEvent> presenceCaches = new ArrayList<GroupChatEvent>();
	private MUCRoomWidget roomWidget; 
	public MUCJoinDialog(MucRoomItem roomItem,final ChatPanelBar chatpanel,MUCRoomWidget roomWidget)
	{
		this.chatpanel = chatpanel;
		this.roomItem = roomItem;
		this.roomWidget = roomWidget;
		if(this.roomItem == null)
			setText(i18n.msg("Create Room"));
		else
			setText(i18n.msg("Join GroupChat"));
		setGlassEnabled(false);
		setAnimationEnabled(true);
		setModal(false);
		setWidget(initUI());
		connectEvent();
	}
	
	private void connectEvent()
	{
		cancelButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				hide();
			}
		});
		
		okButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				MultiUserChatPlugin plugin = Session.instance().getMucPlugin();
				if((roomItem==null)&&(roomName.getText()==null||roomName.getText().length()==0))
				{
					Window.alert(i18n.msg("Roomname can't be empty!"));
					return;
				}
				if(nickName.getText()==null||nickName.getText().length()==0)
				{
					Window.alert(i18n.msg("NickName can't be empty!"));
					return;
				}
				String roomJid = roomName.getText()+"@"+ iJab.conf.getXmppConf().getMUCServernode();
				if(roomItem!=null)
					roomJid = roomItem.getJid();
				String nick = nickName.getText();
				GroupChat gc = plugin.createGroupChat(JID.fromString(roomJid), nick, password.getText());
				gc.setRoomName(roomName.getText());
				gc.setUserData(MUCJoinDialog.this);
				gc.addListener(MUCJoinDialog.this);
				gc.join();
			}
		});
	}
	
	private Widget initUI()
	{
	    FlexTable table = new FlexTable();
	    table.setCellSpacing(6);
	    FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
	    
	    table.setHTML(0, 0, i18n.msg("Room")+":");
	    roomName = new TextBox();
	    if(roomItem!=null)
	    {
	    	roomName.setText(roomItem.getName());
	    	roomName.setEnabled(false);
	    }
	    table.setWidget(0, 1, roomName);
	    
	    table.setHTML(1, 0, i18n.msg("Nickname")+":");
	    nickName = new TextBox();
	    nickName.setText(XmppProfileManager.getName(Session.instance().getUser().getStringBareJid()));
	    table.setWidget(1, 1, nickName);
	    
	    table.setHTML(2, 0, i18n.msg("Password:"));
	    password = new PasswordTextBox();
	    table.setWidget(2, 1, password);
	    
	    HorizontalPanel hPanel = new HorizontalPanel();
	    hPanel.setSpacing(5);
	    if(roomItem!=null)
	    	okButton = new Button(i18n.msg("Join"));
	    else
	    	okButton = new Button(i18n.msg("Create"));
	   
	    
	    cancelButton = new Button(i18n.msg("Cancel"));
	    hPanel.add(cancelButton);
	    hPanel.add(okButton);
	    
	    table.setWidget(3, 1, hPanel);
	    cellFormatter.setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		return table;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCJoinDeny(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCJoinDeny(GroupChatEvent gcEvent) 
	{
		gcEvent.getGroupChat().removeListener(this);
		Window.alert(i18n.msg("Join room failed!"));
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCJoined(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCJoined(final GroupChatEvent gcEvent) 
	{
		hide();
		MUCPanelButton mucButton = chatpanel.createMUCButton();
		mucButton.setGroupChat(gcEvent.getGroupChat());
		mucButton.openWindow();
		for(GroupChatEvent event:presenceCaches)
			mucButton.onUserPresenceChange(event);
		if(roomItem == null)
			roomWidget.reloadList();
		Timer delay = new Timer()
		{
			@Override
			public void run() {
				gcEvent.getGroupChat().removeListener(MUCJoinDialog.this);
			}
		};
		delay.schedule(100);
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onGCLeaved(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onGCLeaved(GroupChatEvent gcEvent) 
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onMessage(ru.it.lecm.im.client.xmpp.stanzas.Message)
	 */
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onUserLeaved(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onUserLeaved(GroupChatEvent gcEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatListener#onUserPresenceChange(ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChatEvent)
	 */
	public void onUserPresenceChange(GroupChatEvent gcEvent) 
	{
		presenceCaches.add(gcEvent);
	}
}
