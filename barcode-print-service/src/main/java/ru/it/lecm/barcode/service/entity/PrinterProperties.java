package ru.it.lecm.barcode.service.entity;

import java.io.Serializable;

/**
 *
 * @author vlevin
 */
public class PrinterProperties implements Serializable {

	private static final long serialVersionUID = 6039790325492657029L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
