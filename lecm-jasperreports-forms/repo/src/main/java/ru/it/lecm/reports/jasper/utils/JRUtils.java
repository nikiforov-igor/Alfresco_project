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
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;

public class JRUtils {

	private JRUtils() {}

	/**
	 * Builds a map of parameters from the array that JR passes in with the report
	 * 
	 * @param params
	 * @return
	 */
	static public Map<String, JRParameter> buildParamMap(JRParameter[] params)
	{
		final Map<String, JRParameter> paramMap = new HashMap<String, JRParameter>();

		for(JRParameter param : params) {
			paramMap.put(param.getName(), param);
		}

		return paramMap;
	}

	public static StringBuilder print(Collection<JRField> fields) {
		return print( new StringBuilder(), (fields == null ? null : fields.toArray(new JRField[fields.size()])) );
	}

	public static StringBuilder print(JRField ... fields) {
		return print( new StringBuilder(), fields);
	}


	final private static String delimline = "\t====================================================================================================\n";

	public static StringBuilder print(StringBuilder dest, JRField ... fields) {
		if (fields == null) {
			dest.append("NULL\n");
		} else {
			dest.append(" counter "+ fields.length+ "\n");
			if (fields.length > 0) {
				dest.append(delimline);
				dest.append( String.format("\t [%3s] \t %-40s \t %-20s \t %s \n", "n", "fldName", "fldValueClass", "fldDescription" ));
				dest.append(delimline);
				int i = 0;
				for (JRField fld: fields) {
					i++;
					dest.append( String.format("\t [%3d] \t %-40s \t %-20s \t %s \n", i, fld.getName(), fld.getValueClass().getName(), fld.getDescription()));
				}
				dest.append(delimline);
			}
		}
		return dest;
	}

	/**
	 * Получить описатели полей.
	 * @param reportDescriptor 
	 * @return
	 */
	public static List<JRField> getJRFields(ReportDescriptor reportDescriptor) {
		// NOTE: если понадобится, можно сделать список для полей "однажды приготавливаемый" ...
		final List<JRField> result =
				(reportDescriptor != null && reportDescriptor.getDsDescriptor() != null) 
					? getJRFields(reportDescriptor.getDsDescriptor().getColumns())
					: new ArrayList<JRField>();
		return result;
	}

	public static List<JRField> getJRFields(Collection<ColumnDescriptor> columns) {
		final List<JRField> result = new ArrayList<JRField>();
		if (columns != null) 
		{
			for(ColumnDescriptor colDesc: columns) {
				final JRDesignField field = createJRField(colDesc);
				result.add(field);
			} // for
		}
		return result;
	}

	public static JRDesignField createJRField(ColumnDescriptor colDesc) {
		final JRDesignField field = new JRDesignField();
		field.setName( colDesc.getColumnName());
		try {
			field.setValueClass( Class.forName(colDesc.className()) );
		} catch (ClassNotFoundException ex) {
			final String msg = String.format( "Column '%s' has invalid value class type: '%s' "
					, colDesc.getColumnName(), colDesc.className());
			// logger.error(msg, ex);
			throw new UnsupportedOperationException( msg, ex);
		}
		return field;
	}

	/**
	 * Получить описатели полей.
	 * @return
	 */
	public static List<DataFieldColumn> getDataFields(ReportDescriptor reportDescriptor) {
		final List<DataFieldColumn> result = new ArrayList<DataFieldColumn>();
		if (	reportDescriptor != null
				&& reportDescriptor.getDsDescriptor() != null
				&& reportDescriptor.getDsDescriptor().getColumns() != null
				) {
			for(ColumnDescriptor colDesc: reportDescriptor.getDsDescriptor().getColumns()) {
				final DataFieldColumn item = new DataFieldColumn();
				item.setName( colDesc.getColumnName());
				item.setValueLink( colDesc.getExpression());
                item.setValueClassName(colDesc.className());
                result.add(item);
			} // for
		}
		return result;
	}

}
