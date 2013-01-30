<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
	var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
	delegationOpts.setMessages (${messages});
	delegationOpts.setOptions ({
		delegator: "${delegator}"
	});
//]]>
</script>

<div style="width: 640px;">
	<div id="${id}-content-part1" class="yui-panel"></div>
<#--
	<div id="${id}-content-part1-footer" style="text-align: right;">
		<div id="${id}-btnSaveDelegationOpts"></div>
	</div>
-->
</div>
