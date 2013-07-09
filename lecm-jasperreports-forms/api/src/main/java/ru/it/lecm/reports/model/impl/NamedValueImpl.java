package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.Valueable;

/**
 * Именованое значение.
 * @author rabdullin
 *
 */
public class NamedValueImpl extends MnemonicNamedItem implements NamedValue {

	private Valueable value;

	public NamedValueImpl() {
		super();
	}

	public NamedValueImpl(String mnem, L18able name) {
		super(mnem, name);
	}

	public NamedValueImpl(String mnem) {
		super(mnem);
	}

	public NamedValueImpl(String mnem, String value) {
		this(mnem);
		setValue(value);
	}

/*
	@Override
	public int hashCode() {
		final int factor = 63;
		int result = super.hashCode();
		result += result * factor + ((value == null) ? 0 : value.hashCode());
		return result;
	}
 */

	@Override
	public boolean equals(Object x) {
		if (x == null)
			return false;

		if (!super.equals(x))
			return false;

		if (x.getClass().equals(this.getClass()))
			return false;

		final NamedValueImpl other = (NamedValueImpl) x;
		if (other.value == null) 
			return this.value == null;

		return value.equals(other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "\n\t\t NamedValueImpl['%s'='%s']\n", getMnem(), value));
		return builder.toString();
	}

	protected Valueable value() {
		if (this.value == null)
			this.value = new ValueableImpl();
		return this.value;
	}

	public String getValue() {
		return value().getValue();
	}

	public void setValue(String value) {
		this.value().setValue(value);
	}

}
