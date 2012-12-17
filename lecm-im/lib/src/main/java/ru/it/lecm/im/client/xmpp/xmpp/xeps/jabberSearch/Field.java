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
package ru.it.lecm.im.client.xmpp.xmpp.xeps.jabberSearch;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class Field 
{
	public String type;
	public String label;
	public String var;
	public String value;
	public Widget widget;
	public Field (String type,String label,String var,String value)
	{
		this.type = type;
		this.label = label;
		this.var = var;
		this.value = value;
	}
	
}
