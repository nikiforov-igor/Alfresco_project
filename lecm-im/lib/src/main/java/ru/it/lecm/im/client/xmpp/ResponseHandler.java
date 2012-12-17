/*
 * tigase-xmpp4gwt
 * Copyright (C) 2007 "Bartosz Małkowski" <bmalkow@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package ru.it.lecm.im.client.xmpp;

import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.xmpp.ErrorCondition;

public interface ResponseHandler {

	public static enum ErrorType {
		/** retry after providing credentials */
		AUTH,
		/** do not retry (the error is unrecoverable) */
		CANCEL,
		/** proceed (the condition was only a warning) */
		CONTINUE,
		/** retry after changing the data sent */
		MODIFY,
		/** retry after waiting (the error is temporary) */
		WAIT
	}

	public void onError(IQ iq, ErrorType errorType, ErrorCondition errorCondition, String text);

	public void onResult(IQ iq);

}
