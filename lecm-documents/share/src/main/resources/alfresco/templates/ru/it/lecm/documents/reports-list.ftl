<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/documents-reports.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />

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

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
    <#if hasPermission>
        <@region id="reports-grid" scope="template" />
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>