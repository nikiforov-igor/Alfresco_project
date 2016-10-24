/*
 * tigase-xmpp4gwt
 * Copyright (C) 2007-2008 "Bartosz Małkowski" <bmalkow@tigase.org>
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
package ru.it.lecm.im.client.xmpp.packet;

import ru.it.lecm.im.client.xmpp.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bmalkow
 * 
 */
public class PacketImp implements Packet {

	private final HashMap<String, String> attributes = new HashMap<String, String>();

	private String cData;

	private final ArrayList<Packet> children = new ArrayList<Packet>();

	private String name;

	public PacketImp(final String name) {
		this(name, null);
	}

	public PacketImp(final String name, final String xmlns) {
		this.name = name;
		if (xmlns != null) {
			setAttribute("xmlns", xmlns);
		}
	}

	public void addChild(final Packet packet) {
		if (packet != null)
			this.children.add(packet);
	}

	public Packet addChild(String nodeName, String xmlns) {
		PacketImp child = new PacketImp(nodeName, xmlns);
		this.children.add(child);
		return child;
	}

	public String getAsString() {
		StringBuilder result = new StringBuilder("<" + name);

		for (Map.Entry<String, String> attr : this.attributes.entrySet()) {
			String x = " " + attr.getKey() + "='" + TextUtils.escape(attr.getValue()) + "'";
			result.append(x);
		}

		if (children.size() > 0) {
			result.append(">");
			for (Packet child : this.children) {
				result.append(child.getAsString());
			}
			result.append("</").append(name).append(">");
		} else if (cData != null) {
			result.append(">");
			result.append(TextUtils.escape(cData));
			result.append("</").append(name).append(">");
		} else {
			result.append("/>");
		}

		return result.toString();
	}

	public String getAtribute(String attrName) {
		return this.attributes.get(attrName);
	}

	public String getCData() {
		return this.cData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.it.lecm.im.client.xmpp.packet.Packet#getChildren()
	 */
	public List<? extends Packet> getChildren() {
		return this.children;
	}

	public Packet getFirstChild(String name) {
		for (Packet child : this.children) {
			if (name.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}

	public String getName() {
		return this.name;
	}

	public void removeChild(Packet packet) {
		this.children.remove(packet);
	}

	public void setAttribute(String attrName, String value) {
		if (value == null) {
			this.attributes.remove(attrName);
		} else {
			this.attributes.put(attrName, value);
		}
	}

	public void setCData(String cdata) {
		this.cData = cdata;
	}

	@Override
	public String toString() {
		return getAsString();
	}

}
