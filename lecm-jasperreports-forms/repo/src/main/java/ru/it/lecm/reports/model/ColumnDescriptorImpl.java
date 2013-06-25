package ru.it.lecm.reports.model;

import java.util.Set;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.FlagsExtendable;
import ru.it.lecm.reports.api.model.JavaDataType;
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.model.JavaDataTypeImpl.SupportedTypes;

/**
 * Описатель колонки.
 * Название колонки columnName == mnem.
 * @author rabdullin
 *
 */
public class ColumnDescriptorImpl
		extends JavaClassableImpl
		implements ColumnDescriptor
{
	private JavaDataType dataType;
	private ParameterTypedValue parameterTypedValue;
	private FlagsExtendable flagsExtendable;
	private String expression;

	public ColumnDescriptorImpl() {
		super();
	}

	public ColumnDescriptorImpl(String colname, SupportedTypes type) {
		super( ((type == null) ? null : type.javaDataType().className()), colname);
		this.dataType = (type == null) ? null : type.javaDataType();
	}

	public ColumnDescriptorImpl(String colname, String typeClassName) {
		this( colname, SupportedTypes.findType(typeClassName));
	}

	public ColumnDescriptorImpl(String colname) {
		this(colname, (SupportedTypes) null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getColumnName() == null) ? 0 : getColumnName().hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((parameterTypedValue == null) ? 0 : parameterTypedValue.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result
				+ ((flagsExtendable == null) ? 0 : flagsExtendable.hashCode());
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
		final ColumnDescriptorImpl other = (ColumnDescriptorImpl) obj;

		final String columnName = getColumnName();
		if (columnName == null) {
			if (other.getColumnName() != null)
				return false;
		} else if (!columnName.equals(other.getColumnName()))
			return false;

		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;

		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;

		if (parameterTypedValue == null) {
			if (other.parameterTypedValue != null)
				return false;
		} else if (!parameterTypedValue.equals(other.parameterTypedValue))
			return false;

		if (flagsExtendable == null) {
			if (other.flagsExtendable != null)
				return false;
		} else if (!flagsExtendable.equals(other.flagsExtendable))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ColumnDescriptorImpl ["
				+ "mnem '"+ getMnem()+ "'"
				+ ", dataType " + dataType
				+ ", expression '"+ expression+ "'"
				+ ", parameter " + parameterTypedValue
				+ ", flagsExtendable " + flagsExtendable
				+ "]";
	}

//	private ParameterTypedValue parameterValue() {
//		if (this.parameterTypedValue == null)
//			this.parameterTypedValue = new ParameterTypedValueImpl();
//		return this.parameterTypedValue;
//	}

	private FlagsExtendable flagsExtendable() {
		if (this.flagsExtendable == null)
			this.flagsExtendable = new FlagsExtendableImpl();
		return this.flagsExtendable;
	}

	@Override
	public String getColumnName() {
		return getMnem();
	}

	@Override
	public void setColumnName(String columnName) {
		setMnem( columnName);
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public void setExpression(String value) {
		this.expression = value;
	}

	@Override
	public JavaDataType getDataType() {
		return this.dataType;
	}

	@Override
	public void setDataType(JavaDataType value) {
		this.dataType = value;
	}

	@Override
	public ParameterTypedValue getParameterValue() {
		return this.parameterTypedValue;
	}

	@Override
	public void setParameterValue(ParameterTypedValue value) {
		this.parameterTypedValue = value;
	}

	@Override
	public Set<NamedValue> flags() {
		return flagsExtendable().flags();
	}

}
