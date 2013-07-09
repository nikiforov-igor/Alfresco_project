<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />
    <#assign filter = ""/>
    <#if page.url.args.query?? && page.url.args.query != "*" && page.url.args.query != "">
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
    <script type="text/javascript">//<![CDATA[
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
<#assign showToolbar = hasPermisson!false/>
<@bpage.basePage showToolbar=showToolbar>
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
</@bpage.basePage>