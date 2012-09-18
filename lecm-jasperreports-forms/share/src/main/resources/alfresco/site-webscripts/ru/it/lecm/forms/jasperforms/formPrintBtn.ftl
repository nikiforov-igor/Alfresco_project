<div class="form-field">
<#escape x as x?js_string>
	<div class="viewmode-field">
		<span class="viewmode-label">${field.label?html}:</span>
	</div>
	<div id="experts" class="yui-skin-sam">
		<a href="#" onclick="printNode('${form.arguments.itemId}')">print</a>
	</div>

</#escape>
	<script type="text/javascript">
		function printNode(nodeRef) {
			document.location.href = Alfresco.constants.PROXY_URI + "/lecm/jforms/form/Simple/" + nodeRef.replace("://", ":////");
		}
	</script>
</div>
