<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector;
        var container;

        var collapseDashletEvent = new YAHOO.util.CustomEvent("onCollaspeDashlet");
        collapseDashletEvent.subscribe(collapseDashlet, null, true);

        function collapseDashlet() {
            // скрываем dashlet
            Dom.setStyle("custom-dashlet", "display", "none");
            // отображаем main region
            Dom.setStyle("main-region", "display", "block");
        }

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.custom.dashlet");
            /*var mainHeigth = Dom.get("main-region").clientHeight;
            Dom.setAttribute("custom-dashlet","heigth", mainHeigth);*/
            new Alfresco.widget.DashletTitleBarActions("${id}").setOptions({
                actions: [
                    {
                        cssClass: "collapse",
                        eventOnClick: collapseDashletEvent,
                        tooltip: "${msg("dashlet.collapse.tooltip")?js_string}"
                    }
                ]
            });
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div id="custom-dashlet" class="dashlet document" style="display: none;">
    <div id="custom-dashlet-title" class="title">${msg("label.title")}</div>
    <div id="custom-dashlet-content" class="body scrollableList">
    </div>
</div>