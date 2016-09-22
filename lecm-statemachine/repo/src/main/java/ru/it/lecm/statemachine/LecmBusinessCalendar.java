package ru.it.lecm.statemachine;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.calendar.BusinessCalendarImpl;
import org.activiti.engine.impl.calendar.DurationHelper;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.runtime.ClockReader;

import java.util.Date;

import ru.it.lecm.wcalendar.IWorkCalendar;

public class LecmBusinessCalendar  extends BusinessCalendarImpl {
	  
	public static String NAME = "duration";
	private IWorkCalendar workCalendarService = null;
	
	public LecmBusinessCalendar(ClockReader cr) {
		super(cr);
	}
	
	public LecmBusinessCalendar(ClockReader cr, IWorkCalendar workCalendarService) {
		super(cr);
		this.workCalendarService = workCalendarService;
	}

	public Date resolveDuedate(String duedate, int maxIterations) {
		ClockReader cr = Context.getProcessEngineConfiguration().getClock();
	    try {
	      if(duedate.startsWith("W")) {
	    	  duedate = duedate.replaceFirst("W", "");
	    	  Date finishDate;
	    	  if(workCalendarService!=null) {
	    		  finishDate = workCalendarService.getNextWorkingDate(new Date(), duedate);
	    	  } else {
	    		  DurationHelper dh = new DurationHelper(duedate, cr);
	    		  finishDate = dh.getDateAfter();
	    	  }
	    	  return finishDate;
	      } else {
	    	  DurationHelper dh = new DurationHelper(duedate, cr);
	    	  return dh.getDateAfter();
	      }
	    } catch (Exception e) {
	      throw new ActivitiException("couldn't resolve duedate: "+e.getMessage(), e);
	    }
	}
}