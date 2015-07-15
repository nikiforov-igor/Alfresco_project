package ru.it.lecm.mobile.services.taskManager;

import ru.it.lecm.mobile.objects.*;

import java.math.BigInteger;

/**
 * Created by pmelnikov on 15.07.2015.
 */
public class WSTaskManagerPort implements WSTaskManager {

    private ObjectFactory objectFactory;

    @Override
    public WSOEDS getfakesign() {
        return null;
    }

    @Override
    public WSOTASK gettask(String idtask, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION gettasksbydoc(String docid, boolean ismobject, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOTASK getstructure(String roottaskid, BigInteger childslevel, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public String createresolution(String docid, WSOTASK newtask, String parenttaskid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK updateresolution(String missionlabelid, WSOTASK newtask, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK deleteresolution(String missionlabelid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getmissionlabels(String docid, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getresolutions(String docid, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION gettasks(boolean ismobject, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getrestemplates(String doctypename, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION gettaskstemplates(WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASK settask(WSOTASK inTASK, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getreports(String taskid, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOMTASKREPORT setreport(WSOTASKREPORT report, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOITEM getitem() {
        return null;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
}
