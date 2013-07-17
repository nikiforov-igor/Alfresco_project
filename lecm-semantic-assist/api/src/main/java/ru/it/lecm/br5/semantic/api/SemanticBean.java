package ru.it.lecm.br5.semantic.api;

import java.util.HashMap;
import javax.xml.datatype.DatatypeConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author snovikov
 */
public interface SemanticBean {
	public Integer loadExpertBr5(NodeRef expert);
	public void loadExpertBr5Async(final NodeRef expert);
	public void loadDocumentBr5Async(NodeRef expert, NodeRef documentFile) throws DatatypeConfigurationException;
	public HashMap<String, HashMap<Double, Integer>> getExpertsTagsBr5(NodeRef expert);
}
