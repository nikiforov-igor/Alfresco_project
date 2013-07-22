package ru.it.lecm.reports.model.impl;

import java.util.Set;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.FlagsExtendable;
import ru.it.lecm.reports.api.model.JavaDataType;
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.utils.Utils;

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
	private boolean special = false;
    private String alfrescoType;
    private int order = 0;

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


	/**
	 * Проверить является ли мапинг параметра простым - т.е. сразу на атрибут (нет ассоциаций).
	 * Сейчас такими принимаются любые непустые строки, которые НЕ начинаются с '{'.
	 * @param column проверяемый описатель
	 * @return true, если параметр смапирован на простое поле
	 */
	public static boolean isMapped2ImmediateProperty(ColumnDescriptor column) {
		return (column != null)
				&& !Utils.isStringEmpty(column.getExpression())
				&& !column.getExpression().startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (this.isSpecial() ? 1231 : 1237);
		result = prime * result
				+ ((getColumnName() == null) ? 0 : getColumnName().hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((parameterTypedValue == null) ? 0 : parameterTypedValue.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result
				+ ((flagsExtendable == null) ? 0 : flagsExtendable.hashCode());
        result = prime * result + order;
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

		if (this.isSpecial() != other.isSpecial())
			return false;

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

        if (order != other.order) {
            return false;
        }
        return true;
	}

	@Override
	public String toString() {
		return "ColumnDescriptorImpl ["
				+ "colname '"+ getColumnName()+ "'"
				+ ", dataType " + dataType
				+ (isSpecial() ? ", special": "")
				+ ", expression '"+ expression+ "'"
				+ ", parameter " + parameterTypedValue
				+ "\n\t, javaClass " + super.toString()
				+ "\n\t, flagsExtendable " + flagsExtendable
                + ", order " + order
				+ "]";
	}

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
		if (Utils.isSafelyEquals(this.dataType, value))
			return;
		this.dataType = value;
		setClassName( (this.dataType == null) ? null : this.dataType.className());
	}

	@Override
	public void setClassName(String valueClazz) {
		if (Utils.isSafelyEquals(className(), valueClazz))
			return;
		super.setClassName(valueClazz);
		// Class<?> javaClass = Utils.getJavaClassByName( valueClazz, defaultClass);
		final SupportedTypes t = JavaDataTypeImpl.SupportedTypes.findType(className());
		setDataType( t == null ? null : t.javaDataType() );
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
    public String getAlfrescoType() {
        return this.alfrescoType;
    }

    @Override
    public void setAlfrescoType(String alfrescoType) {
        this.alfrescoType = alfrescoType;
    }

    @Override
	public Set<NamedValue> flags() {
		return flagsExtendable().flags();
	}

	@Override
	public boolean isSpecial() {
		return this.special;
	}

	@Override
	public void setSpecial(boolean flag) {
		this.special = flag;		
	}

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int compareTo(Object obj) {
        ColumnDescriptor tmp = (ColumnDescriptor) obj;
        if (this.getOrder() < tmp.getOrder()) {
            return -1;
        } else if (this.getOrder() > tmp.getOrder()) {
            return 1;
        }
        return 0;
    }
}
