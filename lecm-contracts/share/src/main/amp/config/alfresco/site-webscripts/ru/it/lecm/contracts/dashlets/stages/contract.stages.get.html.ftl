<#assign id = args.htmlid?js_string>
<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector;
        var container;

        function drawForm(){
        	var nodeRef="${nodeRef}",
			htmlId='${id}_container',
			formId="contract-stages";
			container = Dom.get('${id}_container');
			//drawForm("${nodeRef}",'${id}_container', "contract-stages");
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj:{
                            htmlid: htmlId + "-" + Alfresco.util.generateDomId(),
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
                        htmlId: htmlId + "-" + Alfresco.util.generateDomId()
                    });
            var param = decodeURIComponent(location.search.substr(1)).split('&');
            for (var i=0; i < param.length; i++) {
                var tmp = param[i].split('=');
                if (tmp[0] == 'view' && tmp[1] == 'main') {
                    documentMetadataComponent.onExpand();
                }
            }
        }

        function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/documents/tables/lecm-document-table.js'
			], drawForm);
        }
        Event.onContentReady("${id}_container", init, true);
    })();
    //]]>
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>
            <div class="dashlet-title-text">${msg('label.contracts.stages')}</div>
            <div class="total-tasks-count">
                <span class="lecm-dashlet-actions">
                    <a id="${id}-action-add" href="javascript:void(0);" class="add"
                       title="${msg("dashlet.contract.stages.add.tooltip")}">${msg('label.contract.stage.add')}</a>
                </span>
            </div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" class="expand stages-expand" title="${msg("dashlet.contract.stages.expand.tooltip")}">&nbsp</a>
		</span>
    </div>
    <div class="dashlet-contract-stages body scrollableList dashlet-body" id="${id}_results">
        <div id="${id}_container"></div>
    </div>
</div>
