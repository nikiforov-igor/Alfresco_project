package com.aplana.scanner;

import java.util.Locale;

/**
 * {@link RuntimeException} with localized message.
 * 
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public class LocalizedException extends RuntimeException {
	private static final long serialVersionUID = 1132879087339185563L;
	
	private String code;

	/**
	 * Constructs a new {@link LocalizedException} with specified detail message.
	 *
	 * @param code    the code for the localized error message
	 * @param message the default error message
	 */
	public LocalizedException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	/**
	 * Constructs a new {@link LocalizedException} with specified detail message and nested
	 * {@link Throwable}.
	 *
	 * @param code    the code for the localized error message
	 * @param message the default error message
	 * @param cause   the exception or error that caused this exception to be
	 *                thrown
	 */
	public LocalizedException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	/**
	 * Creates a localized description of this exception for the default locale.
	 * 
	 * @return the localized message
	 */
	@Override
	public String getLocalizedMessage() {
		String message = ScannerApplet.getMessage(code);
		return message != null ? message : getMessage();
	}

	/**
	 * Creates a localized description of this exception.
	 * 
	 * @param  locale  the locale to localize the message
	 * @return the localized message
	 */
	public String getLocalizedMessage(Locale locale) {
		String message = ScannerApplet.getMessage(code, locale);
		return message != null ? message : getMessage();
	}
}
