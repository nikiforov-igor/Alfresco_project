package ru.it.lecm.reports.model.DAO;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.NodeUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Служба хранения файлов, шаблонов и др контента, связанного с разворачиваемыми отчётами.
 * Атом хранения - файл или шаблон связанный с каким-либо отчётом.
 * Базовый объект типа "cm:content" имеет "cm:name" как ключ хранения.
 * Иерахия хранения:
 * 1. папка службы (как принято для lecm служб)
 * 2. папка "Типы отчётов"
 * 3. папка конкретного "Типа отчёта" (reportType)
 * 4. папка "Отчёт" (reportMnemo)
 * 5. [Файл/Контент] Название + данные
 * здесь название файла должно быть уникально для своего отчёта
 *
 * @author rabdullin
 */
public class RepositoryReportContentDAOBean extends BaseBean implements ReportContentDAO {
    /**
     * для создания папки службы в репозитории, см также BaseBean.getFolder();
     */
    final public static String REPORT_SERVICE_FOLDER_ROOT_ID = "REPORT_SERVICE_FOLDER_ID";
    final public static String REPORT_SERVICE_FOLDER_ROOT_NAME = "Сервис построения отчётов";

    private static final transient Logger logger = LoggerFactory.getLogger(RepositoryReportContentDAOBean.class);

    /**
     * флаг запрета записи: true = запрещено, false = разрешено
     */
    private boolean readonly = false;

    @Override
    public String toString() {
        return String.format("RepositoryReportContentDAOBean [readonly %s, root {%s} '%s']"
                , isReadonly()
                , getServiceRootFolder()
                , serviceFolders.get(REPORT_SERVICE_FOLDER_ROOT_ID)
        );
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean value) {
        this.readonly = value;
    }

