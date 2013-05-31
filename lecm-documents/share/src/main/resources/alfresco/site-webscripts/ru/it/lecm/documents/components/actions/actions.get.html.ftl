<#if hasPermission >
    <#assign el=args.htmlid/>

<!-- Markup -->
<div id="${el}">
<div class="widget-panel-grey">
    <h2 id="${el}-heading" class="dark">
        ${msg("label.title")}
    </h2>
    <div id="${el}-formContainer"></div>
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