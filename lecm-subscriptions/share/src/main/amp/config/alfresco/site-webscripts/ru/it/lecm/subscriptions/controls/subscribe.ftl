<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{

	function init() {
        LogicECM.module.Base.Util.loadResources(
                ['scripts/lecm-subscriptions/controls/lecm-subscribe.js'],
                ['css/lecm-subscriptions/subscription-control.css'],
                createControl);
	}

	function createControl () {
		var control = new LogicECM.module.Subscriptions.SubscribeControl("${fieldHtmlId}").setMessages(${messages});
		control.setOptions({
			<#if field.control.params.availableRoles??>
				availableRoles: "${field.control.params.availableRoles}",
			</#if>
			objectNodeRef: "${form.arguments.itemId}"
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
<div class="form-field">
	<div class="subscribe">
		<span id="${controlId}-subscribe-button" class="yui-button yui-push-button hidden1">
		   <span class="first-child">
			  <button type="button" title="${msg("button.subscribe")}">${msg("button.subscribe")}</button>
		   </span>
		</span>
	</div>
	<div class="unsubscribe">
		<span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button hidden1">
		   <span class="first-child">
			  <button type="button" title="${msg("button.unsubscribe")}">${msg("button.unsubscribe")}</button>
		   </span>
		</span>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
	<@formLib.renderFieldHelp field=field />
</div>