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
package ru.it.lecm.im.client.xmpp.xmpp.xeps.vcard;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.ResponseHandler;
import ru.it.lecm.im.client.xmpp.packet.Packet;
import ru.it.lecm.im.client.xmpp.stanzas.IQ;
import ru.it.lecm.im.client.xmpp.ResponseHandler;

public abstract class VCardResponseHandler implements ResponseHandler {

	public final void onResult(IQ iq) {

		JID jid = JID.fromString(iq.getAtribute("from"));

		Packet vcard = iq.getFirstChild("vCard");
		if(vcard == null)
			return;

		VCard result = new VCard();
		result.setJid(jid);
		Packet fn = vcard.getFirstChild("FN");
		fn = fn==null?vcard.getFirstChild("fn"):fn;
		Packet n = vcard.getFirstChild("N");
		n = n==null?vcard.getFirstChild("n"):n;
		Packet nickname = vcard.getFirstChild("NICKNAME");
		nickname = nickname==null?vcard.getFirstChild("nickname"):nickname;
		Packet adr = vcard.getFirstChild("ADR");
		adr = adr==null?vcard.getFirstChild("adr"):adr;
		Packet title = vcard.getFirstChild("TITLE");
		title = title==null?vcard.getFirstChild("title"):title;

		Packet photo = vcard.getFirstChild("PHOTO");
		photo = photo==null?vcard.getFirstChild("photo"):photo;

		result.setName(fn == null ? null : fn.getCData());
		result.setNickname(nickname == null ? null : nickname.getCData());
		result.setTitle(title == null ? null : title.getCData());

		if (adr != null) {
			Packet ctry = adr.getFirstChild("CTRY");
			ctry = ctry==null?adr.getFirstChild("ctry"):ctry;
			Packet locality = adr.getFirstChild("LOCALITY");
			locality=locality==null?adr.getFirstChild("locality"):locality;
			result.setCountry(ctry == null ? null : ctry.getCData());
			result.setLocality(locality == null ? null : locality.getCData());
		}
		if (n != null) {
			Packet family = n.getFirstChild("FAMILY");
			family=family==null?n.getFirstChild("family"):family;
			Packet given = n.getFirstChild("GIVEN");
			given = given==null?n.getFirstChild("given"):given;
			Packet middle = n.getFirstChild("MIDDLE");
			middle = middle==null?n.getFirstChild("middle"):middle;

			result.setNameFamily(family == null ? null : family.getCData());
			result.setNameGiven(given == null ? null : given.getCData());
			result.setNameMiddle(middle == null ? null : middle.getCData());
		}

		if (photo != null) {
			Packet filename = photo.getFirstChild("FILENAME");
			filename = filename==null?photo.getFirstChild("filename"):filename;
			result.setPhotoFileName(filename == null ? null : filename.getCData());
		}

		onSuccess(result);
	}

	public abstract void onSuccess(final VCard vcard);

}
