package ru.it.lecm.im.client.ui.abstraction;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import ru.it.lecm.im.client.ui.ContactView;
import ru.it.lecm.im.client.ui.SearchBox;
import ru.it.lecm.im.client.ui.UserIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 07.12.12
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public interface IBarMainWidget extends HasVisibility, EventListener, HasAttachHandlers, IsWidget, IsRenderable {
    ContactView getContactView();

    UserIndicator getIndictorWidget();

    SearchBox getSearchWidget();

    void setDisconnected(boolean b);

    void removeToolBar();

    void setToolBarListener(IToolBarListener listener);

    public interface IToolBarListener
    {
        void addButtonClicked();
        void manageButtonClicked();
    }
}
