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
package ru.it.lecm.im.client;

import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.ui.ChatPanelBar;
import ru.it.lecm.im.client.ui.ContactView;
import ru.it.lecm.im.client.ui.MainBar;
import ru.it.lecm.im.client.ui.SearchBox;

import java.util.ArrayList;
import java.util.List;

public class BarUI implements ClientListener, VisibilityListener {
	private final MainBar mainBar = new MainBar();
	public BarUI()
	{
        mainBar.setVisible(false);
	}
	public List<Widget> getTopWidgets() 
	{
		List<Widget> topWidgets = new ArrayList<Widget>();
		topWidgets.add(mainBar);
		return topWidgets;
	}
	public ContactView getContactView()
	{
		return mainBar.getContactView();
	}
	public void updateOnlineCount(int count) {
		mainBar.updateOnlineCount(count);		
	}
	public void updateTotalCount(int count) {
		mainBar.updateContactCount(count);
	}
	public Widget getChatManagerWidget() 
	{
		return mainBar.getChatPanel();
	}
	public Widget getOptionWidget() 
	{
		return mainBar.getConfigWidget();
	}

	public void reset() 
	{
		mainBar.reset();
	}
	public void onBeforeLogin() 
	{
		connecting();
		
	}
	public void onEndLogin() 
	{
		connected();
	}
	public void onError(String error) 
	{
		reset();		
	}
	public void onLogout() 
	{
		reset();
	}
	public void onResume() 
	{
		connected();
	}
	public void onSuspend() 
	{
		//no need to do something
	}
	void connected()
	{
		mainBar.connected();
	}
	void connecting()
	{
		mainBar.connecting();
	}
	void disconnected()
	{
		mainBar.disconnected();
	}
	public Widget getIndictorWidget()
	{
		return mainBar.getIndictorWidget();
	}
	public SearchBox getSearchWidget()
	{
		return mainBar.getSearchWidget();
	}
	public void onAvatarClicked(String username, String bareJid) {
	}
	public void onAvatarClicked(int clientX, int clientY, String username,
			String bareJid) {
	}
	public void onAvatarMouseOver(int clientX, int clientY, String username,
			String bareJid) {
	}
	public void onStatusTextUpdated(String text) {
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.iJabUI#setStatusText(java.lang.String)
	 */
    void setStatusText(String status) {
		mainBar.getIndictorWidget().setStatusText(status);
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.iJabUI#getStatusText()
	 */
    String getStatusText() {
		return mainBar.getIndictorWidget().getStatusText();
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onAvatarMouseOut(int, int, java.lang.String, java.lang.String)
	 */
	public void onAvatarMouseOut(int clientX, int clientY, String usrname,
			String bareJid) {
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.iJabUI#getChatPanelBar()
	 */
	public ChatPanelBar getChatPanelBar()
	{
		return mainBar.getChatPanel();
	}
	/* (non-Javadoc)
	 * @see anzsoft.iJab.client.ClientListener#onMessageReceive(java.lang.String, java.lang.String)
	 */
	public void onMessageReceive(String jid, String message) {
		
	}

    @Override
    public void onShow() {
        Log.consoleLog("BarUI.onShow()");
//        mainBar.removeStyleName(".hide");
        mainBar.setVisible(true);

    }

    @Override
    public void onHide() {
        Log.consoleLog("BarUI.onHide()");
        mainBar.setVisible(false);
    }
}
