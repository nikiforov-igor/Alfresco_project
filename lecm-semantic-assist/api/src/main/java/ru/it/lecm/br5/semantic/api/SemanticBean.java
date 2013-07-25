package ru.it.lecm.br5.semantic.api;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.xml.datatype.DatatypeConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author snovikov
 */
public interface SemanticBean {
	public Integer loadExpertBr5(NodeRef expert);
	public Boolean refreshDocumentTagsBr5(NodeRef documentRef) throws DatatypeConfigurationException;
	public Boolean loadDocumentBr5(NodeRef documentFile) throws DatatypeConfigurationException;
	public Map<String, Float> getExpertsTagsBr5 (NodeRef expert);
	public Map<String, Float> getDocumentTagsBr5(NodeRef documentRef);
	public boolean hasBr5Aspect(NodeRef documentRef);
	public void setDocumentTags(NodeRef documentRef, Map<String,Double> tags);
	public void refreshDocument(NodeRef documentRef);
	public SortedMap<Float, List<Map<String, String>>> getDataExpertsByDocument(NodeRef document);
	public List<NodeRef> getSimilarDocumentByTag(String tag);
	public List<NodeRef> getSimilarDocumentByDocument(NodeRef document);
	public Map<String, Integer> normalizeTags(Map<String, Float> tags, Integer maxFontSize, Integer minFontSize);
}
