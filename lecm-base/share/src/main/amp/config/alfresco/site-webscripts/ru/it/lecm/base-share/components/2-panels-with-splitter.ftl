<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<#macro twoPanels initialWidth="" leftRegions=["left-panel"] leftPanelId="left-panel" rightPanelId="right-panel" >
    <@markup id="js">
		<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
        <@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
    </@>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
        <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
	<script type="text/javascript">//<![CDATA[
        (function(){
		function initResizer() {
                  var resizer = new LogicECM.module.Base.Resizer('resizer');
                  resizer.setOptions({
                        <#if initialWidth?has_content>
			initialWidth: ${initialWidth?string},
                        </#if>
			divLeft: "${leftPanelId}",
			divRight: "${rightPanelId}"
		});

		}
		YAHOO.util.Event.onDOMReady(initResizer);
                })();
	//]]></script>

<div class="two-panels-container">
    <div id="yui-main-2">
        <div class="yui-b" id="${rightPanelId}">
            <#nested>
        </div>
    </div>
    <div id="${leftPanelId}" class="yui-u first column1 tree">
        <#list leftRegions as regionId>
            <@region id=regionId scope="template"/>
        </#list>
    </div>
</div>
</#macro>