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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;

import java.util.ArrayList;
import java.util.Stack;

@SuppressWarnings("deprecation")
public class ChatPanelBar extends ChatPanelBarUI
{
	private static int buttonWidth = 136;
	private int maxVisibleButton = 0;
	
	
	private PanelButton activeButton = null;
	private final MainBar mainBar;
	
	private final Stack<PanelButton> leftStack = new Stack<PanelButton>();
	private final Stack<PanelButton> rightStack = new Stack<PanelButton>();
	private boolean onResume = false;
	public ChatPanelBar(MainBar mainBar)
	{
		super();
		this.mainBar = mainBar;
		//calcVisibleTabs();
		Window.addWindowResizeListener(new WindowResizeListener()
		{
			public void onWindowResized(int width, int height) 
			{
				onWindowResize(width,height);
			}
		});		
	}
	
	private void tryPushButton()
	{
		try
		{

			while(getWidgetCount()>maxVisibleButton)
			{
				if(getWidgetCount() <= 1)
					break;
				//push right first
				if(getWidget(getWidgetCount()-1)!=activeButton)
				{
					ChatPanelButton pushRightButton = (ChatPanelButton)getWidget(getWidgetCount()-1);
					rightStack.push(pushRightButton);
					removeWidget(pushRightButton);
					pushRightButton.closeWindow();
					if(pushRightButton.getOldMessageCount()>0)
						updateMessageCount();
					
				}
				else if(getWidget(0) != activeButton)
				{
					ChatPanelButton pushLeftButton = (ChatPanelButton)getWidget(0);
					leftStack.push(pushLeftButton);
					removeWidget(pushLeftButton);
					pushLeftButton.closeWindow();
					if(pushLeftButton.getOldMessageCount()>0)
						updateMessageCount();
				}
			}
		}
		catch(Exception e)
		{
			/*
			Window.alert("error pushTest");
			Window.alert(e.toString());
			Window.alert(e.getStackTrace().toString());
			*/
		}
	}
	
	private void tryPopButton()
	{
		//pop left first
		try
		{
			if(!leftStack.isEmpty())
			{
				PanelButton leftPopButton = leftStack.pop();
				insertWidget(leftPopButton,0);
				if(leftPopButton.getOldMessageCount()>0)
					updateMessageCount();
				if(getWidgetCount()<maxVisibleButton)
					tryPopButton();
			}
			else if(!rightStack.isEmpty())
			{
				PanelButton rightPopButton = rightStack.pop();
				addWidget(rightPopButton);
				if(rightPopButton.getOldMessageCount()>0)
					updateMessageCount();
				if(getWidgetCount()<maxVisibleButton)
					tryPopButton();
			}
		}
		catch(Exception e)
		{
			/*
			Window.alert("error popTest");
			Window.alert(e.toString());
			Window.alert(e.getStackTrace().toString());
			*/
		}
	}
	
	private void scrollLeft()
	{
		try
		{

			PanelButton pushRightButton = (PanelButton)getWidget(getWidgetCount()-1);
			PanelButton popLeftButton = leftStack.pop();
			removeWidget(pushRightButton);
			rightStack.push(pushRightButton);
			pushRightButton.closeWindow();
			insertWidget(popLeftButton,0);
			if(pushRightButton.getOldMessageCount()>0||popLeftButton.getOldMessageCount()>0)
				updateMessageCount();
			updateScrollButtons();

		}
		catch(Exception e)
		{
			GWT.log("error scrollLeft",null);
			GWT.log(e.toString(),null);
			GWT.log(e.getStackTrace().toString(),null);
		}
	}
	
	private void scrollRight()
	{
		try
		{

			PanelButton pushLeftButton = (PanelButton)getWidget(0);
			PanelButton popRightButton = rightStack.pop();
			
			removeWidget(pushLeftButton);
			leftStack.push(pushLeftButton);
			pushLeftButton.closeWindow();
			addWidget(popRightButton);
			if(pushLeftButton.getOldMessageCount()>0||popRightButton.getOldMessageCount()>0)
				updateMessageCount();
			updateScrollButtons();


		}
		catch(Exception e)
		{
			GWT.log("error scrollRight",null);
			GWT.log(e.toString(),null);
			GWT.log(e.getStackTrace().toString(),null);
		}
	}
	
	private void updateScrollButtons()
	{
		try
		{

			setNextChatCount(leftStack.size());
			setPrevChatCount(rightStack.size());
			setScrollVisible(!(leftStack.isEmpty()&&rightStack.isEmpty()));

		}
		catch(Exception e)
		{
			GWT.log("error updateScrollButtons",null);
			GWT.log(e.toString(),null);
			GWT.log(e.getStackTrace().toString(),null);
		}
	}
	
	protected void onWindowResize(int width, int height) 
	{
		calcVisibleTabs();
		try
		{
			if(getWidgetCount()>maxVisibleButton)
			{
				tryPushButton();
				updateScrollButtons();
			}
			else if(getWidgetCount()<maxVisibleButton)
			{
				tryPopButton();
				updateScrollButtons();
			}
		}
		catch(Exception e)
		{
			/*
			Window.alert("error onWindowSize");
			Window.alert(e.toString());
			Window.alert(e.getStackTrace().toString());
			*/
		}
	}
	
