<#if hasPermission!false>
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
            container = Dom.get('${id}_container');
            drawForm("${nodeRef}",'${id}_container', "document-dashlet");
            var param = decodeURIComponent(location.search.substr(1)).split('&');
            for (var i=0; i < param.length; i++) {
                var tmp = param[i].split('=');
                if (tmp[0] == 'view' && tmp[1] == 'main') {
                    documentMetadataComponent.onExpand();
                }
            }

			var semanticEl = YAHOO.util.Dom.get("semantic-mudule-active-htmlid");
			if (!semanticEl){
				var dashletAction = YAHOO.util.Dom.get("${id}-action-similar-doc");
				if (dashletAction){
					YAHOO.util.Dom.addClass(dashletAction, 'hidden');
				}
			}
        }
        Event.onContentReady("${id}_container", init, true);
    })();
    //]]>
</script>

<div class="dashlet-metadata dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" class="expand metadata-expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
			<a id="${id}-action-similar-doc" href="documents-by-term?nodeRef=${nodeRef}&type=lecm" class="semantic-list" target="_blank"  title="${msg('dashlet.semantic.documents.tooltip')}">&nbsp</a>
		</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
        <#if hasStatemachine && (mayAdd!false)>
            <a id="${id}-action-edit" class="edit metadata-edit" title="${msg("dashlet.edit.tooltip")}"></a>
        </#if>
        <div id="${id}_container"></div>
    </div>
</div>
</#if>