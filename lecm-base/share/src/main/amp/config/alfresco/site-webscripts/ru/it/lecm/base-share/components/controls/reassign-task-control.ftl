<script type="text/javascript">//<![CDATA[
	(function(){
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-reassign-task-control.js',
            'modules/simple-dialog.js',
	        'jquery/jquery-1.6.2.js'
		], createControls);
	}
	function createControls(){
		new LogicECM.module.ReassignTaskControl("${fieldHtmlId}").setOptions({
			taskId: "${form.arguments.itemId}",
			reassignReload: ${(args.reassignReload?? && args.reassignReload == "true")?string}
		}).setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
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