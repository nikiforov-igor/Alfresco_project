package ru.it.lecm.dictionary.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 14.06.13
 * Time: 14:55
 */
public class XMLImporterInfo {
	private Map<String, List<String>> assocNotFoundErrors;
	private int createdElementsCount;
	private int updatedElementsCount;

	public XMLImporterInfo() {
		assocNotFoundErrors = new HashMap<String, List<String>>();
		createdElementsCount = 0;
		updatedElementsCount = 0;
	}

	public boolean existErrors() {
		return this.assocNotFoundErrors.size() > 0;
	}

	public Map<String, List<String>> getAssocNotFoundErrors() {
		return assocNotFoundErrors;
	}

	public void addAssocNotFoundError(String assocType, String assocPath) {
		if (this.assocNotFoundErrors.containsKey(assocType)) {
			this.assocNotFoundErrors.get(assocType).add(assocPath);
		} else {
			List<String> errors = new ArrayList<String>();
			errors.add(assocPath);
			this.assocNotFoundErrors.put(assocType, errors);
		}
	}

	public int getCreatedElementsCount() {
		return createdElementsCount;
	}

	public int getUpdatedElementsCount() {
		return updatedElementsCount;
	}

	public void setCreatedElementsCount(int createdElementsCount) {
		this.createdElementsCount = createdElementsCount;
	}

	public void setUpdatedElementsCount(int updatedElementsCount) {
		this.updatedElementsCount = updatedElementsCount;
	}

	public int getImportedElementsCount() {
		return createdElementsCount + updatedElementsCount;
	}

    @Override
    public String toString() {
        return "XMLImporterInfo{" +
                "importedElementsCount=" + getImportedElementsCount() +
                ", createdElementsCount=" + createdElementsCount +
                ", updatedElementsCount=" + updatedElementsCount +
                ", assocNotFoundErrors=" + assocNotFoundErrors.values().size() +
                '}';
    }
}
