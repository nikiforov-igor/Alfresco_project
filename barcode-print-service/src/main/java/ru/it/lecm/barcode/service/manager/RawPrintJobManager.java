package ru.it.lecm.barcode.service.manager;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import org.apache.commons.io.IOUtils;
import ru.it.lecm.barcode.service.entity.PrintJob;

/**
 *
 * @author vmalygin
 */
public class RawPrintJobManager extends AbstractPrintJobManager {

	// начало команды печати и кодировка (cp1251)
	private final static String PRINT_COMMAND_PREFIX = "\nN\nI8,C,001\n";
	// количество экземпляров этикетки
	private final static String PRINT_COMMAND_SUFFIX = "\nP1\n";
	private final static Charset defaultCharset = Charset.forName("Cp1251");

	public RawPrintJobManager(final PrintJob printJob, final PrintService printer) {
		super(printJob, printer);
	}

	@Override
	public void prepareData() {
		byte[] printBytes = (PRINT_COMMAND_PREFIX + printJob.getPrintCommand() + PRINT_COMMAND_SUFFIX).getBytes(defaultCharset);

		dataInputStream = new ByteArrayInputStream(printBytes);
		doc = new SimpleDoc(printBytes, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
	}

	@Override
	public void closeAll() {
		IOUtils.closeQuietly(dataInputStream);
	}
}
