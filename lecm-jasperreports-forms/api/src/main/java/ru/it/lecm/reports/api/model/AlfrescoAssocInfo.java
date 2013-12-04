package ru.it.lecm.reports.api.model;


/**
 * Описатеть связи-ассоциации Альфреско: название и тип связи.
 * @author rabdullin
 *
 */
public interface AlfrescoAssocInfo {

	/**
	 * Тип связи Альфреско, наример, для ассоциации "Контрагенты" 
	 * "lecm-contract:partner-assoc"
	 */
	String getAssocTypeName();
	void setAssocTypeName(String typeName);

	AssocKind getAssocKind();
	void setAssocKind(AssocKind assocKind);

	/**
	 * Вид Альфреско связи: "11", "1M", "M1", "MM"
	 * @author rabdullin
	 */
	public enum AssocKind {
		_11("11")
		, _1M("1M")
		, _M1("M1")
		, _MM("MM")
		;

		final private String mnemonic;

		private AssocKind(String mnemonic) {
			this.mnemonic = mnemonic;
		}

		/**
		 * @return двухбуквенное название вида связи: "11", "1M", "M1", "MM"  
		 */
		public String getMnemonic() {
			return mnemonic;
		}

		public static AssocKind findAssocKind(String nameOrMnemonic) {
			if (nameOrMnemonic != null) {
				for(AssocKind t: values()) {
					if ( nameOrMnemonic.equalsIgnoreCase(t.name())
						|| (t.mnemonic != null && nameOrMnemonic.equalsIgnoreCase(t.mnemonic))
					)
						return t; // FOUND
				}
			}
			return null; // NOT FOUND
		}

		@Override
		public String toString() {
			return this.mnemonic;
		}
	}
}