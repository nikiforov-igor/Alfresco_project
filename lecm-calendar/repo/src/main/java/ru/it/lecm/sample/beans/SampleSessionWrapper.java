package ru.it.lecm.sample.beans;

import org.alfresco.repo.processor.BaseProcessorExtension;

/**
 *
 * @author VLadimir Malygin
 * @since 08.10.2012 10:05:42
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SampleSessionWrapper extends BaseProcessorExtension implements ISampleSession {

	private ISampleSession sampleSession;

	public void setSampleSession (ISampleSession sampleSession) {
		this.sampleSession = sampleSession;
	}

	@Override
	public String getSessionInfo () {
		return sampleSession.getSessionInfo ();
	}

}
