package ru.it.lecm.im.client.ui.abstraction;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import ru.it.lecm.im.client.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 07.12.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public interface IMainBar extends HasVisibility, EventListener, HasAttachHandlers, IsWidget, IsRenderable {
    void addShortcutItem(String url, String target, String tipStr, String icon);

    void addShortcutItem(String url, String tipStr, String icon);

    ShortcutBar getShortcutBar();

    AppsBar getAppsBar();

    ContactView getContactView();

    SearchBox getSearchWidget();

    UserIndicator getIndictorWidget();

    OptionWidget getConfigWidget();

    ChatPanelBar getChatPanel();

    void updateOnlineCount(int online);

    void updateContactCount(int total);

    void reset();

    void connecting();

    void disconnected();

    void connected();
}
