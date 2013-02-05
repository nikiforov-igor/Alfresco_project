<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

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
