package ru.it.lecm.statemachine;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.calendar.DurationHelper;

import java.util.Date;

import ru.it.lecm.wcalendar.IWorkCalendar;

public class LecmBusinessCalendar  implements BusinessCalendar {
	  
	  public static String NAME = "duration";
	  private IWorkCalendar workCalendarService = null;
	  
	  public LecmBusinessCalendar() {
		  
	  }
	  
	  public LecmBusinessCalendar(IWorkCalendar workCalendarService) {
		  this.workCalendarService = workCalendarService;
	  }

	  @Override
	  public Date resolveDuedate(String duedate) {
	    try {
	      if(duedate.startsWith("W")) {
	    	  duedate = duedate.replaceFirst("W", "");
	    	  Date finishDate;
	    	  if(workCalendarService!=null) {
	    		  finishDate = workCalendarService.getNextWorkingDate(new Date(), duedate);
	    	  } else {
	    		  DurationHelper dh = new DurationHelper(duedate);
	    		  finishDate = dh.getDateAfter();
	    	  }
	    	  return finishDate;
	      } else {
	    	  DurationHelper dh = new DurationHelper(duedate);
	    	  return dh.getDateAfter();
	      }
	    } catch (Exception e) {
	      throw new ActivitiException("couldn't resolve duedate: "+e.getMessage(), e);
	    }
	  }
}