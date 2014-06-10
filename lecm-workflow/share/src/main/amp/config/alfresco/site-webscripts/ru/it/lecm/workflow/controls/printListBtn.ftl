<#assign controlId = fieldHtmlId + "-cntrl">
<#assign reportId = "approval-list">

<div class="form-field">
<#escape x as x?js_string>
	<div id="${controlId}" class="yui-skin-sam">
		<button id="${controlId}-print-button" type="button" onclick="LogicECM.module.Base.Util.printReport('${form.arguments.itemId}', '${reportId}')">
			${msg("button.print")}
		</button>
	</div>
</#escape>
</div>
