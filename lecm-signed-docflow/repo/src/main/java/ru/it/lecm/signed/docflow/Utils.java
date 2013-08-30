package ru.it.lecm.signed.docflow;

import java.io.File;
import java.io.IOException;
import javax.xml.ws.Holder;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import ucloud.gate.proxy.exceptions.EResponseType;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 *
 * @author vlevin
 */
public final class Utils {

	private Utils() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of Utils class.");
	}

	public static File createTmpDir() {
		int x = (int) (Math.random() * 1000000);
		String s = System.getProperty("java.io.tmpdir");
		File checkExists = new File(s);
		if (!checkExists.exists() || !checkExists.isDirectory()) {
			throw new RuntimeException("The directory "
					+ checkExists.getAbsolutePath()
					+ " does not exist, please set java.io.tempdir"
					+ " to an existing directory");
		}
		if (!checkExists.canWrite()) {
			throw new RuntimeException("The directory "
					+ checkExists.getAbsolutePath()
					+ " is now writable, please set java.io.tempdir"
					+ " to an writable directory");
		}
		File newTmpDir = new File(s, "alfresco-signed-docflow-tmp-" + x);
		while (!newTmpDir.mkdir()) {
			x = (int) (Math.random() * 1000000);
			newTmpDir = new File(s, "alfresco-signed-docflow-tmp-" + x);
		}

		return newTmpDir;
	}

	/**
	 * преобразование контента ноды в массив байт
	 * @param reader Represents a handle to read specific content. Content may only be accessed once per instance.
	 * @return массив байт контента ноды
	 * @throws ContentIOException
	 */
	public static byte[] contentToByteArray(ContentReader reader) {
		byte[] content;
		try {
			content = IOUtils.toByteArray(reader.getContentInputStream());
		} catch(IOException ex) {
			throw new ContentIOException(ex.getMessage(), ex);
		}
		return content;
	}

	public static void logGateResponse(final Holder<GateResponse> gateResponse, final Logger logger) {
		if (gateResponse.value != null) {
			logGateResponse(gateResponse.value, logger);
		}
	}

	public static void logGateResponse(final GateResponse gateResponse, final Logger logger) {
		logger.debug("message = {}", gateResponse.getMessage());
		logger.debug("operatorMessage = {}", gateResponse.getOperatorMessage());
		logger.debug("responseType = {}", gateResponse.getResponseType());
		logger.debug("stackTrace = {}", gateResponse.getStackTrace());
	}

	public static GateResponse formErrorGateResponse(Exception ex, EResponseType eResponseType) {
		GateResponse gateResponse = new GateResponse();
		gateResponse.setMessage(ex.getMessage());
		gateResponse.setOperatorMessage(null);
		gateResponse.setResponseType(eResponseType);
		gateResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		return gateResponse;
	}
}
