package ru.it.lecm.reports.api.model;

import java.io.InputStream;

public interface ReportTemplate extends L18able {

	String getFileName();
	void setFileName( String fileName);

	InputStream getData();
	void setData( InputStream stm);
}
