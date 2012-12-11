package ru.it.lecm.im.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 07.12.12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class LecmIM implements EntryPoint {
    @Override
    public void onModuleLoad() {
        //To change body of implemented methods use File | Settings | File Templates.

        Button button = new Button("Btn");
        RootPanel.get().add(new TextBox());
        RootPanel.get().add(button);

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.alert("Hello!");
            }
        });

    }
}
