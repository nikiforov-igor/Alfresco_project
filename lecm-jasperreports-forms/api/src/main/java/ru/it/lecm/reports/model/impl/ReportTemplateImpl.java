package ru.it.lecm.reports.model.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import ru.it.lecm.reports.api.model.ReportTemplate;

public class ReportTemplateImpl
		extends MnemonicNamedItem
		implements ReportTemplate
{
	private static final long serialVersionUID = 1L;


	private String fileName;
	// NOTE: тут вполне может быть проксик для реальной загрузки данных только во время первовго требования порции данных ("load-on-demand")
	// private InputStream dataStream;
	private byte[] data;

	public ReportTemplateImpl() {
		super();
	}

	public ReportTemplateImpl(String fileName, InputStream dataStream) {
		super();
		this.fileName = fileName;
		setData(dataStream);
	}

	public ReportTemplateImpl(String fileName) {
		this( fileName, null);
	}


	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public InputStream getData() {
		// return this.dataStream;
		return (this.data != null) ? new ByteArrayInputStream(this.data) : null;
	}

	@Override
	public void setData(InputStream stm) {
		// this.dataStream = stm;
		if (stm == null) {
			this.data = null;
		} else {
			try {
				this.data = IOUtils.toByteArray(stm);
			} catch (IOException e) {
				throw new RuntimeException("Fail to get data bytes from input stream", e);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "ReportTemplateImpl [ ");
		builder.append( String.format( "fileName= '%s'" , fileName));
		builder.append( ", data ");builder.append( data ==  null ? "null" : data.length + " bytes");
		builder.append( ", ").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
