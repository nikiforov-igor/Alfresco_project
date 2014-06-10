<#include "/org/alfresco/components/form/form.dependencies.inc">

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/main.css" />
</@>

<#if !page.url.args.statemachineId??>
<br/>
<#list machines as machine>
    <div>
        <h3><a href="${page.url.context}/page/statemachine?statemachineId=${machine.id}" class="theme-color-1">${machine.title}</a></h3>
        ${machine.description}
    </div>
    <br/>
</#list>

<#else>
<div id="statemachine-properties-menu"></div>
<span class="statemachine-menu">
		<span id="menu-buttons-properties-menu-button" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Свойства">&nbsp;</button>
	        </span>
	    </span>
</span>
<div class="statatemachine-menu-spin">&nbsp;</div>
<span class="statemachine-menu">
		<span id="menu-buttons-deploy-menu-button" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Публикация машины состояний">&nbsp;</button>
	        </span>
	    </span>
</span>

<span class="statemachine-menu">
		<span id="menu-buttons-versions-menu-button" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Версии машины состояний">&nbsp;</button>
	        </span>
	    </span>
</span>
<div class="statatemachine-menu-spin1">&nbsp;</div>
<span class="statemachine-menu">
		<span id="menu-buttons-machine-export" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="${msg('button.export-xml')}">&nbsp;</button>
	        </span>
	    </span>
</span>

<span class="statemachine-menu">
    <span id="menu-buttons-machine-import" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.import-xml')}">&nbsp;</button>
        </span>
    </span>
    <form method="post" id="menu-buttons-import-xml-form" enctype="multipart/form-data"
          action="${url.context}/proxy/alfresco/lecm/statemachine/editor/import" class="statatemachine-import-form">
        <input type="file" id="menu-buttons-import-xml-input" name="f" accept=".xml,application/xml,text/xml" class="statatemachine-import-input" title="${msg('button.import-xml')}">
        <input type="hidden" id="menu-buttons-stateMachineId-input" name="stateMachineId" value="${page.url.args.statemachineId}">
    </form>
</span>

<hr/>
<div class="title">
    <h3>Карта</h3>
</div>
<div id="diagram-cont" class="diagram-cont">
    <div>
        <img id="diagram" />
    </div>
</div>
<hr/>
<table cellpadding="0" cellspacing="2" class="statemachine-statuses-buttons-table">
    <tr>
        <td class="status-lable"><h3>Статусы</h3></td>
        <td>
            <span class="statemachine-menu">
                <span id="menu-buttons-new-status-menu-button" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button" title="Новый статус">&nbsp;</button>
                    </span>
                </span>
            </span>
        </td>
        <td>
            <span class="statemachine-menu">
                    <span id="menu-buttons-end-event-menu-button" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button" title="Новый финальный статус">&nbsp;</button>
                        </span>
                    </span>
            </span>
        </td>
        <td>
            <span class="statemachine-menu">
                <span id="menu-buttons-alternative-start-menu-button" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button" title="Альтернативные начальные статусы">&nbsp;</button>
                    </span>
                </span>
            </span>
        </td>
    </tr>
</table>

<div id="statuses-cont"></div>

<script type="text/javascript">
//<![CDATA[

var workflowForm;
(function () {
	function init() {
		var statemachineEditor = new LogicECM.module.StatemachineEditor("statemachine");
        statemachineEditor.setStatemachineId("${page.url.args.statemachineId}");
        statemachineEditor.setMessages(${messages});
        statemachineEditor.draw();
        if (<#if page.url.args.default??>true<#else>false</#if>) {
            statemachineEditor._restoreDefaultStatemachine();
        }
        var menu = new LogicECM.module.StatemachineEditor.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.setEditor(statemachineEditor);
        menu.draw();

        // Resizer
        var Dom = YAHOO.util.Dom;
        var diagramContainer = Dom.get("diagram-cont");
        var diagramResizer = new YAHOO.util.Resize(diagramContainer,
                {
                    handles: ["b"],
                    minHeight: 150,
                    maxHeight: 400
                });

        diagramResizer.on("resize", function() {
            var contHeight = parseInt(Dom.getStyle(diagramContainer, "height"));
            var diagramDiv = Dom.getFirstChild(diagramContainer);

            Dom.setStyle(diagramDiv, "height", contHeight + "px");
        }, this, true);
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>
