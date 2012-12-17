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
 * Mar 25, 2010
 */
package ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving;

import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

/**
 * @author "Fanglin Zhong<zhongfanglin@gmail.com>"
 *
 */
public abstract class CollectionHandler implements ResponseHandler 
{

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.ResponseHandler#onError(ru.it.lecm.im.client.xmpp.stanzas.IQ, ru.it.lecm.im.client.xmpp.ResponseHandler.ErrorType, ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition, java.lang.String)
	 */
	public void onError(IQ iq, ErrorType errorType,
			ErrorCondition errorCondition, String text) 
	{
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.im.client.xmpp.ResponseHandler#onResult(ru.it.lecm.im.client.xmpp.stanzas.IQ)
	 */
	public void onResult(IQ iq) 
	{
		onSuccess(iq,CollectionResultSet.createResultSetFromIQ(iq));
	}
	
	public abstract void onSuccess(final IQ iq,CollectionResultSet res);

}
