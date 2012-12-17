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
 * Mar 2, 2010
 */
package ru.it.lecm.im.client.utils;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class IdleTool 
{
	private static IdleTool instance = null;
	public static IdleTool instance()
	{
		if(instance == null)
			instance = new IdleTool();
		return instance;
	}

	private Timer timer = null;
	private boolean isIdle = false;
	private boolean isEnabled = false;
	private IdleListener listener = null;
	private IdleTool()
	{
		addEvent();
	}
	
	public void start(IdleListener listener)
	{
		this.listener = listener;
		isIdle = false;
		isEnabled = true;
		resetTimer();
	}
	
	public void stop()
	{
		this.listener = null;
		isIdle = false;
		isEnabled = false;
		if(timer!=null)
			timer.cancel();
	}
	
	private void resetTimer()
	{
		if(!isEnabled)
			return;
		if(timer==null) 
		{
			timer = new Timer()
			{
				@Override
				public void run() 
				{
					isIdle = true;
					if(listener!=null)
						listener.onIdle();
				}
			};
		}
		else
		{
			timer.cancel();
			if(isIdle)
			{
				if(listener!=null)
					listener.onWeek();
			}
			isIdle = false;
		}
		timer.schedule(1000*60*5);
	}
	
	private void addEvent()
	{
		Event.addNativePreviewHandler(new NativePreviewHandler()
		{

			public void onPreviewNativeEvent(NativePreviewEvent event) 
			{
				if(!isIdle)
					return;
				switch(event.getTypeInt())
				{
				case Event.ONMOUSEMOVE:
					resetTimer();
					break;
				case Event.ONKEYPRESS:
					resetTimer();
					break;
				}
			}
		});
	}
}
