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
package ru.it.lecm.im.client.ui.emoticons;

import com.google.gwt.core.client.GWT;

public class Emoticon 
{
	final private String[] texts;
	final private String icon;
	final private String name;
	
	public Emoticon(final String name,final String icon,final String[] texts )
	{
		this.name = name;
		this.icon = icon;
		this.texts = texts;
	}

	public String getIcon() 
	{
		return icon;
	}
	
	public String getIconUrl()
	{
		return GWT.getModuleBaseURL()+"images/emot/"+icon;
	}

	public String getName() {
		return name;
	}
	
	public String getText()
	{
		return texts[0];
	}
	
	public String[] getAllText()
	{
		return texts;
	}
}
