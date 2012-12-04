[#ftl]

[#assign id = args.htmlid/]

<script type="text/javascript">//<![CDATA[
	var delegationOpts = new LogicECM.module.Delegation.DelegationOpts('${id}');
	delegationOpts.setMessages (${messages});
	delegationOpts.setOptions ({
		delegator: "${delegator}"
	});
//]]>
</script>


<div id="${id}">
    <div id="${id}-content-part1"></div>
    <div id="${id}-content-part2">
	<!-- тут у нас будет таблица жить и радио-батон -->
	</div>
    <div id="${id}-content-part3"></div>
    <div id="${id}-content"></div>
</div>
