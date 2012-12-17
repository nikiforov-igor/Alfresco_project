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
package ru.it.lecm.im.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;

public class BrowserHelper 
{
	private static boolean initialized;
	/**
	 * <code>true</code> if the browser uses the webkit engine.
	 */
	public static boolean isWebKit;

	/**
	 * <code>true</code> if the browser is safari.
	 */
	public static boolean isSafari;

	/**
	 * <code>true</code> if the browser is safari2.
	 */
	public static boolean isSafari2;

	/**
	 * <code>true</code> if the browser is safari3.
	 */
	public static boolean isSafari3;

	/**
	 * <code>true</code> if the browser is safari4.
	 */
	public static boolean isSafari4;

	/**
	 * <code>true</code> if the browser is chrome.
	 */
	public static boolean isChrome;

	/**
	 * <code>true</code> if the browser is opera.
	 */
	public static boolean isOpera;

	/**
	 * <code>true</code> if the browser is ie.
	 */
	public static boolean isIE;

	/**
	 * <code>true</code> if the browser is ie6.
	 */
	public static boolean isIE6;

	/**
	 * <code>true</code> if the browser is ie7.
	 */
	public static boolean isIE7;

	/**
	 * <code>true</code> if the browser is ie8.
	 */
	public static boolean isIE8;

	/**
	 * <code>true</code> if the browser is gecko.
	 */
	public static boolean isGecko;

	/**
	 * <code>true</code> if the browser is gecko2.
	 */
	public static boolean isGecko2;

	/**
	 * <code>true</code> if the browser is gecko3.
	 */
	public static boolean isGecko3;

	/**
	 * <code>true</code> if the browser is gecko3.5.
	 */
	public static boolean isGecko35;

	/**
	 * <code>true</code> if the browser is in strict mode.
	 */
	public static boolean isStrict;

	/**
	 * <code>true</code> if using https.
	 */
	public static boolean isSecure;

	/**
	 * <code>true</code> if mac os.
	 */
	public static boolean isMac;

	/**
	 * <code>true</code> if linux os.
	 */
	public static boolean isLinux;

	/**
	 * <code>true</code> if windows os.
	 */
	public static boolean isWindows;

	/**
	 * <code>true</code> if is air.
	 */
	public static boolean isAir;


	/**
	 * <code>true</code> if the browser uses shims.
	 */
	public static boolean useShims;
	
	/**
	 * URL to a blank file used by GXT when in secure mode for iframe src to
	 * prevent the IE insecure content. Default value is 'blank.html'.
	 */
	public static String SSL_SECURE_URL = GWT.getModuleBaseURL() + "blank.html";

	/**
	 * URL to a 1x1 transparent gif image used by GXT to create inline icons with
	 * CSS background images. Default value is '/images/default/shared/clear.gif';
	 */
	public static String BLANK_IMAGE_URL;
	
	/**
	 * Returns the browser's user agent.
	 * 
	 * @return the user agent
	 */
	public native static String getUserAgent() /*-{
	    return $wnd.navigator.userAgent.toLowerCase();
	  }-*/;
	
	public static void init()
	{
		if (initialized) {
			return;
		}
		initialized = true;

		String ua = getUserAgent();

		isOpera = ua.indexOf("opera") != -1;
		isIE = !isOpera && ua.indexOf("msie") != -1;
		isIE7 = !isOpera && ua.indexOf("msie 7") != -1;
		isIE8 = !isOpera && ua.indexOf("msie 8") != -1;
		isIE6 = isIE && !isIE7 && !isIE8;

		isChrome = !isIE && ua.indexOf("chrome") != -1;

		isWebKit = ua.indexOf("webkit") != -1;

		isSafari = !isChrome && ua.indexOf("safari") != -1;
		isSafari3 = isSafari && ua.indexOf("version/3") != -1;
		isSafari4 = isSafari && ua.indexOf("version/4") != -1;
		isSafari2 = isSafari && !isSafari3 && !isSafari4;

		isGecko = !isWebKit && ua.indexOf("gecko") != -1;
		isGecko3 = isGecko && ua.indexOf("rv:1.9.0") != -1;
		isGecko35 = isGecko && ua.indexOf("rv:1.9.1") != -1;
		isGecko2 = isGecko && !isGecko3 && !isGecko35;

		isWindows = (ua.indexOf("windows") != -1 || ua.indexOf("win32") != -1);
		isMac = (ua.indexOf("macintosh") != -1 || ua.indexOf("mac os x") != -1);
		isAir = (ua.indexOf("adobeair") != -1);
		isLinux = (ua.indexOf("linux") != -1);

		useShims = isIE6 || (isMac && isGecko2);

		isStrict = Document.get().isCSS1Compat();


		isSecure = Window.Location.getProtocol().toLowerCase().startsWith("https");
		if (BLANK_IMAGE_URL == null) {
			if (isIE8 || (isGecko && !isSecure)) {
				BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
			} else {
				BLANK_IMAGE_URL = GWT.getModuleBaseURL() + "clear.gif";
			}
		}
	}
}
