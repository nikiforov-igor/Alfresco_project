<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/document-list-filters-manager.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />

    <#assign filter = ""/>
    <#assign formId = ""/>
    <#assign isDocListPage = false/>
    <#assign queryFilterId = ""/>

    <script type="text/javascript">//<![CDATA[
        function findValueByDotNotation(obj, propertyPath, defaultValue) {
            var value = defaultValue ? defaultValue : null;
            if (propertyPath && obj && obj != "") {
                var currObj = obj;
                var props = propertyPath.split(".");
                for (var i = 0; i < props.length; i++) {
                    currObj = currObj[props[i]];
                    if (typeof currObj == "undefined") {
                        return value;
                    }
                }
                return currObj;
            }
            return value;
        }

        <#if filter == "" && preferences??>
            var PREFERENCE = "${preferenceKey}";
            var preference = findValueByDotNotation(${preferences}, PREFERENCE);
            if (preference != null) {
                    window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + preference;
            } else {
                <#if defaultFilter?? && defaultKey??>
                        window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + "query=" + "${defaultFilter}" + "&formId=" + "${defaultKey}";
                </#if>
            }
        </#if>

        /*// инициализуруем менеджер предустановок фильтров для списка документов
        var documentFilters = new LogicECM.module.Documents.FiltersManager();
        <#if docType?? && docType != "" >
            documentFilters.setOptions({
                docType: "${docType}"
            });
        <#else>
            documentFilters.setOptions({
                docType: "lecm-base:document"
            });
        </#if>
        documentFilters.setOptions({
            isDocListPage: ${isDocListPage?string},
            archiveDocs: false
        });
        LogicECM.module.Documents.filtersManager = documentFilters;*/

        <#if queryFilterId?? && queryFilterId != "" && preferences??>
            /*var PREFERENCE_FILTER = "ru.it.lecm.documents." + (("${docType}" != "") ? "${docType}" : "lecm-base:document").split(":").join("_") + "." + "${queryFilterId}";
            var preference = findValueByDotNotation(${preferences}, PREFERENCE_FILTER);
            if (preference != null && preference != "" && location.hash == "") {
                location.hash = '#filter=' + "${queryFilterId}" + "|" + preference;
            }*/
        </#if>

        // настройки из repo
        LogicECM.module.ARM.SETTINGS =
            <#if settings?? >
            ${settings}
            <#else>
            {}
            </#if>;

        LogicECM.module.ARM.FILTER = "";
    //]]></script>
</@>
<div id="no_menu_page" class="sticky-wrapper">
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage showHeader=true showTitle=true showToolbar=hasPermission showMenu=false>
    <#if hasPermission>
        <div class="yui-gc">
            <div id="main-region" class="yui-u first">
                <div class="yui-gd grid columnSize2">
                    <div class="yui-u first column1">
                        <@region id="documents-filter" scope="template" />
                    </div>
                    <div class="yui-u column2">
                        <@region id="filters" scope="template" />
                        <@region id="documents-grid" scope="template" />
                    </div>
                </div>
            </div>
        </div>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
    <@region id="dependencies" scope="template" />
</@bpage.basePage>
</div>