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
 * Mar 3, 2010
 */
package ru.it.lecm.im.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import ru.it.lecm.im.client.iJab;
import ru.it.lecm.im.client.utils.i18n;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public class LoginDialog extends DialogBox
{
	final private TextBox userInput; 
	final private PasswordTextBox passwordInput;
	
	private static LoginDialog instance = null;
	public static LoginDialog instance()
	{
		if(instance == null)
			instance = new LoginDialog();
		return instance;
	}
	private LoginDialog() 
	{
		final FormPanel panel = new FormPanel();
		panel.addSubmitHandler(new SubmitHandler()
		{
			public void onSubmit(SubmitEvent event) 
			{
				String username = userInput.getValue();
				String password = passwordInput.getValue();
				if(username == null||username.length()==0||password==null||password.length()==0)
				{
					Window.alert(i18n.msg("Username and Password must not empty!"));
					event.cancel();
					return;
				}
				iJab.client.login(username, password);
				passwordInput.setText("");
				hide();
			}
			
		});
		panel.setStyleName("ijab-login-form");
		setText(i18n.msg("Login iJab"));
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		final FlexTable t = new FlexTable();
		t.setText(0, 0, i18n.msg("UserName:"));
		t.setText(1, 0, i18n.msg("Password:"));
		
		userInput = new TextBox();
		passwordInput = new PasswordTextBox();
		
		t.setWidget(0, 1, userInput);
		t.setWidget(1, 1, passwordInput);
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(t);
		
		HorizontalPanel hPanel = new HorizontalPanel();
	    hPanel.setSpacing(5);
	    
	    Button cancelButton = new Button(i18n.msg("Cancel"));
	    cancelButton.addClickHandler(new ClickHandler()
	    {
			public void onClick(ClickEvent event) 
			{
				hide();
			}
	    	
	    });

		Button loginButton = new Button(i18n.msg("Login"));
		loginButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
				panel.submit();
			}
			
		});
		hPanel.add(cancelButton);
		hPanel.add(loginButton);
		vPanel.add(hPanel);
		
		panel.add(vPanel);
		this.setWidget(panel);
		
	}

}
