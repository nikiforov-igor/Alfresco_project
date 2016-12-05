<#assign documentRef = args.destination!''/>
<#if documentRef == ''>
	<#assign documentRef = args.documentNodeRef!''/>
</#if>
<#if documentRef == ''>
	<#assign documentRef = args.nodeRef!''/>
</#if>
<#assign formId = args.htmlid/>

<script type="text/javascript">
    (function() {
        var documentRef = '${documentRef}';

        function init() {
            YAHOO.Bubbling.fire('reInitializeControl', {
                formId: "${formId}",
                fieldId: "lecmWorkflowRoutes:selectRouteAssoc",
                options: {
                    lazyLoading: false,
                    allowedNodesScript: 'lecm/workflow/routes/getAllowedRoutes?documentRef=' + documentRef
                }
            });
        }

        YAHOO.util.Event.onContentReady("${formId}_lecmWorkflowRoutes:selectRouteAssoc_componentReady", init);
    })();
</script>
