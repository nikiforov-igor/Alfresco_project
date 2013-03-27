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

/**
 Скрипт для проксирования обращений к BOSH-сервису
 */
public class BoshProxyWebScript extends AbstractWebScript {
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        HttpClient client = new HttpClient();
        // Create a method instance.
        PostMethod method = new PostMethod("http://127.0.0.1:7070/http-bind/");
        String[] headerNames = req.getHeaderNames();
        for (String headerName : headerNames) {
            String header = req.getHeader(headerName);
            method.setRequestHeader(headerName, header);
        }
        method.setRequestEntity(new StringRequestEntity(req.getContent().getContent(), null, req.getContent().getEncoding()));

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
    }
}
