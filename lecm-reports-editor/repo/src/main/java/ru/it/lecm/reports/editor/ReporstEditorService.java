package ru.it.lecm.reports.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 18.06.13
 * Time: 17:45
 */
public class ReporstEditorService extends BaseBean {

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

    private NodeRef reRootRef;
    private NodeRef reDictionaryRef;
    private NodeRef reSourcesRef;
    private NodeRef reTemplatesRef;
    private NodeRef reReportsRef;

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
        List<NodeRef> result = new ArrayList<NodeRef>();
        NodeRef typesDictionary = nodeService.getChildByName(getDictionariesRootFolder(), ContentModel.ASSOC_CONTAINS, "Тип отчета");

        Set<QName> childType = new HashSet<QName>();
        childType.add(ReportsEditorModel.TYPE_REPORT_TYPE);

        List<ChildAssociationRef> refs = nodeService.getChildAssocs(typesDictionary, childType);

        for (ChildAssociationRef ref : refs) {
            result.add(ref.getChildRef());
        }
        return result;
    }
}
