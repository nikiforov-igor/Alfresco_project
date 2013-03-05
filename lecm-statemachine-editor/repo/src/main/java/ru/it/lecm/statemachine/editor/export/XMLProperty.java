package ru.it.lecm.statemachine.editor.export;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 28.02.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class XMLProperty {
    private String name;
    private String value;

    public XMLProperty(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null!");
        }

        this.name = name;
        this.value = value != null ? value : "";
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "XMLProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}