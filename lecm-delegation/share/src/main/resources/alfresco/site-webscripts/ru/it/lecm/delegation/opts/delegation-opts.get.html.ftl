[#ftl]
[#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/]

[#assign id = args.htmlid/]

<script type="text/javascript">//<![CDATA[
	var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
	delegationOpts.setMessages (${messages});
	delegationOpts.setOptions ({
		delegator: "${delegator}",
	});
//]]>
</script>

<div id="${id}" style="width: 500px;">
    <div id="${id}-content-part1"></div>
	<div style="padding-left: 1em;">
		<input id="radioDelegateByFunc" type="radio" name="delegate-group" value="delegate-by" checked>
		<label for="radioDelegateByFunc">делегировать по бизнес функциям</label>
	</div>
	[@grid.datagrid id/]
	<div style="padding: 1em 0 0 1em;">
		<input id="radioDelegateAllFunc" type="radio" name="delegate-group" value="delegate-all">
		<label for="radioDelegateAllFunc">делегировать все функции</label>
	</div>
    <div id="${id}-content-part2"></div>
    <div id="${id}-content-part2-footer" style="text-align: right;">
		<div id="${id}-btnSaveDelegationOpts"></div>
	</div>
</div>

