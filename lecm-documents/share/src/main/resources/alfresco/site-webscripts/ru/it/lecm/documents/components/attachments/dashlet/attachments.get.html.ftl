<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.attachments.dashlet");
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

            container = Dom.get('${id}_results');
            container.innerHTML = 'Вложения';
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>