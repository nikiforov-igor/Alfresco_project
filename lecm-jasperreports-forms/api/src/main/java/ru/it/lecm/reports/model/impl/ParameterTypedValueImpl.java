package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.ParameterTypedValue;

/**
 * Мнемоника здесь это суть параметра (например, "CONST", "REF", "INTERVAL")
 * @author rabdullin
 *
 */
public class ParameterTypedValueImpl
		extends MnemonicNamedItem
		implements ParameterTypedValue 
{

	private L18able[] prompts;
	private Object[] bounds;
	private Type type = Type.VALUE;
	private boolean required = false;

	public ParameterTypedValueImpl() {
		super();
	}

	public ParameterTypedValueImpl(String mnem, L18able name) {
		super(mnem, name);
	}

	public ParameterTypedValueImpl(String mnem) {
		super(mnem);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format( "%s [ mnem '%s', ", this.getClass().getSimpleName(), getMnem()) );
		if (this.bounds != null) {
			builder.append(String.format( ", {bound1 %s='%s'} ", getPrompt1().get( /*locale*/ null, /*default*/ "V1"), this.bounds[0]));
			if ( (this.bounds[1] != null) || (this.prompts != null && this.prompts[1] != null) )
				builder.append(String.format( ", {bound2 %s='%s'} ", getPrompt2().get( /*locale*/ null, /*default*/ "V2"), this.bounds[1]));
		}
		builder.append("]");
		return builder.toString();
	}

	private L18able[] prompts() {
		if (this.prompts == null)
			this.prompts = new L18able[2];
		return this.prompts;
	}

	private L18able safeGetPrompt(int i) {
		this.prompts();
		if (this.prompts[i] == null)
			this.prompts[i] = new L18Value();
		return this.prompts[i];
	}

	private Object[] bounds() {
		if (this.bounds == null)
			this.bounds = new Object[2];
		return this.bounds;
	}

	@Override
	public L18able getPrompt1() {
		return safeGetPrompt(0);
	}

	@Override
	public void setPrompt1(L18able value) {
		this.prompts()[0] = value;
	}

	@Override
	public L18able getPrompt2() {
		return safeGetPrompt(1);
	}

	@Override
	public void setPrompt2(L18able value) {
		this.prompts()[1] = value;
	}

	@Override
	public Object getBound1() {
		return bounds()[0];
	}

	@Override
	public void setBound1(Object value) {
		this.bounds()[0] = value;
	}

	@Override
	public Object getBound2() {
		return bounds()[1];
	}

	@Override
	public void setBound2(Object value) {
		this.bounds()[1] = value;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public void setType(Type value) {
		this.type = value;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public boolean isEmpty() {
		if (this.type == Type.RANGE) // для интервала - проверить обе границы ...
			return (getBound1() == null) && (getBound2() == null);

		return (getBound1() == null); // для остальных - только одна граница
	}
}
