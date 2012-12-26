<#assign controlId = fieldHtmlId + "-cntrl">

<div class="form-field">
	<input type="button" id="${controlId}-subscribe" value="${msg("button.subscribe")}" onclick="alert('Подписка'); return false;"/>
	<@formLib.renderFieldHelp field=field />
</div>