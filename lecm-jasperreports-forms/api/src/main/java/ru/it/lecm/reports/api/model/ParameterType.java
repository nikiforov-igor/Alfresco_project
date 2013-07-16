package ru.it.lecm.reports.api.model;


/**
 * Тип параметра опредеялется фактически мнемоникой.
 * Предполагается, что bound-значения могут верно отрабатываться провайдером.
 * @author rabdullin
 */
public interface ParameterType extends Mnemonicable, L18able {

	/*
	 * Мнемоника поддерживаемых типов параметров
	 */
	// final public static String TYPE_VALUE = "PARAM_TYPE_VALUE";
	// final public static String TYPE_LIST = "PARAM_TYPE_LIST";
	// final public static String TYPE_RANGE = "PARAM_TYPE_RANGE";

	/**
	 * Подсказка для параметров, задаваемых единственным значением или значение слева.
	 * @return
	 */
	L18able getPrompt1();
	void setPrompt1(L18able value);

	/**
	 * Если параметр имеет единственное значение - не используется, иначе подсказка для значения параметра справа.
	 * @return
	 */
	L18able getPrompt2();
	void setPrompt2(L18able value);

	AlfrescoAssocInfo getAlfrescoAssoc();
	void setAlfrescoAssoc(AlfrescoAssocInfo assoc);

	/**
	 * Тип параметра
	 * @return
	 */
	Type getType();
	void setType(Type type);

	/**
	 * Поддерживаемые типы параметров
	 */
	public enum Type {
		  VALUE( "PARAM_TYPE_VALUE")
		, LIST( "PARAM_TYPE_LIST")
		, RANGE( "PARAM_TYPE_RANGE")
		;

		final private String mnemonic;

		private Type(String mnemonic) {
			this.mnemonic = mnemonic;
		}

		/**
		 * @return alfresco-название элемента справочника параметров
		 */
		public String getMnemonic() {
			return mnemonic;
		}

		public static Type findType(String nameOrMnemonic) {
			if (nameOrMnemonic != null) {
				for(Type t: values()) {
					if ( nameOrMnemonic.equalsIgnoreCase(t.name())
						|| (t.mnemonic != null && nameOrMnemonic.equalsIgnoreCase(t.mnemonic))
					)
						return t; // FOUND
				}
			}
			return null; // NOT FOUND
		}
	}
}
