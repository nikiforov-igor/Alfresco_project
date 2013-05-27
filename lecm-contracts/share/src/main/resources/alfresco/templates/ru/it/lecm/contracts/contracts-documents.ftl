<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts-list.css" />
    <#assign filter = ""/>
    <#if page.url.args.query?? && page.url.args.query != "-" && page.url.args.query != "">
        <#assign filter = page.url.args.query/>
    </#if>
    <script type="text/javascript">//<![CDATA[
        LogicECM.module.Contracts.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
        LogicECM.module.Contracts.FILTER = "${filter}";
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
    <#if hasPermission>
    <div class="yui-gc">
        <div id="main-region" class="yui-u first">
            <div class="yui-gd grid columnSize2">
                <div class="yui-u first column1">
                    <@region id="contracts-documents-filter" scope="template" />
                </div>
                <div class="yui-u column2">
                    <@region id="toolbar" scope="template" />
                    <@region id="contracts-documents-grid" scope="template" />
                </div>
            </div>
        </div>
    </div>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>