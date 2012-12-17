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
package ru.it.lecm.im.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class GatewayItemImpl extends JavaScriptObject implements GatewayItem
{

	protected GatewayItemImpl()
	{
	}
	
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.GatewayItem#description()
	 */
	public final native String description() 
	/*-{
		try
		{
			return this.description;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.GatewayItem#icon()
	 */
	public final native String icon() 
	/*-{
		try
		{
			return this.icon;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.GatewayItem#name()
	 */
	public final native String name() 
	/*-{
		try
		{
			return this.name;
		}
		catch(e)
		{
			return "";
		}
	}-*/;


	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.data.GatewayItem#servernode()
	 */
	public final native String servernode() 
	/*-{
		try
		{
			return this.servernode;
		}
		catch(e)
		{
			return "";
		}
	}-*/;

}
