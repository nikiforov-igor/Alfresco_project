package ru.it.lecm.base.scripts;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.LecmModelsService;

import java.io.IOException;

/**
 *
 * Created by AZinovin on 19.12.13.
 */
public class RestoreModel  extends AbstractWebScript {
    private LecmModelsService lecmModelsService;

    public void setLecmModelsService(LecmModelsService lecmModelsService) {
        this.lecmModelsService = lecmModelsService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String modelName = req.getParameter("modelName");
        lecmModelsService.restoreDefaultModel(modelName);
    }
}
