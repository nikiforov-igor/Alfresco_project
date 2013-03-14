package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 28.02.13
 * Time: 16:28
 */
public class DocumentServiceImpl extends BaseBean implements DocumentService {
    public void init() {
    }

    private NodeService nodeService;
    private OrgstructureBean orgstructureService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    @Override
    public Float getRating(NodeRef documentNodeRef) {
        return (Float) nodeService.getProperty(documentNodeRef, DocumentService.PROP_RATING);
    }

    @Override
    public Integer getRatedPersonCount(NodeRef documentNodeRef) {
        return (Integer) nodeService.getProperty(documentNodeRef, DocumentService.PROP_RATED_PERSONS_COUNT);
    }

    @Override
    public Integer getMyRating(NodeRef documentNodeRef) {
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        if (currentEmployee != null) {
            for (int i = 1; i < 6; i++) {
                List<NodeRef> rated = (List<NodeRef>) nodeService.getProperty(documentNodeRef, QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + i));

                if (rated != null && rated.contains(currentEmployee)) {
                    return i;
                }
            }
        }

        return null;
    }

    @Override
    public Integer setMyRating(NodeRef documentNodeRef, Integer rating) {
        if (rating > 0 && rating < 6) {
            NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

            if (currentEmployee != null) {
                QName thisRatedList;
                List<NodeRef> ratedList;
                Integer myRating = getMyRating(documentNodeRef);

                if (myRating != null) {
                    thisRatedList = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + myRating);
                    ratedList = (List<NodeRef>) nodeService.getProperty(documentNodeRef, thisRatedList);

                    if (ratedList != null && !ratedList.isEmpty()) {
                        ratedList.remove(currentEmployee);
                    }
                    nodeService.setProperty(documentNodeRef, thisRatedList, (Serializable) ratedList);
                }
                thisRatedList = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + rating);
                ratedList = (List<NodeRef>) nodeService.getProperty(documentNodeRef, thisRatedList);
                if (ratedList == null) {
                    ratedList = new ArrayList<NodeRef>();
                }
                ratedList.add(currentEmployee);
                nodeService.setProperty(documentNodeRef, thisRatedList, (Serializable) ratedList);
                refreshValues(documentNodeRef);
                return getMyRating(documentNodeRef);
            }
        }
        return null;
    }

    private void refreshValues(NodeRef documentNodeRef) {
        int personsCount = 0;
        int summaryRating = 0;
        int size;

        for (int i = 1; i < 6; i++) {
            List<NodeRef> rated = (List<NodeRef>) nodeService.getProperty(documentNodeRef, QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + i));

            if (rated != null && !rated.isEmpty()) {
                size = rated.size();
                personsCount += size;
                summaryRating += (size * i);
            }
        }

        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATED_PERSONS_COUNT, personsCount);
        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATING, (float) summaryRating / personsCount);
    }

}
