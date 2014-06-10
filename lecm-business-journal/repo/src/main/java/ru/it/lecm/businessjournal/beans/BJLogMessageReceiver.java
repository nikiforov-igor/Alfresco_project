package ru.it.lecm.businessjournal.beans;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.Message;
import javax.jms.MessageListener;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import ru.it.lecm.base.beans.LecmTransactionHelper;

/**
 * User: pmelnikov
 * Date: 12.12.13
 * Time: 11:14
 */
public class BJLogMessageReceiver implements MessageListener, InitializingBean {

    private LocalBusinessJournalServiceImpl localService;
    private RemoteBusinessJournalServiceImpl remoteService;
    private AbstractBusinessJournalService service;
    private LecmTransactionHelper lecmTransactionHelper;
    private String useRemote;

    private static final Logger logger = LoggerFactory.getLogger(BJLogMessageReceiver.class);

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>(){

                        @Override
                        public Void execute() throws Throwable {
                            BusinessJournalRecord record = (BusinessJournalRecord) ((ActiveMQObjectMessage) message).getObject();
                            service.saveToStore(record);
                            return null;
                        }
                    });
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Cannot save bj record", e);
        }
    }

    public void setLocalService(LocalBusinessJournalServiceImpl localService) {
        this.localService = localService;
    }

    public void setRemoteService(RemoteBusinessJournalServiceImpl remoteService) {
        this.remoteService = remoteService;
    }

    public void setUseRemote(String useRemote) {
        this.useRemote = useRemote;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = Boolean.valueOf(useRemote) ? remoteService : localService;
    }
}
