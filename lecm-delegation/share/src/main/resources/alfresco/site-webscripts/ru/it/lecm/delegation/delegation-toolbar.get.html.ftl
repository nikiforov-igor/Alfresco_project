<#assign toolbarId = args.htmlid>
<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationToolbar = new LogicECM.module.Delegation.Toolbar ("${toolbarId}");
		delegationToolbar.setMessages(${messages});
	})();
//]]>
</script>

<div id="${toolbarId}-body" class="datalist-toolbar toolbar">
	<div id="${toolbarId}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<div id="${toolbarId}-btnCreateProcuracy"></div>
			<div id="${toolbarId}-btnRefreshProcuracies"></div>
		</div>
		<div class="right">
			<span id="${toolbarId}-searchInput" class="search-input">
				<input type="text" id="delegationFullSearchInput" value="">
			</span>
			<div id="${toolbarId}-btnSearchProcuracies"></div>
		</div>
	</div>
</div>
