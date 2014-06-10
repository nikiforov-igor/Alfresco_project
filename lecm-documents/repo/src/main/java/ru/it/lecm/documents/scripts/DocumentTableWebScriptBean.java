package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentTableService;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.service.transaction.TransactionService;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:57
 */
public class DocumentTableWebScriptBean extends BaseWebScript {
	private DocumentTableService documentTableService;
	protected NodeService nodeService;
	private NamespaceService namespaceService;
	private TransactionService transactionService;

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	/**
	 * Получение папки с табличными данными для документра
	 * @param documentNodeRef nodeRef документа
	 * @return папка для табличных данных в документе
	 */
	public ScriptNode getRootFolder(String documentNodeRef) {
		org.alfresco.util.ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

		NodeRef documentRef = new NodeRef(documentNodeRef);

		if (this.nodeService.exists(documentRef)) {
			NodeRef root = this.documentTableService.getRootFolder(documentRef);
			if (root != null) {
				return new ScriptNode(root, this.serviceRegistry, getScope());
			}
		}
		return null;
	}

	/**
	 * Получение результирующих строк
	 * @param tableDataRef nodeRef табличных данных
	 * @return массив результирующих строк
	 */
	public Scriptable getTableTotalRow(String tableDataRef) {
		ParameterCheck.mandatory("tableDataRef", tableDataRef);

		NodeRef tableDataNodeRef = new NodeRef(tableDataRef);
		if (nodeService.exists(tableDataNodeRef) && documentTableService.isDocumentTableData(tableDataNodeRef)) {
			List<NodeRef> totalRows = documentTableService.getTableDataTotalRows(tableDataNodeRef);
			if (totalRows != null) {
				return createScriptable(totalRows);
			}
		}
		return null;
	}

	/**
	 * Перемещение строки табличных данных вверх
	 * @param tableRowStr nodeRef строки табличных данных
	 * @return nodeRef записи с которой произошёл обмен
	 */
    public String onMoveTableRowUp(String tableRowStr) {
        org.alfresco.util.ParameterCheck.mandatory("tableRowStr", tableRowStr);
        final NodeRef tableRow = new NodeRef(tableRowStr);

//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<String>(){
//
//			@Override
//			public String execute() throws Throwable {
				return documentTableService.moveTableRowUp(tableRow);
//			}
//
//		});

    }

	/**
	 * Перемещение строки табличных данных вниз
	 * @param tableRowStr nodeRef строки табличных данных
	 * @return nodeRef записи с которой произошёл обмен
	 */
    public String onMoveTableRowDown(String tableRowStr) {
        org.alfresco.util.ParameterCheck.mandatory("tableRowStr", tableRowStr);
        final NodeRef tableRow = new NodeRef(tableRowStr);
		//А должен ли скрипт сам открывать транзакцию???
//		return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<String>(){
//
//			@Override
//			public String execute() throws Throwable {
				return documentTableService.moveTableRowDown(tableRow);
//			}
//
//		});
    }

	/**
	 * Получение строк табличных данных
	 * @param tableDataRef nodeRef табличных данных
	 * @return массив строк табличных данных
	 */
    public Scriptable getTableDataRows(String tableDataRef) {
        ParameterCheck.mandatory("tableDataRef", tableDataRef);

        NodeRef tableDataNodeRef = new NodeRef(tableDataRef);
        if (nodeService.exists(tableDataNodeRef) && documentTableService.isDocumentTableData(tableDataNodeRef)) {
            List<NodeRef> tableDataRows = documentTableService.getTableDataRows(tableDataNodeRef);
            if (tableDataRows != null) {
                return createScriptable(tableDataRows);
            }
        }
        return createScriptable(new ArrayList<NodeRef>());
    }
}
