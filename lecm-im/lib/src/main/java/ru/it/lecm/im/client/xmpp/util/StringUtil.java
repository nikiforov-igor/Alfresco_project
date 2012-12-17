package ru.it.lecm.im.client.xmpp.util;

public class StringUtil 
{
	public static String[] splitString(final String str,int size)
	{
		int count = str.length()/size +1;
		String[] ret = new String[count];
		for(int index = 0;index<count;index++)
		{
			int begin = index*size;
			int end = (index+1)*size;
			if(end>str.length())
				end = str.length();
			ret[index] = str.substring(begin,end);
		}
		return ret;
	}
	
	public static String jid2name(final String jid)
	{
		if(jid.contains("@"))
		{
			String temp = jid.substring(0, jid.indexOf("@"));
			if(temp.contains("%"))
				return temp.substring(0, temp.indexOf("%"));
			else
				return temp;
		}
		else
			return jid;
	}
}
