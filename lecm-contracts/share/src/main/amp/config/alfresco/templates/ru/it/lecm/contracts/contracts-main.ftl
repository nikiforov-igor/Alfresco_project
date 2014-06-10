<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">

    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-main.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />

	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
	<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
    <script type="text/javascript">//<![CDATA[
	    if (typeof LogicECM == "undefined" || !LogicECM) {
			var LogicECM = {};
		}
		LogicECM.module = LogicECM.module || {};
		LogicECM.module.Documents = LogicECM.module.Documents|| {};
        (function () {
        	LogicECM.module.Documents.SETTINGS = <#if settings?? >${settings}<#else>{}</#if>;
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