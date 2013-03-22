package ru.it.lecm.regnumbers.bean;

import java.io.IOException;
import java.io.Writer;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.regnumbers.RegNumbersService;

/**
 *
 * @author vlevin
 */
public class RegNumWebTest extends AbstractWebScript {

	RegNumbersService regNumbersServise;

	public void setRegNumbersServise(RegNumbersService regNumbersServise) {
		this.regNumbersServise = regNumbersServise;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		
		Writer writer = res.getWriter();
		writer.write(String.format("TEST"));
	}

}
