package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.JavaDataType;

/**
 * Тип данных java в шаблонах.
 * Предполагается использование через регламентированный набор поддерживаемых 
 * типов (@SEE SupportedTypes.javaDataType), так что конструктор "убран" в protected.
 *
 * @author rabdullin
 */
public class JavaDataTypeImpl
		extends JavaClassableImpl
		implements JavaDataType
{

	protected JavaDataTypeImpl(String className) {
		super(className);
	}


	/**
	 * Набор типов поддерживаемых для шаблонов.
	 * @author rabdullin
	 */
	public enum SupportedTypes {
		NULL(null)
		, STRING(java.lang.String.class.getName())
		, DATE(java.util.Date.class.getName())

		, INTEGER(java.lang.Integer.class.getName())
		, FLOAT(java.lang.Float.class.getName())
		, NUMERIC(java.lang.Number.class.getName())
		;

		final private JavaDataType javaDataType;

		private SupportedTypes(String clazzName) {
			javaDataType = (clazzName != null) ? new JavaDataTypeImpl(clazzName) : null;
		}

		public JavaDataType javaDataType() {
			return this.javaDataType;
		}

		public static SupportedTypes findType(String clazzName) {
			if (clazzName != null) {
				for(SupportedTypes t: values()) {
					if ( clazzName.equalsIgnoreCase(t.name())
							||
						(
						t.javaDataType != null && clazzName.equalsIgnoreCase(t.javaDataType.className())
						)
					)
						return t; // FOUNDS
				}
			}
			return null; // NOT FOUND
		}
	}
}
