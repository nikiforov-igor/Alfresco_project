package ru.it.lecm.reports.model;

import ru.it.lecm.reports.api.model.JavaClassable;
import ru.it.lecm.reports.api.model.L18able;


public class JavaClassableImpl
		extends MnemonicNamedItem 
		implements JavaClassable
{
	private String className;

	public JavaClassableImpl() {
		super();
	}

	public JavaClassableImpl(String className) {
		this(className, null, null);
	}

	public JavaClassableImpl(String className, String mnem) {
		this(className, mnem, null);
	}

	public JavaClassableImpl(String className, String mnem, L18able name) {
		super( mnem, name);
		this.className = className;
	}

	@Override
	public String className() {
		return this.className;
	}

	@Override
	public void setClassName(String value) {
		this.className = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JavaClassableImpl other = (JavaClassableImpl) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "%s [", this.getClass().getName() ));
		builder.append( String.format( "mnem '%s'", getMnem()));
		builder.append( String.format( ", className '%s'", className));
		builder.append( String.format( ", l18 %s", getL18items()));
		builder.append("]");
		return builder.toString();
	}

}