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
 * Apr 1, 2010
 */
package ru.it.lecm.im.client.ui;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class PanelButton extends BarButton
{
	final protected ChatPanelBar chatPanel;
	protected int oldMessageCount = 0;
	public PanelButton(ChatPanelBar chatPanel)
	{
		super();
		this.chatPanel = chatPanel;
		setButtonTextEnabled(true);
		setTipEnabled(false);
		setButtonWindowMaxEnabled(false);
		setCloseEnabled(true);
		setButtonWidth(134);
		this.addButtonStyle("ijab-chat-button");
	}
	
	public int getOldMessageCount()
	{
		return oldMessageCount;
	}
}
