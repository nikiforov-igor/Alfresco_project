package ru.it.lecm.reports.api;

/**
 * Целевой тип файла
 */
public enum JasperReportTargetFileType {
	PDF( "application/pdf", ".pdf")
	, RTF( "application/rtf", ".rtf")
	, DOCX( "application/msword", ".docx")
	, XML( "text/xml", ".xml")
	;

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
					+ "mimeType=" + mimeType
					+ ", extension=" + extension
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
				if ( aname.equalsIgnoreCase(v.name()) )
					return v;
			}
		}
		return forDefault; // using default value
	}
}