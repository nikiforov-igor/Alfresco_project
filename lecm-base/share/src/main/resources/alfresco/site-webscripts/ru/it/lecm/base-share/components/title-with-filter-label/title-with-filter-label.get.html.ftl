<script type="text/javascript">//<![CDATA[
	new LogicECM.module.Base.FilteredTitle('title-with-filter-label').setMessages(${messages});
//]]></script>
<#if args.title??>
    <#assign titleKey = args.title/>
<#else>
    <#if args.itemType??>
        <#assign titleKey = ("page." + args.itemType?replace(":","_"))/>
    </#if>
</#if>
<div class="page-title theme-bg-color-1 theme-border-1" id="title-with-filter-label">
	<h1 class="theme-color-3">
		<span>${msg(titleKey)}</span>
		<#if args.subtitle?? && msg(args.subtitle) != args.subtitle>
			${msg(args.subtitle)}
		</#if>
		<span class="filtered-label" id="filtered-label">${msg(args.filteredLabel)}</span>
	</h1>
</div>
