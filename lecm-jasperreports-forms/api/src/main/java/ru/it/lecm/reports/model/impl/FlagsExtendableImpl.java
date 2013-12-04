package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.FlagsExtendable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class FlagsExtendableImpl implements FlagsExtendable, Serializable {

	private static final long serialVersionUID = 1L;

	private Set<NamedValue> flags;

	public FlagsExtendableImpl() {
		super();
	}

	@Override
	public Set<NamedValue> flags() {
		if (this.flags == null)
			this.flags = new HashSet<NamedValue>();
		return this.flags;
	}

	/** for reflection */
	public Set<NamedValue> getFlags() {
		return flags();
	}

	public void setFlags(Set<NamedValue> flags) {
		this.flags = flags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
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
		final FlagsExtendableImpl other = (FlagsExtendableImpl) obj;
		if (flags == null) {
			if (other.flags != null)
				return false;
		} else if (!flags.equals(other.flags))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FlagsExtendableImpl [" + flags + "]";
	}

}
