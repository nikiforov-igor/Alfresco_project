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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.data.LinkItemImpl;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.BrowserHelper;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;

public class MainBar extends Composite implements  HasVisibility, EventListener, HasAttachHandlers, IsWidget, IsRenderable {

	private static MainBarUiBinder uiBinder = GWT.create(MainBarUiBinder.class);

	interface MainBarUiBinder extends UiBinder<Widget, MainBar> {
	}

	@UiField ShortcutBar shortcutBar;
	@UiField ChatPanelBar chatpanelBar;
	@UiField AppsBar appsBar;
	@UiField HTMLPanel ijab;
	@UiField Element ijabLayout;
	@UiField CollapseButton collapseButton;
	@UiField Element ijab_ui;
	@UiField Element ijab_layout_r;
	
	@UiFactory ChatPanelBar makeChatPanelBar()
	{
		return new ChatPanelBar(this);
	}
	
	//buttons
	final private BarButton optionsButton;
	//final private BarButton msgBoxButton;
	final private BarButton buddysButton;
	final private BarButton mucButton;
	final private BarButton toolButton;
	final private ContactView contactView;


	final private MUCRoomWidget mucWidget;

    final private BarMainWidget mainWidget = new BarMainWidget();
    final private OptionWidget optionWidget = new OptionWidget();
	final private BarButtonManager btnManager = new BarButtonManager();
    final private BarMenu toolMenu = new BarMenu();

    boolean connected = false;

	private int onlineCount = 0;
	//private int totalCount = 0;
	private boolean getRostered = false;
	
	private AddSearchWnd add_searchWnd = null;
	public MainBar() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		ijab.getElement().setId("ijab");
		//do css hack
		BrowserHelper.init();
		if(BrowserHelper.isIE)
		{
			String cssMark = "";
			cssMark += "ijab-ie ";
			if(BrowserHelper.isIE6)
				cssMark +="ijab-ie6";
			else if(BrowserHelper.isIE7)
				cssMark += "ijab-ie7";
			else if(BrowserHelper.isIE8)
				cssMark += "ijab-ie8";
			ijab.addStyleName(cssMark);
		}
		
		ijabLayout.setId("ijab-layout");
		
		
		buddysButton = btnManager.createCaptionButton(i18n.msg("Chat"), "ijab-icon-buddy", "ijab-buddy-window");
		buddysButton.addButtonStyle("ijab-buddys-button");
		if(iJab.conf.disableOptionsSetting())
        {
            buddysButton.setButtonWidthEm(19);
        }
		//msgBoxButton = btnManager.createIconButton("Message Box", "ijab-icon-notification");
        appsBar.addWidget(buddysButton);



