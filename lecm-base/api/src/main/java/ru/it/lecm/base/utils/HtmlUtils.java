package ru.it.lecm.base.utils;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Created by APanyukov on 17.04.2017.
 */
public class HtmlUtils {

    public static String wrapTitle(String text, String title) {
        return "<span class=\"wrapper-title\" title=\"" + title.replaceAll("\"", "&quot;") + "\">" + text + "</span>";
    }
    public static String wrapperAttribute(NodeRef nodeRef, String description, String formId) {
        return "<a href=\"javascript:void(0);\" onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + nodeRef.toString() + "\', formId: \'" + formId + "\'})\">" + description + "</a>";
    }
}
