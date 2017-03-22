package ru.it.lecm.documents.constraints;

import java.util.HashMap;
import java.util.Map;

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
		return "LECM_ARM_URL";
	}

	@Override
	protected void evaluateSingleValue(Object value) {
	}
	
	@Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        
        params.put("armUrl", this.armUrl);
        
        return params;
    }
}
