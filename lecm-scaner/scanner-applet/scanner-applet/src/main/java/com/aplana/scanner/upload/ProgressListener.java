package com.aplana.scanner.upload;

import java.util.EventListener;

/**
 * A progress listener is informed of progress of a lengthy task.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public interface ProgressListener extends EventListener {
	/**
	 * This method is called when task progress is updated.
	 *
	 * @param progress  the task progress value
	 */
	public void progressUpdate(long progress);
}
