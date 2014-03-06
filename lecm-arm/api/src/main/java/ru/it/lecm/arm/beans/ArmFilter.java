package ru.it.lecm.arm.beans;

import java.util.List;

/**
 * User: DBashmakov
 * Date: 17.02.14
 * Time: 15:02
 */
public class ArmFilter {
    private String title;
    private String code;
    private boolean multipleSelect;
    private String query;
    private String filterClass;
    private List<ArmFilterValue> values;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isMultipleSelect() {
        return multipleSelect;
    }

    public void setMultipleSelect(boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public List<ArmFilterValue> getValues() {
        return values;
    }

    public void setValues(List<ArmFilterValue> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArmFilter armFilter = (ArmFilter) o;

        if (multipleSelect != armFilter.multipleSelect) return false;
        if (values != null ? !values.equals(armFilter.values) : armFilter.values != null) return false;
        if (code != null ? !code.equals(armFilter.code) : armFilter.code != null) return false;
        if (filterClass != null ? !filterClass.equals(armFilter.filterClass) : armFilter.filterClass != null) return false;
        if (title != null ? !title.equals(armFilter.title) : armFilter.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (filterClass != null ? filterClass.hashCode() : 0);
        result = 31 * result + (multipleSelect ? 1 : 0);
        return result;
    }
}
