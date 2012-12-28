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

<div id="${id}" style="width: 500px;">
	<div id="${id}-content-part1"></div>
<#--
	<div id="${id}-content-part1-footer" style="text-align: right;">
		<div id="${id}-btnSaveDelegationOpts"></div>
	</div>
-->
</div>