    /**
     * Корневая папка lecm-службы (первый уровень)
     */
    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(REPORT_SERVICE_FOLDER_ROOT_ID);
    }

    /**
     * @param reportMnem String
     * @return Найти узел для указанного отчёта (2-го уровеня) по мнемонике и типу или вернуть NULL, если его нет
     */
    private NodeRef findReportNode(String reportMnem) {
        if (Utils.isStringEmpty(reportMnem)) {
            return null;
        }
        return getFolder(getServiceRootFolder(), reportMnem);
    }

    /**
     * Создать узел (4-го уровня) для указанного отчёта.
     * Родительские узлы создаются автоматом.
     * Именно, в этом узле будут храниться файлы.
     *
     * @param reportMnem String
     * @return созданный узел
     */
    private NodeRef createReportNode(String reportMnem) {
        if (Utils.isStringEmpty(reportMnem)) {
            return null;
        }

        // Узел самого отчёта (ур 2) ...
        NodeRef nodeReport = getFolder(getServiceRootFolder(), reportMnem);
        if (nodeReport == null) {
            nodeReport = createFolder(getServiceRootFolder(), reportMnem);
        }
        return nodeReport;
    }

    /**
     * Вернуть узел для указанного отчёта и типа. Если нет - создать.
     *
     * @param reportMnem String
     */
    private NodeRef ensureReportNode(String reportMnem) {
        NodeRef nodeReport = findReportNode(reportMnem);
        if (nodeReport == null) {
            nodeReport = createReportNode(reportMnem);
        }
        return nodeReport;
    }

    /**
     * @param reportMnem String
     * @return Найти указанный отчёт по мнемонике и типу или вернуть NULL, если его нет
     */
    private NodeRef findFileNode(String reportMnem, String fileName) {
        if (Utils.isStringEmpty(fileName) || Utils.isStringEmpty(reportMnem)) {
            return null;
        }
        final NodeRef report = findReportNode(reportMnem);
        if (report == null) {
            return null;
        }
        if ("*".equals(fileName)) {
            return report; // целиком узел для самого отчёта
        }
        return getFolder(report, fileName);
    }

    private NodeRef findFileNode(IdRContent id) {
        return (id != null) ? findFileNode(id.getReportMnemo(), id.getFileName()) : null;
    }

    @Override
    public boolean exists(IdRContent id) {
        return findFileNode(id) != null;
    }


    @Override
    public String getRoot() {
        return getServiceRootFolder().getId();
    }

    private void checkWriteable(IdRContent id, String operTag) {
        if (isReadonly()) {
            throw new RuntimeException(String.format("Cannot %s by id={%s} due to Readonly-mode", operTag, id));
        }
    }

    @Override
    public int scanContent(final ContentEnumerator enumerator) {
        final NodeRef root = getServiceRootFolder();
        // узлы и названия с соот-щих уровней
        final int levels = 2;
        final NodeRef[] refs = new NodeRef[levels];
        final String[] names = new String[levels];

        // проходим по всем типа, отчётам и файлам ...
        /*
		 * Иерахия хранения:
		 *   1. папка службы (как принято для lecm служб)
		 *      2. папка "Типы отчётов"
		 *         [lev==1] 3. папка конкретного "Типа отчёта" (reportType)
		 *            [lev==2] 4. папка "Отчёт" (reportMnemo)
		 *               [lev==3] 5. [Файл/Контент] Название + данные
		 *                  здесь название файла должно быть уникально для своего отчёта
		 */

        return NodeUtils.scanHierachicalChilren(root, getNodeService(), levels, new NodeUtils.NodeEnumerator() {
            @Override
            public void lookAt(NodeRef node, List<NodeRef> parents) {
                if (enumerator != null) {
                    // подгрузка названий ...
                    for (int i = 0; i < refs.length; i++) {
                        if (refs[i] == null || !refs[i].equals(parents.get(i))) {
                            refs[i] = parents.get(i);
                            names[i] = Utils.coalesce(getNodeService().getProperty(refs[i], ContentModel.PROP_NAME), "");
                        }
                    }
                    final String nameNode = (String) getNodeService().getProperty(node, ContentModel.PROP_NAME);
                    final IdRContent id = new IdRContent(names[levels-1], nameNode);
                    enumerator.lookAtItem(id);
                }
            }
        }
        );
    }

    @Override
    public void delete(IdRContent id) {
        if (id == null) {
            return;
        }
        checkWriteable(id, "delete");
        final NodeRef nodeFile = findFileNode(id);
        if (nodeFile != null) {
            nodeService.deleteNode(nodeFile);
            logger.info(String.format("File node '%s'\n\t deleted by ref {%s}", id, nodeFile));
        }
    }

    @Override
    public ContentReader loadContent(IdRContent id) {
        ParameterCheck.mandatory("serviceRegistry", serviceRegistry);

        final NodeRef nodeFile = findFileNode(id);
        if (nodeFile == null) {
            return null; // NOT FOUND
        }

        // выдираем контент из узла типа "cm:content" ...

        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<ContentReader>() {
            @Override
            public ContentReader doWork() throws Exception {
                final ContentService contentService = serviceRegistry.getContentService();
                return contentService.getReader(nodeFile, ContentModel.PROP_CONTENT);
            }
        });
    }

    @Override
    public void storeContent(IdRContent id, InputStream stm) {
        if (id == null) {
            return;
        }

        checkWriteable(id, "store");

        ParameterCheck.mandatory("serviceRegistry", serviceRegistry);

        final NodeRef nodeReport = ensureReportNode(id.getReportMnemo());
        if (nodeReport == null) {
            throw new RuntimeException(String.format("Fail to create report node by: %s", id));
        }


        // Сохранение контента типа "cm:content" ...
        final String localName = id.getFileName();
        final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, localName, serviceRegistry.getNamespaceService());

        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, id.getFileName());

        // запишем в прежний контент если узел был или создадим новый ...
        NodeRef nodeFile = nodeService.getChildByName(nodeReport, ContentModel.ASSOC_CONTAINS, id.getFileName());
        if (nodeFile == null) { // создание нового
            final ChildAssociationRef child =
                    nodeService.createNode(nodeReport, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties);
            nodeFile = child.getChildRef();
            logger.debug(String.format("File node '%s'\n\t created by ref {%s}", id, nodeFile));
        }

        final ContentService contentService = serviceRegistry.getContentService();
        final ContentWriter writer = contentService.getWriter(nodeFile, ContentModel.PROP_CONTENT, true);
        try {
            writer.putContent(stm);
            logger.debug(String.format("File node '%s'\n\t content saved %s bytes at ref {%s}", id, writer.getSize(), nodeFile));
        } finally {
            IOUtils.closeQuietly(stm);
        }
    }
}
