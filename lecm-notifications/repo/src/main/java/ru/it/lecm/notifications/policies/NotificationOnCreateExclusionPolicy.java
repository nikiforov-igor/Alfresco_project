package ru.it.lecm.notifications.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.Date;

/**
 * User: dbashmakov
 * Date: 19.05.2017
 * Time: 13:51
 */
public class NotificationOnCreateExclusionPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
    private final static Logger logger = LoggerFactory.getLogger(NotificationOnCreateExclusionPolicy.class);
    private PolicyComponent policyComponent;
    private OrgstructureBean orgstructureBean;

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

        try {
            JSONArray rows = getExclusionsListRows(template);

            JSONObject newExclusion = new JSONObject();
            newExclusion.put("employee", employee);
            newExclusion.put("created", ISO8601DateFormat.format(new Date()));
            NodeRef creator = orgstructureBean.getEmployeeByPerson(AuthenticationUtil.getFullyAuthenticatedUser());
            newExclusion.put("creator", creator);
            rows.put(newExclusion);

            updateExclusions(template, rows);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef associationRef) {
        NodeRef template = associationRef.getSourceRef();
        NodeRef employee = associationRef.getTargetRef();

        try {
            JSONArray rows = getExclusionsListRows(template);

            JSONArray updatedArray = new JSONArray();
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                String employeeFromRow = row.getString("employee");
                if (!employeeFromRow.equals(employee.toString())) {
                    updatedArray.put(row);
                }
            }

            updateExclusions(template, updatedArray);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONArray getExclusionsListRows(NodeRef template) throws JSONException {
        Object exclusionsListProp = nodeService.getProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST);
        JSONObject exclusionsList;
        try {
            exclusionsList = new JSONObject(exclusionsListProp != null ? exclusionsListProp.toString() : "{}");
        } catch (JSONException ignored) {
            exclusionsList = new JSONObject();
        }
        return exclusionsList.has("rows") ? exclusionsList.getJSONArray("rows") : new JSONArray();
    }

    private void updateExclusions(NodeRef template, JSONArray exclusions) throws JSONException {
        JSONObject updatedList = new JSONObject();
        updatedList.put("rows", exclusions);
        nodeService.setProperty(template, NotificationsService.PROP_NOTIFICATION_TEMPLATE_EXCLUSIONS_LIST, updatedList.toString());
    }
}