        //create the muc button
        if(iJab.conf.getXmppConf().isMUCEnabled())
		{
			mucButton = btnManager.createIconButton(i18n.msg("MUC"), "ijab-icon-muc");
			appsBar.addWidget(mucButton);
			mucWidget = new MUCRoomWidget(chatpanelBar);
			mucButton.setButtonWindow(mucWidget);
			mucButton.getButton().addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					mucWidget.loadRoomList();
				}

			});
		}
        else
        {
            mucButton = null;
            mucWidget = null;
        }


		if(!iJab.conf.disableOptionsSetting())
        {
            optionsButton = btnManager.createIconButton(i18n.msg("Options"), "ijab-icon-config");
            appsBar.addWidget(optionsButton);
            optionsButton.setButtonWindow(optionWidget);
        }
        else
        {
            optionsButton = null;
        }

		contactView = mainWidget.getContactView();
		buddysButton.setButtonWindow(mainWidget);

        setupRosterManagement();

        toolButton = btnManager.createCaptionButton(i18n.msg("Tools"), "ijab-icon-home", "");
		toolButton.addButtonStyle("ijab-toolbox-button");

		toolButton.setButtonWindow(toolMenu);
		if(!iJab.conf.disableToolBox())
        {
            shortcutBar.addWidget(toolButton);
        }
		
		mainWidget.getSearchWidget().addListener(contactView.getSearchListener());

        addCollapseButtonClickHandler();
        addBuddysButtonClickHandler();

        readShortItems();
		readTools();
		disconnected();
		
		if(iJab.conf.isBarExpandDefault())
		{
			setBarExpand(true);
			collapseButton.setExpand(true);
		}


		
	}

    private void setupRosterManagement() {
        if(!iJab.conf.isRosterManageEnabled())
			mainWidget.removeToolBar();
		else
		{
			mainWidget.setToolBarListener(new BarMainWidget.IToolBarListener()
			{
				public void addButtonClicked()
				{
					if(add_searchWnd == null)
						add_searchWnd = new AddSearchWnd();
					add_searchWnd.center();
					add_searchWnd.show();
				}
				public void manageButtonClicked() {

				}
			});
		}
    }

    private void addCollapseButtonClickHandler() {
        collapseButton.getWidget().addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                if(ijabLayout.getClassName().contains("ijab-webapi-max"))
                {
                    setBarExpand(false);
                }
                else
                {
                    setBarExpand(true);
                }
            }
        });
    }

    private void addBuddysButtonClickHandler() {
        buddysButton.getButton().addClickHandler(new ClickHandler()
        {

            public void onClick(ClickEvent event)
            {
                if(!iJab.conf.getXmppConf().isNoneRoster()&&iJab.client.isLogined()&&iJab.conf.getXmppConf().isGetRosterDelay()&&!getRostered)
                {
                    Session.instance().getRosterPlugin().getRoster(null);
                    getRostered = true;
                }
                if(iJab.conf.getXmppConf().isAutoLogin()&&!connected)
                {
                    iJab.client.resume();
                }
                else if(iJab.conf.isLoginDialogEnabled()&&!connected)
                    LoginDialog.instance().center();
            }

        });
    }

    private void setBarExpand(boolean b)
	{
		if(b)
		{
			ijab_ui.removeClassName("ijab-left");
			ijabLayout.addClassName("ijab-webapi-max");
			ijab_layout_r.addClassName("ijab-right");
		}
		else
		{
			ijabLayout.removeClassName("ijab-webapi-max");
			ijab_ui.addClassName("ijab-left");
			ijab_layout_r.removeClassName("ijab-right");
		}
	}
	
	private void readShortItems()
	{
		JsArray<LinkItemImpl> array = iJab.conf.getShortcuts();
		for(int index=0;index<array.length();index++)
		{
			LinkItemImpl item = array.get(index);
			addShortcutItem(item.href(),item.target(),item.text(),item.img());
		}
	}
	
	private void readTools()
	{
		JsArray<LinkItemImpl> array = iJab.conf.getTools();
		for(int index=0;index<array.length();index++)
		{
			LinkItemImpl item = array.get(index);
			toolMenu.addItem(item.href(),item.target(),item.img(),item.text());
		}
	}
	
	public void addShortcutItem(final String url, final String target, final String tipStr, final String icon)
	{
		shortcutBar.addShortcutItem(url,target,tipStr,icon);
	}
	
	public void addShortcutItem(final String url, final String tipStr, final String icon)
	{
		shortcutBar.addShortcutItem(url,tipStr,icon);
	}
	
	public ShortcutBar getShortcutBar()
	{
		return shortcutBar;
	}
	
	public AppsBar getAppsBar()
	{
		return appsBar;
	}
	
	public ContactView getContactView()
	{
		return contactView;
	}
	
	public SearchBox getSearchWidget()
	{
		return mainWidget.getSearchWidget();
	}
	
	public UserIndicator getIndictorWidget()
	{
		return mainWidget.getIndictorWidget();
	}
	
	public OptionWidget getConfigWidget()
	{
		return optionWidget;
	}
	
	public ChatPanelBar getChatPanel()
	{
		return chatpanelBar;
	}
	
	public void updateOnlineCount(int online)
	{
		onlineCount = online;
		if(connected)
			buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
	}
	
	public void updateContactCount(int total)
	{
		//totalCount = total;
		if(connected)
			buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
	}
	
	public void reset()
	{
        Log.consoleLog("MainBar.reset()");
		disconnected();
		contactView.clear();
		updateOnlineCount(0);
		updateContactCount(0);
		chatpanelBar.reset();
		optionWidget.reset();
		if(mucWidget!=null)
			mucWidget.setConnected(false);
		disconnected();
	}
	
	public void connecting()
	{
        Log.consoleLog("MainBar.connecting()");
        connected = false;
		buddysButton.removeIconStyle("ijab-icon-buddy");
		buddysButton.removeIconStyle("ijab-icon-buddy-disconnected");
		buddysButton.setIconStyle("ijab-icon-buddy-connecting");
		buddysButton.setButtonText(i18n.msg("Loading..."));
		mainWidget.setDisconnected(false);
	}
	
	public void disconnected()
	{
        Log.consoleLog("MainBar.disconnected()");
        connected = false;
		getRostered = false;
		buddysButton.setIconStyle("ijab-icon-buddy-disconnected");
		buddysButton.removeIconStyle("ijab-icon-buddy");
		buddysButton.removeIconStyle("ijab-icon-buddy-connecting");
		buddysButton.setButtonText(i18n.msg("Chat"));
		mainWidget.setDisconnected(true);
		if(mucWidget!=null)
			mucWidget.setConnected(false);
	}
	
	public void connected()
	{
        Log.consoleLog("MainBar.connected()");
		connected = true;
		buddysButton.removeIconStyle("ijab-icon-buddy-connecting");
		buddysButton.removeIconStyle("ijab-icon-buddy-disconnected");
		buddysButton.setIconStyle("ijab-icon-buddy");
		buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
		mainWidget.setDisconnected(false);
		if(mucWidget!=null)
			mucWidget.setConnected(true);

        buddysButton.openWindow();
	}
}
