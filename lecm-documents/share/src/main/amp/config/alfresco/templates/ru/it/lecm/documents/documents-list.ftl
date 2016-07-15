<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/document-list-filters-manager.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />

    <#assign filter = ""/>
    <#if page.url.args.query?? && page.url.args.query != "">
        <#assign filter = page.url.args.query/>
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
        function getCookie (name) {
            var results = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');

            if (results)
                return ( decodeURIComponent(results[2]) );
            else
                return null;
        }

        LogicECM.currentUser = <#if currentUser?? >'${currentUser}'<#else>null</#if>;
        <#if filter == "">
            var PREFERENCE_DOCUMENTS_STATUSES =
                    "ru.it.lecm.documents." + (("${docType}" != "") ? "${docType}" : "lecm-base:document").split(":").join("_")
                            + ".documents-list-statuses-filter" + ".${currentUser}";
                var preferenceStatuses = LogicECM.module.Base.Util.getCookie(PREFERENCE_DOCUMENTS_STATUSES);
                if (preferenceStatuses != null) {
                        window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + preferenceStatuses;
                } else {
                    <#if defaultFilter?? && defaultKey??>
                        window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + "query=" + "${defaultFilter}" + "&formId=" + "${defaultKey}";
                    </#if>
                }
        </#if>

        // инициализуруем менеджер предустановок фильтров для списка документов
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
        LogicECM.module.Documents.filtersManager = documentFilters;

        <#if queryFilterId?? && queryFilterId != "">
            var PREFERENCE_FILTER = "ru.it.lecm.documents." + (("${docType}" != "") ? "${docType}" : "lecm-base:document").split(":").join("_") +
                    "." + "${queryFilterId}" + ".${currentUser}";
            var preferenceFilter = LogicECM.module.Base.Util.getCookie(PREFERENCE_FILTER);
            if (preferenceFilter != null && preferenceFilter != "" && location.hash == "") {
                location.hash = '#filter=' + "${queryFilterId}" + "|" + preferenceFilter;
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
</@>


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