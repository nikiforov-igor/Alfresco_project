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

import ru.it.lecm.im.client.Log;
import ru.it.lecm.im.client.xmpp.Session;
import ru.it.lecm.im.client.xmpp.Storage;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.stanzas.Message;
import ru.it.lecm.im.client.xmpp.stanzas.Message.ChatState;
import ru.it.lecm.im.client.xmpp.stanzas.Message.MsgEvent;
import ru.it.lecm.im.client.xmpp.stanzas.Message.Type;
import ru.it.lecm.im.client.xmpp.xmpp.message.Chat;
import ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.XmppChat;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.utils.ChatTextFormatter;
import ru.it.lecm.im.client.utils.SoundManager;
import ru.it.lecm.im.client.utils.i18n;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BarChatWidget extends BarChatWidgetUI
{
	private final static String MSG_DATE = "0";
	private final static String MSG_NICK = "1";
	private final static String MSG_MSG = "2";
	
	final private ChatPanelButton button;
	String lastNick = null;
	private final List<JSONObject> messageHistorys = new ArrayList<JSONObject>();
	
	private Timer composingTimer = null;
	boolean isComposing;
	boolean sendComposingEvents;
	ChatState contactChatState;
	ChatState lastChatState;
	
	enum ArchiveRange{ToDay,OneWeek,OneMonth,HalfYear,All}
	
	public BarChatWidget(ChatPanelButton button)
	{
		this.button = button; 
		contactChatState = null;
		lastChatState = null;
		sendComposingEvents = false;
		isComposing = false;
		composingTimer = null;
	}
	public void processMessage(String nick,Message message,boolean firstMessage)
	{
		if(message.getType() == Type.error)
			return;
		String body = message.getBody();
		if(body!=null&&body.length()!=0)
		{
			SoundManager.playMessage();
			DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm a");
			String dateTime = fmt.format(new Date());
			addMessage(createMessageWidget(nick,body,dateTime));
			
			sendComposingEvents = message.containsEvent(MsgEvent.ComposingEvent);
			if(message.containsEvents()||message.getChatState() != null)
				setContactChatState(ChatState.active);
			else
				setContactChatState(null);
		}
		else
		{
			if(message.containsEvent(MsgEvent.CancelEvent))
				setContactChatState(ChatState.paused);
			else if(message.containsEvent(MsgEvent.ComposingEvent))
				setContactChatState(ChatState.composing);
			
			if(message.getChatState() != null)
				setContactChatState(message.getChatState());
		}
	}
	
	public void processSyncSend(Message message,boolean firstMessage)
	{
		if(message.getType() == Type.error)
			return;
		String body = message.getBody();
		if(body!=null&&body.length()!=0)
		{
			DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm a");
			String dateTime = fmt.format(new Date());
			addMessage(createMessageWidget(i18n.msg("Me"),body,dateTime));
		}
	}
	
	private HTML createMessageWidget(String nick,String message,final String dateTime)
	{
		JSONObject msgHistory = new JSONObject();
		msgHistory.put(MSG_DATE, new JSONString(dateTime));
		msgHistory.put(MSG_NICK, new JSONString(nick));
		msgHistory.put(MSG_MSG, new JSONString(message));
		if(messageHistorys.size()>=8)
		{
			messageHistorys.remove(0);
		}
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
		
		messageHistorys.add(msgHistory);	
		
		return new HTML(html);
	}
	
	@Override
	protected void send(String msg) 
	{
		Chat<XmppChat> item = button.getChatItem();
		Message m  = new Message(Message.Type.chat, item.getJid(), null, msg, item.getThreadId());
 		// Request events
		if(sendComposingEvents)
		{
			m.addEvent(MsgEvent.ComposingEvent);
		}
		m.setChatState(ChatState.active);
		// Update current state
		setChatState(ChatState.active);
		
		Session.instance().getChatPlugin().sendChatMessage(m);
		
		DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm a");
		final String dateTime = fmt.format(new Date());
		addMessage(createMessageWidget(i18n.msg("Me"),msg,dateTime));
		resetComposing();
	}

	@Override
	protected void userAvatarClicked(int clientX,int clientY) 
	{
		button.onAvatarClicked(clientX,clientY);
	}
	
	@Override
	protected void userAvatarMouseOver(int clientX, int clientY) {
		button.onAvatarMouseOver(clientX,clientY);
	}
	
	public void cleanHistory()
	{
		super.cleanHistory();
		lastNick = null;
		messageHistorys.clear();
	}
	
	public void doSuspend(final String storeKey)
	{
		if(messageHistorys.isEmpty())
			return;
		try
		{
			JSONArray array = new JSONArray();
			for(int index=0;index<messageHistorys.size();index++)
			{
				array.set(index, messageHistorys.get(index));
			}
			final String prefix = Session.instance().getUser().getStorageID();
			Storage storage = Storage.createStorage(storeKey,prefix);
			storage.set(storeKey, array.toString());
		}
		catch(Exception e)
		{
			Window.alert(e.toString());
		}
	}
	
	public void doResume(final String storeKey)
	{
		try
		{
			final String prefix = Session.instance().getUser().getStorageID();
			Storage storage = Storage.createStorage(storeKey,prefix);
			String data = storage.get(storeKey);
			storage.set(storeKey, "");
			storage.remove(storeKey);
			storage.remove(prefix+storeKey);
			if(data == null||data.length()==0)
				return;
			JSONArray array = JSONParser.parse(data).isArray();
			if(array == null)
				return;
			for(int index=0;index<array.size();index++)
			{
				JSONObject obj = array.get(index).isObject();
				if(obj == null)
					continue;
				String dateString = obj.get(MSG_DATE).isString().stringValue();
				String nick = obj.get(MSG_NICK).isString().stringValue();
				String msg = obj.get(MSG_MSG).isString().stringValue();
				
				addMessage(createMessageWidget(nick,msg,dateString));
			}
		}
		catch(Exception e)
		{
			Window.alert(e.toString());
		}
	}
	
	public void onWindowClose()
	{
		resetComposing();
		setChatState(ChatState.inactive);
	}
	
	public void onButtonClose()
	{
		resetComposing();
		setChatState(ChatState.gone);
		if (contactChatState == ChatState.composing || contactChatState == ChatState.inactive)
			setContactChatState(ChatState.paused);
		updateChatStatusText();
	}
	
	//for message event
	private void setComposing()
	{
		if(composingTimer ==null)
		{
			composingTimer = new Timer()
			{
				@Override
				public void run() {
					checkComposing();
				}
			};
			updateIsComposing(true);
		}
		composingTimer.schedule(2000);
		isComposing = true;
	}
	
	private void checkComposing() 
	{
		if (!isComposing) 
		{
			// User stopped composing
			composingTimer.cancel();
			composingTimer = null;
			updateIsComposing(false);
		}
		isComposing = false; // Reset composing
	}
	
	private void resetComposing()
	{
		if (composingTimer!=null)
		{
			composingTimer.cancel();
			composingTimer = null;
			isComposing = false;
		}
	}
	
	private void updateIsComposing(boolean b)
	{
		setChatState(b ? ChatState.composing : ChatState.paused);
	}
	
	private void setChatState(ChatState state)
	{
		if(!button.isContactAvailable())
		{
			sendComposingEvents = false;
			lastChatState = null;
			return;
		}
		
		// Transform to more privacy-enabled chat states if necessary
		if(state == ChatState.gone ||state == ChatState.inactive)
			state = ChatState.gone;
		
		// Check if we should send a message
		if (state == lastChatState || state == ChatState.active || (lastChatState == ChatState.active && state == ChatState.paused) )
		{
			lastChatState = state;
			return;
		}
		
		Chat<XmppChat> item = button.getChatItem();
		// Build event message
		Message msg = new Message(Message.Type.chat, item.getJid(), null, "", item.getThreadId());
		if(sendComposingEvents)
		{
			if(state == ChatState.composing)
				msg.addEvent(MsgEvent.ComposingEvent);
			else if(lastChatState == ChatState.composing)
				msg.addEvent(MsgEvent.CancelEvent);
		}
		
		if(contactChatState != null&&lastChatState != ChatState.gone)
		{
			if ((state == ChatState.active && lastChatState == ChatState.composing) || (state == ChatState.composing && lastChatState == ChatState.inactive)) 
			{
				// First go to the paused state
				Session.instance().getChatPlugin().sendChatMessage(item.getJid(), "", item.getThreadId(), ChatState.paused);
			}
			msg.setChatState(state);
		}
		
		if(msg.containsEvents()||state != null)
			Session.instance().getChatPlugin().sendChatMessage(msg);
		
		if (lastChatState != ChatState.gone || state == ChatState.active)
			lastChatState = state;
	}
	
	void setContactChatState(ChatState state)
	{
		contactChatState = state;
		if (state == ChatState.gone) 
		{
			setChatStatusText(i18n.msg("Chat closed"));
		}
		else
		{
			// Activate ourselves
			if(lastChatState == ChatState.gone)
				setChatState(ChatState.active);
			updateChatStatusText();
		}
		
	}
	
	private void updateChatStatusText()
	{
		if(contactChatState == ChatState.composing)
			setChatStatusText(i18n.msg("Typeing..."));
		else if(contactChatState == ChatState.inactive)
			setChatStatusText(i18n.msg("Inactive"));
		else
			setChatStatusText("");
		button.setChatState(contactChatState);
	}
	
	@Override
	protected void onTyping() 
	{
		setComposing();
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ui.BarChatWidgetUI#viewHistory()
	 */
	@Override
	public void viewHistory() 
	{
		viewRangeArchive(ArchiveRange.ToDay);
		
		/*
		if(iJab.client instanceof XmppClient)
		{
			XmppClient client = (XmppClient)iJab.client;
			client.viewMessageArchive(button.getChatItem().getJid());
		}
		*/
	}
	
	@SuppressWarnings("deprecation")
	private Date getRangeStart(ArchiveRange range)
	{
		Date ret = new Date();
		int month = ret.getMonth();
		switch(range)
		{
		case ToDay:
			ret.setHours(0);
			ret.setMinutes(0);
			ret.setSeconds(0);
			break;
		case OneWeek:
			ret = new Date(new Date().getTime()-7*24*60*60*1000);
			break;
		case OneMonth:
			if(month == 0)
			{
				ret.setYear(ret.getYear()-1);
				month = 11;
			}
			else
				month = month -1;
			ret.setMonth(month);
			break;
		case HalfYear:
			if(month<6)
			{
				ret.setYear(ret.getYear()-1);
				month = 11+(month-6);
			}
			else
				month = month - 6;
			ret.setMonth(month);
			break;
		case All:
			ret = null;
		}
		//DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println(fmt.format(ret));
		return ret;
	}
	
	private void viewRangeArchive(ArchiveRange range)
	{
        Log.log("BarChatWidget.viewRangeArchive");
        cleanHistory();
		chatContent.add(createRangeSelectorWidget(range));
		chatContent.add(createTopRangeWidget(range));
		final FlowPanel archivePanel = new FlowPanel();
		chatContent.add(archivePanel);
		Date start = getRangeStart(range);
		
		final MessageArchivingPlugin plugin = Session.instance().getMessageArchivingPlugin();
		plugin.retriveCollectionList(button.getChatItem().getJid(), 100, start, new CollectionHandler()
		{
			@Override
			public void onSuccess(IQ iq, CollectionResultSet res) 
			{
				if(res==null||res.getCollections().size() == 0)
				{
					archivePanel.add(new HTML("<div align='center'><span>"+i18n.msg("No message hsitory")+"</span></div>"));
				}
				else
				{
					CollectionItem item0 = res.getCollections().get(0);
					DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
					FocusHTMLPanel panel = new FocusHTMLPanel("<div align='center' class='ijab-archive-collection ui-corner-all'><span>"+fmt.format(item0.getStart())+"</span></div>");
					archivePanel.add(panel);
					final FlowPanel content = new FlowPanel();
					archivePanel.add(content);
					plugin.retriveCollection(item0.getWith(), item0.getStart(), 9999, 0, new MessageArchiveRequestHandler()
					{
						@Override
						public void onSuccess(IQ iq, ResultSet rs) 
						{
							for(Item item:rs.getItems())
							{
								content.add(createMessageWidget4ArchiveItem(item));
							}
						}
					});
					
					for(int index=1;index<res.getCollections().size();index++)
					{
						CollectionItem cItem = res.getCollections().get(index);
						FlowPanel cWidget = new FlowPanel();
						archivePanel.add(createTitleWidget4Collection(cItem,cWidget));
						archivePanel.add(cWidget);
					}
				}
			}
		});
	}
	
	private Widget createMessageWidget4ArchiveItem(Item item)
	{
		DateTimeFormat fmt = DateTimeFormat.getFormat("h:mm a");
		final String dateTime = fmt.format(item.getDate());
		String nick;
		if(item.getType().equals(Item.Type.TO))
			nick = i18n.msg("Me");
		else
			nick = XmppProfileManager.getName(button.getChatItem().getJid().toStringBare());
		return createArchiveMessageWidget(nick,item.getBody(),dateTime);
	}
	
	private Widget createTitleWidget4Collection(final CollectionItem item,final FlowPanel content)
	{
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
		final FocusHTMLPanel panel = new FocusHTMLPanel("<div align='center' class='ijab-archive-collection ui-corner-all'><span>"+fmt.format(item.getStart())+"</span></div>");
		panel.addStyleName("collopse");
		final ClickHandler handler = new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				if(!panel.getStyleName().contains("collopse"))
					return;
				panel.removeStyleName("collopse");
				final MessageArchivingPlugin plugin = Session.instance().getMessageArchivingPlugin();
				plugin.retriveCollection(item.getWith(), item.getStart(), 9999, 0, new MessageArchiveRequestHandler()
				{
					@Override
					public void onSuccess(IQ iq, ResultSet rs) 
					{
						for(Item item:rs.getItems())
						{
							content.add(createMessageWidget4ArchiveItem(item));
						}
					}
				});
			}
		};
		panel.addClickHandler(handler);
		return panel;
	}
	
	private Widget createTopRangeWidget(ArchiveRange range)
	{
        return new HTML("<div class='ijab-archive-top ui-corner-all'><span>"+rangeString(range)+"</span></div>");
	}
	
	private String rangeString(ArchiveRange range)
	{
		String ret = range.toString();
		return i18n.msg(ret);
	}
	
	private FlowPanel createRangeSelectorWidget(ArchiveRange range)
	{
		FlowPanel widget = new FlowPanel();
		widget.addStyleName("ijab-archive-selector ui-helper-clearfix");
		ArchiveRange ranges[] = {ArchiveRange.ToDay,ArchiveRange.OneWeek,ArchiveRange.OneMonth,ArchiveRange.HalfYear,ArchiveRange.All};
		for(int i = range.ordinal()+1;i<ranges.length;i++)
		{
			widget.add(createWidget4Range(ranges[i]));
		}
		return widget;
	}
	
	private FocusHTMLPanel createWidget4Range(final ArchiveRange range)
	{
		FocusHTMLPanel panel = new FocusHTMLPanel("<span>"+rangeString(range)+"</span>");
		panel.setStyleName("ijab-archive-bottom-button ui-corner-all");
		panel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				viewRangeArchive(range);
			}
		});
		return panel;
	}
	
	private HTML createArchiveMessageWidget(String nick,String message,final String dateTime)
	{
		if(message == null)
			message = "";
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
}