	@Override
	protected void nextClicked() {
		scrollLeft();
	}

	@Override
	protected void prevClicked() {
		scrollRight();
	}
	
	private void calcVisibleTabs()
	{
		int panelMaxWidth = (Window.getClientWidth() - 45)-mainBar.getShortcutBar().getOffsetWidth()-mainBar.getAppsBar().getOffsetWidth()-70;
		maxVisibleButton = 1;//panelMaxWidth/buttonWidth;
	}
	
	
	public ChatPanelButton createChatButton()
	{
		if(maxVisibleButton == 0)
			calcVisibleTabs();
		final ChatPanelButton btn = new ChatPanelButton(this);
		btn.addWidgetListener(new BarButtonListener()
		{
			public void onClose() 
			{
				btn.closeWindow();
				removeWidget(btn);
				tryPopButton();
				updateScrollButtons();

				// It's seek
                // Закоментил Я
//				if(iJabOptions.instance()!=null&&iJabOptions.instance().isAutoClearHistory())
//				{
//					btn.clearHistory();
//				}
			}

			public void onMax() {
			}

			public void onWindowClose() 
			{
				if(activeButton == btn)
					activeButton = null;
			}

			public void onWindowOpen() 
			{
                /* my close
                ArrayList<ChatPanelButton> buttons = getChatButtonsInBar();
                for(ChatPanelButton button:buttons)
                {
                    button.closeWindow();
                }
                     my */

				if(activeButton !=null&&activeButton!=btn)
					activeButton.closeWindow();
				activeButton = btn;


                while(leftStack.contains(activeButton))
				{
					scrollLeft();
				}
				while(rightStack.contains(activeButton))
				{
					scrollRight();
				}

			}
			
		});
		
		addButton(btn);
		return btn;
	}
	
	public MUCPanelButton createMUCButton()
	{
		if(maxVisibleButton == 0)
			calcVisibleTabs();
		final MUCPanelButton btn = new MUCPanelButton(this);
		btn.addWidgetListener(new BarButtonListener()
		{
			public void onClose() 
			{
				btn.closeWindow();
				removeWidget(btn);
				tryPopButton();
				updateScrollButtons();
			}

			public void onMax() {
			}

			public void onWindowClose() 
			{
				if(activeButton == btn)
					activeButton = null;
			}

			public void onWindowOpen() 
			{
				if(activeButton !=null&&activeButton!=btn)
					activeButton.closeWindow();
				activeButton = btn;


                while(leftStack.contains(activeButton))
				{
					scrollLeft();
				}
				while(rightStack.contains(activeButton))
				{
					scrollRight();
				}

			}
			
		});
		
		addButton(btn);
		return btn;
	}
	
	public void addButton(PanelButton btn)
	{

        while(!leftStack.isEmpty())
		{
			scrollLeft();
		}

		insertWidget(btn,0);
		// Закоментил я
		//if(activeButton==null&&!onResume)
		//	btn.openWindow();
		//tryPushButton();
		updateScrollButtons();
	}
	
	public boolean isButtonHide(PanelButton button)
	{


		return leftStack.contains(button)||rightStack.contains(button);

	}
	
	public boolean haveAcitveButton()
	{
		return activeButton!=null;
	}
	
	public void updateMessageCount()
	{
		int leftCount = 0;

        for(PanelButton button:leftStack)
		{
			leftCount += button.getOldMessageCount();
		}

		setNextMsgCount(leftCount);
		
		int rightCount = 0;

		for(PanelButton button:rightStack)
		{
			rightCount += button.getOldMessageCount();
		}

		setPrevMsgCount(rightCount);
	}
	
	public void ensureButtonInBar(PanelButton button)
	{

		if(!leftStack.contains(button)&&!rightStack.contains(button)&&!containsWidget(button))
		{
			addButton(button);

		}

	}
	
	public void reset()
	{
		clear();
		activeButton = null;

        leftStack.clear();
		rightStack.clear();

		updateScrollButtons();
	}

	public ArrayList<ChatPanelButton> getChatButtonsInBar()
	{
		ArrayList<ChatPanelButton> ret = new ArrayList<ChatPanelButton>();

        for(PanelButton b:rightStack)
		{
			if(b instanceof ChatPanelButton)
				ret.add((ChatPanelButton)b);
		}

		for(int index=0;index<chatsContent.getWidgetCount();index++)
		{
			if(chatsContent.getWidget(index) instanceof ChatPanelButton)
				ret.add((ChatPanelButton)chatsContent.getWidget(index));
		}


		for(PanelButton b:leftStack)
		{
			if(b instanceof ChatPanelButton)
				ret.add((ChatPanelButton)b);
		}

		return ret;
	}

	public ChatPanelButton getActiveChatButton()
	{
		if(activeButton instanceof ChatPanelButton)
			return (ChatPanelButton)activeButton;
		else
			return null;
	}

	public void setOnResume(boolean b)
	{
		onResume = b;
	}

}
