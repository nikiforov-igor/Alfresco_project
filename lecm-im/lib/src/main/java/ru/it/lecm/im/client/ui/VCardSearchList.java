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
 * Mar 18, 2010
 */
package ru.it.lecm.im.client.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.xmpp.roster.RosterItem;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch.Field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import ru.it.lecm.im.client.utils.PopupPrompt;
import ru.it.lecm.im.client.utils.i18n;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class VCardSearchList extends Composite {
	
	private static VCardSearchListUiBinder uiBinder = GWT
			.create(VCardSearchListUiBinder.class);

	interface VCardSearchListUiBinder extends UiBinder<Widget, VCardSearchList> {
	}
	

	static final int VISIBLE_ITEM_COUNT = 20;

	@UiField FlexTable header;
	@UiField FlexTable table;
	@UiField Button addButton;
	@UiField FlowPanel buttonOuter;

	private int selectedRow = -1;
	private Map<String,Integer> varToIndex = new HashMap<String,Integer>();
	public VCardSearchList() {
		initWidget(uiBinder.createAndBindUi(this));
		addButton.setText(i18n.msg("Add"));
		buttonOuter.getElement().setAttribute("align", "right");
		header.setText(0, 0, i18n.msg("Search result"));
		addButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(selectedRow == -1||varToIndex.get("jid")==null)
					return;
				int jidCol = varToIndex.get("jid");
				String jid = table.getText(selectedRow, jidCol);
				addFriend(jid);
				PopupPrompt.prompt(i18n.msg("Friend added!"));
			}
			
		});
	}
	
	private void addFriend(String jid)
	{
		RosterItem ri = new RosterItem();
		ri.setJid(jid);
		Session.instance().getRosterPlugin().addItem(ri, null);
		
		Session.instance().getPresencePlugin().subscribe(JID.fromString(jid));
	}
	
	public void initTable(List<Field> fields) 
	{
		header.clear();
		varToIndex.clear();
		for(int index=0;index<fields.size();index++)
		{
			Field field = fields.get(index);
			//header.setText(0, index, field.label);
			varToIndex.put(field.var, index);
			//table.getColumnFormatter().setWidth(index, "100px");
		}
	}
	public void setFields(List<Field> titles,List<List<Field>> values)
	{
		table.clear();
		initTable(titles);
		selectedRow = -1;
		if(values.size()==0)
			table.setText(0, 0, i18n.msg("No result!"));
		for(int index=0;index<values.size();index++)
		{
			List<Field> fields = values.get(index);
			for(Field field:fields)
			{
				if(varToIndex.get(field.var) == null)
					continue;
				table.setText(index, varToIndex.get(field.var), field.value);
			}
		}
	}
	
	@UiHandler("table")
	void onTableClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = table.getCellForEvent(event);
		if (cell != null) {
			int row = cell.getRowIndex();
			selectRow(row);
		}
	}
	
	private void selectRow(int row) 
	{

		styleRow(selectedRow, false);
		styleRow(row, true);

		selectedRow = row;
	}

	private void styleRow(int row, boolean selected) {
		if (row != -1) 
		{
			if (selected) {
				table.getRowFormatter().addStyleName(row, "selectedRow");
			} else {
				table.getRowFormatter().removeStyleName(row, "selectedRow");
			}
		}
	}

}
