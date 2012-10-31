<#assign centerId = args.htmlid>
<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationCenter = new LogicECM.module.Delegation.Center ("${centerId}");
		delegationCenter.setMessages(${messages});
	})();
//]]>
</script>

<div id="${centerId}-body">
	<div style="border: 1px solid red;">this is center content</div>
	<div id="dataTableContainer" class="grid"></div>
</div>
