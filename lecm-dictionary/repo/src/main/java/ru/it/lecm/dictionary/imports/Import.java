package ru.it.lecm.dictionary.imports;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.dictionary.beans.XMLImportBean;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;


/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Import extends AbstractWebScript {
	private DictionaryBean dictionaryBean;
    private XMLImportBean xmlImportBean;

    public void setXmlImportBean(XMLImportBean xmlImportBean) {
        this.xmlImportBean = xmlImportBean;
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
			NodeRef rootDir = dictionaryBean.getDictionariesRoot();
            XMLImportBean.XMLImporter xmlDictionaryImporter = xmlImportBean.getXMLImporter(inputStream);
		    xmlDictionaryImporter.readItems(rootDir);
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

	public void setDictionaryBean(DictionaryBean dictionaryBean) {
		this.dictionaryBean = dictionaryBean;
	}
}
