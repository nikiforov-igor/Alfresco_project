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
package ru.it.lecm.im.client.ui.emoticons;

import java.util.ArrayList;
import java.util.List;


public class Emoticons
{
	public List<Emoticon> emoticons = new ArrayList<Emoticon>();
	private static Emoticons instance = null;
	
	static public Emoticons instance()
	{
		if(instance == null)
			instance = new Emoticons();
		return instance;
	}
	private Emoticons()
	{
		emoticons.add(new Emoticon("Confused","confused.gif",new String[]{":-$",":$"}));
		emoticons.add(new Emoticon("Drink","drink.gif",new String[]{"*DRINK*"}));
		emoticons.add(new Emoticon("Red Rose","rose.gif",new String[]{"@}->--","@>->--","@>->-","@}->-"}));
		emoticons.add(new Emoticon("Headphone","headphone.gif",new String[]{"[:-}","[:-)","[:}","[:)"}));
		emoticons.add(new Emoticon("Stop and talk to the hand","stop.gif",new String[]{"*STOP*"}));
		emoticons.add(new Emoticon("Two Kissing","kissing.gif",new String[]{"*KISSING*",":**:"}));
		emoticons.add(new Emoticon("Sick","sick.gif",new String[]{":-!",":!"}));
		emoticons.add(new Emoticon("Cool","cool.gif",new String[]{"8-)","8)","B-)","B)"}));
		emoticons.add(new Emoticon("Bomb","bomb.gif",new String[]{"@="}));
		emoticons.add(new Emoticon("Sad","sad.gif",new String[]{":-(",":("}));
		emoticons.add(new Emoticon("Tongue sticking out","tongueout.gif",new String[]{":-P",":P"}));
		emoticons.add(new Emoticon("Laughing","laughing.gif",new String[]{":-D",":D",":))",":-))"}));
		emoticons.add(new Emoticon("Angel","angel.gif",new String[]{"O:-)","O:)"}));
		emoticons.add(new Emoticon("Happy","happy.gif",new String[]{":-)",":)","=)"}));
		emoticons.add(new Emoticon("Devil","devil.gif",new String[]{">:)"}));
		emoticons.add(new Emoticon("Kiss","kiss.gif",new String[]{":-{}",":{}"}));
		emoticons.add(new Emoticon("Kissed","kissed.gif",new String[]{"*KISSED*"}));
		emoticons.add(new Emoticon("Shocked","shocked.gif",new String[]{"=-o","=-O"}));
		emoticons.add(new Emoticon("Yawning","yawning.gif",new String[]{"*TIRED*"}));
		emoticons.add(new Emoticon("Pensive","pensive.gif",new String[]{":-\\",":\\",":/",":-/"}));
		emoticons.add(new Emoticon("Crying","crying.gif",new String[]{":'-(",":'("}));
		emoticons.add(new Emoticon("Thumbs Up","thumbsup.gif",new String[]{"*THUMBS UP*"}));
		emoticons.add(new Emoticon("Embarassed","embarrassed.gif",new String[]{":-["}));
		emoticons.add(new Emoticon("Angry","angry.gif",new String[]{"O:)", "o:)", "o:-)", "O:-)", "0:)", "0:-)"}));
		emoticons.add(new Emoticon("Silent","silent.gif",new String[]{":-X",":X"}));
		emoticons.add(new Emoticon("Joking","jokingly.gif",new String[]{"*JOKINGLY*"}));
		emoticons.add(new Emoticon("Winking","wink.gif",new String[]{";-)",";)"}));
		emoticons.add(new Emoticon("In Love","inlove.gif",new String[]{"*IN LOVE*"}));
	}
}
