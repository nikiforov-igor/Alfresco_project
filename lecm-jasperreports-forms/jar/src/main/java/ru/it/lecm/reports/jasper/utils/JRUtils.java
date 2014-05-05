package ru.it.lecm.reports.jasper.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignField;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.JavaDataType;

public class JRUtils {

    private JRUtils() {
    }

    /**
     * Получить описатели полей.
     *
     */
    public static List<JRField> getJRFields(ReportDescriptor reportDescriptor) {
        // NOTE: если понадобится, можно сделать список для полей "однажды приготавливаемый" ...
        return (reportDescriptor != null && reportDescriptor.getDsDescriptor() != null)
                ? getJRFields(reportDescriptor.getDsDescriptor().getColumns())
                : new ArrayList<JRField>();
    }

    public static List<JRField> getJRFields(Collection<ColumnDescriptor> columns) {
        final List<JRField> result = new ArrayList<JRField>();
        if (columns != null) {
            for (ColumnDescriptor colDesc : columns) {
                final JRDesignField field = createJRField(colDesc);
                result.add(field);
            } // for
        }
        return result;
    }

    public static JRDesignField createJRField(ColumnDescriptor colDesc) {
        final JRDesignField field = new JRDesignField();
        field.setName(colDesc.getColumnName());
        field.setDescription(colDesc.getDefault());
        try {
            if (!colDesc.getClassName().equals(JavaDataType.HTML)) {
                field.setValueClass(Class.forName(colDesc.getClassName()));
            } else {
                field.setValueClass(String.class);
            }
        } catch (ClassNotFoundException ex) {
            final String msg = String.format("Column '%s' has invalid value class type: '%s' "
                    , colDesc.getColumnName(), colDesc.getClassName());
            throw new UnsupportedOperationException(msg, ex);
        }
        return field;
    }

    /**
     * Получить описатели полей.
     *
     */
    public static List<DataFieldColumn> getDataFields(ReportDescriptor reportDescriptor) {
        final List<DataFieldColumn> result = new ArrayList<DataFieldColumn>();
        if (reportDescriptor != null
                && reportDescriptor.getDsDescriptor() != null
                && reportDescriptor.getDsDescriptor().getColumns() != null
                ) {
            for (ColumnDescriptor colDesc : reportDescriptor.getDsDescriptor().getColumns()) {
                final DataFieldColumn item = DataFieldColumn.createDataField(colDesc);
                result.add(item);
            } // for
        }
        return result;
    }
}
