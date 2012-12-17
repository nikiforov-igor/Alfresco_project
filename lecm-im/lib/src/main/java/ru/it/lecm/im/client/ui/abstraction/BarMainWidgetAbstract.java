package ru.it.lecm.im.client.ui.abstraction;

import com.google.gwt.user.client.ui.Composite;
import ru.it.lecm.im.client.ui.ContactView;
import ru.it.lecm.im.client.ui.SearchBox;
import ru.it.lecm.im.client.ui.UserIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 07.12.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class BarMainWidgetAbstract extends Composite implements IBarMainWidget {
    protected abstract void popGateMenu();

    protected abstract String getGatewayName(String jid);

    @Override
public abstract ContactView getContactView();

    @Override
public abstract UserIndicator getIndictorWidget();

    @Override
public abstract SearchBox getSearchWidget();

    @Override
public abstract void setDisconnected(boolean b);

    @Override
public abstract void removeToolBar();

    @Override
public abstract void setToolBarListener(IToolBarListener listener);
}
