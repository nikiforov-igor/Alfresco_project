package ru.it.lecm.reports.api.model;


public interface ColumnDescriptor
		extends JavaClassable, L18able, FlagsExtendable {

	String getColumnName();
	void setColumnName(String colname);

	JavaDataType getDataType();
	void setDataType(JavaDataType value);

	String getExpression();
	void setExpression(String value);

	ParameterTypedValue getParameterValue();
	void setParameterValue(ParameterTypedValue value);
}
