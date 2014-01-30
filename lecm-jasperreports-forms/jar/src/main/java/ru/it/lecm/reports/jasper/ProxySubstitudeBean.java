package ru.it.lecm.reports.jasper;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: dbashmakov
 * Date: 20.11.13
 * Time: 12:25
 */
public class ProxySubstitudeBean implements SubstitudeBean {

    /**
     * префикс расширенного синтаксиса
     * предполагается что строка вся целиком будет окружена: "{{ ... }}"
     */
    final public static String XSYNTAX_MARKER_OPEN = "{{";
    final public static String XSYNTAX_MARKER_CLOSE = "}}";

    SubstitudeBean realBean; // имплементация нативного бина, который организует "хождение" по ссылкам

    public ProxySubstitudeBean() {
        super();
    }

    public void setRealBean(SubstitudeBean realBean) {
        this.realBean = realBean;
    }

    public String getObjectDescription(NodeRef object) {
        return (realBean == null) ? null : realBean.getObjectDescription(object);
    }

    public String getTemplateStringForObject(NodeRef object) {
        return (realBean == null) ? null : realBean.getTemplateStringForObject(object);
    }

    @Override
    public String getTemplateStringForObject(NodeRef object, boolean forList, boolean returnDefaulIfNull) {
        return (realBean == null) ? null : realBean.getTemplateStringForObject(object, forList, returnDefaulIfNull);
    }

    @Override
    public Object getNodeFieldByFormat(NodeRef node, String formatString) {
        if (formatString == null) {
            return null;
        }
        if (isExtendedSyntax(formatString)) {
            return extendedFormatNodeTitle(node, formatString);
        }
        return (realBean == null) ? null : realBean.getNodeFieldByFormat(node, formatString);
    }

    @Override
    public Object getNodeFieldByFormat(NodeRef node, String formatString, String dateFormat, Integer timeZoneOffset) {
        if (formatString == null) {
            return null;
        }
        if (isExtendedSyntax(formatString)) {
            return extendedFormatNodeTitle(node, formatString);
        }
        return (realBean == null) ? null : realBean.getNodeFieldByFormat(node, formatString, dateFormat, timeZoneOffset);
    }

    public String getTemplateStringForObject(NodeRef object, boolean forList) {
        return (realBean == null) ? null : realBean.getTemplateStringForObject(object, forList);
    }

    public List<NodeRef> getObjectsByTitle(NodeRef object, String formatTitle) {
        return (realBean == null) ? null : realBean.getObjectsByTitle(object, formatTitle);
    }

    public String formatNodeTitle(NodeRef node, String fmt, String dateFormat, Integer timeZoneOffset) {
        if (fmt == null) {
            return null;
        }
        if (isExtendedSyntax(fmt)) {
            return extendedFormatNodeTitle(node, fmt);
        }
        return (realBean == null) ? null : realBean.formatNodeTitle(node, fmt, dateFormat, timeZoneOffset);
    }

    @Override
    public String formatNodeTitle(NodeRef node, String formatString) {
        return formatNodeTitle(node, formatString, null, null);
    }

    protected boolean isExtendedSyntax(String fmt) {
        return (fmt != null) && fmt.contains(XSYNTAX_MARKER_OPEN) && fmt.contains(XSYNTAX_MARKER_CLOSE);
    }

    @Override
    public List<NodeRef> getObjectByPseudoProp(NodeRef object, String psedudoProp) {
        return (realBean == null) ? null : realBean.getObjectByPseudoProp(object, psedudoProp);
    }

    @Override
    public String getFormatStringByPseudoProp(NodeRef object, String psedudoProp) {
        return (realBean == null) ? null : realBean.getFormatStringByPseudoProp(object, psedudoProp);
    }

    @Override
    public Object getRealValueByPseudoProp(NodeRef object, String psedudoProp) {
        return (realBean == null) ? null : realBean.getRealValueByPseudoProp(object, psedudoProp);
    }

    /**
     * Функция расширенной обработки. Вызывается когда выражение начинается
     * с двойной фигурной скобки. Здесь сейчас отрабатывает дополнительно
     * только @AUTHOR.REF, чтобы выполнить получение автора и применить к
     * нему отсавшуюся часть выражения.
     */
    protected String extendedFormatNodeTitle(final NodeRef node, String fmt) {
        // NOTE: here new features can be implemented
        String begAuthorRef = "\\{\\{@AUTHOR.*?}}";
        Pattern authorPattern = Pattern.compile(begAuthorRef);
        //создаем Matcher
        Matcher m = authorPattern.matcher(fmt);
        NodeRef authorNode = null;
        while (m.find()) {
            // замена node на узел Автора
            if (authorNode == null) {
                final List<NodeRef> list = realBean.getObjectByPseudoProp(node, AUTHOR);
                authorNode = (list != null && !list.isEmpty()) ? list.get(0) : null;
            }
            String groupText = m.group();
            int startPos = "{{@AUTHOR".length();
            if (groupText.charAt(startPos) == '/') {
                startPos++; // если после "@AUTHOR" есть символ '/' его тоже убираем
            }
            final String shortFmt = "{" + groupText.substring(startPos, groupText.length() - 1);
            if (shortFmt.equals("{}")) {
                fmt = fmt.replace(groupText, realBean.getObjectDescription(authorNode));
            } else {
                fmt = fmt.replace(groupText, realBean.formatNodeTitle(authorNode, shortFmt));
            }

        }
        return realBean.formatNodeTitle(node, fmt);
    }
}