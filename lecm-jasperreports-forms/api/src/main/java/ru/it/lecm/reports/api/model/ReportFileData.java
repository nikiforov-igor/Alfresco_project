package ru.it.lecm.reports.api.model;

import java.io.Serializable;

public class ReportFileData implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] data;
	private String mimeType, filename, encoding;

	public ReportFileData() {
		this.encoding = "UTF-8"; // by default
		this.mimeType = "text/html";
	}

	/** Бинарный образ файла результата */
	public byte[] getData() {
		return data;
	}

	/** Бинарный образ файла результата */
	public void setData(byte[] data) {
		this.data = data;
	}

	/** mime-тип результата */
	public String getMimeType() {
		return mimeType;
	}

	/** mime-тип результата */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/** Название файла результата */
	public String getFilename() {
		return filename;
	}

	/** Название файла результата */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/** кодировка, "UTF-8" по-умолчанию */
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String value) {
		this.encoding = value;
	}
}
