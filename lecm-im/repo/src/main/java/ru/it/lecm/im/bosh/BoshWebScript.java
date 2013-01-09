package ru.it.lecm.im.bosh;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 09.01.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class BoshWebScript extends DeclarativeWebScript {

    @Override
    protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("model", "<?xml version='1.0' encoding='UTF-8'?>\\n<message>Hello world!</message>");
        return model;
    }
}
