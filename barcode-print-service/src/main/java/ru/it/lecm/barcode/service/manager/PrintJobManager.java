package ru.it.lecm.barcode.service.manager;

import javax.print.Doc;
import javax.print.PrintException;
import javax.print.attribute.PrintRequestAttributeSet;

/**
 *
 * @author vmalygin
 */
public interface PrintJobManager {

	/**
	 * Подготовить поток данных к отправке на принтер
	 */
	void prepareData();

	Doc getDoc();

	PrintRequestAttributeSet getPrintRequestAttributeSet();

	/**
	 * закрыть все потоки и удалить все файлы
	 */
	void closeAll();

	void print() throws PrintException;
}
