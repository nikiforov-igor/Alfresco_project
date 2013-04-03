<#if result?? && (result.states?? || result.workflows??) >
<!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#include "/org/alfresco/components/component.head.inc">
    <#assign el=args.htmlid/>

<@script type="text/javascript" src="${page.url.context}/scripts/statemachine/form.js"></@script>
<!-- Markup -->
<div class="widget-panel-white">
<div class="widget-panel-grey">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("label.title")}
    </h2>
    <div id="${el}-formContainer">
        <script type="text/javascript">//<![CDATA[
            var workflowForm = new window.LogicECM.module.StartWorkflow("workflowForm");
            Alfresco.util.createTwister("${el}-heading", "DocumentActions");
        //]]>
       </script>
       <#if result.states?? >
            <#list result.states as state>
                <div class="widget-button-grey" id="statemachine-state-action-${state_index}">${state.label}</div>
                <script type="text/javascript">//<![CDATA[
                (function () {
                    YAHOO.util.Event.onDOMReady(function (){
                        var el = new YAHOO.util.Element("statemachine-state-action-${state_index}");
                        el.on("click", function() {
                            workflowForm.show("trans", "${nodeRef}", "${state.workflowId}", "${result.taskId}", "${state.actionId}", "", [<#list state.errors as error>"${error?replace("\"", "\\\"")}"<#if error_has_next>,</#if></#list>], [<#list state.fields as field>"${field}"<#if field_has_next>,</#if></#list>], "${state.label?replace("\"", "\\\"")}");
                        });
                    })
                })();
                //]]></script>
            </#list>
       </#if>
       <#if result.workflows??>
        <#list result.workflows as workflow>
            <div class="widget-button-grey" id="statemachine-workflow-action-${workflow_index}">${workflow.label}</div>
            <script type="text/javascript">//<![CDATA[
            (function () {
                YAHOO.util.Event.onDOMReady(function (){
                    var el = new YAHOO.util.Element("statemachine-workflow-action-${workflow_index}");
                    el.on("click", function() {
                        workflowForm.show("user","${nodeRef}", "${workflow.workflowId}", "${result.taskId}", "${workflow.id}", "<#list workflow.assignees as assignee>${assignee?replace("\"", "\\\"")}<#if assignee_has_next>,</#if></#list>", [<#list workflow.errors as error>"${error?replace("\"", "\\\"")}"<#if error_has_next>,</#if></#list>], [<#list workflow.fields as field>"${field}"<#if field_has_next>,</#if></#list>], "${workflow.label?replace("\"", "\\\"")}");
                    });
                })
            })();
            //]]></script>
        </#list>
       </#if>
    </div>
</div>
</div>
</#if>