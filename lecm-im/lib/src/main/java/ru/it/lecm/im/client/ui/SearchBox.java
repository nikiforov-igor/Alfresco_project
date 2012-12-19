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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import ru.it.lecm.im.client.XmppProfileManager;
import ru.it.lecm.im.client.utils.i18n;

public class SearchBox extends Composite {

	private static SearchBoxUiBinder uiBinder = GWT
			.create(SearchBoxUiBinder.class);

	interface SearchBoxUiBinder extends UiBinder<Widget, SearchBox> {
	}

	@UiField EmFocusWidget closeWidget;
	@UiField TextBox textBox;
	
	private String SEARCH_TIP = i18n.msg("Search contact...");
	private SuggestOracle oracle;
	private String currentText = null;
	private static int limit = 1000;
	private List<SearchBoxListener> listeners = new ArrayList<SearchBoxListener>();
	private final Callback callback = new Callback() 
	{
		public void onSuggestionsReady(Request request, Response response) {
			showSuggestions(response.getSuggestions());
		}
	};
	
	
	public SearchBox() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		setOracle(new MultiWordSuggestOracle());
		closeWidget.addMouseOverHandler(new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event) {
				closeWidget.addStyleName("ijab-actions-prompt");
			}
		});
		closeWidget.addMouseOutHandler(new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event) {
				closeWidget.removeStyleName("ijab-actions-prompt");
			}
		});
		closeWidget.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				clearSearch();
			}
			
		});
		initTextBox();
	}
	
	private void setOracle(SuggestOracle oracle) 
	{
	    this.oracle = oracle;
	}
	
	public SuggestOracle getSuggestOracle() 
	{
	    return oracle;
	}
	
	public void addListener(SearchBoxListener l)
	{
		listeners.add(l);
	}
	
	public void removeListeners(SearchBoxListener l)
	{
		listeners.remove(l);
	}

	
	private void initTextBox()
	{
		textBox.addStyleName("ijab-gray");
		textBox.setText(SEARCH_TIP);
		textBox.addKeyUpHandler(new KeyUpHandler()
		{
			public void onKeyUp(KeyUpEvent event) 
			{
				if(event.getNativeKeyCode() == 13)
				{
					textBox.cancelKey();
					clearSearch();
					fireOnFinished();
				}
				else if(event.getNativeKeyCode() == 27)
				{
					//clear suggestion and cancel search 
					clearSearch();
				}
				else
				{
					refreshSuggestions();
				}
			}
			
		});
		
		textBox.addFocusHandler(new FocusHandler()
		{
			public void onFocus(FocusEvent event) 
			{
				final String text = textBox.getText();
				if(text!=null&&text.equalsIgnoreCase(SEARCH_TIP))
					textBox.setText("");
				textBox.removeStyleName("ijab-gray");
			}
		});
		
		textBox.addBlurHandler(new BlurHandler()
		{
			public void onBlur(BlurEvent event) 
			{
				final String text = textBox.getText();
				if(text == null||text.length() == 0)
				{
					textBox.setText(SEARCH_TIP);
					textBox.addStyleName("ijab-gray");
				}
			}
		});
	}
	
	private void refreshSuggestions()
	{
		String text = textBox.getText();
		if(text.equals(currentText)||text.equals(SEARCH_TIP))
			return;
		else
			currentText = text;
		showSuggestions(text);
	}
	
	void showSuggestions(String query) 
	{
		if (query.length() == 0) 
		{
			oracle.requestDefaultSuggestions(new Request(null, limit), callback);
			getWidget().removeStyleName("ijab-buddy-search-warning");
			fireOnCancel();
		} else {
			oracle.requestSuggestions(new Request(query, limit), callback);
		}
	}
	
	private void showSuggestions(Collection<? extends Suggestion> suggestions) 
	{
		if(suggestions.size()==0)
		{
			getWidget().addStyleName("ijab-buddy-search-warning");
			return;
		}
		else
			getWidget().removeStyleName("ijab-buddy-search-warning");
		List<String> results = new ArrayList<String>();
		for(Suggestion suggestion:suggestions)
		{
			String string = suggestion.getReplacementString();
			if(string.contains("@")&&!results.contains(string))//it's a jid
				results.add(string);
			else
			{
				for(String key:XmppProfileManager.names.keySet())
				{
					if(XmppProfileManager.names.get(key).equals(string))
					{
						if(!results.contains(key))
							results.add(key);
					}
				}
			}
		}
		fireOnSearch(results);
	}
	
	private void clearSearch()
	{
		getWidget().removeStyleName("ijab-buddy-search-warning");
		textBox.setFocus(false);
		currentText = null;
		textBox.setText(SEARCH_TIP);
		textBox.addStyleName("ijab-gray");
		fireOnCancel();
	}
	
	private void fireOnFinished()
	{
		for(SearchBoxListener l:listeners)
		{
			l.onFinished();
		}
	}
	
	private void fireOnCancel()
	{
		for(SearchBoxListener l:listeners)
		{
			l.onCancel();
		}
	}
	
	private void fireOnSearch(List<String> results)
	{
		for(SearchBoxListener l:listeners)
			l.onSearch(results);
	}

}
