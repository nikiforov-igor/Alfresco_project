<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-contracts/contracts-main.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />

    <script type="text/javascript">//<![CDATA[
        LogicECM.module.Documents.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
        (function () {
            function init() {
                setTimeout(function () {
                    LogicECM.module.Base.Util.setHeight();
                    LogicECM.module.Base.Util.setDashletsHeight("main-region");
                }, 10);
            }

            YAHOO.util.Event.onDOMReady(init);
        })();

//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
<div id="doc-bd">
    <#if hasPermission>
        <@region id="toolbar" scope="template"/>
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