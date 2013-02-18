<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.module.Connection.ConnectedDocuments("${fieldHtmlId}").setMessages(${messages});
	control.setOptions({
		primaryDocumentNodeRef: "${form.arguments.itemId}"
	});
})();
//]]></script>
<div class="form-field">
	<div class="add-connection">
        <span id="${controlId}-add-connection-button" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.connection.add")}">${msg("button.connection.add")}</button>
           </span>
        </span>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="${fieldHtmlId}" value="${field.value?html}" />
	<@formLib.renderFieldHelp field=field />
</div>