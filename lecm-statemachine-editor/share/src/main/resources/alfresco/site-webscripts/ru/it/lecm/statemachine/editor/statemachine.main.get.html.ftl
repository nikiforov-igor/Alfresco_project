<#if !page.url.args.statemachineId??>
<br/>
<#list machines as machine>
    <div>
    <h3><a href="${page.url.context}/page/statemachine?statemachineId=${machine.id}" class="theme-color-1" style="font-weight: bold;">${machine.title}</a></h3>
    ${machine.description}
    </div>
    <br/>
</#list>

<#else>
<div class="title"><h3 style="font-weight: bold; margin: 5px 0 5px 0">${title}</div>
<hr/>
<div class="title"><h3 style="font-weight: bold; margin: 5px 0 5px 0">Карта</div>
<div style="height: 150px; overflow: auto; border: solid 1px #d3d3d3; margin-bottom: 10px; padding: 10px;">
    <img id="diagram" />
</div>
<hr/>
<div class="title"><h3 style="font-weight: bold; margin: 5px 0 5px 0">Статусы</div>
<div id="statuses-cont">
</div>
<script type="text/javascript">
//<![CDATA[

var workflowForm;
(function () {
	function init() {
		var statemachineEditor = new LogicECM.module.StatemachineEditor("statemachine");
        statemachineEditor.setStatemachineId("${page.url.args.statemachineId}");
        statemachineEditor.setMessages(${messages});
        statemachineEditor.draw();
        var menu = new LogicECM.module.StatemachineEditor.Menu("menu-buttons");
        menu.setMessages(${messages});
        menu.setEditor(statemachineEditor)
        menu.draw();
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>
