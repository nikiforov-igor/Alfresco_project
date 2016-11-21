package ru.it.lecm.workflow.routes.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class RoutesModel {

	public final static String ROUTES_URL = "http://www.it.ru/logicECM/model/workflow/routes/1.0";

	public final static QName TYPE_ROUTE = QName.createQName(ROUTES_URL, "route");
	public final static QName PROP_ROUTE_AVAILABILITY_CONDITION = QName.createQName(ROUTES_URL, "routeAvailabilityCondition");
	public final static QName PROP_ROUTE_INTERRUPT_AFTER_REJECT = QName.createQName(ROUTES_URL, "routeInterruptAfterReject");
	public final static QName PROP_ROUTE_NOTIFY_ABOUT_EVERY_DECISION = QName.createQName(ROUTES_URL, "routeNotifyAboutEveryDecision");
	public final static QName PROP_ROUTE_EDITABLE = QName.createQName(ROUTES_URL, "routeEditable");
	public final static QName ASSOC_ROUTE_ORGANIZATION_UNIT = QName.createQName(ROUTES_URL, "routeOrganizationUnitAssoc");
	public final static QName ASSOC_ROUTE_ORGANIZATION = QName.createQName(ROUTES_URL, "routeOrganizationAssoc");
	public final static QName PROP_ROUTE_START_DATE = QName.createQName(ROUTES_URL, "routeStartDate");
	public final static QName PROP_ROUTE_COMPLETE_DATE = QName.createQName(ROUTES_URL, "routeCompleteDate");
	public final static QName ASSOC_ROUTE_INITIATOR_EMPLOYEE = QName.createQName(ROUTES_URL, "initiatorEmployeeAssoc");

	public final static QName TYPE_STAGE = QName.createQName(ROUTES_URL, "stage");
	public final static QName PROP_STAGE_WORKFLOW_TYPE = QName.createQName(ROUTES_URL, "stageWorkflowType");
	public final static QName PROP_STAGE_WORKFLOW_TERM = QName.createQName(ROUTES_URL, "stageWorkflowTerm");
	public final static QName PROP_STAGE_EXPRESSION = QName.createQName(ROUTES_URL, "stageExpression");

	public final static QName TYPE_STAGE_ITEM = QName.createQName(ROUTES_URL, "stageItem");
	public final static QName PROP_STAGE_ITEM_TERM = QName.createQName(ROUTES_URL, "stageItemTerm");
	public final static QName PROP_STAGE_ITEM_USERNAME = QName.createQName(ROUTES_URL, "stageItemUserName");
	public final static QName PROP_STAGE_ITEM_DUE_DATE = QName.createQName(ROUTES_URL, "stageItemDueDate");
	public final static QName PROP_STAGE_ITEM_START_DATE = QName.createQName(ROUTES_URL, "stageItemStartDate");
	public final static QName PROP_STAGE_ITEM_COMPLETE_DATE = QName.createQName(ROUTES_URL, "stageItemCompleteDate");
	public final static QName PROP_STAGE_ITEM_DECISION = QName.createQName(ROUTES_URL, "stageItemDecision");
	public final static QName PROP_STAGE_ITEM_COMMENT = QName.createQName(ROUTES_URL, "stageItemComment");
	public final static QName ASSOC_STAGE_ITEM_EMPLOYEE = QName.createQName(ROUTES_URL, "stageItemEmployeeAssoc");
	public final static QName ASSOC_STAGE_ITEM_MACROS = QName.createQName(ROUTES_URL, "stageItemMacrosAssoc");

	private RoutesModel() {
		throw new IllegalStateException("Class RoutesModel can not be instantiated");
	}
}
