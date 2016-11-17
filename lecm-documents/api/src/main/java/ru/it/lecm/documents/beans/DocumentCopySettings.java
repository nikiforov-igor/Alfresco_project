package ru.it.lecm.documents.beans;

import java.util.Collections;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 29.07.13
 * Time: 16:06
 */
public class DocumentCopySettings {

    private List<String> propsToCopy;
    private List<String> assocsToCopy;
    private List<String> categoriesToCopy;
    private List<String> tableDataToCopy;
    private String baseURL = DocumentService.DEFAULT_CREATE_URL;

    public List<String> getTableDataToCopy() {
        return tableDataToCopy;
    }

    public void setTableDataToCopy(List<String> tableDataToCopy) {
        this.tableDataToCopy = tableDataToCopy;
    }
    
    public List<String> getPropsToCopy() {
        return propsToCopy != null ? propsToCopy : Collections.<String>emptyList();
    }

    public void setPropsToCopy(List<String> propsToCopy) {
        this.propsToCopy = propsToCopy;
    }

    public List<String> getAssocsToCopy() {
        return assocsToCopy != null ? assocsToCopy : Collections.<String>emptyList();
    }

    public void setAssocsToCopy(List<String> assocsToCopy) {
        this.assocsToCopy = assocsToCopy;
    }

    public List<String> getCategoriesToCopy() {
        return categoriesToCopy != null ? categoriesToCopy : Collections.<String>emptyList();
    }

    public void setCategoriesToCopy(List<String> categoriesToCopy) {
        this.categoriesToCopy = categoriesToCopy;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public boolean isEmpty() {
        return getPropsToCopy().isEmpty() && getAssocsToCopy().isEmpty();
    }
}
