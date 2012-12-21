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
package ru.it.lecm.im.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.data.iJabConf;
import ru.it.lecm.im.client.data.iJabConfImpl;
import ru.it.lecm.im.client.XmppClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class iJab implements EntryPoint {
	/**
	 * This is the entry point method.
	 */

	static String theme = "standard";

	public static iJabUI ui;
	public static iJabConf conf ;
	public static Client client;
	@Override
	public void onModuleLoad()
	{
        conf = iJabConfImpl.getConf();
		if(conf == null)
		{
			Window.alert("Please make conf of iJab!");
			return;
		}

		theme = conf.getTheme();
		ui = createUI();
		client = createClient();
		chageTheme(theme,"");
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{
				defineBridgeMethod(client);
				client.run();
			}
		});
	}

	private iJabUI createUI()
	{
		if(conf.getAppType().equals(iJabConf.AppType.bar))
			return (new BarUI());
		else
			//return (new FullUI());
            return null;
	}

	private Client createClient()
	{
		if(conf.getClientType().equals(iJabConf.ClientType.xmpp))
			return new XmppClient();
		else
			//return new CometdClient();
            return null;
	}



	private void chageTheme(final String newThem,final String oldThem)
	{
		String oldThemeStyle = oldThem + "/iJab.css";
		String newThemStyle = newThem + "/iJab.css";
		boolean styleSheetsFound = false;

		final HeadElement headElem = StyleSheetLoader.getHeadElement();
		final List<Element> toRemove = new ArrayList<Element>();
		NodeList<Node> children = headElem.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node node = children.getItem(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element elem = Element.as(node);
				if (elem.getTagName().equalsIgnoreCase("link")
						&& elem.getPropertyString("rel").equalsIgnoreCase("stylesheet"))
				{
					String href = elem.getPropertyString("href");
					if(href.contains(newThemStyle))
					{
						styleSheetsFound = true;
					}
					if (href.contains(oldThemeStyle))
					{
						toRemove.add(elem);
					}
				}
			}
		}

		if (styleSheetsFound && toRemove.size() == 0) {
			return;
		}

		// Detach the app while we manipulate the styles to avoid rendering issues
		for(Widget w:ui.getTopWidgets())
		{
			RootPanel.get().remove(w);
		}

		for (Element elem : toRemove) {
			headElem.removeChild(elem);
		}

		String modulePath = GWT.getModuleBaseURL();
		Command callback = new Command() {
			/**
			 * The number of style sheets that have been loaded and executed this
			 * command.
			 */
			private int numStyleSheetsLoaded = 0;

			public void execute() {
				// Wait until all style sheets have loaded before re-attaching the app
				numStyleSheetsLoaded++;
				if (numStyleSheetsLoaded < 1) {
					return;
				}

				RootPanel.getBodyElement().getStyle().setProperty("display", "none");
				RootPanel.getBodyElement().getStyle().setProperty("display", "");
				for(Widget w:ui.getTopWidgets())
				{
					RootPanel.get().add(w);
				}
			}
		};

		StyleSheetLoader.loadStyleSheet(modulePath + newThemStyle,
				getCurrentReferenceStyleName("ijab"), callback);
	}

	/**
	 * Get the style name of the reference element defined in the current GWT
	 * theme style sheet.
	 *
	 * @param prefix the prefix of the reference style name
	 * @return the style name
	 */
	private String getCurrentReferenceStyleName(String prefix) {
		String gwtRef = prefix + "-Reference-" + theme;
		return gwtRef;
	}

	private native void defineBridgeMethod(Client client)
	/*-{
		$wnd.iJab =
		{
			login:function(id,password)
			{
				client.@ru.it.lecm.im.client.Client::login(Ljava/lang/String;Ljava/lang/String;)(id,password);
			},

			loginWithStatus:function(id,password,status)
			{
                client.@ru.it.lecm.im.client.Client::loginWithStatus(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id,password,status);
			},

			logout:function()
			{
                client.@ru.it.lecm.im.client.Client::logout()();
			},

			addListener:function(listener)
			{
                client.@ru.it.lecm.im.client.Client::addNativeListener(Lcom/google/gwt/core/client/JavaScriptObject;)(listener);
			},

			talkTo:function(jid)
			{
                client.@ru.it.lecm.im.client.Client::talkTo(Ljava/lang/String;)(jid);
			},

			isActive:function()
			{
				return client.@ru.it.lecm.im.client.Client::isConnected()();
			},

			version:function()
			{
				return "1.0.0-beta3";
			},

			getNickname:function()
			{
				return client.@ru.it.lecm.im.client.Client::getNickname()();
			},

			getStatusText:function()
			{
				return client.@ru.it.lecm.im.client.Client::getStatusText()();
			},

			setStatusText:function(statusText)
			{
                client.@ru.it.lecm.im.client.Client::setStatusText(Ljava/lang/String;)(statusText);
			},

			getStatus:function()
			{
				return client.@ru.it.lecm.im.client.Client::getStatus()();
			},

			setStatus:function(status)
			{
                client.@ru.it.lecm.im.client.Client::setStatus(Ljava/lang/String;)(status);
			},

			addRoster:function(users,group)
			{
                client.@ru.it.lecm.im.client.Client::addRoster(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/String;)(users,group);
			},

			removeRoster:function(users)
			{
                client.@ru.it.lecm.im.client.Client::removeRoster(Lcom/google/gwt/core/client/JsArrayString;)(users);
			},

			joinMUC:function(room,nick)
			{
                client.@ru.it.lecm.im.client.Client::joinMUC(Ljava/lang/String;Ljava/lang/String;)(room,nick);
			},

			leaveMUC:function(room)
			{
                client.@ru.it.lecm.im.client.Client::leaveMUC(Ljava/lang/String;)(room);
			},

			addToBlackList:function(users)
			{
                client.@ru.it.lecm.im.client.Client::addUsersToBlacklist(Lcom/google/gwt/core/client/JsArrayString;)(users);
			},

			removeUsersFromBlacklist:function(users)
			{
                client.@ru.it.lecm.im.client.Client::removeUsersFromBlacklist(Lcom/google/gwt/core/client/JsArrayString;)(users);
			}
		};
	  }-*/;


}
