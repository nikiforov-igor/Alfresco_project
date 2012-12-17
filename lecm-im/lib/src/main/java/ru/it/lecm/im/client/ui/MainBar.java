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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.bubling.BubblingHelper;
import ru.it.lecm.im.client.bubling.IToggleWindow;
import ru.it.lecm.im.client.data.LinkItemImpl;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.ui.abstraction.BarMainWidgetAbstract;
import ru.it.lecm.im.client.ui.abstraction.IBarMainWidget;
import ru.it.lecm.im.client.ui.abstraction.IMainBar;
import ru.it.lecm.im.client.utils.BrowserHelper;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.Session;

public class MainBar extends Composite implements IMainBar {

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
	
	boolean connected = false;
	
	@UiFactory ChatPanelBar makeChatPanelBar()
	{
		return new ChatPanelBar(this);
	}
	
	//buttons
	final private BarButton optionsButton;
	//final private BarButton msgBoxButton;
	final private BarButton buddysButton;
	private BarButton mucButton;
	final private BarButton toolButton;
	final private ContactView contactView;
	final private OptionWidget optionWidget;
	final private BarMenu toolMenu;
	final private BarMainWidgetAbstract mainWidget;
	private MUCRoomWidget mucWidget;
	
	private BarButtonManager btnManager = new BarButtonManager();
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
			buddysButton.setButtonWidthEm(19);
		//msgBoxButton = btnManager.createIconButton("Message Box", "ijab-icon-notification");
		optionsButton = btnManager.createIconButton(i18n.msg("Options"), "ijab-icon-config");
		appsBar.addWidget(buddysButton);

        //BubblingHelper.SubscribeToToggle(buddysButton);
        //buddysButton.ToggleWindow();

        BubblingHelper.Subscribe( new IToggleWindow() {
            public void ToggleWindow(boolean result) {
                buddysButton.ToggleWindow();
            }
        });

//        JavaScriptObject obj = BubblingHelper.callbackFunc();
//        BubblingHelper.on(obj);
		
		//create the muc button
		if(iJab.conf.getXmppConf().isMUCEnabled())
		{
			mucButton = btnManager.createIconButton(i18n.msg("MUC"), "ijab-icon-muc");
			//mucButton.addButtonStyle("ijab-muc-button");
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
		//end create the muc button
		if(!iJab.conf.disableOptionsSetting())
			appsBar.addWidget(optionsButton);

		
		optionWidget = new OptionWidget();
		optionsButton.setButtonWindow(optionWidget);
		
		mainWidget = new BarMainWidget();

		contactView = mainWidget.getContactView();
		buddysButton.setButtonWindow(mainWidget);
		if(!iJab.conf.isRosterManageEnabled())
			mainWidget.removeToolBar();
		else
		{
			mainWidget.setToolBarListener(new IBarMainWidget.IToolBarListener()
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
		
		toolButton = btnManager.createCaptionButton(i18n.msg("Tools"), "ijab-icon-home", "");
		toolButton.addButtonStyle("ijab-toolbox-button");
		toolMenu = new BarMenu();
		toolButton.setButtonWindow(toolMenu);
		if(!iJab.conf.disableToolBox())
			shortcutBar.addWidget(toolButton);
		
		mainWidget.getSearchWidget().addListener(contactView.getSearchListener());
		
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
		readShortItems();
		readTools();
		disconnected();
		
		if(iJab.conf.isBarExpandDefault())
		{
			setBarExpand(true);
			collapseButton.setExpand(true);
		}


		
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
	
	@Override
    public void addShortcutItem(final String url, final String target, final String tipStr, final String icon)
	{
		shortcutBar.addShortcutItem(url,target,tipStr,icon);
	}
	
	@Override
    public void addShortcutItem(final String url, final String tipStr, final String icon)
	{
		shortcutBar.addShortcutItem(url,tipStr,icon);
	}
	
	@Override
    public ShortcutBar getShortcutBar()
	{
		return shortcutBar;
	}
	
	@Override
    public AppsBar getAppsBar()
	{
		return appsBar;
	}
	
	@Override
    public ContactView getContactView()
	{
		return contactView;
	}
	
	@Override
    public SearchBox getSearchWidget()
	{
		return mainWidget.getSearchWidget();
	}
	
	@Override
    public UserIndicator getIndictorWidget()
	{
		return mainWidget.getIndictorWidget();
	}
	
	@Override
    public OptionWidget getConfigWidget()
	{
		return optionWidget;
	}
	
	@Override
    public ChatPanelBar getChatPanel()
	{
		return chatpanelBar;
	}
	
	@Override
    public void updateOnlineCount(int online)
	{
		onlineCount = online;
		if(connected)
			buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
	}
	
	@Override
    public void updateContactCount(int total)
	{
		//totalCount = total;
		if(connected)
			buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
	}
	
	@Override
    public void reset()
	{
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
	
	@Override
    public void connecting()
	{
		connected = false;
		buddysButton.removeIconStyle("ijab-icon-buddy");
		buddysButton.removeIconStyle("ijab-icon-buddy-disconnected");
		buddysButton.setIconStyle("ijab-icon-buddy-connecting");
		buddysButton.setButtonText(i18n.msg("Loading..."));
		mainWidget.setDisconnected(false);
	}
	
	@Override
    public void disconnected()
	{
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
	
	@Override
    public void connected()
	{
		connected = true;
		buddysButton.removeIconStyle("ijab-icon-buddy-connecting");
		buddysButton.removeIconStyle("ijab-icon-buddy-disconnected");
		buddysButton.setIconStyle("ijab-icon-buddy");
		buddysButton.setButtonText(i18n.msg("Chat")+"("+onlineCount+")");
		mainWidget.setDisconnected(false);
		if(mucWidget!=null)
			mucWidget.setConnected(true);
	}
}
