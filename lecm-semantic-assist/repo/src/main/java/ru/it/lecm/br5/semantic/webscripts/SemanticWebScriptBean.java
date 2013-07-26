package ru.it.lecm.br5.semantic.webscripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.datatype.DatatypeConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.br5.semantic.api.ConstantsBean;
import ru.it.lecm.br5.semantic.api.SemanticBean;
import ru.it.lecm.br5.semantic.beans.SemanticBeanImpl;
import ru.it.lecm.br5.semantic.policies.DocumentBr5AspectPolicy;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author snovikov
 */
public class SemanticWebScriptBean extends BaseWebScript implements ConstantsBean {

	private final static Logger logger = LoggerFactory.getLogger(DocumentBr5AspectPolicy.class);
	private NodeService nodeService;
	private SemanticBean semanticService;
	protected OrgstructureBean orgstructureService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSemanticService(SemanticBeanImpl semanticService) {
		this.semanticService = semanticService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void addBr5AspectToDocument(String sDocument) {
		if (sDocument != null && !sDocument.isEmpty()) {
			NodeRef documentRef = new NodeRef(sDocument);
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(ConstantsBean.PROP_BR5_INTEGRATION_LOADED, false);
			if (!nodeService.hasAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION)) {
				nodeService.addAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION, props);
			}
		}
	}

	//TODO debug purpouse method REMOVE!
	@Deprecated
	public void loadExpertBr5(String sExpert) {
		if (sExpert != null && !sExpert.isEmpty()) {
			NodeRef expertRef = new NodeRef(sExpert);
			semanticService.loadExpertBr5(expertRef);
		}
	}

	//TODO debug purpouse method REMOVE!
	@Deprecated
	public void loadDocumentBr5(String sDocument) throws DatatypeConfigurationException {
		if (sDocument != null && !sDocument.isEmpty()) {
			NodeRef documentRef = new NodeRef(sDocument);
			semanticService.loadDocumentBr5(documentRef);
		}
	}

	public Map<String, Integer> getExpertsTagsBr5(String sExpert, String maxFont, String minFont) {
		NodeRef expertRef;
		if (sExpert != null && !sExpert.isEmpty()) {
			expertRef = new NodeRef(sExpert);
		} else {
			expertRef = orgstructureService.getCurrentEmployee();
		}
		Integer maxFontSize;
		Integer minFontSize;
		try {
			maxFontSize = Integer.parseInt(maxFont);
			minFontSize = Integer.parseInt(minFont);
		} catch (NumberFormatException e) {
			maxFontSize = null;
			minFontSize = null;
		}
		return semanticService.normalizeTags(semanticService.getExpertsTagsBr5(expertRef), maxFontSize, minFontSize);
	}

	public boolean hasBr5Aspect(String sDocument) {
		if (sDocument != null && !sDocument.isEmpty()) {
			NodeRef documentRef = new NodeRef(sDocument);
			return semanticService.hasBr5Aspect(documentRef);
		}
		return false;
	}

	public Map<String, Integer> getDocumentTagsBr5(String sDocument, String maxFont, String minFont) {
		if (sDocument != null && !sDocument.isEmpty()) {
			NodeRef documentRef = new NodeRef(sDocument);
			Integer maxFontSize;
			Integer minFontSize;
			try {
				maxFontSize = Integer.parseInt(maxFont);
				minFontSize = Integer.parseInt(minFont);
			} catch (NumberFormatException e) {
				maxFontSize = null;
				minFontSize = null;
			}
			return semanticService.normalizeTags(semanticService.getDocumentTagsBr5(documentRef),maxFontSize, minFontSize);
		}
		return null;
	}

	public SortedMap<Float, List<Map<String, String>>> getDataExpertsByDocument(String sDocument) {
		if (sDocument != null && !sDocument.isEmpty()) {
			NodeRef documentRef = new NodeRef(sDocument);
			return semanticService.getDataExpertsByDocument(documentRef);
		}
		return null;
	}

	public void refreshDocument(String nodeRef) {
		if (nodeRef != null && !nodeRef.isEmpty()) {
			semanticService.refreshDocument(new NodeRef(nodeRef));
		} else {
			throw new RuntimeException("nodeRef parameter is empty");
		}
	}
	public List<NodeRef> getSimilarDocumentsByTag(String tag){
		if (tag!=null && !tag.isEmpty()){
			return semanticService.getSimilarDocumentsByTag(tag);
		}
		return null;
	}


}
