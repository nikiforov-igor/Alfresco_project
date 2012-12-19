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
import ru.it.lecm.im.client.ui.SearchBox;

import java.util.List;

public abstract class iJabUI implements ClientListener {
    abstract List<Widget> getTopWidgets();

    abstract ContactView getContactView();

    abstract Widget getChatManagerWidget();

    public abstract Widget getOptionWidget();

    public abstract Widget getIndictorWidget();

    public abstract SearchBox getSearchWidget();

    public abstract ChatPanelBar getChatPanelBar();

    abstract void updateOnlineCount(int count);

    abstract void updateTotalCount(int count);

    abstract void connecting();

    abstract void connected();

    abstract void disconnected();

    abstract void setStatusText(final String status);

    abstract String getStatusText();

    abstract void reset();
}
