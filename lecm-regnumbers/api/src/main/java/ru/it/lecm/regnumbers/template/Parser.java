package ru.it.lecm.regnumbers.template;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public interface Parser {
	String runTemplate(String templateStr, NodeRef documentNode);
	boolean validateTemplate(String templateStr);
}
