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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.utils.i18n;

public abstract class BarChatWidgetUI extends Composite {

	private static BarChatWidgetUIUiBinder uiBinder = GWT
			.create(BarChatWidgetUIUiBinder.class);

	interface BarChatWidgetUIUiBinder extends UiBinder<Widget, BarChatWidgetUI> {
	}
	
	@UiField Element picElement;
	@UiField Image imgAvatar;
	@UiField Element userStatusElement;
	
	@UiField Element noticeElement;
	
	@UiField FlowPanel chatContent;
	@UiField HTMLPanel chatStatusPanel;	
	
	@UiField Element emotElement;
	@UiField Element cleanElement;
	@UiField Element historyElement;
	
	@UiField TextArea msgInput;
	@UiField EmoticonsPanel emotionsPanel;
	
	private SimpleFocusWidget emotButton;
	private SimpleFocusWidget cleanButton;
	private SimpleFocusWidget historyButton;
	private Timer noticeTimer = null;
	
	private final String inputPrompt;

	public BarChatWidgetUI() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		emotElement.setAttribute("title", i18n.msg("Emotions"));
		cleanElement.setAttribute("title", i18n.msg("Clean"));
		historyElement.setAttribute("title", i18n.msg("History"));
		inputPrompt = i18n.msg("Input message here and press Enter to send");
		msgInput.setText(inputPrompt);
		
		emotButton = new SimpleFocusWidget(emotElement);
		emotButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				showEmotionWidget();
			}
		});
		
		cleanButton = new SimpleFocusWidget(cleanElement);
		cleanButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				cleanHistory();
			}
		});
		
		historyButton = new SimpleFocusWidget(historyElement);
		historyButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				viewHistory();
			}
			
		});
		
		msgInput.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event) 
			{
				if(event.getCharCode() == 13)
				{
					msgInput.cancelKey();
					final String msg = msgInput.getText();
					if(msg == null||msg.length() == 0||msg.equals(inputPrompt))
						return;
					msgInput.setText("");
					msgInput.setFocus(true);
					send(msg);
				}
				else
					onTyping();
			}	
		});
		
		msgInput.addFocusHandler(new FocusHandler(){
			public void onFocus(FocusEvent event) {
				final String text = msgInput.getText();
				if(text!=null&&text.equalsIgnoreCase(inputPrompt))
					msgInput.setText("");
				msgInput.removeStyleName("ijab-gray");
			}
		});
		
		msgInput.addBlurHandler(new BlurHandler()
		{
			public void onBlur(BlurEvent event) 
			{
				final String text = msgInput.getText();
				if(text == null||text.length() == 0)
				{
					msgInput.setText(inputPrompt);
					msgInput.addStyleName("ijab-gray");
				}
			}
		});
		imgAvatar.setUrl(GWT.getModuleBaseURL()+"images/default_avatar.png");
		imgAvatar.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				userAvatarClicked(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
			}
			
		});
		imgAvatar.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) {
				userAvatarMouseOver(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
			}
		});
		imgAvatar.addErrorHandler(new ErrorHandler()
		{
			public void onError(ErrorEvent event) 
			{
				imgAvatar.setUrl(GWT.getModuleBaseURL()+"images/default_avatar.png");
			}
		});
		
		emotionsPanel.setListener(new EmoticonsPanelListener()
		{
			public void emotSelect(String text) 
			{
				String orgMsg = msgInput.getText();
				if(orgMsg == null||orgMsg.equals(inputPrompt))
					orgMsg = "";
				orgMsg = orgMsg+" "+text;
				msgInput.setText(orgMsg);
				msgInput.setFocus(true);
				msgInput.setCursorPos(orgMsg.length());
				emotionsPanel.setVisible(false);
			}
		}
		);
	}
	
	public void setUserAvatar(final String url)
	{
		imgAvatar.setUrl(url);
	}
	
	public void setUserStatus(final String status)
	{
		userStatusElement.setInnerText(status);
	}
	
	public void setNotice(final String notice)
	{
		noticeElement.getStyle().setDisplay(Display.BLOCK);
		noticeElement.setInnerText(notice);
		if(noticeTimer != null)
		{
			noticeTimer.cancel();
			noticeTimer.schedule(10000);
		}
		noticeTimer = new Timer()
		{
			@Override
			public void run() 
			{
				noticeElement.getStyle().setDisplay(Display.NONE);
				noticeElement.setInnerText("");
				noticeTimer = null;
			}
		};
		noticeTimer.schedule(10000);
	}
	
	public void setChatStatusText(final String status)
	{
		chatStatusPanel.getElement().setInnerText(status);
	}
	
	public void cleanHistory()
	{
		chatContent.clear();
	}
	
	public void scrollHistoryToBottom()
	{
		try
		{
			chatContent.getWidget(chatContent.getWidgetCount()-1).getElement().scrollIntoView();
			//chatContent.getElement().scrollIntoView();
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void focusToInput()
	{
		msgInput.setFocus(true);
	}
	
	
	protected abstract void userAvatarClicked(int clientX,int clientY);
	protected abstract void userAvatarMouseOver(int clientX,int clientY);
	protected abstract void send(final String msg);
	protected abstract void onTyping();
	protected abstract void viewHistory();
	
	protected void addMessage(HTML message)
	{
		chatContent.add(message);
		scrollHistoryToBottom();
	}
	
	private void showEmotionWidget()
	{
		emotionsPanel.setVisible(!emotionsPanel.isVisible());
	}
}
