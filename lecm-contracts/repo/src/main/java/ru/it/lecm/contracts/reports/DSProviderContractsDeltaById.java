package ru.it.lecm.contracts.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.ParameterCheck;

import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.DSProviderSearchQueryReportBase;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.jasper.TypedJoinDS;

/**
 * Провайдер для построения отчёта "Опись изменений к договору"
 *
 * @author rabdullin
 */
public class DSProviderContractsDeltaById extends DSProviderSearchQueryReportBase {
    private static final Logger logger = LoggerFactory.getLogger(DSProviderSearchQueryReportBase.class);

    @Override
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        final LinkedDocumentsDS result = new LinkedDocumentsDS(iterator);
        result.getContext().setSubstitudeService(getServices().getSubstitudeService());
        result.getContext().setRegistryService(getServices().getServiceRegistry());
        result.getContext().setJrSimpleProps(jrSimpleProps);
        result.getContext().setMetaFields(conf().getMetaFields());
        result.buildJoin();
        return result;
    }

    /**
     * Данные для отчёта по основному документу
     */
    private class NodeInfo {
        final NodeRef nodeRef;

        /**
         * По именам из отчёта здесь атрибуты основного документа, которые в
         * конфигурации помечены флажком "inMainDoc":
         * Вид_договора
         * Рег_номер
         * Дата_регистрации
         * Контрагент_краткое_наименование.
         */
        final Map<String, Serializable> props = new HashMap<String, Serializable>();

        private NodeInfo(NodeRef nodeRef) {
            super();
            this.nodeRef = nodeRef;
        }

        @Override
        public String toString() {
            return "" + nodeRef;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((nodeRef == null) ? 0 : nodeRef.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final NodeInfo other = (NodeInfo) obj;
            if (nodeRef == null) {
                if (other.nodeRef != null) {
                    return false;
                }
            } else if (!nodeRef.equals(other.nodeRef)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Данные для отчёта по вложенному документу
     */
    private class LinkedDocumentInfo {
        /**
         * Данные основного документа
         */
        final NodeInfo docInfo;

        /**
         * Данные вложенного документа: его атрибуты те, которые в xml-конфигурации
         * НЕ помечены флажком "inMainDoc":
         * Тип_документа // изменения к договору, спецификация, протокол согласования разногласий
         * Номер
         * Дата_документа
         * Статус.
         */
        final NodeInfo connectionInfo;

        public LinkedDocumentInfo(NodeRef connectionRef, NodeInfo docInfo) {
            super();
            this.connectionInfo = new NodeInfo(connectionRef);
            this.docInfo = docInfo;
        }
    }

    private class LinkedDocumentsDS extends TypedJoinDS<LinkedDocumentInfo> {

        public LinkedDocumentsDS(Iterator<ResultSetRow> iterator) {
            super(iterator);
        }

        @Override
        public int buildJoin() {
            final ArrayList<LinkedDocumentInfo> result = new ArrayList<LinkedDocumentInfo>();

            final NodeInfo mainDoc = new NodeInfo(nodeRef());
            loadDocInfo(mainDoc, true); // получаем свойства основного документа

            if (context.getRsIter() != null) {
                while (context.getRsIter().hasNext()) { // тут только одна запись будет по-идее
                    final ResultSetRow rs = context.getRsIter().next();
                    final NodeRef docId = rs.getNodeRef(); // id основного документа

                    // поиск id присоединённых документов, которые и надо будет добавлять в result ...
                    final List<NodeRef> connections = findSystemConnections(docId);
                    if (connections == null || connections.isEmpty()) {
                        continue;
                    }
                    for (final NodeRef conn : connections) {
                        final LinkedDocumentInfo linkedDoc = new LinkedDocumentInfo(conn, mainDoc);
                        loadDocInfo(linkedDoc.connectionInfo, false); // прогрузить остальное
                        result.add(linkedDoc);
                        logger.info(String.format(" for doc %s found linked %s", docId, linkedDoc.connectionInfo));
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
         *         связанных с docId так, что выставлено isSystem = true.
         *         Далее эти коннекторы можно обработать так:
         *         // get other side document ...
         *         final NodeRef target= srv.getTargetAssocs(connector, DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT).get(0).getTargetRef();
         *         <p/>
         *         // get connection type (of type "lecm-connect-types:connection-type") ...
         *         final NodeRef atype = srv.getTargetAssocs(connector, DocumentConnectionService.ASSOC_CONNECTION_TYPE).get(0).getTargetRef();
         */
        private List<NodeRef> findSystemConnections(NodeRef docId) {
            final NodeService srv = getServices().getServiceRegistry().getNodeService();
            /* получение списка Связей службой документальных связей ... */
            final NodeRef linksRef = getServices().getDocumentConnectionService().getRootFolder(docId); // получить ссылку на "Связи"

            final QName qnConnection = QName.createQName("lecm-connect:connection", getServices().getServiceRegistry().getNamespaceService());
            final List<ChildAssociationRef> connectionsList = srv.getChildAssocs(linksRef, new HashSet<QName>(Arrays.asList(qnConnection)));
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
         * @param docInfo
         * @param isInMainDoc true, если надо брать атрибуты помеченные в конфигурации
         *                    флажками "inMainDoc"
         */
        private void loadDocInfo(NodeInfo docInfo, boolean isInMainDoc) {
            if (conf().getMetaFields() == null) {
                return;
            }

			/* читаем свойства документа целиком */
            final Map<QName, Serializable> props = getServices().getServiceRegistry().getNodeService().getProperties(docInfo.nodeRef);

			/* проходим по всем сконфигурированным свойствам, выбираем нужные */
            if (props == null) {
                return;
            }

            final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();
            for (Map.Entry<String, DataFieldColumn> e : conf().getMetaFields().entrySet()) {
                final DataFieldColumn fld = e.getValue();
                final boolean hasInMainDoc = fld.hasXAttributes() && fld.getAttributes().containsKey("inMainDoc");
                if (hasInMainDoc == isInMainDoc) {
                    final String link = fld.getValueLink();
                    // данные получаем либо непосредственно из атрибутов, либо по ссылкам
                    final Serializable value = (ReportDSContextImpl.isCalcField(link))
                            ? /* по ссылке */ getServices().getSubstitudeService().formatNodeTitle(docInfo.nodeRef, link)
                            : props.get(QName.createQName(link, ns));
                    docInfo.props.put(fld.getName(), value);
                }
            }
        }

        @Override
        protected Map<String, Serializable> getReportContextProps(
                LinkedDocumentInfo item) {
            final Map<String, Serializable> result = new HashMap<String, Serializable>();

            // DONE: move the data from main & linked documents ...
            for (Map.Entry<String, DataFieldColumn> e : conf().getMetaFields().entrySet()) {
                final DataFieldColumn fld = e.getValue();
                final String reportColName = fld.getName();
                if (item.docInfo.props.containsKey(reportColName))
                    result.put(reportColName, item.docInfo.props.get(reportColName));
                else if (item.connectionInfo.props.containsKey(reportColName))
                    result.put(reportColName, item.connectionInfo.props.get(reportColName));
            }
            return result;
        }
    }
}
