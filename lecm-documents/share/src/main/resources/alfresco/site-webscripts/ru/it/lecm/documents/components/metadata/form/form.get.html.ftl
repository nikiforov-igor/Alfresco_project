<#if mayView!false>
<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector;
        var container;

        function drawForm(nodeRef, htmlId){
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj:{
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: "",
                            mode:"view"
                            <#if args.setId?? >, setId: "${args.setId}"</#if>
                        },
                        successCallback:{
                            fn:function(response){
                                container = Dom.get('${id}_container');
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true,
                        htmlId:htmlId + nodeRef
                    });
        }

        function init() {
            drawForm("${nodeRef}",'${id}');
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="metadata-form" id="${id}_metadata">
    <#if hasStatemachine && (mayAdd!false)>
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-edit" onclick="documentMetadataComponent.onEdit('${id}_container')"
            class="edit" title="${msg("dashlet.edit.tooltip")}"></a>
    </div>
    </#if>
    <div id="${id}_container"></div>
</div>
</#if>