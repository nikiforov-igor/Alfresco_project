package ru.it.lecm.events.ical;

import java.net.URI;
import java.net.URL;
import java.util.Date;

/**
 *
 * @author vkuprin
 */
public class CalendarReply {
	private String uid;
	private URI attendeeMail;
	private String answer;
	private Date timeStamp;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAttendeeMail() {
		return attendeeMail.getSchemeSpecificPart();
	}

	public void setAttendeeMail(String attendeeMail) {
		this.attendeeMail = URI.create(attendeeMail);
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append("Reply to uid=").append(uid).append(", attendee mail=").append(attendeeMail).append(", answer is ").append(answer).toString();
	}
	
	
}
