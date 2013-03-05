<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        var expandDashletEvent = new YAHOO.util.CustomEvent("onExpandDashlet");
        expandDashletEvent.subscribe(expandDashlet, null, true);

        var documentComponentBase = new LogicECM.DocumentComponentBase("${id}").setOptions({
            title:"${msg('label.title')}"
        });

        function expandDashlet() {
            documentComponentBase.expandView(container.innerHtml);
        }

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.members.dashlet");
            new Alfresco.widget.DashletTitleBarActions("${id}").setOptions({
                actions: [
                    {
                        cssClass: "expand",
                        eventOnClick: expandDashletEvent,
                        tooltip: "${msg("dashlet.expand.tooltip")?js_string}"
                    }
                ]
            });

            container = Dom.get('${id}_results');
            container.innerHTML = 'Участники';
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>