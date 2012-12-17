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
 * Mar 4, 2010
 */
package ru.it.lecm.im.client.utils;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class i18n 
{
	static public native String msg(String str)
	/*-{
		try
		{
			if(typeof $wnd.ijab_i18n == 'undefined')
				return str;
			var ret = $wnd.ijab_i18n[str];
			if(ret ==null||ret.length==0)
				return str;
			return ret;
		}
		catch(e)
		{
			return str;
		}
	 }-*/;
}
