package ru.it.lecm.notifications.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 19.05.2017
 * Time: 13:51
 */
public class NotificationOnCreateExclusionPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
    private final static Logger logger = LoggerFactory.getLogger(NotificationOnCreateExclusionPolicy.class);
    private PolicyComponent policyComponent;
    private OrgstructureBean orgstructureBean;
    private BusinessJournalService businessJournalService;

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                NotificationsService.TYPE_NOTIFICATION_TEMPLATE, NotificationsService.ASSOC_NOTIFICATION_TEMPLATE_EXCLUSIONS_EMPLOYEE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                NotificationsService.TYPE_NOTIFICATION_TEMPLATE, NotificationsService.ASSOC_NOTIFICATION_TEMPLATE_EXCLUSIONS_EMPLOYEE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef template = associationRef.getSourceRef();
        NodeRef employee = associationRef.getTargetRef();

        Object exclusionsListProp = nodeService.getProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST);
        JSONObject exclusionsList;
        try {
            exclusionsList = new JSONObject(exclusionsListProp != null ? exclusionsListProp.toString() : "{}");
        } catch (JSONException ignored) {
            exclusionsList = new JSONObject();
        }
        try {
            JSONObject updatedList = new JSONObject();

            JSONArray rows = exclusionsList.has("rows") ? exclusionsList.getJSONArray("rows") : new JSONArray();

            JSONObject newExclusion = new JSONObject();
            newExclusion.put("employee", employee);
            newExclusion.put("created", BaseBean.DateFormatISO8601.format(new Date()));
            NodeRef creator = orgstructureBean.getEmployeeByPerson(AuthenticationUtil.getFullyAuthenticatedUser());
            newExclusion.put("creator", creator);
            newExclusion.put("creatorShortName", businessJournalService.getObjectDescription(creator));
            rows.put(newExclusion);
            updatedList.put("rows", rows);

            nodeService.setProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST, updatedList.toString());

            //Запись в бизнес-журнал
            List<String> objects = new ArrayList<>();
            objects.add(employee.toString());
            businessJournalService.log(template, "CREATE_TEMPLATE_EXCLUSION", "#initiator создал исключение для #object1 по правилу #mainobject", objects);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef template = associationRef.getSourceRef();
        NodeRef employee = associationRef.getTargetRef();

        Object exclusionsListProp = nodeService.getProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST);
        JSONObject exclusionsList;
        try {
            exclusionsList = new JSONObject(exclusionsListProp != null ? exclusionsListProp.toString() : "{}");
        } catch (JSONException ignored) {
            exclusionsList = new JSONObject();
        }
        try {
            JSONObject updatedList = new JSONObject();

            JSONArray rows = exclusionsList.has("rows") ? exclusionsList.getJSONArray("rows") : new JSONArray();

            JSONArray updatedArray = new JSONArray();
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                String employeeFromRow = row.getString("employee");
                if (!employeeFromRow.equals(employee.toString())) {
                    updatedArray.put(row);
                }
            }

            updatedList.put("rows", updatedArray);

            nodeService.setProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST, updatedList.toString());

            //Запись в бизнес-журнал
            List<String> objects = new ArrayList<>();
            objects.add(employee.toString());
            businessJournalService.log(template, "DELETE_TEMPLATE_EXCLUSION", "#initiator удалил исключение для #object1 по правилу #mainobject", objects);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
