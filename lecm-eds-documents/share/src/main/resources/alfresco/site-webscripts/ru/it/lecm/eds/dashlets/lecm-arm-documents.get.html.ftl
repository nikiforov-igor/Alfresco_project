<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">//<![CDATA[
(function () {
    new LogicECM.module.ARM.dashlet.ARMDocuments("${jsid}").setOptions(
            {
                baseQuery: '${settings.baseQuery}',
                isExist: ${settings.isExist?string}
            }).setMessages(${messages});

    new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");
    new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
            {
                actions: [
                    {
                        cssClass: "arm",
                        linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "arm?code=SED",
                        tooltip: "${msg("dashlet.arm.tooltip")?js_string}"
                    },
                    {
                        cssClass: "help",
                        bubbleOnClick: {
                            message: "${msg("dashlet.help")?js_string}"
                        },
                        tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                    }
                ]
            });
})();
//]]></script>

<div class="dashlet arm-documents">
    <div class="title">СЭД: <#if settings?? && settings.title?length == 0>Дашлет не настроен<#else>${settings.title}</#if></div>
    <div class="toolbar flat-button">
        <div class="hidden">
         <span class="align-left yui-button yui-menu-button" id="${id}-filters">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
            <select id="${id}-filters-menu">
            <#if settings?? && settings.filters??>
                <#list settings.filters as filter>
                    <option value="${filter.query?html}">${filter.title}</option>
                </#list>
            </#if>
            </select>

            <div class="clear"></div>
        </div>
    </div>
    <div id="${id}-main" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <div id="${id}-documents"></div>
    </div>
</div>