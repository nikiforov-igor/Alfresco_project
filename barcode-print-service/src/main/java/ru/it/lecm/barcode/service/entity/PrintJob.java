package ru.it.lecm.barcode.service.entity;

import java.io.Serializable;

/**
 *
 * @author vlevin
 */
public class PrintJob implements Serializable {

	private static final long serialVersionUID = -2533528648585392580L;

	private String printCommand;
	private String printerName;
	private boolean usesPostScript;

	public String getPrintCommand() {
		return printCommand;
	}

	public void setPrintCommand(String printCommand) {
		this.printCommand = printCommand;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public boolean isUsesPostScript() {
		return usesPostScript;
	}

	public void setUsesPostScript(boolean usesPostScript) {
		this.usesPostScript = usesPostScript;
	}

}
