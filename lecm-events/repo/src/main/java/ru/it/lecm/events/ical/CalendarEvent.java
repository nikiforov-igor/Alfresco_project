package ru.it.lecm.events.ical;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vkuprin
 */
public class CalendarEvent {
	private String uid;
	private String title;
	private String initiatorName;
	private String initiatorMail;
	private Map<String,String> attendees = new HashMap<>();
	private String summary;
	private String place;
	private Calendar startTime;
	private Calendar endTime;
	private Boolean fullDay;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public String getInitiatorMail() {
		return initiatorMail;
	}

	public void setInitiatorMail(String initiatorMail) {
		this.initiatorMail = initiatorMail;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Boolean isFullDay() {
		return fullDay;
	}

	public void setFullDay(Boolean fullDay) {
		this.fullDay = fullDay;
	}

	public Map<String, String> getAttendees() {
		return Collections.unmodifiableMap(attendees);
	}

	public void addAttendee(String name, String mail) {
		attendees.put(name, mail);
	}

	public void removeAttendee(String name) {
		attendees.remove(name);
	}
	
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
}
