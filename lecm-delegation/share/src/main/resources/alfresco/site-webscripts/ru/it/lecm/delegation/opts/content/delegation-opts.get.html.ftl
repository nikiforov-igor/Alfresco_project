<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<#if delegator??>
<script type="text/javascript">//<![CDATA[
	var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
	delegationOpts.setMessages (${messages});
	delegationOpts.setOptions ({
		delegator: "${delegator}",
		isActive: ${isActive?string}
	});
//]]>
</script>

<style>
	.delegation-content {
		width: 640px;
		border: 0 !important;
	}

	.delegation-content .form-buttons {
		border-bottom: 0 !important;
	}
</style>

<div id="${id}-content-part1" class="yui-panel delegation-content"></div>
<#else>
<div style="position: absolute; top: 50%; width: 100%; height: 0; text-align: center;">
	<h1 class="theme-color-3" style="color: #3C3C3C;">
		<span>Невозможно отобразить страницу с настройками делегирования. Связка пользователь-сотрудник не настроена.</span>
	</h1>
</div>
</#if>
