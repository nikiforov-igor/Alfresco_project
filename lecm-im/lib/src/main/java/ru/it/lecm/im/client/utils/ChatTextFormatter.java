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

public class ChatTextFormatter 
{
	public static String format(final String messageOrig) 
	 {
	        String message = messageOrig;
	        message = escapeHtmlLight(message);
	        message = message.replaceAll("\n", "<br>\n");
	        message = formatUrls(message);
	        message = formatEmoticons(message);

	        return message;
	 }
	 
	 static String formatUrls(String message) 
	 {
	        return message = message.replaceAll(TextUtils.URL_REGEXP, "<a href=\"$1\" target=\"_blank\">$1</a>");
	 }
	    
	 public static String escapeHtmlLight(String textOrig)
	 {
		 String text = textOrig;
		 text = text.replaceAll("&", "&amp;");
		 text = text.replaceAll("\"", "&quot;");
		 text = text.replaceAll("<", "&lt;");
		 text = text.replaceAll(">", "&gt;");
		 return text;
	 }
	
	static String preFormatEmoticons(String message) 
	{
		message = replace(message, new String[]{":-\\$",":\\$"}, "KuneProtIniConfusedKuneProtEnd");
		message = replace(message, new String[]{"\\*DRINK\\*"}, "KuneProtIniDrinkKuneProtEnd");
		message = replace(message, new String[]{"@}-&gt;--","@&gt;-&gt;--","@&gt;-&gt;-","@}-&gt;-"}, "KuneProtIniRedRoseKuneProtEnd");
		message = replace(message, new String[]{"\\[:-}","\\[:-\\)","\\[:}","\\[:\\)"}, "KuneProtIniHeadphoneKuneProtEnd");
		message = replace(message, new String[]{"\\*STOP\\*"}, "KuneProtIniStopandtalktothehandKuneProtEnd");
		message = replace(message, new String[]{"\\*KISSING\\*",":\\*\\*:"}, "KuneProtIniTwoKissingKuneProtEnd");
		message = replace(message, new String[]{":-!",":!"}, "KuneProtIniSickKuneProtEnd");
		message = replace(message, new String[]{"8-\\)","8\\)","B-\\)","B\\)"}, "KuneProtIniCoolKuneProtEnd");
		message = replace(message, new String[]{"@="}, "KuneProtIniBombKuneProtEnd");
		message = replace(message, new String[]{":-\\(",":\\("}, "KuneProtIniSadKuneProtEnd");
		message = replace(message, new String[]{":-P",":P"}, "KuneProtIniTonguestickingoutKuneProtEnd");
		message = replace(message, new String[]{":-D",":D",":\\)\\)",":-\\)\\)"}, "KuneProtIniLaughingKuneProtEnd");
		message = replace(message, new String[]{"O:-\\)","O:\\)"}, "KuneProtIniAngelKuneProtEnd");
		message = replace(message, new String[]{":-\\)",":\\)","=\\)"}, "KuneProtIniHappyKuneProtEnd");
		message = replace(message, new String[]{"&gt;:\\)"}, "KuneProtIniDevilKuneProtEnd");
		message = replace(message, new String[]{":-\\{}",":\\{}"}, "KuneProtIniKissKuneProtEnd");
		message = replace(message, new String[]{"\\*KISSED\\*"}, "KuneProtIniKissedKuneProtEnd");
		message = replace(message, new String[]{"=-o","=-O"}, "KuneProtIniShockedKuneProtEnd");
		message = replace(message, new String[]{"\\*TIRED\\*"}, "KuneProtIniYawningKuneProtEnd");
		message = replace(message, new String[]{":-\\\\",":\\\\",":/",":-/"}, "KuneProtIniPensiveKuneProtEnd");
		message = replace(message, new String[]{":'-\\(",":'\\("}, "KuneProtIniCryingKuneProtEnd");
		message = replace(message, new String[]{"\\*THUMBS UP\\*"}, "KuneProtIniThumbsUpKuneProtEnd");
		message = replace(message, new String[]{":-\\["}, "KuneProtIniEmbarassedKuneProtEnd");
		message = replace(message, new String[]{"O:\\)", "o:\\)", "o:-\\)", "O:-\\)", "0:\\)", "0:-\\)"}, "KuneProtIniAngryKuneProtEnd");
		message = replace(message, new String[]{":-X",":X"}, "KuneProtIniSilentKuneProtEnd");
		message = replace(message, new String[]{"\\*JOKINGLY\\*"}, "KuneProtIniJokingKuneProtEnd");
		message = replace(message, new String[]{";-\\)",";\\)"}, "KuneProtIniWinkingKuneProtEnd");
		message = replace(message, new String[]{"\\*IN LOVE\\*"}, "KuneProtIniInLoveKuneProtEnd");
		return message;
	}
	
