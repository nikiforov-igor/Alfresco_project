<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new window.LogicECM.module.Subscriptions.SubscribeControl("${fieldHtmlId}").setMessages(${messages});
	control.setOptions({
		objectNodeRef: "${form.arguments.itemId}"
	});
})();
//]]></script>
<div class="form-field">
	<div class="subscribe">
        <span id="${controlId}-subscribe-button" class="yui-button yui-push-button" style="display: none">
           <span class="first-child">
              <button type="button" title="${msg("button.subscribe")}">${msg("button.subscribe")}</button>
           </span>
        </span>
	</div>
	<div class="unsubscribe">
        <span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button" style="display: none">
           <span class="first-child">
              <button type="button" title="${msg("button.unsubscribe")}">${msg("button.unsubscribe")}</button>
           </span>
        </span>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
	<@formLib.renderFieldHelp field=field />
</div>