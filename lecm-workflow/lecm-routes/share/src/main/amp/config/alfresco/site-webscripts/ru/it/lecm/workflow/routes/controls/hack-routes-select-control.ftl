<#assign documentRef = args.destination/>
<#assign formId = args.htmlid/>

<script type="text/javascript">
	(function() {
		var documentRef = '${documentRef}';
		var firstRun = true;

		YAHOO.Bubbling.on('associationAutoCompleteControlReady', function(layer, args) {
			if (firstRun && args[1].itemType === 'lecmWorkflowRoutes:route') {
				firstRun = false;
				YAHOO.Bubbling.fire('reInitializeControl', {
					formId: args[1].formId,
					fieldId: args[1].fieldId,
					options: {
						lazyLoading: false,
						allowedNodesScript: 'lecm/workflow/routes/getAllowedRoutes?documentRef=' + documentRef
					}
				});
			}
		});
	})();
</script>
