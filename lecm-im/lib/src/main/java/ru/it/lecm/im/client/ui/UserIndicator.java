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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.listeners.XmppProfileListener;
import ru.it.lecm.im.client.ui.listeners.IndicatorListener;
import ru.it.lecm.im.client.ui.listeners.StatusMenuListener;
import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;

public class UserIndicator extends FlexTable
{
	private StatusSelector statusSelector = new StatusSelector();
	//private Image statusImg;
	private Image avatarImg = new Image(GWT.getModuleBaseURL()+"images/alf_chat_userpic_32.png");
	//private Label nickName = new Label("");
	private Label statusLabel = new Label();
	private TextBox statusEditor = new TextBox();
	private String STATUS_TIP = i18n.msg("Введите здесь свой статус.");
	
	private StatusMenu statusMenu;
	private IndicatorListener listener;
	final XmppProfileListener profileListener;
	public UserIndicator()
	{
		setWidth("100%");
		setCellPadding(0);
	    setCellSpacing(0);
	    setStyleName("ijab-indicator");
	    
	    FlexCellFormatter formatter = getFlexCellFormatter();
	    setTitleWidget(null);
	    formatter.setStyleName(0, 0, "ijab-indicator-title");

		avatarImg.setStyleName("ijab-self-avatar ui-corner-all");

		avatarImg.addErrorHandler(new ErrorHandler()
		{
			public void onError(ErrorEvent event) 
			{
				avatarImg.setUrl(GWT.getModuleBaseURL()+"images/alf_chat_userpic_32.png");
			}
		});

        //nickName.setDirection(Direction.LTR);
		//nickName.setStyleName("ijab-self-nick");

        statusSelector.setUrl(XmppStatus.statusIconFromStatus(XmppStatus.Status.STATUS_ONLINE));
		
		final ContextMenuUI menu = new ContextMenuUI();
		statusMenu = new StatusMenu(menu);
		statusMenu.setListener(new StatusMenuListener()
		{
			public void onSetXmppStatus(XmppStatus.Status status)
			{
				statusSelector.setUrl(XmppStatus.statusIconFromStatus(status));
				listener.onXmppStatusChange(status);
			}
			
		});
		menu.setWidget(statusMenu);
		
		statusSelector.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				/*
				Widget source = (Widget) event.getSource();
				int left = source.getAbsoluteLeft()-simplePopup.getOffsetWidth()-50;
				int top = source.getAbsoluteTop()+source.getOffsetHeight();
				simplePopup.setPopupPosition(left, top);
				simplePopup.show();
				*/
				menu.showRelativeTo(statusSelector);
			}
		});

        statusLabel.setStyleName("ijab-self-status");
		statusLabel.setStylePrimaryName("ijab-self-status");
		statusLabel.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) {
				statusLabel.addStyleDependentName("hover");
				statusLabel.addStyleName("ui-corner-all");
			}
		});
		statusLabel.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				statusLabel.removeStyleDependentName("hover");
				statusLabel.removeStyleName("ui-corner-all");
			}
		});

        statusEditor.setVisible(false);
		statusEditor.setStyleName("ijab-self-status-editor");
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setWidth("100%");

		VerticalPanel statusPanel = new VerticalPanel();
		statusPanel.setWidth("100%");

		FlowPanel hNameStatusIconPanel = new FlowPanel();
        hNameStatusIconPanel.add(statusSelector);
        //hNameStatusIconPanel.add(nickName);
        hNameStatusIconPanel.setStyleName("status-and-name");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		hPanel.setSpacing(2);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		hPanel.add(statusLabel);
		hPanel.add(statusEditor);
		//hPanel.add(statusButton);

		statusPanel.add(hNameStatusIconPanel);
		statusPanel.add(hPanel);

		//titlePanel.add(statusImg);
		titlePanel.add(avatarImg);
		titlePanel.add(statusPanel);
		

		titlePanel.setCellWidth(statusSelector, "20px");
		titlePanel.setCellWidth(statusPanel, "100%");
		titlePanel.setCellWidth(avatarImg, "36px");
		setTitleWidget(titlePanel);
		
		statusLabel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				statusLabel.setVisible(false);
				statusEditor.setVisible(true);
				if(statusLabel.getText().equals(STATUS_TIP))
					statusEditor.setText("");
				else
				{
					statusEditor.setText(statusLabel.getText());
					statusEditor.setCursorPos(statusLabel.getText().length());
				}
				statusEditor.setFocus(true);
			}
			
		});
		
		statusEditor.addKeyUpHandler(new KeyUpHandler()
		{
			public void onKeyUp(KeyUpEvent event) 
			{
				if(event.getNativeKeyCode() == 13)
					doneChangeStatusString();
				else if(event.getNativeKeyCode() == 27)
				{
					statusEditor.setFocus(false);
				}
			}
			
		});
		
		statusEditor.addBlurHandler(new BlurHandler()
		{
			public void onBlur(BlurEvent event) 
			{
				statusLabel.setVisible(true);
				statusEditor.setVisible(false);
			}
		});
		
		setStatusText("");
		
		profileListener=new XmppProfileListener()
		{
			public void onNameChange(String name) 
			{
				setNickName(name);
			}

			public void onPresenceChange(Presence item) 
			{
			}
		};
	}
	
	public void setStatusText(final String statusText)
	{
		if(statusText==null||statusText.length() ==0)
			statusLabel.setText(STATUS_TIP);
		else
			statusLabel.setText(statusText);
			
	}
	
	public String getStatusText()
	{
		return statusLabel.getText();
	}
	public void setNickName(final String nick)
	{
        if (nick != null)
        {
            //nickName.setText(nick);
        }
	}
	
	public void setListener(IndicatorListener l)
	{
		this.listener = l;
	}
	
	public void setOption(String name,String statusText,XmppStatus.Status status)
	{
		setNickName(name);
		setStatusText(statusText);
		statusMenu.setStatus(status);
		statusSelector.setUrl(XmppStatus.statusIconFromStatus(status));
		avatarImg.setUrl(XmppProfileManager.getAvatarUrl(Session.instance().getUser().getStringBareJid()));
	}
	
	public XmppProfileListener getProfileListener()
	{
		return profileListener;
	}

	private void doneChangeStatusString()
	{
		String newStatus = statusEditor.getText();
		newStatus = newStatus==null?"":newStatus;
		if(!newStatus.equals(statusLabel.getText()))
		{
			listener.onStatusTextChange(newStatus);
		}
		setStatusText(newStatus);
		statusLabel.setVisible(true);
		statusEditor.setVisible(false);
	}

	private void setTitleWidget(Widget title) 
	{
		setWidget(1, 0, title);
	}

}
