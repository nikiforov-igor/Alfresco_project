<#assign fieldValue = "">
<#if field.control.params.contextProperty??>
	<#if context.properties[field.control.params.contextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.contextProperty]>
	<#elseif args[field.control.params.contextProperty]??>
		<#assign fieldValue = args[field.control.params.contextProperty]>
	</#if>
<#elseif context.properties[field.name]??>
	<#assign fieldValue = context.properties[field.name]>
<#else>
	<#assign fieldValue = field.value>
</#if>

<script type="text/javascript">
	(function()
	{
		YAHOO.util.Event.onContentReady("${fieldHtmlId}", function (){
			<#if field.control.params.addedXpath??>
				addValue("${field.control.params.addedXpath}");
			</#if>

			YAHOO.Bubbling.fire("hiddenAssociationFormReady",
					{
						fieldName: "${field.name}",
						fieldId: "${fieldHtmlId}"
					});
		});

		function addValue(xPath) {
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/node/search?titleProperty=" + encodeURIComponent("cm:name") + "&xpath=" + encodeURIComponent(xPath);
			Alfresco.util.Ajax.jsonGet(
					{
						url: sUrl,
						successCallback:
						{
							fn: function (response) {
								var oResults = response.json;
								if (oResults != null && oResults.nodeRef != null) {
									YAHOO.util.Dom.get("${fieldHtmlId}-added").value = oResults.nodeRef;
								}
							}
						}
					});
		}
	})();
</script>

<div class="control hidden-assoc">
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}"
	       <#if field.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />
</div>