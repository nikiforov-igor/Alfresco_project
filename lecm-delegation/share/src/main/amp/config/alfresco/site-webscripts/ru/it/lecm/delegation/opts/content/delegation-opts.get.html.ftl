<#assign id = args.htmlid/>

<#if delegator??>
<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadResources([
					'/scripts/lecm-delegation/delegation-validator.js',
					'/scripts/lecm-base/components/advsearch.js',
					'/scripts/lecm-base/components/lecm-datagrid.js',
					'/scripts/lecm-delegation/opts/procuracy-grid.js',
					'/scripts/lecm-delegation/opts/delegation-opts.js'
				],
				[
					'css/lecm-delegation/opts/procuracy-grid.css'
				], createObject);
	}

	function createObject() {
		"use strict";
		var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
		delegationOpts.setMessages (${messages});
		delegationOpts.setOptions ({
			delegator: "${delegator}",
			isActive: ${isActive?string}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}-content-part1" class="delegation-content"></div>
<#else>
<div class="not-fount-procuracy">
	<h1 class="theme-color-3">
		<span>Невозможно отобразить страницу с настройками делегирования. Связка пользователь-сотрудник не настроена.</span>
	</h1>
</div>
</#if>
