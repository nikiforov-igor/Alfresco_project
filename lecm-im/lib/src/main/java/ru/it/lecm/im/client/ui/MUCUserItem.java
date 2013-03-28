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

import ru.it.lecm.im.client.utils.XmppStatus;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCUserItem extends ContactViewItemUI
{

	/**
	 * @param presence presence
	 */
	public MUCUserItem(Presence presence) 
	{
		super(presence.getFrom().toString());
		//avatar.getStyle().setDisplay(Display.NONE);
		setStatusText(null);
		setName(shortName(presence.getFrom().getResource()));
		mainWidget.setTitle(presence.getFrom().getResource());
		setStatusIcon(XmppStatus.statusIconFromPresence(presence));
	}
	
	private String shortName(String name)
	{
		if(name.length()>9)
			name = name.substring(0, 8)+"...";
		return name;
	}
	
	public void updatePresence(Presence presence)
	{
		setStatusIcon(XmppStatus.statusIconFromPresence(presence));
	}

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ui.ContactViewItemUI#onAvatarClicked(int, int)
	 */

	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ui.ContactViewItemUI#onAvatarOver(int, int)
	 */

    /* (non-Javadoc)
     * @see anzsoft.iJab.client.ui.ContactViewItemUI#onItemClicked()
     */
	@Override
	protected void onItemClicked() {
		// TODO Auto-generated method stub
		
	}


}
