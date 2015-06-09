package ru.it.lecm.secretary.beans;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretarySecurityService;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.Types.SGPrivateMeOfUser;
import ru.it.lecm.security.Types.SGSecretaryOfUser;
import ru.it.lecm.security.beans.SgNameResolver;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

/**
 *
 * @author vmalygin
 */
public class SecretarySecurityServiceImpl implements SecretarySecurityService {

	private final static Logger logger = LoggerFactory.getLogger(SecretarySecurityService.class);

	private AuthorityService authorityService;
	private OrgstructureBean orgstructureService;
	private IOrgStructureNotifiers lecmSecurityGroupsService;
	private final SgNameResolver sgnm = new SgNameResolver(logger);

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setLecmSecurityGroupsService(IOrgStructureNotifiers lecmSecurityGroupsService) {
		this.lecmSecurityGroupsService = lecmSecurityGroupsService;
	}

	public void init() {
		sgnm.setAuthorityService(authorityService);
	}

	@Override
	public boolean addSecretary(NodeRef chief, NodeRef secretary) {
		if (addSecretarySimple(chief, secretary)) {
			logger.info("Secretary was succesfully added to simple chief");
		}
		if (addSecretaryBossOnly(chief, secretary)) {
			logger.info("Secretary was succesfully added to boss/delegate chief");
		}
		return true;
	}

	@Override
	public boolean addSecretarySimple(NodeRef chief, NodeRef secretary) {
		String chiefLogin = orgstructureService.getEmployeeLogin(chief);
		String secretaryLogin = orgstructureService.getEmployeeLogin(secretary);

		SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(chief.getId(), chiefLogin);
		SGPrivateMeOfUser sgMe = SGKind.getSGMeOfUser(chief.getId(), chiefLogin);

		//включение группы SG_SECRETARY в группу SG_ME
		lecmSecurityGroupsService.sgInclude(sgSecretary, sgMe);

		//включение секретаря
		String secretaryAuthorityIdentifier = authorityService.getName(AuthorityType.USER, secretaryLogin);
		String sgSecretaryGroup = sgnm.makeSGName(sgSecretary.getAlfrescoSuffix());
		lecmSecurityGroupsService.ensureUserParent(secretaryAuthorityIdentifier, sgSecretaryGroup);
		return true;
	}

