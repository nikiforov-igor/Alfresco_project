package ru.it.lecm.statemachine.editor.export;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 28.02.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class XMLRoleAssociation {
    private String type;
    private String businessRoleName;

    public XMLRoleAssociation(String type, String businessRoleName) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }

        if (businessRoleName == null) {
            throw new IllegalArgumentException("businessRoleName cannot be null!");
        }

        this.type = type;
        this.businessRoleName = businessRoleName;
    }

    public String getType() {
        return type;
    }

    public String getBusinessRoleName() {
        return businessRoleName;
    }

    @Override
    public String toString() {
        return "XMLRoleAssociation {" +
                "type='" + type + '\'' +
                ", businessRoleName='" + businessRoleName + '\'' +
                '}';
    }
}

