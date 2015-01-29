package ru.it.lecm.barcode.service.rest;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.it.lecm.barcode.service.entity.PrintJob;
import ru.it.lecm.barcode.service.entity.PrintResult;
import ru.it.lecm.barcode.service.service.PrintService;

/**
 * @author vlevin
 */
@Component
@Path("print")
public class PrintResource {

	@Autowired
	private PrintService printService;

	@Resource(name = "producerTemplate")
	private JmsTemplate jmsTemplate;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PrintResult createPrintJob(PrintJob content) {
		PrintResult result = new PrintResult();

		if (content.getPrinterName() == null) {
			result.setSuccess(false);
			result.setErrorMessage("Mandatory parameter PrinterName in not provided");
		} else if (content.getPrintCommand() == null) {
			result.setSuccess(false);
			result.setErrorMessage("Mandatory parameter PrintCommand in not provided");
		} else {

			if (printService.isPrinterAvailable(content.getPrinterName())) {
				jmsTemplate.convertAndSend(content);
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setErrorMessage("Can not find printer with name " + content.getPrinterName());
			}

		}
		return result;
	}
}
