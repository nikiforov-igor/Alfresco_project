<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/title-with-filter-label/title-with-filter-label.js"/>

<script type="text/javascript">//<![CDATA[
	new LogicECM.module.Base.FilteredTitle('title-with-filter-label').setMessages(${messages});
//]]></script>
<div class="page-title theme-bg-color-1 theme-border-1" id="title-with-filter-label">
	<h1 class="theme-color-3">
		<span>${arm.name!msg("lecm.arm.title.ttl.arm")}</span>
		<span class="filtered-label" id="filtered-label">${msg(args.filteredLabel)}</span>
	</h1>
</div>
