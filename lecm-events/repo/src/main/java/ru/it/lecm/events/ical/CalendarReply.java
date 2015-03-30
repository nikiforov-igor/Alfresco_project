package ru.it.lecm.events.ical;

/**
 *
 * @author vkuprin
 */
public class CalendarReply {
	private String uid;
	private String attendeeMail;
	private String answer;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAttendeeMail() {
		return attendeeMail;
	}

	public void setAttendeeMail(String attendeeMail) {
		this.attendeeMail = attendeeMail;
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
