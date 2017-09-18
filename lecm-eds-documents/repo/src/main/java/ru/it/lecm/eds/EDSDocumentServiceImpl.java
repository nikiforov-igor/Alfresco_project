package ru.it.lecm.eds;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 24.01.14
 * Time: 12:48
 */
public class EDSDocumentServiceImpl extends BaseBean implements EDSDocumentService {
    private final static Logger logger = LoggerFactory.getLogger(EDSDocumentServiceImpl.class);

    private String calendarDayTypeString = "к.д.";
    private String workDayTypeString = "р.д.";
    private String limitlessString = "Без срока";

    private IWorkCalendar calendarBean;
    private OrgstructureBean orgstructureService;

    private DocumentAttachmentsService documentAttachmentsService;

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    private NamespaceService namespaceService;

    public OrgstructureBean getOrgstructureService() {
        return orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setCalendarDayTypeString(String calendarDayTypeString) {
        this.calendarDayTypeString = calendarDayTypeString;
    }

    public void setWorkDayTypeString(String workDayTypeString) {
        this.workDayTypeString = workDayTypeString;
    }

    public void setLimitlessString(String limitlessString) {
        this.limitlessString = limitlessString;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void sendChildChangeSignal(NodeRef baseDoc) {
        doIncrementProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT);
    }

    @Override
    public void resetChildChangeSignal(NodeRef baseDoc) {
        try {
            nodeService.setProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT, 0);
        } catch (ConcurrencyFailureException ex) {
            logger.warn("Send signal at the same time", ex);
        }
    }

    @Override
    public void sendChangeDueDateSignal(NodeRef doc, Long shiftSize, Boolean limitless, Date newDate, String reason) {
        Map<QName, Serializable> props = new HashMap<>();
        props.put(PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_SIZE, shiftSize);
        props.put(PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_LIMITLESS, limitless);
        props.put(PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_NEW_DATE, newDate);
        props.put(PROP_CHANGE_DUE_DATE_SIGNAL_SHIFT_REASON, reason);
        nodeService.addAspect(doc, ASPECT_CHANGE_DUE_DATE_SIGNAL, props);
    }

    @Override
    public void resetChangeDueDateSignal(NodeRef doc) {
        nodeService.removeAspect(doc, ASPECT_CHANGE_DUE_DATE_SIGNAL);
    }

    @Override
    public String getComplexDateText(String radio, Date date, String daysType, Integer daysCount) {
        String result = null;
        if (COMPLEX_DATE_RADIO_LIMITLESS.equals(radio)) {
            result = this.limitlessString;
        } else if (COMPLEX_DATE_RADIO_DATE.equals(radio) && date != null) {
            DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
            result = formater.format(date);
        } else if (COMPLEX_DATE_RADIO_DAYS.equals(radio) && daysType != null && daysCount != null) {
            if (COMPLEX_DATE_DAYS_WORK.equals(daysType)) {
                result = daysCount + " " + workDayTypeString;
            } else if (COMPLEX_DATE_DAYS_CALENDAR.equals(daysType)) {
                result = daysCount + " " + calendarDayTypeString;
            }
        }
        return result;
    }

    @Override
    public Date convertComplexDate(String radio, Date date, String daysType, Integer daysCount) {
        if (COMPLEX_DATE_RADIO_DATE.equals(radio)) {
            return date;
        } else if (COMPLEX_DATE_RADIO_DAYS.equals(radio) && daysCount != null && daysType != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (COMPLEX_DATE_DAYS_WORK.equals(daysType)) {
                return calendarBean.getNextWorkingDateByDays(cal.getTime(), daysCount);
            } else if (COMPLEX_DATE_DAYS_CALENDAR.equals(daysType)) {
                cal.add(Calendar.DAY_OF_YEAR, daysCount);
                return cal.getTime();
            }
        }
        return null;
    }

    @Override
    public void sendCompletionSignal(NodeRef document, String reason, NodeRef signalSender) {
        if (document != null && nodeService.exists(document)) {
            nodeService.setProperty(document, EDSDocumentService.PROP_COMPLETION_SIGNAL, true);
            nodeService.setProperty(document, EDSDocumentService.PROP_COMPLETION_SIGNAL_REASON, reason);
            nodeService.setProperty(document, EDSDocumentService.PROP_COMPLETION_SIGNAL_CLOSE_CHILD, true);
            if (signalSender == null || !nodeService.exists(signalSender)) {
                signalSender = orgstructureService.getCurrentEmployee();
            }
            if (signalSender != null) {
                nodeService.createAssociation(document, signalSender, EDSDocumentService.ASSOC_COMPLETION_SIGNAL_SENDER);
            }
        }
    }

    @Override
    public void resetCompletionSignal(NodeRef document) {
        if (document != null && nodeService.exists(document)) {
            nodeService.removeAspect(document, EDSDocumentService.ASPECT_COMPLETION_SIGNAL);
        }
    }

    @Override
    public boolean isSignedOnPaper(NodeRef document) {
        if (document != null) {
            Boolean isSignedOnPaper = (Boolean) nodeService.getProperty(document, PROP_SIGNED_ON_PAPER);
            return Boolean.TRUE.equals(isSignedOnPaper);
        }
        return false;
    }

    public List<NodeRef> getCategoriesToSign(NodeRef documentRef, String documentTypeAssoc) {
        List<NodeRef> result = new ArrayList<>();
        if (documentRef != null) {
            QName documentTypeQName = QName.createQName(documentTypeAssoc, namespaceService);
            NodeRef docTypeDicRef = findNodeByAssociationRef(documentRef, documentTypeQName, null, ASSOCIATION_TYPE.TARGET);

            List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
            if (categories != null && docTypeDicRef != null) {
                QName categoriesToSignQName = QName.createQName("lecm-doc-dic-dt:categories-of-attachments-to-sign", namespaceService);
                String categoriesToSign = (String) nodeService.getProperty(docTypeDicRef, categoriesToSignQName);
                if (categoriesToSign != null) {
                    String[] categoriesToSignArr = categoriesToSign.split(";");
                    if (categoriesToSignArr.length > 0) {
                        if (categoriesToSignArr[0].equals("")) {
                            result.addAll(categories);
                        } else {
                            for (int i = 0; i < categoriesToSignArr.length; i++) {
                                for (NodeRef categoryNodeRef: categories) {
                                    String categoryName = (String) nodeService.getProperty(categoryNodeRef, ContentModel.PROP_TITLE);
                                    if (categoryName.equals(categoriesToSignArr[i])) {
                                        result.add(categoryNodeRef);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    result.addAll(categories);
                }
            }
        }
        return result;
    }
}
