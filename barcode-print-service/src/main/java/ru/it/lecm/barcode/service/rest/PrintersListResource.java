package ru.it.lecm.barcode.service.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.it.lecm.barcode.service.entity.PrinterProperties;
import ru.it.lecm.barcode.service.service.PrintService;

/**
 *
 * @author vlevin
 */
@Component
@Path("printersList")
public class PrintersListResource {

	@Autowired
	private PrintService printService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PrinterProperties> listPrinters() {
		return printService.listPrinters();
	}
}
