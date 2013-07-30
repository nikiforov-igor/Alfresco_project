<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/document-list-filters-manager.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />

    <#assign filter = ""/>
    <#if page.url.args.query?? && page.url.args.query != "">
        <#assign filter = page.url.args.query/>
    <#else>
        <#if prefQuery??>
            <#assign  reloadPage = true/>
        </#if>
    </#if>

    <#assign formId = ""/>
    <#if page.url.args.formId?? && page.url.args.formId != "">
        <#assign formId = page.url.args.formId/>
    </#if>

    <#assign isDocListPage = false/>
    <#if page.url.args.doctype?? && page.url.args.doctype != "">
        <#assign isDocListPage = true/>
    </#if>

    <#assign queryFilterId = ""/>
    <#if preferedFilter??>
        <#assign queryFilterId = preferedFilter?string/>
    </#if>

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
        };

        <#if filter == "" && preferences??>
            var PREFERENCE_DOCUMENTS_STATUSES = "ru.it.lecm.documents." + (("${docType}" != "") ? "${docType}" : "lecm-base:document").split(":").join("_") + ".documents-list-statuses-filter";
                var preference = findValueByDotNotation(${preferences}, PREFERENCE_DOCUMENTS_STATUSES);
                if (preference != null) {
                        window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + preference;
                }
        </#if>

        // инициализуруем менеджер предустановок фильтров для списка документов
        var documentFilters = new LogicECM.module.Documents.FiltersManager();
        <#if docType?? && docType != "" >
                documentFilters.setOptions({
                    docType: ("${docType}" != "") ? "${docType}" : "lecm-base:document",
                    isDocListPage: ${isDocListPage?string}
                });
        </#if>
        LogicECM.module.Documents.filtersManager = documentFilters;

        <#if queryFilterId?? && queryFilterId != "" && preferences??>
            var PREFERENCE_FILTER = "ru.it.lecm.documents." + (("${docType}" != "") ? "${docType}" : "lecm-base:document").split(":").join("_") + "." + "${queryFilterId}";
            var preference = findValueByDotNotation(${preferences}, PREFERENCE_FILTER);
            if (preference != null && preference != "") {
                location.hash = '#filter=' + "${queryFilterId}" + "|" + preference;
            }
        </#if>

        // настройки из repo
        LogicECM.module.Documents.SETTINGS =
            <#if settings?? >
            ${settings}
            <#else>
            {}
            </#if>;

        LogicECM.module.Documents.FILTER =
            <#if filter != "">
            "${filter}"
            <#else>
            LogicECM.module.Documents.SETTINGS.defaultFilter
            </#if>;

        LogicECM.module.Documents.FORM_ID =
            <#if formId != "">
            "${formId}"
            <#else>
            LogicECM.module.Documents.SETTINGS.defaultKey
            </#if>;
    //]]></script>

    <#if isDocListPage>
        <style type="text/css">
            #bd #lecm-menu {
                width: 0px;
                margin: 0px;
            }
            #bd #lecm-page {
                margin: 5px 5px 5px 5px;
            }
        </style>
    </#if>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage true true hasPermission>
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