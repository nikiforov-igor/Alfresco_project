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
package ru.it.lecm.im.client.utils;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import ru.it.lecm.im.client.data.iJabConfImpl;

public class WindowPrompt 
{
	private static Timer flashTimer = null;
	static int loop = 0;
	private static String oldTitle = null;
	private static boolean inPrompt = false;
	private static boolean eventAdded = false;
	public static void prompt(final String promptString)
	{
		if(iJabConfImpl.getConf().isTitleNotifyDisabled())
			return;
		if(flashTimer!=null)
			stopPrompt();
		inPrompt = true;
		oldTitle = Window.getTitle();
		loop = 0;
		flashTimer = new Timer()
		{
			public void run() 
			{
				if(loop == promptString.length()-1)
					loop = 0;
				String msg = promptString.substring(loop, promptString.length()) +" " + promptString.substring(0,loop);
				Window.setTitle(msg);
				loop++;
				flashTimer.schedule(200);
			}
		};
		flashTimer.schedule(200);
		//invokeMouseEvent();
		addEvent();
	}
	
	public static void stopPrompt()
	{
		inPrompt = false;
		flashTimer.cancel();
		Window.setTitle(oldTitle);
		flashTimer = null;
		//deleteEvent();
	}
	
	private static void addEvent()
	{
		if(eventAdded)
			return;
		eventAdded = true;
		Event.addNativePreviewHandler(new NativePreviewHandler()
		{

			public void onPreviewNativeEvent(NativePreviewEvent event) 
			{
				if(!inPrompt)
					return;
				switch(event.getTypeInt())
				{
				case Event.ONMOUSEMOVE:
					stopPrompt();
					break;
				case Event.ONKEYPRESS:
					stopPrompt();
					break;
				}
			}
		});
	}
	
	public static native void invokeMouseEvent()
	/*-{
		$wnd.document.onmouseover = function()
		{
			@ru.it.lecm.im.client.utils.WindowPrompt::stopPrompt()();
			return true;
		}
		$wnd.document.onkeydown = function()
		{
			@ru.it.lecm.im.client.utils.WindowPrompt::stopPrompt()();
			return true;
		} 
	 }-*/;
	
	public static native void deleteEvent()
	/*-{
		$wnd.document.onmouseover  = null;
		$wnd.document.onkeydown = null; 
	}-*/;
}
