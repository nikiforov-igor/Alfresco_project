package ru.it.lecm.reports.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.reports.api.ReportsManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: dbashmakov
 * Date: 18.06.13
 * Time: 17:45
 */
public class ReportsEditorService extends BaseBean {

	private final static Logger logger = LoggerFactory.getLogger(ReportsEditorService.class);

    public static final String RE_ROOT_NAME = "Сервис Редактор Отчетов";
    public static final String RE_ROOT_ID = "RE_ROOT_ID";
    public static final String RE_DICTIONARY_ROOT_NAME = "Справочники";
    public static final String RE_DICTIONARY_ROOT_ID = "RE_DICTIONARY_ROOT_ID";
    public static final String RE_SOURCES_ROOT_NAME = "Наборы данных";
    public static final String RE_SOURCES_ROOT_ID = "RE_SOURCES_ROOT_ID";
    public static final String RE_TEMPLATES_ROOT_NAME = "Шаблоны";
    public static final String RE_TEMPLATES_ROOT_ID = "RE_TEMPLATES_ROOT_ID";
    public static final String RE_REPORTS_ROOT_NAME = "Отчеты";
    public static final String RE_REPORTS_ROOT_ID = "RE_REPORTS_ROOT_ID";
    public static final String RE_TEMPLATES_FILES_ROOT_NAME = "Файлы";
    public static final String RE_TEMPLATES_FILES_ROOT_ID = "RE_TEMPLATES_FILES_ROOT_ID";

    private ReportsManager reportsManager;

    @Override
    public NodeRef getServiceRootFolder() {
	return getFolder(RE_ROOT_ID);
    }

    public NodeRef getReportsRootFolder() {
	return getFolder(RE_REPORTS_ROOT_ID);
    }

    public NodeRef getSourcesRootFolder() {
	return getFolder(RE_REPORTS_ROOT_ID);
    }

    public NodeRef getTemplatesRootFolder() {
	return getFolder(RE_TEMPLATES_ROOT_ID);
    }

    public NodeRef getDictionariesRootFolder() {
	return getFolder(RE_DICTIONARY_ROOT_ID);
    }

    public List<NodeRef> getReportTypes() {
        NodeRef typesDictionary = nodeService.getChildByName(getDictionariesRootFolder(), ContentModel.ASSOC_CONTAINS, "Тип отчета");
        return getElements(typesDictionary, ReportsEditorModel.TYPE_REPORT_TYPE);
    }

    public List<NodeRef> getDataSources() {
        return getElements(getSourcesRootFolder(), ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
    }

    public List<NodeRef> getTemplates() {
        return getElements(getTemplatesRootFolder(), ReportsEditorModel.TYPE_REPORT_TEMPLATE);
    }

    public List<NodeRef> getReportTemplates(NodeRef reportId) {
        List<NodeRef> templates = new ArrayList<NodeRef>();
        if (reportId != null) {
            List<AssociationRef> templatesAssocs = nodeService.getTargetAssocs(reportId, ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);
            for (AssociationRef templatesAssoc : templatesAssocs) {
                templates.add(templatesAssoc.getTargetRef());
            }
        }

        return templates;
    }

    public List<NodeRef> getDataColumnTypeByClass(String className) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        NodeRef typesDictionary = nodeService.getChildByName(getDictionariesRootFolder(), ContentModel.ASSOC_CONTAINS, "Тип столбцов");
        List<NodeRef> dicElements = getElements(typesDictionary, ReportsEditorModel.TYPE_REPORT_COLUMN_TYPE);
        for (NodeRef dicElement : dicElements) {
            Serializable recordClass = nodeService.getProperty(dicElement, ReportsEditorModel.PROP_REPORT_COLUMN_TYPE_CLASS);
            if (recordClass != null && recordClass.equals(className) && !isArchive(dicElement)) {
                results.add(dicElement);
            }
        }
        return results;
    }

    private List<NodeRef> getElements(NodeRef parent, QName type) {
        List<NodeRef> result = new ArrayList<NodeRef>();

        Set<QName> childType = new HashSet<QName>();
        childType.add(type);

        List<ChildAssociationRef> refs = nodeService.getChildAssocs(parent, childType);

        for (ChildAssociationRef ref : refs) {
            result.add(ref.getChildRef());
        }
        return result;
    }

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    public ReportsManager getReportsManager() {
        return reportsManager;
    }
	
}
