package ru.it.lecm.contracts.reports;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.utils.LuceneSearchWrapper;

import java.io.Serializable;
import java.util.*;

/**
 * Провайдер для построения отчёта "Опись изменений к договору"
 *
 * @author rabdullin
 */
public class DSProviderContractsDeltaById extends GenericDSProviderBase {
    private static final Logger logger = LoggerFactory.getLogger(DSProviderContractsDeltaById.class);
    public static final String IN_MAIN_DOC = "inMainDoc";

    @SuppressWarnings("unsed")
    private String nodeRef;

    private NodeRef mainDoc;

    public void setNodeRef(String value) {
        this.nodeRef = value;
        this.mainDoc = (value != null && NodeRef.isNodeRef(value)) ? new NodeRef(value) : null;
    }

    public NodeRef getMainDoc() {
        return mainDoc;
    }

    protected AlfrescoJRDataSource createDS(ReportDescriptor descriptor, ReportDSContext parentContext) {
        this.setReportDescriptor(descriptor);
        LuceneSearchWrapper alfrescoQuery = execQuery(descriptor, parentContext);
        if (alfrescoQuery == null || alfrescoQuery.getSearchResults() == null) {
            return null;
        }

        Iterator<ResultSetRow> iterator = alfrescoQuery.getSearchResults().iterator();

        final LinkedDocumentsDS result = new LinkedDocumentsDS(this);
        result.getContext().setRegistryService(getServices().getServiceRegistry());
        result.getContext().setJrSimpleProps(getSimpleColumnNames(this.getReportDescriptor()));
        result.getContext().setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));
        result.getContext().setResolver(this.getResolver());
        result.getContext().setRsIter(iterator);

        // фильтр данных ...
        result.getContext().setFilter(getDataFilter(alfrescoQuery));
        result.buildJoin();
        return result;
    }

    private class LinkedDocumentsDS extends TypedJoinDS<Map<String, Serializable>> {

        public LinkedDocumentsDS(GenericDSProviderBase provider) {
            super(provider);
        }

        @Override
        public int buildJoin() {
            final List<Map<String, Serializable>> result = new ArrayList<Map<String, Serializable>>();

            final NodeRef mainDoc = getMainDoc();
            Map<String, Serializable> mainDocProps = loadDocInfo(mainDoc, true); // получаем свойства основного документа

            if (getContext().getRsIter() != null) {
                while (getContext().getRsIter().hasNext()) { // тут только одна запись будет по-идее
                    final ResultSetRow rs = getContext().getRsIter().next();
                    final NodeRef docId = rs.getNodeRef(); // id основного документа

                    // поиск id присоединённых документов, которые и надо будет добавлять в result ...
                    final List<NodeRef> connections = findSystemConnections(docId);
                    if (connections == null || connections.isEmpty()) {
                        continue;
                    }
                    for (final NodeRef conn : connections) {
                        Map<String, Serializable> propsMap = new HashMap<String, Serializable>();
                        propsMap.putAll(mainDocProps);

                        Map<String, Serializable> linkDocProps = loadDocInfo(conn, false); // прогрузить остальное
                        propsMap.putAll(linkDocProps);

                        result.add(propsMap);
                        logger.info(String.format(" for doc %s found linked %s", docId, conn));
                    } // for
                } // while
            }

            setData(result);
            setIterData(result.iterator());

            return result.size();
        }

        /**
         * Загрузка связанных документов, созданных на основании docId
         *
         * @param docId - id документа
         * @return список СВЯЗЕЙ Документов типа DocumentConnectionService.TYPE_CONNECTION,
         * связанных с docId так, что выставлено isSystem = true.
         */
        private List<NodeRef> findSystemConnections(NodeRef docId) {
            final NodeService srv = getServices().getServiceRegistry().getNodeService();
            /* получение списка Связей службой документальных связей ... */
            final NodeRef linksRef = getServices().getDocumentConnectionService().getRootFolder(docId); // получить ссылку на "Связи"
            if (linksRef == null) { //TODO DONE Рефакторинг AL-2733
                return null;
            }

            final List<ChildAssociationRef> connectionsList =
                    srv.getChildAssocs(linksRef, new HashSet<QName>(Arrays.asList(DocumentConnectionService.TYPE_CONNECTION)));
            if (connectionsList == null) {
                return null;
            }

			/* отбор только системных связей */
            final List<NodeRef> result = new ArrayList<NodeRef>();
            for (ChildAssociationRef cref : connectionsList) {
                final NodeRef connector = cref.getChildRef();
                final Boolean isSys = (Boolean) srv.getProperty(connector, DocumentConnectionService.PROP_IS_SYSTEM);
                if (Boolean.TRUE.equals(isSys)) { // добавление второго конца ...
                    result.add(connector);
                } // if
            } // for
            return (result.isEmpty()) ? null : result;
        }

        /**
         * Загрузка данных документа
         *
         * @param isInMainDoc true, если надо брать атрибуты помеченные в конфигурации
         *                    флажками "inMainDoc"
         */
        private Map<String, Serializable> loadDocInfo(NodeRef docInfo, boolean isInMainDoc) {
            final Map<String, Serializable> resProps = new HashMap<String, Serializable>();

            if (getConfigXML().getMetaFields() == null) {
                return resProps;
            }

			/* читаем свойства документа целиком */
            final Map<QName, Serializable> props = getServices().getServiceRegistry().getNodeService().getProperties(docInfo);

            final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();
            for (Map.Entry<String, DataFieldColumn> e : getConfigXML().getMetaFields().entrySet()) {
                final DataFieldColumn fld = e.getValue();
                final boolean hasInMainDoc = fld.hasXAttributes() && fld.getAttributes().containsKey(IN_MAIN_DOC);
                if (hasInMainDoc == isInMainDoc) {
                    final String link = fld.getValueLink();
                    // данные получаем либо непосредственно из атрибутов, либо по ссылкам
                    final Serializable value = getResolver().isSubstCalcExpr(link)
                            ? /* по ссылке */ getServices().getSubstitudeService().formatNodeTitle(docInfo, link)
                            : props.get(QName.createQName(link, ns));
                    resProps.put(fld.getName(), value);
                }
            }

            return resProps;
        }

        @Override
        protected Map<String, Serializable> getReportContextProps(Map<String, Serializable> item) {
            final Map<String, Serializable> result = new HashMap<String, Serializable>();

            // DONE: move the data from main & linked documents ...
            for (Map.Entry<String, DataFieldColumn> e : getConfigXML().getMetaFields().entrySet()) {
                final DataFieldColumn fld = e.getValue();
                final String reportColName = fld.getName();
                if (item.containsKey(reportColName)) {
                    result.put(reportColName, item.get(reportColName));
                }
            }
            return result;
        }
    }
}
