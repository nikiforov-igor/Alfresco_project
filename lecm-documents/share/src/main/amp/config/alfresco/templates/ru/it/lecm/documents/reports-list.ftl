<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">
    <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-reports.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />

    <#assign isDocListPage = false/>
    <#if isDocPage??>
        <#assign isDocListPage = isDocPage/>
    </#if>

<script type="text/javascript">//<![CDATA[
LogicECM.module.Documents.SETTINGS =
    <#if reportSettings?? >
    ${reportSettings}
    <#else>
    {}
    </#if>;
//]]></script>
</@>



<@bpage.basePage showToolbar=false>
    <#if hasPermission>
        <@region id="reports-grid" scope="template" />
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>