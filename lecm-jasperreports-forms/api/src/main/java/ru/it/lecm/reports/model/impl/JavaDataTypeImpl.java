package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.JavaDataType;
import ru.it.lecm.reports.utils.ArgsHelper;

import java.sql.Types;
import java.util.*;

/**
 * Тип данных java в шаблонах.
 * Предполагается использование через регламентированный набор поддерживаемых
 * типов (@SEE SupportedTypes.javaDataType), так что конструктор "убран" в protected.
 *
 * @author rabdullin
 */
public class JavaDataTypeImpl
        extends JavaClassableImpl
        implements JavaDataType {
    private static final long serialVersionUID = 1L;

    protected JavaDataTypeImpl(String className) {
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
        DATE(Date.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
                return (value instanceof Date ? value : ArgsHelper.tryMakeDate(value.toString(), null));
            }
        },
        BOOL(Boolean.class.getName()) {
            @Override
            public Object getValueByRealType(Object value) {
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
                return (value instanceof List ? value : new ArrayList<Object>().add(value));
            }
        };

        final private JavaDataType javaDataType;

        private SupportedTypes(String clazzName) {
            javaDataType = (clazzName != null) ? new JavaDataTypeImpl(clazzName) : null;
        }

        public JavaDataType javaDataType() {
            return this.javaDataType;
        }

        public Object getValueByRealType(Object value) {
            return value.toString();
        }

        public static SupportedTypes findType(String clazzName) {
            if (clazzName != null) {
                // поиск точного соот-вия
                for (SupportedTypes t : values()) {
                    if (clazzName.equalsIgnoreCase(t.name()) // X совпадает с name полностью
                            || (t.javaDataType != null
                            && clazzName.equalsIgnoreCase(t.javaDataType.className()) // X совпадает с "длинным типом" ...
                    )
                            )
                        return t; // FOUNDS
                }

                // поиск как подстрок ...
                for (SupportedTypes t : values()) {
                    if (t.name().toLowerCase().startsWith(clazzName.toLowerCase()) // name начинается с X ...
                            || (t.javaDataType != null
                            && t.javaDataType.className().toLowerCase().contains(("." + clazzName).toLowerCase()) // "длинный тип" содержит ".X"
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
