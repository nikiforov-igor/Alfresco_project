package ru.it.lecm.secretary.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.secretary.SecretarySecurityService;

/**
 *
 * @author vmalygin
 */
public class SecretarySecurityJavascriptExtension extends BaseWebScript {

	private SecretarySecurityService secretarySecurityService;

	public void setSecretarySecurityService(SecretarySecurityService secretarySecurityService) {
		this.secretarySecurityService = secretarySecurityService;
	}

	public boolean addSecretary(final ScriptNode chief, final ScriptNode secretary) {
		return secretarySecurityService.addSecretary(chief.getNodeRef(), secretary.getNodeRef());
	}

	public boolean addSecretarySimple(final ScriptNode chief, final ScriptNode secretary) {
		return secretarySecurityService.addSecretarySimple(chief.getNodeRef(), secretary.getNodeRef());
	}

	public boolean addSecretaryBoss(final ScriptNode chief, final ScriptNode secretary) {
		return secretarySecurityService.addSecretaryBossOnly(chief.getNodeRef(), secretary.getNodeRef());
	}

	public boolean removeSecretary(final ScriptNode chief, final ScriptNode secretary) {
		return secretarySecurityService.removeSecretary(chief.getNodeRef(), secretary.getNodeRef());
	}
}
