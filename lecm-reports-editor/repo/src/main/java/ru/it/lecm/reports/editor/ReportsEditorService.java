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

/**
 * User: dbashmakov
 * Date: 18.06.13
 * Time: 17:45
 */
public class ReportsEditorService extends BaseBean {

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

    private NodeRef reRootRef;
    private NodeRef reDictionaryRef;
    private NodeRef reSourcesRef;
    private NodeRef reTemplatesRef;
    private NodeRef reReportsRef;
    private ReportsManager reportsManager;

    /**
     * Метод инициализвции сервиса
     */
    public void init() {
        reRootRef = getFolder(RE_ROOT_ID);
        reDictionaryRef = getFolder(RE_DICTIONARY_ROOT_ID);
        reSourcesRef = getFolder(RE_SOURCES_ROOT_ID);
        reTemplatesRef = getFolder(RE_TEMPLATES_ROOT_ID);
        reReportsRef = getFolder(RE_REPORTS_ROOT_ID);
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return reRootRef;
    }

    public NodeRef getReportsRootFolder() {
        return reReportsRef;
    }

    public NodeRef getSourcesRootFolder() {
        return reSourcesRef;
    }

    public NodeRef getTemplatesRootFolder() {
        return reTemplatesRef;
    }

    public NodeRef getDictionariesRootFolder() {
        return reDictionaryRef;
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

    public NodeRef getReportDescriptorNodeByCode(String rtMnemo) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        List<NodeRef> reportsElement = getElements(reReportsRef, ReportsEditorModel.TYPE_REPORT_DESCRIPTOR);
        for (NodeRef report : reportsElement) {
            Serializable reportCode = nodeService.getProperty(report, ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);
            if (reportCode != null && reportCode.equals(rtMnemo) && !isArchive(report)) {
                return report;
            }
        }
        return null;
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
