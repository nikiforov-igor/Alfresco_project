package ru.it.lecm.reports.model.DAO;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
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
import ru.it.lecm.reports.xml.DSXMLProducer;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

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
            try {
                nodeReport = createFolder(getServiceRootFolder(), reportMnem);
            } catch (WriteTransactionNeededException ex) {
                logger.error(ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
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
        // проходим по всем типа, отчётам и файлам ...
        /*
		 * Иерахия хранения:
		 *   1. папка службы (как принято для lecm служб)
		 *            [lev==1] 2. папка "Отчёт" (reportMnemo)
		 *            [lev==2] 3. Файлы отчета
		 */
        AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                Set<QName> folderType = new HashSet<QName>();
                folderType.add(ContentModel.TYPE_FOLDER);

                int resultCnt = 0;
                List<ChildAssociationRef> reportsFolders = nodeService.getChildAssocs(root, folderType);
                for (ChildAssociationRef reportsFolder : reportsFolders) {
                    NodeRef report = reportsFolder.getChildRef();
                    String reportCode = (String) nodeService.getProperty(report, ContentModel.PROP_NAME);

                    String dsXmlFileName = DSXMLProducer.PFX_DS + reportCode + ".xml";

                    NodeRef dsConfigFile = nodeService.getChildByName(report, ContentModel.ASSOC_CONTAINS, dsXmlFileName);

                    if (dsConfigFile != null) {
                        resultCnt++;
                        enumerator.lookAtItem(new IdRContent(reportCode, dsXmlFileName));
                    }
                }
                return  resultCnt;
            }
        };
        return Integer.parseInt(AuthenticationUtil.runAsSystem(raw).toString());
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
