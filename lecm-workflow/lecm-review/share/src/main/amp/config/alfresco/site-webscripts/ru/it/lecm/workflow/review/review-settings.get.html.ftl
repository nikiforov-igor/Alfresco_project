<#assign el=args.htmlid?html>

<script type="text/javascript">
(function(){

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-review/review-settings.js'
		], [
            'css/lecm-base/global-settings.css'
        ], function() {
			new LogicECM.module.LecmReviewSettings('${el}').setMessages(${messages});
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>


<div id="${el}-body" class="global-settings review-global-settings">
	<div class="yui-g">
		<div class="yui-u first">
			<div class="title">${msg('label.title')}</div>
		</div>
	</div>

	<div id="${el}-settings"></div>
</div>
