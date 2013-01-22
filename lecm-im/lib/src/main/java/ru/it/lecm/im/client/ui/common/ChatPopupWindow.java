package ru.it.lecm.im.client.ui.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.it.lecm.im.client.ui.SimpleFocusWidget;
import ru.it.lecm.im.client.ui.listeners.ButtonPopupWindowListener;
import ru.it.lecm.im.client.utils.i18n;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 21.01.13
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */

public class ChatPopupWindow extends Composite {

    private static ChatPopupWindowUiBinder uiBinder = GWT
            .create(ChatPopupWindowUiBinder.class);

    interface ChatPopupWindowUiBinder extends UiBinder<Widget, ChatPopupWindow> {
    }

    @UiField Element closeElement;
    @UiField Element closePicElement;
    @UiField Element captionElement;
    @UiField FlowPanel contentWrap;

    private SimpleFocusWidget closeButton;

    private Widget contentWidget;
    private List<ButtonPopupWindowListener> listeners = new ArrayList<ButtonPopupWindowListener>();

    public ChatPopupWindow()
    {
        initWidget(uiBinder.createAndBindUi(this));

        closeButton = new SimpleFocusWidget(closeElement);
        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                clickClose();
            }
        });
        closeButton.addMouseOverHandler(new MouseOverHandler()
        {
            public void onMouseOver(MouseOverEvent event)
            {
                overClose();
            }
        });
        closeButton.addMouseOutHandler(new MouseOutHandler()
        {
            public void onMouseOut(MouseOutEvent event)
            {
                outClose();
            }
        });

        closePicElement.setInnerText(i18n.msg("Close"));
        closeElement.setAttribute("title", i18n.msg("Close"));
    }

    private void fireOnClose()
    {
        for(ButtonPopupWindowListener l:listeners)
        {
            l.onClose();
        }
    }

    private void clickClose()
    {
        fireOnClose();
    }

    private void overClose()
    {
        closePicElement.addClassName("ijab-actions-prompt");
    }

    private void outClose()
    {
        closePicElement.removeClassName("ijab-actions-prompt");
    }

    public void setClosable(boolean closable)
    {
        if(closable)
            closeButton.setVisible(true);
        else
            closeButton.setVisible(false);
    }

    public void addListener(ButtonPopupWindowListener l)
    {
        listeners.add(l);
    }

    public void removeListener(ButtonPopupWindowListener l)
    {
        listeners.remove(l);
    }

    public Widget getWidget()
    {
        return this.contentWidget;
    }

    public void setWidget(Widget widget)
    {
        this.contentWidget = widget;
        contentWrap.add(contentWidget);
    }

    public void setCaption(final String caption)
    {
        captionElement.setInnerText(caption);
    }

    public void ClearButtonPopupWindowListeners()
    {
        listeners.clear();
    }
}
