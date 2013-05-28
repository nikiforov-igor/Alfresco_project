<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />
    <#assign filter = ""/>
    <#if page.url.args.query?? && page.url.args.query != "*" && page.url.args.query != "">
        <#assign filter = page.url.args.query/>
    </#if>
    <#assign formId = ""/>
    <#if page.url.args.formId?? && page.url.args.formId != "">
        <#assign formId = page.url.args.formId/>
    </#if>
    <script type="text/javascript">//<![CDATA[
        LogicECM.module.Contracts.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
        var defaultFilter = LogicECM.module.Contracts.SETTINGS.defaultFilter;
        LogicECM.module.Contracts.FILTER = <#if filter != "">"${filter}"<#else>defaultFilter</#if>;
        var defaultKey = LogicECM.module.Contracts.SETTINGS.defaultKey;
        LogicECM.module.Contracts.FORM_ID = <#if formId != "">"${formId}"<#else>defaultKey</#if>;
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
    <#if hasPermission>
    <div class="yui-gc">
        <div id="main-region" class="yui-u first">
            <div class="yui-gd grid columnSize2">
                <div class="yui-u first column1">
                    <@region id="contracts-filter" scope="template" />
                </div>
                <div class="yui-u column2">
                    <@region id="toolbar" scope="template" />
                    <@region id="contracts-grid" scope="template" />
                </div>
            </div>
        </div>
    </div>

    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>