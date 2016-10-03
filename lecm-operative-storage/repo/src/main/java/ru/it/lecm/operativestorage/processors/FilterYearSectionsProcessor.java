/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.processors;

import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class FilterYearSectionsProcessor extends SearchQueryProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FilterYearSectionsProcessor.class);

	OperativeStorageService operativeStorageService;
	OrgstructureBean orgstructureService;

	private final String DEFAULT_QUERY = "@os\\-aspects\\:nomenclature\\-organization\\-assoc\\-ref:\"*\" OR ISNULL:\"os-aspects:nomenclature-organization-assoc-ref\" OR NOT EXISTS:\"os-aspects:nomenclature-organization-assoc-ref\"";

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setOperativeStorageService(OperativeStorageService operativeStorageService) {
		this.operativeStorageService = operativeStorageService;
	}

	@Override
	public String getQuery(Map<String, Object> params) {
		StringBuilder sbQuery = new StringBuilder();
		boolean allowAdmin = false;
		boolean centralized = operativeStorageService.isCetralized();

		if(centralized) {
			return DEFAULT_QUERY;
		}

		Object allowAdminStr = params != null ? params.get("allowAdmin") : null;
		if(allowAdminStr != null) {
			allowAdmin = Boolean.TRUE.equals(allowAdminStr);
		}

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		if(allowAdmin && orgstructureService.getEmployeeLogin(currentEmployee).equals("admin")) {
			return DEFAULT_QUERY;
		}

		NodeRef currentEmployeeOrg = orgstructureService.getEmployeeOrganization(currentEmployee);
		NodeRef currentEmployeeOrgUnit = orgstructureService.getUnitByOrganization(currentEmployeeOrg);

		sbQuery.append("@os\\-aspects\\:nomenclature\\-organization\\-assoc\\-ref:\"").append(currentEmployeeOrgUnit).append("\" OR ISNULL:\"os-aspects:nomenclature-organization-assoc-ref\" OR NOT EXISTS:\"os-aspects:nomenclature-organization-assoc-ref\"");

		return sbQuery.toString();
	}

}
