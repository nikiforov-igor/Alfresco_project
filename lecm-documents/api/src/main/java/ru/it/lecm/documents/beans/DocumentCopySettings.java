package ru.it.lecm.documents.beans;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 29.07.13
 * Time: 16:06
 */
public class DocumentCopySettings {

    private List<String> propsToCopy;
    private List<String> assocsToCopy;

    public List<String> getPropsToCopy() {
        return propsToCopy;
    }

    public void setPropsToCopy(List<String> propsToCopy) {
        this.propsToCopy = propsToCopy;
    }

    public List<String> getAssocsToCopy() {
        return assocsToCopy;
    }

    public void setAssocsToCopy(List<String> assocsToCopy) {
        this.assocsToCopy = assocsToCopy;
    }
}
