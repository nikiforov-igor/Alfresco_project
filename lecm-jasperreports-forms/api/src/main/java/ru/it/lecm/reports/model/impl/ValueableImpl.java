package ru.it.lecm.reports.model.impl;

import java.io.Serializable;

import ru.it.lecm.reports.api.model.Valueable;

public class ValueableImpl implements Valueable, Serializable {

	private static final long serialVersionUID = 1L;

	private String value;

	public ValueableImpl() {
		super();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ValueableImpl other = (ValueableImpl) obj;
		if (value == null)
			return other.value == null;
		return value.equals(other.value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(value);
		return builder.toString();
	}
}