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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.data.GatewayItemImpl;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.i18n;

public class BarMainWidget extends Composite implements HasVisibility, EventListener, HasAttachHandlers, IsWidget, IsRenderable {

    private static BarMainWidgetUiBinder uiBinder = GWT
			.create(BarMainWidgetUiBinder.class);

	interface BarMainWidgetUiBinder extends UiBinder<Widget, BarMainWidget> {
	}

	@UiField ContactView contactView;
	@UiField UserIndicator indicator;
	@UiField HTMLPanel disconnected;
	@UiField SearchBox searchBox;
	@UiField Element disText;
	@UiField FlowPanel mainWidget;
//	@UiField FlowPanel toolBar;
//	@UiField Anchor addButton;
//	@UiField Anchor gatewayButton;
//	@UiField Element addButtonText;
//	@UiField Element gatewayButtonText;
	
	//private ContextMenu gatewayMenu = null;
	
	private IToolBarListener listener = null;
	public BarMainWidget() 
	{
		initWidget(BarMainWidget.uiBinder.createAndBindUi(this));
		disText.setInnerText(i18n.msg("Disconnected"));
//        addButton.setTitle(i18n.msg("Search/Add Friend"));
//        addButtonText.setInnerText(i18n.msg("Search/Add"));
//        gatewayButton.setTitle(i18n.msg("Manage gateway"));
//        gatewayButtonText.setInnerText(i18n.msg("Gateway"));
//
//		addButton.addClickHandler(new ClickHandler()
//		{
//			public void onClick(ClickEvent event)
//			{
//				if(listener!=null)
//					listener.addButtonClicked();
//			}
//		});
//
//		gatewayButton.addClickHandler(new ClickHandler()
//		{
//			public void onClick(ClickEvent event)
//			{
//				if(iJab.client instanceof XmppClient)
//				{
//					if(iJab.conf.getXmppConf().getGateways().length()>0) {
//                        popGateMenu();
//                    }
//				}
//			}
//		});
	}
	
	protected void popGateMenu()
	{

//		if(gatewayMenu == null)
//		{
//			gatewayMenu = new ContextMenu();
//		}
//
//		gatewayMenu.clear();
//		MenuBar gatewaysMenu = new MenuBar(true);
//		gatewaysMenu.setStylePrimaryName("ijab-contextmenu-body");
//		gatewaysMenu.setAutoOpen(true);
//		JsArray<GatewayItemImpl> gateways = iJab.conf.getXmppConf().getGateways();
//		for(int index=0;index<gateways.length();index++)
//		{
//			final GatewayPlugin gatePlugin = Session.instance().getGatewayPlugin();
//			final GatewayItemImpl gateway = gateways.get(index);
//			ContextMenuItem menuItem = new ContextMenuItem(gatewayMenu,gateway.name(),true,new ContextMenuItemListener()
//			{
//				public void onSelected(Object data)
//				{
//					gatePlugin.getInfo(gateway.servernode(), new ResponseHandler()
//					{
//						public void onError(IQ iq, ErrorType errorType,
//								ErrorCondition errorCondition, String text) {
//						}
//
//						public void onResult(IQ iq)
//						{
//							GatewayDialog dialog = new GatewayDialog(iq);
//							dialog.center();
//						}
//
//					});
//				}
//			});
//			gatewaysMenu.addItem(menuItem);
//		}
//		gatewayMenu.addItem(i18n.msg("Gateways"), gatewaysMenu);
//		MenuBar registeredMenuBar = new MenuBar(true);
//		registeredMenuBar.setStylePrimaryName("ijab-contextmenu-body");
//		registeredMenuBar.setAutoOpen(true);
//		List<String> services = new ArrayList<String>();
//		services.addAll(Session.instance().getRosterPlugin().getTransports());
//		for(final String service:services)
//		{
//			if(service.equals(Session.instance().getDomainName()))
//				continue;
//			ContextMenuItem menuItem = new ContextMenuItem(gatewayMenu,i18n.msg("Remove")+" "+getGatewayName(service),true,new ContextMenuItemListener()
//			{
//
//				public void onSelected(Object data)
//				{
//					Session.instance().getRosterPlugin().removeRosterItem(JID.fromString(service));
//				}
//
//			});
//			registeredMenuBar.addItem(menuItem);
//		}
//		gatewayMenu.addItem(i18n.msg("Registered"), registeredMenuBar);
//		gatewayMenu.showRelativeTo(gatewayButton);
	}
	
	protected String getGatewayName(String jid)
	{
		JsArray<GatewayItemImpl> gateways = iJab.conf.getXmppConf().getGateways();
		for(int index=0;index<gateways.length();index++)
		{
			final GatewayItemImpl gateway = gateways.get(index);
			if(gateway.servernode().equalsIgnoreCase(jid))
				return gateway.servernode();
		}
		return jid;
	}
	
	public ContactView getContactView()
	{
		return contactView;
	}
	
	public UserIndicator getIndictorWidget()
	{
		return indicator;
	}
	
	public SearchBox getSearchWidget()
	{
		return searchBox;
	}
	
	public void setDisconnected(boolean b)
	{
		if(b)
		{
			disconnected.setVisible(true);
			contactView.setVisible(false);
			indicator.setVisible(false);
			searchBox.setVisible(false);
//			if(toolBar!=null)
//				toolBar.setVisible(false);
		}
		else
		{
			disconnected.setVisible(false);
			contactView.setVisible(true);
			indicator.setVisible(true);
			searchBox.setVisible(true);
//			if(toolBar!=null)
//				toolBar.setVisible(true);
		}
	}
	
	public void removeToolBar()
	{
//		mainWidget.remove(toolBar);
//		toolBar = null;
	}
	
	public void setToolBarListener(IToolBarListener listener)
	{
		this.listener = listener;
	}

    public interface IToolBarListener
    {
        void addButtonClicked();
        void manageButtonClicked();
    }
}
