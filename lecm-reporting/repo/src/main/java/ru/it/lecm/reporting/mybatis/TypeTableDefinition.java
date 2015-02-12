package ru.it.lecm.reporting.mybatis;

/**
 * User: dbashmakov
 * Date: 09.12.2014
 * Time: 17:44
 */
public class TypeTableDefinition {
    private String tablename;
    private String typename;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public TypeTableDefinition(String tablename, String typename) {
        this.setTablename(tablename);
        this.setTypename(typename);
    }
}
