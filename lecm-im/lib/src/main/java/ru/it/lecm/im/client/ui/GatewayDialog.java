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
 * Apr 2, 2010
 */
package ru.it.lecm.im.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import ru.it.lecm.im.client.utils.i18n;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class GatewayDialog extends DialogBox
{
	private Button cancelButton;
	private Button okButton;
	private Map<String,TextBox> fields = new HashMap<String,TextBox>();
	public GatewayDialog(final IQ iq)
	{
		setGlassEnabled(false);
		setAnimationEnabled(true);
		setModal(false);
		setText(i18n.msg("Registration")+":"+iq.getFrom().toString());
		this.setWidth("400px");
		
		VerticalPanel vPanel = new VerticalPanel();
	    vPanel.setSpacing(5);
	    Packet query = iq.getFirstChild("query");
	    Packet instructionsPacket = query.getFirstChild("instructions");
	    String instructions = "";
	    if(instructionsPacket != null)
	    	instructions = instructionsPacket.getCData();
	    vPanel.add(new Label(instructions));
	    
	    FlexTable layout = new FlexTable();
	    FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
	    List<? extends Packet> childs = query.getChildren();
	    int index = 0;
	    for(index=0;index<childs.size();index++)
	    {
	    	Packet child = childs.get(index);
	    	String name = child.getName();
	    	if(name.equals("instructions")||name.equals("registered"))
	    		continue;
	    	String value = child.getCData();
	    	TextBox widget;
	    	if(name.equals("password"))
	    		widget = new PasswordTextBox();
	    	else
	    		widget = new TextBox();
	    	if(value!=null)
	    		widget.setText(value);
	    	fields.put(name, widget);
	    	layout.setWidget(index, 0, new HTML(i18n.msg(name)));
	    	layout.setWidget(index, 1, widget);
	    }
	    
	    HorizontalPanel hPanel = new HorizontalPanel();
	    hPanel.setSpacing(5);
	    okButton = new Button(i18n.msg("Register"));
	    cancelButton = new Button(i18n.msg("Cancel"));
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
				Map<String,String> values = new HashMap<String,String>();
				for(String key:fields.keySet())
				{
					TextBox widget = fields.get(key);
					String value = widget.getText();
					value = value==null?"":value;
					values.put(key, value);
				}
				Session.instance().getGatewayPlugin().register(iq.getFrom().toString(), values, new ResponseHandler()
				{
					public void onError(IQ iq, ErrorType errorType,
							ErrorCondition errorCondition, String text) 
					{
						Window.alert(i18n.msg("Regsiter failed!"));
					}
					public void onResult(IQ iq) {
						Window.alert(i18n.msg("Regsiter succeed!"));
						hide();
					}
					
				});
			}
	    });
	    hPanel.add(cancelButton);
	    hPanel.add(okButton);
	    
	    layout.setWidget(index+1, 1, hPanel);
	    cellFormatter.setHorizontalAlignment(index+1, 1, HasHorizontalAlignment.ALIGN_RIGHT);
	    vPanel.add(layout);
	    setWidget(vPanel);
	}
}
