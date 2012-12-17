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

public class BarButtonManager extends ButtonManagerBase
{
	public BarButtonManager()
	{
		super();
	}
	
	public BarButton createCaptionButton(final String caption,final String iconStyle,final String buttonStyle)
	{
		BarButton newBtn = createButton();
		newBtn.setButtonText(caption);
		newBtn.setButtonWindowCaption(caption);
		newBtn.setButtonTextEnabled(true);
		newBtn.setTipEnabled(false);
		newBtn.setButtonWindowMaxEnabled(false);
		newBtn.setButtonWindowCloseEnabled(false);
		newBtn.setIconStyle(iconStyle);
		newBtn.addButtonStyle(buttonStyle);
		
		connectBarButton(newBtn);
		return newBtn;
	}
	
	public BarButton createIconButton(final String tip,final String iconStyle)
	{
		BarButton newBtn = createButton();
		newBtn.setButtonWindowCaption(tip);
		newBtn.setButtonTextEnabled(false);
		newBtn.setTipEnabled(true);
		newBtn.setTip(tip);
		newBtn.setButtonWindowMaxEnabled(false);
		newBtn.setButtonWindowCloseEnabled(false);
		newBtn.setIconStyle(iconStyle);
		
		connectBarButton(newBtn);
		return newBtn;
	}
	
	private void connectBarButton(final BarButton btn)
	{
		btn.addWidgetListener(new BarButtonListener()
		{
			public void onClose() {
			}

			public void onMax() {
			}

			public void onWindowClose() {
				if(activeButton == btn)
					activeButton = null;
			}

			public void onWindowOpen() {
				if(activeButton!=null)
					activeButton.closeWindow();
				activeButton = btn;
			}
			
		});
	}
}
