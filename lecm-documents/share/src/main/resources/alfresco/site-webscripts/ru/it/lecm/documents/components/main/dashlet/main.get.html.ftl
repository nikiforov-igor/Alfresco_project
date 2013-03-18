<#assign id = args.htmlid?js_string>
<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function drawForm(nodeRef, htmlId, formId){
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid: htmlId + nodeRef,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: formId,
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
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
            container = Dom.get('${id}_results');
            drawForm("${nodeRef}",'${id}_results', "document-dashlet");
            var param = decodeURIComponent(location.search.substr(1)).split('&');
            for (var i=0; i < param.length; i++) {
                var tmp = param[i].split('=');
                if (tmp[0] == 'dashlet' && tmp[1] == 'main') {
                    documentMetadataComponent.onExpand();
                }
            }
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMetadataComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
    </div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>