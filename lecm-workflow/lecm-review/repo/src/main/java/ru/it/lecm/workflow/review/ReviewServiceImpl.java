package ru.it.lecm.workflow.review;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author kuper
 */
public class ReviewServiceImpl extends BaseBean {

    public static final String CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS = "NOT_REVIEWED";
    public static final String CONSTRAINT_REVIEW_TS_STATE_REVIEWED = "REVIEWED";
    public static final String CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED = "NOT_STARTED";
    public static final String CONSTRAINT_REVIEW_TS_STATE_CANCELLED = "CANCELLED";

    public static final String REVIEW_TS_NAMESPACE = "http://www.it.ru/logicECM/model/review-ts/1.0";
    public static final QName ASSOC_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-assoc");
    public static final QName ASSOC_REVIEW_TS_REVIEWER = QName.createQName(REVIEW_TS_NAMESPACE, "reviewer-assoc");
    public static final QName ASSOC_REVIEW_TS_INITIATOR = QName.createQName(REVIEW_TS_NAMESPACE, "initiator-assoc");
    public static final QName TYPE_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table");
    public static final QName PROP_REVIEW_TS_STATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-state");
    public static final QName PROP_REVIEW_TS_REVIEW_FINISH_DATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-finish-date");

    private DocumentTableService documentTableService;
    private OrgstructureBean orgstructureBean;

    public OrgstructureBean getOrgstructureBean() {
        return orgstructureBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public DocumentTableService getDocumentTableService() {
        return documentTableService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean needReviewByCurrentUser(NodeRef document) {
        NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
        Boolean result = false;
        if (null != tableData) {
            List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
            for (NodeRef reviewListRow : reviewList) {
                NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                if (currentEmployee.equals(itemEmployee)) {
                    String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
                    result = result || CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(state);
                }
            }
        }
        return result;
    }

    public void markReviewed(final NodeRef document) {
        NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
        if (null != tableData) {
            List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
            for (final NodeRef reviewListRow : reviewList) {
                NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                if (currentEmployee.equals(itemEmployee)) {
                    if (CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE))) {
                        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {

                            @Override
                            public Void doWork() throws Exception {
                                Map<QName, Serializable> properties = nodeService.getProperties(reviewListRow);
                                properties.put(PROP_REVIEW_TS_STATE, CONSTRAINT_REVIEW_TS_STATE_REVIEWED );
                                properties.put(PROP_REVIEW_TS_REVIEW_FINISH_DATE, new Date());
                                nodeService.setProperties(reviewListRow, properties);
                                return null;
                            }
                        });
                    }
                }
            }
        }
    }

    public Boolean canSendToReview(NodeRef document) {
        NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
        if (null != tableData) {
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
            List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
            for (NodeRef reviewListRow : reviewList) {
                NodeRef itemInitiator = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_INITIATOR, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
                if (currentEmployee.equals(itemInitiator) && CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED.equals(state)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Boolean canCancelReview(NodeRef document) {
        NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
        if (null != tableData) {
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
            List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
            for (NodeRef reviewListRow : reviewList) {
                NodeRef itemInitiator = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_INITIATOR, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
                if (currentEmployee.equals(itemInitiator) && CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(state)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
