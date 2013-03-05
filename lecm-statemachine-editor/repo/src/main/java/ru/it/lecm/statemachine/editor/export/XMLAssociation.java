package ru.it.lecm.statemachine.editor.export;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 28.02.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class XMLAssociation {
    private String type;
    private String reference;

    public XMLAssociation(String type, String reference) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }

        if (reference == null) {
            throw new IllegalArgumentException("reference cannot be null!");
        }

        this.type = type;
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "XMLAssociation{" +
                "type='" + type + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}

