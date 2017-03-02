package ru.it.lecm.reports.api;

/**
 * Целевой тип файла в отчётах - подмножество alfresco mime (alfresco\tomcat\webapps\alfresco\WEB-INF\classes\alfresco\mimetype\*.xml).
 */
// TODO: сделать поддерживаемые mime-типы частью провайдера и контролировать это на уровне ReportManager
public enum JasperReportTargetFileType {
	PDF("application/pdf", ".pdf"),
	RTF("application/rtf", ".rtf"),
	DOC("application/msword", ".doc"),
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
	XML("text/xml", ".xml"),
	XLS("application/vnd.ms-excel", ".xls"),
	XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
	ODT("application/vnd.oasis.opendocument.text", ".odt"),
	OTT("application/vnd.oasis.opendocument.text-template", ".ott"),
	ODS("application/vnd.oasis.opendocument.spreadsheet", ".ods"),
	OTS("application/vnd.oasis.opendocument.spreadsheet-template", ".ots");

	final private String mimeType, extension;

	private JasperReportTargetFileType(String mimeType, String extension) {
		this.mimeType = mimeType;
		this.extension = extension;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getExtension() {
		return extension;
	}

	@Override
	public String toString() {
		return super.name()
				+ "["
					+ "mimeType='" + mimeType
					+ "', extension='" + extension + "'"
				+ "]";
	}

	/**
	 * Получить по названию константу перечисления. Регистр символов и незначащие пробелы игнорируются.
	 * @param aname название для преобразования
	 * @param forDefault значение по-умолчанию
	 * @return константу перечисление, если подходящая имеется, или forDefault иначе (в том числе, когда aname = null)
	 */
	static public JasperReportTargetFileType findByName(String aname, JasperReportTargetFileType forDefault) {
		if (aname != null) {
			aname = aname.trim();
			for(JasperReportTargetFileType v: values()) {
				// по совпадению имени,mime типа или расширения - любое из этого уникально...
				if ( aname.equalsIgnoreCase(v.name()) 
						|| aname.equalsIgnoreCase(v.extension) 
						|| aname.equalsIgnoreCase(v.mimeType) )
					return v;
			}
		}
		return forDefault; // using default value
	}
}