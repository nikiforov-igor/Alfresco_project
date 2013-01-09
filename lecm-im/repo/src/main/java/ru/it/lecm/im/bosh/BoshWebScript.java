package ru.it.lecm.im.bosh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 09.01.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class BoshWebScript extends AbstractWebScript {

    private final static Logger logger = LoggerFactory.getLogger(BoshWebScript.class);

    private PalladiumLogic logic;

    public void setLogic(PalladiumLogic logic) {
        this.logic = logic;
        try {
            logger.info("try to init palladium logic");
            logic.init();
        } catch (ServletException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
        HttpServletRequest request = WebScriptServletRuntime.getHttpServletRequest(req);
        HttpServletResponse response = WebScriptServletRuntime.getHttpServletResponse(res);
        try {
            logic.doPost(request, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    /*
    @Override
    protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> model = new HashMap<String, Object>();
        HttpServletRequest request = WebScriptServletRuntime.getHttpServletRequest(req);
        model.put("model", "<?xml version='1.0' encoding='UTF-8'?>\\n<message>Hello world!</message>");
        return model;
    }
    */
}
