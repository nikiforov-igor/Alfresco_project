<div class="form-field">
<#escape x as x?js_string>
	<div id="experts" class="yui-skin-sam">
		<a href="#" onclick="printNode('${form.arguments.itemId}')">Печать</a>
	</div>

</#escape>
	<script type="text/javascript">
		function printNode(nodeRef) {
			document.location.href = Alfresco.constants.PROXY_URI + "/lecm/report/Simple?nodeRef=" + encodeURI(nodeRef);
		}
	</script>
</div>
