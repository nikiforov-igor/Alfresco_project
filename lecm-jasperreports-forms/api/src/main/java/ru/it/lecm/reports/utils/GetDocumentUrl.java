package ru.it.lecm.reports.utils;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.util.UrlUtil;

/**
 * User: dbashmakov
 * Date: 28.02.2017
 * Time: 14:22
 */
public class GetDocumentUrl {
    private static SysAdminParams sysAdminParams;

    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        GetDocumentUrl.sysAdminParams = sysAdminParams;
    }
    public static String getDocumentLink(String nodeRef) {
        return UrlUtil.getShareUrl(sysAdminParams) + "/page/document?nodeRef=" + nodeRef;
    }
}
