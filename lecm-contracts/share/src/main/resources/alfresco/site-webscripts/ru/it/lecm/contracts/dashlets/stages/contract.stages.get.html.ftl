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
                        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj:{
                            htmlid: htmlId + nodeRef,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: formId,
                            mode:"view",
                            args : "{externalCreateId: \"${id}-action-add\"}"
                        },
                        successCallback:{
                            fn:function(response){
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts: true,
                        htmlId:htmlId + nodeRef
                    });
        }

        function init() {
            container = Dom.get('${id}_container');
            drawForm("${nodeRef}",'${id}_container', "contract-stages");
            var param = decodeURIComponent(location.search.substr(1)).split('&');
            for (var i=0; i < param.length; i++) {
                var tmp = param[i].split('=');
                if (tmp[0] == 'view' && tmp[1] == 'main') {
                    documentMetadataComponent.onExpand();
                }
            }
        }
        Event.onContentReady("${id}_container", init, true);
    })();
    //]]>
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>
            <div style="float:left; margin-right: 4px;">Этапы договора</div>
            <div class="total-tasks-count">
                <span class="lecm-dashlet-actions">
                    <a id="${id}-action-add" href="javascript:void(0);" class="add"
                       title="${msg("dashlet.add.errand.tooltip")}">Добавить этап</a>
                </span>
            </div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMetadataComponent.onExpandTab('contract-stages')" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
		</span>
    </div>
    <div class="dashlet-contract-stages body scrollableList dashlet-body" id="${id}_results">
        <div id="${id}_container"></div>
    </div>
</div>
