package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 01.07.15
 * Time: 16:03
 */
public interface DocumentStampService {

    public static final String DOCUMENT_STAMP_NAMESPACE_URI = "http://www.it.ru/lecm/org/document/stamp/1.0";

    public static final QName TYPE_STAMP = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "stamp");

    public static final QName PROP_CODE = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "code");
    public static final QName PROP_WIDTH = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "width");
    public static final QName PROP_HEIGHT = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "height");
    public static final QName PROP_SUBSTITUDE_STRING = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "substitude-string");
    public static final QName PROP_FONT_SIZE = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "font-size");
    public static final QName PROP_HORIZONTAL_ALIGN = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "horizontal-align");
    public static final QName PROP_VERTICAL_ALIGN = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "vertical-align");
    public static final QName PROP_PREV_STAMPS = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "previous-stamps");

    public static final QName ASSOC_IMAGE = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "image-assoc");

    public static final QName ASPECT_PREV_STAMPS = QName.createQName(DOCUMENT_STAMP_NAMESPACE_URI, "previous-stamps-aspect");

    public static final String STRUCT_PROP_PAGE_WIDTH = "pageWidth";
    public static final String STRUCT_PROP_PAGE_HEIGHT = "pageHeight";
    public static final String STRUCT_PROP_STAMP_WIDTH = "stampWidth";
    public static final String STRUCT_PROP_STAMP_HEIGHT = "stampHeight";
    public static final String STRUCT_PROP_DOCUMENT = "document";
    public static final String STRUCT_PROP_STAMP = "stamp";
    public static final String STRUCT_PROP_PREV_STAMPS = "prevStamps";

    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_CENTER = "CENTER";
    public static final String ALIGN_RIGHT = "RIGHT";
    public static final String ALIGN_TOP = "TOP";
    public static final String ALIGN_MIDDLE = "MIDDLE";
    public static final String ALIGN_BOTTOM = "BOTTOM";

    /**
     * Получить параметры штампа для документа
     *
     * @param document документ
     * @param code код штампа
     */
    HashMap<String, Object> getStamp(NodeRef document, String code);

    /**
     * Печать штампа на документе
     *
     * @param document Документ
     * @param attach Вложение на котором будет печататься штамп
     * @param stamp штамп
     * @param x координата штампа
     * @param y координат штампа
     * @param width ширина страницы
     * @param height высота страницы
     * @param page высота страницы
     */
    void drawStamp(NodeRef document, NodeRef attach, NodeRef stamp, int x, int y, int width, int height, int page);
    void drawStamp(NodeRef document, NodeRef attach, NodeRef stamp, int x, int y, int width, int height, int page, List<String> additionalStrings);

    void clearPreviousStampInfo(NodeRef attach);
}
