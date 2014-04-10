package ru.it.lecm.reports.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;
import ru.it.lecm.reports.api.model.QueryDescriptor;
import ru.it.lecm.reports.utils.Utils;

public class QueryDescriptorImpl extends MnemonicNamedItem implements QueryDescriptor {
    private static final long serialVersionUID = 1L;

    private String text;
    private String sort;
    private int offset = 0, limit = -1, pgSize = -1;
    private boolean allVersions = true;

    private List<String> supportedNodeTypes = new ArrayList<String>();

    @Override
    public void setPreferedNodeType(String value) {
        List<String> newSupportedList = null;
        if (value != null && value.length() > 0) {
            final String[] items = value.split("\\s*[,;]\\s*");
            newSupportedList = Arrays.asList(items);
        }
        this.setSupportedNodeTypes(newSupportedList);
    }

    @Override
    public List<String> getSupportedNodeTypes() {
        return supportedNodeTypes;
    }

    @Override
    public void setSupportedNodeTypes(List<String> values) {
        this.supportedNodeTypes = values;
        if (this.supportedNodeTypes != null) { // отфильтруем и оставим только непустые
            for (Iterator<String> ii = this.supportedNodeTypes.iterator(); ii.hasNext(); ) {
                final String s = ii.next();
                if (s == null || s.trim().length() == 0) {
                    // убираем пустое
                    ii.remove();
                }
            }
        }
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getSort() {
        return this.sort;
    }

    @Override
    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getPgSize() {
        return this.pgSize;
    }

    @Override
    public void setPgSize(int pgSize) {
        this.pgSize = pgSize;
    }

    @Override
    public boolean isAllVersions() {
        return this.allVersions;
    }

    @Override
    public void setAllVersions(boolean flag) {
        this.allVersions = flag;
    }

    @Override
    public boolean isTypeSupported(String qname) {
        final boolean isOuterEmpty = Utils.isStringEmpty(qname);
        if (isOuterEmpty) {
            // если проверяется пустой внешний тип - считаем что он подходит к любому внутреннему
            return true;
        }

        if (getSupportedNodeTypes() == null) {
            return true;
        }

        for (String s : getSupportedNodeTypes()) {
            if (s.equalsIgnoreCase(qname)) {
                // совпадение типа (с точностью до регистра)
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + limit;
        result = prime * result + offset;
        result = prime * result + pgSize;
        result = prime * result + ((supportedNodeTypes == null) ? 0 : supportedNodeTypes.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QueryDescriptorImpl other = (QueryDescriptorImpl) obj;
        if (limit != other.limit)
            return false;
        if (offset != other.offset)
            return false;
        if (pgSize != other.pgSize)
            return false;

        if (this.supportedNodeTypes == null) {
            if (other.supportedNodeTypes != null)
                return false;
        } else if (!Arrays.equals(this.supportedNodeTypes.toArray(), other.supportedNodeTypes.toArray())) {
            return false;
        }

        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("QueryDescriptorImpl [ mnem '%s'", getMnem()));
        builder.append(", offset ").append(offset);
        builder.append(", limit ").append(limit);
        builder.append(", pgSize ").append(pgSize);
        builder.append(", supportedNodeTypes ").append(
                getSupportedNodeTypes() == null
                        ? "NULL"
                        : StringUtils.collectionToCommaDelimitedString(getSupportedNodeTypes())
        );
        builder.append(String.format("\n\t\t\t<text>\n'%s'\n\t\t\t<text>", text));
        builder.append("\n\t\t]");
        return builder.toString();
    }
}
