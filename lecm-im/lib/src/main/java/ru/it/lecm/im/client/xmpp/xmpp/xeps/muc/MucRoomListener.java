package ru.it.lecm.im.client.xmpp.xmpp.xeps.muc;

import java.util.List;

public interface MucRoomListener 
{
	void onRoomListUpdate(List<MucRoomItem> rooms);
}
