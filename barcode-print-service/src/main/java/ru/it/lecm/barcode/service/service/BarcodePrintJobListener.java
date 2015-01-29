package ru.it.lecm.barcode.service.service;

import javax.print.DocPrintJob;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vmalygin
 */
public class BarcodePrintJobListener implements PrintJobListener {

	private final static Logger logger = LoggerFactory.getLogger(BarcodePrintJobListener.class);

	@Override
	public void printDataTransferCompleted(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] data transfer completed for {}", eventType, getPrintJobAttributes(attrs));
	}

	@Override
	public void printJobCompleted(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] print job completed for {}", eventType, getPrintJobAttributes(attrs));
	}

	@Override
	public void printJobFailed(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] print job failed for {}", eventType, getPrintJobAttributes(attrs));
	}

	@Override
	public void printJobCanceled(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] print job canceled for {}", eventType, getPrintJobAttributes(attrs));
	}

	@Override
	public void printJobNoMoreEvents(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] print job has no more events for {}", eventType, getPrintJobAttributes(attrs));
	}

	@Override
	public void printJobRequiresAttention(PrintJobEvent pje) {
		DocPrintJob job = pje.getPrintJob();
		PrintJobAttributeSet attrs = job.getAttributes();
		int eventCode = pje.getPrintEventType();
		String eventType = getPrintEventType(eventCode);
		logger.debug("[{}] print job requires attention for {}", eventType, getPrintJobAttributes(attrs));
	}

	private String getPrintEventType(int printEventType) {
		String eventType;
		switch (printEventType) {
			case PrintJobEvent.DATA_TRANSFER_COMPLETE:
				eventType = "DATA_TRANSFER_COMPLETE";
				break;
			case PrintJobEvent.JOB_CANCELED:
				eventType = "JOB_CANCELED";
				break;
			case PrintJobEvent.JOB_COMPLETE:
				eventType = "JOB_COMPLETE";
				break;
			case PrintJobEvent.JOB_FAILED:
				eventType = "JOB_FAILED";
				break;
			case PrintJobEvent.NO_MORE_EVENTS:
				eventType = "NO_MORE_EVENTS";
				break;
			case PrintJobEvent.REQUIRES_ATTENTION:
				eventType = "REQUIRES_ATTENTION";
				break;
			default:
				eventType = "UNKNOWN_EVENT";
				break;
		}
		return eventType;
	}

	private String getPrintJobAttributes(PrintJobAttributeSet attrs) {
		StringBuilder sb = new StringBuilder();
		if (attrs != null) {
			sb.append('{');
			for (Attribute attr : attrs.toArray()) {
				sb.append('[');
				sb.append(attr.getName());
				sb.append("; ");
				sb.append(attr.toString());
				sb.append(']');
			}
			sb.append('}');
		}
		return sb.toString();
	}
}
