<script type="text/javascript">//<![CDATA[
	(function()
	{
		new LogicECM.module.ReassignTaskControl("${fieldHtmlId}").setOptions({
			taskId: "${form.arguments.itemId}"
		}).setMessages(${messages});
	})();
//]]></script>
<div class="form-field">
	<div class="reassign-task">
        <span id="${fieldHtmlId}-reassign-task-btn" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.task.reassign")}">${msg("button.task.reassign")}</button>
           </span>
        </span>
	</div>
	<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?string}"/>
	<@formLib.renderFieldHelp field=field />
</div>