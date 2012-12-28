<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.module.Subscriptions.SubscribeControl("${fieldHtmlId}").setMessages(${messages});
	control.setOptions({
		objectNodeRef: "${form.arguments.itemId}"
	});
})();
//]]></script>
<div class="form-field">
	<input type="button" style="display: none" id="${controlId}-subscribe-button" value="${msg("button.subscribe")}"/>
	<input type="button" style="display: none" id="${controlId}-unsubscribe-button" value="${msg("button.unsubscribe")}"/>
	<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
	<@formLib.renderFieldHelp field=field />
</div>