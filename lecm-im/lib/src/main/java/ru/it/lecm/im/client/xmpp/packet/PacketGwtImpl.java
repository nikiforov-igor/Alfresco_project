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

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bmalkow
 * 
 */
public class PacketGwtImpl implements Packet {

	private final Element element;

	public PacketGwtImpl(Element element) {
		this.element = element;
	}

	public Packet addChild(String nodeName, String xmlns) {
		final Element child = element.getOwnerDocument().createElement(nodeName);
		element.appendChild(child);
		return new PacketGwtImpl(child);
	}

	public String getAsString() {
		return this.element.toString();
	}

	public String getAtribute(String attrName) {
		return element.getAttribute(attrName);
	}

	public String getCData() {
		Node x = element.getFirstChild();
		if (x != null) {
			//return x.getNodeValue();
            return getNodeValue(element.getChildNodes());
		}
		return null;
	}

    private String getNodeValue(NodeList nodes)
    {
        StringBuilder sb = new StringBuilder();
        int count = nodes.getLength();
        for (int i = 0; i < count; i++)
        {
            Node node = nodes.item(i);
            if ("br".equals(node.getNodeName()))
            {
                sb.append('\n');
            }
            else{
                sb.append(node.getNodeValue());
            }
        }

        return sb.toString();
    }

	public List<? extends Packet> getChildren() {
		NodeList nodes = this.element.getChildNodes();
		ArrayList<PacketGwtImpl> result = new ArrayList<PacketGwtImpl>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				PacketGwtImpl gpi = new PacketGwtImpl((Element) node);
				result.add(gpi);
			}
		}
		return result;
	}

	public Packet getFirstChild(String name) {
		NodeList nodes = this.element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				PacketGwtImpl gpi = new PacketGwtImpl((Element) node);
				if (name.equals(gpi.getName())) {
					return gpi;
				}
			}
		}
		return null;
	}

	public String getName() {
		String n = this.element.getNodeName();
		return n;
	}

	public void removeChild(Packet packet) {
		if (packet instanceof PacketGwtImpl) {
			this.element.removeChild(((PacketGwtImpl) packet).element);
		} else {
			Packet x = getFirstChild(packet.getName());
			this.element.removeChild(((PacketGwtImpl) x).element);
		}
	}

	public void setAttribute(String attrName, String value) {
		this.element.setAttribute(attrName, value);
	}

	public void setCData(String cdata) {
		final NodeList nodes = element.getChildNodes();
		for (int index = 0; index < nodes.getLength(); index++) {
			final Node child = nodes.item(index);
			if (child.getNodeType() == Node.TEXT_NODE) {
				element.removeChild(child);
			}
		}
		element.appendChild(element.getOwnerDocument().createTextNode(cdata));
	}

	@Override
	public String toString() {
		return getAsString();
	}
	
	public Element getElement()
	{
		return this.element;
	}

}
