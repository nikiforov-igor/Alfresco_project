<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if hasViewHistoryPerm!false>
<!-- Markup -->
<div class="widget-bordered-panel">
<div class="document-components-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.edit")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">
        <div id="${el}-form" style="display:none"></div>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            new window.LogicECM.DocumentHistory("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
</div>
</#if>