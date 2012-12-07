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
<div id="statemachine" style="margin: 0 0 20px 0">
    <div id="top1" style="padding: 5px; background-color: white;">
        <img id="diagram" />
    </div>
    <div id="center1" style="padding: 5px;">
    </div>
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
