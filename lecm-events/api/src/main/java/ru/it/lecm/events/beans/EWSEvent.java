package ru.it.lecm.events.beans;

import java.util.Date;

/**
 *
 * @author vmalygin
 */
public class EWSEvent {

	private Date start;
	private Date end;

	public EWSEvent() {
	}

	public EWSEvent(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
