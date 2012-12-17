package ru.it.lecm.im.client.xmpp.xmpp.xeps.messageArchiving;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.it.lecm.im.client.xmpp.JID;
import ru.it.lecm.im.client.xmpp.JID;

public class ResultSet {

	private List<Item> items = new ArrayList<Item>();

	private Date start;

	private String subject;

	private JID withJid;

	public List<Item> getItems() {
		return items;
	}

	public Date getStart() {
		return start;
	}

	public String getSubject() {
		return subject;
	}

	public JID getWithJid() {
		return withJid;
	}

	void setItems(List<Item> items) {
		this.items = items;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setWithJid(JID withJid) {
		this.withJid = withJid;
	}

}
