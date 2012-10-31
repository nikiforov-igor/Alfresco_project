package ru.it.lecm.dictionary.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Export extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(Export.class);

	protected NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void collectNodes(NodeRef node, XMLStreamWriter xmlw, ArrayList<String> namespace,
	                         String[] fields
	) throws XMLStreamException {
//        list.add(node);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
//        xmlw.writeStartElement("items");
		if (!childAssocs.isEmpty()) {
			xmlw.writeStartElement("items");
			{
				for (ChildAssociationRef subNodeAss : childAssocs) {
					xmlw.writeStartElement("item");
					{
						xmlw.writeAttribute("name", subNodeAss.getChildRef().toString());
						Set set = nodeService.getProperties(subNodeAss.getChildRef()).entrySet();
						for (Object aSet : set) {
							Map.Entry m = (Map.Entry) aSet;
							QName key = (QName) m.getKey();
							String value = m.getValue().toString();
							for (int i = 0; i < namespace.size(); i++) {
								if (namespace.get(i).equals(key.getLocalName())) {
									xmlw.writeStartElement("property");
									xmlw.writeAttribute("name", fields[i]);
									xmlw.writeCharacters(value);
									xmlw.writeEndElement();
								}
							}
						}
					}
					collectNodes(subNodeAss.getChildRef(), xmlw, namespace, fields);
					xmlw.writeEndElement();
				}
			}
			xmlw.writeEndElement();
		}
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		OutputStream resOutputStream = null;
		try {
			ArrayList<String> namespace = new ArrayList<String>();
			String[] fields = req.getParameterValues("field");
			String nodeRefStr = req.getParameter("nodeRef");
			for (String field : fields) {
				namespace.add(field.split(":")[1]);
			}
			NodeRef nodeRef = new NodeRef(nodeRefStr);

			String name = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
//			Set set = nodeService.getProperties(nodeRef).entrySet();
//			Iterator iterator = set.iterator();
//			while (iterator.hasNext()) {
//				Map.Entry m = (Map.Entry) iterator.next();
//				QName key = (QName) m.getKey();
//				String value = (String) m.getValue().toString();
//				if (key.getLocalName().equals("name")) {
//					name = value;
//				}
//			}


			// Create an output factory
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();

			res.setContentEncoding("UTF-8");
			res.setContentType("text/xml");
			res.setHeader("fileName", "test.xml");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(resOutputStream);
			xmlw.writeStartDocument("1.0");
			{
				xmlw.writeStartElement("dictionary");
				{
					xmlw.writeAttribute("nodeRef", nodeRef.toString());
					xmlw.writeStartElement("properties");
					{
						xmlw.writeAttribute("name", name);
					}
					xmlw.writeEndElement();
					//Обход по дереву
					collectNodes(nodeRef, xmlw, namespace, fields);
				}
				xmlw.writeEndElement();
			}
			xmlw.writeEndDocument();
			// Close the writer to flush the output
			xmlw.close();
			resOutputStream.flush();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
		log.info("Export complete");
	}

}
