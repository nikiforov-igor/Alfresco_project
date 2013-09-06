package ru.it.lecm.reports.api.model;

import java.io.Serializable;

import ru.it.lecm.reports.api.ReportFileData;

public class ReportFileDataImpl implements ReportFileData, Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] data;
	private String mimeType, filename, encoding;

	public ReportFileDataImpl() {
		this.encoding = "UTF-8"; // by default
		this.mimeType = "text/html";
	}

	/** Бинарный образ файла результата */
	@Override
	public byte[] getData() {
		return data;
	}

	/** Бинарный образ файла результата */
	@Override
	public void setData(byte[] data) {
		this.data = data;
	}

	/** mime-тип результата */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/** mime-тип результата */
	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/** Название файла результата */
	@Override
	public String getFilename() {
		return filename;
	}

	/** Название файла результата */
	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/** кодировка, "UTF-8" по-умолчанию */
	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public void setEncoding(String value) {
		this.encoding = value;
	}

}
