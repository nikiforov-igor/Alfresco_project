package ru.it.lecm.businessjournal.beans;

import org.springframework.beans.factory.FactoryBean;

/**
 * User: pmelnikov
 * Date: 09.12.13
 * Time: 14:48
 */
public class BusinessJournalServiceFactory implements FactoryBean<BusinessJournalServiceFactory> {

    private BusinessJournalService localService;
    private BusinessJournalService remoteService;
    private String useRemoteService;

    public void setLocalService(BusinessJournalService localService) {
        this.localService = localService;
    }

    public void setRemoteService(BusinessJournalService remoteService) {
        this.remoteService = remoteService;
    }

    @Override
    public BusinessJournalServiceFactory getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return BusinessJournalServiceFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public BusinessJournalService getService() {
        return  Boolean.valueOf(useRemoteService) ? remoteService : localService;
    }

    public void setUseRemoteService(String useRemoteService) {
        this.useRemoteService = useRemoteService;
    }
}