	 private static String formatEmoticons(String message) 
	 {
		 message = preFormatEmoticons(message);
		 message = message.replaceAll("KuneProtIniConfusedKuneProtEnd", getImgSrcHtml("confused.gif"));
		 message = message.replaceAll("KuneProtIniDrinkKuneProtEnd", getImgSrcHtml("drink.gif"));
		 message = message.replaceAll("KuneProtIniRedRoseKuneProtEnd", getImgSrcHtml("rose.gif"));
		 message = message.replaceAll("KuneProtIniHeadphoneKuneProtEnd", getImgSrcHtml("headphone.gif"));
		 message = message.replaceAll("KuneProtIniStopandtalktothehandKuneProtEnd", getImgSrcHtml("stop.gif"));
		 message = message.replaceAll("KuneProtIniTwoKissingKuneProtEnd", getImgSrcHtml("kissing.gif"));
		 message = message.replaceAll("KuneProtIniSickKuneProtEnd", getImgSrcHtml("sick.gif"));
		 message = message.replaceAll("KuneProtIniCoolKuneProtEnd", getImgSrcHtml("cool.gif"));
		 message = message.replaceAll("KuneProtIniBombKuneProtEnd", getImgSrcHtml("bomb.gif"));
		 message = message.replaceAll("KuneProtIniSadKuneProtEnd", getImgSrcHtml("sad.gif"));
		 message = message.replaceAll("KuneProtIniTonguestickingoutKuneProtEnd", getImgSrcHtml("tongueout.gif"));
		 message = message.replaceAll("KuneProtIniLaughingKuneProtEnd", getImgSrcHtml("laughing.gif"));
		 message = message.replaceAll("KuneProtIniAngelKuneProtEnd", getImgSrcHtml("angel.gif"));
		 message = message.replaceAll("KuneProtIniHappyKuneProtEnd", getImgSrcHtml("happy.gif"));
		 message = message.replaceAll("KuneProtIniDevilKuneProtEnd", getImgSrcHtml("devil.gif"));
		 message = message.replaceAll("KuneProtIniKissKuneProtEnd", getImgSrcHtml("kiss.gif"));
		 message = message.replaceAll("KuneProtIniKissedKuneProtEnd", getImgSrcHtml("kissed.gif"));
		 message = message.replaceAll("KuneProtIniShockedKuneProtEnd", getImgSrcHtml("shocked.gif"));
		 message = message.replaceAll("KuneProtIniYawningKuneProtEnd", getImgSrcHtml("yawning.gif"));
		 message = message.replaceAll("KuneProtIniPensiveKuneProtEnd", getImgSrcHtml("pensive.gif"));
		 message = message.replaceAll("KuneProtIniCryingKuneProtEnd", getImgSrcHtml("crying.gif"));
		 message = message.replaceAll("KuneProtIniThumbsUpKuneProtEnd", getImgSrcHtml("thumbsup.gif"));
		 message = message.replaceAll("KuneProtIniEmbarassedKuneProtEnd", getImgSrcHtml("embarrassed.gif"));
		 message = message.replaceAll("KuneProtIniAngryKuneProtEnd", getImgSrcHtml("angry.gif"));
		 message = message.replaceAll("KuneProtIniSilentKuneProtEnd", getImgSrcHtml("silent.gif"));
		 message = message.replaceAll("KuneProtIniJokingKuneProtEnd", getImgSrcHtml("jokingly.gif"));
		 message = message.replaceAll("KuneProtIniWinkingKuneProtEnd", getImgSrcHtml("wink.gif"));
		 message = message.replaceAll("KuneProtIniInLoveKuneProtEnd", getImgSrcHtml("inlove.gif"));

		 return message;
		 
	 }
	
	private static String getImgSrcHtml(final String name)
   {
   	return "<img align=\"absmiddle\" src=\""+GWT.getModuleBaseURL()+"images/emot/"+name+"\"/>";
   }
		
	private static String replace(String message, final String[] from, final String to) 
	{
        for (String aFrom : from) {
            message = message.replaceAll("(^|[\\s])" + aFrom + "([\\s]|$)", "$1" + to + "$2");
            // two times for: :) :) :) :)
            message = message.replaceAll("(^|[\\s])" + aFrom + "([\\s]|$)", "$1" + to + "$2");
        }
		return message;
	}

}
