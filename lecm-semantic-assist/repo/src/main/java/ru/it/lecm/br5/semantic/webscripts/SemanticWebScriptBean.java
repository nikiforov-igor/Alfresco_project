package ru.it.lecm.br5.semantic.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.br5.semantic.api.ConstantsBean;
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
	private SemanticBeanImpl semanticService;
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

	public void addBr5AspectToDocument(String sDocument){
		if (sDocument!=null && !sDocument.isEmpty()){
			NodeRef documentRef = new NodeRef(sDocument);
			Map<QName,Serializable> props = new HashMap<QName,Serializable>();
			props.put(ConstantsBean.PROP_BR5_INTEGRATION_LOADED,false);
			if (!nodeService.hasAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION)){
				nodeService.addAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION, props);
			}
		}
	}
	public void loadExpertBr5(String sExpert){
		if (sExpert!=null && !sExpert.isEmpty()){
			NodeRef expertRef = new NodeRef(sExpert);
			semanticService.loadExpertBr5Async(expertRef);
		}
	}

	public void loadDocumentBr5(String sExpert,String sDocument) throws DatatypeConfigurationException{
		if (sExpert!=null && sDocument!= null && !sExpert.isEmpty() && !sDocument.isEmpty()){
			NodeRef expertRef = new NodeRef(sExpert);
			NodeRef documentRef = new NodeRef(sDocument);
			semanticService.loadDocumentBr5Async(expertRef,documentRef);
		}
	}

	public HashMap<String, HashMap<Double, Integer>> getExpertsTagsBr5(String sExpert){
		if (sExpert!=null && !sExpert.isEmpty() ){
			NodeRef expertRef = new NodeRef(sExpert);
			return semanticService.getExpertsTagsBr5(expertRef);
		}
		else{
			NodeRef curEmp = orgstructureService.getCurrentEmployee();
			return semanticService.getExpertsTagsBr5(curEmp);
		}
	}


}
