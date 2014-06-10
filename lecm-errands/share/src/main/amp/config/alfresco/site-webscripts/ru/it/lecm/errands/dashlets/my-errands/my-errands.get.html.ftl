<!-- Errands-->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-dashlet.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/my-errands-dashlet.js"/>

<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">//<![CDATA[
(function()
{
    new LogicECM.dashlet.MyErrands("${jsid}").setOptions(
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
                            },
                            {
                                cssClass: "arm",
                                linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "errands-list",
                                tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
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
