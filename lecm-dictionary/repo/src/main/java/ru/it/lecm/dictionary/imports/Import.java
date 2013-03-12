package ru.it.lecm.dictionary.imports;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.dictionary.beans.DictionaryBean;


/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Import extends AbstractWebScript {
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;


    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
	    JSONObject wf = new JSONObject();
	    JSONArray compositions = new JSONArray();
	    InputStream inputStream = null;
	    try {
		    FormData formData = (FormData) req.parseContent();
		    FormField[] fields = formData.getFields();

		    inputStream = fields[0].getInputStream();
			NodeRef rootDir = dictionaryService.getDictionariesRoot();
		    XmlDictionaryImporter xmlDictionaryImporter = new XmlDictionaryImporter(inputStream, nodeService, namespaceService, rootDir);
		    xmlDictionaryImporter.readDictionary();
		    //Возможно необходимо выводить статистику по добавленым значениям
		    wf.put("text", "Справочник успешно создан");
		    compositions.put(wf);
		    res.setContentEncoding("utf-8");
		    res.getWriter().write(compositions.toString());
	    } catch (XMLStreamException e) {
		    e.printStackTrace();
	    } catch (JSONException e) {
		    e.printStackTrace();
	    } finally {
		    if (inputStream != null) {
			    inputStream.close();
		    }
	    }

    }

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
}
