<#assign westId = args.htmlid>
<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationWest = new LogicECM.module.Delegation.West ("${westId}");
		delegationWest.setMessages(${messages});
	})();
//]]>
</script>

<div id="${westId}-body">
	<div>${myItem}</div>
	<div id="${westId}-myButton"></div>
</div>
