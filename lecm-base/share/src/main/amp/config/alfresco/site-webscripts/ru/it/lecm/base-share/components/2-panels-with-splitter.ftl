<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<#macro twoPanels initialWidth="" leftRegions=["left-panel"] leftPanelId="left-panel" rightPanelId="right-panel" >
	<script type="text/javascript">//<![CDATA[
        (function(){

            function initResizer() {
                var resizer = new LogicECM.module.Base.Resizer(Alfresco.util.generateDomId());
                resizer.setOptions({
                    <#if initialWidth?has_content>
                        initialWidth: ${initialWidth?string},
                    </#if>
                    divLeft: "${leftPanelId}",
                    divRight: "${rightPanelId}"
                });
                resizer.onStartResize = function() {
                    YAHOO.Bubbling.fire("startSplitterMoving");
                };
                resizer.onEndResize = function() {
                    YAHOO.Bubbling.fire("endSplitterMoving");
                };
                YAHOO.Bubbling.on("GridRendered", resizer.onResize, resizer);
                YAHOO.Bubbling.on("tabsRendered", resizer.onResize, resizer);
            }

            function init() {
                LogicECM.module.Base.Util.loadResources([
                    'yui/resize/resize.js'
                ], [], initResizer);
            }
            YAHOO.util.Event.onDOMReady(init);
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