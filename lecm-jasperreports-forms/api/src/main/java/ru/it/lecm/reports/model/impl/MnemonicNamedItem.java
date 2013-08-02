package ru.it.lecm.reports.model.impl;

import java.util.Map;

import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;

/**
 * Значение, имеюшее мнемонику и L18-название.
 * Мнемоника регистрочувствительная.
 * Хеш вычисляется по mnem.
 * @author rabdullin
 *
 */
public class MnemonicNamedItem implements Mnemonicable, L18able {

	private String mnem;
	private L18able name;

	public MnemonicNamedItem() {
		super();
	}

	public MnemonicNamedItem(String mnem) {
		this( mnem, null);
	}

	public MnemonicNamedItem(String mnem, L18able name) {
		super();
		this.mnem = mnem;
		this.name = name;
	}

	public L18able getL18Name() {
		if (name == null) name = new L18Value();
		return name;
	}

	public void setL18Name(L18able value) {
		this.name = value;
	}

	@Override
	public String getDefault() {
		return get( "ru", null);
	}

	@Override
	public String getMnem() {
		return mnem;
	}

	@Override
	public void setMnem(String mnem) {
		this.mnem = mnem;
	}

	@Override
	public int hashCode() {
		return (mnem == null) ? 0 : mnem.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (this == x)
			return true;

		if (x == null)
			return mnem == null;

		if ( x.getClass() != this.getClass())
			return false;

		final MnemonicNamedItem other = (MnemonicNamedItem) x;

		if (other.mnem == null)
			return mnem == null;

		return mnem.equals(other.mnem);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (name == null) {
			builder.append( String.format( "mnem '%s'", mnem));
		} else {
			builder.append("\t[");
			builder.append( String.format( "mnem '%s'", mnem));
			builder.append( String.format( ", '%s'", name));
			builder.append("]\n");
		}
		return builder.toString();
	}

	public Map<String, String> getL18items() {
		return getL18Name().getL18items();
	}

	public void regItem(String locale, String translation) {
		getL18Name().regItem(locale, translation);
	}

	public String getStrict(String locale) {
		return getL18Name().getStrict(locale);
	}

	public String get(String locale, String l18default) {
		return getL18Name().get(locale, l18default);
	}

}
