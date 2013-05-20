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
<div class="title">
    <h3>${title}</h3>
</div>
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
<div class="title">
    <h3>Статусы</h3>
</div>
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
