<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts-main.css" />

    <script type="text/javascript">//<![CDATA[
        LogicECM.module.Contracts.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
<div id="doc-bd">
    <#if hasPermission>
        <div class="yui-gc">
            <div id="main-region" class="yui-u first">
                <div class="yui-gd grid columnSize2">
                    <div class="yui-u first column1">
                        <@region id="summary" scope="template"/>
                        <@region id="tasks" scope="template"/>
                    </div>
                    <div class="yui-u column2">
                        <@region id="activity" scope="template"/>
                        <@region id="sr-info" scope="template"/>
                    </div>
                </div>
            </div>
        </div>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</div>
</@bpage.basePage>