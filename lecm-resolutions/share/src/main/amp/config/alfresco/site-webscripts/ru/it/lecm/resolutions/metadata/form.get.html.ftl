<#if mayView!false>
<#assign id = args.htmlid>

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
                            mode:"view",
                            hasStatemachine: ${hasStatemachine?string},
                            mayView: ${(mayView!false)?string},
                            mayAdd: ${(mayAdd!false)?string}
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
                        htmlId:htmlId + '${id}'
                    });
        }

        function init() {
            drawForm("${nodeRef}","${id}");
        }

        function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }

        Event.onAvailable("${id}-action-collapse", hideButton);
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="metadata-form" id="${id}_metadata">
    <#if hasStatemachine && (mayAdd!false)>
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-edit" href="${url.context}/page/resolution-edit?nodeRef=${nodeRef}&formId=editForm" class="edit metadata-edit" title="${msg("dashlet.edit.tooltip")}"></a>
        <a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
    </#if>
    <div id="${id}_container"></div>
</div>
</#if>
