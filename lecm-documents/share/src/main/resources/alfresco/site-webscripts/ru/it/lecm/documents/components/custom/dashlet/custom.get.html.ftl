<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector;
        var container;

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
            new Alfresco.widget.DashletTitleBarActions("${id?html}").setOptions({
                actions: [
                    {
                        cssClass: "help",
                        bubbleOnClick: {
                            message: "${msg("dashlet.help")?js_string}"
                        },
                        tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                    }
                ]
            });
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div id="custom-dashlet" class="dashlet document" style="display: none">
    <div id="custom-dashlet-title" class="title">${msg("label.title")}</div>
    <div id="custom-dashlet-content" class="body scrollableList">
    </div>
</div>