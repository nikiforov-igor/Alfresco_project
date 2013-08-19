<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<div class="page-title theme-bg-color-1 theme-border-1">
	<#if item??>
		<h1 class="theme-color-3">
			<span>
				<#if item.node??>
					<#if armUrl?? && armUrl.url?? && (armUrl.url?length > 0)>
						<#assign linkUrl = armUrl.url>
					<#else>
						<#assign linkUrl = "documents-list?doctype=" + item.node.type>
					</#if>

					<a href="${siteURL(linkUrl)}">
						${item.typeTitle!msg("title.documents")}
					</a>
				</#if>
			</span>
		</h1>
	</#if>
</div>
