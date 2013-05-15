<#if hasPermission >
<!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#include "/org/alfresco/components/component.head.inc">
    <#assign el=args.htmlid/>

<@script type="text/javascript" src="${page.url.context}/scripts/statemachine/form.js"></@script>
<!-- Markup -->
<div class="widget-panel-white" id="${el}">
<div class="widget-panel-grey">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("label.title")}
    </h2>
    <div id="${el}-formContainer">
    </div>
    <script type="text/javascript">//<![CDATA[
    YAHOO.util.Event.onDOMReady(function (){
        var workflowForm = new LogicECM.module.StartWorkflow("${el}").setOptions({
            nodeRef: "${nodeRef}"
        });
        workflowForm.draw();
        Alfresco.util.createTwister("${el}-heading", "DocumentActions");
    });
    //]]>
    </script>
</div>
</div>
</#if>