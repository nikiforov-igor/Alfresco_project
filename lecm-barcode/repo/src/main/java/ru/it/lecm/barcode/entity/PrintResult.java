package ru.it.lecm.barcode.entity;

import java.io.Serializable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author vlevin
 */
public class PrintResult implements Serializable {

	private static final long serialVersionUID = 3349000846577088720L;

	private boolean success;
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	private String errorMessage;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
