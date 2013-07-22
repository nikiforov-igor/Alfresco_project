<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
(function()
{
    new LogicECM.dashlet.Errands("${jsid}").setOptions(
            {
                maxItems: 50,
                sorting:
                {<#list sorting as sort>
                    "${sort.type?js_string}": "${sort.parameters?js_string}"<#if sort_has_next>,</#if>
                </#list>}
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
    <div class="toolbar flat-button">
        <div class="hidden">
         <span class="align-left yui-button yui-menu-button" id="${id}-sorting">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
            <select id="${id}-sorting-menu">
            <#list sorting as sort>
                <option value="${sort.type?html}">${msg("sorting." + sort.type)}</option>
            </#list>
            </select>
            <div class="clear"></div>
        </div>
    </div>
    <#--<div class="toolbar flat-button">-->
        <#--<div class="align-left" id="${id}-paginator">&nbsp;Здесь будет выводится перечень моих поручений</div>-->
        <#--<div class="clear"></div>-->
    <#--</div>-->
    <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}-errands"></div>
    </div>
</div>
