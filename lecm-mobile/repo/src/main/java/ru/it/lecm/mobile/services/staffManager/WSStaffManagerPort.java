package ru.it.lecm.mobile.services.staffManager;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import ru.it.lecm.mobile.objects.*;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.jws.WebParam;
import java.math.BigInteger;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 14.07.2015
 * Time: 10:22
 */
@javax.jws.WebService(name = "StaffManagerServicePort",
        serviceName = "StaffManager",
        portName = "StaffManagerServicePort",
        targetNamespace = "urn:DefaultNamespace",
        endpointInterface = "ru.it.lecm.mobile.services.staffManager.WSStaffManager")
public class WSStaffManagerPort implements WSStaffManager {
    private ObjectFactory objectFactory;
    private NodeService nodeService;
    private PersonService personService;
    private OrgstructureBean orgstructureService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public WSOFILE getitemfile() {
        return new WSOFILE();
    }

    @Override
    public WSOEDS getfakesign() {
        return new WSOEDS();
    }

    @Override
    public WSOPERSON getperson(@WebParam(name = "IDPERSON", partName = "IDPERSON")String idperson,
                               @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments,
                               @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOPERSON(idperson);
    }

    @Override
    public WSOGROUP getgroup(final String idgr, boolean includeattachments, WSOCONTEXT context) {
        String login = context.getUSERID();

        final AuthenticationUtil.RunAsWork<WSOGROUP> runAsWork = new AuthenticationUtil.RunAsWork<WSOGROUP>() {
            @Override
            public WSOGROUP doWork() throws Exception {
                if (NodeRef.isNodeRef(idgr)) {
                    return objectFactory.createWSOGROUP(new NodeRef(idgr));
                } else {
                    return objectFactory.createWSOGROUP(orgstructureService.getUnitByCode(idgr));
                }
            }
        };

        return AuthenticationUtil.runAs(runAsWork, login);
    }

    @Override
    public WSOCOLLECTION getorganizations(boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        String login = context.getUSERID();

        final AuthenticationUtil.RunAsWork<WSOCOLLECTION> runAsWork = new AuthenticationUtil.RunAsWork<WSOCOLLECTION>() {
            @Override
            public WSOCOLLECTION doWork() throws Exception {
                WSOCOLLECTION groupData = objectFactory.createWSOCOLLECTION();
                NodeRef rootUnit = orgstructureService.getRootUnit();
                if (rootUnit != null) {
                    WSOGROUP group = objectFactory.createWSOGROUP(rootUnit);
                    groupData.getDATA().add(group);
                    groupData.setCOUNT((short) groupData.getDATA().size());
                    return groupData;
                }
                return objectFactory.createWSOCOLLECTION();
            }
        };

        return AuthenticationUtil.runAs(runAsWork, login);
    }

    @Override
    public WSOCOLLECTION getpersons(final String iddep, boolean ismobject, BigInteger childslevel, boolean includeattachments, WSOCONTEXT context) {
        String login = context.getUSERID();

        final AuthenticationUtil.RunAsWork<WSOCOLLECTION> runAsWork = new AuthenticationUtil.RunAsWork<WSOCOLLECTION>() {
            @Override
            public WSOCOLLECTION doWork() throws Exception {
                WSOCOLLECTION employeesCollection = objectFactory.createWSOCOLLECTION();
                List<NodeRef> employeesRefs = null;
                if (iddep != null) {
                    if (NodeRef.isNodeRef(iddep)) {
                        employeesRefs = orgstructureService.getUnitEmployees(new NodeRef(iddep));
                    }
                } else {
                    employeesRefs = orgstructureService.getAllEmployees();
                }
                if (employeesRefs != null) {
                    for (NodeRef employee : employeesRefs) {
                        employeesCollection.getDATA().add(objectFactory.createWSOPERSON(employee));
                    }
                    employeesCollection.setCOUNT((short) employeesCollection.getDATA().size());
                }
                return employeesCollection;
            }
        };

        return AuthenticationUtil.runAs(runAsWork, login);
    }

    @Override
    public WSOGROUP getstructure(final String idorg, BigInteger childslevel, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        String login = context.getUSERID();

        final AuthenticationUtil.RunAsWork<WSOGROUP> runAsWork = new AuthenticationUtil.RunAsWork<WSOGROUP>() {
            @Override
            public WSOGROUP doWork() throws Exception {
                if (idorg != null && idorg.length() > 0) {
                    return objectFactory.createWSOGROUP(idorg);
                } else {
                    NodeRef rootUnit = orgstructureService.getRootUnit();
                    return objectFactory.createWSOGROUP(rootUnit.toString());
                }
            }
        };

        return AuthenticationUtil.runAs(runAsWork, login);
    }

    @Override
    public WSOITEM getitem() {
        return new WSOITEM();
    }
}
