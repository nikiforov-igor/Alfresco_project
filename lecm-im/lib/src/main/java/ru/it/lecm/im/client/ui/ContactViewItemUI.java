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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContactViewItemUI extends Composite {

	private static ContactViewItemUIUiBinder uiBinder = GWT
			.create(ContactViewItemUIUiBinder.class);

	interface ContactViewItemUIUiBinder extends
			UiBinder<Widget, ContactViewItemUI> {
	}

//	@UiField Element avatar;
//	@UiField Image avatarImg;
	@UiField Element nameElement;
	@UiField Element nameTextElement;
	@UiField Element msgCounterTextElement;
	@UiField Element statusIconElement;
	@UiField Element statusTextElement;
	@UiField FocusHTMLPanel mainWidget;
	@UiField TextBox nameEditor;
	
	public interface NameEditListener
	{
		void onNameChange(final String name);
	}
	
	private NameEditListener editListener = null;
	
	public ContactViewItemUI(final String id)
	{
		initWidget(uiBinder.createAndBindUi(this));
		nameEditor.setVisible(false);
		mainWidget.getElement().setId(id);
		mainWidget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				onItemClicked();
			}
		});
		
//		mainWidget.addContextMenuHandler(new ContextMenuHandler()
//		{
//			public void onContextMenu(ContextMenuEvent event)
//			{
//				event.stopPropagation();
//				onConextMenu(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
//			}
//
//		});
		
//		mainWidget.addMouseOutHandler(new MouseOutHandler()
//		{
//			public void onMouseOut(MouseOutEvent event)
//			{
//				ContactViewItemUI.this.onMouseOut();
//			}
//		});
//
//		mainWidget.addMouseOverHandler(new MouseOverHandler()
//		{
//			public void onMouseOver(MouseOverEvent event)
//			{
//				ContactViewItemUI.this.onMouseOver();
//			}
//		});
//
//		avatarImg.addClickHandler(new ClickHandler()
//		{
//			public void onClick(ClickEvent event)
//			{
//				onAvatarClicked(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
//				event.stopPropagation();
//			}
//
//		});
//
//		avatarImg.addMouseOverHandler(new MouseOverHandler()
//		{
//			public void onMouseOver(MouseOverEvent event)
//			{
//				onAvatarOver(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
//			}
//		});
//
//		avatarImg.addMouseOutHandler(new MouseOutHandler()
//		{
//			public void onMouseOut(MouseOutEvent event) {
//				onAvatarOut(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
//			}
//		});
//
//		avatarImg.addErrorHandler(new ErrorHandler(){
//			public void onError(ErrorEvent event) {
//				avatarImg.setUrl(GWT.getModuleBaseURL()+"images/alf_chat_userpic_25.png");
//			}
//		});
//		avatarImg.setUrl(GWT.getModuleBaseURL()+"images/alf_chat_userpic_25.png");
		
//		nameEditor.addKeyUpHandler(new KeyUpHandler()
//		{
//			public void onKeyUp(KeyUpEvent event)
//			{
//				if(event.getNativeKeyCode() == 13)
//				{
//					String newName = nameEditor.getValue();
//					newName = newName==null?"":newName;
//					newName= newName.equals("") ?nameTextElement.getInnerText():newName;
//					if(editListener !=null &&!newName.equals(nameTextElement.getInnerText()))
//					{
//						editListener.onNameChange(nameEditor.getValue());
//						nameTextElement.setInnerText(newName);
//					}
//					nameEditor.setVisible(false);
//					nameTextElement.getStyle().setDisplay(Display.BLOCK);
//					editListener = null;
//
//				}
//				else if(event.getNativeKeyCode() == 27)
//				{
//					nameEditor.setFocus(false);
//				}
//			}
//
//		});
//
//		nameEditor.addBlurHandler(new BlurHandler()
//		{
//			public void onBlur(BlurEvent event)
//			{
//				nameEditor.setVisible(false);
//				nameTextElement.getStyle().setDisplay(Display.BLOCK);
//				editListener = null;
//			}
//		});
	}
	
	public String getWidgetID()
	{
		return mainWidget.getElement().getId();
	}
	
	public void setName(final String name)
	{
		nameTextElement.setInnerText(name);
	}
	
	public void setTitle(final String title)
	{
		mainWidget.setTitle(title);
		nameElement.setTitle(title);
	}
	
	public void setItemOdd(boolean b)
	{
		if(b)
			mainWidget.addStyleName("ijab-contactview-item-odd");
		else
			mainWidget.removeStyleName("ijab-contactview-item-odd");
	}
	
	public void setStatusText(final String status)
	{
		if(status == null||status.length()==0)
			nameElement.addClassName("names_nostatus");
		else
			nameElement.removeClassName("names_nostatus");
		statusTextElement.setInnerText(status);
	}
	
	public void setAvatar(final String url)
	{
//        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
//            @Override
//            public void execute() {
//                avatarImg.setUrl(url);
//            }
//        });


	}
	
	public void setStatusIcon(final String url)
	{
		statusIconElement.setAttribute("src", url);
	}
	
	public void setOffline(boolean b)
	{
//		if(b)
//		{
//			avatarImg.addStyleName("ijab-offline");
//		}
//		else
//			avatarImg.removeStyleName("ijab-offline");
	}

	protected abstract void onConextMenu(int x,int y);
	protected abstract void onItemClicked();
	
	private void onMouseOver()
	{
		mainWidget.addStyleName("ijab-contactview-item-hover");
		mainWidget.removeStyleName("ijab-contactview-item-normal");
	}
	
	private void onMouseOut()
	{
		mainWidget.addStyleName("ijab-contactview-item-normal");
		mainWidget.removeStyleName("ijab-contactview-item-hover");
	}
	
	public void setHighlight()
	{
		mainWidget.addStyleName("ijab-contactview-item-highlight");
	}
	
	public void removeHighlight()
	{
		mainWidget.removeStyleName("ijab-contactview-item-highlight");
	}
	
	public void onNameEdit(NameEditListener listener)
	{
		nameEditor.setVisible(true);
		nameEditor.setText(nameTextElement.getInnerText());
		nameTextElement.getStyle().setDisplay(Display.NONE);
		editListener = listener;
		nameEditor.setFocus(true);
	}


}
