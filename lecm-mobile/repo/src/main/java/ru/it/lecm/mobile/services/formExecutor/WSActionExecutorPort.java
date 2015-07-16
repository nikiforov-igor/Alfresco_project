package ru.it.lecm.mobile.services.formExecutor;

import ru.it.lecm.mobile.objects.*;

/**
 * User: dbashmakov
 * Date: 16.07.2015
 * Time: 17:04
 */
@javax.jws.WebService(name = "WSActionExecutorPort",
        serviceName = "ActionExecutor",
        portName = "WSActionExecutorPort",
        targetNamespace = "urn:DefaultNamespace",
        endpointInterface = "ru.it.lecm.mobile.services.formExecutor.WSActionExecutor")
public class WSActionExecutorPort implements WSActionExecutor {
    private ObjectFactory objectFactory;

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public WSOEDS getfakesign() {
        return new WSOEDS();
    }

    @Override
    public boolean execute(WSOBJECT object, WSOFORMACTION action, WSOCONTEXT context) {
        return false;
    }

    @Override
    public WSOITEM getitem() {
        return objectFactory.createWSOITEM();
    }
}
