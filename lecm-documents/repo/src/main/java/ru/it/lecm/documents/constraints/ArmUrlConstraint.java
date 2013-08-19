package ru.it.lecm.documents.constraints;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * User: AIvkin
 * Date: 19.08.13
 * Time: 11:45
 */
public class ArmUrlConstraint extends AbstractConstraint {
	private String armUrl;

	public String getArmUrl() {
		return armUrl;
	}

	public void setArmUrl(String armUrl) {
		this.armUrl = armUrl;
	}

	@Override
	public String getType() {
		return "LECM_PRESENT_STRING";
	}

	@Override
	protected void evaluateSingleValue(Object value) {
	}
}