	@Override
	public boolean addSecretaryBossOnly(NodeRef chief, NodeRef secretary) {
		//если руководитель занимает руководящую должность по оргштатке или согласно настроек делегирования
		if (orgstructureService.isBoss(chief, true)) {
			String chiefLogin = orgstructureService.getEmployeeLogin(chief);
			SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(chief.getId(), chiefLogin);
			String sgSecretaryGroup = sgnm.makeSGName(sgSecretary.getAlfrescoSuffix());

			//находим все секьюрити группы в которые входит chief и фильтруем _LECM$OUSV%
			Set<String> allChiefAuthorities = authorityService.getAuthoritiesForUser(chiefLogin);
			Set<String> svChiefAuthorities = Sets.filter(allChiefAuthorities, new Predicate<String>() {

				@Override
				public boolean apply(String input) {
					return input.contains(Types.PFX_LECM + Types.SFX_SV);
				}
			});

			for (String svAuthotiry : svChiefAuthorities) {
				lecmSecurityGroupsService.ensureParent(sgSecretaryGroup, svAuthotiry);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean removeSecretary(NodeRef chief, NodeRef secretary) {
		String chiefLogin = orgstructureService.getEmployeeLogin(chief);
		String secretaryLogin = orgstructureService.getEmployeeLogin(secretary);

		SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(chief.getId(), chiefLogin);

		//исключить секретаря
		String secretaryAuthorityIdentifier = authorityService.getName(AuthorityType.USER, secretaryLogin);
		String sgSecretaryGroup = sgnm.makeSGName(sgSecretary.getAlfrescoSuffix());
		lecmSecurityGroupsService.removeUserParent(secretaryAuthorityIdentifier, sgSecretaryGroup);

		//удалить группу SG_SECRETARY если она пустая и существует
		removeSGSecretary(sgSecretaryGroup);
		return true;
	}

	@Override
	public void makeChiefBossOrEmployee(NodeRef chief, NodeRef unit, boolean isBoss) {
		String chiefLogin = orgstructureService.getEmployeeLogin(chief);
		SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(chief.getId(), chiefLogin);
//		String sgSecretaryGroup = sgnm.makeSGName(sgSecretary.getAlfrescoSuffix());

		SGPosition sgSV = Types.SGKind.SG_SV.getSGPos(unit.getId());
		if (isBoss) {
			lecmSecurityGroupsService.sgInclude(sgSecretary, sgSV);
		} else {
			lecmSecurityGroupsService.sgExclude(sgSecretary, sgSV);
		}
	}

	@Override
	public void makeSecretaryBossOrEmployee(NodeRef secretary, boolean isBoss) {
		if (isBoss) {
			String secretaryLogin = orgstructureService.getEmployeeLogin(secretary);
			String secretaryAuthorityIdentifier = authorityService.getName(AuthorityType.USER, secretaryLogin);

			//находим все секьюрити группы в которые входит secretary и фильтруем _LECM$SECRETARY%
			Set<String> allSecretaryAuthorities = authorityService.getAuthoritiesForUser(secretaryLogin);
			Set<String> secretarySecretaryAuthorities = Sets.filter(allSecretaryAuthorities, new Predicate<String>() {

				@Override
				public boolean apply(String input) {
					return input.contains(Types.PFX_LECM + Types.SFX_SECRETARY);
				}
			});

			for (String secretaryAuthotiry : secretarySecretaryAuthorities) {
				lecmSecurityGroupsService.removeUserParent(secretaryAuthorityIdentifier, secretaryAuthotiry);
				//удалить группу SG_SECRETARY если она пустая и существует
				removeSGSecretary(secretaryAuthotiry);
			}
		} else {
			//no op
		}
	}

	private boolean removeSGSecretary(String sgSecretaryGroup, boolean purge) {
		boolean exists = authorityService.authorityExists(sgSecretaryGroup);
		boolean removed = false;
		if (exists && (purge || authorityService.getContainedAuthorities(AuthorityType.USER, sgSecretaryGroup, true).isEmpty())) {
			authorityService.deleteAuthority(sgSecretaryGroup);
			removed = true;
		}
		return removed;
	}

	private boolean removeSGSecretary(String sgSecretaryGroup) {
		return removeSGSecretary(sgSecretaryGroup, false);
	}

	private boolean purgeSGSecretary(String sgSecretaryGroup) {
		return removeSGSecretary(sgSecretaryGroup, true);
	}


	private boolean removeSGSecretary(NodeRef employee, boolean purge) {
		String employeeLogin = orgstructureService.getEmployeeLogin(employee);
		SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(employee.getId(), employeeLogin);
		String sgSecretaryGroup = sgnm.makeSGName(sgSecretary.getAlfrescoSuffix());
		return removeSGSecretary(sgSecretaryGroup, purge);
	}

	@Override
	public boolean removeSGSecretary(NodeRef employee) {
		return removeSGSecretary(employee, false);
	}

	@Override
	public boolean purgeSGSecretary(NodeRef employee) {
		return removeSGSecretary(employee, true);
	}

	@Override
	public void notifyChiefDelegationChanged(NodeRef bossEmployee, NodeRef chiefBossAssistant, boolean created) {
		String employeeLogin = orgstructureService.getEmployeeLogin(chiefBossAssistant);
		SGSecretaryOfUser sgSecretary = SGKind.getSGSecretaryOfUser(chiefBossAssistant.getId(), employeeLogin);
		// получить все подразделения, в которых делегирующий является руководителем ...
		final List<NodeRef> orgsBoss = orgstructureService.getEmployeeUnits(bossEmployee, true, false);
		for(NodeRef orgUnit: orgsBoss) {
			// руководящая позиция подразделения ...
			SGPosition sgSV = Types.SGKind.SG_SV.getSGPos(orgUnit.getId());
			if (created) {
				// sgNotifier.sgExclude( sgSV, sgDestMe); // отвязать SV-группу своего подраздедения (SVOU) от себя ("anti-recurse step")
				lecmSecurityGroupsService.sgInclude( sgSecretary, sgSV); // привязать себя к SVOU
			} else {
				lecmSecurityGroupsService.sgExclude( sgSecretary, sgSV); // ("anti-recurse step") отвязать себя от SVOU
				// sgNotifier.sgInclude( sgSV, sgDestMe); // привязать SVOU к себе
			}
		}
	}
}
