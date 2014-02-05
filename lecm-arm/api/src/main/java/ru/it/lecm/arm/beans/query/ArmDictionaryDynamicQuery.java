package ru.it.lecm.arm.beans.query;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 10:00
 */
public class ArmDictionaryDynamicQuery extends ArmBaseQuery {
	private NodeRef dictionary;

	public NodeRef getDictionary() {
		return dictionary;
	}

	public void setDictionary(NodeRef dictionary) {
		this.dictionary = dictionary;
	}
}
