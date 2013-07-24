<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">//<![CDATA[
(function()
{
    new LogicECM.dashlet.Errands("${jsid}").setOptions(
            {
                maxItems: 50
            }).setMessages(${messages});

    new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
    new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
            {
                actions:
                        [
                            {
                                cssClass: "help",
                                bubbleOnClick:
                                {
                                    message: "${msg("dashlet.help")?js_string}"
                                },
                                tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                            }
                        ]
            });
})();
//]]></script>

<div class="dashlet errands">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollableList" id="${id}-paginator" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}-errands"></div>
    </div>
</div>
