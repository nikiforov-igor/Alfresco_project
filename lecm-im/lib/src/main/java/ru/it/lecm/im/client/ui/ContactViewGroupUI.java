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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContactViewGroupUI extends Composite {

	private static ContactViewGroupUiBinder uiBinder = GWT
			.create(ContactViewGroupUiBinder.class);

	interface ContactViewGroupUiBinder extends
			UiBinder<Widget, ContactViewGroupUI> {
	}
	
	@UiField FocusHTMLPanel groupHead;
	@UiField Element groupName;
	@UiField Element groupCount;
	@UiField Element groupHeadTriangle;
	protected String name;
	protected final FlowPanel groupBody;
	
	private static final String GROUP_PREFIX = "ijab_contactview_group_";
	private boolean opened = false; 
	public ContactViewGroupUI(final String name,final FlowPanel groupBody) 
	{
		this(name,groupBody,"ijab-contactview-grouphead");
	}
	
	public ContactViewGroupUI(final String name,final FlowPanel groupBody,final String groupIconStyle)
	{
		this.name = name;
		this.groupBody = groupBody;
		initWidget(uiBinder.createAndBindUi(this));
		groupName.setInnerText(name);
		groupHead.setStyleName("ijab-contactview-grouphead");
		
		if(groupIconStyle.equalsIgnoreCase("ijab-contactview-grouphead"))
		{
			groupCount.setInnerText("[0/0]");
			closeGroup();
		}
		else
		{
			groupCount.setInnerText("[0]");
			openGroup();
		}
		groupHead.getElement().setId(getGroupHeadID());
		groupBody.getElement().setId(getGroupBodyID());
		
	}
	
	protected void setGroupHeaderCount(final String text)
	{
		groupCount.setInnerText(text);
	}
	
	public String getGroupHeadID()
	{
		return GROUP_PREFIX+name;
	}
	
	public String getGroupBodyID()
	{
		return getGroupHeadID()+"_div";
	}
	
	public void rename(final String newGroupName)
	{
		name = newGroupName;
		groupName.setInnerText(name);
		groupHead.getElement().setId(getGroupHeadID());
		groupBody.getElement().setId(getGroupBodyID());
	}
	
	public FocusHTMLPanel getGroupHead()
	{
		return groupHead;
	}
	
	public ComplexPanel getGroupBody()
	{
		return groupBody;
	}
	
	public static String buildGroupWidgetID(final String gn)
	{
		return GROUP_PREFIX+gn;
	}
	
	public static String buildGroupBodyWidgetID(final String gn)
	{
		return buildGroupWidgetID(gn)+"_div";
	}
	
	public void closeGroup()
	{
		opened = false;
		groupBody.setVisible(false);
		groupHeadTriangle.removeClassName("ui-icon-triangle-1-s");
		groupHeadTriangle.addClassName("ui-icon-triangle-1-e");
	}
	
	public void openGroup()
	{
		opened = true;
		groupBody.setVisible(true);
		groupHeadTriangle.removeClassName("ui-icon-triangle-1-e");
		groupHeadTriangle.addClassName("ui-icon-triangle-1-s");
	}
	
	public void setGroupVisible(boolean b)
	{
		groupHead.setVisible(b);
		groupBody.setVisible(b&&opened);
	}
	
	@UiHandler("groupHead")
	void handleClick(ClickEvent e) 
	{
		groupBody.setVisible(!groupBody.isVisible());
		if(groupBody.isVisible())
		{
			groupHeadTriangle.removeClassName("ui-icon-triangle-1-e");
			groupHeadTriangle.addClassName("ui-icon-triangle-1-s");
		}
		else
		{
			groupHeadTriangle.removeClassName("ui-icon-triangle-1-s");
			groupHeadTriangle.addClassName("ui-icon-triangle-1-e");
		}
	}

}
