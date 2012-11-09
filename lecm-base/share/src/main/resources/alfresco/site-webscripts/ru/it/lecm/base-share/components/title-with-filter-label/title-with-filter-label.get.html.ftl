<script type="text/javascript">//<![CDATA[
	new LogicECM.module.Base.FilteredTitle('title-with-filter-label').setMessages(${messages});
//]]></script>
<div class="page-title theme-bg-color-1 theme-border-1" id="title-with-filter-label">
	<h1 class="theme-color-3">
		<span>${msg(args.title)}</span>
		<#if args.subtitle?? && msg(args.subtitle) != args.subtitle>
			${msg(args.subtitle)}
		</#if>
		<span class="filtered-label" id="filtered-label">${msg(args.filteredLabel)}</span>
	</h1>
</div>
