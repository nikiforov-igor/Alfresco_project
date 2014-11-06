package ru.it.lecm.reports.model.impl;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.FlagsExtendable;
import ru.it.lecm.reports.api.model.JavaClassable;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.model.impl.JavaDataType.SupportedTypes;
import ru.it.lecm.reports.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Описатель колонки.
 * Название колонки columnName == mnem.
 *
 * @author rabdullin
 */
public class ColumnDescriptor extends JavaClassableImpl implements JavaClassable, L18able, FlagsExtendable, Comparable<ru.it.lecm.reports.model.impl.ColumnDescriptor> {
    private static final long serialVersionUID = 1L;

    private JavaDataType dataType;
    private ParameterTypedValue parameterTypedValue;
    private FlagsExtendable flagsExtendable;

    private String expression;
    private boolean special = false;
    private String alfrescoType;

    private int order = 0;

    private Map<String, String> controlParams = new HashMap<>();

    public ColumnDescriptor() {
        super();
    }

    public ColumnDescriptor(String colname, SupportedTypes type) {
        super(((type == null) ? null : type.javaDataType().getClassName()), colname);
        this.dataType = (type == null) ? null : type.javaDataType();
    }

    public ColumnDescriptor(String colname) {
        this(colname, null);
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
        final ColumnDescriptor other = (ColumnDescriptor) obj;

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

        return order == other.order;
    }

    @Override
    public String toString() {
        return "ColumnDescriptor ["
                + "colname '" + getColumnName() + "'"
                + ", dataType " + dataType
                + (isSpecial() ? ", special" : "")
                + ", expression '" + expression + "'"
                + ", parameter " + parameterTypedValue
                + "\n\t, javaClass " + super.toString()
                + "\n\t, flagsExtendable " + flagsExtendable
                + ", order " + order
                + "]";
    }

    private FlagsExtendable flagsExtendable() {
        if (this.flagsExtendable == null) {
            this.flagsExtendable = new FlagsExtendableImpl();
        }
        return this.flagsExtendable;
    }

    /**
     * Мнемоника колонки - для использования как ссылка на колонку в конфигурации,
     * request-аргументах (если не задано явно название праметра)
     */
    public String getColumnName() {
        return getMnem();
    }

    public void setColumnName(String columnName) {
        setMnem(columnName);
    }

    /**
     * Выражение в терминах Провайдера НД для получения значения колонки.
     * Сейчас используется так:
     *    1) значения внутри одианрых "{...}" принимаются за ссылки на поля или ассоциации,
     *    2) значения внутри двойных "{{...}}" - данные для провайдера
     *    3) другие воспринимаются как константы
     */
    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String value) {
        this.expression = value;
    }

    public JavaDataType getDataType() {
        return this.dataType;
    }

    public void setDataType(JavaDataType value) {
        if (Utils.isSafelyEquals(this.dataType, value)) {
            return;
        }
        this.dataType = value;
        setClassName((this.dataType == null) ? null : this.dataType.getClassName());
    }

    @Override
    public void setClassName(String valueClazz) {
        if (Utils.isSafelyEquals(getClassName(), valueClazz)) {
            return;
        }
        super.setClassName(valueClazz);
        final SupportedTypes t = JavaDataType.SupportedTypes.findType(getClassName());
        setDataType(t == null ? null : t.javaDataType());
    }

    public ParameterTypedValue getParameterValue() {
        return this.parameterTypedValue;
    }

    public void setParameterValue(ParameterTypedValue value) {
        this.parameterTypedValue = value;
    }

    /** связанный тип параметра альфреско */
    public String getAlfrescoType() {
        return this.alfrescoType;
    }

    public void setAlfrescoType(String alfrescoType) {
        this.alfrescoType = alfrescoType;
    }

    @Override
    public Set<NamedValue> flags() {
        return flagsExtendable().flags();
    }

    public void setFlags(Set<NamedValue> aflags) {
        this.flagsExtendable().flags().clear();
        if (aflags != null) {
            this.flagsExtendable().flags().addAll(aflags);
        }
    }

    public Map<String, String> getControlParams() {
        if (this.controlParams == null) {
            this.controlParams = new HashMap<>();
        }
        return controlParams;
    }

    public void setControlParams(Map<String, String> controlParams) {
        this.controlParams = controlParams;
    }

    public void setControlParams(String paramsStr) {
        getControlParams().clear();
        if (!Utils.isStringEmpty(paramsStr)) {
            final String[] items = Utils.replaceCR(paramsStr).split("\\s*[;]\\s*");
            for (String item : items) {
                if (!Utils.isStringEmpty(item)) {
                    final String[] param = Utils.trimmed(item).split("\\s*[=]\\s*");
                    if (param.length == 2) {
                        this.controlParams.put(param[0], param[1]);
                    } else if (param.length == 1) {//у нас пустое значение параметра
                        this.controlParams.put(param[0], "");
                    }
                }
            }
        }
    }

    /**
     * Является ли колонка спецальной, например, константой для запроса или
     * чем-то подобным. По-умолчанию false. Специальные колонки не включаются
     * автоматом в шаблоны отчётов в часть вывода.
     */
    public boolean isSpecial() {
        return this.special;
    }

    public void setSpecial(boolean flag) {
        this.special = flag;
    }

    /**
     * Порядковый номер в списке выбора параметров (сравнение тоже в порядке order)
     */
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(ru.it.lecm.reports.model.impl.ColumnDescriptor other) {
        return this.getOrder() - other.getOrder();
    }

    public String getQNamedExpression() {
        if (this.expression == null) {
            return null;
        }
        return this.expression.replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");
    }

    public void assign(ru.it.lecm.reports.model.impl.ColumnDescriptor srcCol) {
        if (srcCol.getDataType() != null) {
            this.setDataType(srcCol.getDataType());
        }

        this.setL18Name((srcCol).getL18Name());

        this.setExpression(srcCol.getExpression());
        this.setSpecial(srcCol.isSpecial());
        this.setAlfrescoType(srcCol.getAlfrescoType());
        this.setControlParams(srcCol.getControlParams());

        this.setOrder(srcCol.getOrder());
        this.setParameterValue(Utils.clone(srcCol.getParameterValue()));

        this.setFlags(srcCol.flags());
    }
}
