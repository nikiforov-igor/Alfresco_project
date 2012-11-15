<#assign toolbarId = args.htmlid>
<script type="text/javascript"> //<![CDATA[
	(function () {
		var rootNode = ${rootNode};
		var delegationToolbar = new LogicECM.module.Delegation.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
		delegationToolbar.setRootNode (rootNode.nodeRef);
	})();
//]]>
</script>

<div id="${toolbarId}-body" class="datalist-toolbar toolbar">
	<div id="${toolbarId}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<div id="${toolbarId}-btnCreateProcuracy"></div>
			<div id="${toolbarId}-btnListProcuracies"></div>
		</div>
	</div>
</div>
