package ru.it.lecm.dictionary.imports;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import ru.it.lecm.dictionary.export.CsvDictionaryImporter;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: mShafeev
 * Date: 02.11.12
 * Time: 16:33
 */
public class ImportCSV extends AbstractWebScript {

	NodeService nodeService;
	NamespaceService namespaceService;
	Repository repositoryHelper;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		JSONObject wf = new JSONObject();
		JSONArray compositions = new JSONArray();
		String nodeRefStr = req.getParameter("nodeRef");
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		try {
			FormData formData = (FormData) req.parseContent();
			FormData.FormField[] fields = formData.getFields();

			InputStream inputStream = fields[0].getInputStream();

			CsvDictionaryImporter csvDictionaryImporter = new CsvDictionaryImporter(inputStream,
					nodeService, namespaceService, nodeRef);
			csvDictionaryImporter.readDictionary();

			wf.put("text", "Элементы успешно загружены");
			compositions.put(wf);
			res.setContentEncoding("utf-8");
			res.getWriter().write(compositions.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
