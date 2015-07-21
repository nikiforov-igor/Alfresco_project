<#if hasPermission!false>
<#assign aDateTime = .now>
<#assign id = args.htmlid?js_string + aDateTime?iso_utc>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-component-base.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/document-details/document-metadata.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-metadata.js"></@script>
</@>


<script type="text/javascript">
		function hideButton() {
            if(location.hash != "#expanded") {
                YAHOO.util.Dom.setStyle(this, 'display', 'none');
            }
        }
        YAHOO.util.Event.onAvailable("${id}-action-collapse", hideButton);
</script>
<div class="metadata-form">
		<div class="lecm-dashlet-actions">
        	<a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    	</div>
</div>
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
                            itemId: nodeRef,
                            formId: "agenda",
							mayAdd: ${mayAdd?string!"false"},
							mayView: ${hasPermission?string!"false"},
							hasStatemachine: ${hasStatemachine?string!"false"},
                            mode:"edit"
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
            container = Dom.get('${id}-container');
            drawForm("${nodeRef}",'${id}-container', "agenda-style");
        }
        Event.onContentReady("${id}-container", init, true);
    })();
    //]]>
</script>

<div id="${id}" class="agenda-list-container">
		<div id="${id}-container"></div>
</div>


</#if>