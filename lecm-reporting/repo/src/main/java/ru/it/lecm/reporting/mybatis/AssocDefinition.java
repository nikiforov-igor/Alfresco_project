package ru.it.lecm.reporting.mybatis;

/**
 * User: dbashmakov
 * Date: 21.04.2015
 * Time: 15:37
 */
public class AssocDefinition {
    private String sourceRef;
    private String targetRef;
    private String assocName;

    public AssocDefinition(String sourceRef, String targetRef, String assocName) {
        this.setAssocName(assocName != null ? assocName.toLowerCase() : null);
        this.setSourceRef(sourceRef);
        this.setTargetRef(targetRef);
    }

    public String getAssocName() {
        return assocName;
    }

    public void setAssocName(String assocName) {
        this.assocName = assocName;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }
}
