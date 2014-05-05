package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.JavaClassable;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;
import ru.it.lecm.reports.utils.ArgsHelper;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Тип данных java в шаблонах.
 * Предполагается использование через регламентированный набор поддерживаемых
 * типов (@SEE SupportedTypes.javaDataType), так что конструктор "убран" в protected.
 *
 * @author rabdullin
 */
public class JavaDataType extends JavaClassableImpl implements JavaClassable, Mnemonicable, L18able {
    private static final long serialVersionUID = 1L;

    protected JavaDataType(String className) {
        super(className);
    }

    /**
     * Набор типов поддерживаемых для шаблонов.
     *
     * @author rabdullin
     */
    public enum SupportedTypes {
        NULL(null),
        STRING(String.class.getName()),
        HTML("HTML"),
        DATE(Date.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Date ? value : ArgsHelper.tryMakeDate(value.toString(), null));
            }
        },
        BOOL(Boolean.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Boolean ? value : Boolean.valueOf(value.toString()));
            }
        },

        DOUBLE(Double.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Double ? value : Double.valueOf(value.toString()));
            }
        },

        LONG(Long.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Long ? value : Long.valueOf(value.toString()));
            }
        },

        INTEGER(Integer.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Integer ? value : Integer.valueOf(value.toString()));
            }
        },

        FLOAT(Float.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Float ? value : Float.valueOf(value.toString()));
            }
        },

        NUMERIC(java.lang.Number.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                if (value == null || value.toString().isEmpty()) {
                    return null;
                }
                return (value instanceof Number ? value : Long.valueOf(value.toString()));
            }
        },

        LIST(List.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                return ((value instanceof List || value == null )?
                        value :
                        (value instanceof String[] ?
                                new ArrayList<Object>(Arrays.asList((String[])value)) :
                                new ArrayList<Object>(Arrays.asList(value.toString().split("[,;]")))));
            }
        };

        final private JavaDataType javaDataType;

        private SupportedTypes(String clazzName) {
            javaDataType = (clazzName != null) ? new JavaDataType(clazzName) : null;
        }

        public JavaDataType javaDataType() {
            return this.javaDataType;
        }

        public Object getValueByRealType(Object value) {
            return value != null ? value.toString() : null;
        }

        public static SupportedTypes findType(String clazzName) {
            if (clazzName != null) {
                // поиск точного соот-вия
                for (SupportedTypes t : values()) {
                    if (clazzName.equalsIgnoreCase(t.name()) // X совпадает с name полностью
                            || (t.javaDataType != null
                            && clazzName.equalsIgnoreCase(t.javaDataType.getClassName()) // X совпадает с "длинным типом" ...
                    )
                            )
                        return t; // FOUNDS
                }

                // поиск как подстрок ...
                for (SupportedTypes t : values()) {
                    if (t.name().toLowerCase().startsWith(clazzName.toLowerCase()) // name начинается с X ...
                            || (t.javaDataType != null
                            && t.javaDataType.getClassName().toLowerCase().contains(("." + clazzName).toLowerCase()) // "длинный тип" содержит ".X"
                    )
                            )
                        return t; // FOUNDS
                }
            }
            return null; // NOT FOUND
        }

        public static SupportedTypes findTypeBySQL(int sqlTypeCode) {
            switch (sqlTypeCode) {
                case Types.VARCHAR: {
                    return SupportedTypes.STRING;
                }
                case Types.BIGINT: {
                    return SupportedTypes.LONG;
                }
                case Types.BOOLEAN: {
                    return SupportedTypes.BOOL;
                }
                case Types.DATE: {
                    return SupportedTypes.DATE;
                }
                case Types.DOUBLE: {
                    return SupportedTypes.DOUBLE;
                }
                case Types.FLOAT: {
                    return SupportedTypes.FLOAT;
                }
                case Types.INTEGER: {
                    return SupportedTypes.INTEGER;
                }
                case Types.NUMERIC: {
                    return SupportedTypes.NUMERIC;
                }
                case Types.SMALLINT: {
                    return SupportedTypes.INTEGER;
                }
                case Types.NULL: {
                    return SupportedTypes.NULL;
                }
                default: {
                    return SupportedTypes.STRING;
                }
            }
        }
    }
}
