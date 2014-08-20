package ru.it.lecm.base.messages;

import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 20.08.14
 * Time: 9:37
 */
public class LecmMessagesWebScript extends org.springframework.extensions.webscripts.MessagesWebScript {

    /**
     * Generate the message for a given locale.
     *
     * @param locale Java locale format
     * @return messages as JSON string
     * @throws java.io.IOException
     */
    @Override
    protected String generateMessages(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException {
        Writer writer = new StringBuilderWriter(8192);
        writer.write("if (typeof Alfresco == \"undefined\" || !Alfresco) {var Alfresco = {};}\r\n");
        writer.write("Alfresco.messages = Alfresco.messages || {global: null, scope: {}}\r\n");
        writer.write("Alfresco.messages.global = ");
        JSONWriter out = new JSONWriter(writer);
        try {
            out.startObject();
            Map<String, String> messages = I18NUtil.getAllMessages(I18NUtil.parseLocale(locale));
            for (Map.Entry<String, String> entry : messages.entrySet()) {
                out.writeValue(entry.getKey(), entry.getValue());
            }
            out.endObject();
        } catch (IOException jsonErr) {
            throw new WebScriptException("Error building messages response.", jsonErr);
        }
        writer.write(";\r\n");

        // start logo
        // community logo
        /*final String serverPath = req.getServerPath();
        final int schemaIndex = serverPath.indexOf(':');
        writer.write("window.setTimeout(function(){(document.getElementById('alfresco-yuiloader')||document.createElement('div')).innerHTML = '<img src=\"");
        writer.write(serverPath.substring(0, schemaIndex));
        writer.write("://www.alfresco.com/assets/images/logos/community-4.2-share.png\" alt=\"*\" style=\"display:none\"/>\'}, 100);\r\n");
        // end logo*/

        return writer.toString();
    }

    @Override
    protected String getMessagesPrefix(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException {
        return "if (typeof Alfresco == \"undefined\" || !Alfresco) {var Alfresco = {};}\r\nAlfresco.messages = Alfresco.messages || {global: null, scope: {}}\r\nAlfresco.messages.global = ";
    }

    @Override
    protected String getMessagesSuffix(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(";\r\n");

        /*
        // start logo
        // community logo
        final String serverPath = req.getServerPath();
        final int schemaIndex = serverPath.indexOf(':');
        sb.append("window.setTimeout(function(){(document.getElementById('alfresco-yuiloader')||document.createElement('div')).innerHTML = '<img src=\"");
        sb.append(serverPath.substring(0, schemaIndex));
        sb.append("://www.alfresco.com/assets/images/logos/community-4.2-share.png\" alt=\"*\" style=\"display:none\"/>\'}, 100);\r\n");
        // end logo */
        return sb.toString();
    }
}