/**
 * PALLADIUM v1.4
 * Description: the session janitor tool
 * Authors: Stefan Strigler, Vanaryon
 * License: GNU GPL
 * Last revision: 24/10/10
 */

package ru.it.lecm.im.bosh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

public class Janitor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(Janitor.class);

    public static final int SLEEPMILLIS = 1000;
	
	private boolean keep_running = true;
	
	public void run() {
		while (this.keep_running) {
			for (Enumeration e = Session.getSessions(); e.hasMoreElements();) {
				Session sess = (Session) e.nextElement();
				
				// Stop inactive sessions
				if (System.currentTimeMillis() - sess.getLastActive() > SessionConstants.MAX_INACTIVITY * 1000) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Session timed out: " + sess.getSID());
                    }
					sess.terminate();
				}
			}
			
			try {
				Thread.sleep(SLEEPMILLIS);
			}
			
			catch (InterruptedException ie) {
                logger.error(ie.getMessage(), ie);
			}
		}
	}
	
	public void stop() {
		this.keep_running = false;
	}
}
