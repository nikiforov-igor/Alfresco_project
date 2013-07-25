package ru.it.lecm.br5.semantic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.xml.datatype.DatatypeConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.soap.DataItem;

/**
 *
 * @author snovikov
 */
public interface SemanticBean {
	public Integer loadExpertBr5(NodeRef expert);
	public void loadExpertBr5Async(final NodeRef expert);
	public void loadDocumentBr5Async(NodeRef documentFile) throws DatatypeConfigurationException;
	public List<DataItem> getExpertsTagsBr5 (NodeRef expert);
	public HashMap<String, HashMap<Double, Integer>> getExpertsTagsBr5WithCoefAndFont(NodeRef expert);
	public HashMap<String,Integer> getExpertsTagsBr5OnlyWithFont(NodeRef expert);
	public boolean hasBr5Aspect(NodeRef documentRef);
	public void setDocumentTags(NodeRef documentRef, HashMap<String,Double> tags);
	public HashMap<String,Double> getDocumentTags(NodeRef documentRef);
	public HashMap<String,Integer> getDocumentTagsWithFont(NodeRef documentRef);
	public TreeMap<Float,ArrayList<HashMap<String,String>>> getDataExpertsByDocument(NodeRef document);
	public List<NodeRef> getSimilarDocumentByTag(String tag);
	public List<NodeRef> getSimilarDocumentByDocument(NodeRef document);
}
