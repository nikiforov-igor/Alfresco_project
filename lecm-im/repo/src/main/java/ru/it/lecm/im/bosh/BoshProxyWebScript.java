package ru.it.lecm.im.bosh;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Скрипт для проксирования обращений к BOSH-сервису
 */
public class BoshProxyWebScript extends AbstractWebScript {

    private final static Logger logger = LoggerFactory.getLogger(BoshProxyWebScript.class);
    private Properties properties;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        if(!Boolean.parseBoolean(properties.getProperty("lecmim.enabled"))){
            String response = "Messenger is disabled.";
            res.setStatus(500);
            res.getWriter().write(response);
            res.getWriter().flush();
        } else {
            HttpClient client = new HttpClient();
            String bindURL = properties.getProperty("lecmim.bind");
            PostMethod method = new PostMethod(bindURL);
            String[] headerNames = req.getHeaderNames();
            for (String headerName : headerNames) {
                String header = req.getHeader(headerName);
                method.setRequestHeader(headerName, header);
            }
            method.setRequestEntity(new StringRequestEntity(req.getContent().getContent(), null, req.getContent().getEncoding()));
            try {
                final int code = client.executeMethod(method);
                String responseBody = method.getResponseBodyAsString();
                method.releaseConnection();

                if (code != 200) {
                    throw new WebScriptException(code, responseBody);
                }

                final Header[] responseHeaders = method.getResponseHeaders();
                for (Header responseHeader : responseHeaders)
                {
                    res.addHeader(responseHeader.getName(), responseHeader.getValue());
                }

                res.getWriter().write(responseBody);
                res.getWriter().flush();
            } catch(Exception e) {
                String response = "Cannot execute post method. Probably, server is not avaliable.";
                res.setStatus(500);
                res.getWriter().write(response);
                res.getWriter().flush();
                logger.error("I can't execute post on " + bindURL, e);
            }
        }
    }
}
