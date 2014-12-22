<#assign el=args.htmlid?html>

<script type="text/javascript">
(function(){

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-approval/approval-settings.js'
		], [
            'css/lecm-eds-documents/global-settings.css'
        ], function() {
			new LogicECM.module.LecmApprovalSettings('${el}').setMessages(${messages});
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>


<div id="${el}-body" class="eds-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg('label.title')}</div>
		</div>
	</div>

	<div id="${el}-settings"></div>
</div>
