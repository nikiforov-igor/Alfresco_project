package com.aplana.scanner.upload;

import java.util.Observable;
import java.util.Observer;

/**
 * {@link ProgressListener} 
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public class UploaderProgressListener extends Observable implements ProgressListener {
	private long bytesToSend;
	
	/**
	 * Constructs a listener instance.
	 *
	 * @param observer     the <code>Observer</code> object to be informed of upload progress 
	 * @param bytesToSend  the number of bytes to be sent to the server
	 */
	public UploaderProgressListener(Observer observer, long bytesToSend) {
		// for some reson, twice much bytes is sent to server, so this is ugly patch
		this.bytesToSend = bytesToSend * 2;
		this.addObserver(observer);
	}

	/* (non-Javadoc)
	 * @see com.aplana.scanner.upload.ProgressListener#progressUpdate(long)
	 */
	public void progressUpdate(long progress) {
		this.setChanged();
		this.notifyObservers((int)(progress / bytesToSend * 100));
	}
}
