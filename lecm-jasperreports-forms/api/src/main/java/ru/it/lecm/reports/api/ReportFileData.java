package ru.it.lecm.reports.api;

/**
 * Файловый контент, например, результат создания отчёта или сгенерированные данные.
 * @author rabdullin
 */
public interface ReportFileData {

	/**
	 * Бинарный образ файла результата 
	 */
	byte[] getData();
	void setData(byte[] data);

	/**
	 * mime-тип результата
	 * @return
	 */
	String getMimeType();
	void setMimeType(String mimeType);


	/**
	 * Название файла результата
	 * @return
	 */
	String getFilename();
	void setFilename(String filename);

	/**
	 * Название файла результата
	 * @return example: "UTF-8"
	 */
	String getEncoding();
	void setEncoding(String value);

}
