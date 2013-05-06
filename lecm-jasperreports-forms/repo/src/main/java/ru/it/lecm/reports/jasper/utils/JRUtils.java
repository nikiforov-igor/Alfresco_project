package ru.it.lecm.reports.jasper.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;

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

}
