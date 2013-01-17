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
 * Mar 16, 2010
 */
package ru.it.lecm.im.client.ui;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch.Field;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch.JabberSearchPlugin;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch.JabberSearchResponseHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.PopupPrompt;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class AddSearchWnd extends DialogBox
{
	//for add tab
	private ListBox serviceList;
	private List<String> services;
	private Widget translationWidget;
	private TextBox translationTextBox;
	private Button translationButton;
	private TextBox jabberIDTextBox;
	private TextBox nickTextBox;
	private ListBox groupListBox;
	private Button addButton;
	
	//for vcard search
	private List<Field> searchFields;
	
	private Button searchButton;
	
	private VCardSearchList searchList;
	private FlexTable filedsWidget;
	public AddSearchWnd()
	{
		setText(i18n.msg("Add/Search Friends"));
		setGlassEnabled(false);
		setAnimationEnabled(true);
		this.addStyleName("ijab-searchwnd");
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		DecoratedTabPanel tabPanel = new DecoratedTabPanel();
	    tabPanel.setAnimationEnabled(true);
	    tabPanel.addStyleName("ijab-searchwnd_tabpanel");
	    vPanel.add(tabPanel);
	    
	    //init tab panel
	    tabPanel.add(buildAddWidget(), i18n.msg("Add Friend"));
	    tabPanel.add(createVCardSearchWidget(), i18n.msg("VCard Search"));
	    
	    tabPanel.selectTab(0);
	    
	    Button closeButton = new Button(i18n.msg("Close"));
	    closeButton.addClickHandler(new ClickHandler()
	    {
			public void onClick(ClickEvent event) 
			{
				hide();
			}
	    });
	    vPanel.add(closeButton);
	    
	    connectEvent();
	    setWidget(vPanel);
	}
	
	private void connectEvent()
	{
		serviceList.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event) 
			{
				if(serviceList.getSelectedIndex()==0)
					translationWidget.setVisible(false);
				else
					translationWidget.setVisible(true);
			}
		});
		
		translationButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				String str = translationTextBox.getValue();
				str = str==null?"":str;
				if(str.length()>0)
				{
					String gateway = serviceList.getValue(serviceList.getSelectedIndex());
					queryJabberID(gateway,str,new AsyncCallback<String>()
					{
						public void onFailure(Throwable caught) {
						}

						public void onSuccess(String result) 
						{
							jabberIDTextBox.setText(result);
						}
						
					});
				}
			}
		});
		
		addButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				String jid = jabberIDTextBox.getValue();
				jid = jid==null?"":jid;
				if(jid.length()==0)
				{
					Window.alert(i18n.msg("JID can not be empty!"));
					return;
				}
				String nick = nickTextBox.getValue();
				nick = nick==null?"":nick;
				String group="";
				if(groupListBox.getSelectedIndex() != 0)
					group = groupListBox.getValue(groupListBox.getSelectedIndex());
				addFriend(jid,nick,group);
				PopupPrompt.prompt(i18n.msg("Friend added!"));
				jabberIDTextBox.setText("");
				nickTextBox.setText("");
			}
		});
		
		searchButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				List<Field> search = new ArrayList<Field>();
				for(Field field:searchFields)
				{
					String var = field.var;
					String value = getTextBoxValue((TextBox) field.widget);
					search.add(new Field(null,null,var,value));
				}
				JabberSearchPlugin plugin = Session.instance().getJabberSearchPlugin();
				plugin.search(iJab.conf.getXmppConf().getVCardSearchServernode(), search, new JabberSearchResponseHandler()
				{
					public void onForm(IQ iq, List<Field> fields) {
					}

					@Override
					public void onResult(IQ iq,List<Field> titles, List<List<Field>> values) 
					{
						searchList.setFields(titles,values);
					}
					
				});
			}
		});
		
		Session.instance().getJabberSearchPlugin().queryFields(iJab.conf.getXmppConf().getVCardSearchServernode(), new JabberSearchResponseHandler()
		{
			public void onForm(IQ iq, List<Field> fields) 
			{
				filedsWidget.clear();
				searchFields = fields;
				int index;
				for(index=0;index<searchFields.size();index++)
				{
					Field field = searchFields.get(index);
					if(field.type.equals("text-single"))
					{
                        field.widget = new TextBox();
						filedsWidget.setWidget(index*2, 0, new HTML(i18n.msg(field.label)));
						filedsWidget.setWidget(index*2+1, 0, field.widget);
					}
				}
				filedsWidget.setWidget(index*2,0,searchButton);
				searchList.initTable(searchFields);
			}

			public void onResult(IQ iq,List<Field> titles, List<List<Field>> values) {
			}
		});
	}
	
	//for vcard search widget
	private Widget createVCardSearchWidget()
	{
		HorizontalPanel hPanel = new HorizontalPanel();
		filedsWidget = new FlexTable();
		filedsWidget.setCellSpacing(6);
		searchButton = new Button(i18n.msg("Search"));
		
		
		hPanel.add(filedsWidget);
		
		searchList = new VCardSearchList();
		hPanel.add(searchList);
		return hPanel;
	}
	
	private String getTextBoxValue(TextBox textBox)
	{
		String str = textBox.getValue();
		str = str==null?"":str;
		return str;
	}
	
	//for add tab widget
	private Widget buildAddWidget()
	{
		VerticalPanel vPanel = new VerticalPanel();
		// service list box
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new HTML(i18n.msg("Service")+":&nbsp"));
		
		//build services list
		services = new ArrayList<String>();
		services.add("Jabber");
		services.addAll(Session.instance().getRosterPlugin().getTransports());
		serviceList = new ListBox();
		for(String service:services)
		{
			if(!service.equals(Session.instance().getUser().getDomainname()))
				serviceList.addItem(service);
		}
		serviceList.setItemSelected(0, true);
		hPanel.add(serviceList);
		
		vPanel.add(hPanel);
		translationWidget = createTranslationWidget();
		vPanel.add(translationWidget);
		translationWidget.setVisible(false);
		
		FlexTable layout = new FlexTable();
		layout.setCellSpacing(6);
		
		jabberIDTextBox = new TextBox();
		nickTextBox = new TextBox();
		groupListBox = new ListBox();
		//init group box
		groupListBox.addItem("<none>");
		for(String groupName:iJab.client.getGroups())
			groupListBox.addItem(groupName);
		
		addButton = new Button(i18n.msg("Add"));
		
		layout.setWidget(0, 0, new HTML("Jabber ID:"));
		layout.setWidget(0, 1, jabberIDTextBox);
		layout.setWidget(1,0,new HTML(i18n.msg("Nickname(optional):")));
		layout.setWidget(1,1,nickTextBox);
		layout.setWidget(2, 0, new HTML(i18n.msg("Group")));
		layout.setWidget(2, 1, groupListBox);
		layout.setWidget(3, 1, addButton);
		
		vPanel.add(layout);
		
		return vPanel;
	}
	
	private Widget createTranslationWidget()
	{
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.add(new HTML(i18n.msg("Service ID Translation")));
		
		FlexTable layout = new FlexTable();
	    layout.setCellSpacing(6);
	    
	    translationTextBox = new TextBox();
	    translationButton = new Button(i18n.msg("Get Jabber ID"));
	    layout.setWidget(0, 0, translationTextBox);
	    layout.setWidget(0, 1, translationButton);
	    DecoratorPanel decPanel = new DecoratorPanel();
	    decPanel.setWidget(layout);
	    
	    vPanel.add(decPanel);
	    
		return vPanel;
	}
	
	private void queryJabberID(String gateway,String id,final AsyncCallback<String> callBack)
	{
		IQ iq = new IQ(IQ.Type.set);
		iq.setAttribute("id", String.valueOf(Session.nextId()));
		iq.setTo(JID.fromString(gateway));
		Packet query = iq.addChild("query", "jabber:iq:gateway");
		Packet prompt = query.addChild("prompt", null);
		prompt.setCData(id);
		Session.instance().addResponseHandler(iq, new ResponseHandler()
		{
			public void onError(IQ iq, ErrorType errorType,
					ErrorCondition errorCondition, String text) {
			}

			public void onResult(IQ iq) 
			{
				try
				{
					Packet query = iq.getFirstChild("query");
					Packet prompt = query.getFirstChild("prompt");
					if(prompt!=null)
						callBack.onSuccess(prompt.getCData());
				}
				catch(Exception e)
				{
					
				}
			}
		});
	}
	
	private void addFriend(String jid,String nick,String group)
	{
		RosterItem ri = new RosterItem();
		ri.setJid(jid);
		if(nick.length()>0)
			ri.setName(nick);
		String[] groups = {group};
		ri.setGroups(groups);
		Session.instance().getRosterPlugin().addItem(ri, null);
		
		Session.instance().getPresencePlugin().subscribe(JID.fromString(jid));
	}
}
