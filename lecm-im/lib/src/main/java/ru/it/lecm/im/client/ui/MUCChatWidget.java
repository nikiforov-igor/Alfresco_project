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
 * Mar 31, 2010
 */
package ru.it.lecm.im.client.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import ru.it.lecm.im.client.utils.ChatTextFormatter;
import ru.it.lecm.im.client.utils.i18n;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Presence;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.muc.GroupChat;

import java.util.Date;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class MUCChatWidget extends BarChatWidgetUI 
{

	private GroupChat gc;
	String lastNick = null;
	
	public MUCChatWidget()
	{
		historyElement.getStyle().setDisplay(Display.NONE);
	}
	
	public void setGroupChat(GroupChat gc)
	{
		this.gc = gc;
	}
	
	public void processMessage(Message message)
	{
        if (message.getFrom().getResource() == null || message.getBody() == null)
        {
            return;
        }

        String nick = null;
		String resrouceName = message.getFrom().getResource();
		String gcNick = gc == null?"":gc.getNickname();
		gcNick = gcNick==null?"":gcNick;
		if(resrouceName!=null&&resrouceName.equals(gcNick))
		{
			nick = i18n.msg("Me");
		}
		else
		{
			Presence presence = gc.getPresence(message.getFrom());
			if(presence!=null)
				nick = presence.getExtNick();
			if(nick==null||nick.length() == 0)
				nick = message.getFrom().getResource();
		}
		DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm a");
		final String dateTime = fmt.format(new Date());
		addMessage(createMessageWidget(nick,message.getBody(),dateTime));
	}
	
	public void cleanHistory()
	{
		super.cleanHistory();
		lastNick = null;
	}

	@Override
	protected void send(String msg) 
	{
		gc.send(msg);
	}
	
	private HTML createMessageWidget(String nick,String message,final String dateTime)
	{
		message = ChatTextFormatter.format(message);
		boolean isConsecutiveMessage= false;
		if(lastNick != null&&lastNick.equals(nick))
			isConsecutiveMessage = true;
		lastNick = nick;
		
		String nickStyle = "ijab-local-message";
		if(!nick.equals(i18n.msg("Me")))
			nickStyle = "ijab-other-message";
		String html;
		if(isConsecutiveMessage)
		{
			html = "<p>"+message+"</p>";
		}
		else
		{
			String header = "<h4 class=\""+nickStyle+"\">"+"<span class=\"ijab-gray\">"+dateTime+"</span>"+nick+"</h4>";
			html = header+"<p>"+message+"</p>"; 
		}
		return new HTML(html);
	}

	@Override
	protected void onTyping() {
	}
	@Override
	protected void userAvatarClicked(int clientX, int clientY) {
	}
	@Override
	protected void userAvatarMouseOver(int clientX, int clientY) {
	}
	@Override
	protected void viewHistory() {
	}

}
