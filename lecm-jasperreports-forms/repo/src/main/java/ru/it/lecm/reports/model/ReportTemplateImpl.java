package ru.it.lecm.reports.model;

import java.io.InputStream;

import ru.it.lecm.reports.api.model.ReportTemplate;

public class ReportTemplateImpl
		extends MnemonicNamedItem
		implements ReportTemplate
{

	private String fileName;
	// NOTE: тут вполне может быть проксик для реальной загрузки данных только во время первовго требования порции данных ("load-on-demand")
	private InputStream dataStream;

	public ReportTemplateImpl() {
		super();
	}

	public ReportTemplateImpl(String fileName, InputStream dataStream) {
		super();
		this.fileName = fileName;
		this.dataStream = dataStream;
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
		return this.dataStream;
	}

	@Override
	public void setData(InputStream stm) {
		this.dataStream = stm;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "ReportTemplateImpl [ ");
		builder.append( String.format( "fileName= '%s'" , fileName));
		builder.append( ", dataStream ");builder.append( dataStream ==  null ? "null" : "assigned");
		builder.append( ", ").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
