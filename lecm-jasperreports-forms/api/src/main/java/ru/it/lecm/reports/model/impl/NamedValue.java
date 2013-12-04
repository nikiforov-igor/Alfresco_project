package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.Mnemonicable;
import ru.it.lecm.reports.api.model.Valueable;

/**
 * Именованое значение.
 * @author rabdullin
 *
 */
public class NamedValue extends MnemonicNamedItem implements Mnemonicable, Valueable {

	private static final long serialVersionUID = 1L;

	private Valueable value;

	public NamedValue(String mnem) {
		super(mnem);
	}

	public NamedValue(String mnem, String value) {
		this(mnem);
		setValue(value);
	}

	@Override
	public boolean equals(Object x) {
		if (x == null)
			return false;

		if (!super.equals(x))
			return false;

		if (x.getClass().equals(this.getClass()))
			return false;

		final NamedValue other = (NamedValue) x;
		if (other.value == null) 
			return this.value == null;

		return value.equals(other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "\n\t\t NamedValue['%s'='%s']\n", getMnem(), value));
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
