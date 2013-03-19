package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private BusinessJournalService businessJournalService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }
    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    @Override
    public String getRating(NodeRef documentNodeRef) {
        return (String) nodeService.getProperty(documentNodeRef, DocumentService.PROP_RATING);
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

                //обновить хранимые значения (среднего рейтинга и количества проголосовавших)
                refreshValues(documentNodeRef);

                //логировать изменения в журнал
                List<String> ratingList = new ArrayList<String>();
                ratingList.add(rating.toString());
                businessJournalService.log(documentNodeRef, DocumentEventCategory.SET_RATING, "Сотрудник #initiator присвоил рейтинг #object1 документу \"#mainobject\"", ratingList);

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
        BigDecimal rating = (new BigDecimal((float) summaryRating / personsCount)).setScale(1, BigDecimal.ROUND_HALF_UP);

        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATED_PERSONS_COUNT, personsCount);
        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATING, rating.toString());
    }

}
