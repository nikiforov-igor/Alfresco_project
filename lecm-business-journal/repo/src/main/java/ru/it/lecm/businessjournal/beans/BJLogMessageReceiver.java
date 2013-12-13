package ru.it.lecm.businessjournal.beans;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * User: pmelnikov
 * Date: 12.12.13
 * Time: 11:14
 */
public class BJLogMessageReceiver implements MessageListener, InitializingBean {

    private LocalBusinessJournalServiceImpl localService;
    private RemoteBusinessJournalServiceImpl remoteService;
    private AbstractBusinessJournalService service;
    private String useRemote;

    @Override
    public void onMessage(final Message message) {
        try {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    BusinessJournalRecord record = (BusinessJournalRecord) ((ActiveMQObjectMessage) message).getObject();
                    service.saveToStore(record);
                    return null;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Cannot save bj record", e);
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
