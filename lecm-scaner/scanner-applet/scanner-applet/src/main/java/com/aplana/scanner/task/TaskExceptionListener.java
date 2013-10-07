package com.aplana.scanner.task;

import java.util.EventListener;

/**
 * An exception listener is notified of internal {@link Task} exceptions.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public interface TaskExceptionListener extends EventListener {
	/**
	 * This method is called when an exception was caught when performing a task's
	 * <code>doInBackground</code> method.
	 *
	 * @param source  the object that caught the exception
	 * @param t       the <code>Throwable</code> that was caught
	 */
	public void exceptionThrown(Object source, Throwable t);
}
