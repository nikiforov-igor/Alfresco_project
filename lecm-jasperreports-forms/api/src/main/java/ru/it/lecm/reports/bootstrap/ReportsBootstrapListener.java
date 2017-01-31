package ru.it.lecm.reports.bootstrap;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.XMLImportListener;
import ru.it.lecm.reports.api.ReportsManager;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 03.06.2014
 * Time: 15:51
 */
public class ReportsBootstrapListener implements XMLImportListener {
    private static final transient Logger logger = LoggerFactory.getLogger(ReportsBootstrapListener.class);

    private List<String> reportsForDeploy;
    private ReportsManager manager;
    private TransactionService transactionService;

    public void setReportsForDeploy(List<String> reportsForDeploy) {
        this.reportsForDeploy = reportsForDeploy;
    }

    public void setManager(ReportsManager manager) {
        this.manager = manager;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void execute() {
        if (reportsForDeploy == null) {
            logger.warn("Reports deploy impossible. Check report codes in config file");
            return; //пропускаем
        }

		for (String code : reportsForDeploy) {
			NodeRef reportDescriptorRef = manager.getReportEditorDAO().getReportDescriptorNodeByCode(code);
			if (reportDescriptorRef != null) { // нашли отчет - деплоем его
				manager.registerReportDescriptor(reportDescriptorRef);
			}
		}
    }
}
